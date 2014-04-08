<style>

    .image-caption, .image-thumbnail {
        text-align: center;
    }

</style>

<ul class="thumbnails">
    <g:each in="${images}" var="image">
        <li class="span2">
            <div class="thumbnail" imageId="${image.id}">
                <g:if test="${allowSelection}">
                    <div class="">
                        <g:checkBox class="chkSelectImage" name="chkSelectImage${image.id}" checked="${selectedImageMap?.containsKey(image.imageIdentifier)}" />
                    </div>
                </g:if>
                <div class="image-thumbnail">
                    <a href="${createLink(controller:'image', action:'details', id: image.id)}">
                        <img src="<img:imageSquareThumbUrl imageId='${image.imageIdentifier}'/>" />
                    </a>
                </div>
                <div class="image-caption" style="padding: 2px">
                    &nbsp;
                    <div class="label pull-left image-info-button">
                        <i class="icon-info-sign icon-white"></i>
                    </div>
                    <div class="pull-right image-tags-button">
                        <i class="icon-tags"></i>
                    </div>
                </div>
            </div>
        </li>
    </g:each>
</ul>

<g:if test="${allowSelection}">
    <button id="btnSelectAllOnPage" class="btn btn-small">Select all on this page</button>
    <button id="btnDeselectAllOnPage" class="btn btn-small">Deselect all on this page</button>
</g:if>

<div class="pagination">
    <g:paginate total="${totalImageCount}" prev="" next="" params="${[q:params.q]}" />
</div>


<script>

    $(document).ready(function() {

        $(".chkSelectImage").change(function(e) {
            var imageId = $(this).closest("[imageId]").attr("imageId");
            if (imageId) {
                if ($(this).is(":checked")) {
                    $.ajax("${createLink(controller: 'selection', action:'ajaxSelectImage')}/" + imageId).done(function(results) {
                        updateSelectionContext();
                    });
                } else {
                    $.ajax("${createLink(controller: 'selection', action:'ajaxDeselectImage')}/" + imageId).done(function(results) {
                        updateSelectionContext();
                    });
                }
            }
        });

        $("#btnSelectAllOnPage").click(function(e) {
            e.preventDefault();
            selectAllOnPage();
        });

        $("#btnDeselectAllOnPage").click(function(e) {
            e.preventDefault();
            deselectAllOnPage();
        });


        $(".image-info-button").each(function() {
            var imageId = $(this).closest("[imageId]").attr("imageId");
            if (imageId) {
                $(this).qtip({
                    content: {
                        text: function(event, api) {
                            $.ajax("${createLink(controller:'image', action:"imageTooltipFragment")}/" + imageId).then(function(content) {
                                api.set("content.text", content);
                            },
                            function(xhr, status, error) {
                                api.set("content.text", status + ": " + error);
                            });
                        }
                    }
                });
            }
        });

        $(".image-tags-button").each(function() {
            var imageId = $(this).closest("[imageId]").attr("imageId");
            if (imageId) {
                $(this).qtip({
                    content: {
                        text: function(event, api) {
                            $.ajax("${createLink(controller:'image', action:"imageTagsTooltipFragment")}/" + imageId).then(function(content) {
                                    api.set("content.text", content);
                                },
                                function(xhr, status, error) {
                                    api.set("content.text", status + ": " + error);
                                });
                            }
                    }
                });
            }
        });

    });

    function selectAllOnPage() {
        $(".thumbnail[imageId]").each(function(e) {
            var imageId = $(this).attr("imageId");
            if (imageId) {
                var checkBox = $(this).find(".chkSelectImage");
                $.ajax("${createLink(controller: 'selection', action:'ajaxSelectImage')}/" + imageId).done(function (results) {
                    checkBox.prop('checked', true);
                    updateSelectionContext();
                });
            }
        });
    }

    function deselectAllOnPage() {
        $(".thumbnail[imageId]").each(function(e) {
            var imageId = $(this).attr("imageId");
            if (imageId) {
                var checkBox = $(this).find(".chkSelectImage");
                $.ajax("${createLink(controller: 'selection', action:'ajaxDeselectImage')}/" + imageId).done(function (results) {
                    checkBox.prop('checked', false);
                    updateSelectionContext();
                });
            }
        });
    }


</script>