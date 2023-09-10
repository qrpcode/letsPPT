<?php

require 'api/php_tool.php';
$ppts = homePPTShow(5000)["hits"]["hits"];
$tags = randTag(5000)["hits"]["hits"];

$urls = array(
);
for ($i = 0; $i < sizeof($ppts); $i++) {
    Array_push($urls, 'https://mubangou.com/ppt/' . $ppts[$i]["_source"]["uid"] . '.html');
}
for ($i = 0; $i < sizeof($ppts); $i++) {
    Array_push($urls, 'https://mubangou.com/nearly/' . $ppts[$i]["_source"]["uid"] . '.html');
}
for ($i = 0; $i < sizeof($ppts); $i++) {
    Array_push($urls, 'https://mubangou.com/about/' . $ppts[$i]["_source"]["uid"] . '.html');
}
for ($i = 0; $i < sizeof($tags); $i++) {
    Array_push($urls, 'https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '.html');
}
for ($i = 0; $i < sizeof($tags); $i++) {
    Array_push($urls, 'https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c4.html');
}
for ($i = 0; $i < sizeof($tags); $i++) {
    Array_push($urls, 'https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c5.html');
}
for ($i = 0; $i < sizeof($tags); $i++) {
    Array_push($urls, 'https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c6.html');
}
for ($i = 0; $i < sizeof($tags); $i++) {
    Array_push($urls, 'https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c7.html');
}
for ($i = 0; $i < sizeof($tags); $i++) {
    Array_push($urls, 'https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c8.html');
}
for ($i = 0; $i < sizeof($tags); $i++) {
    Array_push($urls, 'https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c10.html');
}
for ($i = 0; $i < sizeof($tags); $i++) {
    Array_push($urls, 'https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c13.html');
}

$begin = 0;
$array = array_slice($urls, $begin, 1900);
while (sizeof($array) > 0) {
    $api = 'http://data.zz.baidu.com/urls?site=https://mubangou.com&token=4yDyDl3UIVLOFQp1';
    $ch = curl_init();
    $options =  array(
        CURLOPT_URL => $api,
        CURLOPT_POST => true,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POSTFIELDS => implode("\n", $array),
        CURLOPT_HTTPHEADER => array('Content-Type: text/plain'),
    );
    curl_setopt_array($ch, $options);
    $result = curl_exec($ch);
    echo $result;
    $begin = $begin + 1900;
    $array = array_slice($urls, $begin, 1900);
}
?>