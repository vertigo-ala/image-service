<div class="form-horizontal">
    <div class="control-group">
        <label class="control-label" for="keyword">Keyword</label>
        <div class="controls">
            <input type="text" id="keyword" placeholder="Keyword">
        </div>
    </div>

    <div class="control-group">
        <div class="controls">
            <button class="btn btn-primary" id="btnAddNewKeyword">Add Keyword</button>
            <button class="btn" id="btnCancelAddKeyword">Cancel</button>
        </div>
    </div>
</div>
<script>

    $("#btnCancelAddKeyword").click(function(e) {
        e.preventDefault();
        hideModal();
    });

    $("#btnAddNewKeyword").click(function(e) {
        e.preventDefault();
        var keyword = $("#keyword").val();
        if (keyword) {
            $.ajax("${createLink(controller:'webService', action:'addKeyword', id: imageInstance.imageIdentifier)}?keyword=" + keyword).done(function() {
                hideModal();
            });
        }
    });

</script>