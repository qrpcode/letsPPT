let great = [];
let bad = [];

let maxGreat = 501;
let maxBad = 485;

//1-great -1-bad
let nowSeal = 1;

let fileInfo = [];
let allIds = [];

$(document).ready(function () {
    $.get("../api/getAllInfo.php", function (data) {
        fileInfo = data;
        for (let index = 0; index < fileInfo.length; index++) {
            const element = fileInfo[index];
            allIds.push(element.uid)
        }
        flushPPT();
    })
})

function chooseSeal() {
    if (nowSeal == 1) {
        nowSeal = -1;
        $(".great-seal").attr("class", "head-seal great-seal")
        $(".bad-seal").attr("class", "head-seal bad-seal head-seal-hot")
    } else {
        nowSeal = 1;
        $(".great-seal").attr("class", "head-seal great-seal head-seal-hot")
        $(".bad-seal").attr("class", "head-seal bad-seal")
    }
}

function affixSeal(uid) {
    if (nowSeal === 1) {
        if (great.includes(uid)) {
            great.splice(great.indexOf(uid), 1)
        } else {
            great.push(uid)
            if (bad.includes(uid)) {
                bad.splice(bad.indexOf(uid), 1)
            }
        }
    } else {
        if (bad.includes(uid)) {
            bad.splice(bad.indexOf(uid), 1)
        } else {
            bad.push(uid)
            if (great.includes(uid)) {
                great.splice(great.indexOf(uid), 1)
            }
        }
    }
    flushPPT();
}

function flushPPT() {
    html = '';
    for (let i = 0; i < fileInfo.length; i++) {
        const element = fileInfo[i];
        let greatDisplay = "none";
        let badDisplay = "none";
        if (great.includes(element.uid)) {
            greatDisplay = "block";
        }
        if (bad.includes(element.uid)) {
            badDisplay = "block";
        }
        html += '<div class="ppt" onclick="affixSeal(\'' + element.uid + '\')"><div class="ppt-bad-seal" style="display:' + badDisplay + '"></div><div class="ppt-great-seal" style="display:' + greatDisplay + '"></div><div class="ppt-title">' + element.title + '</div><img src="' + element.pic_url + '?imageView2/4/w/500/h/300/format/webp/q/100" alt=""><div class="ppt-id">uid: ' + element.uid + '</div></div>';
    }
    html += '<div class="clear"></div>';
    $(".ppt-box").html(html)
    flushTips()
}

function flushTips() {
    $(".head-tip").html("<p>优质名额剩余: " + (maxGreat - great.length) + "</p><p>作废名额剩余: " + (maxBad - bad.length) + "</p>");
}

function save() {
    $.post('../api/remark.php',
        {
            great: great,
            bad: bad,
            uids: allIds
        },
        function (res) {
            console.log(res);
            alert("完成")
        }
    )
}