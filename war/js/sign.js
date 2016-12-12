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
            var upIn;
            var url;
            if (e.target.id === 'js-sign-up') {
                // 新規登録の場合は確認ダイアログ表示
                if (confirm("sign up ok ？")) {
                    upIn = 'up';
                    url = '/RegistUserInfo';
                }
            } else {
                upIn = 'in';
                url = '/Login';
            }
            // サインアップまたはサインイン実行
            excute(url, upIn);
        }
    };
    var excute = function(url, upIn) {
        var prop = window.opener.prop;
        var _return = prop.ajax(url, [document.forms[0].userId.value,
                                       document.forms[0].userPassword.value]);
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
            alert('fail to sign ' + upIn + ' ...');
        }
    };
    $(function() {
        // 処理開始
        sign.init();
    });
})();