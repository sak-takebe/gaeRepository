<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.google.appengine.api.blobstore.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html/css; charset=UTF-8">
        <meta name="viewport" content="width=device-width, height=device-height,initial-scale=1.0">
        <title>TOP</title>
        <!-- touch-icon -->
        <link rel="apple-touch-icon" href="image/icon.jpg" />
        <!-- CSS -->
        <link rel="stylesheet" type="text/css"
            href="dropzone-master/dist/dropzone.css">
        <link rel="stylesheet" type="text/css" media="all"
            href="prettyPhoto_compressed_3.1.6/css/prettyPhoto.css">
        <link rel="stylesheet" type="text/css"
            href="css/fileSelect.css">
        <!-- javascript -->
        <script type="text/javascript"
            src="https://code.jquery.com/jquery-2.0.0.min.js"></script>
        <script src="dropzone-master/dist/dropzone.js"></script>
        <script src="prettyPhoto_compressed_3.1.6/js/jquery.prettyPhoto.js"></script>
        <script src="/js/fileSelect.js"></script>
    </head>
    <body>
        <br>
        <div class="css-statusMessage">
            <a id="statusMessage">&nbsp;* easy to start ！ &nbsp; please sign in or sign up.</a>
        </div>
        <br>
        <div class="css-button-sign">
            <button id='js-signIn'>sign in</button>
            <button id='js-signUp'>sign up</button>
        </div>
        <br>
        <%
            BlobstoreService service = BlobstoreServiceFactory
                    .getBlobstoreService();
            String uploadUrl = service.createUploadUrl("/Upload");
        %>
        <form action="<%=uploadUrl%>" class="dropzone">
            <input type="hidden" name="token" value="" size="36">
        </form>
        <br>
        <div class="css-button-upload">
            <input type="button" id='js-upload' value="upload"/>
        </div>
        <br>
        <br>
        <!-- <div class="gallery"> -->
        <div>
            <ul id="dispzone" class="image_list"></ul>
        </div>
        <br>
        <div class="css-bottom-showDel">
            <button id="js-showImages">show images</button>
            <button id="js-deleteImages">delete images</button>
        </div>
        <br>
        <div id="infoMessage"></div>
        <br>
        <br>
        <br>
    </body>
</html>