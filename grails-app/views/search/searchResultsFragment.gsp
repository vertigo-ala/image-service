<div class="">
    <%
        def toolButtons = []
        toolButtons << [label: "Select all matching images (${imageList?.totalCount})", id:"btnSelectAllImages"]
        toolButtons << [label: 'Clear selection', id:"btnDeselectAll"]
    %>
    <g:render template="/image/imageThumbnails" model="${[images: imageList, totalImageCount: imageList.totalCount, allowSelection: true, selectedImageMap: selectedImageMap, thumbsTitle: "${imageList.totalCount} matching images", toolButtons: toolButtons]}" />
</div>
<script>

    $("#btnDeselectAll").click(function(e) {
        e.preventDefault();
        $.ajax("${createLink(controller: 'selection', action: 'clearSelection')}").done(function() {
            location.reload(true);
        });
    });

    $("#btnSelectAllImages").click(function(e) {
        e.preventDefault();
        $.ajax("${createLink(controller: 'search', action: 'ajaxSelectAllCurrentQuery')}").done(function() {
            location.reload(true);
        });
    });

</script>