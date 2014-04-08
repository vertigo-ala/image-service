<%@ page import="au.org.ala.cas.util.AuthenticationUtils" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="adminLayout"/>
    <title>ALA Images - Admin - Duplicates</title>
    <style type="text/css" media="screen">
    </style>
</head>

<body>
    <content tag="pageTitle">Duplicates</content>
    <content tag="adminButtonBar" />
    <table class="table table-bordered">
        <thead>
            <tr>
                <td>Image</td>
                <td>MD5 Hash</td>
                <td>Count</td>
            </tr>
        </thead>
        <g:each in="${results}" var="dupe">
            <tr>
                <td>
                    <img src="<img:imageThumbUrl imageId="${dupe.image.imageIdentifier}" />" width="100">
                </td>
                <td>${dupe.hash}</td>
                <td>${dupe.count}</td>
            </tr>
        </g:each>
    </table>
</body>
<r:script>

        $(document).ready(function() {
        });

</r:script>
</html>
