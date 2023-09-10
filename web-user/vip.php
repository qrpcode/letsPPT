<?php
require_once 'api/php_tool.php';
$timestamp = msectime();
?>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>开通VIP服务-模板狗</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" type="text/css" href="../css/global.css" />
    <link rel="stylesheet" type="text/css" href="../css/list.css" />
    <link rel="stylesheet" type="text/css" href="../css/inner.css" />
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
                <div class="head-user-box" id="member-login" style="display: block">
                    <img src="../img/touxiang.jpg" alt="我的账户" />
                    <div class="user-box">
                        <div class="user-box-name"></div>
                        <div class="user-box-vip"></div>
                        <a href="../buy/vip.html" target="_blank">
                            <div class="user-box-once">开通VIP</div>
                        </a>
                        <a href="../user.html" target="_blank">
                            <div class="user-box-once">个人中心</div>
                        </a>
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
                <div class="vip-head">
                    <div class="vip-title">超值全站VIP服务</div>
                    <div class="vip-tips">账号所有人自己商用、给自己雇主都可以，更加划算</div>
                </div>
                <div class="vip-choose-box">
                    <div class="vip-choose" onclick="buyForever()" id="forever-choose">
                        <div class="vip-buy-hot">推荐</div>
                        <div class="vip-price">
                            <span class="vip-price-unit">￥</span>199<span class="vip-price-unit">/</span>
                            <span class="vip-price-time">终身</span>
                        </div>
                        <div class="vip-down">20次下载/天</div>
                        <div class="vip-selected" id="forever-selected"></div>
                    </div>
                    <div class="vip-choose" onclick="buyYear()" id="year-choose">
                        <div class="vip-buy-send">买一年,送一年</div>
                        <div class="vip-price">
                            <span class="vip-price-unit">￥</span>99<span class="vip-price-unit">/</span>
                            <span class="vip-price-time">年</span>
                        </div>
                        <div class="vip-down"><span>平均4.2元/月</span>，15次下载/天</div>
                        <div class="vip-selected" id="year-selected"></div>
                    </div>
                    <div class="vip-choose" onclick="buyMonth()" id="month-choose">
                        <div class="vip-price">
                            <span class="vip-price-unit">￥</span>39<span class="vip-price-unit">/</span>
                            <span class="vip-price-time">月</span>
                        </div>
                        <div class="vip-down">2次下载/天</div>
                        <div class="vip-selected" id="month-selected"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="vip-pay-box">
                    <div class="vip-pay">
                        <div class="pay-num">应付金额：<span class="pay-num-price">199</span>元</div>
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
        buyForever()
        mustLogin()
        $(".left-btn-box").hide()
    </script>
</body>

</html>