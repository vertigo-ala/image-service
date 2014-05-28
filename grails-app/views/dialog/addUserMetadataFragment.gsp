<%@ page import="au.org.ala.images.MetaDataSourceType" %>
<div class="form-horizontal">

    <div class="control-group">
        <label class="control-label" for="metaDataKey">Name</label>
        <div class="controls">
            <input type="text" id="metaDataKey" placeholder="Metadata key">
        </div>
    </div>

    <div class="control-group">
        <label class="control-label" for="metaDataValue">Value</label>
        <div class="controls">
            <input type="text" id="metaDataValue" placeholder="Value">
        </div>
    </div>

    <div class="control-group">
        <div class="controls">
            <button class="btn btn-primary" id="btnAddNewUserMetadata">Add</button>
            <button class="btn" id="btnCancelAddUserMetaData">Cancel</button>
        </div>
    </div>

</div>
<script>

    $("#btnCancelAddUserMetaData").click(function(e) {
        e.preventDefault();
        imglib.hideModal();
    });

    $("#btnAddNewUserMetadata").click(function(e) {
        e.preventDefault();
        var key = $("#metaDataKey").val();
        var value = $("#metaDataValue").val();
        if (key && value) {
            key = encodeURIComponent(key);
            value = encodeURIComponent(value);
            if (imglib.onAddMetadata) {
                imglib.onAddMetadata(key, value);
            }
        }
    });

</script>