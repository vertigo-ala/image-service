<div class="form-horizontal">
    <g:if test="${parentTag}">
        <div class="alert alert-info">
            Enter a name for your new tag. It will be created under <strong>${parentTag.path}</strong>.
        </div>
    </g:if>
    <g:else>
        <div class="alert alert-info">
            Enter a name for your new tag.
        </div>
    </g:else>
    <div class="control-group">
        <label class="control-label" for="tag">Tag name</label>
        <div class="controls">
            <input type="text" id="tag" placeholder="<new tag>">
        </div>
    </div>

    <div class="control-group">
        <div class="controls">
            <button class="btn btn-primary" id="btnAddTag">Create Tag</button>
            <button class="btn" id="btnCancelAddTag">Cancel</button>
        </div>
    </div>
</div>
<script>

    $("#btnCancelAddTag").click(function(e) {
        e.preventDefault();
        hideModal();
    });

    $("#btnAddTag").click(function(e) {
        e.preventDefault();
        var tagPath = $("#tag").val();
        if (tagPath) {
            $.ajax("${createLink(controller:'webService', action:'createTagByPath')}?tagPath=" + tagPath + "&parentTagId=${parentTag?.id}").done(function() {
                hideModal();
            });
        }
    });

</script>