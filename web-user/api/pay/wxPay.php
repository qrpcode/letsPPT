<?php

function wxPay($payAmount, $orderName, $outTradeNo, $openId) {
    header('Content-Type:application/json; charset=utf-8');
    $signUrl = 'appid=*******************&body='.$orderName.'&mch_id=1636418351&nonce_str=********************&notify_url=https://mubangou.com/api/pay/wx_notify_bHpikYVZszjhUI3E.php&openid='.$openId.'&out_trade_no=' . $outTradeNo . '&sign_type=MD5&spbill_create_ip=81.68.164.140&total_fee='.$payAmount.'&trade_type=JSAPI&key=**************';

    $curl = curl_init();

    curl_setopt_array($curl, array(
    CURLOPT_URL => 'https://api.mch.weixin.qq.com/pay/unifiedorder',
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS =>'<xml>
        <appid>*******************</appid>
        <body>'.$orderName.'</body>
        <mch_id>1636418351</mch_id>
        <nonce_str>*********************</nonce_str>
        <notify_url>https://mubangou.com/api/pay/wx_notify_bHpikYVZszjhUI3E.php</notify_url>
        <openid>'.$openId.'</openid>
        <out_trade_no>'.$outTradeNo.'</out_trade_no>
        <sign_type>MD5</sign_type>
        <sign>'.strtoupper(md5($signUrl)).'</sign>
        <spbill_create_ip>***********************</spbill_create_ip>
        <total_fee>'.$payAmount.'</total_fee>
        <trade_type>JSAPI</trade_type>
    </xml>',
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/xml'
    ),
    ));
    
    $response = curl_exec($curl);

    curl_close($curl);
    
    $wxPay = [];
    $wxPay["package"] = 'prepay_id='.getStrBetween($response, '<prepay_id><![CDATA[', ']]></prepay_id>');
    $wxPay["nonceStr"] = 'gUhRWwvTFTFdyGdzprwd';

    $timeNow = time();
    
    $appPay = 'appId=wx69662b82e934386e&nonceStr=gUhRWwvTFTFdyGdzprwd&package='.$wxPay['package'].'&signType=MD5&timeStamp='.$timeNow.'&key=11f7c67400e7c1f6dcf9e03e0a001a02';
    $wxPay["paySign"] = strtoupper(md5($appPay));
    $wxPay["timeStamp"] = $timeNow;
    return $wxPay;
}

function getStrBetween($content, $start, $end){
    $r = explode($start, $content);
    if (isset($r[1])){
        $r = explode($end, $r[1]);
        return $r[0];
    }
    return '';
}

?>