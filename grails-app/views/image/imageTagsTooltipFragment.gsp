<g:if test="${tags}">
    <g:each in="${tags}" var="tag">
        <span class="">
            <i class="icon-tag"></i>&nbsp;${tag.label}
        </span>
    </g:each>
</g:if>
<g:else>
    <div>
        No tags
    </div>
</g:else>