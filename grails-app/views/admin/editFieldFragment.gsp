<%@ page import="au.org.ala.images.ImportFieldType" %>
<div class="form-horizontal">

    <div class="control-group">
        <label class="control-label" for="fieldName">Field name</label>
        <div class="controls">
            <g:textField name="fieldName" id="fieldName" value="${fieldDefinition?.fieldName}"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label" for="fieldType">Field type</label>
        <div class="controls">
            <g:select name="fieldType" id="fieldType" from="${ImportFieldType.values()}" value="${fieldDefinition?.fieldType}" />
        </div>
    </div>

    <div class="control-group">
        <label class="control-label" for="value">Value</label>
        <div class="controls">
            <g:textField name="value" id="value" value="${fieldDefinition?.value}" />
        </div>
    </div>

    <div class="control-group">
        <div class="controls">
            <button class="btn" id="btnCancel">Cancel</button>
            <button class="btn btn-primary" id="btnCreateNewField">${fieldDefinition ? "Save" : "Add"} Field</button>
        </div>
    </div>

    <script>

        $("#btnCancel").click(function(e) {
            e.preventDefault();
            imgvwr.hideModal();
        });

        $("#btnCreateNewField").click(function(e) {
            e.preventDefault();
            var name = encodeURIComponent($("#fieldName").val());
            var type = encodeURIComponent($("#fieldType").val());
            var value = encodeURIComponent($("#value").val());
            if (name && type && value) {
                $.ajax("${createLink(action:'saveFieldDefinition')}?name=" + name + "&type=" + type + "&value=" + value).done(function(results) {
                    if (!results.success) {
                        alert(results.message);
                    } else {
                        imgvwr.hideModal();
                    }
                });
            }

        });

    </script>

</div>