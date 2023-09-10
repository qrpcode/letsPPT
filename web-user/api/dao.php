<?php
require_once 'config.php';
function selectMember($member_id){
    global $config;
    $sql = "select * from member where id = '$member_id'";
    $getUser = mysqli_query($config, $sql);
    $user = mysqli_fetch_array($getUser);
    return $user;
}

function selectMemberByPhone($phone) {
    global $config;
    $sql = "select * from member where phone = '$phone'";
    $getUser = mysqli_query($config, $sql);
    $user = mysqli_fetch_array($getUser);
    return $user;
}

function selectMemberByWxid($wxid) {
    global $config;
    $sql = "select * from member where wxid = '$wxid'";
    $getUser = mysqli_query($config, $sql);
    $user = mysqli_fetch_array($getUser);
    return $user;
}

function updateMemberVipTypeAndTime($type, $time, $memberId) {
    global $config;
    $sql = "update member set vip_type = '$type' , vip_time = '$time' where id = '$memberId'";
    mysqli_query($config, $sql);
}

function insertMember($wxid, $phone, $member_pic, $member_name){
    global $config;
    $sql = "insert into member (wxid, phone, member_pic, member_name) VALUES (\"$wxid\", \"$phone\", \"$member_pic\", \"$member_name\")";
    mysqli_query($config, $sql);
}

function updateMemberWxid($memberId, $wxid) {
    global $config;
    $sql = "update member set wxid = '$wxid' where id = '$memberId'";
    mysqli_query($config, $sql);
}

function select1hourSenddata($phone, $code, $time) {
    global $config;
    $sql = "select * from phone_code where phone_number = '$phone' and checked = 0 and phone_code = '$code' and $time - create_time < 3600000";
    $getCode = mysqli_query($config, $sql);
    $codeData = mysqli_fetch_array($getCode);
    return $codeData;
}

function updateSenddata($id){
    global $config;
    $sql = "update phone_code set checked = '1' where id = '$id'";
    mysqli_query($config, $sql);
}

function select24HourSenddata($phone, $time) {
    global $config;
    $sql = "select COUNT(*) AS count from phone_code where phone_number = '$phone' and create_time - $time < 86400000";
    $getCode = mysqli_query($config, $sql);
    $codeData = mysqli_fetch_array($getCode);
    return $codeData["count"];
}

function select60SecondSenddata($phone, $time) {
    global $config;
    $sql = "select COUNT(*) AS count from phone_code where phone_number = '$phone' and checked = 0 and $time - create_time < 60000";
    $getCode = mysqli_query($config, $sql);
    $codeData = mysqli_fetch_array($getCode);
    return $codeData["count"];
}

function insertPhoneCode($phone, $code, $time){
    global $config;
    $sql = "insert into phone_code (phone_number, phone_code, create_time) VALUES (\"$phone\", \"$code\", \"$time\")";
    mysqli_query($config, $sql);
}

function insertTag($tag){
    global $config;
    $sql = "insert into search_tag (tag) VALUES (\"$tag\")";
    mysqli_query($config, $sql);
    return mysqli_insert_id($config);
}

function selectTagByWord($word) {
    global $config;
    $sql = "select * from search_tag where tag = '$word'";
    $tag = mysqli_query($config, $sql);
    return mysqli_fetch_array($tag);
}

function selectTagById($id) {
    $config = $GLOBALS['config'];
    $sql = "select * from search_tag where id = '$id'";
    $tag = mysqli_query($config, $sql);
    return mysqli_fetch_array($tag);
}

function findLoginWait($uuid) {
    global $config;
    $sql = "select * from login_wait where uuid = '$uuid'";
    $tag = mysqli_query($config, $sql);
    return mysqli_fetch_array($tag);
}

function findLoginWaitById($id) {
    global $config;
    $sql = "select * from login_wait where id = '$id'";
    $tag = mysqli_query($config, $sql);
    return mysqli_fetch_array($tag);
}

function updateLoginOk($wxid, $id) {
    global $config;
    $sql = "update login_wait set login_type = '1', wxid = '$wxid' where id = '$id'";
    mysqli_query($config, $sql);
}

function updateLoginNewUser($wxid, $id) {
    global $config;
    $sql = "update login_wait set login_type = '2' , wxid = '$wxid' where id = '$id'";
    mysqli_query($config, $sql);
}

function insertLoginWaitByUuid($uuid) {
    global $config;
    $sql = "insert into login_wait (uuid) VALUES (\"$uuid\")";
    mysqli_query($config, $sql);
    return mysqli_insert_id($config);
}

function insertPPTOrder($uid, $oncePrice, $memberId, $orderId, $payWay) {
    global $config;
    $sql = "insert into ppt_order (ppt_uid, ppt_price, member_id, uuid, pay_way) VALUES (\"$uid\", \"$oncePrice\", \"$memberId\", \"$orderId\", \"$payWay\")";
    mysqli_query($config, $sql);
    return mysqli_insert_id($config);
}

function selectPPTOrder($memberId, $uid) {
    global $config;
    $sql = "select * from ppt_order where pay_type = 1 and ppt_uid = '$uid' and member_id = '$memberId'";
    $tag = mysqli_query($config, $sql);
    return mysqli_fetch_array($tag);
}

function selectPPTOrderByUuid($uuid) {
    global $config;
    $sql = "select * from ppt_order where uuid = '$uuid'";
    $tag = mysqli_query($config, $sql);
    return mysqli_fetch_array($tag);
}

function updatePPTOrderPay($uuid, $orderId) {
    global $config;
    $sql = "update ppt_order set pay_type = '1', pay_order_id = '$orderId' where uuid = '$uuid'";
    mysqli_query($config, $sql);
}

function insertVipOrder($vipType, $vipPrice, $memberId, $orderId, $payWay) {
    global $config;
    $sql = "insert into vip_order (vip_type, vip_price, member_id, uuid, pay_way) VALUES (\"$vipType\", \"$vipPrice\", \"$memberId\", \"$orderId\", \"$payWay\")";
    mysqli_query($config, $sql);
    return mysqli_insert_id($config);
}

function selectVipOrderByUuid($uuid) {
    global $config;
    $sql = "select * from vip_order where uuid = '$uuid'";
    $tag = mysqli_query($config, $sql);
    return mysqli_fetch_array($tag);
}

function updateVipOrderPay($uuid, $orderId) {
    global $config;
    $sql = "update vip_order set pay_type = '1' , pay_order_id = '$orderId' where uuid = '$uuid'";
    mysqli_query($config, $sql);
}

function selectDesigner($id){
    global $config;
    $sql = "select * from designer where id = '$id'";
    $getDesigner = mysqli_query($config, $sql);
    return mysqli_fetch_array($getDesigner);
}

function findTodayDown($memberId) {
    global $config;
    $sql = "select COUNT(*) AS count from down_log where member_id = '$memberId' and DATEDIFF(create_time, NOW())=0";
    $getCode = mysqli_query($config, $sql);
    $codeData = mysqli_fetch_array($getCode);
    return $codeData["count"];
}

function insertDownLog($memberId, $uid) {
    global $config;
    $sql = "insert into down_log (member_id, ppt_uid) VALUES (\"$memberId\", \"$uid\")";
    mysqli_query($config, $sql);
    return mysqli_insert_id($config);
}

function selectNearlyDown($memberId) {
    global $config;
    $sql = "select * from down_log where member_id = '$memberId' order by id desc limit 200";
    return mysqli_query($config, $sql);
}

function selectNearlyBuy($memberId) {
    global $config;
    $sql = "select * from ppt_order where pay_type = 1 and member_id = '$memberId' order by id desc limit 200";
    return mysqli_query($config, $sql);
}

function insertThirdOrder($uuid, $pptUid, $channel) {
    global $config;
    $sql = "insert into third_order (uuid, ppt_uid, channel) VALUES (\"$uuid\", \"$pptUid\", \"$channel\")";
    mysqli_query($config, $sql);
    return mysqli_insert_id($config);
}

function selectThirdByUuid($uuid) {
    global $config;
    $sql = "select * from third_order where uuid = '$uuid'";
    $getDesigner = mysqli_query($config, $sql);
    return mysqli_fetch_array($getDesigner);
}

function selectHistory($openId, $uid) {
    global $config;
    $sql = "select * from third_order where open_id = '$openId' and ppt_uid = '$uid' and pay_state = 1";
    $getDesigner = mysqli_query($config, $sql);
    return mysqli_fetch_array($getDesigner);
}

function updateThirdOrderState1($uuid) {
    global $config;
    $sql = "update third_order set pay_state = '1' where uuid = '$uuid'";
    mysqli_query($config, $sql);
}

function update3thOpenId($uuid, $openId) {
    global $config;
    $sql = "update third_order set open_id = '$openId' where uuid = '$uuid'";
    mysqli_query($config, $sql);
}

?>