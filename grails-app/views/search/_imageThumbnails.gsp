<!-- results list -->
<div id="facetWell" class="col-md-2 well well-sm">
    <h2 class="hidden-xs">Refine results</h2>
    <g:if test="${filters || searchCriteria}">
        <h5>Selected filters</h5>
        <ul class="facets list-unstyled">
            <g:each in="${filters}" var="filter">
                <li>
                    <a href="${raw(facet.selectedFacetLink([filter:filter.value]))}"  title="Click to remove this filter">
                     <span class="fa fa-check-square-o">&nbsp;</span> ${filter.key}
                    </a>
                </li>
            </g:each>
            <g:each in="${searchCriteria}" var="criteria">
                <li searchCriteriaId="${criteria.id}" >
                    <a href="${raw(facet.selectedCriterionLink(criteriaId:  criteria.id))}" title="Click to remove this filter">
                        <span class="fa fa-check-square-o">&nbsp;</span>
                        <img:searchCriteriaDescription criteria="${criteria}"/>
                    </a>
                </li>
            </g:each>
        </ul>
    </g:if>

    <g:each in="${facets}" var="facet">
        <h4>
            <span class="FieldName"><g:message code="facet.${facet.key}" default="${facet.key}"/></span>
        </h4>
        <ul class="facets list-unstyled">
            <g:each in="${facet.value}" var="facetCount">
                <li>
                    <a href="${request.getRequestURL().toString()}${raw(request.getQueryString() ? '?' + request.getQueryString() : '')}${raw(request.getQueryString() ? '&' : '?' )}fq=${facet.key}:${facetCount.key}">
                        <span class="fa fa-square-o">&nbsp;</span>
                        <span class="facet-item">
                        <g:if test="${facet.key == 'dataResourceUid'}">
                            <img:facetDataResourceResult dataResourceUid="${facetCount.key}"/>
                            <span class="facetCount">
                            (<g:formatNumber number="${facetCount.value}" format="###,###,###" />)
                            </span>
                        </g:if>
                        <g:else>
                            <g:message code="${facetCount.key}" default="${facetCount.key}" />
                            <span class="facetCount">
                            (<g:formatNumber number="${facetCount.value}" format="###,###,###" />)
                            </span>
                        </g:else>
                        </span>
                    </a>
                </li>
            </g:each>

            <g:if test="${facet.value.size() >= 10}">
            <a href="#multipleFacets" class="multipleFacetsLink" id="multi-${facet.key}"
               role="button" data-toggle="modal" data-target="#multipleFacets" data-facet="${facet.key}">
                <span class="glyphicon glyphicon-hand-right" aria-hidden="true"></span> choose more...
            </a>
            </g:if>
        </ul>
    </g:each>
</div>
<div class="col-md-10" style="margin-right:0px; padding-right:0px;">
    <div id="imagesList">
        <g:each in="${images}" var="image" status="imageIdx">
            <g:if test="${image}">
              <div class="imgCon" imageId="${image.imageIdentifier}">
                <g:if test="${headerTemplate}">
                    <g:render template="${headerTemplate}" model="${[image: image]}" />
                </g:if>
                <a href="${createLink(mapping: 'image_url', params: [imageId: image.imageIdentifier])}">
                    <g:if test="${image.format.startsWith("image")}">
                        <img src="<img:imageThumbUrl imageId='${image.imageIdentifier}'/>" />
                    </g:if>
                    <g:elseif test="${image.format.startsWith("audio")}">
                      <img src="${grailsApplication.config.placeholder.sound.thumbnail}"/>
                    </g:elseif>
                    <g:else>
                        <img src="${grailsApplication.config.placeholder.document.thumbnail}"/>
                    </g:else>
                </a>
                <g:if test="${footerTemplate}">
                    <g:render template="${footerTemplate}" model="${[image: image]}" />
                </g:if>
                <img:imageSearchResult image="${image}" />
            </div>
            </g:if>
        </g:each>
    </div>

    <g:set var="maxOffsetLimit" value="${grailsApplication.config.elasticsearch.maxOffset as int}" />
    <tb:paginate total="${totalImageCount > maxOffsetLimit ? maxOffsetLimit : totalImageCount}" max="100"
                 action="list"
                 controller="search"
                 params="${[q:params.q, fq:params.fq]}"
    />
</div>


<!-- modal popup for "choose more" link -->
<div id="multipleFacets" class="modal fade " tabindex="-1" role="dialog" aria-labelledby="multipleFacetsLabel"><!-- BS modal div -->
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h3 id="multipleFacetsLabel">Refine your search</h3>
            </div>
            <div class="modal-body">
                <div id="facetContent" class="tableContainer" style="max-height: 500px; overflow-y: auto;">

                </div>
            </div>
            <div id='submitFacets' class="modal-footer" style="text-align: left;">
                <button class="btn btn-default btn-small" data-dismiss="modal" aria-hidden="true" style="float:right;">Close</button>
            </div>
        </div>
    </div>
</div>


<!-- paging -->
<script>
    var self = this,
        $imageContainer = $('#imagesList'),
        MAX_HEIGHT = 300;

    $(document).ready(function() {
        $(window).on("load", function() {
            layoutImages();
        });
    });

    $("#multipleFacets").on('show.bs.modal', function(e){
        $("#facetContent").html("");
        var facet = $(e.relatedTarget).data('facet');
        $.ajax("${createLink(controller:'search',action: "facet")}?${raw(request.getQueryString())}${raw(request.getQueryString() ? '&' : '')}facet=" + facet).done(function(content) {
            $("#addButtonDiv").css("display", "block");
            $("#facetContent").html(content);
        });
    });
</script>