package au.org.ala.images

import au.org.ala.cas.util.AuthenticationUtils
import au.org.ala.web.AlaSecured
import au.org.ala.web.CASRoles
import grails.converters.JSON

@AlaSecured(value = [CASRoles.ROLE_USER], redirectUri = '/')
class AlbumController {

    def imageService
    def albumService
    def selectionService

    def index() {
        def userId = AuthenticationUtils.getUserId(request)
        def albums = albumService.getUserAlbums(userId, params)

        def c = AlbumImage.createCriteria()
        def counts = c.list() {
            album {
                eq("userId", userId)
            }
            projections {
                count("image")
            }
            groupProperty("album")
        }

        def countMap = [:]
        counts.each { arr ->
            countMap[arr[1]] = arr[0]
        }

        def selectedAlbum = Album.get(params.int("id"))
        if (!selectedAlbum && albums?.size() > 0) {
            selectedAlbum = albums.get(0)
        }

        [albums: albums, countMap: countMap, selectedAlbum: selectedAlbum]
    }

    def userContextFragment() {
        def userId = AuthenticationUtils.getUserId(request)

        if (!userId) {
            render("<div/>")
            return
        }

        def albums = albumService.getUserAlbums(userId, params)
        [albums: albums]
    }

    def editAlbumFragment() {
        def album = Album.get(params.int("id"))
        // null album is ok - means we are creating a new one...
        [album: album]
    }


    def saveAlbum() {
        def userId = AuthenticationUtils.getUserId(request)
        if (!userId) {
            flash.errorMessage = "Could not identify current user!"
            redirect(uri:"/")
            return
        }

        def album = Album.get(params.int("id"))
        if (album) {
            album.description = params.description
            album.name = params.name
        } else {
            album = new Album(name: params.name ?: "<New album>", userId: userId, externalIdentifier: UUID.randomUUID().toString(), description: params.description)
            album.save(flush: true, failOnError: true)
        }
        redirect(action:'index')
    }

    def ajaxDeleteAlbum() {
        def album = Album.get(params.int('id'))
        if (album) {
            albumService.deleteAlbum(album)
            render([success: true] as JSON)
            return
        }
        render([success: false, message: "Album not found, or no album id specified."] as JSON)
    }

    def albumDetailsFragment() {

        def userId = AuthenticationUtils.getUserId(request)
        def album = Album.get(params.int("id"))
        if (!album) {
            render("<div />")
            return
        }
        params.max = params.max ?: 48

        def imageList = albumService.getAlbumImages(album, params)
        def selectedImageMap = selectionService.getSelectedImageIdsAsMap(userId)
        [album: album, imageList: imageList.list, selectedImageMap: selectedImageMap, totalCount: imageList.totalCount]
    }

    def ajaxRemoveImageFromAlbum() {
        def album = Album.get(params.int("id"))
        def image = Image.get(params.int("imageId"));
        if (album && image) {
            albumService.removeImageFromAlbum(album, image)
        }
        render([success: true] as JSON)
    }

    def selectAlbumFragment() {
        def userId = AuthenticationUtils.getUserId(request)
        def albums = []
        if (userId) {
            albums = albumService.getUserAlbums(userId, params)
        }
        [albums: albums]
    }

    def ajaxCreateNewAlbum() {
        def userId = AuthenticationUtils.getUserId(request)
        if (!userId) {
            render([success:false, message:'Missing or invalid user id!'] as JSON)
            return
        }

        def albumName = params.albumName
        if (!albumName) {
            render([success:false, message:'Missing or invalid album name!'] as JSON)
            return
        }
        
        def existing = Album.findByUserIdAndName(userId, albumName)

        if (existing) {
            render([success:false, message:"An album called ${albumName} already exists!"] as JSON)
            return
        }

        def album = new Album(name: albumName, userId: userId, externalIdentifier: UUID.randomUUID().toString(), description: params.description)
        album.save(flush: true, failOnError: true)

        render([success:true, albumId: album.id, message:"Album called ${albumName} created"] as JSON)
    }

    def ajaxAddImageToAlbum() {
        def album = Album.get(params.int("id"))
        def image = Image.get(params.int("imageId"))
        if (album && image) {
            albumService.addImageToAlbum(album, image)
            render([success:true] as JSON)
            return
        }
        render([success:false] as JSON)
    }

    def ajaxScheduleTileGeneration() {
        def album = Album.get(params.int("id"))
        if (album) {
            albumService.scheduleTileRegeneration(album)
            render([success:true] as JSON)
            return
        }
        render([success:false, message:'Missing or invalid album id'] as JSON)
    }

    def ajaxScheduleThumbnailGeneration() {
        def album = Album.get(params.int("id"))
        if (album) {
            albumService.scheduleThumbnailRegeneration(album)
            render([success:true] as JSON)
            return
        }
        render([success:false, message:'Missing or invalid album id'] as JSON)
    }

    def deleteAllImages() {
        def album = Album.get(params.int("id"))
        if (album) {
            def userId = AuthenticationUtils.getUserId(request) ?: "<unknown>"
            albumService.deleteAllImages(album, userId)
            redirect(action:'index', id: album.id)
            return
        }

        flash.message = "Missing or invalid album id!"
        redirect(action:index())
    }

    def ajaxTagImages() {
        def album = Album.get(params.int("id"))
        def tag = Tag.get(params.int("tagId"))
        if (album) {
            if (tag) {
                def userId = AuthenticationUtils.getUserId(request) ?: "<unknown>"
                albumService.tagImages(album, tag, userId)
                render([success: true] as JSON)

            } else {
                render([success: false, message: 'Missing or invalid tag id'] as JSON)
            }
        } else {
            render([success: false, message: 'Missing or invalid album id'] as JSON)
        }
    }

    def ajaxAddMetaData() {
        def album = Album.get(params.int("id"))
        def key = params.key as String
        def value = params.value as String
        if (album) {
            if (key && value) {
                albumService.attachMetadata(album, key, value, MetaDataSourceType.UserDefined)
                render([success: true] as JSON)
            } else {
                render([success: false, message: 'Missing either key or value!'] as JSON)
            }
        } else {
            render([success: false, message: 'Missing or invalid album id'] as JSON)
        }
    }

    def exportAsCSV() {
        def album = Album.get(params.int("id"))
        if (!album) {
            flash.message = "Missing or invalid album id!"
            redirect(controller: 'album')
            return
        }
        def previewData = []
        def results = albumService.getAlbumImages(album, [max: 10])
        results.list.each { image ->
            previewData << ['imageUrl': imageService.getImageUrl(image.imageIdentifier)]
        }
        [album: album, previewData: previewData]
    }

}
