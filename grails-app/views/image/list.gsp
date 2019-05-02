<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Images</title>
        <asset:stylesheet src="application.css" />
    </head>
    <body class="fluid">
        <h1>${totalImageCount} Images</h1>
        <!-- search box -->
        <div class="search" style="margin-bottom:20px;">
            <g:form action="list" controller="image" method="get">
            <div class="input-group">
                <input type="text" class="input-large form-control" id="keyword" name="q" value="${params.q}" />
                <div class="input-group-btn">
                    <button class="btn btn-primary" type="submit">
                        <span class="glyphicon glyphicon-search"></span>
                        Search
                    </button>
                    <a class="btn btn-default" href="${createLink(controller:'search', action:'index')}">
                        Advanced search
                    </a>
                </div>
            </div>
            </g:form>
        </div>
        <!-- results -->
        <g:render template="imageThumbnails"
                  model="${[images: images, totalImageCount: totalImageCount, allowSelection: isLoggedIn,
                            selectedImageMap: selectedImageMap]}" />
    </body>
</html>
