package au.org.ala.images

class ImageTag {

    Tag tag
    Image image

    static constraints = {
        tag nullable: false
        image nullable: false
    }

}
