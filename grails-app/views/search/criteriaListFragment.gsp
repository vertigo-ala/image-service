%{--
  - ﻿Copyright (C) 2013 Atlas of Living Australia
  - All Rights Reserved.
  -
  - The contents of this file are subject to the Mozilla Public
  - License Version 1.1 (the "License"); you may not use this file
  - except in compliance with the License. You may obtain a copy of
  - the License at http://www.mozilla.org/MPL/
  -
  - Software distributed under the License is distributed on an "AS
  - IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  - implied. See the License for the specific language governing
  - rights and limitations under the License.
  --}%
<%@ page defaultCodec="none" %>
<%@ page import="au.org.ala.images.CriteriaValueType; au.org.ala.images.SearchCriteriaUtils" %>
<div>
    <g:if test="${searchCriteria}">
        <h5>Each of the following criteria must be met:</h5>
        <g:each in="${searchCriteria}" var="criteria">
            <div class="alert alert-info" style="color: black; padding-right: 10px">
                <table style="width: 100%">
                    <tr searchCriteriaId="${criteria.id}">
                        <td>
                            <img:searchCriteriaDescription criteria="${criteria}" />
                        </td>
                        <td style="width: 80px">
                            <button type="button" class="btn btn-mini pull-right btnDeleteCriteria" title="Remove this search criteria"><i class="icon-remove"></i></button>&nbsp;
                            <button type="button" style="margin-right:5px" class="btn btn-mini pull-right btnEditCriteria" title="Edit search criteria"><i class="icon-edit"></i></button>
                        </td>
                    </tr>
                </table>

            </div>
        </g:each>
    </g:if>
    <script type="text/javascript">
        $(".btnDeleteCriteria").click(function(e) {
            e.preventDefault();
            var criteriaId = $(this).parents("tr[searchCriteriaId]").attr("searchCriteriaId");
            if (criteriaId) {
                $.ajax("${createLink(controller: 'search', action:'ajaxRemoveSearchCriteria')}?searchCriteriaId=" + criteriaId).done(function(results) {
                    renderCriteria();
                    if (doSearch) {
                        doSearch();
                    }
                });
            }
        });

        $(".btnEditCriteria").click(function(e) {
            e.preventDefault();
            var criteriaId = $(this).parents("tr[searchCriteriaId]").attr("searchCriteriaId");
            if (criteriaId) {
                imgvwr.showModal({
                    url: "${createLink(action:'editSearchCriteriaFragment')}?criteriaId=" + criteriaId,
                    title: "Edit search criteria",
                    height: 520,
                    width: 700,
                    onClose: function () {
                        renderCriteria();
                    }
                });
            }
        });
    </script>
</div>

