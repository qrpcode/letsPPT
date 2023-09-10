<?php
$lifeTime = 31 * 24 * 3600;
session_set_cookie_params($lifeTime);
session_start(); 
require_once 'php_tool.php';
header('Content-Type:application/json; charset=utf-8');

$return = [];
$return["down"] = false;
$return["later"] = false;
$return["msg"] = '不合理请求';

$downNow = false;
if (!checkTime()) {
    echo json_encode($return);
    exit;
}

if (isset($_GET["now"]) && $_GET["now"] == 'true') {
    $downNow = true;
}

if (isset($_GET["uid"]) && is_numeric($_GET["uid"])) {
    $uid = $_GET["uid"];
    if (isset($_SESSION["member_id"])) {
        $member = selectMemberById($_SESSION["member_id"]);
        $canDown = false;
        $type = 0;
        $nowVip = true;//strtotime($member["vip_time"]) > strtotime($nowTime);
        /*if ($nowVip) {
            if (todayDownMax($member)) {
                $return["later"] = true;
                $return["msg"] = "今日已经上限";
            } else {
                $canDown = true;
            }
        }*/
        $canDown = true;
        if (!$canDown) {
            $canDown = historyBuy($member["id"], $uid);
            $type = 1;
        }
        $return["down"] = $canDown;
        if (!$canDown) {
            $return["msg"] = "还没有购买";
        } else {
            $return["msg"] = "允许下载";
            if ($downNow) {
                $return["downLink"] = getDownLink($uid);   
                //只有VIP下载才记录
                if ($type == 0) {
                    addDownLog($member["id"], $uid);
                }
            }
        }
    } else {
        $return["msg"] = '用户未登录';
    }
}

echo json_encode($return);

?>