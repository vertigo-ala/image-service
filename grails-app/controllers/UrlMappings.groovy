class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/ws/image/$id?(.$format)?"(controller: "webService", action: "getImageInfo")
        "/ws/updateMetadata/$imageIdentifier"(controller: "webService", action: "updateMetadata")

        "/ws/$action?/$id?(.$format)?" {
            controller = "webService"
        }

        // legacy URLS
        "/image/proxyImageThumbnail"(controller: "image", action: "proxyImageThumbnail")
        "/image/proxyImageThumbnailLarge"(controller: "image", action: "proxyImageThumbnailLarge")
        "/image/proxyImageTile"(controller: "image", action: "proxyImageTile")
        "/image/proxyImage"(controller: "image", action: "proxyImage")
        "/image/details"(controller: "image", action: "details")

        //analytics
        "/ws/analytics"(controller: "analytics", action: "byAll")
        "/ws/analytics/dataResource/$dataResourceUID"(controller: "analytics", action: "byDataResource")

        name image_url: "/image/$imageId"(controller: "image", action: "details")

        "/"(controller:'search', action:'list')
        "500"(view:'/error')
	}
}
