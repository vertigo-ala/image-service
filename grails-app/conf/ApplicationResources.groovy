modules = {

    application {
        dependsOn 'jquery', 'jquery-migration', 'image_viewer'
        resource url:'js/application.js'
        resource url:'css/img-bootstrap.css'
    }

    jstree {
        dependsOn 'jquery'
        resource url: 'js/jstree/jstree.min.js'
        resource url: 'js/jstree/themes/default/style.min.css'
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

    qtip {
        dependsOn "jquery"
        resource "js/qtip/jquery.qtip.min.js"
        resource "js/qtip/jquery.qtip.min.css"
    }
}