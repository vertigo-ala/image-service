<div class="">
    <g:render template="/image/imageThumbnails" model="${[images: imageList, totalImageCount: imageList.totalCount, allowSelection: true, thumbsTitle: "${imageList.totalCount} matching images"]}" />
</div>