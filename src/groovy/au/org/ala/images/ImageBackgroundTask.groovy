package au.org.ala.images

class ImageBackgroundTask extends BackgroundTask {

    long imageId
    ImageTaskType[] operations
    ImageService imageService

    public ImageBackgroundTask(long imageId, ImageService imageService, ImageTaskType...operations) {
        this.imageId = imageId
        this.imageService = imageService
        this.operations = operations
    }

    @Override
    public void execute() {
        Image.withNewTransaction {
            Image.lock(imageId)

            def imageInstance = Image.get(imageId)

            println "Executing image operations for ${imageInstance?.imageIdentifier}"

            operations.each { ImageTaskType operation ->
                switch (operation) {
                    case ImageTaskType.Thumbnail:
                        def thumbDimensions = imageService.generateImageThumbnails(imageInstance.imageIdentifier)
                        imageInstance.thumbWidth = thumbDimensions.width
                        imageInstance.thumbHeight = thumbDimensions.height
                        imageInstance.squareThumbSize = thumbDimensions.squareThumbSize
                        break;
                    case ImageTaskType.TMSTile:
                        imageService.generateTMSTiles(imageInstance.imageIdentifier)
                        break;
                    case ImageTaskType.KeywordRebuild:
                        imageService.tagService.rebuildKeywords(imageInstance)
                        break;
                    default:
                        throw new Exception("Unhandled image operation type: ${operation}")
                }
            }
            imageInstance.save(flush: true, failOnError: true)
        }
    }

}
