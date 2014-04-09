<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <meta name="section" content="home"/>
        <title>ALA Image Service - Find Images</title>

        <style>
        </style>
        <r:require module="bootstrap" />
        <r:require module="bootstrap-datepicker" />
    </head>

    <body class="content">
        <img:headerContent title="Advanced Search">
            <%
                pageScope.crumbs = [
                ]
            %>
        </img:headerContent>
        <div class="row-fluid">
            <div class="well well-small">
                <button type="button" id="btnAddCriteria" class="btn btn-small btn-info"><i class="icon-plus icon-white"></i>&nbsp;Add Criteria</button>

                <button type="button" id="btnSearch" class="btn btn-primary pull-right">
                    <i class="icon-search icon-white"></i>&nbsp;Search
                </button>
                <button type="button" id="btnStartOver" class="btn pull-right" style="margin-right: 5px">
                    <i class="icon-remove-circle"></i>&nbsp;Start over
                </button>
                <div class="row-fluid">
                    <div id="searchCriteria">

                    </div>
                </div>
            </div>
        </div>
        %{--<div class="row-fluid">--}%
        %{--</div>--}%
        <div id="searchResults">
        </div>

    </body>
</html>

<r:script>

    $(document).ready(function() {
        $("#btnAddCriteria").click(function (e) {
            e.preventDefault();
            showModal({
                url: "${createLink(action:'addSearchCriteriaFragment')}",
                title: "Add Search Criteria",
                height: 520,
                width: 700,
                onClose: function () {
                    renderCriteria();
                    doSearch();
                }
            });
        });

        $("#btnStartOver").click(function(e) {
            e.preventDefault();
            $.ajax("${createLink(action:"ajaxClearSearchCriteria")}").done(function() {
                renderCriteria();
                clearResults();
            });
        });

        $("#btnSearch").click(function(e) {
            e.preventDefault();
            doSearch();
        });

        <g:if test="${hasCriteria}">
            renderCriteria();
            doSearch();
        </g:if>

    });

    function clearResults() {
        $("#searchResults").html("");
    }

    function doSearch() {
        doAjaxSearch("${createLink(action:'searchResultsFragment')}");
    }

    function doAjaxSearch(url) {
        $("#searchResults").html('<div>Searching...<img src="${resource(dir:'images', file:'spinner.gif')}"></img></div>');
        $.ajax(url).done(function(content) {
            $("#searchResults").html(content);
            $(".pagination a").click(function(e) {
                e.preventDefault();
                doAjaxSearch($(this).attr("href"));
            });
        });
    }

    function renderCriteria() {
        $.ajax("${createLink(action: 'criteriaListFragment', params:[:])}").done(function (content) {
            $("#searchCriteria").html(content);
        });

    }

</r:script>

