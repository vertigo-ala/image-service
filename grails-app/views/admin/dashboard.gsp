<!doctype html>
<html>
    <head>
        <meta name="layout" content="adminLayout"/>
        <title>ALA Images - Admin - Dashboard</title>
        <style type="text/css" media="screen">
        </style>
    </head>

    <body>
        <content tag="pageTitle">Dashboard</content>
        <content tag="adminButtonBar" />
        <div class="well well-small">
            <h4>Statistics</h4>
            <table class="table table-striped">
                <tr>
                    <td>Image count</td>
                    <td><span id="statImageCount"></span></td>
                </tr>
            </table>
        </div>
        <div class="well well-small">
            <h4>Background processing</h4>
            <table class="table">
                <tr>
                    <td>
                        Import/Thumbnailing queue size
                    </td>
                    <td>
                        <span id="statQueueSize"></span>
                    </td>
                </tr>
                <tr>
                    <td>
                        Tiling queue size
                    </td>
                    <td>
                        <span id="tilingQueueSize"></span>
                    </td>
                </tr>

            </table>
        </div>
    </body>
    <r:script>

    $(document).ready(function() {
        $.ajax("${createLink(controller:'webService', action:'getRepositoryStatistics')}").done(function(data) {
            $("#statImageCount").html(data.imageCount);
        });

        updateQueueLength();

        setInterval(updateQueueLength, 10000);
    });

    function updateQueueLength() {
        $.ajax("${createLink(controller:'webService', action:'getBackgroundQueueStats')}").done(function(data) {
            $("#statQueueSize").html(data.queueLength);
            $("#tilingQueueSize").html(data.tilingQueueLength);
        });
    }

    </r:script>
</html>
