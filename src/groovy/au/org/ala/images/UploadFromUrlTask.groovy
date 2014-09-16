package au.org.ala.images

class UploadFromUrlTask extends BackgroundTask {

    private Map<String, String> _imageSource
    private ImageService _imageService
    private String _userId

    public UploadFromUrlTask(Map<String, String> imageSource, ImageService imageService, String userId) {
        _imageSource = imageSource
        _imageService = imageService
        _userId = userId
    }

    @Override
    void execute() {
        def results = _imageService.batchUploadFromUrl([_imageSource], _userId)
        def newImage = results[_imageSource.sourceUrl ?: _imageSource.imageUrl]
        if (newImage && newImage.success) {
            Image.withNewTransaction {
                _imageService.setMetadataItemsByImageId(newImage.image.id, _imageSource, MetaDataSourceType.SystemDefined, _userId)
            }
            _imageService.scheduleArtifactGeneration(newImage.image.id, _userId)
            this.yieldResult(newImage.image)
        }
    }

}

class UploadFromStagedImageTask extends BackgroundTask {

    private StagedFile _stagedFile
    private Map<String, String> _metaData
    private ImageStagingService _imageStagingService
    private String _batchId

    public UploadFromStagedImageTask(StagedFile stagedFile, Map<String, String> metadata, ImageStagingService imageStagingService, String batchId) {
        _stagedFile = stagedFile
        _metaData = metadata
        _imageStagingService = imageStagingService
        _batchId = batchId
    }

    @Override
    void execute() {
        this.yieldResult(_imageStagingService.importFileFromStagedFile(_stagedFile, _batchId, _metaData))
    }

}
