<?php
$lifeTime = 31 * 24 * 3600;
session_set_cookie_params($lifeTime);
require_once '../php_tool.php';

header('Content-Type:application/json; charset=utf-8');
session_start(); 

$return = [];
$return["type"] = 0;

if (checkTime() && isset($_GET["uuid"])) {
    if (isset($_SESSION["member_id"])) {
        $return["type"] = -1;
        echo json_encode($return);
        exit;
    }
    $uuid = strip_tags(htmlspecialchars(stripslashes(trim($_GET["uuid"]))));
    $loginWait = selectLoginWait($uuid);
    if (isset($loginWait['login_type'])) {
        if ($loginWait['login_type'] == 1) {
            $member = findMemberByWxid($loginWait['wxid']);
            $_SESSION["member_id"] = $member["id"];
        } else if ($loginWait['login_type'] == 2) {
            $_SESSION["wait_id"] = $loginWait['id'];
        }
        $return['type'] = $loginWait['login_type'];
    }
}

echo json_encode($return);
