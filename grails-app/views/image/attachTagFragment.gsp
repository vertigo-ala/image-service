<div class="form-horizontal">

    <div class="control-group">
        <label class="control-label" for="search">Search</label>
        <div class="controls">
            <input type="text" id="search" placeholder="Find tags">
            <button id="btnSearchTags" class="btn"><i class="icon-search"></i>&nbsp;Search</button>
        </div>
    </div>


    <div class="well well-small">
        <div id="tagContainer"></div>
    </div>


    <div class="control-group">
        <div class="controls">
            <button class="btn btn-primary" id="btnAttachTagToImage">Attach tag</button>
            <button class="btn" id="btnCancelAttachTag">Cancel</button>
        </div>
    </div>
</div>
<style>
    #tagTree {
        height: 300px;
    }
</style>
<script>

    $("#search").keydown(function(e) {
        if (e.which == 13) {
            e.preventDefault();
            loadTagTree();
        }
    }).focus();

    $("#btnSearchTags").click(function(e) {
        e.preventDefault();
        loadTagTree();
    });

    $("#btnCancelAttachTag").click(function(e) {
        e.preventDefault();
        hideModal();
    });

    function attachSelectedTag() {
        var tagId = getSelectedTagId();
        if (tagId) {
            $.ajax("${createLink(controller:'webService', action:'attachTagToImage', id: imageInstance.imageIdentifier)}?tagId=" + tagId).done(function() {
                hideModal();
            });
        }
    }

    $("#btnAttachTagToImage").click(function(e) {
        e.preventDefault();
        attachSelectedTag();
    });

    function getSelectedTagId() {
        var tree = $("#tagTree");
        var selected = tree.jstree("get_selected");
        if (selected && selected.length > 0) {
            var selectedNode = tree.jstree("get_node", selected[0]);
            return selectedNode.original.tagId;
        }
        return null
    }

    function loadTagTree() {

        $("#tagContainer").html('<div id="tagTree"></div>');

        var q = $("#search").val();

        $.ajax("${createLink(controller:'webService', action:'getTagModel')}?q=" + q).done(function(rootNodes) {

            var tree = $("#tagTree");

            tree.on("ready.jstree", function (event, data) {
                tree.jstree("open_all");
            }).on("dblclick.jstree", function() {
                attachSelectedTag();
            }).jstree({
                "plugins" : [],
                "core" : {
                    "animation" : 100,
                    data: rootNodes,
                    "themes": {
                        dots: false
                    },
                    multiple: false
                }
            });

        });
    }

    loadTagTree();


</script>