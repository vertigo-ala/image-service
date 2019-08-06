package au.org.ala.images

import grails.converters.JSON
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses

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
@Api(value = "/ws", tags = ["Analytics services - image usage tracking"], description = "Image Web Services")
class AnalyticsController {

    def analyticsService

    @ApiOperation(
            value = "Get image usage for data resource. e.g dataResourceUID=dr123",
            nickname = "analytics/{dataResourceUID}",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    @ApiImplicitParams([
            @ApiImplicitParam(name = "dataResourceUID", paramType = "path", required = true, value = "Data Resource UID", dataType = "string" )
    ])
    def byDataResource() {

        def dataResourceUID = params.dataResourceUID
        if (!dataResourceUID){
            response.sendError(400, "Please supply a data resource UID")
            return null
        } else {
            def results = analyticsService.byDataResource(dataResourceUID)
            render (results as JSON)
        }
    }

    @ApiOperation(
            value = "Get overall image usage for the system",
            nickname = "analytics",
            produces = "application/json",
            consumes = "application/json",
            httpMethod = "GET",
            response = Map.class
    )
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 405, message = "Method Not Allowed. Only GET is allowed"),
            @ApiResponse(code = 404, message = "Image Not Found")]
    )
    def byAll() {
        def results = analyticsService.byAll()
        render (results as JSON)
    }
}
