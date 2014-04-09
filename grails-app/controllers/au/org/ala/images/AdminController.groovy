package au.org.ala.images

import au.org.ala.web.AlaSecured
import au.org.ala.web.CASRoles
import grails.converters.JSON

import java.util.regex.Pattern


@AlaSecured(value = [CASRoles.ROLE_ADMIN], redirectUri = "/")
class AdminController {

    def imageService
    def imageStoreService

    def index() {
        redirect(action:'dashboard')
    }

    def searchCriteria() {
        def searchCriteriaDefinitions = SearchCriteriaDefinition.list()
        [criteriaDefinitions: searchCriteriaDefinitions]
    }

    def newSearchCriteriaDefinition() {
        SearchCriteriaDefinition criteriaDefinition = null
        render(view: 'editSearchCriteriaDefinition', model: [criteriaDefinition:  criteriaDefinition])
    }

    def editSearchCriteriaDefinition() {
        SearchCriteriaDefinition criteriaDefinition = SearchCriteriaDefinition.get(params.int("searchCriteriaDefinitionId"))
        render(view: 'editSearchCriteriaDefinition', model: [criteriaDefinition:  criteriaDefinition])
    }

    def saveSearchCriteriaDefinition() {
        def criteria = SearchCriteriaDefinition.get(params.int("searchCriteriaDefinitionId"))
        if (criteria == null) {
            criteria = new SearchCriteriaDefinition(params)
        } else {
            criteria.properties = params
        }

        criteria.save()

        redirect(action:"searchCriteria")
    }

    def deleteSearchCriteriaDefinition() {
        def criteria = SearchCriteriaDefinition.get(params.int("searchCriteriaDefinitionId"))
        if (criteria) {
            try {
                criteria.delete(flush: true)
            } catch (Exception ex) {
                flash.errorMessage = "Delete failed. This is probably because there exists search critera that use this definition<br/>" + ex.message
            }
        }

        redirect(action:"searchCriteria")
    }

    def dashboard() {
    }

    def tools() {
    }

    def localIngest() {
    }

    def fieldDefinitionsFragment() {
        def fieldDefinitions = ImportFieldDefinition.listOrderByFieldName()
        [fieldDefinitions: fieldDefinitions]
    }

    def inboxFileListFragment() {
        def fileList = imageService.listStagedImages()
        [fileList: fileList, fieldDefinitions: ImportFieldDefinition.listOrderByFieldName()]
    }

    def addFieldFragment() {
        render(view:'editFieldFragment')
    }

    def editFieldFragment() {
        def field = ImportFieldDefinition.get(params.int("id"))
        [fieldDefinition: field]
    }

    def saveFieldDefinition() {
        def name = params.name
        def type = params.type as ImportFieldType
        def value = params.value

        if (name && type && value) {

            if (type == ImportFieldType.FilenameRegex) {
                // try and compile the pattern

                try {
                    def pattern = Pattern.compile(value)
                } catch (Exception ex) {
                    render(['success': false, message: "Invalid regular Expression: ${ex.message}"] as JSON)
                    return
                }
            }
            // check existing
            def existing = ImportFieldDefinition.findByFieldName(name)
            if (existing) {
                existing.value = value
                existing.fieldType = type
            } else {
                def field = new ImportFieldDefinition(fieldType: type, fieldName: name, value: value)
                field.save(flush: true, failOnError: true)
            }
            render(['success': true, message: ""] as JSON)
        }

        render(['success': false, message: "Missing or incorrect parameters!"] as JSON)

    }

    def deleteFieldDefinition() {
        def fieldDefinition = ImportFieldDefinition.findById(params.int("id"))
        if (fieldDefinition) {
            fieldDefinition.delete()
        }
        render([success:true] as JSON)
    }

    def duplicates() {
        def c = Image.executeQuery("select contentMD5Hash, count(*) from Image group by contentMD5Hash having count(*) > 1 order by count(*)")
        // find an exemplar image for each set of duplicates
        def results = []
        c.each {
            def hash = it[0]
            def image = Image.findByContentMD5Hash(hash)
            results << [image: image, hash: hash, count:it[1]]
        }

        [results: results?.sort { 1/it.count }]
    }

}
