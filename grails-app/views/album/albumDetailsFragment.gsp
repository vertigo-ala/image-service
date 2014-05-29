<style>

    .image-tags-button {
        opacity: 0.5;
    }

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
<auth:ifAnyGranted roles="${au.org.ala.web.CASRoles.ROLE_ADMIN}">
    <%
        def adminButtons = []
        adminButtons << [id:'divider']
        adminButtons << [id:'btnGenerateTiles', label: 'Regenerate tiles']
        adminButtons << [id:'btnGenerateThumbnails', label: 'Regenerate thumbnails']
        adminButtons << [id:'divider']
        adminButtons << [id:'btnDeleteAlbumImages', label: 'Delete images']
        buttons.addAll(adminButtons)
    %>
</auth:ifAnyGranted>


<g:render template="/image/imageThumbnails" model="${[images: imageList, totalImageCount: albumImages.totalCount, allowSelection: true, thumbsTitle:"Album '${album.name}' (${albumImages.totalCount} images)", paginateActionId: album.id, footerTemplate:'imageThumbFooter', toolButtons: buttons, selectedImageMap: selectedImageMap]}" />

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

    $("#btnGenerateTiles").click(function(e) {
        e.preventDefault();
        $.ajax("${createLink(controller:'album', action:'ajaxScheduleTileGeneration', id:album.id)}").done(function(e) {
        });
    });

    $("#btnGenerateThumbnails").click(function(e) {
        e.preventDefault();
        $.ajax("${createLink(controller:'album', action:'ajaxScheduleThumbnailGeneration', id:album.id)}").done(function(e) {
        });
    });

    $("#btnDeleteAlbumImages").click(function(e) {
        e.preventDefault();
        var options = {
            message: "Are sure you wish to permanently delete the images in album '${album.name}'?",
            affirmativeAction: function() {
                window.location = "${createLink(controller:'album', action:'deleteAllImages', id: album.id)}";
            }
        };
        imglib.areYouSure(options);
    });

    $("#btnAddTag").click(function(e) {
        e.preventDefault();
        imglib.selectTag(function(tagId) {
            var url = "${createLink(controller:'album', action:"ajaxTagImages", id: album.id)}?tagId=" + tagId;
            imglib.pleaseWait("Adding tags to images...", url);
        });
    });

    $("#btnAddMetaData").click(function(e) {
        e.preventDefault();
        imglib.promptForMetadata(function(key, value) {
            var url = "${createLink(controller:'album', action:'ajaxAddMetaData', id: album.id)}?key=" + key + "&value=" + value;
            imglib.pleaseWait("Applying metadata to images...", url);
        });
    });

    imglib.bindImageTagTooltips();

</script>
