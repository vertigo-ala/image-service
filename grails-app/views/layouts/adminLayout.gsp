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
<g:applyLayout name="main">
    <head>
        <title>Admin</title>
        <meta name="breadcrumbs" content="${g.createLink( controller: 'image', action: 'list')}, Images"/>
    </head>
    <body>
        <div class="container-fluid">
            <h1>Admin tools</h1>
            <div class="row-fluid">
                <div class="col-md-3">
                    <ul class="nav nav-pills nav-stacked">
                        <img:menuNavItem href="${createLink(controller: 'admin', action: 'dashboard')}" title="Dashboard" />
                        <img:menuNavItem href="${createLink(controller: 'image', action: 'upload')}" title="Upload an image" />
                        <img:menuNavItem href="${createLink(controller: 'admin', action: 'tools')}" title="Tools" />
                        <img:menuNavItem href="${createLink(controller: 'admin', action: 'duplicates')}" title="Duplicates" />
                        <img:menuNavItem href="${createLink(controller: 'admin', action: 'searchCriteria')}" title="Search Criteria" />
                        <img:menuNavItem href="${createLink(controller: 'admin', action: 'tags')}" title="Tags" />
                        <img:menuNavItem href="${createLink(controller: 'admin', action: 'settings')}" title="Settings" />
                    </ul>
                </div>
                <div class="col-md-9">
                    <g:layoutBody/>
                </div>
            </div>
        </div>
    </body>
</g:applyLayout>