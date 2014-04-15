package au.org.ala.images

class Album {

    String externalIdentifier
    String name
    String userId
    String description

    static constraints = {
        externalIdentifier nullable: false
        name nullable: false
        userId nullable: false
        description nullable: true
    }

    static mapping = {
        name length: 255
        description length: 8192
    }

}
