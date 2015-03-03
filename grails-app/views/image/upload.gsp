<!doctype html>
<html>
    <head>
        <meta name="layout" content="${grailsApplication.config.skin.layout}"/>
        <meta name="section" content="home"/>
        <title>ALA Image Service - Home</title>
    </head>

    <body class="content">

        <img:headerContent title="Upload an image">
            <%
                pageScope.crumbs = [
                ]
            %>
        </img:headerContent>

        <div class="row-fluid">
            <div class="span12 alert">

                <button type="button" class="btn" id="btnUploadFromCSV">Upload from CSV</button>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span12">
                <div class="well">
                    <g:form action="storeImage" controller="image" method="post" enctype="multipart/form-data">
                        <input type="file" name="image" />
                        <g:submitButton class="btn btn-small btn-primary" name="Upload"/>
                    </g:form>
                </div>
            </div>
        </div>
        <r:script>

            $(document).ready(function() {
                $("#btnUploadFromCSV").click(function(e) {
                    var opts = {
                        url:"${createLink(controller:'imageClient', action:'uploadFromCSVFragment')}",
                        title:"Upload images from CSV"
                    };

                    imglib.showModal(opts);
                });
            });

        </r:script>
    </body>
</html>

