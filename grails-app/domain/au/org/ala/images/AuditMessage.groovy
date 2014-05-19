package au.org.ala.images

class AuditMessage {

    String imageIdentifier
    String message
    String userId
    Date dateCreated

    static constraints = {
        imageIdentifier nullable: false
        message nullable: false, maxSize: 2048
        userId nullable: false
        dateCreated nullable: true
    }

}
