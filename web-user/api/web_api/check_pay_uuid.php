<?php

$pptUid = $_GET['uid'];
header('Content-Type:application/json; charset=utf-8');
require_once '../php_tool.php';

$return = [];
$return["buy"] = false;
if (checkTime() && isset($_GET["uuid"])) {
    $uuid = strip_tags(htmlspecialchars(stripslashes(trim($_GET["uuid"]))));
    if (userHistoryBuy($uuid)) {
        $return["buy"] = true;
        $order = select3thOrderByUuid($uuid);
        $return["downLink"] = getDownLink($order['ppt_uid']);
    } 
}
echo json_encode($return);

?>