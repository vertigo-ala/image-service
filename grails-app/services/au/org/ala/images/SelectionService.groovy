package au.org.ala.images

import grails.transaction.Transactional
import org.hibernate.FlushMode

@Transactional
class SelectionService {

    def sessionFactory

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

    def selectImages(String userId, imageIds) {
        try {
            sessionFactory.currentSession.flushMode = FlushMode.MANUAL
            imageIds.each { imageId ->
                def image = Image.get(imageId as Long)
                if (image) {
                    def existing = SelectedImage.findByUserIdAndImage(userId, image)
                    if (!existing) {
                        def selected = new SelectedImage(userId: userId, image: image)
                        selected.save()
                    }
                }
            }
            sessionFactory.currentSession.flush()
        } finally {
            sessionFactory.currentSession.flushMode = FlushMode.AUTO
        }
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
