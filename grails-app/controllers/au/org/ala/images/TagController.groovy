package au.org.ala.images

class TagController {

    def index() {

    }

    def createTagFragment() {
        def parentTag = Tag.get(params.int("parentTagId"))
        [parentTag: parentTag]
    }

    def renameTagFragment() {
        def tag = Tag.get(params.int("tagId"))
        [tagInstance: tag]
    }

    def deleteTagFragment() {
        def tag = Tag.get(params.int("tagId"))
        [tagInstance: tag]
    }


}
