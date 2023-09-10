<?php
header('Content-Type:application/json; charset=utf-8');
require_once '../php_tool.php';
require_once 'token.php';

$return = [];
$return["msg"] = '请求参数不合法，请求信息已记录';

if (checkTime()) {
    $uuid = createUuid();
    $code = getCode($uuid);
    insertLoginWait($uuid);
    $return["uuid"] = $uuid;
    $return["sunCode"] = $code;
    $return["msg"] = "生成成功";
}

echo json_encode($return);

?>