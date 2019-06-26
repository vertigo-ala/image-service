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
<div id="facetWell" class="col-md-2 well well-sm">

    <g:if test="${filters || searchCriteria}">
        <h5>Selected filters</h5>
        <ul class="facets list-unstyled">
            <g:each in="${filters}" var="filter">
                <li>
                    <a href="${facet.selectedFacetLink([filter:filter.value])}"  title="Click to remove this filter">
                     <span class="fa fa-check-square-o">&nbsp;</span> ${filter.key}
                    </a>
                </li>
            </g:each>
            <g:each in="${searchCriteria}" var="criteria">
                <li searchCriteriaId="${criteria.id}" >
                    <a href="${facet.selectedCriterionLink(criteriaId:  criteria.id)}" title="Click to remove this filter">
                        <span class="fa fa-check-square-o">&nbsp;</span>
                        <img:searchCriteriaDescription criteria="${criteria}" />
                    </a>
                </li>
            </g:each>
        </ul>
    </g:if>

    <g:each in="${facets}" var="facet">
        <h5>
            <span class="FieldName"><g:message code="facet.${facet.key}" default="${facet.key}"/></span>
        </h5>
        <ul class="facets list-unstyled">
            <g:each in="${facet.value}" var="facetCount">
                <li class="">
                    <a href="${request.getRequestURL().toString()}${request.getQueryString() ? '?' + request.getQueryString() : ''}${request.getQueryString() ? '&' : '?' }fq=${facet.key}:${facetCount.key}">
                        <span class="fa fa-square-o">&nbsp;</span>
                        <span class="facet-item">
                        <g:if test="${facet.key == 'dataResourceUid'}">
                            <img:facetDataResourceResult dataResourceUid="${facetCount.key}"/>
                            <span class="facetCount">
                            (<g:formatNumber number="${facetCount.value}" format="###,###,###" />)
                            </span>
                        </g:if>
                        <g:else>
                            ${facetCount.key}
                            <span class="facetCount">
                            (<g:formatNumber number="${facetCount.value}" format="###,###,###" />)
                            </span>
                        </g:else>
                        </span>
                    </a>
                </li>
            </g:each>
        </ul>
    </g:each>
</div>
<div class="col-md-10" style="margin-right:0px; padding-right:0px;">
    <div id="imagesList">
        <g:each in="${images}" var="image" status="imageIdx">
            <div class="imgCon" imageId="${image.imageIdentifier}">
                <g:if test="${headerTemplate}">
                    <g:render template="${headerTemplate}" model="${[image: image]}" />
                </g:if>
                <a href="${createLink(mapping: 'image_url', params: [imageId: image.imageIdentifier])}">
                    <g:if test="${image.mimeType.startsWith("image")}">
                        <img src="<img:imageThumbUrl imageId='${image.imageIdentifier}'/>" />
                    </g:if>
                    <g:else>
                        <img src="${grailsApplication.config.placeholder.sound.thumbnail}"/>
                    </g:else>
                </a>
                <g:if test="${footerTemplate}">
                    <g:render template="${footerTemplate}" model="${[image: image]}" />
                </g:if>
                <img:imageSearchResult image="${image}" />
            </div>
        </g:each>
    </div>
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
    var self = this,
        $imageContainer = $('#imagesList'),
        MAX_HEIGHT = 300;

    $(document).ready(function() {
        $(window).on("load", function() {
            layoutImages();
        });
    });
</script>