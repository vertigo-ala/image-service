<div class="form-horizontal">
<div class="well well-small">
    <g:if test="${parentTag}">
            Enter a name for your new tag. It will be created under <strong>${parentTag.path}</strong>.
    </g:if>
    <g:else>
            Enter a new tag name. Tag hierarchy elements can be delimited with '/'.
    </g:else>
    </div>
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
        imgvwr.hideModal();
    });

    $("#btnAddTag").click(function(e) {
        e.preventDefault();
        var tagPath = $("#tag").val();
        if (tagPath) {
            $.ajax("${createLink(controller:'webService', action:'createTagByPath')}?tagPath=" + tagPath + "&parentTagId=${parentTag?.id}").done(function(results) {
                if (imgvwr.onTagCreated && results.tagId) {
                    imgvwr.onTagCreated(results.tagId);
                } else {
                    imgvwr.hideModal();
                }
            });
        }
    });

</script>