package au.org.ala.images

import grails.transaction.Transactional
import groovy.json.JsonSlurper

/**
 * Services to retrieve resource level metadata.
 */
@Transactional
class CollectoryService {

    def grailsApplication

    def serviceMethod() {}

    //a low rent cache of image metadata
    static _lookupCache = [:]

    /**
     * Adds the image metadata (dublin core terms) to the image
     * for the image's associated data resource definition in the collectory.
     *
     * @param image
     */
    def addMetadataForResource(image){

        //if there no resource UID, move on
        if(!image["dataResourceUid"]){
            return
        }

        //if there no collectory configured, move on
        if(!grailsApplication.config.collections.baseUrl){
            return
        }

        def imageMetadata = getResourceLevelMetadata(image.dataResourceUid)

        //only add properties if they are blank on the source image
        imageMetadata.each { kvp ->
            if(kvp.value && !image[kvp.key]){
                image[kvp.key] = kvp.value
            }
        }
    }

    def clearCache() {
        log.info("Clearing cache - current size: " + _lookupCache.size())
        _lookupCache.clear()
    }

    def getResourceLevelMetadata(dataResourceUid){

        def imageMetadata = [:]

        if(!dataResourceUid){
            return imageMetadata
        }

        //lookup the resource UID
        if(!_lookupCache.containsKey(dataResourceUid)){
            def url = grailsApplication.config.collections.baseUrl + "/ws/dataResource/" + dataResourceUid
            try {
                def js = new JsonSlurper()
                def json = js.parseText(new URL(url).text)
                if (json && json.imageMetadata) {
                    imageMetadata = json.imageMetadata
                }
                _lookupCache.put(dataResourceUid, imageMetadata)
            } catch (Exception e){
                log.warn("Unable to load metadata from ${url}")
            }
        } else {
            imageMetadata = _lookupCache.get(dataResourceUid)
        }
        imageMetadata
    }
}