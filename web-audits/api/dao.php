<?php
require_once 'config.php';

function findAllNotSeal() {
    global $config;
    $sql = "select * from file_ppt_info where upload = 0";
    $ppts = mysqli_query($config, $sql);
    $pptList = [];
    while ($pptOnce = mysqli_fetch_array($ppts)) {
        $ppt = [];
        $ppt["id"] = $pptOnce["id"];
        $ppt["uid"] = $pptOnce["uid"];
        $ppt["title"] = $pptOnce["title"];
        $ppt["pic_url"] = $pptOnce["pic_url"];
        array_push($pptList, $ppt);
    }
    return $pptList;
}

function updateAllCheck($uids) {
    global $config;
    $sql = "UPDATE `file_ppt_info` SET upload = 1 WHERE `uid` IN ".buildOrStr($uids);

    mysqli_query($config, $sql);
}

function updateGreatCheck($uids) {
    global $config;
    $sql = "UPDATE `file_ppt_info` SET upload = 2 WHERE `uid` IN ".buildOrStr($uids);

    mysqli_query($config, $sql);
}

function updateBadCheck($uids) {
    global $config;
    $sql = "UPDATE `file_ppt_info` SET upload = -1 WHERE `uid` IN ".buildOrStr($uids);

    mysqli_query($config, $sql);
}

function buildOrStr($array) {
    return '(\'-1\', \''.implode("','", $array).'\')';
}
