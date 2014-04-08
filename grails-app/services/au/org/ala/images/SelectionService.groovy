package au.org.ala.images

import grails.transaction.Transactional

@Transactional
class SelectionService {

    def getSelectedImageIds(String userId) {
        return SelectedImage.executeQuery("select image.imageIdentifier from SelectedImage where userId = :userId", [userId: userId])
    }

    def getSelectedImageIdsAsMap(String userId) {

        if (!userId) {
            return [:]
        }

        def selectedImageMap = [:]
        def idList = SelectedImage.executeQuery("select image.imageIdentifier from SelectedImage where userId = :userId", [userId: userId])
        // stick in map for quick retrieval
        idList?.each {
            selectedImageMap[it] = 1
        }
        return selectedImageMap
    }

    def selectImage(String userId, Image image) {
        def existing = SelectedImage.findByUserIdAndImage(userId, image)
        if (!existing) {
            def selected = new SelectedImage(userId: userId, image: image)
            selected.save()
            return true
        }
        return false
    }

    def deselectImage(String userId, Image image) {
        def existing = SelectedImage.findByUserIdAndImage(userId, image)
        if (existing) {
            existing.delete();
            return true
        }
        return false;
    }

    def clearSelection(String userId) {
        def selectedImages = SelectedImage.findAllByUserId(userId)
        selected?.each { selected ->
            selected.delete();
        }
    }

}
