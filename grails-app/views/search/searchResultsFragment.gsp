<div class="">
    <h3>${imageList.totalCount} matching images</h3>
    <g:render template="/image/imageThumbnails" model="${[images: imageList, totalImageCount: imageList.totalCount, allowSelection: true]}" />
</div>