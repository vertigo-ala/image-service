<div>
    <div style="display: none" id="selectAlbumErrorDiv" class="alert alert-danger"></div>
    <div class="form-horizontal">
        <div class="control-group">
            <label class="control-label" for="album">Select an album</label>
            <div class="controls">
                <g:select name="album" from="${albums}" optionValue="name" optionKey="id" />
                <button class="btn btn-primary" id="btnSelectAlbum">Select album</button>
            </div>
        </div>
        <div class="control-group">
            OR
        </div>
        <div class="control-group">
            <label class="control-label" for="newAlbumName">Create a new album</label>
            <div class="controls">
                <g:textField name="newAlbumName" placeholder="New album name" />
                <button class="btn" id="btnCreateNewAlbum">Create</button>
            </div>
        </div>

        <div class="control-group">
            <div class="controls">
                <button class="btn" id="btnCancelSelectAlbum">Cancel</button>
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
        selectAlbumClearError();
        var albumId = $("#album").val();
        if (albumId && imglib.onAlbumSelected) {
            imglib.onAlbumSelected(albumId);
        }
    });

    $("#btnCreateNewAlbum").click(function(e) {
        e.preventDefault();
        selectAlbumClearError();
        var albumName = $("#newAlbumName").val();
        if (!albumName) {
            selectAlbumError("You must enter an album name!");
            return
        }

        $.ajax("${createLink(controller:'album', action:'ajaxCreateNewAlbum')}?albumName=" + albumName).done(function(results) {
            if (!results.success) {
                selectAlbumError(results.message);
            } else {
                var albumId = results.albumId
                if (albumId && imglib.onAlbumSelected) {
                    imglib.onAlbumSelected(albumId);
                }
            }
        });
    });

    function selectAlbumClearError() {
        var s = $("#selectAlbumErrorDiv");
        s.html("");
        s.css('display', 'none');
    }

    function selectAlbumError(message) {
        var s = $("#selectAlbumErrorDiv");
        s.html(message);
        s.css("display", "block");
    }

</script>