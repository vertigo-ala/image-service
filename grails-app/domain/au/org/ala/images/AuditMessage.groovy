package au.org.ala.images

class AuditMessage {

    String imageIdentifier
    String message
    String userId
    Date dateCreated

    static constraints = {
        imageIdentifier nullable: false
        message nullable: false
        userId nullable: false
        dateCreated nullable: true
    }

}
