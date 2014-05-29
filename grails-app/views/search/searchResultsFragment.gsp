<div class="">
    <%
        def toolButtons = []
        toolButtons << [label: "Select all matching images (${imageList?.totalCount})", id:"btnSelectAllImages"]
    %>
    <g:render template="/image/imageThumbnails" model="${[images: imageList, totalImageCount: imageList.totalCount, allowSelection: true, selectedImageMap: selectedImageMap, thumbsTitle: "${imageList.totalCount} matching images", toolButtons: toolButtons]}" />
</div>
<script>

    $("#btnSelectAllImages").click(function(e) {
        e.preventDefault();
        $.ajax("${createLink(controller: 'search', action: 'ajaxSelectAllCurrentQuery')}").done(function() {
            location.reload(true);
        });
    });

</script>