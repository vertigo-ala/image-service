package au.org.ala.images

import grails.transaction.Transactional

@Transactional
class SearchCriteriaService {

    def logService

    public List<SearchCriteriaDefinition> getCriteriaDefinitionList() {
        def fields = Image.class.declaredFields

        fields.each { field ->
            if (field.isAnnotationPresent(SearchableProperty)) {
                def ann = field.getAnnotation(SearchableProperty)

                def criteriaDefinition = SearchCriteriaDefinition.findByTypeAndFieldName(CriteriaType.ImageProperty, field.name)
                if (!criteriaDefinition) {
                    logService.debug("Creating new Image Property criteria definition for ${field.name}")
                    criteriaDefinition = new SearchCriteriaDefinition(name: "Image ${field.name}", type: CriteriaType.ImageProperty, valueType: ann.valueType(), fieldName: field.name, units: ann.units(), description: ann.description())
                    criteriaDefinition.save(flush: true, failOnError: true)
                }
            }
        }

        // add a criteria definition for meta data items...
        def existing = SearchCriteriaDefinition.findByType(CriteriaType.ImageMetadata)
        if (!existing) {
            existing = new SearchCriteriaDefinition(name: "Image metadata", type: CriteriaType.ImageMetadata, fieldName: "n/a", valueType: CriteriaValueType.StringDirectEntry, units:"")
            existing.save(flush: true, failOnError: true)
        }

        return SearchCriteriaDefinition.list().sort { it.name }
    }
}
