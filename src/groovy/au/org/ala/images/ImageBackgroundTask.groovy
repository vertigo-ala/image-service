package au.org.ala.images

class ImageBackgroundTask extends BackgroundTask {

    long imageId
    ImageTaskType operation
    ImageService imageService

    public ImageBackgroundTask(long imageId, ImageService imageService, ImageTaskType operation) {
        this.imageId = imageId
        this.imageService = imageService
        this.operation = operation
    }

    @Override
    public void execute() {
        Image.withNewTransaction {
            Image.lock(imageId)
            def imageInstance = Image.get(imageId)
            switch (operation) {
                case ImageTaskType.Thumbnail:
                    def thumbResults = imageService.generateImageThumbnails(imageInstance)
                    if (thumbResults) {
                        imageInstance.thumbWidth = thumbResults.width
                        imageInstance.thumbHeight = thumbResults.height
                        imageInstance.squareThumbSize = thumbResults.squareThumbSize
                    }
                    break;
                case ImageTaskType.TMSTile:
                    if (imageService.isImageType(imageInstance)) {
                        imageService.generateTMSTiles(imageInstance.imageIdentifier)
                    }
                    break;
                case ImageTaskType.KeywordRebuild:
                    imageService.tagService.rebuildKeywords(imageInstance)
                    break;
                default:
                    throw new Exception("Unhandled image operation type: ${operation}")
            }
            imageInstance.save(flush: true, failOnError: true)
        }
    }

}
