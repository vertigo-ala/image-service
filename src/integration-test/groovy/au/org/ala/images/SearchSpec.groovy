package au.org.ala.images

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import groovy.json.JsonSlurper
import image.service.Application
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

@Integration(applicationClass = Application.class)
@Rollback
class SearchSpec extends Specification {

    @Shared RestBuilder rest = new RestBuilder()

    def setup() {}

    def cleanup() {}

    void 'test upload'() {

        when:
        def occurrenceID = "f4c13adc-2926-44c8-b2cd-fb2d62378a1a"

        //first upload an image
        def testUrl = "https://www.ala.org.au/app/uploads/2019/06/Rufous-Betting-by-Graham-Armstrong-CCBY-25-Apr-2019-1920-x-1200-640x480.jpg"

        RestResponse uploadResponse = rest.post("http://localhost:${serverPort}/ws/uploadImagesFromUrls", {
            json {
                [images: [[
                          sourceUrl   : testUrl,
                          occurrenceID: occurrenceID
                  ]]]
            }
        })
        def jsonUploadResponse = new JsonSlurper().parseText(uploadResponse.body)

        then:
        uploadResponse.status == 200
        jsonUploadResponse.success == true
    }

    void 'test search for previous upload'(){


        when:
        //search by occurrence ID
        RestResponse resp = rest.post("http://localhost:${serverPort}/ws/findImagesByMetadata",{
            json {
                [
                    "key": "occurrenceid",
                    "values": ["f4c13adc-2926-44c8-b2cd-fb2d62378a1a"]
                ]
            }
        })
        def jsonResponse = new JsonSlurper().parseText(resp.body)

        then:
        resp.status == 200
        jsonResponse.count > 0
    }
}
