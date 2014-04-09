class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/ws/$action?/$id?(.$format)?" {
            controller = "webService"
        }

        "/"(controller:'image', action:'list')
        "500"(view:'/error')
	}
}
