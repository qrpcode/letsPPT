<?php
/**
 * GET参数说明
 * t：1-月卡 2-年卡（实际到账2年） 3-终身卡  -1-单独购买PPT
 * m：用户id
 * p：支付渠道 有ali和wx
 * 
 * 当type=-1的时候有以下参数
 * u：PPT模板id
 */
require_once 'login/token.php';
require_once 'php_tool.php';
require_once 'pay/alipay.php';
require_once 'pay/wxPay.php';

$right = false;

if (isset($_GET["t"]) && isset($_GET["m"]) && isset($_GET["p"])) {
    if (is_numeric($_GET["t"]) && is_numeric($_GET["m"]) && ($_GET["p"] == 'wx' || $_GET["p"] == 'ali')) {
        if (($_GET["t"] == -1 && isset($_GET["u"]) && is_numeric($_GET["u"])) || $_GET["t"] == 1 || $_GET["t"] == 2 || $_GET["t"] == 3) {
            if ($_GET["t"] == -1) {
                beginBuyOncePay($_GET["m"], $_GET["p"], $_GET["u"]);
            } else {
                beginVipPay($_GET["t"], $_GET["m"], $_GET["p"]);
            }
            $right = true;
        }
    }
}

function beginBuyOncePay($memberId, $payWay, $uid) {
    global $oncePrice;

    $member = selectMemberById($memberId);
    if (!isset($member["id"])) {
        echo '用户不合理';
        exit;
    }
    $nowTime = date("Y-m-d H:i:s"); 
    if (strtotime($member["vip_time"]) > strtotime($nowTime)) {
        echo '您已是会员，不需要单独购买';
        exit;
    }
    $pptByUid = searchPPTByUid($uid);
    if (!isset($pptByUid["_source"])) {
        echo '商品不合理';
        exit;
    }
    if (historyBuy($memberId, $uid)) {
        echo '您已经购买过本商品，无需重复购买';
        exit;
    }
    $orderId = insertPPTOrderData($memberId, $payWay, $uid);
    if ($payWay == 'ali') {
        alipay($oncePrice, '模板狗PPT模板下载服务', $orderId);
    } else {
        $openId = getOpenId();
        if ($openId != '') {
            echo json_encode(wxPay($oncePrice, '模板狗PPT模板下载服务', $orderId, $openId));
        }
    }
}

function beginVipPay($type, $memberId, $payWay) {
    global $vip1Price, $vip2Price, $vip3Price;

    $price = 0;
    if ($type == 1) {
        $price = $vip1Price;
    } else if ($type == 2) {
        $price = $vip2Price;
    } else if ($type == 3) {
        $price = $vip3Price;
    }
    $member = selectMemberById($memberId);
    if (!isset($member["id"])) {
        echo '用户不合理';
        exit;
    }

    $orderId = insertVipOrderData($memberId, $payWay, $type, $price);
    if ($payWay == 'ali') {
        alipay($price, '模板狗PPT模板下载服务', $orderId);
    } else {
        $openId = getOpenId();
        if ($openId != '') {
            echo json_encode(wxPay($price, '模板狗PPT模板下载服务', $orderId, $openId));
        }
    }
}

if (!$right) {
    echo '参数不正确';
}

function getOpenId() {
    if (!isset($_GET['code'])) {
        exit;
    }
    return codeGetOpen($_GET["code"]);
}