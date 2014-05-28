<style>

.image-tag button.close {
    line-height: 16px;
    font-size: 14px;
}

</style>
<table style="width: 100%">
    <tr>
        <td>
            <h4>Tags</h4>
        </td>
        <td>
            <button id="btnAttachTag" class="btn btn-small btn-success pull-right"><i class="icon-plus icon-white"></i>&nbsp;Add tag</button>
        </td>
    </tr>
</table>

<g:each in="${tags}" var="tag">
    <div class="badge image-tag" style="white-space: pre-wrap">${tag.label}&nbsp;&nbsp;<button type="button" class="close btnDetachTag" tagId="${tag.id}">&times;</button></div>
</g:each>

<script>

    $(document).ready(function() {
        $("#btnAttachTag").click(function(e) {
            e.preventDefault();

            imglib.selectTag(function(tagId) {
                if (tagId) {
                    $.ajax("${createLink(controller:'webService', action:'attachTagToImage', id: imageInstance.imageIdentifier)}?tagId=" + tagId).done(function() {
                        if (loadTags) {
                            loadTags();
                        }
                    });
                }
            });
        });

        $(".btnDetachTag").click(function(e) {
            e.preventDefault();
            var tagId = $(this).attr("tagId");
            $.ajax("${createLink(controller:'webService', action:'detachTagFromImage', id: imageInstance.imageIdentifier)}?tagId=" + tagId).done(function() {
                if (loadTags) {
                    loadTags();
                }
            });
        });

    });
</script>
