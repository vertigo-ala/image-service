<!doctype html>
<html>
    <head>
        <meta name="layout" content="adminLayout"/>
        <meta name="section" content="home"/>
        <title>Upload images</title>
        <meta name="breadcrumbs" content="${g.createLink( controller: 'image', action: 'list')}, Images"/>
        <asset:stylesheet src="ala/images-client.css" />
    </head>
    <body>

        <div class="container">
            <h1>Manual image upload</h1>

            <div class="row">
                <div class="col-md-12 alert">
                    <button type="button" class="btn btn-default" id="btnUploadFromCSV">Upload from CSV</button>
                </div>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <div class="well">
                        <g:form action="storeImage" controller="image" method="post" enctype="multipart/form-data">
                            <input type="file" name="image" />
                            <g:submitButton class="btn btn-small btn-primary" name="Upload"/>
                        </g:form>
                    </div>
                </div>
            </div>
            <script>
                $(document).ready(function() {
                    $("#btnUploadFromCSV").click(function(e) {
                        var opts = {
                            url:"${createLink(controller:'imageClient', action:'uploadFromCSVFragment')}",
                            title:"Upload images from CSV"
                        };
                        imgvwr.showModal(opts);
                    });
                });
            </script>
        </div>
    </body>
</html>

