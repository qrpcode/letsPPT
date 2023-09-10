<xml>
  <return_code><![CDATA[SUCCESS]]></return_code>
  <return_msg><![CDATA[OK]]></return_msg>
</xml>
<?php
$text = file_get_contents("php://input");
require_once '../php_tool.php';

if (!isStrExist($text, '<result_code><![CDATA[SUCCESS]]></result_code>')) {
    exit;
}

$uuid = getBetween($text, '<out_trade_no><![CDATA[', ']]></out_trade_no>');
$tradeNo = getBetween($text, '<transaction_id><![CDATA[', ']]></transaction_id>');
updateOrder($uuid, $tradeNo);

?>