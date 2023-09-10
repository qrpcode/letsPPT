<?php
if (!isset($_GET['key']) || $_GET['key'] != 'LwDYALNrGuXs8hkp6V2x') {
    echo 'key error';
    exit;
}

header('Content-Type:application/json; charset=utf-8');
require_once '../php_tool.php';

$uuid = md5(microtime().'-'.rand(1000000000,9999999999));
$pptUid = $_GET['uid'];
$channel = $_GET['channel'];

addThirdOrder($uuid, $pptUid, $channel);

$return = [];
$return["uuid"] = $uuid;
$return["sun"] = get3thPayCode($uuid);

echo json_encode($return);

?>