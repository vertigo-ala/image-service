package au.org.ala.images

class Tag {

    String path

    static constraints = {
        path nullable: false
    }

    static mapping = {
        path length: 2048
    }

    static transients = [ "label"]

    String toString() {
        return "Tag ${id} ${path}"
    }

    String getLabel() {
        return path?.substring(path.lastIndexOf(TagConstants.TAG_PATH_SEPARATOR) + 1)
    }
}
