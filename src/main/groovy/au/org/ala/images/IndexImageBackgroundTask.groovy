package au.org.ala.images

class IndexImageBackgroundTask extends BackgroundTask {

    private long _imageId
    private ElasticSearchService _elasticSearchService

    IndexImageBackgroundTask(long imageId, ElasticSearchService elasticSearchService) {
        _imageId = imageId
        _elasticSearchService = elasticSearchService
    }

    @Override
    void execute() {
        def imageInstance = Image.get(_imageId)
        if (imageInstance) {
            _elasticSearchService.indexImage(imageInstance)
        }
    }
}

class ScheduleReindexAllImagesTask extends BackgroundTask {

    private ImageService _imageService

    ScheduleReindexAllImagesTask(ImageService imageService) {
        _imageService = imageService
    }

    @Override
    void execute() {
        _imageService.deleteIndex()

        def c = Image.createCriteria()
        def imageIds = c.list {
            projections {
                property("id")
            }
        }

        imageIds.each {
            _imageService.scheduleImageIndex(it)
        }
    }
}
