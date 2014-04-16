<div>
    <g:form action="addSelectionToAlbum" class="form-horizontal">
        <div class="control-group">
            <label class="control-label" for="album">Select an album</label>
            <div class="controls">
                <g:select name="album" from="${albums}" optionValue="name" optionKey="id" />
            </div>
        </div>

        <div class="control-group">
            <div class="controls">
                <button class="btn" id="btnCancelAddToAlbum">Cancel</button>
                <button class="btn btn-primary" id="btnAddToAlbum">Add to album</button>
            </div>
        </div>

    </g:form>
</div>