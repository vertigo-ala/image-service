package au.org.ala.images

import au.org.ala.cas.util.AuthenticationUtils
import au.org.ala.web.AlaSecured
import au.org.ala.web.CASRoles
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class SelectionController {

    def selectionService
    def imageService

    def index() {
        redirect(action:'list')
    }

    def ajaxSelectImage() {
        def image = getImageFromParams(params)
        def userId = AuthenticationUtils.getUserId(request)

        if (!userId) {
            render([success:false, message:"Not logged in (or url filters are wrong!)"] as JSON)
            return
        }

        if (image) {
            def result = selectionService.selectImage(userId, image)
            render([success:result] as JSON)
        } else {
            render([success:false, message:"No image id specified!"] as JSON)
        }
    }

    def ajaxDeselectImage() {
        def image = getImageFromParams(params)
        def userId = AuthenticationUtils.getUserId(request)

        if (!userId) {
            render([success:false, message:"Not logged in (or url filters are wrong!)"] as JSON)
            return
        }

        if (image) {
            def result = selectionService.deselectImage(userId, image)
            render([success:result] as JSON)
        } else {
            render([success:false, message:"No image id specified!"] as JSON)
        }
    }

    def clearSelection() {
        def userId = AuthenticationUtils.getUserId(request)

        if (!userId) {
            render([success:false, message:"Not logged in (or url filters are wrong!)"] as JSON)
            return
        }

        def selected = SelectedImage.findAllByUserId(userId)
        selected?.each {
            it.delete()
        }
        render([success:true] as JSON)
    }

    def ajaxGetSelectedImages() {
        def userId = AuthenticationUtils.getUserId(request)
        if (!userId) {
            render([success:false, message:"Not logged in (or url filters are wrong!)"] as JSON)
            return
        }

        def selected = SelectedImage.findAllByUserId(userId)
        def results = [success: true, images:[]]
        selected.each { selectedImage ->
            results.images << imageService.getImageInfoMap(selectedImage.image)
        }
        render(results as JSON)
    }

    def userContextFragment() {
        def userId = AuthenticationUtils.getUserId(request)
        def selectionCount = 0
        if (userId) {
            selectionCount = SelectedImage.countByUserId(userId)
        }
        [userId: userId, selectionCount: selectionCount]
    }

    private static Image getImageFromParams(GrailsParameterMap params) {
        def image = Image.get(params.int("id"))
        if (!image) {
            image = Image.findByImageIdentifier(params.imageId as String)
        }
        return image
    }

    def list() {
        def userId = AuthenticationUtils.getUserId(request)
        def selected = []
        if (userId) {
            selected = SelectedImage.findAllByUserId(userId)
        }

        [selectedImages: selected]
    }

    @AlaSecured(value = [CASRoles.ROLE_ADMIN], message = "You do not have sufficient privileges to perform this action", redirectAction = "list")
    def deleteSelected() {

        def userId = AuthenticationUtils.getUserId(request)
        def selected = []
        if (userId) {
            selected = SelectedImage.findAllByUserId(userId)
        }

        int count = 0
        if (selected) {
            selected.each {
                imageService.deleteImage(it.image, AuthenticationUtils.getUserId(request) ?: '<unknown>')
                count++
            }
        }

        flash.message = "${count} images deleted"

        redirect(action:'list')
    }

    @AlaSecured(value = [CASRoles.ROLE_ADMIN], message = "You do not have sufficient privileges to perform this action", redirectAction = "list")
    def generateThumbnails() {

        def userId = AuthenticationUtils.getUserId(request)
        def selected = []
        if (userId) {
            selected = SelectedImage.findAllByUserId(userId)
        }

        int count = 0
        if (selected) {
            selected.each {
                imageService.scheduleThumbnailGeneration(it.image.id)
                count++
            }
        }

        flash.message = "Scheduled thumbnail generation for ${count} images."

        redirect(action:'list')
    }

    @AlaSecured(value = [CASRoles.ROLE_ADMIN], message = "You do not have sufficient privileges to perform this action", redirectAction = "list")
    def generateTMSTiles() {

        def userId = AuthenticationUtils.getUserId(request)
        def selected = []
        if (userId) {
            selected = SelectedImage.findAllByUserId(userId)
        }

        int count = 0
        if (selected) {
            selected.each {
                imageService.scheduleTileGeneration(it.image.id)
                count++
            }
        }

        flash.message = "Scheduled tile generation for ${count} images."

        redirect(action:'list')

    }

}
