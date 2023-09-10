<?php
require 'api/php_tool.php';
$timestamp = msectime();
$uid = findBuyPageUid();
if ($uid == 0) {
    require '404.php';
    exit;
}
$ppt = searchPPTByUid($uid)["_source"];
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>购买模板-模板狗</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" type="text/css" href="../css/global.css"/>
    <link rel="stylesheet" type="text/css" href="../css/list.css"/>
    <link rel="stylesheet" type="text/css" href="../css/inner.css"/>
    <script src="../js/jquery.min.js"></script>
    <script src="../js/global.js"></script>
    <script src="../js/buy.js"></script>
</head>
<body>
<div class="head" id="search-head">
    <div class="head-main">
        <div class="head-logo">
            <a href="https://mubangou.com" target="_blank">
                <img src="../img/logo-inner.png" alt="模板狗" class="head-logo-pic">
            </a>
        </div>
        <div class="head-user">       
            <div class="head-user-box" id="member-login"  style="display: block">
                <img src="../img/touxiang.jpg" alt="我的账户"/>
                <div class="user-box">
                        <div class="user-box-name"></div>
                        <div class="user-box-vip"></div>
                    <a href="../buy/vip.html" target="_blank"><div class="user-box-once">开通VIP</div></a>
                    <a href="../user.html" target="_blank"><div class="user-box-once">个人中心</div></a>
                    <div class="user-box-once" onclick="loginExit()">退出</div>
                </div>
            </div>
            <div class="head-login" onclick="loginShow()" id="member-not-login" style="display: none">
                <div class="head-login-region">注册</div>
                <div class="head-login-login">登录</div>
            </div>
        </div>
    </div>
</div>
<div class="content">
    <div class="main">
        <div class="vip-buy-box">
            <div class="item-box">
                <div class="item-left">
                    <img src="<?php echo $ppt["pic_url"] ?>" alt="<?php echo $ppt["title"] ?>">
                </div>
                <div class="item-right">
                    <div class="item-title"><?php echo $ppt["title"] ?></div>
                    <div class="item-price">单独购买价格: ￥<span>18.9</span></div>
                </div>
            </div>
            <div class="buy-ok-flush">
                <a href="../ppt/<?php echo $uid?>.html">已支付完成？点击这里返回页面下载</a>
            </div>
            <div class="vip-pay-box">
                <div class="vip-pay">
                    <div class="pay-num">应付金额：<span class="pay-num-price">18.9</span>元</div>
                    <div class="pay-ways">
                        <div class="pay-way-wx">
                            <div class="pay-way-wx-qrcode">
                                <img id="wx-pay-code" src="" alt="微信支付">
                            </div>
                            <div class="pay-way-wx-title">微信支付</div>
                        </div>
                        <div class="pay-way-alipay">
                            <div class="pay-way-alipay-qrcode">
                                <img id="alipay-pay-code" src="" alt="支付宝支付">
                            </div>
                            <div class="pay-way-alipay-title">支付宝支付</div>
                        </div>
                    </div>
                    <div class="phone-pay-way">
                        <div class="phone-bay-alipay">支付宝付款</div>
                        <div class="phone-bay-wx">微信付款</div>
                    </div>
                </div>
            </div>
            <div class="vip-60height-box"></div>
        </div>
        <div class="vip-foot">
            <p>温馨提示：为保护版权，您下载的所有文件均会嵌入基于OpenXML的无感文件下载者溯源水印，正常使用过程完全无感。下载问题可直接联系网站客服；</p>
            <p>网站设置防盗刷系统，请合理使用下载账号。如用户短时间内高频或大量下载可能触发该系统，将暂时暂停账号下载功能，如有疑问请咨询网站客服。</p>
        </div>
    </div>
</div>

<?php echo getFoot($timestamp);?>

<?php echo getLogin(true)?>

<script src="https://v-cn.vaptcha.com/v3.js"></script>
<script src="../js/login.js"></script>
<script>
    buyOnce("<?php echo $uid?>")
    mustLogin()
    $(".left-btn-box").hide()
</script>
</body>
</html>