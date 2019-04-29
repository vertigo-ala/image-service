<!doctype html>
<html>
    <head>
        <meta name="layout" content="images"/>
        <meta name="section" content="home"/>
        <title>Image Service | ${grailsApplication.config.skin.orgNameLong}</title>
    </head>

    <body class="content">

        <img:headerContent title="Images" hideCrumbs="${true}">
        </img:headerContent>

        <div class="">
            <input type="text" class="input-xlarge" id="keyword" style="margin-bottom: 0" value="${q}" />
            <button class="btn" id="btnFindImagesByKeyword"><i class="icon-search"></i>&nbsp;Search</button>
            <a class="btn btn-info" id="btnAdvancedSearch" href="${createLink(controller:'search', action:'index')}"><i class="icon-cog icon-white"></i>&nbsp;Advanced Search</a>
        </div>

        <g:render template="imageThumbnails" model="${[images: images, totalImageCount: totalImageCount, allowSelection: isLoggedIn, selectedImageMap: selectedImageMap, thumbsTitle:"${totalImageCount} images"]}" />

    <script>
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

            });

            function doSearch() {
                var q = $("#keyword").val();
                if (q) {
                    window.location = "${createLink(action:'list')}?q=" + q
                } else {
                    window.location = "${createLink(action:'list')}";
                }
            }

        </script>

    </body>
</html>
