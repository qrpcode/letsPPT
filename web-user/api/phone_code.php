<?php
$lifeTime = 31 * 24 * 3600;
session_set_cookie_params($lifeTime);
session_start(); 
require_once 'php_tool.php';
header('Content-Type:application/json; charset=utf-8');

$error = [];
$error["code"] = 500;

if (is_array($_POST) && count($_POST) > 0) {
    if (isset($_POST["phone"]) && isset($_POST["token"]) && isset($_POST["server"])) {
        $return = [];
        $msg = sendPhoneCode($_POST["phone"], $_POST["token"], $_POST["server"]);
        if ($msg == null) {
            $return["code"] = 200;
        } else {
            $return["code"] = 500;
            $return["msg"] = $msg;
        }
        echo json_encode($return);
    } else {
        echo json_encode($error);
    }
} else {
    echo json_encode($error);
}



?>