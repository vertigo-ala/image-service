<style>

    .image-caption, .image-thumbnail {
        text-align: center;
    }

    .row-fluid .thumbnails > li.span2:nth-child(6n+1),
    .row-fluid .thumbnails > li.span3:nth-child(4n+1),
    .row-fluid .thumbnails > li.span4:nth-child(3n+1),
    .row-fluid .thumbnails > li.span6:nth-child(2n+3){ margin-left: 0 !important; }

    .thumbnail-button-tray {
        display: block;
        margin-bottom: 5px;
        border: 1px solid #ddd;
    }

    input[type=checkbox] { display:none; } /* to hide the checkbox itself */
    input[type=checkbox] + label:before {
        font-family: FontAwesome, Arial, san-serif;
        font-size: 14px;
        display: inline-block;
        opacity: 0.50;
    }

    .selection-header label {
        margin-bottom: 0;
    }

    input[type=checkbox] + label:before { content: "\f096"; } /* unchecked icon */
    input[type=checkbox] + label:before { letter-spacing: 10px; } /* space between checkbox and label */

    input[type=checkbox]:checked + label:before {
        content: "\f046";
        opacity: 1;
    } /* checked icon */
    input[type=checkbox]:checked + label:before { letter-spacing: 5px; } /* allow space for check mark */

    #imagesList {
        margin:0;
    }

    .imgCon {
        display: inline-block;
        /*margin-right: 8px;*/
        text-align: center;
        line-height: 1.3em;
        background-color: #DDD;
        color: #DDD;
        /*padding: 5px;*/
        /*margin-bottom: 8px;*/
        margin: 2px 0 2px 0;
        position: relative;
    }

    .imgCon .meta {
        opacity: 0.7;
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        overflow: hidden;
        text-align: left;
        padding: 4px 6px 2px 8px;
    }

    .imgCon .full {
        color: white;
        background-color: black;
        display: none;
    }

    .imgCon .brief {
        color: black;
        background-color: white;
    }

    .imgCon .hover-target {
        display: none;
    }

    .imgCon:hover .full {
        display: inline-block;
    }

    .imgCon:hover .brief {
        display: none;
    }

    .column img {
        margin-top: 8px;
        vertical-align: middle;

    }

    .thumb-caption {
        position: absolute;
        bottom: 0;
        left: 0;
        width: 100%;
        background: rgba(0,0,0,0.5);
        font-size: 12px;
        line-height: 14px;
        color: #fff;
        padding: 4px 5px;
    }

    /* Responsive layout - makes a two column-layout instead of four columns */
    @media screen and (max-width: 800px) {
        .column {
            flex: 50%;
            max-width: 50%;
        }
    }

    /* Responsive layout - makes the two columns stack on top of each other instead of next to each other */
    @media screen and (max-width: 600px) {
        .column {
            flex: 100%;
            max-width: 100%;
        }
    }

    /*.image-thumbnail img {*/
    /*    position: relative;*/
    /*    !*margin: -10% auto;!* virtualy height needed turn don to zero *!*!*/
    /*    width: 100%;!* height will follow within image ratio *!*/
    /*    height:auto;!* to overrride attribute height set in tag *!*/
    /*    vertical-align:middle;!* finalise vertical centering on baseline*!*/
    /*}*/
    /*.image-thumbnail {*/
    /*    display:block;*/
    /*    height:100px;!*set an height *!*/
    /*    line-height:100px;!* set the baseline at 100px from top*!*/
    /*    overflow:hidden;!* crops/cut off *!*/
    /*}*/

</style>

<table style="width: 100%;">
    <tr>
        <td>
            <g:if test="${thumbsTitle}">
                <h4>${thumbsTitle}</h4>
            </g:if>
        </td>
        <td>
            <div class="">
                <g:if test="${allowSelection && images?.size() > 0 || toolButtons}">
                    <div class="btn-group pull-right">
                        <a class="btn btn-small dropdown-toggle" data-toggle="dropdown" href="#">
                            <i class="icon-cog"></i>&nbsp;
                            <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu pull-right">
                            <g:if test="${allowSelection && images?.size() > 0}">
                                <li>
                                    <a href="#" id="btnSelectAllOnPage">Select all on page</a>
                                </li>
                                <li>
                                    <a href="#" id="btnDeselectAllOnPage">Deselect all on page</a>
                                </li>
                                <li class="divider"></li>
                                <li>
                                    <a href="#" id="btnClearSelection">Clear selection</a>
                                </li>
                            </g:if>
                            <g:if test="${toolButtons}">
                                <g:if test="${allowSelection}">
                                    <li class="divider"></li>
                                </g:if>
                                <g:each in="${toolButtons}" var="tool">
                                    <g:if test="${tool.id == 'divider'}">
                                        <li class="divider"></li>
                                    </g:if>
                                    <g:else>
                                        <li>
                                            <a href="#" id="${tool.id}">${tool.label}</a>
                                        </li>
                                    </g:else>
                                </g:each>
                            </g:if>
                        </ul>
                    </div>
                </g:if>
            </div>
        </td>
    </tr>
</table>

<!-- results list -->
<div id="imagesList">
    <g:each in="${images}" var="image" status="imageIdx">
        <div class="imgCon" imageId="${image.imageIdentifier}">
            <g:if test="${allowSelection == true}">
                <div class="selection-header">
                    <g:checkBox class="chkSelectImage" name="chkSelectImage${image.id}"
                                checked="${selectedImageMap?.containsKey(image.imageIdentifier)}" />
                    <label for="chkSelectImage${image.imageIdentifier}"></label>
                </div>
            </g:if>
            <g:if test="${headerTemplate}">
                <g:render template="${headerTemplate}" model="${[image: image]}" />
            </g:if>
            <a href="${createLink(mapping: 'image_url', params: [imageId: image.imageIdentifier])}">
                <img src="<img:imageThumbUrl imageId='${image.imageIdentifier}'/>" />
            </a>
            <g:if test="${footerTemplate}">
                <g:render template="${footerTemplate}" model="${[image: image]}" />
            </g:if>

            <img:imageSearchResult image="${image}" />
        </div>
    </g:each>
</div>

<!-- pagenation -->
<div class="col-md-12">
    <tb:paginate total="${totalImageCount}" max="100"
                 action="list"
                 controller="image"
                 params="${[q:params.q]}"
    />
</div>

<script>
    $(document).ready(function() {

        $("#btnClearSelection").click(function(e) {
            e.preventDefault();
            imgvwr.showSpinner("Clearing selection...");
            $.ajax("${createLink(controller: 'selection', action: 'clearSelection')}").done(function() {
                location.reload(true);
                imgvwr.hideSpinner();
            });
        });

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

        $(window).on("load", function() {
            layoutImages();
        });
    });

    function selectAllOnPage() {

        imgvwr.showSpinner("Selecting images on page...");
        var imageList = $(".thumbnail[imageId]").map(function() {
            return $(this).attr("imageId");
        }).get();

        $.post("${createLink(controller:'selection', action:'ajaxSelectImages')}", { imageList: imageList } ).done(function() {
            updateSelectionContext();
            $(".thumbnail[imageId]").each(function(e) {
                $(this).find(".chkSelectImage").prop('checked', true);
            });
            imgvwr.hideSpinner();
        });
    }

    function deselectAllOnPage() {

        imgvwr.showSpinner("Deselecting images...");

        var imageList = $(".thumbnail[imageId]").map(function() {
            return $(this).attr("imageId");
        }).get();

        $.post("${createLink(controller:'selection', action:'ajaxDeselectImages')}", { imageList: imageList } ).done(function() {
            updateSelectionContext();
            $(".thumbnail[imageId]").each(function(e) {
                $(this).find(".chkSelectImage").prop('checked', false);
            });
            imgvwr.hideSpinner();
        });
    }

    var self = this,
        $imageContainer = $('#imagesList'),
        MAX_HEIGHT = 300;

    function getheight (images, width) {
        width -= images.length * 5;
        var h = 0;
        for (var i = 0; i < images.length; ++i) {
            if ($(images[i]).data('width') === undefined) {
                $(images[i]).data('width', $(images[i]).width());
            }
            if ($(images[i]).data('height') === undefined) {
                $(images[i]).data('height', $(images[i]).height());
            }
            //console.log("original = " + $(images[i]).data('width') + '/' + $(images[i]).data('height'));
            h += $(images[i]).data('width') / $(images[i]).data('height');
        }
        //console.log("row count = " + images.length + " row height = " + width / h);
        return width / h;
    };

    function setheight (images, height) {
        for (var i = 0; i < images.length; ++i) {
            //console.log("setting width to " + height * $(images[i]).data('width') / $(images[i]).data('height'));
            $(images[i]).css({
                width: height * $(images[i]).data('width') / $(images[i]).data('height'),
                height: height
            });
        }
    };

    function layoutImages (maxHeight) {

        var size = $imageContainer.innerWidth(),
            n = 0,
            images = $imageContainer.find('img');
        if (maxHeight === undefined) {
            maxHeight = MAX_HEIGHT;
        }

        w: while (images.length > 0) {
            for (var i = 1; i < images.length + 1; ++i) {
                var slice = images.slice(0, i);
                var h = self.getheight(slice, size);
                if (h < maxHeight) {
                    self.setheight(slice, h);
                    n++;
                    images = images.slice(i);
                    continue w;
                }
            }

            self.setheight(slice, Math.min(maxHeight, h));
            n++;
            break;
        }
    };
</script>