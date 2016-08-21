function sign(up_in) {

	if (up_in === "up") {
		// 新規登録の場合は確認ダイアログ表示
		if (confirm("sign up ok ？")) {
			param_pass_to_openner(up_in);
		}
	} else {
		param_pass_to_openner(up_in);
	}
}

function param_pass_to_openner (up_in) {

	var _return;
	if (up_in === "up") {
		_return = regist_or_login('/RegistUserInfo');
	} else {
		_return = regist_or_login('/Login');
	}
	var obj = JSON.parse(_return);

	if (obj['message'] == "success") {

		alert("success !");
		// ユーザIDを親画面に埋め込み
		window.opener.document.getElementById('memo').innerHTML = "USER ID：" + obj['userid'];
		// トークンを親画面に埋め込み
		window.opener.document.forms[0].token.value = obj['token'];

		// 閉じる
		window.close();
	} else {
		alert("fail to sign " + up_in + " ...");
	}
}

function regist_or_login(transUrl) {

	var params = [document.forms[0].userId.value,
	              document.forms[0].userPassword.value];

	var _return = ajax(transUrl, params);
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