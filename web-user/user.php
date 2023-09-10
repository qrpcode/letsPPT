<?php
require_once 'api/php_tool.php';
$lifeTime = 31 * 24 * 3600;
session_set_cookie_params($lifeTime);
session_start();
$timestamp = msectime();
$ppts = homePPTShow($onceSize * 4)["hits"]["hits"];
$tags = randTag(50)["hits"]["hits"];
$member = [];
$buys = [];
$downs = [];
if ($_SESSION["member_id"]) {
    $member = selectMemberById($_SESSION["member_id"]);
    $memberId = $member["id"];
    $downs = findNearlyDown($memberId);
    $downPPTUids = [];
    while ($downOnce = mysqli_fetch_array($downs)) {
        array_push($downPPTUids, $downOnce["ppt_uid"]);
    }
    $downPPTs = findPPTByUids($downPPTUids)["hits"]["hits"];

    $buys = findNearlyBuy($memberId);
    $buyPPTUids = [];
    while ($buyOnce = mysqli_fetch_array($buys)) {
        array_push($buyPPTUids, $buyOnce["ppt_uid"]);
    }
    $buyPPTs = findPPTByUids($buyPPTUids)["hits"]["hits"];
}
?>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>我的个人中心-模板狗</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" type="text/css" href="../css/global.css?1" />
    <link rel="stylesheet" type="text/css" href="../css/list.css" />
    <link rel="stylesheet" type="text/css" href="../css/inner.css" />
    <script src="../js/jquery.min.js"></script>
    <script src="../js/global.js"></script>
</head>

<body>
    <div class="head" id="search-head">
        <?php echo getNormalHead($tags, 'ppt') ?>
    </div>
    <div class="content">
        <div class="main">
            <div class="designer-box">
                <div class="designer-user-img">
                    <img src="../img/user/user2.png" alt="用户">
                </div>
                <div class="designer-right">
                    <div class="designer-name"><?php echo $member["member_name"] ?></div>
                    <div class="designer-show">
                        <span></span>
                    </div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
        <div class="list-box">
            <h2 class="tab-title"><i></i>最近下载</h2>
            <div class="user-home-tab-box">
                <div class="user-home-tab user-home-tab-1" onclick="downShow()">会员下载</div>
                <div class="user-home-tab user-home-tab-2" onclick="buyShow()">单独购买</div>
                <div class="clear"></div>
            </div>
            <div class="user-home-tip">记录仅展示最近200次下载记录，单独购买后的模板在此处超出不展示的依旧可以下载！</div>
            <div class="ppt-box" style="min-height: 800px;">
                <?php
                if ($downPPTs != null) {
                    for ($j = 0; $j < sizeof($downPPTs); $j++) {
                        $thisPpt = $downPPTs[$j]['_source'];
                        echo '<a href="../ppt/' . $thisPpt['uid'] . '.html" target="_blank" class="down-ppt-list"><div class="ppt"><div class="img-box"><img src="' . $thisPpt['pic_url'] . '?imageView2/4/w/500/h/300/format/webp/q/100" alt="' . $thisPpt['title'] . '"/></div><div class="ppt-go"><div class="ppt-free-btn">会员免费</div><div class="ppt-down-btn">立即下载</div></div><div class="ppt-copyright"><span class="copyright-small">版权</span><span class="copyright-big">版权可溯源<br>使用更安全</span></div><div class="ppt-title">' . $thisPpt['title'] . '</div></div></a>';
                    }
                }
                if ($buyPPTs != null) {
                    for ($j = 0; $j < sizeof($buyPPTs); $j++) {
                        $thisPpt = $buyPPTs[$j]['_source'];
                        echo '<a href="../ppt/' . $thisPpt['uid'] . '.html" target="_blank" class="buy-ppt-list"><div class="ppt"><div class="img-box"><img src="' . $thisPpt['pic_url'] . '?imageView2/4/w/500/h/300/format/webp/q/100" alt="' . $thisPpt['title'] . '"/></div><div class="ppt-go"><div class="ppt-free-btn">会员免费</div><div class="ppt-down-btn">立即下载</div></div><div class="ppt-copyright"><span class="copyright-small">版权</span><span class="copyright-big">版权可溯源<br>使用更安全</span></div><div class="ppt-title">' . $thisPpt['title'] . '</div></div></a>';
                    }
                }
                ?>
                <div class="ppt-box-clear"></div>
            </div>
        </div>

    </div>

    <?php echo getFoot($timestamp); ?>

    <?php echo getLogin(true) ?>

    <div class="black-mark" id="first-use-box" style="display: none">
        <a href="buy/vip.html" target="_blank">
            <div class="first-use"></div>
        </a>
        <i class="mark-close" onclick="$('#first-use-box').css('display', 'none');">×</i>
    </div>

    <!--手势验证sdk-->
    <script src="https://v-cn.vaptcha.com/v3.js"></script>
    <script src="../js/login.js"></script>
    <script>
        mustLogin()
        function downShow() {
            $(".down-ppt-list").css("display", "block")
            $(".buy-ppt-list").css("display", "none")
        }
        function buyShow() {
            $(".down-ppt-list").css("display", "none")
            $(".buy-ppt-list").css("display", "block")
        }
    </script>
</body>

</html>