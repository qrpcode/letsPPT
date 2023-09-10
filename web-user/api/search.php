<?php
header('Content-Type:application/json; charset=utf-8');
$return = [];
$return["haveResult"] = false;
$return["msg"] = "写点什么在提交吧!";

require_once 'token.php';
require_once 'php_tool.php';

if (!isset($_GET["s"]) || trim($_GET["s"]) == '') {
    echo json_encode($return);
    exit;
}
$str = strip_tags(htmlspecialchars(stripslashes(trim($_GET["s"]))));

//审核不合法
if (mb_strlen($str) > 15) {
    $return["msg"] = "内容太长了，缩短点在查询吧!";
    echo json_encode($return);
    exit;
}

/*
$isCompliance = isCompliance($str, $token);
if ($isCompliance["conclusionType"] != 1) {
    $return["msg"] = "提交内容不合法!";
    echo json_encode($return);
    exit;
}*/

$return["msg"] = '';

//审核通过，先查有没有命中的Tag
$tags = findTagByStr($str)["hits"]["hits"];
if (sizeof($tags) > 0) {
    $return["haveResult"] = true;
    $return["url"] = getHost() . 'tag/' . $tags[0]["_source"]["pinyin"] . '.html';
    echo json_encode($return);
    exit;
}

//检查是不是包含
$tagFind = findTagByWord($str);
if (isset($tagFind["id"])) {
    $return["haveResult"] = true;
    $return["url"] = getHost() . 'tag/s-' . $tagFind["id"] . '.html';
    echo json_encode($return);
    exit;
}

//没有Tag，就需要新建一组
$id = addTag($str);
$return["haveResult"] = true;
$return["url"] = getHost() . 'tag/s-' . $id . '.html';
echo json_encode($return);

    /*

function isCompliance($str, $token){
    $curl = curl_init();
    curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, false);
    curl_setopt_array($curl, array(
    CURLOPT_URL => 'https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined?access_token='.$token,
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS => 'text='.urlencode($str),
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/x-www-form-urlencoded'
    ),
    ));

    $response = curl_exec($curl);

    curl_close($curl);
    return json_decode($response, true);
    
    
}*/

?>