<?php
$lifeTime = 31 * 24 * 3600;
session_set_cookie_params($lifeTime);
session_start(); 
require_once 'php_tool.php';
header('Content-Type:application/json; charset=utf-8');

if (is_array($_GET) && count($_GET) > 0) {
    if (isset($_GET["type"])) {
        echo json_encode(buyVip($_GET["type"]));
    } else {
        echo json_encode("{'login': false}");
    }
} else {
    echo json_encode("{'login': false}");
}

function buyVip($type) {
    $return = [];
    if (isset($_SESSION["member_id"])) {
        $return["login"] = true;
        if ($type != 1 && $type != 2 && $type != 3) {
            $type = 1;
        }
        
        $p = "m=". $_SESSION["member_id"] . "&t=" . $type . "&p=";
        $url = "https://mubangou.com/api/pay.php?".$p;
        
        $return["wx_qrcode"] = getPayCode($p.'wx');
        $return["ali_qrcode"] = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" . urlencode($url . 'ali');
    } else {
        $return["login"] = false;
    }
    return $return;
}


?>