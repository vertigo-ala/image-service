package au.org.ala.images

import au.ala.org.ws.security.RequireApiKey
import au.org.ala.cas.util.AuthenticationUtils
import au.org.ala.ws.security.ApiKeyInterceptor
import grails.converters.JSON
import grails.converters.XML
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.annotations.Authorization
import org.apache.http.HttpStatus
import grails.plugins.csv.CSVWriter
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartRequest

import javax.servlet.http.HttpServletRequest
import java.util.zip.GZIPOutputStream

@Api(value = "/ws", description = "Image Web Services")
class WebServiceController {

    static namespace = 'ws'
    static allowedMethods = [findImagesByMetadata: 'POST', getImageInfoForIdList: 'POST']

    def imageService
    def imageStoreService
    def tagService
    def searchService
    def logService
    def batchService
    def elasticSearchService
    def collectoryService

    @RequireApiKey
    @ApiOperation(
            value = "Delete image",
            nickname = "image/{id}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "DELETE",
            response = Map.class,
            authorizations = @Authorization(value="apiKey"),
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "id", paramType = "path", required = true, value = "Image Id", dataType = "string")
    ])
    def deleteImageService() {

        def success = false
        def message = ""

        def userId = request.getHeader(ApiKeyInterceptor.API_KEY_HEADER_NAME)

        if(!userId) {
            response.sendError(400, "Must include API key")
        } else {
            def image = Image.findByImageIdentifier(params.imageID as String)
            if (image) {
                success = imageService.scheduleImageDeletion(image, userId)
                message = "Image scheduled for deletion."
            } else {
                message = "Invalid image identifier."
            }
            renderResults(["success": success, message: message])
        }
    }

    private long forEachImageId(closure) {
        def c = Image.createCriteria()
        def imageIdList = c {
            projections {
                property("id")
            }
        }

        long count = 0
        imageIdList.each { imageId ->
            if (closure) {
                closure(imageId)
            }
            count++
        }
        return count
    }

    @RequireApiKey
    @ApiOperation(
            value = "Schedule thumbnail generation",
            nickname = "scheduleThumbnailGeneration/{imageID}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            authorizations = @Authorization(value="apiKey"),
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "imageID", paramType = "path", required = true, value = "Image Id", dataType = "string")
    ])
    def scheduleThumbnailGeneration() {
        def imageInstance = Image.findByImageIdentifier(params.id as String)
        def userId = getUserIdForRequest(request)
        def results = [success: true]

        if (params.id && !imageInstance) {
            results.success = false
            results.message = "Could not find image ${params.id}"
        } else {
            if (imageInstance) {
                imageService.scheduleThumbnailGeneration(imageInstance.id, userId)
                results.message = "Image thumbnail generation scheduled for image ${imageInstance.id}"
            } else {
                def count = forEachImageId { imageId ->
                    imageService.scheduleThumbnailGeneration(imageId, userId)
                }
                results.message = "Image thumbnail generation scheduled for ${count} images."
            }
        }

        renderResults(results)
    }

    @RequireApiKey
    @ApiOperation(
            value = "Schedule artifact generation",
            nickname = "scheduleArtifactGeneration/{imageID}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            authorizations = @Authorization(value="apiKey"),
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "imageID", paramType = "path", required = true, value = "Image Id", dataType = "string")
    ])
    def scheduleArtifactGeneration() {

        def imageInstance = Image.findByImageIdentifier(params.id as String)
        def userId = request.getHeader(ApiKeyInterceptor.API_KEY_HEADER_NAME)
        def results = [success: true]

        if (params.id && !imageInstance) {
            results.success = false
            results.message = "Could not find image ${params.id}"
        } else {
            if (imageInstance) {
                imageService.scheduleArtifactGeneration(imageInstance.id, userId)
                results.message = "Image artifact generation scheduled for image ${imageInstance.id}"
            } else {
                def count = forEachImageId { imageId ->
                    imageService.scheduleArtifactGeneration(imageId, userId)
                }
                results.message = "Image artifact generation scheduled for ${count} images."
            }
        }
        renderResults(results)
    }

    @RequireApiKey
    @ApiOperation(
            value = "Schedule keyword generation",
            nickname = "scheduleKeywordRegeneration/{imageID}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            authorizations = @Authorization(value="apiKey"),
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "imageID", paramType = "path", required = true, value = "Image Id", dataType = "string")
    ])
    def scheduleKeywordRegeneration() {
        def imageInstance = Image.findByImageIdentifier(params.id as String)
        def userId = request.getHeader(ApiKeyInterceptor.API_KEY_HEADER_NAME)
        def results = [success:true]
        if (params.id && !imageInstance) {
            results.success = false
            results.message = "Could not find image ${params.id}"
        } else {
            if (imageInstance) {
                imageService.scheduleKeywordRebuild(imageInstance.id, userId)
                results.message = "Image keyword rebuild scheduled for image ${imageInstance.id}"
            } else {
                def imageList = Image.findAll()
                long count = 0
                imageList.each { image ->
                    imageService.scheduleKeywordRebuild(image.id, userId)
                    count++
                }
                results.message = "Image keyword rebuild scheduled for ${count} images."
            }
        }
        renderResults(results)
    }

    @RequireApiKey
    def scheduleInboxPoll() {
        def results = [success:true]
        def userId =  AuthenticationUtils.getUserId(request) ?: params.userId
        results.importBatchId = imageService.schedulePollInbox(userId)
        renderResults(results)
    }

    @RequireApiKey
    @ApiOperation(
            value = "Get tag model",
            nickname = "tags",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["Tag services"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def getTagModel() {

        def newNode = { Tag tag, String label, boolean disabled = false ->
            [name: label, text: "${label}", children:[], 'icon': false, tagId: tag?.id, state:[disabled: disabled]]
        }

        def rootNode = newNode(null, "root")

        def tags
        if (params.q) {
            def query = params.q.toString().toLowerCase()
            def c = Tag.createCriteria()
            tags = c.list([order: 'asc', sort: 'path']) {
                like("path", "%${query}%")
            }

        } else {
            tags = Tag.list([order: 'asc', sort: 'path'])
        }

        tags.each { tag ->
            def path = tag.path
            if (path.startsWith(TagConstants.TAG_PATH_SEPARATOR)) {
                path = path.substring(TagConstants.TAG_PATH_SEPARATOR.length())
            }
            def bits = path.split(TagConstants.TAG_PATH_SEPARATOR)
            if (bits) {
                def parent = rootNode
                bits.eachWithIndex { pathElement, elementIndex ->
                    def child
                    child = parent.children?.find({ it.name == pathElement})
                    if (!child) {
                        boolean disabled = false
                        if (elementIndex < bits.size() - 1) {
                            disabled = true
                        }
                        child = newNode(tag, pathElement, disabled)
                        parent.children << child
                    }
                    parent = child
                }
            }
        }

        renderResults(rootNode.children)
    }

    @RequireApiKey
    @ApiOperation(
            value = "Create tag by path",
            nickname = "tag",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "PUT",
            response = Map.class,
            authorizations = @Authorization(value="apiKey"),
            tags = ["Tag services"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only PUT is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "tagPath", paramType = "query", required = true, value = "Tag path. Paths separated by '/'. e.g. 'Birds/Colour/Red'", dataType = "string")
    ])
    def createTagByPath() {
        def success = false
        def tagPath = params.tagPath as String
        def tagId = 0
        if (tagPath) {
            def parent = Tag.get(params.int("parentTagId"))
            def tag = tagService.createTagByPath(tagPath, parent)
            success = tag != null
            tagId = tag.id
        }
        renderResults([success: success, tagId: tagId])
    }

    @RequireApiKey
    @ApiOperation(
            value = "Move tag",
            nickname = "tag/move",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "PUT",
            response = Map.class,
            authorizations = @Authorization(value="apiKey"),
            tags = ["Tag services"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only PUT is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "targetTagID", paramType = "query", required = true, value = "Target Tag ID to move", dataType = "string"),
            @ApiImplicitParam(name = "newParentTagID", paramType = "query", required = true, value = "New target parent tag ID", dataType = "string")
    ])
    def moveTag() {
        def success = false

        def target = Tag.get(params.int("targetTagId"))
        def newParent = Tag.get(params.int("newParentTagId"))

        if (target) {
            tagService.moveTag(target, newParent)
        }
        renderResults([success: success])
    }

    @RequireApiKey
    @ApiOperation(
            value = "Rename tag",
            nickname = "tag/rename",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "PUT",
            response = Map.class,
            authorizations = @Authorization(value="apiKey"),
            tags = ["Tag services"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "tagID", paramType = "query", required = true, value = "Tag ID", dataType = "string"),
            @ApiImplicitParam(name = "name", paramType = "query", required = true, value = "New name", dataType = "string")
    ])
    def renameTag() {
        def success = false
        def tag = Tag.get(params.int("tagID"))
        if (tag && params.name) {
            tagService.renameTag(tag, params.name)
        }
        renderResults([success: success])
    }

    @RequireApiKey
    @ApiOperation(
            value = "Delete tag",
            nickname = "tag/{tagID}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "DELETE",
            response = Map.class,
            authorizations = @Authorization(value="apiKey"),
            tags = ["Tag services"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "tagID", paramType = "path", required = true, value = "Tag Id", dataType = "string")
    ])
    def deleteTag() {
        def success = false
        def tag = Tag.get(params.int("tagId"))
        if (tag) {
            tagService.deleteTag(tag)
        }
        renderResults([success: success])
    }

    @RequireApiKey
    @ApiOperation(
            value = "Tag an image",
            nickname = "tag/{tagID}/image/{imageID}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "PUT",
            response = Map.class,
            authorizations = @Authorization(value="apiKey"),
            tags = ["Tag services"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "imageID", paramType = "path", required = true, value = "Image Id", dataType = "string"),
            @ApiImplicitParam(name = "tagID", paramType = "path", required = true, value = "Tag Id", dataType = "string")
    ])
    def attachTagToImage() {
        def success = false
        def message = ""
        def image = Image.findByImageIdentifier(params.imageId as String)
        def tag = Tag.get(params.int("tagId"))
        if (!image){
            message =  "Unrecognised image ID"
        } else if(!tag) {
            message = "Unrecognised tag ID"
        } else if (image && tag) {
            success = tagService.attachTagToImage(image, tag, AuthenticationUtils.getUserId(request))
        }
        renderResults([success: success, message: message])
    }

    @ApiOperation(
            value = "Find images by keyword (a keyword is just the raw string of a tag)",
            nickname = "images/keyword/{keyword}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["Tag services"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "keyword", paramType = "path", required = true, value = "Keyword", dataType = "string"),
            @ApiImplicitParam(name = "max", paramType = "query", required = true, value = "max results to return", defaultValue = "100", dataType = "string"),
            @ApiImplicitParam(name = "offset", paramType = "query", required = true, value = "offset for paging", defaultValue = "0", dataType = "string")

    ])
    def getImagesForKeyword(){
        QueryResults<Image> results = searchService.findImagesByKeyword(params.keyword, params)
        renderResults([
            keyword: params.keyword,
            totalImageCount: results.totalCount,
            images: results.list,
        ])
    }

    @ApiOperation(
            value = "Find images by keyword (a keyword is just the raw string of a tag)",
            nickname = "images/tag/{tagID}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["Tag services"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "tagID", paramType = "path", required = true, value = "Tag Id", dataType = "string"),
            @ApiImplicitParam(name = "max", paramType = "query", required = true, value = "max results to return", defaultValue = "100", dataType = "string"),
            @ApiImplicitParam(name = "offset", paramType = "query", required = true, value = "offset for paging", defaultValue = "0", dataType = "string")
    ])
    def getImagesForTag(){
        QueryResults<Image> results = searchService.findImagesByTagID(params.tagID, params)
        renderResults([
                tagID: params.tagID,
                totalImageCount: results.totalCount,
                images: results.list,
        ])
    }

    @RequireApiKey
    @ApiOperation(
            value = "Remove a tag from an image",
            nickname = "tag/{tagID}/image/{imageID}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "DELETE",
            response = Map.class,
            authorizations = @Authorization(value="apiKey"),
            tags = ["Tag services"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "imageID", paramType = "path", required = true, value = "Image Id", dataType = "string"),
            @ApiImplicitParam(name = "tagID", paramType = "path", required = true, value = "Tag Id", dataType = "string")
    ])
    def detachTagFromImage() {
        def success = false
        def image = Image.findByImageIdentifier(params.id as String)
        def tag = Tag.get(params.int("tagId"))
        if (image && tag) {
            success = tagService.detachTagFromImage(image, tag)
        }
        renderResults([success: success])
    }

    private addImageInfoToMap(Image image, Map results, Boolean includeTags, Boolean includeMetadata) {

        results.height = image.height
        results.width = image.width
        results.tileZoomLevels = image.zoomLevels ?: 0
        results.mimeType = image.mimeType
        results.originalFileName = image.originalFilename
        results.sizeInBytes = image.fileSize
        results.rights = image.rights ?: ''
        results.rightsHolder = image.rightsHolder ?: ''
        results.dateUploaded = formatDate(date: image.dateUploaded, format:"yyyy-MM-dd HH:mm:ss")
        results.dateTaken = formatDate(date: image.dateTaken, format:"yyyy-MM-dd HH:mm:ss")
        results.imageUrl = imageService.getImageUrl(image.imageIdentifier)
        results.tileUrlPattern = "${imageService.getImageTilesRootUrl(image.imageIdentifier)}/{z}/{x}/{y}.png"
        results.mmPerPixel = image.mmPerPixel ?: ''
        results.description = image.description ?: ''
        results.title = image.title ?: ''
        results.creator = image.creator ?: ''
        results.license = image.license ?: ''
        results.recognisedLicence = image.recognisedLicense
        results.dataResourceUid = image.dataResourceUid ?: ''

        collectoryService.addMetadataForResource(results)

        if (includeTags) {
            results.tags = []
            def imageTags = ImageTag.findAllByImage(image)
            imageTags?.each { imageTag ->
                results.tags << imageTag.tag.path
            }
        }

        if (includeMetadata) {
            results.metadata = []
            def metaDataList = ImageMetaDataItem.findAllByImage(image)
            metaDataList?.each { md ->
                results.metadata << [key: md.name, value: md.value, source: md.source]
            }
        }

    }

    @ApiOperation(
            value = "Get Image Details - optionally include tags and other metadata (e.g. EXIF)",
            nickname = "image/{imageID}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["JSON services for accessing and updating metadata"]

    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "imageID", paramType = "path", required = true, value = "Image Id", dataType = "string"),
            @ApiImplicitParam(name = "includeTags", paramType = "query", required = false, value = "Include tags", dataType = "string"),
            @ApiImplicitParam(name = "includeMetadata", paramType = "query", required = false, value = "Include metadata", dataType = "string")
    ])
    def getImageInfo() {
        def results = [success:false]
        def image = Image.findByImageIdentifier(params.imageID as String)
        if (image) {
            results.success = true
            addImageInfoToMap(image, results, params.boolean("includeTags"), params.boolean("includeMetadata"))
        }
        renderResults(results)
    }

    @ApiOperation(
            value = "Get Image PopUp details",
            nickname = "imagePopupInfo/{id}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "id", paramType = "path", required = true, value = "Image Id", dataType = "string")
    ])
    def imagePopupInfo() {
        def results = [success:false]

        def image = Image.findByImageIdentifier(params.id as String)
        if (image) {
            results.success = true
            results.data = [:]
            addImageInfoToMap(image, results.data, false, false)
            results.link = createLink(controller: "image", action:'details', id: image.id)
            results.linkText = "Image details..."
            results.title = "Image properties"
        }
        renderResults(results)
    }

    private renderResults(Object results, int responseCode = 200) {
        response.addHeader("Access-Control-Allow-Origin", "")
        response.status = responseCode
        withFormat {
            json {
                def jsonStr = results as JSON
                if (params.callback) {
                    response.setContentType("text/javascript")
                    render("${params.callback}(${jsonStr})")
                } else {
                    response.setContentType("application/json")
                    render(jsonStr)
                }
            }
            xml {
                render(results as XML)
            }
        }
    }

    @ApiOperation(
            value = "Repository statistics",
            nickname = "repositoryStatistics",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def getRepositoryStatistics() {
        def results = [:]
        results.imageCount = Image.count()
        results.sizeOnDisk =
        renderResults(results)
    }

    @ApiOperation(
            value = "Repository size statistics",
            nickname = "repositorySizeOnDisk",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def getRepositorySizeOnDisk() {
        def results = [ repoSizeOnDisk : ImageUtils.formatFileSize(imageStoreService.getRepositorySizeOnDisk()) ]
        renderResults(results)
    }

    @ApiOperation(
            value = "Background queue statistics",
            nickname = "backgroundQueueStats",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def getBackgroundQueueStats() {
        def results = [:]
        results.queueLength = imageService.getImageTaskQueueLength()
        results.tilingQueueLength = imageService.getTilingTaskQueueLength()
        renderResults(results)
    }

    @ApiOperation(
            value = "Create Subimage",
            nickname = "createSubimage",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            authorizations = @Authorization(value="apiKey"),
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @RequireApiKey
    def createSubimage() {
        def image = Image.findByImageIdentifier(params.id as String)
        if (!image) {
            renderResults([success:false, message:"Image not found: ${params.id}"])
            return
        }

        if (!params.x || !params.y || !params.height || !params.width) {
            renderResults([success:false, message:"Rectangle not correctly specified. Use x, y, height and width params"])
            return
        }

        def x = params.int('x')
        def y = params.int('y')
        def height = params.int('height')
        def width = params.int('width')
        def title = params.title
        def description = params.description

        if (height == 0 || width == 0) {
            renderResults([success:false, message:"Rectangle not correctly specified. Height and width cannot be zero"])
            return
        }

        def userId = getUserIdForRequest(request)
        if(!userId){
            renderResults([success:false, message:"User needs to be logged in to create sub image"])
            return
        }

        def subimage = imageService.createSubimage(image, x, y, width, height, userId, [title:title, description:description] )
        renderResults([success: subimage != null, subImageId: subimage?.imageIdentifier])
    }

    def getSubimageRectangles() {

        def image = Image.findByImageIdentifier(params.id as String)
        if (!image) {
            renderResults([success:false, message:"Image not found: ${params.id}"])
            return
        }

        def subimages = Subimage.findAllByParentImage(image)
        def results = [success: true, subimages: []]
        subimages.each { subImageRect ->
            def sub = subImageRect.subimage
            results.subimages << [imageId: sub.imageIdentifier, x: subImageRect.x, y: subImageRect.y, height: subImageRect.height, width: subImageRect.width]
        }
        renderResults(results)
    }

    def addUserMetadataToImage() {
        def image = Image.findByImageIdentifier(params.id as String)
        if (!image) {
            renderResults([success:false, message:"Image not found: ${params.id}"])
            return
        }

        def key = params.key
        if (!key) {
            renderResults([success:false, message:"Metadata key/name not supplied!"])
            return
        }
        def value = params.value
        if (!value) {
            renderResults([success:false, message:"Metadata value not supplied!"])
            return
        }

        def userId = getUserIdForRequest(request)

        def success = imageService.setMetaDataItem(image, MetaDataSourceType.UserDefined, key, value, userId)
        imageService.scheduleImageIndex(image.id)

        renderResults([success:success])
    }

    @ApiOperation(
            value = "Search for images",
            nickname = "search",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["Search"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "q", paramType = "path", required = false, value = "Query", dataType = "string"),
            @ApiImplicitParam(name = "fq", paramType = "path", required = false, value = "Filter Query", dataType = "string")
    ])
    def search(){
        def ct = new CodeTimer("Image list")

        params.offset = params.offset ?: 0
        params.max = params.max ?: 50
        params.sort = params.sort ?: 'dateUploaded'
        params.order = params.order ?: 'desc'

        def query = params.q as String

        QueryResults<Image> results = searchService.search(params)

        def filterQueries = params.findAll { it.key == 'fq' && it.value}

        ct.stop(true)
        renderResults([
         q: query,
         totalImageCount: results.totalCount,
         filters: filterQueries,
         searchCriteria: searchService.getSearchCriteriaList(),
         facets: results.aggregations,
         images: results.list,
        ])
    }

    @ApiOperation(
            value = "Facet for images",
            nickname = "facet",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["Search"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "facet", paramType = "path", required = true, value = "Facet", dataType = "string"),
            @ApiImplicitParam(name = "q", paramType = "path", required = false, value = "Query", dataType = "string"),
            @ApiImplicitParam(name = "fq", paramType = "path", required = false, value = "Filter Query", dataType = "string")
    ])
    def facet(){

        if(!params.facet){
            response.sendError(400, "Facet parameter is required")
            return
        }

        params.offset = params.offset ?: 0
        params.max = params.max ?: 50
        params.sort = params.sort ?: 'dateUploaded'
        params.order = params.order ?: 'desc'
        def results = searchService.facet(params)
        renderResults(results.aggregations)
    }

    private getUserIdForRequest(HttpServletRequest request) {

        //check for API access
        if (grailsApplication.config.security.cas.disableCAS.toBoolean()){
            return "-1"
        }

        // First check the CAS filter cookie thing
        def userId = AuthenticationUtils.getUserId(request)

        userId
    }

    def bulkAddUserMetadataToImage(String id) {
        def image = Image.findByImageIdentifier(id)
        def userId = getUserIdForRequest(request)
        if (!image) {
            renderResults([success:false, message:"Image not found: ${params.id}"])
            return
        }

        def metadata = request.getJSON() as Map<String, String>
        def results = imageService.setMetadataItems(image, metadata, MetaDataSourceType.UserDefined, userId)
        imageService.scheduleImageIndex(image.id)

        renderResults([success:results != null])
    }

    def removeUserMetadataFromImage() {
        def image = Image.findByImageIdentifier(params.id as String)
        if (!image) {
            renderResults([success:false, message:"Image not found: ${params.id}"])
            return
        }

        def key = params.key
        if (!key) {
            renderResults([success:false, message:"Metadata key/name not supplied!"])
            return
        }
        def userId = getUserIdForRequest(request)
        def success = imageService.removeMetaDataItem(image, key, MetaDataSourceType.UserDefined, userId)

        renderResults([success: success])
    }

    @ApiOperation(
            value = "Get list of metadata fields available for images. This will include any EXIF fields that have been extracted.",
            nickname = "metadatakeys",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def getMetadataKeys() {

        def source = params.source as MetaDataSourceType
        def results
        def c = ImageMetaDataItem.createCriteria()

        if (source) {
            results = c.list {
                eq("source", source)
                projections {
                    distinct("name")
                }
            }

        } else {
            results = c.list {
                projections {
                    distinct("name")
                }
            }
        }

        renderResults(results?.sort { it?.toLowerCase() })
    }

    @ApiOperation(
            value = "Get Image Links For MetaData Values",
            nickname = "getImageLinksForMetaDataValues",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "key", paramType = "query", required = true, value = "Image Id", dataType = "string"),
            @ApiImplicitParam(name = "q", paramType = "query", required = true, value = "Query", dataType = "string")
    ])
    def getImageLinksForMetaDataValues() {

        def key = params.key as String
        if (!key) {
            render([success:false, message:'No key parameter supplied'])
            return
        }

        def query = (params.q ?: params.value) as String

        if (!query) {
            render([success:false, message:'No q or value parameter supplied'])
            return
        }

        def images = searchService.findImagesByMetadata(key, [query], params)
        def results = [images:[], success: true, count: images.totalCount]

        def keyValues = imageService.getMetadataItemValuesForImages(images, key)

        images.each { image ->
            def info =  imageService.getImageInfoMap(image)
            info[key] = keyValues[image.id]
            results.images << info
        }

        renderResults(results)
    }

    @ApiOperation(
            value = "Get images for a list of image IDs",
            nickname = "imageInfoForList",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def getImageInfoForIdList() {

        def query = request.JSON

        if (query) {

            List<String> imageIds = (query.imageIds as List)?.collect { it as String }

            if (!imageIds) {
                renderResults([success:false, message:'You must supply a list of image IDs (imageIds) to search for!'])
                return
            }

            def results =  [:]
            def errors = []

            imageIds.each { imageId ->

                def image = Image.findByImageIdentifier(imageId)

                if (image) {
                    def map = imageService.getImageInfoMap(image)
                    results[imageId] = map
                } else {
                    errors << imageId
                }
            }

            renderResults([success: true, results: results, invalidImageIds: errors])
            return
        }
        renderResults([success:false, message:'POST with content type "application/JSON" required.'])
    }

    @ApiOperation(
            value = "Retrieve a list of recognised Licences",
            nickname = "licence",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["Licences"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def licence(){
        def licenses = License.findAll()
        renderResults (licenses)
    }

    @ApiOperation(
            value = "Retrieve a list of string to licence mappings in use by the image service",
            nickname = "licenceMapping",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["Licences"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def licenceMapping(){
        def licenses = LicenseMapping.findAll()
        renderResults (licenses)
    }

    @ApiOperation(
            value = "Find image by original filename (URL)",
            nickname = "findImagesByOriginalFilename",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "POST",
            response = Map.class,
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def findImagesByOriginalFilename() {

        CodeTimer ct = new CodeTimer("Original file lookup")

        def query = request.JSON

        if (query) {

            def filenames = query.filenames as List<String>

            if (!filenames) {
                renderResults([success:false, message:'You must supply a list of filenames to search for!'])
                return
            }

            def results =  [:]

            filenames.each { filename ->

                def images = searchService.findImagesByOriginalFilename(filename, params)
                def list = []
                images?.each { image ->
                    def map = imageService.getImageInfoMap(image)
                    list << map
                }
                results[filename] = [count: list.size(), images: list]
            }

            renderResults([success: true, results: results])

        } else {
            renderResults([success:false, message:'POST with content type "application/JSON" required.'])
        }
        ct.stop(true)
    }

    @ApiOperation(
            value = "Find images by image metadata",
            nickname = "findImagesByMetadata",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "POST",
            response = Map.class,
            tags = ["Search"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def findImagesByMetadata() {

        def query = request.JSON

        if (query) {

            def key = query.key as String
            def values = query.values as List<String>

            if (!key) {
                renderResults([success:false, message:'You must supply a metadata key!'])
                return
            }

            if (!values) {
                renderResults([success:false, message:'You must supply a values list!'])
                return
            }

            def images = elasticSearchService.searchByMetadata(key, values, params)
            def results = [:]
            def keyValues = imageService.getMetadataItemValuesForImages(images.list, key)
            images?.list?.each { image ->
                def map = imageService.getImageInfoMap(image)
                collectoryService.addMetadataForResource(image)
                def keyValue = keyValues[image.id]
                def list = results[keyValue]
                if (!list) {
                    list = []
                    results[keyValue] = list
                }
                list << map
            }

            renderResults([success: true, images: results, count:images?.totalCount ?: 0])
            return
        }

        renderResults([success:false, message:'POST with content type "application/JSON" required.'])
    }

    def getNextTileJob() {
        def results = imageService.createNextTileJob()
        renderResults(results)
    }

    def cancelTileJob() {

        def userId = AuthenticationUtils.getUserId(request)
        def ticket = params.jobTicket ?: params.ticket
        if (!ticket) {
            renderResults([success:false, message:'No job ticket specified'])
            return
        }

        def job = OutsourcedJob.findByTicket(ticket)
        if (!job) {
            renderResults([success:false, message:'No such ticket or ticket expired.'])
            return
        }

        logService.log("Cancelling job (Ticket: ${job.ticket} for image ${job.image.imageIdentifier}")

        // Push the job back on the queue
        if (job.taskType == ImageTaskType.TMSTile) {
            imageService.scheduleTileGeneration(job.image.id, userId)
        }

        job.delete()
    }

    def postJobResults() {
        def ticket = params.jobTicket ?: params.ticket
        if (!ticket) {
            renderResults([success:false, message:'No job ticket specified'])
            return
        }

        def job = OutsourcedJob.findByTicket(ticket)
        if (!job) {
            renderResults([success:false, message:'No such ticket or ticket expired.'])
            return
        }

        def zoomLevels = params.int("zoomLevels")
        if (!zoomLevels) {
            renderResults([success:false, message:'No zoomLevels supplied.'])
            return
        }

        if (job.taskType == ImageTaskType.TMSTile) {
            // Expect a multipart file request
            MultipartFile file = request.getFile('tilesArchive')

            if (!file || file.size == 0) {
                renderResults([success:false, message:'tilesArchive param not present. Expected multipart file.'])
                return
            }

            if (imageStoreService.storeTilesArchiveForImage(job.image.imageIdentifier, file)) {
                job.image.zoomLevels = zoomLevels
                job.delete()
                renderResults([success: true])
                return
            } else {
                renderResults([success:false, message: "Error storing tiles for image!"])
                return
            }
        }
        renderResults([success: false, message:'Unhandled task type'])
    }

    /**
     * Update an image's metadata.
     *
     * @return
     */
    @ApiOperation(
            value = "Update image metadata using a JSON payload",
            nickname = "updateMetadata",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "POST",
            response = Map.class,
            tags = ["JSON services for accessing and updating metadata"],
            authorizations = @Authorization(value="apiKey")
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @RequireApiKey
    def updateMetadata(){

        CodeTimer ct = new CodeTimer("Update Image metadata ${params.imageIdentifier}")

        Image image = Image.findByImageIdentifier(params.imageIdentifier)
        if (image){
            def userId = getUserIdForRequest(request)
            def metadata = {
                if(params.metadata){
                    JSON.parse(params.metadata as String) as Map
                } else {
                    [:]
                }
            }.call()

            imageService.updateImageMetadata(image, metadata)
            tagService.updateTags(image, params.tags, userId)

            response.setStatus(200)
            renderResults([success: true])
        } else {
            response.setStatus(404)
            renderResults([success: false])
        }

        ct.stop(true)
    }

    /**
     * Main web service for image upload.
     *
     * @return
     */
    @ApiOperation(
            value = "Upload a single image, with by URL or multipart HTTP file upload",
            nickname = "uploadImage",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "POST",
            response = Map.class,
            tags = ["Upload"],
            authorizations = @Authorization(value="apiKey")
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @RequireApiKey
    def uploadImage() {
        // Expect a multipart file request
        try {
            ImageStoreResult storeResult = null

            CodeTimer storeTimer = new CodeTimer("Store image")

            def userId = getUserIdForRequest(request)
            def url = params.imageUrl ?: params.url
            def metadata = {
                if (params.metadata) {
                    JSON.parse(params.metadata as String) as Map
                } else {
                    [:]
                }
            }.call()

            if (url) {
                // Image is located at an endpoint, and we need to download it first.
                storeResult = imageService.storeImageFromUrl(url, userId, metadata)
                if (!storeResult || !storeResult.image) {
                    renderResults([success: false, message: "Unable to retrieve image from ${url}"])
                }
            } else {
                // it should contain a file parameter
                MultipartRequest req = request as MultipartRequest
                if (req) {
                    MultipartFile file = req.getFile('image')
                    if (!file) {
                        renderResults([success: false, message: 'image parameter not found. Please supply an image file.'])
                        return
                    }

                    if (file.size == 0) {
                        renderResults([success: false, message: 'the supplied image was empty. Please supply an image file.'])
                        return
                    }

                    storeResult = imageService.storeImage(file, userId, metadata)
                } else {
                    renderResults([success: false, message: "No url parameter, therefore expected multipart request!"])
                }
            }

            storeTimer.stop()

            if (storeResult && storeResult.image) {

                CodeTimer ct = new CodeTimer("Setting Image metadata ${params.imageIdentifier}")

                //store any other property
                metadata.each { kvp ->
                    if (!storeResult.image.hasProperty(kvp.key)) {
                        imageService.setMetaDataItem(storeResult.image, MetaDataSourceType.SystemDefined, kvp.key as String, kvp.value as String)
                    }
                }

                tagService.updateTags(storeResult.image, params.tags, userId)
                imageService.schedulePostIngestTasks(storeResult.image.id, storeResult.image.imageIdentifier, storeResult.image.originalFilename, userId)

                ct.stop(true)

                renderResults([success: true, imageId: storeResult.image?.imageIdentifier])
            } else {
                renderResults([success: false, message: "Failed to store image!"])
            }
        } catch (Exception e){
            log.error(e.getMessage(), e)
            renderResults([success: false, message: "Failed to store image!"], 500)
        }
    }

    @ApiOperation(
            value = "Asynchronous upload images by supplying a list of URLs in  a JSON  payload",
            nickname = "uploadImagesFromUrls",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "POST",
            response = Map.class,
            tags = ["Upload"],
            authorizations = @Authorization(value="apiKey")
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @RequireApiKey
    def uploadImagesFromUrls() {

        def userId = getUserIdForRequest(request)
        def body = request.JSON

        if (body) {

            List<Map<String, String>> imageList = body.images

            if (!imageList) {
                renderResults(
                        [success:false,
                         message:'You must supply a list of objects called "images", each of which' +
                                 ' must contain a "sourceUrl" key, along with optional meta data items!'],
                        HttpStatus.SC_BAD_REQUEST
                )
                return
            }

            // first create the images
            def results = imageService.batchUploadFromUrl(imageList, userId)

            imageList.each { srcImage ->
                def newImage = results[srcImage.sourceUrl ?: srcImage.imageUrl]
                if (newImage && newImage.success) {
                    imageService.setMetadataItems(newImage.image, srcImage, MetaDataSourceType.SystemDefined, userId)
                    imageService.scheduleArtifactGeneration(newImage.image.id, userId)
                    imageService.scheduleImageIndex(newImage.image.id)
                    newImage.image = null
                }
            }

            renderResults([success: true, results: results])
        } else {
            renderResults([success:false, message:'POST with content type "application/JSON" required.'], 400)
        }
    }

    def calibrateImageScale() {

        def userId = getUserIdForRequest(request)
        def image = Image.findByImageIdentifier(params.imageId)
        def units = params.units ?: "mm"
        def pixelLength = params.double("pixelLength") ?: 0
        def actualLength = params.double("actualLength") ?: 0
        if (image && units && pixelLength && actualLength) {
            def pixelsPerMM = imageService.calibrateImageScale(image, pixelLength, actualLength, units, userId)
            renderResults([success: true, pixelsPerMM:pixelsPerMM, message:"Image is scaled at ${pixelsPerMM} pixels per mm"])
            return
        }
        renderResults([success:false, message:'Missing one or more required parameters: imageId, pixelLength, actualLength, units'])
    }

    def resetImageCalibration() {
        def image = Image.findByImageIdentifier(params.imageId)
        if (image) {
            imageService.resetImageLinearScale(image)
            renderResults([success: true, message:"Image linear scale has been reset"])
            return
        }
        renderResults([success:false, message:'Missing one or more required parameters: imageId, pixelLength, actualLength, units'])
    }

    def setHarvestable() {
        def userId = getUserIdForRequest(request)
        def image = Image.findByImageIdentifier(params.imageId)
        if (image) {
            imageService.setHarvestable(image, (params.value ?: params.harvest ?: "").toBoolean(), userId)
            renderResults([success: true, message:"Image harvestable now set to ${image.harvestable}", harvestable: image.harvestable])
        } else {
            renderResults([success:false, message:'Missing one or more required parameters: imageId, value'])
        }
    }

    @ApiOperation(
            value = "Schedule upload from URLs",
            nickname = "scheduleUploadFromUrls",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["Upload"],
            authorizations = @Authorization(value="apiKey")
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @RequireApiKey
    def scheduleUploadFromUrls() {

        def userId = getUserIdForRequest(request)
        def body = request.JSON

        if (body) {
            List<Map<String, String>> imageList = body.images
            if (!imageList) {
                renderResults([success:false, message:'You must supply a list of objects called "images", each of which must contain a "sourceUrl" key, along with optional meta data items!'], HttpStatus.SC_BAD_REQUEST)
                return
            }

            def batchId = batchService.createNewBatch()
            int imageCount = 0
            imageList.each { srcImage ->
                if (!srcImage.containsKey("importBatchId")) {
                    srcImage["importBatchId"] = batchId
                }
                batchService.addTaskToBatch(batchId, new UploadFromUrlTask(srcImage, imageService, userId))
                imageCount++
            }

            renderResults([success: true, message: "${imageCount} urls scheduled for upload (batch id ${batchId}).", batchId: batchId])
            return
        }

        renderResults([success:false, message:'POST with content type "application/JSON" required.'])
    }

    @ApiOperation(
            value = "Get batch status for a batch upload of images",
            nickname = "batchstatus",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["Upload"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def getBatchStatus() {
        def status = batchService.getBatchStatus(params.batchId)
        if (status) {
            renderResults([success:true, taskCount: status.taskCount, tasksCompleted: status.tasksCompleted, batchId: status.batchId, timeStarted: status.timeStarted.getTime(), timeFinished: status.timeFinished?.getTime() ?: 0])
            return
        }
        renderResults([success:false, message:'Missing or invalid batchId'])
    }

    @ApiOperation(
            value = "List the recognised darwin core terms",
            nickname = "darwinCoreTerms",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["JSON services for accessing and updating metadata"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def darwinCoreTerms() {
        def terms = []

        def filter = params.q ? { DarwinCoreField dwc -> dwc.name().toLowerCase().contains(params.q.toLowerCase()) } : { DarwinCoreField dwc -> true}

        DarwinCoreField.values().each {
            if (filter(it)) {
                terms.add([name: it.name(), label: it.label])
            }
        }
        renderResults(terms)
    }

    def harvest() {

        def harvestResults = imageService.getHarvestTabularData()

        if (harvestResults) {
            response.setHeader("Content-disposition", "attachment;filename=images-harvest.csv")
            response.contentType = "text/csv"

            def bos = new OutputStreamWriter(response.outputStream)

            def writer = new CSVWriter(bos, {
                for (int i = 0; i < harvestResults.columnHeaders.size(); ++i) {
                    def col = harvestResults.columnHeaders[i]
                    "${col}" {
                        it[col] ?: ""
                    }
                }
            })

            harvestResults.data.each {
                writer << it
            }

            bos.flush()
            bos.close()
        } else {
            renderResults([success:"false", message:'No harvestable images found'])
        }
    }

    @ApiOperation(
            value = "Export CSV of entire image catalogue",
            nickname = "exportCSV",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class,
            tags = ["Export"]
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def exportCSV(){
        response.setHeader("Content-disposition", "attachment;filename=images-export.csv.gz")
        response.contentType = "application/gzip"
        def bos = new GZIPOutputStream(response.outputStream)
        imageService.exportCSV(bos)
        bos.flush()
        bos.close()
    }
}