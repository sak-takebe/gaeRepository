$(function(){

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
	prettyPhoto();

	// アップロードボタン押下時
	$("input:button").click(function() {
		if (window.confirm('upload files ok ？')) {
			// ユーザ存在チェック
			var _return = isExistUser();
			var obj = JSON.parse(_return);
			if (obj['message'] == "success") {
				dropzone.processQueue();
			} else {
				alert("please sign in or sign up.");
			}
		}
	});
});

function prettyPhoto() {
	// prettyPhoto.js
	$("a[rel^='prettyPhoto']").prettyPhoto({
		show_title : false,
		deeplinking : false,
		allow_resize : true
	});
}

function isExistUser() {

	var params = [document.forms[0].token.value, 'nothing'];
	var _return = ajax('/IsExistUser', params);
	return _return;
}

function createUderid() {

	var params = ['nothing','nothing'];

	var _return = ajax('/CreateUserid', params);
	return _return;
}

var uid;
function subWindow (openHTML, clickButton) {

	var win;

	if( !win || win.closed ) {
	// 子画面が開かれていない場合

		// 新規登録の場合
		if (clickButton === "sign_up") {
			// ajax通信でUUIDを取得
			var _return = createUderid();
			uid = JSON.parse(_return)['userid'];
		}
		var w_size = 400;
		var h_size = 300;
		var top = Number((window.screen.height - h_size) / 2);
		var left = Number((window.screen.width - w_size) / 2);
		var option = "width=400," + " height=300," + " top=" + top + ", left="
				+ left;
		win = window.open(openHTML, "sub", option);
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
		var dispzone = document.getElementById('dispzone');
		if (obj['image'] == "") {
			dispzone.innerHTML = "There is no uploaded files ...";
		} else {
			dispzone.innerHTML = obj['image'];
			prettyPhoto();
		}
	} else {
		alert("please sign in or sign up.");
	}
}

function showUploadFiles() {

	var params = [document.forms[0].token.value, 'nothing'];
	var _return = ajax('/ShowUploadedFiles', params);

	return _return;
}

// ajax通信
function ajax (transUrl, params) {

	return $.ajax({
		url : transUrl,
		// async(非同期) : false
		async : false,
		type : 'POST',
		dataType : 'json',
		data : {
			parameter1 : params[0],
			parameter2 : params[1]
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