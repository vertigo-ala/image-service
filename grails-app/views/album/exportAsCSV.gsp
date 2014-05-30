<%@ page import="au.org.ala.web.CASRoles" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>ALA Image Service - Album export csv</title>
        <style>
        </style>
    </head>

    <body class="content">

        <img:headerContent title="Export Album as CSV '${album.name}'">
            <%
                pageScope.crumbs = [
                    [link:createLink(controller: 'album', action:'index'), label: 'My albums']
                ]
            %>
        </img:headerContent>

        <h4>Preview (first 10 images)</h4>
        <table class="table table-condensed table-bordered table-striped">
            <thead>
                <tr>
                    <td>imageUrl</td>
                </tr>
            </thead>
            <tbody>
                <g:each in="${previewData}" var="image">
                    <tr>
                        <td>${image.imageUrl}</td>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </body>
</html>

<r:script>

    $(document).ready(function() {
    });

</r:script>

