<div>
    <div class="form-horizontal">
        <div class="control-group">
            <label class="control-label" for="album">Select an album</label>
            <div class="controls">
                <g:select name="album" from="${albums}" optionValue="name" optionKey="id" />
            </div>
        </div>

        <div class="control-group">
            <div class="controls">
                <button class="btn" id="btnCancelSelectAlbum">Cancel</button>
                <button class="btn btn-primary" id="btnSelectAlbum">Select album</button>
            </div>
        </div>
    </div>
</div>

<script>

    $("#btnCancelSelectAlbum").click(function(e) {
        e.preventDefault();
        imglib.hideModal();
    });

    $("#btnSelectAlbum").click(function(e) {
        e.preventDefault();
        var albumId = $("#album").val();
        if (albumId && imglib.onAlbumSelected) {
            imglib.onAlbumSelected(albumId);
            imglib.hideModal();
        }
    });

</script>