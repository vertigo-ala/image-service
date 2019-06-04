<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Images</title>
        <asset:stylesheet src="application.css" />
    </head>
    <body class="fluid">

        <g:if test="${flash.message}">
            <div class="alert alert-success" style="display: block">${flash.message}</div>
        </g:if>
        <g:if test="${flash.errorMessage}">
            <div class="alert alert-danger" style="display: block">${flash.errorMessage}</div>
        </g:if>

        <div class="row">
            <div class="col-md-3">
                <h1><g:formatNumber number="${totalImageCount}" format="###,###,###" /> Images</h1>
            </div>
            <!-- search box -->
            <div class="search col-md-9" style="margin-bottom:20px;">
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
        </div>

        <!-- results -->
        <g:render template="imageThumbnails"
                  model="${[images: images, facets: facets, totalImageCount: totalImageCount, allowSelection: isLoggedIn,
                            selectedImageMap: selectedImageMap]}" />
    </body>
</html>
