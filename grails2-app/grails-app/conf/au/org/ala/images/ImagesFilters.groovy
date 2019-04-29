package au.org.ala.images

import au.org.ala.cas.util.AuthenticationUtils
import javax.servlet.http.HttpServletRequest

class ImagesFilters {

    def logService

    def filters = {

        all(controller:'*', action:'*') {
            before = {
                def userAgent = request.getHeader("user-agent")
                def httprequest = (HttpServletRequest) request;
                String requestUri = request.getRequestURI()
                def username = AuthenticationUtils.getEmailAddress(request) ?: "N/A"

                logService.log "Username: ${username} IP: ${httprequest.remoteAddr} Session: ${request.session.id} UA: ${userAgent} URI: ${requestUri}"
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }

    }
}
