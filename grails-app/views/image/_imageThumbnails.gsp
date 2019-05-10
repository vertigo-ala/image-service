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

    .image-search-results {
        display: flex;
        flex-wrap: wrap;
        padding: 0 4px;
    }

    /* Create four equal columns that sits next to each other */
    .column {
        flex: 16.6%;
        max-width: 16.6%;
        padding: 0 4px;
        object-fit: cover;
    }

    .column img {
        margin-top: 8px;
        vertical-align: middle;

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
<div class="image-search-results">

    <g:set var="imagesPerCol" value="${images.size() > 6 ? Math.round(images.size() / 6).toInteger() : 1}"/>

    <g:each in="${images}" var="image" status="imageIdx">

            <g:if test="${imageIdx > 0 && imageIdx % imagesPerCol == 0}">
                </div>
            </g:if>

            <g:if test="${imageIdx == 0 || imageIdx % imagesPerCol == 0}">
                <div class="column">
            </g:if>

            <div class="thumbnail" imageId="${image.imageIdentifier}" style="padding:0; margin:0;" >
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
                <div class="image-thumbnail">
                    <a href="${createLink(controller:'image', action:'details', id: image.imageIdentifier)}">
                        <img src="<img:imageThumbUrl imageId='${image.imageIdentifier}'/>" />
                    </a>
                </div>
                <g:if test="${footerTemplate}">
                    <g:render template="${footerTemplate}" model="${[image: image]}" />
                </g:if>
            </div>
    </g:each>
    </div>
</div>
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


        $(".image-thumbnail").each(function() {
            // var imageId = $(this).closest("[imageId]").attr("imageId");
            // var title = $(this).closest("[imageId]").attr("title");
            // if (imageId) {
            //
            //     $(this).attr('data-original-title', title);
            //     $(this).tooltip({html:true});
                %{--$(this).tooltip({--}%
                %{--    html:true--}%
                %{--    --}%%{--"title": function() {--}%
                %{--    --}%%{--    console.log('loading tool tip content');--}%
                %{--    --}%%{--    var contentTT= '';--}%
                %{--    --}%%{--    $.ajax("${createLink(controller:'image', action:"imageTooltipFragment")}/" + imageId).then(function(content) {--}%
                %{--    --}%%{--            contentTT = content;--}%
                %{--    --}%%{--        },--}%
                %{--    --}%%{--        function(xhr, status, error) {--}%
                %{--    --}%%{--            contentTT =  status + ": " + error;--}%
                %{--    --}%%{--        });--}%
                %{--    --}%%{--    return contentTT;--}%
                %{--    --}%%{--}--}%
                %{--});--}%

                %{--$(this).on('show.bs.tooltip', function () {--}%
                %{--    // do something…--}%
                %{--    $.ajax("${createLink(controller:'image', action:"imageTooltipFragment")}/" + imageId).then(function(content) {--}%
                %{--        console.log('loading tool tip content');--}%
                %{--        console.log($(this).id);--}%
                %{--        $(this).attr('data-original-title', content);--}%
                %{--    },--}%
                %{--    function(xhr, status, error) {--}%
                %{--        $(this).attr('data-original-title', status + ": " + error);--}%
                %{--        console.log('ERROR loading tool tip content');--}%
                %{--    });--}%
                %{--});--}%

                %{--$(this).qtip({--}%
                %{--    content: {--}%
                %{--        text: function(event, api) {--}%
                %{--            $.ajax("${createLink(controller:'image', action:"imageTooltipFragment")}/" + imageId).then(function(content) {--}%
                %{--                api.set("content.text", content);--}%
                %{--            },--}%
                %{--            function(xhr, status, error) {--}%
                %{--                api.set("content.text", status + ": " + error);--}%
                %{--            });--}%
                %{--        }--}%
                %{--    }--}%
                %{--});--}%
            // }
        });

        // $(window).scroll(function() {
        //     if($(window).scrollTop() + $(window).height() == $(document).height()) {
        //         alert("bottom!");
        //     }
        // });

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
</script>