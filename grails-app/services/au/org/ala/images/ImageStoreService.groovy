package au.org.ala.images

import au.org.ala.images.tiling.ImageTiler
import au.org.ala.images.tiling.ImageTilerConfig
import au.org.ala.images.tiling.ImageTilerResults
import au.org.ala.images.tiling.TileFormat
import au.org.ala.images.util.ImageReaderUtils
import grails.transaction.Transactional
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.springframework.web.multipart.MultipartFile

import javax.imageio.ImageIO
import javax.imageio.ImageReadParam
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.lang.reflect.Field

@Transactional
class ImageStoreService {

    def grailsApplication
    def logService
    def auditService

    ImageDescriptor storeImage(byte[] imageBytes) {
        def uuid = UUID.randomUUID().toString()
        def imgDesc = new ImageDescriptor(imageIdentifier: uuid)
        String path = createOriginalPathFromUUID(uuid)

        File f = new File(path)
        f.parentFile.mkdirs()
        FileUtils.writeByteArrayToFile(f, imageBytes)
        def reader = ImageReaderUtils.findCompatibleImageReader(imageBytes)
        if (reader) {
            imgDesc.height = reader.getHeight(0)
            imgDesc.width = reader.getWidth(0)
            reader.dispose()
        }
        return imgDesc
    }

    private String createOriginalPathFromUUID(String uuid) {
        def l = [grailsApplication.config.imageservice.imagestore.root]
        computeAndAppendLocalDirectoryPath(uuid, l)
        l << "original"
        return l.join('/')
    }

    private String createThumbnailPathFromUUID(String uuid) {
        def l = [grailsApplication.config.imageservice.imagestore.root]
        computeAndAppendLocalDirectoryPath(uuid, l)
        l << "thumbnail"
        return l.join('/')
    }

    private String createSquareThumbnailPathFromUUID(String uuid, String backgroundColorName) {
        def l = [grailsApplication.config.imageservice.imagestore.root]
        computeAndAppendLocalDirectoryPath(uuid, l)
        if (backgroundColorName) {
            l << "thumbnail_square_${backgroundColorName}"
        } else {
            l << "thumbnail_square"
        }

        return l.join('/')
    }

    private String createTilesPathFromUUID(String uuid) {
        def l = [grailsApplication.config.imageservice.imagestore.root]
        computeAndAppendLocalDirectoryPath(uuid, l)
        l << "tms"
        return l.join('/')
    }

    private File getOriginalImageFile(String imageIdentifier) {
        def path = createOriginalPathFromUUID(imageIdentifier)
        return new File(path)
    }

    private static void computeAndAppendLocalDirectoryPath(String uuid, List bits) {
        for (int i = 1; i <= 4; ++i) {
            bits << uuid.charAt(uuid.length() - i);
        }
        bits << uuid // each image gets it's own directory
    }

    public byte[] retrieveImage(String imageIdentifier) {
        if (imageIdentifier) {
            def imageFile = getOriginalImageFile(imageIdentifier)
            byte[] data = null
            imageFile.withInputStream { is ->
                data = IOUtils.toByteArray(is)
            }
            return data
        }
        return null
    }

    public Map retrieveImageRectangle(String imageIdentifier, int x, int y, int width, int height) {

        def results = [bytes: null, contentType: ""]

        if (imageIdentifier) {
            def imageFile = getOriginalImageFile(imageIdentifier)
            def imageBytes = FileUtils.readFileToByteArray(imageFile);
            def reader = ImageReaderUtils.findCompatibleImageReader(imageBytes);
            if (reader) {
                try {
                    Rectangle stripRect = new Rectangle(x, y, width, height);
                    ImageReadParam params = reader.getDefaultReadParam();
                    params.setSourceRegion(stripRect);
                    params.setSourceSubsampling(1, 1, 0, 0);
                    // This may fail if there is not enough heap!
                    BufferedImage subimage = reader.read(0, params);
                    def bos = new ByteArrayOutputStream()
                    if (!ImageIO.write(subimage, "PNG", bos)) {
                        logService.debug("Could not create subimage in PNG format. Giving up")
                        return null
                    } else {
                        results.contentType = "image/png"
                    }
                    results.bytes = bos.toByteArray()
                    bos.close()
                } finally {
                    reader.dispose()
                }
            } else {
                throw new RuntimeException("No appropriate reader for image type!");
            }
        }

        return results
    }

    public Map getAllUrls(String imageIdentifier) {
        def root = grailsApplication.config.imageservice.apache.root
        def results = [:]
        def path = []
        computeAndAppendLocalDirectoryPath(imageIdentifier, path)

        results.imageUrl = root + (path + "original").join("/")
        results.thumbUrl = root + (path + "thumbnail").join("/")
        results.squareThumbUrl = root + (path + "thumbnail_square").join("/")
        results.tilesUrlPattern = root + (path + "tms").join("/") + "/{z}/{x}/{y}.png"

        return results
    }

    public String getImageUrl(String imageIdentifier) {
        def path = []
        computeAndAppendLocalDirectoryPath(imageIdentifier, path)
        path << "original"
        return grailsApplication.config.imageservice.apache.root + path.join("/")
    }

    public String getImageThumbUrl(String imageIdentifier) {
        def path = []
        computeAndAppendLocalDirectoryPath(imageIdentifier, path)
        path << "thumbnail"
        return grailsApplication.config.imageservice.apache.root + path.join("/")
    }

    public String getImageSquareThumbUrl(String imageIdentifier, String backgroundColor) {
        def path = []
        computeAndAppendLocalDirectoryPath(imageIdentifier, path)
        if (backgroundColor) {
            path << "thumbnail_square_${backgroundColor}"
        } else {
            path << "thumbnail_square"
        }
        return grailsApplication.config.imageservice.apache.root + path.join("/")
    }

    public String getImageTilesRootUrl(String imageIdentifier) {
        def path = []
        computeAndAppendLocalDirectoryPath(imageIdentifier, path)
        path << "tms"
        return grailsApplication.config.imageservice.apache.root + path.join("/")
    }

    /**
     * Create a number of thumbnail artifacts for an image, one the preserves the aspect ratio of the original image, the other drawing a scale image on a transparent
     * square with a constrained maximum dimension of config item "imageservice.thumbnail.size"
     * The first thumbnail (preserved aspect ratio) is of type JPG to conserve disk space, whilst the square thumb is PNG as JPG does not support alpha transparency
     * @param imageIdentifier The id of the image to thumb
     */
    public ThumbDimensions generateImageThumbnails(String imageIdentifier) {
        CodeTimer t = new CodeTimer("Thumbnail generation ${imageIdentifier}".toString())

        def imageFile = getOriginalImageFile(imageIdentifier)

        def imageBytes = FileUtils.readFileToByteArray(imageFile)
        def reader = ImageReaderUtils.findCompatibleImageReader(imageBytes)
        int size = grailsApplication.config.imageservice.thumbnail.size as Integer
        def thumbHeight = 0, thumbWidth = 0
        if (reader) {
            def imageParams = reader.getDefaultReadParam()
            def height = reader.getHeight(0)
            def width = reader.getWidth(0)

            BufferedImage thumbImage

            // Big images need to be thumbed via ImageReader to maintain O(1) heap use
            if (height > 1024 || width > 1024) {

                // roughly scale (subsample) the image to a max dimension of 1024
                int ratio
                if (height > width) {
                    ratio = (int) (height / 1024)
                } else {
                    ratio = (int) (width / 1024)
                }

                imageParams.setSourceSubsampling(ratio, ratio ?: 1, 0, 0)
                thumbImage = reader.read(0, imageParams)

                // then finely scale the sub sampled image to get the final thumbnail
                thumbImage = ImageUtils.scaleWidth(thumbImage, size)
            } else {
                // small images
                thumbImage = loadImage(imageIdentifier)
                thumbImage = ImageUtils.scaleWidth(thumbImage, size)
            }

            if (thumbImage) {
                thumbHeight = thumbImage.height
                thumbWidth = thumbImage.width

                def thumbFilename = createThumbnailPathFromUUID(imageIdentifier)
                def thumbFile = new File(thumbFilename)
                ImageIO.write(thumbImage, "JPG", thumbFile)

                def backgroundColors = ['white', 'black', 'darkGray']

                // for each background color (where empty string means transparent), create a squared thumb
                // If not transparent, keep as jpeg for speed/space!
                backgroundColors.each { colorName ->

                    Color backgroundColor = null
                    if (colorName) {
                        try {
                            Field field = Color.class.getField(colorName);
                            backgroundColor = (Color)field.get(null);
                        } catch (Exception e) {
                            backgroundColor = null; // Not defined
                        }
                    }

                    BufferedImage temp
                    if (!backgroundColor) {
                        temp = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR)
                    } else {
                        temp = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR)
                    }

                    def g = temp.graphics
                    if (colorName && backgroundColor) {
                        g.setColor(backgroundColor);
                        g.fillRect(0, 0, size, size)
                    }

                    if (thumbHeight < size) {
                        int top = (size / 2) - (thumbHeight / 2)
                        g.drawImage(thumbImage, 0, top, null)
                    } else if (thumbWidth < size) {
                        int left = (size / 2) - (thumbWidth / 2)
                        g.drawImage(thumbImage, left, 0, null)
                    }

                    g.dispose()

                    thumbFilename = createSquareThumbnailPathFromUUID(imageIdentifier, colorName)
                    thumbFile = new File(thumbFilename)
                    if (colorName && backgroundColor) {
                        ImageIO.write(temp, "JPG", thumbFile)
                    } else {
                        ImageIO.write(temp, "PNG", thumbFile)
                    }

                }

            }
        } else {
            logService.log("No image readers for image ${imageIdentifier}!")
        }
        t.stop(true)
        auditService.log(imageIdentifier, "Thumbnails created", "N/A")
        return new ThumbDimensions(height: thumbHeight, width: thumbWidth, squareThumbSize: size)
    }

    public void generateTMSTiles(String imageIdentifier) {
        logService.log("Generating TMS compatible tiles for image ${imageIdentifier}")
        def ct = new CodeTimer("Tiling image ${imageIdentifier}")
        def imageFile = getOriginalImageFile(imageIdentifier)

        // Calculate where the tiles will live on the file system
        def bits = [grailsApplication.config.imageservice.imagestore.root]
        computeAndAppendLocalDirectoryPath(imageIdentifier, bits)
        bits << "tms"
        def tileRootPath = bits.join("/")

        def rootFile = new File(tileRootPath)

        def results = tileImage(imageFile, rootFile)
        if (results.success) {
            def image = Image.findByImageIdentifier(imageIdentifier)
            if (image) {
                image.zoomLevels = results.zoomLevels
                image.save(flush: true, failOnError: true)
            } else {
                logService.log("Image not found in database! ${imageIdentifier}")
            }
        } else {
            logService.log("Image tiling failed! ${results}");
        }
        auditService.log(imageIdentifier, "TMS tiles generated", "N/A")
        ct.stop(true)
    }

    private static ImageTilerResults tileImage(File imageFile, File destination) {
        def config = new ImageTilerConfig(2,2,256, 6, TileFormat.JPEG)
        config.setTileBackgroundColor(new Color(221, 221, 221))
        def tiler = new ImageTiler(config)
        return tiler.tileImage(imageFile, destination)
    }

    private BufferedImage loadImage(String imageIdentifier) {
        String path = createOriginalPathFromUUID(imageIdentifier)
        File f = new File(path)
        if (f.exists()) {
            def image = ImageIO.read(f)
            return image
        } else {
            throw new RuntimeException("File not found! ${path}")
        }
    }

    public boolean deleteImage(String imageIdentifier) {
        if (imageIdentifier) {
            File f = getOriginalImageFile(imageIdentifier)
            if (f && f.exists()) {
                FileUtils.deleteQuietly(f.parentFile)
                auditService.log(imageIdentifier, "Image deleted from store", "N/A")
                return true
            }
        }
        return false
    }

    public boolean storeTilesArchiveForImage(String imageIdentifier, MultipartFile zipFile) {

        def original = getOriginalImageFile(imageIdentifier)
        if (original && original.exists()) {
            def parent = original.parentFile
            def stagingFile = new File(parent.absolutePath + "/tiles.zip")
            if (stagingFile.exists()) {
                stagingFile.delete()
            }

            // copy the zip file to the staging area
            stagingFile.newOutputStream() << zipFile.inputStream
            def tilesRoot = createTilesPathFromUUID(imageIdentifier)

            def ant = new AntBuilder()
            ant.unzip(
                    src: stagingFile.absolutePath,
                    dest: tilesRoot,
                    overwrite: true
            )
            // TODO: validate the extracted contents
            auditService.log(imageIdentifier, "Image tiles stored from zip file (outsourced job?)", "N/A")
            return true
        }
        return false
    }

    public long getConsumedSpaceOnDisk(String imageId) {
        def original = getOriginalImageFile(imageId)
        if (original && original.exists()) {
            return FileUtils.sizeOfDirectory(original.parentFile)
        }
        return 0
    }

    public long getRepositorySizeOnDisk() {
        def dir = new File(grailsApplication.config.imageservice.imagestore.root)
        if (dir && dir.exists()) {
            return FileUtils.sizeOfDirectory(dir)
        }
        return 0
    }

}
