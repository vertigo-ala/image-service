class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/ws/image/$imageID?(.$format)?"(controller: "webService") {
                action = [GET: 'getImageInfo', DELETE: 'deleteImageService']
        }
        "/ws/updateMetadata/$imageIdentifier"(controller: "webService", action: "updateMetadata")
        "/ws/getImageInfo/$imageID"(controller: "webService", action:'getImageInfo')
        "/ws/repositoryStatistics"(controller: "webService", action:'getRepositoryStatistics')
        "/ws/repositorySizeOnDisk"(controller: "webService", action:'getRepositorySizeOnDisk')
        "/ws/backgroundQueueStats"(controller: "webService", action:'getBackgroundQueueStats')

        "/ws/$action?/$id?(.$format)?" {
            controller = "webService"
        }

        "/ws/$action?" {
            controller = "webService"
        }

        name api_doc: "/ws"(controller: 'apiDoc', action: 'getDocuments')
        "/ws/"(controller: 'apiDoc', action: 'getDocuments')

        // legacy URLS
        "/image/proxyImageThumbnail"(controller: "image", action: "proxyImageThumbnail")
        "/image/proxyImageThumbnailLarge"(controller: "image", action: "proxyImageThumbnailLarge")
        "/image/proxyImageTile"(controller: "image", action: "proxyImageTile")
        "/image/proxyImage"(controller: "image", action: "proxyImage")

        // homogeneous URLs
        "/image/$id/thumbnail"(controller: "image", action: "proxyImageThumbnail")
        "/image/$id/large"(controller: "image", action: "proxyImageThumbnailLarge")
        "/image/$id/tms"(controller: "image", action: "proxyImageTile")
        "/image/$id/original"(controller: "image", action: "proxyImage")

        "/admin/image/$imageId"(controller: "admin", action: "image")
        "/image/details"(controller: "image", action: "details")

        //analytics
        "/ws/analytics"(controller: "analytics", action: "byAll")
        "/ws/analytics/$dataResourceUID"(controller: "analytics", action: "byDataResource")
        "/ws/analytics/dataResource/$dataResourceUID"(controller: "analytics", action: "byDataResource")

        name image_url: "/image/$imageId"(controller: "image", action: "details")

        "/"(controller:'search', action:'list')
        "500"(view:'/error')
	}
}
