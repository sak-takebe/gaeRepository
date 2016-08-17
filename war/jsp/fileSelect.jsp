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
<script>
$(document).ready(function() {

	// dropzone.js
	var dropzone = new Dropzone('.dropzone', {
		autoProcessQueue : false,
		init : function() {
			this.on('drop', function(file) {
			})
		},
		success:function(_file, _return, _xml){
		var token = _return['token'];
		document.forms[0].token.value = token;
		_file.previewElement.classList.add("dz-success");
		}
	});

	// prettyPhoto.js
	$("a[rel^='prettyPhoto']").prettyPhoto({
		show_title : false,
		deeplinking : false,
		allow_resize : true,
	});

	// アップロードボタン押下時
	$("input:button").click(function() {
		if (window.confirm('upload files ok ？')) {
			// ユーザ存在チェック
			var _return = isExistUser();
			var obj = JSON.parse(_return);
			if (obj['message'] == "success") {
				dropzone.processQueue();
			} else {
				alert("please sign in or register.");
			}
		}
	});
});

function isExistUser() {

	var param1 = document.forms[0].token.value;

	return $.ajax({
		url : '/IsExistUser',
		// async(非同期) : false
		async : false,
		type : 'POST',
		dataType : 'json',
		data : {
			parameter1 : param1
		},
		timeout : 10000,
		success : function(data) {
			// alert("成功");
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("失敗");
		}
	}).responseText;
}

function createUderid() {

	var param1 = "";
	var param2 = "";

	return $.ajax({
		url : '/CreateUserid',
		// async(非同期) : false
		async : false,
		type : 'POST',
		dataType : 'json',
		data : {
			parameter1 : param1,
			parameter2 : param2
		},
		timeout : 10000,
		success : function(data) {
			// alert("成功");
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("失敗");
		}
	}).responseText;
}

var uid;
function userRegistWindow() {

	var win;

	if( !win || win.closed ) {
	// 子画面が開かれていない場合
		// ajax通信でUUIDを取得
		var _return = createUderid();
		uid = JSON.parse(_return)['userid'];

		var w_size = 400;
		var h_size = 300;
		var top = Number((window.screen.height - h_size) / 2);
		var left = Number((window.screen.width - w_size) / 2);
		var option = "width=400," + " height=300," + " top=" + top + ", left="
				+ left;
		win = window.open("html/registWindow.html", "sub", option);

	} else {
	// 子画面がすでに開かれている場合
		win.focus();
	}

}

function userLoginWindow() {

	var win;

	if( !win || win.closed ) {
	// 子画面が開かれていない場合

		var w_size = 400;
		var h_size = 300;
		var top = Number((window.screen.height - h_size) / 2);
		var left = Number((window.screen.width - w_size) / 2);
		var option = "width=400," + " height=300," + " top=" + top + ", left="
				+ left;
		win = window.open("html/loginWindow.html", "sub", option);

	} else {
	// 子画面がすでに開かれている場合
		win.focus();
	}

}

function getFiles() {

	var _return = showUploadFiles();
	var obj = JSON.parse(_return);
	if (obj['message'] == "success") {
		document.forms[0].token.value = obj['token'];
		if (obj['image'] == "") {
			document.getElementById('dispzone').innerHTML = "There is no uploaded files ...";
		} else {
			document.getElementById('dispzone').innerHTML = obj['image'];
		}
	} else {
		alert("please sign in or register.");
	}
}

function showUploadFiles() {

	var param1 = document.forms[0].token.value;

	return $.ajax({
		url : '/ShowUploadedFiles',
		// async(非同期) : false
		async : false,
		type : 'POST',
		dataType : 'json',
		data : {
			parameter1 : param1
		},
		timeout : 10000,
		success : function(data) {
			// alert("成功");
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("失敗");
		}
	}).responseText;
}
</script>
</head>
<body>
	<div>
		<button onClick="userLoginWindow()">sign in</button>
		<button onClick="userRegistWindow()">register</button>
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
	<input type="button" value="upload" />
	<br><br>
	<div id="dispzone"></div>
	<br>
	<button onClick="getFiles()">show images</button>
</body>
</html>