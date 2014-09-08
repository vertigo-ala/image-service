<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <meta name="section" content="home"/>
        <title>ALA Image Service - Images</title>
        <style>

        .numberCircle {
            border-radius: 50%;
            display: inline-block;
            width: 19px;
            height: 19px;
            padding: 6px;
            background: #fff;
            border: 2px solid #666;
            color: #666;
            text-align: center;
        }

        </style>
    </head>

    <body class="content">

        <img:headerContent title="Staged images ${userId}">
        </img:headerContent>

        <div class="well well-small">
        <div class="row-fluid">
            <div class="span3">
                <h4><span class="numberCircle">1</span>&nbsp;Upload Images</h4>
                <p>
                    Upload your images to the staging area
                </p>
            </div>
            <div class="span3">
                <h4><span class="numberCircle">2</span>&nbsp;Upload data file</h4>
                <p>
                    Upload a csv file containing Darwin Core data for each image. The first column in the csv file must be <code>filename</code>, and the filenames in this column will be matched exactly against the names of staged files.
                </p>
            </div>
            <div class="span3">
                <h4><span class="numberCircle">3</span>&nbsp;Configure columns</h4>
            <p>
                Add and configure columns in the table below. Only columns that are configured will be attached to each record
                <img:helpText>
                    <p>Field values can be derived from the image filename, or portions thereof, or can also be read from a separate csv datafile keyed by the image filename.</p>
                    <p><strong>Note:</strong> Only data displayed in the staged images table will be loaded</p>
                </img:helpText>
            </p>
            </div>
            <div class="span3">
                <h4><span class="numberCircle">4</span>&nbsp;Create occurrence records</h4>
                Review the staged images table, and create the records
            </div>
        </div>
        <div class="row-fluid">
            <div class="span3" style="text-align: center">
                <button id="btnSelectImages" class="btn">Select files</button>
            </div>
            <div class="span3" style="text-align: center">
                <g:if test="${hasDataFile}">
                    <button class="btn btn-warning" id="btnClearDataFile">Clear data file</button>
                    <a href="${dataFileUrl}">View data file</a>
                </g:if>
                <g:else>
                    <button class="btn" id="btnUploadDataFile"><i class="icon-upload"></i>&nbsp;Upload data file</button>
                </g:else>
            </div>
            <div class="span3" style="text-align: center">
                <button class="btnAddFieldDefinition btn"><i class="icon-plus"></i> Add column</button>
            </div>
            <div class="span3" style="text-align: center">
                <button id="btnLoadTasks" class="btn btn-primary" style="margin-left: 10px">Create tasks from staged images</button>
            </div>
        </div>

    </div>

        <div class="staged-files-list">
            <table class="table table-condensed table-bordered table-striped">
                <thead>
                    <th>Filename</th>
                    <th>Date staged</th>
                    <g:each in="${dataFileColumns}" var="field">
                        <th columnDefinitionId="${field.id}">
                            <div class="label" style="display: block">
                                <span style="font-weight: normal">${field.fieldDefinitionType} <b>${field.format}</b></span>
                                <a href="#" class="btnEditField pull-right" title="Edit column definition"><i class="icon-edit icon-white"></i></a>
                                <g:if test="${field.fieldName != 'externalIdentifier'}">
                                    <a href="#" class="btnDeleteField pull-right" title="Remove column"><i class="icon-remove icon-white"></i></a>
                                </g:if>
                                <br/>
                                ${field.fieldName}
                            </div>
                        </th>
                    </g:each>
                    <th style="width: 40px"></th>
                </thead>
                <tbody>
                    <g:each in="${fileList}" var="stagedFile">
                        <tr stagedFileId="${stagedFile.id}">
                            <td>
                                ${stagedFile.filename}
                            </td>
                            <td>
                                <img:formatDateTime date="${stagedFile.dateStaged}" />
                            </td>
                            <g:each in="${dataFileColumns}" var="field">
                                <td>

                                </td>
                            </g:each>
                            <td>
                                <button type="button" class="btn btn-small btn-danger btnDeleteStagedFile"><i class="icon-remove icon-white"></i></button>
                            </td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </div>

        <r:script>

            $(document).ready(function () {

                $(".btnDeleteStagedFile").click(function (e) {
                    e.preventDefault();
                    var stagedFileId = $(this).closest("[stagedFileId]").attr("stagedFileId");
                    if (stagedFileId) {
                        window.location = "${createLink(action:'deleteStagedImage')}?stagedImageId=" + stagedFileId;
                    }
                });

                $("#btnSelectImages").click(function(e) {
                    e.preventDefault();
                    var opts = {
                        title:"Upload images to the staging area",
                        url: "${createLink(controller: 'dialog', action:"selectImagesForStagingFragment", params:[userId: userId])}"
                    };

                    imglib.showModal(opts);
                });

                $("#btnUploadDataFile").click(function(e) {
                    e.preventDefault();
                    var options = {
                        title: "Upload a data file",
                        url: "${createLink(controller:'dialog', action:'uploadStagedImagesDataFileFragment', params:[userId: userId])}"
                    };
                    imglib.showModal(options);
                });

                $("#btnClearDataFile").click(function(e) {
                    e.preventDefault();
                    window.location = "${createLink(controller:'image', action:'clearStagingDataFile')}";
                });

                $(".btnAddFieldDefinition").click(function(e) {
                    e.preventDefault();
                    var options = {
                        title: "Add column definition",
                        url:"${createLink(controller: 'image', action:'editStagingColumnFragment')}"
                    };
                    imglib.showModal(options);
                });

                $(".btnDeleteField").click(function(e) {
                    e.preventDefault();
                    var fieldId = $(this).parents("[columnDefinitionId]").attr("columnDefinitionId");
                    if (fieldId) {
                        window.location = "${createLink(controller:'image', action:'deleteStagingColumnDefinition')}?columnDefinitionId=" + fieldId;
                    }
                });

                $(".btnEditField").click(function(e) {
                    e.preventDefault();
                    var fieldId = $(this).parents("[columnDefinitionId]").attr("columnDefinitionId");
                    if (fieldId) {
                        var options = {
                            title: "Edit field definition",
                            url:"${createLink(action: 'editStagingColumnFragment')}?columnDefinitionId=" + fieldId
                        };
                        imglib.showModal(options);
                    }
                });


            });
        </r:script>

    </body>
</html>
