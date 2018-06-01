<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hello!</title>
<#--<link href="/css/main.css" rel="stylesheet">-->
</head>
<body>
<h2 class="hello-title">Hello!</h2>
<div>
    <form method="POST" enctype="multipart/form-data" action="/stabilize/fileUpload">
        <table>
            <tr>
                <td>File to upload:</td>
                <td><input type="file" name="file"/></td>
            </tr>
            <tr>
                <td><input type="hidden" value="test" id="hidden"/></td>
            </tr>
            <tr>
                <td></td>
                <td><input type="submit" value="Upload"/></td>
            </tr>
        </table>
    </form>
</div>
</body>
</html>
