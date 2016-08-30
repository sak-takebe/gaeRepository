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
-->
</style>
<!-- javascript -->
<script type="text/javascript"
	src="https://code.jquery.com/jquery-1.11.0.min.js"></script>
<script src="dropzone-master/dist/dropzone.js"></script>
<script src="prettyPhoto_compressed_3.1.6/js/jquery.prettyPhoto.js"></script>
<script src="/js/fileSelect.js"></script>
</head>
<body>
	<div>
		<button onClick="subWindow('html/sign_in.html', 'sign_in')">sign in</button>
		<button onClick="subWindow('html/sign_up.html', 'sign_up')">sign up</button>
		<br>
		<br>
		<a id="memo">&nbsp;&nbsp;* easy to start ！</a>
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
	<input type="button" value="upload"/>
	<br><br>
	<div class="gallery">
		<ul id="dispzone"></ul>
	</div>
	<br>
	<button onClick="getFiles()">show images</button>
</body>
</html>