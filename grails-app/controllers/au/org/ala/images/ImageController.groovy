package au.org.ala.images

import au.org.ala.cas.util.AuthenticationUtils
import au.org.ala.web.AlaSecured
import au.org.ala.web.CASRoles
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.jasig.cas.client.util.AbstractCasFilter
import org.jasig.cas.client.validation.Assertion
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse
import java.util.regex.Pattern

class ImageController {

    def imageService
    def imageStoreService
    def searchService
    def selectionService
    def logService

    def index() { }

    @AlaSecured(value = [CASRoles.ROLE_ADMIN], redirectUri = '/')
    def upload() { }

    @AlaSecured(value = [CASRoles.ROLE_ADMIN], redirectUri = '/')
    def storeImage() {

        MultipartFile file = request.getFile('image')

        if (!file || file.size == 0) {
            flash.errorMessage = "You need to select a file to upload!"
            redirect(action:'upload')
            return
        }

        def pattern = Pattern.compile('^image/(.*)$')

        def m = pattern.matcher(file.contentType)
        if (!m.matches()) {
            flash.errorMessage = "Invalid file type for upload. Must be an image"
            redirect(action:'upload')
            return
        }

        def userId = AuthenticationUtils.getUserId(request) ?: "<anonymous>"
        def image = imageService.storeImage(file, userId)
        if (image) {
            imageService.scheduleArtifactGeneration(image.id)
        }
        flash.message = "Image uploaded with identifier: ${image?.imageIdentifier}"
        redirect(controller:'image', action:'upload')
    }

    def list() {
        def images

        params.offset = params.offset ?: 0
        params.max = params.max ?: 48
        params.sort = params.sort ?: 'dateTaken'

        def query = params.q as String
        int totalCount = 0

        if (query) {
            def results = searchService.simpleSearch(query, params)

            images = results?.images
            totalCount = results?.totalCount
        } else {
            images = Image.list(params)
            totalCount = images.totalCount
        }

        def userId = AuthenticationUtils.getUserId(request)

        def isLoggedIn = StringUtils.isNotEmpty(userId)
        def selectedImageMap = selectionService.getSelectedImageIdsAsMap(userId)


        [images: images, q: query, totalImageCount: totalCount, isLoggedIn: isLoggedIn, selectedImageMap: selectedImageMap]
    }

    def proxyImage() {
        def imageInstance = getImageFromParams(params)
        if (imageInstance) {
            def imageUrl = imageService.getImageUrl(imageInstance.imageIdentifier)
            proxyImageRequest(imageInstance, imageUrl, response)
        }
    }

    def proxyImageThumbnail() {
        def imageInstance = getImageFromParams(params)
        if (imageInstance) {
            def imageUrl = imageService.getImageThumbUrl(imageInstance.imageIdentifier)
            proxyImageRequest(imageInstance, imageUrl, response)
        }
    }

    def proxyImageTile() {
        def imageIdentifier = params.id
        def url = imageService.getImageTilesRootUrl(imageIdentifier)
        url += "/${params.z}/${params.x}/${params.y}.png"
        proxyUrl(new URL(url), response)
    }

    private void proxyImageRequest(Image imageInstance, String imageUrl, HttpServletResponse response) {

        def u = new URL(imageUrl)
        response.setContentType(imageInstance.mimeType ?: "image/jpeg")
        response.setHeader("Content-disposition", "attachment;filename=${imageInstance.imageIdentifier}.${imageInstance.extension ?: "jpg" }")
        proxyUrl(u, response)
    }

    private void proxyUrl(URL u, HttpServletResponse response) {
        InputStream is = null
        try {
            is = u.openStream()
        } catch (Exception ex) {
            logService.error("Failed it proxy URL", ex)
        }

        if (is) {
            try {
                IOUtils.copy(u.openStream(), response.outputStream)
            } finally {
                is.close()
                response.flushBuffer()
            }
        }
    }

    def scheduleArtifactGeneration() {

        def imageInstance = getImageFromParams(params)

        if (imageInstance) {
            imageService.scheduleArtifactGeneration(imageInstance.id)
            flash.message = "Image artifact generation scheduled for image ${imageInstance.id}"
        } else {
            def imageList = Image.findAll()
            long count = 0
            imageList.each { image ->
                imageService.scheduleArtifactGeneration(image.id)
                count++
            }
            flash.message = "Image artifact generation scheduled for ${count} images."
        }

        redirect(action:'list')
    }

    def details() {
        def image = getImageFromParams(params)
        if (!image) {
            flash.errorMessage = "Could not find image with id ${params.int("id") ?: params.imageId }!"
            redirect(action:'list')
        }
        def subimages = Subimage.findAllByParentImage(image)*.subimage
        def sizeOnDisk = imageStoreService.getConsumedSpaceOnDisk(image.imageIdentifier)

        def userId = AuthenticationUtils.getUserId(request)
        def albums = []
        if (userId) {
            albums = Album.findAllByUserId(userId, [sort:'name'])
        }
        [imageInstance: image, subimages: subimages, sizeOnDisk: sizeOnDisk, albums: albums]
    }

    def view() {
        def image = getImageFromParams(params)
        if (!image) {
            flash.errorMessage = "Could not find image with id ${params.int("id")}!"
        }
        def subimages = Subimage.findAllByParentImage(image)*.subimage
        [imageInstance: image, subimages: subimages]
    }

    def tagsFragment() {
        def imageInstance = getImageFromParams(params)
        def imageTags = ImageTag.findAllByImage(imageInstance)
        def tags = imageTags?.collect { it.tag }
        def leafTags = TagUtils.getLeafTags(tags)

        [imageInstance: imageInstance, tags: leafTags]
    }

    def addKeywordFragment() {
        def imageInstance = getImageFromParams(params)
        [imageInstance: imageInstance]
    }

    @AlaSecured(value = [CASRoles.ROLE_ADMIN])
    def imageAuditTrailFragment() {
        def imageInstance = Image.get(params.int("id"))
        def messages = []
        if (imageInstance) {
            messages = AuditMessage.findAllByImageIdentifier(imageInstance.imageIdentifier, [order:'asc', sort:'dateCreated'])
        }
        [messages: messages]
    }

    def imageMetadataTableFragment() {

        def imageInstance = getImageFromParams(params)
        def metaData = []
        def source = params.source as MetaDataSourceType
        if (imageInstance) {

            if (source) {
                metaData = imageInstance.metadata?.findAll { it.source == source }
            } else {
                metaData = imageInstance.metadata
            }
        }

        [imageInstance: imageInstance, metaData: metaData?.sort { it.name }, source: source]
    }

    def addUserMetadataFragment() {
        def imageInstance = getImageFromParams(params)
        [imageInstance: imageInstance]
    }

    def attachTagFragment() {
        def imageInstance = getImageFromParams(params)
        [imageInstance: imageInstance]
    }

    private Image getImageFromParams(GrailsParameterMap params) {
        def image = Image.get(params.int("id"))
        if (!image) {
            image = Image.findByImageIdentifier(params.imageId as String)
        }
        return image
    }

    def imageTooltipFragment() {
        def imageInstance = getImageFromParams(params)
        [imageInstance: imageInstance]
    }

    def imageTagsTooltipFragment() {
        def imageInstance = getImageFromParams(params)

        def imageTags = ImageTag.findAllByImage(imageInstance)
        def tags = imageTags?.collect { it.tag }
        def leafTags = TagUtils.getLeafTags(tags)

        [imageInstance: imageInstance, tags: leafTags]
    }

    def createSubimageFragment() {
        def imageInstance = getImageFromParams(params)
        def metadata = ImageMetaDataItem.findAllByImage(imageInstance)

        println params

        [imageInstance: imageInstance, x: params.x, y: params.y, width: params.width, height: params.height, metadata: metadata]
    }

}
