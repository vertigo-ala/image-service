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
<g:render template="/image/imageThumbnails" model="${[images: imageList, totalImageCount: albumImages.totalCount, allowSelection: false, thumbsTitle:"Album '${album.name}' (${albumImages.totalCount} images)", paginateActionId: album.id, footerTemplate:'imageThumbFooter' ]}" />

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
