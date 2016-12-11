(function() {
    var sign = {
        init: function() {
            this.bindEvent();
        },
        bindEvent: function() {
            $('#js-sign-up').on('click', this.sign);
            $('#js-sign-in').on('click', this.sign);
        },
        sign: function(e) {
            if (e.target.id === 'js-sign-up') {
                // 新規登録の場合は確認ダイアログ表示
                if (confirm("sign up ok ？")) {
                    param_pass_to_openner('up');
                }
            } else {
                param_pass_to_openner('in');
            }
        }
    };
    var param_pass_to_openner = function(up_in) {
        var _return;
        if (up_in === 'up') {
            _return = regist_or_login('/RegistUserInfo');
        } else {
            _return = regist_or_login('/Login');
        }
        var obj = JSON.parse(_return);

        if (obj['message'] == 'success') {

            alert('success !');
            // ユーザIDを親画面に埋め込み
            window.opener.document.getElementById('statusMessage').innerHTML = "USER ID：" + obj['userid'];
            // トークンを親画面に埋め込み
            window.opener.document.forms[0].token.value = obj['token'];
            // 閉じる
            window.close();
        } else {
            alert('fail to sign ' + up_in + ' ...');
        }
    };
    var regist_or_login = function(transUrl) {
        var params = [document.forms[0].userId.value,
                      document.forms[0].userPassword.value];
        var prop = window.opener.prop;
        var _return = prop.ajax(transUrl, params);
        return _return;
    };
    $(function() {
        // 処理開始
        sign.init();
    });
})();