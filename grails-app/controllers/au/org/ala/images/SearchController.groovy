package au.org.ala.images

import au.org.ala.cas.util.AuthenticationUtils
import au.org.ala.web.AlaSecured
import au.org.ala.web.CASRoles
import grails.converters.JSON

import java.util.regex.Pattern

class SearchController {

    def searchService
    def searchCriteriaService
    def selectionService
    def elasticSearchService

    def index() {
        boolean hasCriteria = searchService.getSearchCriteriaList()?.size() > 0
        def criteriaDefinitions = searchCriteriaService.getCriteriaDefinitionList()
        [criteriaDefinitions: criteriaDefinitions]
        render(view: 'advancedSearch', model:[hasCriteria: hasCriteria, criteriaDefinitions: criteriaDefinitions])
    }

    def addSearchCriteriaFragment() {
        def criteriaDefinitions = searchCriteriaService.getCriteriaDefinitionList()
        [criteriaDefinitions: criteriaDefinitions]
    }

    def criteriaDetailFragment() {
        try {
            def criteriaDefinition = SearchCriteriaDefinition.get(params.searchCriteriaDefinitionId)
            if (criteriaDefinition) {
                switch (criteriaDefinition.type) {
                    case CriteriaType.ImageProperty:
                        redirect(action: 'imageFieldCriteriaFragment', params: params)
                        break
                    case CriteriaType.ImageMetadata:
                        redirect(action: 'imageMetadataCriteriaFragment', params: params)
                        break
                    default:
                        throw new RuntimeException("Unhandled CriteriaType - " + criteriaDefinition.type)
                }
                return
            }
            throw new RuntimeException("No criteria specified!")
        } catch (Exception ex) {
            redirect(action: 'ajaxErrorFragment', params: [title: 'Error getting criteria details', errorMessage: ex.message])
        }
    }

    def imageFieldCriteriaFragment() {
        def criteriaDefinition = SearchCriteriaDefinition.get(params.int("searchCriteriaDefinitionId"))
        def criteria = searchService.getSearchCriteria(params.criteriaId)
        [criteriaDefinition: criteriaDefinition, criteria: criteria]
    }

    def imageMetadataCriteriaFragment() {
        def criteriaDefinition = SearchCriteriaDefinition.get(params.int("searchCriteriaDefinitionId"))
        def criteria = searchService.getSearchCriteria(params.criteriaId)
        def metadataNames = elasticSearchService.getMetadataKeys()
        def metadataItemName = ""
        def metadataItemValue = ""

        if (criteria) {
            def metaDataPattern = Pattern.compile("^(.*)[:](.*)\$")
            def matcher = metaDataPattern.matcher(criteria?.value)
            if (matcher.matches()) {
                metadataItemName = matcher.group(1)
                metadataItemValue = matcher.group(2)
            }
        }

        [criteriaDefinition: criteriaDefinition, criteria: criteria, metadataNames: metadataNames, metadataItemName: metadataItemName, metadataItemValue: metadataItemValue]
    }


    def ajaxErrorFragment() {
        def title = params.title ?: "Error!"
        def errorMessage = params.errorMessage ?: "Unspecified Error!"
        [title: title, errorMessage: errorMessage]
    }

    def ajaxAddSearchCriteria() {

        def results = [status: 'ok']
        try {

            def existing = searchService.getSearchCriteria(params.criteriaId)
            if (existing) {
                // we are saving a change to an existing criteria...
                searchService.saveSearchCriteria(existing.id, params)
            } else {
                // we create a new criteria
                searchService.addSearchCriteria(params)
            }


        } catch (Exception ex) {
            results.status = "error"
            results.errorMessage = ex.message
        }

        render(results as JSON)
    }

    def editSearchCriteriaFragment() {
        def criteria = searchService.getSearchCriteria(params.criteriaId as String)
        [criteria: criteria]
    }

    def ajaxRemoveSearchCriteria() {
        def results = [status: 'ok']
        searchService.removeSearchCriteria(params.searchCriteriaId as String)
        render(results as JSON)
    }

    def ajaxClearSearchCriteria() {
        def results = [status: 'ok']
        searchService.removeAllSearchCriteria()
        render(results as JSON)
    }

    def criteriaListFragment() {
        def list = searchService.getSearchCriteriaList()
        [searchCriteria: list]
    }

    def searchResultsFragment() {

        params.max = params.max ?: 48

        def searchResults = searchService.searchUsingCriteria(params)
        def userId = AuthenticationUtils.getUserId(request)
        def selectedImageMap = [:]
        if (userId) {
            selectedImageMap = selectionService.getSelectedImageIdsAsMap(userId)
        }

        [imageList: searchResults.list, totalCount: searchResults.totalCount, selectedImageMap: selectedImageMap]
    }

    @AlaSecured(value=[CASRoles.ROLE_USER], anyRole = true)
    def ajaxSelectAllCurrentQuery() {

        def results = [success: true]
        def userId = AuthenticationUtils.getUserId(request)
        if (userId) {
            searchService.withCriteriaImageIds(null, { idList ->
                selectionService.selectImages(userId, idList)
            })
        } else {
            results.success = false
            results.message = "Could not identify user!"
        }
        render(results as JSON)
    }
}
