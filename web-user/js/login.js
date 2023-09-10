let must_login = false;
let countDown = 60;

let loginUuid = '';

let agreeLogin = false;

$(document).ready(function () {

    $(".login-wx-go").click(function () {
        $(".login-wx").css({ "display": "block" });
        $(".login-phone").css({ "display": "none" });
    })

    $(".login-phone-go").click(function () {
        showLoginPhone()
    })

    login(false);

    $(".login-phone-btn").click(function () {
        $.get("../api/login.php?phone=" + $("#login-phone").val() + "&code=" + $("#login-code").val(), function (data) {
            if (data["success"]) {
                login(false);
                loginClose();
                if (must_login) {
                    location.reload();
                }
            } else {
                $(".login-phone-fail").html(data["error"]);
            }
        })
    })

    if (!isMobile() && !nearlyShow()) {
        setTimeout(function () {
            if ($("#login-mark").css("display") != "block" && $("#member-not-login").css("display") != "none") {
                //loginShow();
                //window.localStorage.setItem("login-show", "1")
            }
        }, 5000);
    }

});

function nearlyShow() {
    return window.localStorage.getItem("login-show") == '1'
}

function isMobile() {
    let flag = navigator.userAgent.match(
        /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i
    );
    if (flag == null) {
        return false;
    } else {
        return true;
    }
}

function timeDown() {
    if (countDown === 0) {
        $(".login-phone-send").css("display", "block")
        $(".login-phone-send-wait").css("display", "none")
    } else {
        let wait = $(".login-phone-send-wait");
        wait.html("重发(" + countDown + ")");
        wait.css("display", "block")
        $(".login-phone-send").css("display", "none")
        countDown--;
        setTimeout(function () {
            timeDown()
        }, 1000)
    }
}

function mustLogin() {
    must_login = true;
    login(true);
}

function login(must) {
    let indexMemberLogin = $("#index-member-login");
    $.get("../api/login.php", function (data) {
        if (data["login"]) {
            window.localStorage.removeItem("login-show")

            let memberLogin = $("#member-login");
            memberLogin.css({ "display": "block" });
            $("#member-login img").attr("src", data["pic"])
            $("#member-not-login").css({ "display": "none" });
            $(".head-user").css({ "width": "50px" })
            $(".user-box-name").html(data.name)
            let vipBox = $(".user-box-vip")
            let goVip = $(".user-box-go-vip")
            if (data.vip) {
                vipBox.html("会员" + data.vipTime + "过期")
                if (data.vipTime == '2099-12-31 23:59:59') {
                    goVip.hide()
                } else {
                    goVip.html("续费VIP")
                }
            } else {
                //vipBox.html("未开通会员服务")
                vipBox.html("下载权限至2099-12-31")
                goVip.html("开通VIP")
            }
            if (indexMemberLogin.length > 0) {
                indexMemberLogin.css({ "display": "block" });
                $("#index-member-login img").attr("src", data["pic"])
                $("#index-member-not-login").css({ "display": "none" });
            }
        } else {
            $("#member-login").css({ "display": "none" });
            $("#member-not-login").css({ "display": "block" });
            if (must) {
                loginShow();
            }
            if (indexMemberLogin.length > 0) {
                indexMemberLogin.css({ "display": "none" });
                $("#index-member-not-login").css({ "display": "block" });
            }
        }
    });
}

function loginClose() {
    $("#login-mark").css("display", "none")
}

function loginShow() {
    $("#login-mark").css("display", "block")

    flushLoginCode();

    vaptcha({
        vid: '6006fc9ce9079bac2dd2593a',
        mode: 'invisible',
        scene: 0,
        area: 'auto',
    }).then(function (VAPTCHAObj) {
        obj = VAPTCHAObj;
        VAPTCHAObj.listen('pass', function () {
            serverToken = VAPTCHAObj.getServerToken();
            var data = {
                server: serverToken.server,
                token: serverToken.token,
                phone: $("#login-phone").val()
            }
            console.log(data);
            $.post('../api/phone_code.php', data, function (r) {
                if (r.code === 200) {
                    $(".login-phone-send").css("display", "none")
                    $(".login-phone-send-wait").css("display", "block")
                    countDown = 60;
                    timeDown()
                } else {
                    $(".login-phone-fail").html(r["msg"]);
                }
                VAPTCHAObj.reset()
            })
        })

        $('.login-phone-send').on('click', function () {
            obj.validate();
        })
    })
}

function loginExit() {
    $.get("../api/login.php?exit=1")
    location.reload();
}


function loginAgree() {
    if (agreeLogin) {
        $("#login-agree-icon").css({ 'background-image': 'url("../img/icon/login-no.png")' })
        $(".login-agree-tip").css("display", "block")
        $(".login-phone-btn").css("display", "none")
        $(".login-phone-btn-disable").css("display", "block")
        agreeLogin = false;
    } else {
        $("#login-agree-icon").css({ 'background-image': 'url("../img/icon/login-ok.png")' })
        $(".login-agree-tip").css("display", "none")
        $(".login-phone-btn").css("display", "block")
        $(".login-phone-btn-disable").css("display", "none")
        agreeLogin = true;
    }
}

function flushLoginCode() {
    $.get("../api/login/sunCode.php?time=" + getTime(), function (data) {
        $("#login-qrcode-img").attr("src", data.sunCode)
        loginUuid = data.uuid
        flushLoginState()
    })
}

function flushLoginState() {
    if (loginUuid == '') {
        return;
    }
    if ($("#login-mark").css("display") == "block" && $(".login-wx").css("display") == "block") {
        $.get("../api/login/ajax.php?time=" + getTime() + "&uuid=" + loginUuid, function (data) {
            if (data.type == 0) {
                setTimeout(function () {
                    flushLoginState()
                }, 2000);
            } else if (data.type == 1) {
                window.location.reload()
            } else if (data.type == 2) {
                showLoginPhoneMust()
            }
        })
    } else {
        setTimeout(function () {
            flushLoginState()
        }, 2000);
    }
}

function showLoginPhone() {
    $(".login-wx").css({ "display": "none" });
    $(".login-phone").css({ "display": "block" });
}

function showLoginPhoneMust() {
    showLoginPhone();
    loginByWx();
}

function loginByWx() {
    //隐藏微信登录，展示强制绑定手机
    $(".login-other").css("display", "none")
    $(".login-phone-must").css("display", "block")
    $(".login-phone-fail").css("height", "22px")
}
