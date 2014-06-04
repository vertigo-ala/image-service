<%@ page import="au.org.ala.web.CASRoles" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>ALA Image Service - Image details</title>
        <style>

            .property-value {
                font-weight: bold;
            }

            .audiojs {
                width: 100%;
            }

        </style>
        <r:require module="bootstrap" />
        <r:require module="jstree" />
        <r:require module="audiojs" />
    </head>

    <body class="content">
        <img:headerContent title="Image details ${imageInstance?.originalFilename ?: imageInstance?.id}">
            <%
                pageScope.crumbs = [
                ]
            %>
        </img:headerContent>
        <div class="row-fluid">
            <div class="span4">
                <div id="image-thumbnail">
                    <ul class="thumbnails">
                        <li class="span12">
                            <div class="thumbnail" style="text-align: center">
                                <g:if test="${isImage}">
                                <a href="${createLink(action:'view', id:imageInstance.id)}">
                                    <img src="<img:imageThumbUrl imageId="${imageInstance?.imageIdentifier}"/>" />
                                </a>
                                </g:if>
                                <g:if test="${imageInstance.mimeType?.toLowerCase()?.startsWith("audio/")}">
                                    %{--// <img src="<img:imageThumbUrl imageId="${imageInstance?.imageIdentifier}"/>" />--}%
                                    <audio src="<img:imageUrl imageId="${imageInstance.imageIdentifier}" />"></audio>
                                </g:if>
                            </div>
                        </li>
                    </ul>
                </div>
                <div class="well well-small">
                    <div id="tagsSection">
                        Loading&nbsp;<img:spinner />
                    </div>
                </div>
                <g:if test="${isImage}">
                <div class="well well-small">
                    <h4>Square thumbnails</h4>
                    <g:each in="${squareThumbs}" var="thumbUrl">
                        <image class="img-polaroid" src="${thumbUrl}" height="80px" width="50px" title="${thumbUrl}" style="margin: 5px"></image>
                    </g:each>
                </div>
                </g:if>
            </div>
            <div class="span8">
                <div class="well well-small">
                    <div class="tabbable">
                        <ul class="nav nav-tabs">
                            <li class="active">
                                <a href="#tabProperties" data-toggle="tab">Image properties</a>
                            </li>
                            <li>
                                <a href="#tabExif" data-toggle="tab">Embedded</a>
                            </li>
                            <li>
                                <a href="#tabUserDefined" data-toggle="tab">User Defined Metadata</a>
                            </li>
                            <li>
                                <a href="#tabSystem" data-toggle="tab">System</a>
                            </li>
                            <auth:ifAnyGranted roles="${CASRoles.ROLE_ADMIN}">
                                <li>
                                    <a href="#tabAuditMessages" data-toggle="tab">Audit trail</a>
                                </li>
                            </auth:ifAnyGranted>
                        </ul>

                        <div class="tab-content">
                            <div class="tab-pane active" id="tabProperties">
                                <table class="table table-bordered table-condensed table-striped">
                                    <tr>
                                        <td class="property-name">Filename</td>
                                        <td class="property-value">${imageInstance.originalFilename}</td>
                                    </tr>
                                    <g:if test="${imageInstance.parent}">
                                        <tr>
                                            <td>Parent image</td>
                                            <td imageId="${imageInstance.parent.id}">
                                                <g:link controller="image" action="details" id="${imageInstance.parent.id}">${imageInstance.parent.originalFilename ?: imageInstance.parent.id}</g:link>
                                                <i class="icon-info-sign image-info-button"></i>
                                            </td>
                                        </tr>
                                    </g:if>
                                    <tr>
                                        <td class="property-name">Dimensions (w x h)</td>
                                        <td class="property-value">${imageInstance.width} x ${imageInstance.height}</td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">File size</td>
                                        <td class="property-value"><img:sizeInBytes size="${imageInstance.fileSize}" /></td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">Date uploaded</td>
                                        <td class="property-value"><img:formatDateTime date="${imageInstance.dateUploaded}" /></td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">Uploaded by</td>
                                        <td class="property-value"><img:userDisplayName userId="${imageInstance.uploader}" /></td>
                                    </tr>
                                    <g:if test="${imageInstance.dateTaken}">
                                        <tr>
                                            <td class="property-name">Date taken/created</td>
                                            <td class="property-value"><img:formatDateTime date="${imageInstance.dateTaken}" /></td>
                                        </tr>
                                    </g:if>
                                    <tr>
                                        <td class="property-name">Mime type</td>
                                        <td class="property-value">${imageInstance.mimeType}</td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">Image Identifier</td>
                                        <td class="property-value">${imageInstance.imageIdentifier}</td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">Zoom levels</td>
                                        <td class="property-value">${imageInstance.zoomLevels}</td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">Image URL</td>
                                        <td class="property-value"><img:imageUrl imageId="${imageInstance.imageIdentifier}" /></td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">MD5 Hash</td>
                                        <td class="property-value">${imageInstance.contentMD5Hash}</td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">SHA1 Hash</td>
                                        <td class="property-value">${imageInstance.contentSHA1Hash}</td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">Size on disk (including all artifacts)</td>
                                        <td class="property-value"><img:sizeInBytes size="${sizeOnDisk}" /></td>
                                    </tr>

                                    <g:if test="${subimages}">
                                        <tr>
                                            <td>Sub-images</td>
                                            <td>
                                                <ul>
                                                    <g:each in="${subimages}" var="subimage">
                                                        <li imageId="${subimage.id}">
                                                            <g:link controller="image" action="details" id="${subimage.id}">${subimage.originalFilename ?: subimage.id}</g:link>
                                                            <i class="icon-info-sign image-info-button"></i>
                                                        </li>
                                                    </g:each>
                                                </ul>
                                            </td>
                                        </tr>
                                    </g:if>


                                    <tr>
                                        <td colspan="2">

                                            <g:if test="${isImage}">
                                                <button class="btn btn-small" id="btnViewImage" title="View zoomable image"><i class="icon-eye-open"></i></button>
                                            </g:if>
                                            <a class="btn btn-small" href="<img:imageUrl imageId="${imageInstance.imageIdentifier}" />" title="Download full image" target="imageWindow"><i class="icon-download-alt"></i></a>

                                            <auth:ifAnyGranted roles="${au.org.ala.web.CASRoles.ROLE_USER}, ${au.org.ala.web.CASRoles.ROLE_USER}">
                                                <g:if test="${albums}">
                                                    <button class="btn btn-small" title="Add this image to an album" id="btnAddToAlbum"><i class="icon-book"></i></button>
                                                </g:if>
                                            </auth:ifAnyGranted>

                                            <auth:ifAnyGranted roles="${CASRoles.ROLE_ADMIN}">
                                                <button class="btn btn-small" id="btnRegen" title="Regenerate artifacts"><i class="icon-refresh"></i></button>
                                                <button class="btn btn-small btn-danger" id="btnDeleteImage" title="Delete image"><i class="icon-remove icon-white"></i></button>
                                            </auth:ifAnyGranted>
                                        </td>
                                    </tr>

                                </table>
                            </div>
                            <div class="tab-pane" id="tabExif" metadataSource="${au.org.ala.images.MetaDataSourceType.Embedded}">
                            </div>
                            <div class="tab-pane" id="tabUserDefined" metadataSource="${au.org.ala.images.MetaDataSourceType.UserDefined}">
                            </div>
                            <div class="tab-pane" id="tabSystem" metadataSource="${au.org.ala.images.MetaDataSourceType.SystemDefined}">
                            </div>
                            <auth:ifAnyGranted roles="${CASRoles.ROLE_ADMIN}">
                            <div class="tab-pane" id="tabAuditMessages">
                            </div>
                            </auth:ifAnyGranted>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>

<r:script>

    function refreshMetadata(tabDiv) {
        var dest = $(tabDiv);
        dest.html("Loading...");
        var source = dest.attr("metadataSource");
        $.ajax("${createLink(controller:'image', action:'imageMetadataTableFragment', id: imageInstance.id)}?source=" + source).done(function(content) {
            dest.html(content);
        });
    }

    <auth:ifAnyGranted roles="${CASRoles.ROLE_ADMIN}">

        function refreshAuditTrail() {
            $.ajax("${createLink(controller: 'image', action:'imageAuditTrailFragment', id: imageInstance.id)}").done(function(content) {
                $("#tabAuditMessages").html(content);
            });
        }

    </auth:ifAnyGranted>

    $(document).ready(function() {

        audiojs.createAll();

        $("#btnAddToAlbum").click(function(e) {

            e.preventDefault();
            imglib.selectAlbum(function(albumId) {
                $.ajax("${createLink(controller:'album', action:'ajaxAddImageToAlbum')}/" + albumId + "?imageId=${imageInstance.id}").done(function(result) {
                    if (result.success) {
                    }
                });
            });

        });

        $('a[data-toggle="tab"]').on('shown', function (e) {
            var dest = $($(this).attr("href"));
            if (dest.attr("metadataSource")) {
                refreshMetadata(dest);
            } else {
                if (dest.attr("id") == "tabAuditMessages") {
                    refreshAuditTrail();
                }
            }
        });

        $("#btnViewImage").click(function(e) {
            e.preventDefault();
            window.location = "${createLink(controller:'image', action:'view', id: imageInstance.id)}";
        });

        $("#btnRegen").click(function(e) {
            e.preventDefault();
            $.ajax("${createLink(controller:'webService', action:'scheduleArtifactGeneration', id: imageInstance.imageIdentifier)}").done(function() {
                window.location = this.location.href; // reload
            });
        });

        $("#btnDeleteImage").click(function(e) {
            e.preventDefault();

            var options = {
                message: "Warning! This operation cannot be undone. Are you sure you wish to permanently delete this image?",
                affirmativeAction: function() {
                    $.ajax("${createLink(controller:'webService', action:'deleteImage', id: imageInstance.imageIdentifier)}").done(function() {
                        window.location = "${createLink(controller:'image', action:'list')}";
                    });
                }
            };

            imglib.areYouSure(options);
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

        loadTags();

    });

    function loadTags() {
        $.ajax("${createLink(action:'tagsFragment',id:imageInstance.id)}").done(function(html) {
            $("#tagsSection").html(html);
        });
    }


</r:script>

