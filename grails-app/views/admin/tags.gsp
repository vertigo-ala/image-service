<!doctype html>
<html>
    <head>
        <meta name="layout" content="adminLayout"/>
        <meta name="section" content="home"/>
        <title>ALA Images - Admin - Tags</title>

        <r:require module="bootstrap" />
        <r:require module="jstree" />
    </head>

    <body class="content">

        <style>
            #searchTags {
                margin-bottom: 0 !important;
            }
        </style>

        <content tag="pageTitle">Tags</content>
        <content tag="adminButtonBar" />

        <div class="row-fluid" style="margin-bottom: 10px">
            <div class="span12">
                <button class="btn btn-success" id="btnCreateNewTag"><i class="icon-plus icon-white"></i>&nbsp;Add</button>
                <button class="btn" id="btnRenameSelectedTag">Rename</button>
                <button class="btn btn-danger" id="btnDeleteSelectedTag"><i class="icon-remove icon-white"></i>&nbsp;Delete</button>
                <button class="btn pull-right" id="btnUploadTags"><i class="icon-upload"></i>&nbsp;Upload tags from CSV file</button>

                <input type="text" id="searchTags" placeholder="Find tags">
                <button id="btnSearchTags" class="btn"><i class="icon-search"></i>&nbsp;Search</button>


            </div>
        </div>

        <div class="row-fluid">
            <div class="span12">
                <div id="tagContainer" class="well well-small">
                    <img:spinner />
                </div>
            </div>
        </div>
    </body>
</html>

<r:script>

    $(document).ready(function() {

        $("#btnCreateNewTag").click(function(e) {
            e.preventDefault();
            createTag();
        });

        $("#btnRenameSelectedTag").click(function(e) {
            e.preventDefault();
            renameSelectedTag();
        });

        $("#btnDeleteSelectedTag").click(function(e) {
            e.preventDefault();
            deleteSelectedTag();
        });

        $("#btnUploadTags").click(function(e) {
            e.preventDefault();
            var options = {
                title:"Select a file to upload",
                url: "${createLink(action:'uploadTagsFragment')}"
            };
            imgvwr.showModal(options);
        });

        $("#searchTags").keydown(function(e) {
            if (e.which == 13) {
                e.preventDefault();
                loadTagTree();
            }
        }).focus();

        $("#btnSearchTags").click(function(e) {
            e.preventDefault();
            loadTagTree();
        });


        loadTagTree();

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

    function deleteSelectedTag() {
        var tagId = getSelectedTagId();
        if (tagId) {
            var opts = {
                title:"Delete tag",
                url: "${createLink(controller:'tag', action:'deleteTagFragment')}?tagId=" +tagId,
                onClose: function() {
                    loadTagTree();
                }
            }
            imgvwr.showModal(opts);
        }
    }

    function renameSelectedTag() {
        var tagId = getSelectedTagId();
        if (tagId) {
            var opts = {
                title:"Rename tag",
                url: "${createLink(controller:'tag', action:'renameTagFragment')}?tagId=" +tagId,
                onClose: function() {
                    loadTagTree();
                }
            }
            imgvwr.showModal(opts);
        }
    }

    function createTag() {
        var parentTagId = getSelectedTagId();
        var opts = {
            title:"Create tag",
            url: "${createLink(controller:'tag', action:'createTagFragment')}?parentTagId=" + parentTagId,
            onClose: function() {
                loadTagTree();
            }
        }
        imgvwr.showModal(opts);
    }

    function loadTagTree() {

        $("#tagContainer").html('<div id="tagTree"></div>');

        var q = $("#searchTags").val();

        $.ajax("${createLink(controller:'webService', action:'getTagModel')}?q=" + q).done(function(rootNodes) {

            var tree = $("#tagTree");

            tree.on("ready.jstree", function (event, data) {
                tree.jstree("open_all");
            }).on("move_node.jstree", function(e, data) {

                var newParentTagId = -1;
                if (data.parent) {
                    var parentNode = tree.jstree("get_node", data.parent);
                    if (parentNode && parentNode.original) {
                        newParentTagId = parentNode.original.tagId;
                    }
                }

                var targetTagId = data.node.original.tagId;
                moveTag(targetTagId, newParentTagId);
            }).jstree({
                "plugins" : ["dnd", "checkbox"],
                "core" : {
                    "animation" : 100,
                    data: rootNodes,
                    "themes": {
                        dots: false
                    },
                    "check_callback" : true,
                    multiple: false
                },
                "checkbox" : {
                    three_state: false,
                    whole_node: false
                },
                "dnd" : {
                    check_while_dragging: false
                }
            });

        });
    }

    function moveTag(targetTagId, newParentTagId) {
        var url = "${createLink(controller:'webService', action:'moveTag')}?targetTagId=" + targetTagId + "&newParentTagId=" + newParentTagId;
        $.ajax(url).done(function() {
            loadTagTree();
        });

    }

</r:script>


