<style>

    .btnRemoveFromAlbum {
        opacity: 0.5;
        border: 1px solid white;
        border-radius: 3px;
    }

    .btnRemoveFromAlbum:hover {
        border: 1px solid darkgray;
    }

    .thumbFooter {
        width: 100%;
        text-align: right;
    }

</style>

<g:set var="buttons" value="${[[id:'btnExport', label:'Export links'],[id:'divider'],[id:'btnAddTag', label:'Tag images'], [id:'btnAddMetaData', label:'Attach meta data']]}" />

<g:render template="/image/imageThumbnails" model="${[images: imageList, totalImageCount: albumImages.totalCount, allowSelection: false, thumbsTitle:"Album '${album.name}' (${albumImages.totalCount} images)", paginateActionId: album.id, footerTemplate:'imageThumbFooter', toolButtons: buttons ]}" />

<script>

    $(".btnRemoveFromAlbum").click(function(e) {
        e.preventDefault();
        var imageId = $(this).closest("[imageId").attr("imageId");
        if (imageId) {
            $.ajax("${createLink(action:'ajaxRemoveImageFromAlbum', id: album.id)}?imageId=" + imageId).done(function() {
                updateAlbumDetails("${createLink(action:'albumDetailsFragment', id: album.id)}");
            });
        }
    });

</script>
