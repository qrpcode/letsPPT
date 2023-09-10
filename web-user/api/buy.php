<?php
$lifeTime = 31 * 24 * 3600;
session_set_cookie_params($lifeTime);
session_start(); 
require_once 'php_tool.php';
require_once 'login/token.php';
header('Content-Type:application/json; charset=utf-8');

if (is_array($_GET) && count($_GET) > 0) {
    if (isset($_GET["uid"])) {
        echo json_encode(buyPPT($_GET["uid"]));
    } else {
        echo json_encode("{'login': false}");
    }
} else {
    echo json_encode("{'login': false}");
}

function buyPPT($uid) {
    $return = [];
    if (isset($_SESSION["member_id"])) {
        $return["login"] = true;

        $p = "m=". $_SESSION["member_id"] . "&t=-1&u=" . $uid. "&p=";
        $url = "https://mubangou.com/api/pay.php?".$p;
        $return["wx_qrcode"] = getPayCode($p.'wx');
        $return["ali_qrcode"] = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" . urlencode($url . 'ali');
    } else {
        $return["login"] = false;
    }
    return $return;
}

?>