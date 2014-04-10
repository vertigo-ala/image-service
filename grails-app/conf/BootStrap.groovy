import javax.imageio.ImageIO
import javax.imageio.spi.IIORegistry

class BootStrap {

    def grailsApplication
    def logService
    def settingService

    def init = { servletContext ->

        def inboxLocation = grailsApplication.config.imageservice.imagestore.inbox

        def file = new File(inboxLocation)
        if (!file.exists()) {
            logService.log("Creating directory ${file.absolutePath}")
            file.mkdirs()
        }

        ImageIO.scanForPlugins()
        IIORegistry.getDefaultInstance()
        ImageIO.setUseCache(false)

        settingService.ensureSettingsCreated()

    }

    def destroy = {
    }
}
