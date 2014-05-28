<div>
    <p>
    ${message}
    </p>
    <div class="form-horizontal">
        <div class="control-group">
            <button id="btnNo" class="btn">${negativeText}</button>
            <button id="btnYes" class="btn">${affirmativeText}</button>
        </div>
    </div>
</div>
<script>

    $("#btnYes").click(function(e) {
        e.preventDefault();
        if (imglib.areYouSureOptions && imglib.areYouSureOptions.affirmativeAction) {
            imglib.areYouSureOptions.affirmativeAction();
        }
        imglib.hideModal();
    });

    $("#btnNo").click(function(e) {
        e.preventDefault();
        if (imglib.areYouSureOptions && imglib.areYouSureOptions.negativeAction) {
            imglib.areYouSureOptions.negativeAction();
        }
        imglib.hideModal();
    });

</script>