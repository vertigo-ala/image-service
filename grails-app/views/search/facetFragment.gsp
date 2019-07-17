
<table class="table table-bordered table-condensed table-striped scrollTable" id="fullFacets">
<tbody class="scrollContent">

<g:each in="${facetValues}" var="facetField">
<tr class="normalRow">
    <td class="multiple-facet-value">
        <a href="${g.createLink(controller:'search', action:'list')}?q=${params.q}&fq=${params.fq}&fq=${facet}:${facetField.key}">
            <g:if test="${facet =='dataResourceUid'}">
                <img:facetDataResourceResult dataResourceUid="${facetField.key}"/>
            </g:if>
            <g:else>
                <g:message code="${facetField.key}" default="${facetField.key}" />
            </g:else>
        </a>
    </td>
    <td class="multiple-facet-count" style="border-right: none;">${facetField.value}</td>
</tr>
</g:each>
</tbody>
</table>
