modules = {

    application {
        dependsOn 'jquery'
        resource url:'js/application.js'
    }

    leaflet {
        dependsOn 'jquery'
        resource url: 'leaflet/leaflet.css'
        resource url: 'leaflet/Control.FullScreen.css'
        resource url: 'leaflet/leaflet.js'
        resource url: 'leaflet/Control.FullScreen.js'
    }

    leaflet_draw {
        dependsOn 'leaflet'
        resource 'leaflet.draw/leaflet.draw.css'
        resource 'leaflet.draw/leaflet.draw.js'

    }

    jstree {
        dependsOn 'jquery'
        resource url: 'jstree/jstree.min.js'
        resource url: 'jstree/themes/default/style.min.css'
    }

    qtip {
        dependsOn 'jquery'
        resource url: 'qtip/jquery.qtip.min.css'
        resource url: 'qtip/jquery.qtip.min.js'
    }

    handlebars {
        dependsOn 'jquery'
        resource url:'handlebars/handlebars-v1.3.0.js'
    }

    jstypeahead {
        dependsOn 'jquery,handlebars,bootstrap'
        resource url: 'typeahead/typeahead.js'
    }

    "bootstrap-datepicker" {
        dependsOn "bootstrap, jquery"
        resource url: "bootstrap-datepicker/bootstrap-datepicker.js"
        resource url: "bootstrap-datepicker/datepicker.css"
    }

    "bootstrap-switch" {
        dependsOn "bootstrap, jquery"
        resource url: 'bootstrap-switch/bootstrap-switch.css'
        resource url: 'bootstrap-switch/bootstrap-switch.js'
    }

}