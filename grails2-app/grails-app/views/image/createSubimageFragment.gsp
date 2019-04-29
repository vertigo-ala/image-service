<div class="form-horizontal">

    <form>
        <label for="subImageTitle">
            Description
        </label>
        <input id="description" type="text" class="input-xlarge" name="description" value=""/>
    </form>

    <div class="control-group">
        <div class="controls">
            <btn class="btn" id="btnCancelSubimage">Cancel</btn>
            <btn class="btn btn-primary" id="btnCreateSubimage2">Create subimage</btn>
        </div>
    </div>
</div>

<script>

    $("#btnCancelSubimage").click(function(e) {
        e.preventDefault();
        imgvwr.hideModal();
    });

    $("#btnCreateSubimage2").click(function(e) {
        e.preventDefault();
        var url = "${grailsApplication.config.serverName}${raw(createLink(controller:'webService', action:'createSubimage', id: imageInstance.imageIdentifier,  params:[x: x, y: y, width: width, height: height]))}&description=" + encodeURIComponent($('#description').val());
        $.ajax(url).done(function(results) {
            if (results.success) {
                imgvwr.hideModal();
            } else {
                alert("Failed to create subimage: " + results.message);
            }
        });
    });

</script>