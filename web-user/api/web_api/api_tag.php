<?php
if (!isset($_GET['key']) || $_GET['key'] != 'LwDYALNrGuXs8hkp6V2x') {
    echo 'key error';
    exit;
}

header('Content-Type:application/json; charset=utf-8');
require_once '../php_tool.php';

$return = [];
$return["error"] = 0;
$pinyin = $_GET['pinyin'];
$page = $_GET['page'];
$channel = $_GET['channel'];

//tag信息
$tag = findTag($pinyin)["hits"]["hits"];
if (sizeof($tag) == 0) {
    $return["error"] = 1;
    echo json_encode($return);
    exit;
}
$tagName = $tag[0]["_source"]["tag"];
$return['tag'] = $tag[0]["_source"];

//近似Tag推荐
$tags = nearlyTags($tagName)["hits"]["hits"];
$return['nearly'] = $tags;

//PPT结果
$pptsReturn = nearlyPPTByPage($tagName, $page, 30, [], [], $channel);
$return['list'] = $pptsReturn;

echo json_encode($return);
?>