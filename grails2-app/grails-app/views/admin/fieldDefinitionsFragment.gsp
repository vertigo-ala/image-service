<div>
    <table class="table table-striped table-condensed table-bordered">
        <thead>
            <tr>
                <th>Name</th>
                <th>Type</th>
                <th>Value</th>
                <th />
            </tr>
        </thead>
        <tbody>
            <g:each in="${fieldDefinitions}" var="field">
                <tr fieldDefinitionId="${field.id}">
                    <td>${field.fieldName}</td>
                    <td>${field.fieldType}</td>
                    <td>${field.value}</td>
                    <td>
                        <button class="btn btn-small btn-danger btnDeleteFieldDefinition"><i class="icon-remove icon-white"></i></button>
                        <button class="btn btn-small btnEditFieldDefinition"><i class="icon-edit"></i></button>
                    </td>
                </tr>
            </g:each>
        </tbody>
    </table>
    <script>
    </script>
</div>