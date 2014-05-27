package au.org.ala.images

import au.org.ala.cas.util.AuthenticationUtils
import au.org.ala.web.AlaSecured
import au.org.ala.web.CASRoles
import grails.converters.JSON

@AlaSecured(value = [CASRoles.ROLE_USER], redirectUri = '/')
class AlbumController {

    def auditService
    def albumService


    def index() {
        def userId = AuthenticationUtils.getUserId(request)
        def albums = Album.findAllByUserId(userId, [sort:'name'])

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

        [albums: albums, countMap: countMap, selectedAlbum: albums?.get(0)]
    }

    def userContextFragment() {
        def userId = AuthenticationUtils.getUserId(request)

        if (!userId) {
            render("<div/>")
            return
        }

        def albums = Album.findAllByUserId(userId, [sort:'name'])

        [albums: albums]
    }

    def createAlbumFragment() {
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
        def album = Album.get(params.int("id"))
        if (!album) {
            render("<div />")
            return
        }
        params.max = params.max ?: 48

        def c = AlbumImage.createCriteria()
        def albumImages = c.list(params) {
            eq('album', album)
        }

        def imageList = albumImages*.image
        [album: album, albumImages: albumImages, imageList: imageList]
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
            albums = Album.findAllByUserId(userId, [sort:'name'])
        }
        [albums: albums]
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

}
