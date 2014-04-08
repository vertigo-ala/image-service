<!doctype html>
<html>
<head>
    <meta name="layout" content="adminLayout"/>
    <title>ALA Images - Admin - Tools</title>
    <style type="text/css" media="screen">
    </style>
</head>

<body>
    <content tag="pageTitle">Tools</content>
    <content tag="adminButtonBar" />

    <table class="table">
        <tr>
            <td>
                <button id="btnImportFromLocalInbox" class="btn">Import images from local incoming directory</button>
            </td>
            <td>
                Imports image files from the designated incoming server directory ("${grailsApplication.config.imageservice.imagestore.inbox}")
            </td>
        </tr>
        <tr>
            <td>
                <button id="btnRegenArtifacts" class="btn">Regenerate Image Artifacts</button>
            </td>
            <td>
                Regenerate all tiles and thumbnails for all images in the repository. Progress can be tracked on the dashboard.
            </td>
        </tr>
        <tr>
            <td>
                <button id="btnRegenThumbnails" class="btn">Regenerate Image Thumbnails</button>
            </td>
            <td>
                Regenerate just thumbnails for all images in the repository. Progress can be tracked on the dashboard.
            </td>
        </tr>

        <tr>
            <td>
                <button id="btnRebuildKeywords" class="btn">Rebuild Keywords</button>
            </td>
            <td>
                Rebuild the synthetic keywords based on image tags (used for fast searching)
            </td>
        </tr>

    </table>
</body>
<r:script>

    $(document).ready(function() {

        $("#btnRegenArtifacts").click(function(e) {
            e.preventDefault();
            $.ajax("${createLink(controller:'webService', action:'scheduleArtifactGeneration')}").done(function() {
            });
        });

        $("#btnRegenThumbnails").click(function(e) {
            e.preventDefault();
            $.ajax("${createLink(controller:'webService', action:'scheduleThumbnailGeneration')}").done(function() {
            });
        });

        $("#btnRebuildKeywords").click(function(e) {
            e.preventDefault();
            $.ajax("${createLink(controller:'webService', action:'scheduleKeywordRegeneration')}").done(function() {
            });
        });

        $("#btnImportFromLocalInbox").click(function(e) {
            e.preventDefault();
            window.location = "${createLink(action:'localIngest')}";
        });

    });

</r:script>
</html>
