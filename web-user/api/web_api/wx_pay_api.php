<?php
header('Content-Type:application/json; charset=utf-8');
require_once '../php_tool.php';
require_once '../pay/wxPay.php';

if (!isset($_GET['uuid']) || !isset($_GET['code'])) {
    echo 'key error';
    exit;
}

$orderId = $_GET['uuid'];
$return = [];

$return['down_now'] = false;

$openId = getOpenId3th();
update3thOrderOpenId($orderId, $openId);

if (userHistoryBuy($orderId, $openId)) {
    $return['down_now'] = true;
    updateOrderCanDown($uuid);
    echo json_encode($return);
    exit;
}

if ($openId != '') {
    $return = wxPay(1, 'PPT模板下载服务', $orderId, $openId);
    $return['down_now'] = false;
    echo json_encode($return);
}

function getOpenId3th() {
    if (!isset($_GET['code'])) {
        exit;
    }
    return codeGetOpen($_GET["code"]);
}
?>