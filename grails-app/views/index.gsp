<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <meta name="section" content="home"/>
        <title>ALA Image Service - Home</title>
    </head>

    <body class="content">
        <div class="row">
            <div class="well well-small">
                <h1>ALA Image Service</h1>
                <ul>
                    <li>
                        <a href="${createLink(controller:'image', action:'upload')}">Upload an image</a>
                    </li>
                    <li>
                        <a href="${createLink(controller:'image', action:'list')}">Images</a>
                    </li>
                    <li>
                        <a href="${createLink(controller:'tag', action:'index')}">Tags</a>
                    </li>
                    <li>
                        <a href="${createLink(controller:'admin', action:'index')}">Administration</a>
                    </li>
                </ul>
            </div>
        </div>
    </body>

</html>
