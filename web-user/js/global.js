$(document).ready(function () {

    let wrap = $(".wrap");
    let titles = $(".wrap-title-box");
    let links = $(".wrap-link-box");
    let svg = $(".wrap-title svg");

    $(".left-go-top").click(function () {
        $("body,html").animate({
            scrollTop: 0
        }, 400); //1000是滚动到顶部所需要的时间1秒
    })

    $(".wrap-title").click(function () {
        if (wrap.css("height") === '40px') {
            wrapShow(wrap, titles, links, svg);
        } else {
            wrapHide(wrap, titles, links, svg);
        }
    })

    $("#link-box-title-1").hover(function() {
        $("#link-box-1").css("display", "block");
        $("#link-box-2").css("display", "none");
        $("#link-box-3").css("display", "none");
    })

    $("#link-box-title-2").hover(function() {
        $("#link-box-1").css("display", "none");
        $("#link-box-2").css("display", "block");
        $("#link-box-3").css("display", "none");
    })

    $("#link-box-title-3").hover(function() {
        $("#link-box-1").css("display", "none");
        $("#link-box-2").css("display", "none");
        $("#link-box-3").css("display", "block");
    })

    wrapHide(wrap, titles, links, svg);

    $("#head-input").keydown(function (e){
        if (e.keyCode == 13){
            search($("#head-input").val())
        }
    })
    $("#home-big-input").keydown(function (e){
        if (e.keyCode == 13){
            searchHome($("#home-big-input").val())
        }
    })

})

function wrapShow(wrap, titles, links, svg) {
    wrap.css({"height": "auto"});
    titles.css({"display": "block"});
    links.css({"display": "block"});
    svg.css({"transform": "rotate(0)"});
    svg.css({"animation": "0.8s wrap-title-svg-up"})
}

function wrapHide(wrap, titles, links, svg) {
    wrap.css("height", "40px");
    titles.css("display", "none");
    links.css("display", "none");
    svg.css("transform", "rotate(180deg)");
    svg.css("animation", "0.8s wrap-title-svg-down")
}

function colorChoose(num) {
    if (colorList.includes(num)) {
        $(".color-" + num).css("background-image", "none")
        colorList.splice(colorList.indexOf(num), 1)
    } else {
        $(".color-" + num).css("background-image", "url(\"../img/icon/check_mark.png\")")
        colorList.push(num)
    }
}

function colorNoChoose() {
    colorList = [];
    $(".color-tab").css("background-image", "none")
}

function pageChoose(num) {
    if (pageList.includes(num)) {
        $("#page-once-" + num).attr("class", "search-page-once")
        pageList.splice(pageList.indexOf(num), 1)
    } else {
        $("#page-once-" + num).attr("class", "search-page-once-hot")
        pageList.push(num)
    }
}

function goTagUrl() {
    let pageUri = '';
    let colorUri = '';
    pageList.sort();
    colorList.sort();
    if (pageList.length > 0) {
        pageUri = '_p' + pageList.join('-');
    }
    if (colorList.length > 0) {
        colorUri = '_c' + colorList.join('-');
    }
    return pageUri + colorUri;
}

function buildChoose() {
    for (let num of colorList) {
        $(".color-" + num).css("background-image", "url(\"../img/icon/check_mark.png\")")
    }
    for (let num of pageList) {
        $("#page-once-" + num).attr("class", "search-page-once-hot")
    }
}

function buyVip() {
    window.open('../buy/vip.html', '_blank');
}

function homePageChoose(num) {
    if (pageList.includes(num)) {
        $("#home-page-select-" + num).attr("class", "choose-box")
        $("#home-page-select-" + num + " .choose-icon img").attr("src", "img/icon/home_" + num + ".png")
        pageList.splice(pageList.indexOf(num), 1)
    } else {
        $("#home-page-select-" + num).attr("class", "choose-box-hot")
        $("#home-page-select-" + num + " .choose-icon img").attr("src", "img/icon/home_" + num + "_hot.png")
        pageList.push(num)
    }
}

function styleChoose(num) {
    if (styleList.includes(num)) {
        $("#style-" + num).attr("class", "type-once")
        styleList.splice(pageList.indexOf(num), 1)
    } else {
        $("#style-" + num).attr("class", "type-once-hot")
        styleList.push(num)
    }
}

function search(word) {
    $.get("../api/search.php?s=" + encodeURIComponent(word), function(data){
        if (data["haveResult"]) { 
            window.open(data["url"], '_self');
        } else {
            alert(data["msg"]);
        }
    });
}


function searchHome(word) {
    $.get("../api/search.php?s=" + encodeURIComponent(word), function(data){
        if (data["haveResult"]) { 
            let url = data["url"];
            url = url.replace(/.html/, goTagUrl() + '.html')
            window.open(url, '_self');
        } else {
            alert(data["msg"]);
        }
    });
}