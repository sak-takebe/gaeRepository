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

function isExistUser() {

    var params = [document.forms[0].token.value, 'nothing'];
    var _return = ajax('/IsExistUser', params);
    return _return;
}

function createUderid() {

    var params = ['nothing', 'nothing'];

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

    // 1. ユーザ認証成功、トークン更新成功、取得したファイル名なし(message:success, image:空文字)
    // 2. ユーザ認証成功、トークン更新成功、取得したファイル名あり(message:success, image:aタグ)
    // 3. ユーザ認証失敗、トークン更新失敗(message: auth fail または token update fail)
    var _return = showUploadFiles();
    var obj = JSON.parse(_return);
    if (obj['message'] == "success") {
        document.forms[0].token.value = obj['token'];
        var dispzone = document.getElementById('dispzone');
        if (obj['image'] == "") {
            dispzone.innerHTML = "There is no uploaded files ...";
            // 削除ボタン非表示
            $('#deleteButton').hide();
        } else {
            dispzone.innerHTML = obj['image'];
            // prettyPhoto.jsの呼出
            prettyPhoto();
            // checkboxイベント付与
            addCheckboxEvent();
            // 削除ボタン表示
            $('#deleteButton').show();
        }
    } else {
        // ユーザ認証失敗または、トークン更新失敗
        alert("please sign in or sign up.");
    }
}

function showUploadFiles() {

    var params = [document.forms[0].token.value, 'nothing'];
    var _return = ajax('/ShowUploadedFiles', params);
    return _return;
}

function deleteFiles() {

    // 入力チェック
    var fileNames = getCheckedImages();
    if (fileNames.length === 0) {
        return;
    }

    // [servletからの返答バリエーション]
    // 1. ユーザ認証成功、トークン更新成功、ファイル削除成功(message:success, deleted:true)
    // 2. ユーザ認証成功、トークン更新成功、ファイル削除失敗(message:success, deleted:false)
    // 3. ユーザ認証失敗、トークン更新失敗(message: auth fail または token update fail)
    if (window.confirm('delete files ok ？')) {
        var params = [document.forms[0].token.value, fileNames];
        var _return = ajax('/DeleteUploadedFiles', params);
        var obj = JSON.parse(_return);
        if (obj['message'] == "success") {
            document.forms[0].token.value = obj['token'];
            if (obj['deleted'] == "true") {
                alert("success to deleted !");
                getFiles();
            } else if (obj['deleted'] == "false") {
                var infoMessage = document.getElementById('infoMessage');
                infoMessage.innerHTML = "<br> delete failed ...";
            }
        } else {
            // ユーザ認証失敗または、トークン更新失敗
            alert("please sign in or sign up.");
        }
    }
}

function getCheckedImages () {
    // ファイル名を配列で返却
    var fileNames = $('.checkbox.checked').map(function() {
        return $(this).val();
    }).get();
    return fileNames;
}

function prettyPhoto() {
    $("a[rel^='prettyPhoto']").prettyPhoto({
        show_title : false,
        deeplinking : false,
        allow_resize : true
    });
}

function addCheckboxEvent () {
    $('.image_box .checkbox').click(function(e) {
    if (!$(this).is('.checked')) {
        $(this).addClass('checked');
    } else {
        $(this).removeClass('checked');
    }
    // バブリングストップ。PreviewDialogを起動しない。
    e.stopPropagation();
    });
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