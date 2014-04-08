<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <meta name="section" content="home"/>
        <title>ALA Image Service - Images</title>
    </head>

    <body class="content">

        <img:headerContent title="Images">
            <%
                pageScope.crumbs = [
                ]
            %>
        </img:headerContent>

        <div class="well well-small">
            <a class="btn" href="${createLink(action:'upload')}"><i class="icon-upload"></i>&nbsp;Upload an image</a>
            <input type="text" class="input-large" id="keyword" style="margin-bottom: 0" value="${q}" />
            <button class="btn" id="btnFindImagesByKeyword"><i class="icon-search"></i>&nbsp;Search</button>
            <a class="btn btn-info" id="btnAdvancedSearch" href="${createLink(controller:'search', action:'index')}"><i class="icon-cog icon-white"></i>&nbsp;Advanced Search</a>
            <span id="selectionContext" style="display: inline-block"></span>
        </div>

        <h4>${totalImageCount} images</h4>

        <g:render template="imageThumbnails" model="${[images: images, totalImageCount: totalImageCount, allowSelection: isLoggedIn, selectedImageMap: selectedImageMap]}" />

    <r:script>
            $(document).ready(function() {

                $("#btnFindImagesByKeyword").click(function(e) {
                    e.preventDefault();
                    doSearch();
                });

                $("#keyword").keydown(function(e) {
                    if (e.which == 13) {
                        e.preventDefault();
                        doSearch();
                    }
                }).focus();

                updateSelectionContext();

            });

            function doSearch() {
                var q = $("#keyword").val();
                if (q) {
                    window.location = "${createLink(action:'list')}?q=" + q
                } else {
                    window.location = "${createLink(action:'list')}";
                }
            }

        function updateSelectionContext() {
            $.ajax("${createLink(controller:'selection', action:'userContextFragment')}").done(function(content) {
                $("#selectionContext").html(content);
            });
        }

        </r:script>

    </body>
</html>
