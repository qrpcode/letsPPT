<?php
if (!isset($_GET['key']) || $_GET['key'] != 'LwDYALNrGuXs8hkp6V2x') {
    echo 'key error';
    exit;
}

header('Content-Type:application/json; charset=utf-8');
require_once '../php_tool.php';

$return = [];
$uid = $_GET['uid'];
$channel = $_GET['channel'];

//PPT信息
$pptByUid = searchPPTByUid($uid, $channel);
$ppt = $pptByUid["_source"];
$return['ppt'] = $ppt;

//tag信息
$pptTags = findTags($ppt["tags"])["hits"]["hits"];
$return['tags'] = $pptTags;

//近似推荐
$nearly = nearlyPPT($ppt["title"], $ppt["uid"], $channel)["hits"]["hits"];
$return['nearly'] = $nearly;


echo json_encode($return);
?>