package au.org.ala.images

class ProcessBackgroundTilingTasksJob {

    def imageService
    def logService

    def concurrent = false

    static triggers = {
        simple repeatInterval: 1000l
    }

    def execute() {
        try {
            imageService.processTileBackgroundTasks()
        } catch (Exception ex) {
            logService.error("Exception thrown in tiling job handler", ex)
            throw ex
        }
    }

}
