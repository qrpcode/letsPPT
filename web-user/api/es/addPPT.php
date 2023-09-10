<?php

if (!isset($_GET['key']) || $_GET['key'] != 'LwDYALNrGuXs8hkp6V2x') {
    echo 'key error';
    exit;
}

$postStr = file_get_contents("php://input");
addTag($_GET['id'], $postStr);

$uid = json_decode($postStr, true)["uid"];
$channel = json_decode($postStr, true)["channel"];

if ($uid != '' && $channel == 'MBG') {
    $urls = array(
        'https://mubangou.com/ppt/'.$uid.'.html'
    );
    
    $api = 'http://data.zz.baidu.com/urls?site=https://mubangou.com&token=4yDyDl3UIVLOFQp1';
    $ch = curl_init();
    $options =  array(
        CURLOPT_URL => $api,
        CURLOPT_POST => true,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POSTFIELDS => implode("\n", $urls),
        CURLOPT_HTTPHEADER => array('Content-Type: text/plain'),
    );
    curl_setopt_array($ch, $options);
    $result = curl_exec($ch);
    echo $result;
}

function addTag($id, $body) {
    $curl = curl_init();

    curl_setopt_array($curl, array(
    CURLOPT_URL => '127.0.0.1:9200/ppt_info/_doc/'.$id,
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS => $body,
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
    ),
    ));

    $response = curl_exec($curl);

    curl_close($curl);
    //echo $response;
}

?>