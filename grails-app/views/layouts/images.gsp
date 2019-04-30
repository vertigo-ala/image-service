<%@ page import="au.org.ala.web.CASRoles" %>
<g:applyLayout name="${grailsApplication.config.skin.layout}">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="app.version" content="${g.meta(name: 'app.version')}"/>
        <meta name="app.build" content="${g.meta(name: 'app.build')}"/>
        <meta name="description" content="Atlas of Living Australia"/>
        <meta name="author" content="Atlas of Living Australia">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://netdna.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
%{--        <r:require modules="application, qtip, image-viewer"/>--}%

        <asset:stylesheet src="application.css" />
        <asset:javascript src="ala/images-client.js"/>
        <asset:stylesheet src="ala/images-client.css" />
        <style>

            .pagination a {
                text-decoration: none;
            }

            .spinner {
                background: url("${g.resource(dir:'images', file:'spinner.gif')}") 50% 50% no-repeat transparent;
                height: 16px;
                width: 16px;
                padding: 0.5em;
                position: absolute;
                right: 0;
                top: 0;
                text-indent: -9999px;
            }

        </style>
        <script disposition='head'>
            var IMAGES_CONF = {}

            function updateSelectionContext() {
                $.ajax("${createLink(controller:'selection', action:'userContextFragment')}").done(function(content) {
                    $("#selectionContext").html(content);
                });
            }

            function updateAlbums() {
                $.ajax("${createLink(controller:'album', action:'userContextFragment')}").done(function(content) {
                    $("#albums-div").html(content);
                });
            }

            function loadingSpinner() {
                return '<img src="${g.resource(dir:'images', file:'spinner.gif')}"/>&nbsp;Loading...';
            }

            $(document).ready(function() {
                updateSelectionContext();
                updateAlbums();
            });

        </script>

        <g:layoutHead/>
    </head>

    <body class="fluid ${pageProperty(name: 'body.class')}" id="${pageProperty(name: 'body.id')}" onload="${pageProperty(name: 'body.onload')}">

        <g:set var="containerClass" value="container"/>
        <g:if test="${pageProperty(name:'page.useFluidLayout')}">
            <g:set var="containerClass" value="container-fluid"/>
        </g:if>

        <div class="container">
            <header id="page-header">
                <div class="container">
                    <hgroup>
                        <div class="row">
                            <div class="span8">
                                <g:pageProperty name="page.page-header" />
                            </div>
                            <div class="span4">
                                <div class="pull-right">
                                    <auth:ifLoggedIn>
                                        <span id="albums-div" style="display: inline-block"></span>
                                        <span id="selectionContext" style="display: inline-block"></span>
                                        <a href="${createLink(controller:'image', action:'stagedImages')}" class="btn btn-small btn-success"><i class="icon-plus icon-white"></i>Upload</a>
                                    </auth:ifLoggedIn>
                                    <auth:ifAnyGranted roles="${au.org.ala.web.CASRoles.ROLE_ADMIN}">
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

        <g:layoutBody/>

        <div class="spinner well well-small" style="display: none"></div>

        <div class="container hidden-desktop">
            <%-- Borrowed from http://marcusasplund.com/optout/ --%>
            <a class="btn btn-small toggleResponsive"><i class="icon-resize-full"></i> <span>Desktop</span> version</a>
        </div>
    </body>
</g:applyLayout>
