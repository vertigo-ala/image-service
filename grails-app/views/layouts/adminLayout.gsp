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
<g:applyLayout name="${grailsApplication.config.skin.layout}">
    <head>
        <style type="text/css">

        .icon-chevron-right {
            float: right;
            margin-top: 2px;
            margin-right: -6px;
            opacity: .25;
        }

        </style>

        <r:require modules="bootstrap, application" />

    </head>

    <body>

        <div class="container-fluid">
            <legend>
                <table style="width: 100%">
                    <tr>
                        <td><g:link uri="/">Home</g:link><img:navSeparator/><g:link controller="admin" action="index">Administration</g:link><img:navSeparator/><g:pageProperty name="page.pageTitle"/></td>
                        <td style="text-align: right"><span><g:pageProperty name="page.adminButtonBar"/></span></td>
                    </tr>
                </table>
            </legend>

            <div class="row-fluid">
                <div class="span3">
                    <ul class="nav nav-list nav-stacked nav-tabs">
                        <img:menuNavItem href="${createLink(controller: 'admin', action: 'dashboard')}" title="Dashboard" />
                        <img:menuNavItem href="${createLink(controller: 'image', action: 'upload')}" title="Upload an image" />
                        <img:menuNavItem href="${createLink(controller: 'admin', action: 'tools')}" title="Tools" />
                        <img:menuNavItem href="${createLink(controller: 'admin', action: 'duplicates')}" title="Duplicates" />
                        <img:menuNavItem href="${createLink(controller: 'admin', action: 'searchCriteria')}" title="Search Criteria" />
                        <img:menuNavItem href="${createLink(controller: 'admin', action: 'tags')}" title="Tags" />
                        <img:menuNavItem href="${createLink(controller: 'admin', action: 'settings')}" title="Settings" />
                    </ul>
                </div>

                <div class="span9">
                    <g:layoutBody/>
                </div>
            </div>
        </div>
    </body>
</g:applyLayout>