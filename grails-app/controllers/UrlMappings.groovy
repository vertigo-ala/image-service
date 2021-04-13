class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/ws/image/$imageID?(.$format)?"(controller: "webService") {
                action = [GET: 'getImageInfo', DELETE: 'deleteImageService', HEAD: 'getImageInfo']
        }
        "/ws/updateMetadata/$imageIdentifier"(controller: "webService", action: "updateMetadata")
        "/ws/getImageInfo/$imageID"(controller: "webService", action:'getImageInfo')
        "/ws/repositoryStatistics"(controller: "webService", action:'getRepositoryStatistics')
        "/ws/repositorySizeOnDisk"(controller: "webService", action:'getRepositorySizeOnDisk')
        "/ws/backgroundQueueStats"(controller: "webService", action:'getBackgroundQueueStats')
        "/ws/metadatakeys"(controller: "webService", action:'getMetadataKeys')
        "/ws/batchstatus"(controller: "webService", action:'getBatchStatus')
        "/ws/imageInfoForList"(controller: "webService", action: "getImageInfoForIdList")

        "/ws/$action?/$id?(.$format)?" {
            controller = "webService"
        }

        "/ws/$action?" {
            controller = "webService"
        }

        "/ws/api"(controller: 'apiDoc', action: 'getDocuments')
        name api_doc: "/ws/"(controller: 'webService', action: 'swagger')
        "/ws"(controller: 'webService', action: 'swagger')

        // legacy URLS
        "/image/imageTooltipFragment"(controller: "image", action: "imageTooltipFragment")
        "/image/proxyImageThumbnail"(controller: "image", action: "proxyImageThumbnail")
        "/image/proxyImageThumbnailLarge"(controller: "image", action: "proxyImageThumbnailLarge")
        "/image/proxyImageTile"(controller: "image", action: "proxyImageTile")
        "/image/proxyImage"(controller: "image", action: "proxyImage")
        "/proxyImageThumbnail"(controller: "image", action: "proxyImageThumbnail")
        "/proxyImageThumbnailLarge"(controller: "image", action: "proxyImageThumbnailLarge")
        "/proxyImageTile"(controller: "image", action: "proxyImageTile")
        "/proxyImage"(controller: "image", action: "proxyImage")

        "/image/viewer"(controller:"image", action: "viewer")
        "/image/view/$id"(controller:"image", action: "viewer")
        "/image/viewer/$id"(controller:"image", action: "viewer")

        // homogeneous URLs
        "/image/$id/thumbnail"(controller: "image", action: "proxyImageThumbnail")
        "/image/$id/large"(controller: "image", action: "proxyImageThumbnailLarge")
        "/image/$id/tms"(controller: "image", action: "proxyImageTile")
        "/image/$id/original"(controller: "image", action: "getOriginalFile")

        "/admin/image/$imageId"(controller: "admin", action: "image")
        "/image/details"(controller: "image", action: "details")

        //analytics
        "/ws/analytics"(controller: "analytics", action: "byAll")
        "/ws/analytics/$dataResourceUID"(controller: "analytics", action: "byDataResource")
        "/ws/analytics/dataResource/$dataResourceUID"(controller: "analytics", action: "byDataResource")

        name image_url: "/image/$imageId"(controller: "image", action: "details")
        name image_ws_url: "/ws/image/$imageId"(controller: "webService", action: "getImageInfo")

        //tags
        "/ws/tags"(controller: "webService", action: "getTagModel")
        "/ws/tag"(controller: "webService", action: "createTagByPath")

        "/ws/tag/$tagId/rename"(controller: "webService", action: "renameTag")
        "/ws/tag/$tagId/move"(controller: "webService", action: "moveTag")
        "/ws/tag/$tagId/images"(controller: "webService", action: "getImagesForTag")

        "/ws/images/keyword/$keyword"(controller: "webService", action: "getImagesForKeyword")
        "/ws/images/tag/$tagID"(controller: "webService", action: "getImagesForTag")

        "/ws/tag/$tagId/image/$imageId"(controller: "webService"){
           action = [GET: 'attachTagToImage', PUT: 'attachTagToImage', DELETE: 'detachTagFromImage']
        }
        "/ws/tag/$tagId/images"(controller: "webService", action:"getImagesForTag")

        "/"(controller:'search', action:'list')
        "500"(view:'/error')
	}
}
