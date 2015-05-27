<g:if test="${imageInstance}">
    <div>
        <g:if test="${imageInstance.description}">
            <strong>${imageInstance.description}</strong>
            <div> ${imageInstance.originalFilename}</div>
        </g:if>
        <g:else>
            <strong>${imageInstance.originalFilename}</strong>
        </g:else>
        <div>${imageInstance.width} x ${imageInstance.height} (w x h)</div>
        <div>${imageInstance.mimeType}&nbsp;&nbsp;<img:sizeInBytes size="${imageInstance.fileSize}" /></div>
        <div>Uploaded on <img:formatDate date="${imageInstance.dateUploaded}" /></div>
        <g:if test="${imageInstance.dateTaken != imageInstance.dateUploaded}">
            <div>Taken on <img:formatDate date="${imageInstance.dateTaken}" /></div>
        </g:if>
        <g:if test="${imageInstance.copyright}">
            <div>${imageInstance.copyright}</div>
        </g:if>
        <g:if test="${imageInstance.attribution}">
            <div>
                ${imageInstance.attribution}
            </div>
        </g:if>
        <g:if test="${imageInstance.parent}">
            <div><small>*Subimage of <strong>${imageInstance.parent.originalFilename}</strong></small></div>
        </g:if>

        <a href="${grailsApplication.config.serverName}${createLink(controller: 'image', action: 'details', params:[id:imageInstance.id])}">View metadata</a>
    </div>
</g:if>
<g:else>
    <div>Could not retrieve image for id ${params.id}</div>
</g:else>