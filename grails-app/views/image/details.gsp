<%@ page import="au.org.ala.web.CASRoles" %>
<g:set var="mediaTitle" value="${isImage ? 'Image' : 'Media'}" />
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>${mediaTitle} - ${imageInstance.title ? imageInstance.title : imageInstance.imageIdentifier}</title>
        <meta name="breadcrumbs" content="${g.createLink( controller: 'image', action: 'list')}, Images"/>
        <asset:stylesheet src="ala/images-client.css" />
        <style>
            td.property-value { font-weight: bold !important; }
            .audiojs { width: 100%; }
            .tab-pane { padding-top: 20px !important; }
        </style>
    </head>
    <body class="fluid">
        <img:headerContent title="${mediaTitle} details ${imageInstance?.id}">
            <% pageScope.crumbs = []  %>
        </img:headerContent>
        <div class="container-fluid">
            <div class="row">
                <div id="viewerContainerId" class="col-md-8">
                </div>
                <div id="imageTabs" class="col-md-4">
                    <div class="tabbable" >
                        <ul class="nav nav-tabs">
                            <li class="active">
                                <a href="#tabProperties" data-toggle="tab">${mediaTitle}</a>
                            </li>
                            <li>
                                <a href="#tabExif" data-toggle="tab">Embedded</a>
                            </li>
                            <li>
                                <a href="#tabUserDefined" data-toggle="tab">User Metadata</a>
                            </li>
                            <li>
                                <a href="#tabSystem" data-toggle="tab">System</a>
                            </li>
%{--                            <g:if test="${isImage}">--}%
%{--                                <li>--}%
%{--                                    <a href="#tabThumbnails" data-toggle="tab">Thumbnails</a>--}%
%{--                                </li>--}%
%{--                            </g:if>--}%
                            <auth:ifAnyGranted roles="${CASRoles.ROLE_ADMIN}">
                                <li>
                                    <a href="#tabAuditMessages" data-toggle="tab">Audit</a>
                                </li>
                            </auth:ifAnyGranted>
                        </ul>

                        <div class="tab-content">
                            <div class="tab-pane active" id="tabProperties">
                                <table class="table table-bordered table-condensed table-striped">
                                    <tr>
                                        <td class="property-name">Image Identifier</td>
                                        <td class="property-value">${imageInstance.imageIdentifier}</td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">Title</td>
                                        <td class="property-value">${imageInstance.title}</td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">Creator</td>
                                        <td class="property-value"><img:imageMetadata image="${imageInstance}" resource="${resourceLevel}" field="creator"/></td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">Data resource UID</td>
                                        <td class="property-value">${imageInstance.dataResourceUid}</td>
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
                                        <td class="property-name">Rights</td>
                                        <td class="property-value"><img:imageMetadata image="${imageInstance}" resource="${resourceLevel}" field="rights"/></td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">Rights holder</td>
                                        <td class="property-value"><img:imageMetadata image="${imageInstance}" resource="${resourceLevel}" field="rightsHolder"/></td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">Licence</td>
                                        <td class="property-value">
                                            <img:imageMetadata image="${imageInstance}" resource="${resourceLevel}" field="license"/>
                                        </td>
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
                                        <td class="property-name">Harvested as occurrence record?</td>
                                        <auth:ifAnyGranted roles="${CASRoles.ROLE_ADMIN}">
                                            <td>
                                                <g:checkBox name="chkIsHarvestable" data-size="small" data-on-text="Yes" data-off-text="No" checked="${imageInstance.harvestable}" />
                                            </td>
                                        </auth:ifAnyGranted>
                                        <auth:ifNotGranted roles="${CASRoles.ROLE_ADMIN}">
                                            <td class="property-value">${imageInstance.harvestable ? "Yes" : "No"}</td>
                                        </auth:ifNotGranted>
                                    </tr>

                                    <tr>
                                        <td colspan="2">
                                            <g:link controller="webService" action="getImageInfo" params="[id:imageInstance.imageIdentifier]" title="View JSON metadata" class="btn btn-small">
                                                <i class="glyphicon glyphicon-icon-wrench"> </i>
                                            </g:link>

                                            <g:if test="${isImage}">
                                                <button class="btn btn-default" id="btnViewImage" title="View zoomable image"><i class="glyphicon glyphicon-icon-eye-open"></i></button>
                                            </g:if>
                                            <a class="btn btn-default" href="${grailsApplication.config.serverName}${createLink(controller:'image', action:'proxyImage', id:imageInstance.id, params:[contentDisposition: 'true'])}" title="Download full image" target="imageWindow"><i class="glyphicon glyphicon-icon-download-alt"></i></a>

                                            <auth:ifAnyGranted roles="${au.org.ala.web.CASRoles.ROLE_USER}, ${au.org.ala.web.CASRoles.ROLE_USER}">
                                                <g:if test="${albums}">
                                                    <button class="btn btn-default" title="Add this image to an album" id="btnAddToAlbum"><i class="glyphicon glyphicon-icon-book"></i></button>
                                                </g:if>
                                            </auth:ifAnyGranted>

                                            <auth:ifAnyGranted roles="${CASRoles.ROLE_ADMIN}">
                                                <button class="btn btn-default" id="btnRegen" title="Regenerate artifacts"><i class="glyphicon glyphicon-icon-refresh"></i></button>
                                            </auth:ifAnyGranted>

                                            <auth:ifAnyGranted roles="${CASRoles.ROLE_ADMIN}" creatorUserId="${imageInstance.uploader}">
                                                <button class="btn btn-danger" id="btnDeleteImage" title="Delete image (admin)"><i class="glyphicon glyphicon-icon-remove  glyphicon-icon-white"></i></button>
                                            </auth:ifAnyGranted>
                                            <auth:ifNotGranted roles="${CASRoles.ROLE_ADMIN}">
                                                <img:userIsUploader image="${imageInstance}">
                                                    <button class="btn btn-danger" id="btnDeleteImage" title="Delete your image"><i class="glyphicon glyphicon-icon-remove glyphicon-icon-white"></i></button>
                                                </img:userIsUploader>
                                            </auth:ifNotGranted>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            <div class="tab-pane" id="tabExif" metadataSource="${au.org.ala.images.MetaDataSourceType.Embedded}">
                            </div>
                            <div class="tab-pane" id="tabUserDefined" metadataSource="${au.org.ala.images.MetaDataSourceType.UserDefined}">
                            </div>
                            <div class="tab-pane" id="tabSystem" metadataSource="${au.org.ala.images.MetaDataSourceType.SystemDefined}" >
                                <table class="table table-bordered table-condensed table-striped">
                                    <tr>
                                        <td class="property-name">Data resource UID</td>
                                        <td class="property-value">${imageInstance.dataResourceUid}</td>
                                    </tr>
                                    <g:if test="${isImage}">
                                        <tr>
                                            <td class="property-name">Dimensions (w x h)</td>
                                            <td class="property-value">${imageInstance.width} x ${imageInstance.height}</td>
                                        </tr>
                                    </g:if>
                                    <tr>
                                        <td class="property-name">File size</td>
                                        <td class="property-value"><img:sizeInBytes size="${imageInstance.fileSize}" /></td>
                                    </tr>
                                    <tr>
                                        <td class="property-name">Mime type</td>
                                        <td class="property-value">${imageInstance.mimeType}</td>
                                    </tr>
                                    <g:if test="${isImage}">
                                        <tr>
                                            <td class="property-name">Zoom levels</td>
                                            <td class="property-value">${imageInstance.zoomLevels}</td>
                                        </tr>
                                        <tr>
                                            <td class="property-name">Linear scale</td>
                                            <td class="property-value">
                                                <g:if test="${imageInstance.mmPerPixel}">
                                                    ${imageInstance.mmPerPixel} mm per pixel
                                                    <button id="btnResetLinearScale" type="button" class="btn btn-default pull-right" title="Reset calibation">
                                                        <i class="icon-remove"></i></button>
                                                </g:if>
                                                <g:else>
                                                    &lt;not calibrated&gt;
                                                </g:else>
                                            </td>
                                        </tr>
                                    </g:if>
                                    <tr>
                                        <td class="property-name">${mediaTitle} URL</td>
                                        <td class="property-value">
                                            <a href="${img.imageUrl([imageId: imageInstance.imageIdentifier])}">
                                                <img:imageUrl imageId="${imageInstance.imageIdentifier}" />
                                            </a>
                                        </td>
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
                                </table>
                            </div>
                            <div class="tab-pane" id="tabThumbnails">
                                <ul class="list-unstyled">
                                    <g:each in="${squareThumbs}" var="thumbUrl">
                                        <li>
                                            <a href="${thumbUrl}" target="thumbnail">
                                                <img class="thumbnail" src="${thumbUrl}" style="width:75px;" title="${thumbUrl}" style="margin: 5px"/>
                                            </a>
                                        </li>
                                    </g:each>
                                </ul>
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

        <asset:javascript src="ala/images-client.js"/>
        <asset:javascript src="audiojs/audio.min.js"/>

    <script>

        function refreshMetadata(tabDiv) {
            var dest = $(tabDiv);
            dest.html("Loading...");
            var source = dest.attr("metadataSource");
            $.ajax("${grailsApplication.config.serverName}${createLink(controller:'image', action:'imageMetadataTableFragment', id: imageInstance.id)}?source=" + source).done(function(content) {
                dest.html(content);
            });
        }

        <auth:ifAnyGranted roles="${CASRoles.ROLE_ADMIN}">

        function refreshAuditTrail() {
            $.ajax("${grailsApplication.config.serverName}${createLink(controller: 'image', action:'imageAuditTrailFragment', id: imageInstance.id)}").done(function(content) {
                $("#tabAuditMessages").html(content);
            });
        }

        </auth:ifAnyGranted>

        $(document).ready(function() {

            var options = {
                auxDataUrl : "${auxDataUrl ? auxDataUrl : ''}",
                imageServiceBaseUrl : "${grailsApplication.config.serverName}${grailsApplication.config.contextPath}",
                imageClientBaseUrl : "${grailsApplication.config.serverName}${grailsApplication.config.contextPath}",
                zoomFudgeFactor: 0.65
            };

            var screenHeight = $(window).height();
            $('#viewerContainerId').css('height', (screenHeight - 200) + 'px');

            imgvwr.viewImage($("#viewerContainerId"), '${imageInstance.imageIdentifier}', "", "", options);
            // imgvwr.getViewerInstance().setZoom(9)
            audiojs.createAll();

            $("#btnAddToAlbum").click(function(e) {
                e.preventDefault();
                imgvwr.selectAlbum(function(albumId) {
                    $.ajax("${grailsApplication.config.serverName}${createLink(controller:'album', action:'ajaxAddImageToAlbum')}/" + albumId + "?imageId=${imageInstance.id}").done(function(result) {
                        if (result.success) {
                        }
                    });
                });
            });

            $("#btnResetLinearScale").click(function(e) {
                e.preventDefault();
                imgvwr.areYouSure({
                    title:"Reset calibration for this image?",
                    message:"Are you sure you wish to reset the linear scale for this image?",
                    affirmativeAction: function() {
                        var url = "${grailsApplication.config.serverName}${createLink(controller:'webService', action:'resetImageCalibration')}?imageId=${imageInstance.imageIdentifier}";
                        $.ajax(url).done(function(result) {
                            window.location.reload(true);
                        });
                    }
                });
            });

            $('a[data-toggle="tab"]').on('click', function (e) {

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
                window.location = "${grailsApplication.config.serverName}${createLink(controller:'image', action:'view', id: imageInstance.id)}";
            });

            $("#btnRegen").click(function(e) {
                e.preventDefault();
                $.ajax("${grailsApplication.config.serverName}${createLink(controller:'webService', action:'scheduleArtifactGeneration', id: imageInstance.imageIdentifier)}").done(function() {
                    window.location = this.location.href; // reload
                });
            });

            $("#btnDeleteImage").click(function(e) {
                e.preventDefault();
                var options = {
                    content: "Warning! This operation cannot be undone. Are you sure you wish to permanently delete this image?",
                    affirmativeAction: function() {
                        $.ajax("${grailsApplication.config.serverName}${createLink(controller:'webService', action:'deleteImage', id: imageInstance.imageIdentifier)}").done(function() {
                            window.location = "${grailsApplication.config.serverName}${createLink(controller:'image', action:'list')}";
                        });
                    }
                };
                imgvwr.areYouSure(options);
            });

            $(".image-info-button").each(function() {
                var imageId = $(this).closest("[imageId]").attr("imageId");
                if (imageId) {
                    $(this).qtip({
                        content: {
                            text: function(event, api) {
                                $.ajax("${grailsApplication.config.serverName}${createLink(controller:'image', action:"imageTooltipFragment")}/" + imageId).then(function(content) {
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
            $.ajax("${grailsApplication.config.serverName}${createLink(action:'tagsFragment',id:imageInstance.id)}").done(function(html) {
                $("#tagsSection").html(html);
            });
        }

    </script>
    </body>
</html>

