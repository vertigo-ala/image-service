<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <meta name="section" content="home"/>
    <title>ALA Image Service - Selected Images</title>
</head>

<body class="content">

    <img:headerContent title="Selected images">
        <%
            pageScope.crumbs = [
            ]
        %>
    </img:headerContent>

    <div class="well well-small">
        <button class="btn" id="btnClearSelection">Clear selection</button>
        <button class="btn" id="btnRegenerateThumbs">Regenerate thumbnails</button>
        <button class="btn" id="btnRegenerateTiles">Regenerate tiles</button>

        <a href="${createLink(controller:'selection', action:'deleteSelected')}" class="btn btn-danger pull-right"><i class="icon-remove icon-white"></i>&nbsp;Delete all selected</a>
    </div>

    <table class="table table-condensed table-bordered table-striped">
        <thead>
            <tr>
                <th width="110">Thumb</th>
                <th>Name</th>
                <th>Size</th>
                <th>Content type</th>
            </tr>
        </thead>
        <tbody>
            <g:each in="${selectedImages*.image}" var="image">
                <tr>
                    <td>
                        <a href="${createLink(controller:'image', action:'details', id: image.id)}">
                            <img src="<img:imageSquareThumbUrl imageId='${image.imageIdentifier}'/>" width="100" />
                        </a>
                    </td>
                    <td><a href="${createLink(controller:'image', action:'details', id: image.id)}">${image.originalFilename ?: image.imageIdentifier}</a></td>
                    <td><img:sizeInBytes size="${image.fileSize}" /></td>
                    <td>${image.mimeType}</td>
                </tr>
            </g:each>
        </tbody>
    </table>

    <r:script>
        $(document).ready(function() {
            $("#btnClearSelection").click(function(e) {
                e.preventDefault();
                $.ajax("${createLink(controller: 'selection', action:'clearSelection')}").done(function() {
                    window.location = "${createLink(controller: 'selection', action:"list")}";
                });
            });

            $("#btnRegenerateThumbs").click(function(e) {
                e.preventDefault();
                window.location = "${createLink(controller: 'selection', action:'generateThumbnails')}";
            });

            $("#btnRegenerateTiles").click(function(e) {
                e.preventDefault();
                window.location = "${createLink(controller: 'selection', action:'generateTMSTiles')}";
            });

        });

    </r:script>

</body>
</html>
