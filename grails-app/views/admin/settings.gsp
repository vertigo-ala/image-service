<!doctype html>
<html>
<head>
    <meta name="layout" content="adminLayout"/>
    <title>ALA Images - Admin - Settings</title>
    <style type="text/css" media="screen">
    </style>
    <r:require module="bootstrap-switch" />
</head>

<body>
    <content tag="pageTitle">Settings</content>
    <content tag="adminButtonBar" />
    <table class="table table-bordered table-striped">
        <g:each in="${settings}" var="setting">
            <tr>
                <td>
                    <small>
                        ${setting.name}
                    </small>
                </td>
                <td>
                    ${setting.description}
                </td>
                <td>
                    <g:if test="${setting.type == au.org.ala.images.SettingType.Boolean}">
                        <g:checkBox name="${setting.name}" data-size="small" checked="${Boolean.parseBoolean(setting.value)}" />
                    </g:if>
                    <g:else>
                        ${setting.value}
                    </g:else>
                </td>
            </tr>
        </g:each>
    </table>
</div>
</body>
<r:script>

    $(document).ready(function() {
        $('input:checkbox').bootstrapSwitch();

        $('input:checkbox').on('switchChange.bootstrapSwitch', function(event, state) {
            var name = $(this).attr("name");
            if (name) {
                window.location = "${createLink(controller:'admin', action:'setSettingValue')}?name=" + encodeURIComponent(name) + "&value=" + encodeURIComponent(state);
            }
        });
    });

</r:script>
</html>
