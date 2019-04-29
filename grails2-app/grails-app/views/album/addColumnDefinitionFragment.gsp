<g:form class="form-horizontal" action="addColumnDefinition">
    <g:hiddenField name="id" value="${albumId}" />
    <div class="control-group">
        <label for="columndef">Column</label>
        <div class="controls">
            <g:select name="columndef" from="${columnDefinitions}" value="" />
        </div>
    </div>
    <div class="control-group">
        <div class="controls">
            <button class="btn" id="btnCancelAddColumn">Cancel</button>
            <button class="btn btn-primary" type="submit">Add column</button>
        </div>
    </div>
</g:form>