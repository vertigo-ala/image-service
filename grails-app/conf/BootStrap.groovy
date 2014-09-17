import au.org.ala.images.Image

import javax.imageio.ImageIO
import javax.imageio.spi.IIORegistry

class BootStrap {

    def grailsApplication
    def logService
    def settingService
    def elasticSearchService

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

        elasticSearchService.ping()
    }

    def destroy = {
    }
}
