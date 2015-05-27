<%@ page import="au.org.ala.web.CASRoles" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="${grailsApplication.config.skin.layout}"/>
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

        <table style="width: 100%">
            <tr>
                <td><h4>Preview (first ${params.max ?: 10} images)</h4></td>
                <td>
                    <button style="margin-left: 8px" class="btn btn-primary pull-right" id="btnExportToFile"><i class="icon-file icon-white"></i>&nbsp;Export</button>
                    <button class="btn pull-right" id="btnAddColumn"><i class="icon-plus"></i>&nbsp;Add column</button>
                </td>
            </tr>
        </table>
        <table class="table table-condensed table-bordered table-striped">
            <thead>
                <tr>
                    <th>imageUrl</th>
                    <g:each in="${columnDefinitions}" var="column">
                        <th style="white-space: nowrap" columnDefId="${column.id}">
                            <a href="#" class="btnRemoveColumn" title="Remove column"><span class="icon-remove icon-grey"></span></a>&nbsp;
                            ${column.columnName}
                        </th>
                    </g:each>
                </tr>
            </thead>
            <tbody>
                <g:each in="${previewData}" var="image">
                    <tr>
                        <td>${image.imageUrl}</td>
                        <g:each in="${columnDefinitions}" var="column">
                            <td>
                                ${image[column.columnName]}
                            </td>
                        </g:each>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </body>
</html>

<r:script>

    $(document).ready(function() {

        $("#btnAddColumn").click(function(e) {
            e.preventDefault();
            var options = {
                url: "${createLink(controller: 'album', action:'addColumnDefinitionFragment', id: album.id)}",
                title: "Select column"
            };

            imgvwr.showModal(options);
        });

        $(".btnRemoveColumn").click(function(e) {
            e.preventDefault();
            var coldefid = $(this).closest("[columndefid]").attr("columndefid");
            if (coldefid) {
                window.location = "${createLink(action:'removeColumnDefinition', id: album.id)}?columndefid=" + coldefid
            }
        });

        $("#btnExportToFile").click(function(e) {
            e.preventDefault();
            window.location = "${createLink(action:'exportAsCSV', id: album.id)}";
        });
    });

</r:script>

