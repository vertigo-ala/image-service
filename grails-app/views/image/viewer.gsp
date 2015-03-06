<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Image ${imageInstance.originalFilename}</title>

        <style>
        html, body {
            height:100%;
            padding: 0;
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

        <r:require module="bootstrap" />
        <r:require module="leaflet" />
        <r:require module="leaflet_draw" />

        <r:require module="viewer" />
        <r:layoutResources/>
        <r:script>

        $(document).ready(function() {
            var options = {
                imageServiceBaseUrl : "${grailsApplication.config.serverName}${grailsApplication.config.contextPath}",
                auxDataUrl : "${auxDataUrl}"
            };

            imgvwr.viewImage($("#imageViewer"), "${imageInstance.imageIdentifier}", options);
        });
        </r:script>

    </head>
    <body class="content" style="padding-top:0px;">
        <div id="imageViewerContainer" class="container-fluid">
            <div id="imageViewer">
            </div>
        </div>
        <r:layoutResources/>
    </body>
</html>
