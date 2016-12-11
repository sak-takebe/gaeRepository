<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.google.appengine.api.blobstore.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html/css; charset=UTF-8">
        <meta name="viewport" content="width=device-width, height=device-height,initial-scale=1.0">
        <title>TOP</title>
        <link rel="apple-touch-icon" href="image/icon.jpg" />
        <!-- CSS -->
        <link rel="stylesheet" type="text/css"
            href="dropzone-master/dist/dropzone.css">
        <link rel="stylesheet" type="text/css" media="all"
            href="prettyPhoto_compressed_3.1.6/css/prettyPhoto.css">
        <style type="text/css">
        <!--
        img {
            -moz-border-radius: 15px;
            -webkit-border-radius: 15px;
            border-radius: 15px;
        }
        ul.image_list li a.image_box {
            /* position：相対位置  画像とチェックボックスを重ねるために指定 */
            position: relative;
        }
        .image_box .checkbox {
            /* チェックボックスの位置は絶対位置に指定 */
            position: absolute;
            /* チェックボックスは、親要素の右上からの位置 */
            /* top: 5px; */
            right: 0.5px;
            /* チェックボックス拡大 */
            transform: scale(2);
        }
        .image_box img.thumbnail.checked {
            /* チェックが入った状態だと、枠が表示 */
            border: 6px solid blue;
            /* 線をwidthとheightに含める */
            box-sizing: border-box;
        }
        #js-deleteImages {
            display: none;
        }
        -->
        </style>
        <!-- javascript -->
        <script type="text/javascript"
            src="https://code.jquery.com/jquery-2.0.0.min.js"></script>
        <script src="dropzone-master/dist/dropzone.js"></script>
        <script src="prettyPhoto_compressed_3.1.6/js/jquery.prettyPhoto.js"></script>
        <script src="/js/fileSelect.js"></script>
    </head>
    <body>
        <div>
            <button id='js-signIn'>sign in</button>
            <button id='js-signUp'>sign up</button>
            <br>
            <br>
            <a id="statusMessage">&nbsp;&nbsp;* easy to start ！</a>
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
        <input type="button" id='js-upload' value="upload"/>
        <br><br>
        <!-- <div class="gallery"> -->
        <div>
            <ul id="dispzone" class="image_list"></ul>
        </div>
        <br>
        <button id='js-showImages'>show images</button>
        <button id="js-deleteImages">delete images</button>
        <br>
        <div id="infoMessage"></div>
    </body>
</html>