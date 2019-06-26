package au.org.ala.images

import grails.converters.JSON

/**
 * Instructions for obtaining required JSON...
 * http://notes.webutvikling.org/access-google-analytics-api-with-oauth-token/
 *
 * Note:
 * https://stackoverflow.com/questions/12837748/analytics-google-api-error-403-user-does-not-have-any-google-analytics-account
 *
 * Referenced JSON should look something like this:
 * {
 *   "type": "service_account",
 *   "project_id": "XXXXXXXXXX",
 *   "private_key_id": "XXXXXXXXXX",
 *   "private_key": "XXXXXXXXXX",
 *   "client_email": "XXXXXXXXXX",
 *   "client_id": "XXXXXXXXXX",
 *   "auth_uri": "https://accounts.google.com/o/oauth2/auth",
 *   "token_uri": "https://oauth2.googleapis.com/token",
 *   "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
 *   "client_x509_cert_url": "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
 * }
 */
class AnalyticsController {

    def analyticsService

    def byDataResource = {

        def dataResourceUID = params.dataResourceUID
        if (!dataResourceUID){
            response.sendError(400, "Please supply a data resource UID")
            return null
        } else {
            def results = analyticsService.byDataResource(dataResourceUID)
            render (results as JSON)
        }
    }

    def byAll = {
        def results = analyticsService.byAll()
        render (results as JSON)
    }
}
