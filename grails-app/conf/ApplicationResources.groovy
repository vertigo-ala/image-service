modules = {

    application {
        dependsOn 'jquery','imglib'
        resource url:'js/application.js'
    }

    leaflet {
        dependsOn 'jquery'
        resource url: 'js/leaflet/leaflet.css'
        resource url: 'js/leaflet/Control.FullScreen.css'
        resource url: 'js/leaflet/leaflet.js'
        resource url: 'js/leaflet/Control.FullScreen.js'
        resource url: 'js/leaflet/leaflet.measure.js'
        resource url: 'js/leaflet/leaflet.measure.css'
    }

    leaflet_draw {
        dependsOn 'leaflet'
        resource 'js/leaflet.draw/leaflet.draw.css'
        resource 'js/leaflet.draw/leaflet.draw.js'

    }

    jstree {
        dependsOn 'jquery'
        resource url: 'js/jstree/jstree.min.js'
        resource url: 'js/jstree/themes/default/style.min.css'
    }

    qtip {
        dependsOn 'jquery'
        resource url: 'js/qtip/jquery.qtip.min.css'
        resource url: 'js/qtip/jquery.qtip.min.js'
    }

    handlebars {
        dependsOn 'jquery'
        resource url:'js/handlebars/handlebars-v1.3.0.js'
    }

    jstypeahead {
        dependsOn 'jquery,handlebars,bootstrap'
        resource url: 'js/typeahead/typeahead.js'
    }

    "bootstrap-datepicker" {
        dependsOn "bootstrap, jquery"
        resource url: "js/bootstrap-datepicker/bootstrap-datepicker.js"
        resource url: "js/bootstrap-datepicker/datepicker.css"
    }

    "bootstrap-switch" {
        dependsOn "bootstrap, jquery"
        resource url: 'js/bootstrap-switch/bootstrap-switch.css'
        resource url: 'js/bootstrap-switch/bootstrap-switch.js'
    }

    imglib {
        dependsOn "jquery"
        resource url: 'js/images.js'
    }

    audiojs {
        dependsOn "jquery"
        resource url: 'js/audiojs/audio.min.js'
    }

    viewer {
        dependsOn "jquery"
        resource url: 'js/ala-image-viewer.js'
    }

}