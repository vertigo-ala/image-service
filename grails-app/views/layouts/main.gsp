<%@ page import="au.org.ala.web.CASRoles; org.codehaus.groovy.grails.commons.ConfigurationHolder" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="app.version" content="${g.meta(name: 'app.version')}"/>
        <meta name="app.build" content="${g.meta(name: 'app.build')}"/>
        <meta name="description" content="Atlas of Living Australia"/>
        <meta name="author" content="Atlas of Living Australia">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link rel="icon" href="http://www.ala.org.au/wp-content/themes/ala2011/images/favicon.ico">
        <link rel="shortcut icon" href="http://www.ala.org.au/wp-content/themes/ala2011/images/favicon.ico">

        <title><g:layoutTitle/></title>

    <%-- Do not include JS & CSS files here - add them to your app's "application" module (in "Configuration/ApplicationResources.groovy") --%>
        <r:require modules="bootstrap, application"/>
        <r:require module="qtip" />

        <style>
            /*nav#breadcrumb {*/
                /*margin-top: 10px;*/
            /*}*/

            nav#breadcrumb ul, nav#breadcrumb ol {
                margin: 0;
            }


            nav#breadcrumb li {
                display:inline;
            }
            nav#breadcrumb li:after {
                content:" \BB ";
            }
            nav#breadcrumb li.last:after {
                content:"";
            }

            #page-header #breadcrumb a:link, #page-header #breadcrumb a:visited {
                color:#3a5c83;
                text-decoration: underline;
                outline:none;
            }

            hgroup h2 {
                margin: 0;
            }

            .pagination a {
                text-decoration: none;
            }

    </style>

        <r:script disposition='head'>
            // initialise plugins
            jQuery(function () {
                // autocomplete on navbar search input
                jQuery("form#search-form-2011 input#search-2011, form#search-inpage input#search, input#search-2013").autocomplete('http://bie.ala.org.au/search/auto.jsonp', {
                    extraParams: {limit: 100},
                    dataType: 'jsonp',
                    parse: function (data) {
                        var rows = new Array();
                        data = data.autoCompleteList;
                        for (var i = 0; i < data.length; i++) {
                            rows[i] = {
                                data: data[i],
                                value: data[i].matchedNames[0],
                                result: data[i].matchedNames[0]
                            };
                        }
                        return rows;
                    },
                    matchSubset: false,
                    formatItem: function (row, i, n) {
                        return row.matchedNames[0];
                    },
                    cacheLength: 10,
                    minChars: 3,
                    scroll: false,
                    max: 10,
                    selectFirst: false
                });

                // Mobile/desktop toggle
                // TODO: set a cookie so user's choice is remembered across pages
                var responsiveCssFile = $("#responsiveCss").attr("href"); // remember set href
                $(".toggleResponsive").click(function (e) {
                    e.preventDefault();
                    $(this).find("i").toggleClass("icon-resize-small icon-resize-full");
                    var currentHref = $("#responsiveCss").attr("href");
                    if (currentHref) {
                        $("#responsiveCss").attr("href", ""); // set to desktop (fixed)
                        $(this).find("span").html("Mobile");
                    } else {
                        $("#responsiveCss").attr("href", responsiveCssFile); // set to mobile (responsive)
                        $(this).find("span").html("Desktop");
                    }
                });

            });

            function showModal(options) {

                var opts = {
                    url: options.url ? options.url : false,
                    id: options.id ? options.id : 'modal_element_id',
                    height: options.height ? options.height : 500,
                    width: options.width ? options.width : 600,
                    title: options.title ? options.title : 'Modal Title',
                    hideHeader: options.hideHeader ? options.hideHeader : false,
                    onClose: options.onClose ? options.onClose : null,
                    onShown: options.onShown ? options.onShown : null
                }

                var html = "<div id='" + opts.id + "' class='modal hide' role='dialog' aria-labelledby='modal_label_" + opts.id + "' aria-hidden='true' style='width: " + opts.width + "px; margin-left: -" + opts.width / 2 + "px;overflow: hidden'>";
                if (!opts.hideHeader) {
                    html += "<div class='modal-header'><button type='button' class='close' data-dismiss='modal' aria-hidden='true'>x</button><h3 id='modal_label_" + opts.id + "'>" + opts.title + "</h3></div>";
                }
                html += "<div class='modal-body' style='max-height: " + opts.height + "px'>Loading...</div></div>";

                $("body").append(html);

                var selector = "#" + opts.id;

                $(selector).on("hidden", function() {
                    $(selector).remove();
                    if (opts.onClose) {
                        opts.onClose();
                    }
                });

                $(selector).on("shown", function() {
                    if (opts.onShown) {
                        opts.onShown();
                    }
                });

                $(selector).modal({
                    remote: opts.url
                });
            }

            function hideModal() {
                $("#modal_element_id").modal('hide');
            }

            function updateSelectionContext() {
                $.ajax("${createLink(controller:'selection', action:'userContextFragment')}").done(function(content) {
                    $("#selectionContext").html(content);
                });
            }

            function loadingSpinner() {
                return '<img src="../images/spinner.gif"/>&nbsp;Loading...';
            }

            $(document).ready(function() {
                updateSelectionContext();
            });

        </r:script>

        <r:layoutResources/>
        <g:layoutHead/>
    </head>

    <body class="${pageProperty(name: 'body.class')}" id="${pageProperty(name: 'body.id')}" onload="${pageProperty(name: 'body.onload')}">

        <hf:banner logoutUrl="${grailsApplication.config.grails.serverURL}/logout/logout"/>
        <hf:menu/>

        <g:set var="containerClass" value="container"/>
        <g:if test="${pageProperty(name:'page.useFluidLayout')}">
            <g:set var="containerClass" value="container-fluid"/>
        </g:if>


        <div class="container">
            <header id="page-header">
                <div class="container">
                    <hgroup>
                        <div class="row-fluid">
                            <div class="span8">
                                <g:pageProperty name="page.page-header" />
                            </div>
                            <div class="span4">
                                <div class="pull-right">
                                    <auth:ifLoggedIn>
                                        <span id="selectionContext" style="display: inline-block"></span>
                                    </auth:ifLoggedIn>
                                    <auth:ifAnyGranted roles="${CASRoles.ROLE_ADMIN}">
                                        <a href="${createLink(controller:'admin', action:'index')}" class="btn btn-warning btn-small"><i class="icon-cog icon-white"></i>Admin</a>
                                    </auth:ifAnyGranted>
                                </div>
                            </div>
                        </div>
                    </hgroup>
                    <g:if test="${flash.message}">
                        <div class="alert alert-info" role="status">${flash.message}</div>
                    </g:if>

                    <g:if test="${flash.errorMessage}">
                        <div class="alert alert-error" role="status">${flash.errorMessage}</div>
                    </g:if>
                </div>
            </header>
        </div>
        <div class="${containerClass}" id="main-content">
            <g:layoutBody/>
        </div><!--/.container-->

        <div class="container hidden-desktop">
            <%-- Borrowed from http://marcusasplund.com/optout/ --%>
            <a class="btn btn-small toggleResponsive"><i class="icon-resize-full"></i> <span>Desktop</span> version</a>
            %{--<a class="btn btn-small toggleResponsive"><i class="icon-resize-full"></i> Desktop version</a>--}%
        </div>

        <hf:footer/>

        <script type="text/javascript">
            var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
            document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
        </script>
        <r:script>
            var pageTracker = _gat._getTracker("UA-4355440-1");
            pageTracker._initData();
            pageTracker._trackPageview();

            // show warning if using IE6
            if ($.browser.msie && $.browser.version.slice(0, 1) == '6') {
                $('#header').prepend($('<div style="text-align:center;color:red;">WARNING: This page is not compatible with IE6.' +
                    ' Many functions will still work but layout and image transparency will be disrupted.</div>'));
            }
        </r:script>

    <!-- JS resources-->
        <r:layoutResources/>

    </body>
</html>