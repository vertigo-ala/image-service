package au.org.ala.images

class Image {

    Image parent
    @SearchableProperty(description = "The unique identifier of an image")
    String imageIdentifier
    @SearchableProperty(description = "The MD5 hash of an image")
    String contentMD5Hash
    @SearchableProperty(description = "The SHA1 hash of an image")
    String contentSHA1Hash
    @SearchableProperty(description = "The content type of the image")
    String mimeType
    @SearchableProperty(description= "The original name of the image file when it was uploaded")
    String originalFilename
    @SearchableProperty(description = "The extension of the image file when uploaded")
    String extension
    @SearchableProperty(valueType = CriteriaValueType.DateRange, description = "The date the image was uploaded")
    Date dateUploaded
    String uploader
    @SearchableProperty(valueType = CriteriaValueType.DateRange, description = "The date the image was captured or authored")
    Date dateTaken
    @SearchableProperty(valueType = CriteriaValueType.NumberRangeLong, units = "bytes", description = "The size of the image file in bytes")
    Long fileSize = 0
    @SearchableProperty(valueType = CriteriaValueType.NumberRangeInteger, units = "pixels", description = "The height of the image in pixels")
    Integer height
    @SearchableProperty(valueType = CriteriaValueType.NumberRangeInteger, units = "pixels", description = "The width of the image in pixels")
    Integer width
    @SearchableProperty(valueType = CriteriaValueType.NumberRangeInteger, units = "", description = "The number of zoom levels available in the TMS tiles")
    Integer zoomLevels = 0
    @SearchableProperty(description="The UID for the data resource associated with this image.")
    String dataResourceUid
    @SearchableProperty(description="An entity primarily responsible for making the resource.")
    String creator
    @SearchableProperty(description="A title for this image")
    String title
    @SearchableProperty(description="A general description of the image")
    String description
    @SearchableProperty(description="Rights information includes a statement about various property rights associated with the resource, including intellectual property rights")
    String rights
    @SearchableProperty(description="A person or organization owning or managing rights over the resource.")
    String rightsHolder
    @SearchableProperty(description="A legal document giving official permission to do something with the resource.")
    String license
    @SearchableProperty(description="Associated occurrence ID.")
    String occurrenceId
    @SearchableProperty(description="Calibrated by user.")
    String calibratedByUser
    License recognisedLicense
    @SearchableProperty(valueType = CriteriaValueType.NumberRangeInteger, units = "pixels", description = "The height of the thumbnail in pixels")
    Integer thumbHeight = 0
    @SearchableProperty(valueType = CriteriaValueType.NumberRangeInteger, units = "pixels", description = "The width of the thumbnail in pixels")
    Integer thumbWidth = 0
    @SearchableProperty(valueType = CriteriaValueType.Boolean, description = "Should be harvested by the ALA")
    Boolean harvestable = false

    Date dateDeleted

    Double mmPerPixel

    Integer squareThumbSize

    static hasMany = [keywords:ImageKeyword, metadata: ImageMetaDataItem, tags: ImageTag]

    static constraints = {
        parent nullable: true
        contentMD5Hash nullable: true
        contentSHA1Hash nullable: true
        mimeType nullable: true
        originalFilename nullable: true
        extension nullable: true
        dateUploaded nullable: true
        uploader nullable: true
        dateTaken nullable: true
        fileSize nullable: true
        height nullable: true
        width nullable: true
        zoomLevels nullable: true
        recognisedLicense nullable:true
        dataResourceUid nullable: true
        creator nullable: true
        title nullable: true
        rightsHolder nullable: true  //formerly 'attribution'
        rights nullable: true  //formerly 'copyright'
        license nullable: true

        description nullable: true
        thumbHeight nullable: true
        thumbWidth nullable: true
        squareThumbSize nullable: true
        mmPerPixel nullable: true
        calibratedByUser nullable:true
        harvestable nullable: true

        dateDeleted  nullable: true
        occurrenceId nullable: true
    }

    static mapping = {
        imageIdentifier index: 'ImageIdentifier_Idx'
        description length: 8096
        metadata cascade: 'all'
    }


}