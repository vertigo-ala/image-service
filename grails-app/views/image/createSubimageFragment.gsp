<div class="form-horizontal">

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
        hideModal();
    });

    $("#btnCreateSubimage2").click(function(e) {
        e.preventDefault();
        var url = "${raw(createLink(controller:'webService', action:'createSubimage', id: imageInstance.imageIdentifier,  params:[x: x, y: y, width: width, height: height]))}";
        $.ajax(url).done(function(results) {
            if (results.success) {
                hideModal();
            } else {
                alert("Failed to create subimage: " + results.message);
            }
        });
    });

</script>