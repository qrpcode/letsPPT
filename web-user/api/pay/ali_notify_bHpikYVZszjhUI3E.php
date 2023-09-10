<?php
require_once '../php_tool.php';


if (isset($_POST["out_trade_no"]) && isset($_POST["trade_status"]) && isset($_POST["trade_no"])) {
    $uuid = $_POST["out_trade_no"];
    $status = $_POST["trade_status"];
    $tradeNo = safeString($_POST["trade_no"]);
    if ($status == 'TRADE_SUCCESS') {
        updateOrder($uuid, $tradeNo);
    }
}

echo 'success';

?>