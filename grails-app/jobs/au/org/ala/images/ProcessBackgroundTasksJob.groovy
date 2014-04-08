package au.org.ala.images

class ProcessBackgroundTasksJob {

    def imageService
    def logService

    def concurrent = false

    static triggers = {
        simple repeatInterval: 1000l
    }

    def execute() {
        try {
            imageService.processBackgroundTasks()
        } catch (Exception ex) {
            logService.error("Exception thrown in job handler", ex)
            throw ex
        }
    }
}
