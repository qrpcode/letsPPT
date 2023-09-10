let vipTime = '-1';

function buyForever() {
    $("#forever-selected").css({ "display": "block" });
    $("#year-selected").css({ "display": "none" });
    $("#month-selected").css({ "display": "none" });
    $("#forever-choose").css({ "border": "2px solid #ff7a00" })
    $("#year-choose").css({ "border": "1px solid #E5E5E5" })
    $("#month-choose").css({ "border": "1px solid #E5E5E5" })
    flushPayCode(3)
    flushNumber(199)
}

function buyYear() {
    $("#forever-selected").css({ "display": "none" });
    $("#year-selected").css({ "display": "block" });
    $("#month-selected").css({ "display": "none" });
    $("#forever-choose").css({ "border": "2px solid #ffdbe9" })
    $("#year-choose").css({ "border": "1px solid #ff7a00" })
    $("#month-choose").css({ "border": "1px solid #E5E5E5" })
    flushPayCode(2)
    flushNumber(99)
}

function buyMonth() {
    $("#forever-selected").css({ "display": "none" });
    $("#year-selected").css({ "display": "none" });
    $("#month-selected").css({ "display": "block" });
    $("#forever-choose").css({ "border": "2px solid #ffdbe9" })
    $("#year-choose").css({ "border": "1px solid #E5E5E5" })
    $("#month-choose").css({ "border": "1px solid #ff7a00" })
    flushPayCode(1)
    flushNumber(39)
}

function flushPayCode(type) {
    oldCodeClean()
    $.get("../api/vip.php?type=" + type, function (data) {
        if (!data.login) {
            return;
        }
        $("#wx-pay-code").attr("src", data.wx_qrcode);
        $("#alipay-pay-code").attr("src", data.ali_qrcode);
        flushBuyVipState()
    })
}

function buyOnce(uid) {
    $.get("../api/login.php", function (data) {
        if (!data.vip && data) {
            oldCodeClean();
            $.get("../api/buy.php?uid=" + uid, function (data) {
                if (!data.login) {
                    return;
                }
                $("#wx-pay-code").attr("src", data.wx_qrcode);
                $("#alipay-pay-code").attr("src", data.ali_qrcode);
                
                setTimeout(function () {
                    $(".buy-ok-flush a").css({ "display": "block" });
                }, 4000);
            })
        } else {
            alert("您已经是会员，无需购买即可下载！")
            window.open('../ppt/' + uid + '.html', '_self');
        }
    })
}

function oldCodeClean() {
    $("#wx-pay-code").attr("src", '');
    $("#alipay-pay-code").attr("src", '');
}

function flushNumber(num) {
    $(".pay-num-price").html(num)
}

let needStop = false;
function flushBuyVipState() {
    $.get("../api/login.php", function (data) {
        if (data.login) {
            if (vipTime == '-1') {
                vipTime = data.vipTime
                setTimeout(function () {
                    flushBuyVipState()
                }, 3000);
            } else {
                if (vipTime != data.vipTime) {
                    needStop = true;
                    alert("您已经成功购买VIP，谢谢您！")
                    window.open('../', '_self');
                } else {
                    if (!needStop) {
                        setTimeout(function () {
                            flushBuyVipState()
                        }, 3000);
                    }
                }
            }
        }
    })
}