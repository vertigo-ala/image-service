package au.org.ala.images

import grails.transaction.Transactional

@Transactional
class AlbumService {

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

}
