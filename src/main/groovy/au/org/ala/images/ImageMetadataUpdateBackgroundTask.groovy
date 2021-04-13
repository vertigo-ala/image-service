package au.org.ala.images

class ImageMetadataUpdateBackgroundTask extends BackgroundTask {

    private String _imageIdentifier
    private Map _metadata
    private ImageService _imageService

    ImageMetadataUpdateBackgroundTask(String imageIdentifier, Map metadata, ImageService imageService) {
        _imageIdentifier = imageIdentifier
        _imageService = imageService
        _metadata = metadata
    }

    @Override
    void execute() {
        _imageService.updateMetadata(_imageIdentifier, _metadata)
    }
}

