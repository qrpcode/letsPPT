<?php
session_start(); 
require_once 'php_tool.php';
header('Content-Type:application/json; charset=utf-8');

if (is_array($_GET) && count($_GET) > 0) {
    if (isset($_GET["exit"])) {
        unset($_SESSION["member_id"]);
        unset($_SESSION["wait_id"]);
        return json_encode('{}');
    }
    if (isset($_GET["phone"]) && isset($_GET["code"])) {
        $phone = $_GET["phone"];
        $code = $_GET["code"];
        echo json_encode(loginByPhone($phone, $code));
    } else {
        echo json_encode(login());
    }
} else {
    echo json_encode(login());
}

function login() {
    $return = [];
    if (isset($_SESSION["member_id"])) {
        $member = selectMemberById($_SESSION["member_id"]);
        $nowTime = date("Y-m-d H:i:s"); 
        $return["vipTime"] = $member["vip_time"];
        $return["vip"] = strtotime($member["vip_time"]) > strtotime($nowTime);
        $return["login"] = true;
        $return["name"] = $member["member_name"];
        $return["pic"] = $member["member_pic"];
    } else {
        $return["login"] = false;
    }
    return $return;
}

function loginByPhone($phone, $code) {
    $return = [];
    if (!isPhoneNumber($phone)) {
        $return["success"] = false;
        $return["error"] = "手机号格式不正确";
        return $return;
    }
    if (!is_numeric($code)) {
        $return["success"] = false;
        $return["error"] = "验证码格式不正确";
        return $return;
    }
    if (checkPhoneCode($phone, $code)) {
        if (isNewUser($phone)) {
            insertUser($phone, $_SESSION["wait_id"]);
        }
        $member = selectMemberByPhone($phone);
        maybeNeedBinding($_SESSION["wait_id"], $member);
        $return["success"] = true;
        $return["member_name"] = $member["member_name"];
        $return["member_pic"] = $member["member_pic"];
        $return["nearly_region"] = isNearlyRegion($member["create_time"]);
        $return["mark"] = "";
        $_SESSION["member_id"] = $member["id"];
    } else {
        $return["success"] = false;
        $return["error"] = "验证码错误";
    }
    return $return;
}

?>