<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Image ${imageInstance.originalFilename} | Image Service | ${grailsApplication.config.skin.orgNameLong}</title>
        <style>
        html, body {
            height:100%;
            padding: 0;
            margin:0;
        }
        #imageViewerContainer {
            height: 100%;
            padding: 0;
        }
        #imageViewer {
            width: 100%;
            height: 100%;
            margin: 0;
        }
        </style>
        <link rel="stylesheet" href="/assets/font-awesome-4.7.0/css/font-awesome.css?compile=false" />
        <asset:stylesheet src="ala/images-client.css" />
    </head>
    <body style="padding:0;">
        <div id="imageViewerContainer" class="container-fluid">
            <div id="imageViewer"> </div>
        </div>
        <asset:javascript src="head.js"/>
        <asset:javascript src="ala/images-client.js"/>
        <script>
            $(document).ready(function() {
                var options = {
                    auxDataUrl : "${auxDataUrl ? auxDataUrl : ''}",
                    imageServiceBaseUrl : "${grailsApplication.config.grails.serverURL}${grailsApplication.config.server.contextPath}",
                    imageClientBaseUrl : "${grailsApplication.config.grails.serverURL}${grailsApplication.config.server.contextPath}"
                };
                imgvwr.viewImage($("#imageViewer"), "${imageInstance.imageIdentifier}", "", "", options);
            });
        </script>
    </body>
</html>
