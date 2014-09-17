package au.org.ala.images

class IndexImageBackgroundTask extends BackgroundTask {

    private long _imageId
    private ElasticSearchService _elasticSearchService

    public IndexImageBackgroundTask(long imageId, ElasticSearchService elasticSearchService) {
        _imageId = imageId
        _elasticSearchService = elasticSearchService
    }

    @Override
    void execute() {
        def imageInstance = Image.get(_imageId)
        _elasticSearchService.indexImage(imageInstance)
    }

}

class ScheduleReindexAllImagesTask extends BackgroundTask {

    private ImageService _imageService

    public ScheduleReindexAllImagesTask(ImageService imageService) {
        _imageService = imageService
    }

    @Override
    void execute() {
        _imageService.deleteIndex();

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
