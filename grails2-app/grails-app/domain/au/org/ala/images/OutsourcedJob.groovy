package au.org.ala.images

class OutsourcedJob {

    String ticket
    Image image
    Date dateCreated
    ImageTaskType taskType
    int expectedDurationInMinutes

    static constraints = {
        ticket nullable: false
        image nullable: false
        taskType nullable: false
        expectedDurationInMinutes nullable: false
        dateCreated nullable: true
    }

}
