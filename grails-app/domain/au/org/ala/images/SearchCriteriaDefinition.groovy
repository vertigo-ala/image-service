package au.org.ala.images

class SearchCriteriaDefinition implements Serializable {

    CriteriaType type
    CriteriaValueType valueType

    String name
    String description
    String fieldName
    String units

    static constraints = {
        valueType nullable: false
        name nullable: false
        description nullable: true
        units nullable: true
    }

    static mapping = {
        description length: 1024
    }

}
