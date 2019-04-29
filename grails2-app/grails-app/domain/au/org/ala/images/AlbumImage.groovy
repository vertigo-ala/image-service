package au.org.ala.images

class AlbumImage {

    Album album
    Image image


    static constraints = {
        album nullable: false
        image nullable: false
    }
}
