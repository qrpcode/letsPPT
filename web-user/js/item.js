
$(document).ready(function() {
    $(window).scroll(function(){
        let top = $(document).scrollTop();
        if (top > 300) {
            $("#title-head").css({"display": "block"});
            $("#search-head").css({"display": "none"});
        } else {
            $("#title-head").css({"display": "none"});
            $("#search-head").css({"display": "block"});
        }
    })

    $(".can-down").click(function () {
        downFile()
    })

    $(".source-show-all").click(function() {
        $(".source").css("max-height", "999999px");
        $(".source-show-all").css("display", "none");
    })

    if ($(".source").height() < 300) {
        $(".source-show-all").css("display", "none");
    }

    showDownBox();

    checkCanDown();

})


function showDownBox() {
    $(".left-foot-down i").css({"display": "block"});
    $(".left-foot-about i").css({"display": "none"});
    $(".left-foot-down-box").css({"display": "block"});
    $(".left-foot-about-box").css({"display": "none"});
}

function showAboutBox() {
    $(".left-foot-down i").css({"display": "none"});
    $(".left-foot-about i").css({"display": "block"});
    $(".left-foot-down-box").css({"display": "none"});
    $(".left-foot-about-box").css({"display": "block"});
}

function goComplaint() {
    window.open('https://www.wjx.cn/vm/Pb1okDm.aspx#', '_blank');
}

function checkCanDown() {
    $.get("../api/down.php?now=false&time=" + getTime() + "&uid=" + uid, function (data) {
        if (data.down || data.later) {
            showDownBtn()
        }
    })
}

function downFile() {
    $.get("../api/down.php?now=true&time=" + getTime() + "&uid=" + uid, function (data) {
        if (data.down) {
            window.open(data.downLink, '_blank');
        } else if (data.later) {
            alert("今日下载次数已经用尽")
        }
    })
}

function showDownBtn() {
    $(".can-not-down").css("display", "none")
    $(".can-down").css("display", "block")
    $(".sale").css("height", "60px")
}
