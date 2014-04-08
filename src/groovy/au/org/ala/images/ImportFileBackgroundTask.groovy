package au.org.ala.images

class ImportFileBackgroundTask extends BackgroundTask {

    File imageFile
    ImageService imageService
    String batchId
    String userId

    public ImportFileBackgroundTask(File imageFile, ImageService imageService, String batchId, String userId) {
        this.imageFile = imageFile
        this.imageService = imageService
        this.batchId = batchId
        this.userId = userId
    }

    @Override
    void execute() {
        imageService.importFile(imageFile, batchId, userId);
    }
}
