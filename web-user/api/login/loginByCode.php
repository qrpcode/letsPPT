<?php
//微信登录成功接口
header('Content-Type:application/json; charset=utf-8');
require_once 'token.php';
require_once '../php_tool.php';

$return = [];
$return["login"] = -1;

if(isset($_GET["code"]) && isset($_GET["uuid"])) {
    $openId = codeGetOpen($_GET["code"]);
    if ($openId != '') {
        $uuid = strip_tags(htmlspecialchars(stripslashes(trim($_GET["uuid"]))));
        $return["login"] = setLoginState($openId, $uuid);
    }
}
echo json_encode($return);


?>