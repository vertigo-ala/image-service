package au.org.ala.images

import grails.transaction.Transactional

@Transactional
class AlbumService {

    def imageService
    def tagService

    def deleteAlbum(Album album) {
        if (!album) {
            return false
        }

        // first delete album images
        def albumImages = AlbumImage.findAllByAlbum(album)
        albumImages.each { albumImage ->
            albumImage.delete()
        }

        // then delete the album
        album.delete()
    }

    def addImageToAlbum(Album album, Image image) {
        if (!album || !image) {
            return
        }

        // Does this image already exist in this album
        def albumImage = AlbumImage.findByAlbumAndImage(album, image)
        if (!albumImage) {
            albumImage = new AlbumImage(album: album, image: image)
            albumImage.save()
        }
        return albumImage
    }

    def removeImageFromAlbum(Album album, Image image) {
        if (!album || !image) {
            return
        }

        def albumImage = AlbumImage.findByAlbumAndImage(album, image)
        if (albumImage) {
            albumImage.delete()
        }
    }

    def withImageIds(Album album, Closure closure) {
        if (!album) {
            return
        }

        // Get a list of just the images ids...
        def c = AlbumImage.createCriteria()
        def imageIds = c.list {
            eq('album', album)
            projections {
                image {
                    property("id")
                }
            }
        }

        if (imageIds && closure) {
            imageIds.each { imageId ->
                closure(imageId)
            }
        }
    }

    def scheduleTileRegeneration(Album album) {
        if (album) {
            withImageIds(album) { imageId ->
                imageService.scheduleTileGeneration(imageId)
            }
        }
    }

    def scheduleThumbnailRegeneration(Album album) {
        if (album) {
            withImageIds(album) { imageId ->
                imageService.scheduleThumbnailGeneration(imageId)
            }
        }
    }

    def deleteAllImages(Album album, String userId) {
        if (album) {
            withImageIds(album) { imageId ->
                def image = Image.get(imageId)
                if (image) {
                    imageService.deleteImage(image, userId)
                }
            }
        }
    }

    def tagImages(Album album, Tag tag, String userId) {
        if (album && tag) {
            withImageIds(album) { imageId ->
                def image = Image.get(imageId)
                if (image) {
                    tagService.attachTagToImage(image, tag, userId)
                }
            }
        }
    }

}
