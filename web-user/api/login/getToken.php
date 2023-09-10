<?php
$tokenGet = file_get_contents("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=**********&secret=****************");
$accessToken = getSubstr($tokenGet, '"access_token":"', '","');
file_put_contents("token.php",'<?php $wxToken = "'.$accessToken.'"; ?>');

function getSubstr($str, $leftStr, $rightStr)
{
    $left = strpos($str, $leftStr);
    $right = strpos($str, $rightStr,$left);
    if($left < 0 or $right < $left) return '';
    return substr($str, $left + strlen($leftStr), $right-$left-strlen($leftStr));
}
?>