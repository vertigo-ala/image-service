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

        <div class="btn-group">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                Actions
                <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li>
                    <a href="#" id="">Add meta data</a>
                </li>
                <li>
                    <a href="#" id="">Add tags</a>
                </li>
            </ul>
        </div>

        <auth:ifAnyGranted roles="${au.org.ala.web.CASRoles.ROLE_ADMIN}">≈
        <div class="btn-group">
            <a class="btn dropdown-toggle btn-warning" data-toggle="dropdown" href="#">
                <i class="icon-cog icon-white"></i>&nbsp;Admin functions
                <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li>
                    <a href="#" id="btnRegenerateThumbs">Regenerate thumbnails</a>
                </li>
                <li>
                    <a href="#" id="btnRegenerateTiles">Regenerate tiles</a>
                </li>
                <li class="divider"></li>
                <li>
                    <a href="#" id="btnDeleteImages">Delete Images</a>
                </li>
            </ul>
        </div>
        </auth:ifAnyGranted>

        <button class="btn" id="btnClearSelection">Clear selection</button>

        %{--<a href="" class="btn btn-danger pull-right"><i class="icon-remove icon-white"></i>&nbsp;Delete all selected</a>--}%
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
                $.ajax("${createLink(controller: 'selection', action: 'clearSelection')}").done(function() {
                    window.location = "${createLink(controller: 'selection', action: "list")}";
                });
            });

            $("#btnRegenerateThumbs").click(function(e) {
                e.preventDefault();
                window.location = "${createLink(controller: 'selection', action: 'generateThumbnails')}";
            });

            $("#btnRegenerateTiles").click(function(e) {
                e.preventDefault();
                window.location = "${createLink(controller: 'selection', action: 'generateTMSTiles')}";
            });

            $("#btnDeleteImage").click(function(e) {
                window.location = "${createLink(controller: 'selection', action: 'deleteSelected')}";
            });

        });

    </r:script>

</body>
</html>
