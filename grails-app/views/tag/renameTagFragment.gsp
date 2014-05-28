<div class="form-horizontal">
    <div class="control-group">
        <label class="control-label" for="tag">Current name</label>
        <div class="controls">
            <input type="text" readonly="true" id="existing" value="${tagInstance.label}">
        </div>
    </div>

    <div class="control-group">
        <label class="control-label" for="tag">New name</label>
        <div class="controls">
            <input type="text" id="tag" placeholder="${tagInstance.label}" value="${tagInstance.label}">
        </div>
    </div>

    <div class="control-group">
        <div class="controls">
            <button class="btn btn-primary" id="btnRenameTag">Rename Tag</button>
            <button class="btn" id="btnCancelRenameTag">Cancel</button>
        </div>
    </div>
</div>
<script>


    $("input:text").focus(function() {
        $(this).select();
    });


    $("#btnCancelRenameTag").click(function(e) {
        e.preventDefault();
        imglib.hideModal();
    });

    $("#btnRenameTag").click(function(e) {
        e.preventDefault();
        var newSuffix = $("#tag").val();
        if (newSuffix) {
            $.ajax("${createLink(controller:'webService', action:'renameTag')}?tagId=${tagInstance.id}&name=" + newSuffix).done(function() {
                imglib.hideModal();
            });
        }
    });

</script>