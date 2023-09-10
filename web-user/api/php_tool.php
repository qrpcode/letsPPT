<?php
date_default_timezone_set('PRC');
require_once 'dao.php';
require_once 'host.php';
require_once 'login/token.php';
$oncePrice = 1890;
$vip1Price = 3900;
$vip2Price = 9900;
$vip3Price = 19900;

function getListPageSize() {
    return 32;
}

function findPPTUid() {
    if (!isset($_GET["s"])) {
        return null;
    }
    $uid = getBetween($_GET["s"], 'ppt/', '.html');
    return is_numeric($uid) ? $uid : '0';
}

function findBuyPageUid() {
    if (!isset($_GET["s"])) {
        return null;
    }
    $uid = getBetween($_GET["s"], 'buy/', '.html');
    return is_numeric($uid) ? $uid : '0';
}

function analysisAboutLink() {
    return analysisUidAndPage('about');
}

function analysisNearlyLink() {
    return analysisUidAndPage('nearly');
}

function analysisDesignerLink() {
    return analysisUidAndPage('designer');
}

function analysisUidAndPage($from) {
    if (!isset($_GET["s"])) {
        return null;
    }
    $uri = getBetween($_GET["s"], $from.'/', '.html');
    $uriArray = explode('_', $uri);
    $return = [];
    $return["uid"] = '';
    $return["page"] = 1;
    if (sizeof($uriArray) > 0) {
        $return["uid"] = $uriArray[0];
    }
    if (sizeof($uriArray) > 1 && is_numeric($uriArray[1])) {
        $return["page"] = $uriArray[1];
    }
    return $return;
}

function analysisTagLink() {
    $return = [];
    $return["page"] = 1;
    $return["pageFilter"] = '';
    $return["colorFilter"] = '';
    $return["pageTitle"] = '';
    $return["colorTitle"] = '';
    $return["headTitle"] = '';
    $return["pageFilterArray"] = [];
    $return["colorFilterArray"] = [];
    $uri = getBetween($_GET["s"], 'tag/', '.html');
    $uriArray = explode('_', $uri);
    if (sizeof($uriArray) > 0) {
        $first = $uriArray[0];
        $return["pinyin"] = $first;
        if (startWith($first, 's-')) {
            $return["searchId"] = str_replace('s-', '', $first);
            if (!is_numeric($return["searchId"])) {
                return null;
            }
        }
    }
    for ($i = 1; $i < sizeof($uriArray); $i++) {
        if ($uriArray[$i] == '') {
            continue;
        }
        if (is_numeric($uriArray[$i])) {
            $return["page"] = $uriArray[$i];
        }
        if (startWith($uriArray[$i], 'p')) {
            $return["pageFilter"] = str_replace('p', '', $uriArray[$i]);
            $return["pageFilter"] = str_replace('-', ',', $return["pageFilter"]);
        }
        if (startWith($uriArray[$i], 'c')) {
            $return["colorFilter"] = str_replace('c', '', $uriArray[$i]);
            $return["colorFilter"] = str_replace('-', ',', $return["colorFilter"]);
        }
    }
    if ($return["pageFilter"] != '') {
        $pages = explode(',', $return["pageFilter"]);
        $newPages = [];
        for ($i = 0; $i < sizeof($pages); $i++) { 
            if ($pages[$i] <= 9 && $pages[$i] >= 1) {
                Array_push($newPages, $pages[$i]);
            }
        }
        sort($newPages);
        $return["pageFilterArray"] = $newPages;
        $return["pageFilter"] = implode(',', $newPages);
        $return["pageTitle"] = getPageTitle($newPages);
    }
    if ($return["colorFilter"] != '') {
        $colors = explode(',', $return["colorFilter"]);
        $newColors = [];
        for ($i = 0; $i < sizeof($colors); $i++) { 
            if (($colors[$i] <= 17 && $colors[$i] >= 4) || $colors[$i] == 2) {
                Array_push($newColors, $colors[$i]);
            }
        }
        sort($newColors);
        $return["colorFilterArray"] = $newColors;
        $return["colorFilter"] = implode(',', $newColors);
        $return["colorTitle"] = getColorTitle($newColors);
    }
    if ($return["colorTitle"] != '' || $return["pageTitle"] != '') {
        if ($return["pageTitle"] != '') {
            $return["headTitle"] = '有' . $return["pageTitle"];
        }
        if ($return["headTitle"] != '') {
            $return["headTitle"] = $return["headTitle"] . '的';
        }
        if ($return["colorTitle"] != '') {
            $return["headTitle"] = $return["headTitle"] . $return["colorTitle"] . '的';
        }
    }
    return $return;
}


function startWith($str, $needle) {
    return strpos($str, $needle) === 0;    
}

function getPageTitle($pages) {
    if (sizeof($pages) == 0) {
        return '';
    }
    $str = implode('和', $pages);
    $str = str_replace('9', '荣誉页', $str);
    $str = str_replace('8', '金字塔', $str);
    $str = str_replace('7', '引言页', $str);
    $str = str_replace('6', '对比介绍', $str);
    $str = str_replace('5', '时间轴', $str);
    $str = str_replace('4', '组织架构', $str);
    $str = str_replace('3', '扇形图', $str);
    $str = str_replace('2', '柱状图', $str);
    $str = str_replace('1', '折线图', $str);
    return $str;
}

function getColorTitle($colors) {
    if (sizeof($colors) == 0) {
        return '';
    }
    $str = implode('或', $colors);
    $str = str_replace('17', '灰色', $str);
    $str = str_replace('16', '棕色', $str);
    $str = str_replace('15', '浅棕色', $str);
    $str = str_replace('14', '橙色', $str);
    $str = str_replace('13', '金色', $str);
    $str = str_replace('12', '杏色', $str);
    $str = str_replace('11', '浅绿色', $str);
    $str = str_replace('10', '绿色', $str);
    $str = str_replace('9', '青色', $str);
    $str = str_replace('8', '蔚蓝色', $str);
    $str = str_replace('7', '深蓝色', $str);
    $str = str_replace('6', '紫色', $str);
    $str = str_replace('5', '粉红色', $str);
    $str = str_replace('4', '红色', $str);
    $str = str_replace('2', '黑色', $str);
    return $str;
}

function getPageChooseLink($uriData, $pageId) {
    $pages = $uriData["pageFilterArray"];
    if (in_array($pageId, $pages)) {
        $pages = delByValue($pages, $pageId);
    } else {
        array_push($pages, $pageId);
    }
    sort($pages);
    return $uriData['pinyin'] . getLinkUriByArray($uriData["colorFilterArray"], $pages) . '.html';
}

function getColorChooseLink($uriData, $colorId) {
    $colors = $uriData["colorFilterArray"];
    if (in_array($colorId, $colors)) {
        $colors = delByValue($colors, $colorId);
    } else {
        array_push($colors, $colorId);
    }
    sort($colors);
    return $uriData['pinyin'] . getLinkUriByArray($colors, $uriData["pageFilterArray"]) . '.html';
}

function getLinkUri($uriData) {
    return getLinkUriByArray($uriData["colorFilterArray"], $uriData["pageFilterArray"]);
}

function getLinkUriByArray($colorFilterArr, $pageFilterArr) {
    sort($colorFilterArr);
    sort($pageFilterArr);
    $uri = '';
    if (sizeof($colorFilterArr) > 0) {
        $uri = $uri . '_c' . implode('-', $colorFilterArr);
    }
    if (sizeof($pageFilterArr) > 0) {
        $uri = $uri . '_p' . implode('-', $pageFilterArr);
    }
    return $uri;
}

function getBetween($content, $start, $end){
    $r = explode($start, $content);
    if (isset($r[1])){
        $r = explode($end, $r[1]);
        return $r[0];
    }
    return '';
}

function searchPPTByUid($uid, $channel = '') {
    $curl = curl_init();

    curl_setopt_array($curl, array(
    CURLOPT_URL => 'http://127.0.0.1:9200/ppt_info/_doc/' . $uid,
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'GET',
    ));

    $response = curl_exec($curl);

    curl_close($curl);
    $return = json_decode($response, true);
    if ($return["_source"]["channel"] == $channel || $channel == '') {
        return $return;
    }
    return null;
}

function nearlyPPT($title, $uid, $channel = "MBG") {
    $curl = curl_init();

    curl_setopt_array($curl, array(
    CURLOPT_URL => 'http://127.0.0.1:9200/ppt_info/_search',
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'GET',
    CURLOPT_POSTFIELDS =>'{
    "query": {
        "bool": {
        "must": [
            {
                "match": {
                    "title": "' . $title . '"
                }
            },
            {
                "match": {
                    "channel.keyword": "' . $channel . '"
                }
            }
        ],
        "must_not": [
            {
            "match": {
                "uid": "' . $uid . '"
            }
            }
        ]
        }
    },
    "size": 33
    }',
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
    ),
    ));

    $response = curl_exec($curl);

    curl_close($curl);
    return json_decode($response, true);
}

function nearlyPPTByPage($tag, $pageNum, $size, $colors, $pages, $channel = "MBG") {
    $begin = $size * ($pageNum - 1);

    $search = '{"query": {"bool": {"must": [{"match": {"title": "' . $tag . '"}},{"match": {"channel": "' . $channel . '"}}';

    $colorStr = '';
    if ($colors != null && sizeof($colors) > 0) {
        $colorStr = '{"bool": {"should": [';
        for ($i = 0; $i < sizeof($colors); $i++) {
            if ($i == sizeof($colors) - 1) {
                $colorStr = $colorStr . '{"match": {"color_type": ' . $colors[$i] . '}}';
            } else {
                $colorStr = $colorStr . '{"match": {"color_type": ' . $colors[$i] . '}},';
            }
        }
        $colorStr = $colorStr . ']}}';

        $search = $search.','. $colorStr;
    }

    $pageStr = '';
    if ($pages != null && sizeof($pages) > 0) {
        $pageStr = '{"bool": {"should": [';
        for ($i = 0; $i < sizeof($pages); $i++) {
            $pageArr = [$pages[$i]];
            if ($i == sizeof($pages) - 1) {
                $pageStr = $pageStr . '{"match": {"about_text": "'.getPageTitle($pageArr).'"}}';
            } else {
                $pageStr = $pageStr . '{"match": {"about_text": "'.getPageTitle($pageArr).'"}},';
            }
        }
        $pageStr = $pageStr . ']}}';

        $search = $search.','. $pageStr;
    }

    $search = $search . ']}},"size": ' . $size . ',"from": ' . $begin . '}';

    $curl = curl_init();

    curl_setopt_array($curl, array(
      CURLOPT_URL => 'http://127.0.0.1:9200/ppt_info/_search',
      CURLOPT_RETURNTRANSFER => true,
      CURLOPT_ENCODING => '',
      CURLOPT_MAXREDIRS => 10,
      CURLOPT_TIMEOUT => 0,
      CURLOPT_FOLLOWLOCATION => true,
      CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
      CURLOPT_CUSTOMREQUEST => 'POST',
      CURLOPT_POSTFIELDS => $search,
      CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
      ),
    ));
    
    $response = curl_exec($curl);
    
    curl_close($curl);
    return json_decode($response, true);
    
}

function homePPTShow($size) {
    $curl = curl_init();

    curl_setopt_array($curl, array(
    CURLOPT_URL => 'http://127.0.0.1:9200/ppt_info/_search',
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'GET',
    CURLOPT_POSTFIELDS =>'{
    "query": {
        "bool": {
        "must": [
            {
            "match": {
                "title": "PPT模板"
            }
            },
            {
            "match": {
                "channel": "MBG"
            }
            }
        ]
        }
    },
    "size": ' . $size . ',
    "sort": {
      "uid.keyword": {
        "order": "desc"
      }
    }
    }',
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
    ),
    ));

    $response = curl_exec($curl);

    curl_close($curl);
    return json_decode($response, true);
}

function findTags($tagsStr) {
    $tags = explode(',', $tagsStr);
    $json = '';
    for($i = 0; $i < sizeof($tags); $i++) {
        if ($i != 0) {
            $json = $json.',';
        }
        $json = $json.'{"match": {"_id": ' . $tags[$i] . '}}';
    }

    $curl = curl_init();

    curl_setopt_array($curl, array(
    CURLOPT_URL => 'http://127.0.0.1:9200/ppt_tag/_search',
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS =>'{
        "query": {
            "bool": {
                "must": [
                    {
                        "bool": {
                            "should": [' . $json . ']
                        }
                    }
                ]
            }
        }
    }',
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
    ),
    ));

    $response = curl_exec($curl);
    curl_close($curl);
    return json_decode($response, true);
}

function msectime() {
    list($msec, $sec) = explode(' ', microtime());
    $msectime = (float)sprintf('%.0f', (floatval($msec) + floatval($sec)) * 1000);
    return intval($msectime);
}

function sendPhoneCode($phone, $token, $server) {
    if (isPhoneNumber($phone)) {
        if (!isRightServer($server)) {
            return "验证地址不合理，请联系客服";
        }
        $vaptcha = checkCode($token, $server);
        if ($vaptcha["success"] != 1) {
            return "验证码校验失败，请重新验证";
        }
        $sendNumber = select24HourSenddata($phone, msectime());
        if ($sendNumber > 10) {
            return "此手机号24小时内已经发送超过10次，不能继续发送";
        }
        $sendNumber = select60SecondSenddata($phone, msectime());
        if ($sendNumber > 0) {
            return "60秒内发送过验证码，再等等看";
        }
        $code = getSendNum();
        sendPhoneMsgCode($phone, $code, $token);
        insertPhoneCode($phone, $code, msectime());
        return null;
    } else {
        return "手机号码格式不正确";
    }
}

function sendPhoneMsgCode($phone, $code, $token) {
    $url  = "http://sms.vaptcha.com/send";
    $body = array(
        'smsid' => '******************',
        'smskey' => '********************',
        'templateid' => '1',
        'countrycode' => '86',
        'token' => $token,
        'data' => [$code], // 与模板中的 {变量} 一一对应
        'phone' => $phone,
    );
    $header = array(
        "Content-type:application/json;charset='utf-8'",
        "Accept:application/json"
    );
    $res = curl_post($url,json_encode($body),$header);
}

function curl_post($url,$data,$header) {
    $curl = curl_init();
    curl_setopt($curl, CURLOPT_URL, $url);
    curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, FALSE);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,FALSE);
    curl_setopt($curl, CURLOPT_POST, 1);
    curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
    curl_setopt($curl,CURLOPT_HTTPHEADER,$header);
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
    $output = curl_exec($curl);
    curl_close($curl);
    return $output;
}

function checkCode($token, $server) {
    $curl = curl_init();
    curl_setopt_array($curl, array(
      CURLOPT_URL => str_replace('https://', 'http://', $server),
      CURLOPT_RETURNTRANSFER => true,
      CURLOPT_ENCODING => '',
      CURLOPT_MAXREDIRS => 10,
      CURLOPT_TIMEOUT => 0,
      CURLOPT_FOLLOWLOCATION => true,
      CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
      CURLOPT_CUSTOMREQUEST => 'POST',
      CURLOPT_POSTFIELDS =>'{
        "id": "**********",
        "secretkey": "************",
        "scene": 0,
        "token": "' . $token . '",
        "ip": "127.0.0.1"
    }',
      CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
      ),
    ));
    
    $response = curl_exec($curl);
    
    curl_close($curl);
    
    return json_decode($response, true);

}

function isRightServer($server) {
    $domain = parse_url($server)["host"];
    return endsWith($domain, "vaptcha.net") || endsWith($domain, "vaptcha.com");
}

function isPhoneNumber($phone) {
    return preg_match("/^1[345789]\d{9}$/", $phone);
}

function getSendNum() {
    $length = 4;
     $chars = '0123456789';
     $password = '';
     for ( $i = 0; $i < $length; $i++ ) {
         $password .= $chars[ mt_rand(0, strlen($chars) - 1) ];
     }
     return $password;
}

function selectMemberById($member_id) {
    return selectMember($member_id);
}

function checkPhoneCode($phone, $code) {
    if (!is_numeric($phone) || !is_numeric($code)) {
        return false;
    }
    $sendData = select1hourSenddata($phone, $code, msectime());
    if ($sendData != null) {
        updateSenddata($sendData["id"]);
        return true;
    }
    return false;
}

function isNewUser($phone) {
    $member = selectMemberByPhone($phone);
    return $member == null;
}

function insertUser($phone, $waitId) {
    $wxid = null;
    if ($waitId != null && is_numeric($waitId)) {
        $wait = findLoginWaitById($waitId);
        if (isset($wait['wxid'])) {
            $wxid = $wait['wxid'];
        }
    }
    insertMember($wxid, $phone, randPic(), nameByPhone($phone));
} 

function maybeNeedBinding($waitId, $member) {
    if (($member["wxid"] == null || $member["wxid"] == '') && isset($member["id"])) {
        if ($waitId != null && is_numeric($waitId)) {
            $wait = findLoginWaitById($waitId);
            if (isset($wait['wxid'])) {
                updateMemberWxid($member["id"], $wait['wxid']);
            }
        }
    }
}

function randPic() {
    return getHost()."img/user/morentouxiang.png";
}

function nameByPhone($phone) {
    return substr($phone, 0, 3).'****'.substr($phone, 7);
}

function isNearlyRegion($regionTime) {
    $nowTime = date("Y-m-d H:i:s"); 
    return strtotime($nowTime) - strtotime($regionTime) > 2592000;
}

function endsWith($haystack, $needles, $strict = true) {
    if (!$strict) $haystack = mb_strtolower($haystack);
    foreach ((array)$needles as $needle) {
        if (!$strict) $needle = mb_strtolower($needle);
        if ((string)$needle === mb_substr($haystack, -mb_strlen($needle))) {
            return true;
        }
    }
    return false;
}

function nearlyTags($title) {
    return nearlyTagsByPage($title, 50, 1);
}


function nearlyTagsByPage($title, $size, $page) {
    $begin = ($page - 1) * $size;
    $curl = curl_init();

    curl_setopt_array($curl, array(
    CURLOPT_URL => 'http://127.0.0.1:9200/ppt_tag/_search',
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS =>'{
    "query": {
        "match": {
            "tag": "' . $title . '"
        }
    },
    "size": ' . $size . ',
    "from": ' . $begin . '
    }',
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
    ),
    ));

    $response = curl_exec($curl);
    curl_close($curl);
    return json_decode($response, true);
}

function findDesignerPPTs($designerId, $designerPageSize, $page, $channel = 'MBG') {
    $curl = curl_init();

    curl_setopt_array($curl, array(
    CURLOPT_URL => '127.0.0.1:9200/ppt_info/_search',
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS =>'{
        "query": {
            "bool": {
                "must": [
                    {
                        "match": {
                            "designer_id": ' . $designerId . '
                        }
                    },
                    {
                        "match": {
                            "channel.keyword": "' . $channel . '"
                        }
                    }
                ]
            }
        },
        "size": '.$designerPageSize.',
        "from": ' . ($page - 1) * $designerPageSize . '
    }',
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
    ),
    ));

    $response = curl_exec($curl);

    curl_close($curl);
    return json_decode($response, true);

}

function randTag($size) {
    $curl = curl_init();

    curl_setopt_array($curl, array(
    CURLOPT_URL => 'http://127.0.0.1:9200/ppt_tag/_search',
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS =>'{
      "size": '.$size.',
      "from": 0,
      "sort": {
        "_script": {
          "script": "Math.random()",
          "type": "number",
          "order": "asc"
        }
      }
    }',
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
    ),
    ));

    $response = curl_exec($curl);
    curl_close($curl);
    return json_decode($response, true);
}

function searchTagById($idBegin, $idEnd) {
    $curl = curl_init();
    curl_setopt_array($curl, array(
    CURLOPT_URL => 'http://127.0.0.1:9200/ppt_tag/_search',
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS =>'{
        "query": {
            "range": {
                "id": {
                    "gte": '.$idBegin.',
                    "lte": '.$idEnd.'
                }
            }
        },
        "size": 9999
    }',
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
    ),
    ));

    $response = curl_exec($curl);

    curl_close($curl);
    return json_decode($response, true);
}

function findTag($pinyin) {
    $curl = curl_init();

    curl_setopt_array($curl, array(
    CURLOPT_URL => 'http://127.0.0.1:9200/ppt_tag/_search',
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS =>'{
    "query": {
        "match": {
            "pinyin.keyword": "' . $pinyin . '"
        }
    }
    }',
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
    ),
    ));

    $response = curl_exec($curl);

    curl_close($curl);
    return json_decode($response, true);
}

function findTagByStr($str) {
    $curl = curl_init();

    curl_setopt_array($curl, array(
    CURLOPT_URL => 'http://127.0.0.1:9200/ppt_tag/_search',
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS =>'{
    "query": {
        "match": {
            "tag.keyword": "' . $str . '"
        }
    }
    }',
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
    ),
    ));

    $response = curl_exec($curl);

    curl_close($curl);
    return json_decode($response, true);
}

function delByValue($arr, $value){
    $key = array_search($value,$arr);
    if(isset($key)){
      unset($arr[$key]);
    }
    return $arr;
}

function aboutAndNearlyPage($allPage, $uri) {
    if ($allPage <= 12) {
        for ($i = 1; $i <= $allPage; $i++) {
            if ($uri["page"] == $i) {
                echo '<div class="page-num-hot">' . $i . '</div>';
            } else if ($i == 1) {
                echo '<a href="' . $uri["uid"] . '.html"><div class="page-num">' . $i . '</div></a>';
            } else {
                echo '<a href="' . $uri["uid"] . '_' . $i . '.html"><div class="page-num">' . $i . '</div></a>';
            }
        }
    } else if ($uri["page"] <= 4) {
        for ($i = 1; $i <= 10; $i++) {
            if ($uri["page"] == $i) {
                echo '<div class="page-num-hot">' . $i . '</div>';
            } else if ($i == 1) {
                echo '<a href="' . $uri["uid"] . '.html"><div class="page-num">' . $i . '</div></a>';
            } else {
                echo '<a href="' . $uri["uid"] . '_' . $i . '.html"><div class="page-num">' . $i . '</div></a>';
            }
        }
        echo '<div class="page-long">...</div>';
        echo '<a href="' . $uri["uid"] . '_' . $allPage  . '.html"><div class="page-num">' . $allPage . '</div></a>';
    } else if ($uri["page"] + 4 >= $allPage) {
        echo '<a href="' . $uri["uid"] . '.html"><div class="page-num">1</div></a>';
        echo '<div class="page-long">...</div>';
        for ($i = $allPage - 10; $i <= $allPage; $i++) {
            if ($uri["page"] == $i) {
                echo '<div class="page-num-hot">' . $i . '</div>';
            } else {
                echo '<a href="' . $uri["uid"] . '_' . $i . '.html"><div class="page-num">' . $i . '</div></a>';
            }
        }
    } else {
        echo '<a href="' . $uri["uid"] . '.html"><div class="page-num">1</div></a>';
        echo '<div class="page-long">...</div>';
        for ($i = $uri["page"] - 3; $i <= $uri["page"] + 4; $i++) {
            if ($uri["page"] == $i) {
                echo '<div class="page-num-hot">' . $i . '</div>';
            } else {
                echo '<a href="' . $uri["uid"] . '_' . $i  . '.html"><div class="page-num">' . $i . '</div></a>';
            }
        }
        echo '<div class="page-long">...</div>';
        echo '<a href="' . $uri["uid"] . '_' . $allPage . '.html"><div class="page-num">' . $allPage . '</div></a>';
    }
}


function getBaiduToken() {
    $url = 'http://aip.baidubce.com/oauth/2.0/token';
    $post_data['grant_type'] = 'client_credentials';
    $post_data['client_id'] = '************';
    $post_data['client_secret'] = '**************';
    $o = "";
    foreach ( $post_data as $k => $v ) 
    {
    	$o.= "$k=" . urlencode( $v ). "&" ;
    }
    $post_data = substr($o,0,-1);
    
    $res = request_post($url, $post_data);

    return json_decode($res, true);
}

function request_post($url = '', $param = '') {
    if (empty($url) || empty($param)) {
        return false;
    }
    
    $postUrl = $url;
    $curlPost = $param;
    $curl = curl_init();//初始化curl
    curl_setopt($curl, CURLOPT_URL,$postUrl);//抓取指定网页
    curl_setopt($curl, CURLOPT_HEADER, 0);//设置header
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);//要求结果为字符串且输出到屏幕上
    curl_setopt($curl, CURLOPT_POST, 1);//post提交方式
    curl_setopt($curl, CURLOPT_POSTFIELDS, $curlPost);
    $data = curl_exec($curl);//运行curl
    curl_close($curl);
    
    return $data;
}

function addTag($tag) {
    return insertTag($tag);
}

function findTagById($tagId) {
    return selectTagById($tagId);
}

function findTagByWord($tag) {
    return selectTagByWord($tag);
}

function selectLoginWait($uuid) {
    return findLoginWait($uuid);
}

function findMemberByWxid($openId) {
    return selectMemberByWxid($openId);
}

function setLoginState($openId, $uuid) {
    $loginWait = findLoginWait($uuid);
    if (isset($loginWait['id'])) {
        $member = selectMemberByWxid($openId);
        if (!isset($member['id']) || $member['phone'] = '') {
            //新用户
            updateLoginNewUser($openId, $loginWait['id']);
            return 1;
        } else {
            //老用户
            updateLoginOk($openId, $loginWait['id']);
            return 2;
        }
    } else {
        return -1;    
    }
}

function getCode($uuid){
    global $wxToken;
    $accessToken = $wxToken;
    //获取小程序码
    $data = '{
        "scene": "'.$uuid.'",
        "width": "280",
        "page": "pages/login/login"
    }';
    $url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=". $accessToken;
    $sunCode = http_request($url, $data);
    $sunCode = 'data:image/png;base64,'.chunk_split(base64_encode($sunCode));
    return $sunCode;
}

function getPayCode($payCode){
    global $wxToken;
    $accessToken = $wxToken;
    //获取小程序码
    $data = '{
        "scene": "'.$payCode.'",
        "width": "150",
        "page": "pages/pay/pay"
    }';
    $url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=". $accessToken;
    $sunCode = http_request($url, $data);
    $sunCode = 'data:image/png;base64,'.chunk_split(base64_encode($sunCode));
    return $sunCode;
}

function get3thPayCode($payCode){
    global $wxToken;
    $accessToken = $wxToken;
    //获取小程序码
    $data = '{
        "scene": "'.$payCode.'",
        "width": "150",
        "page": "pages/3pay/3pay"
    }';
    $url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=". $accessToken;
    $sunCode = http_request($url, $data);
    $sunCode = 'data:image/png;base64,'.chunk_split(base64_encode($sunCode));
    return $sunCode;
}


function createUuid() {
    return md5(uniqid(mt_rand(), true));
}

function codeGetOpen($code){
    $url = "https://api.weixin.qq.com/sns/jscode2session?appid=**********&secret=**********&js_code=" . $code . "&grant_type=authorization_code";
    $wxData = http_get_data($url);
    $wxJson = json_decode($wxData, true);
    if (isset($wxJson['openid'])) {
        return $wxJson['openid'];
    }
    return '';
}

function http_request($url, $data = null)
{
    $curl = curl_init();
    curl_setopt($curl, CURLOPT_URL, $url);
    curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, FALSE);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, FALSE);
    if (!empty($data)){
        curl_setopt($curl, CURLOPT_POST, 1);
        curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
    }
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, TRUE);
    $output = curl_exec($curl);
    curl_close($curl);
    return $output;
}

function http_get_data($url)
{
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'GET');
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_URL, $url);
    ob_start();
    curl_exec($ch);
    $return_content = ob_get_contents();
    ob_end_clean();
    $return_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    return $return_content;
}


function getIp()
{
    if (getenv('HTTP_CLIENT_IP')) {
        $ip = getenv('HTTP_CLIENT_IP');
    }
    if (getenv('HTTP_X_REAL_IP')) {
        $ip = getenv('HTTP_X_REAL_IP');
    } elseif (getenv('HTTP_X_FORWARDED_FOR')) {
        $ip = getenv('HTTP_X_FORWARDED_FOR');
        $ips = explode(',', $ip);
        $ip = $ips[0];
    } elseif (getenv('REMOTE_ADDR')) {
        $ip = getenv('REMOTE_ADDR');
    } else {
        $ip = '0.0.0.0';
    }
    return $ip;
}

function insertLoginWait($uuid) {
    insertLoginWaitByUuid($uuid);
} 

function insertPPTOrderData($memberId, $payWay, $uid) {
    global $oncePrice;
    $orderId = $memberId . '_' . $uid;
    insertPPTOrder($uid, $oncePrice, $memberId, $orderId, $payWay);
    return $orderId;
}

function historyBuy($memberId, $uid) {
    $order = selectPPTOrder($memberId, $uid);
    return isset($order['id']);
}

function insertVipOrderData($memberId, $payWay, $type, $priceFen) {
    $orderId = $memberId.'_'.$type.'_'.msectime();
    insertVipOrder($type, $priceFen, $memberId, $orderId, $payWay);
    return $orderId;
}

function findDesigner($id) {
    return selectDesigner($id);
}

function checkTime() {
    if (isset($_GET['time']) && is_numeric($_GET['time'])) {
        $getTime = $_GET['time'];
        if ($getTime % 292 == 0) {
            $timeDiff = msectime() - $_GET['time'];
            if ($timeDiff >= -300000 && $timeDiff <= 300000) {
                return true;
            }
        }
    }
    return false;
}

function todayDownMax($member) {
    $count = findTodayDown($member["id"]);
    if ($member["vip_type"] == 1) {
        return $count == 2;
    } else if ($member["vip_type"] == 2) {
        return $count == 10;
    } else if ($member["vip_type"] == 3) {
        return $count == 15;
    }
    return true;
}

function addDownLog($memberId, $uid) {
    insertDownLog($memberId, $uid);
}

function getFoot($timestamp, $showFriendLink = false) {
    echo '<div class="foot"><div class="foot-top"><div class="foot-left"></div><div class="foot-right"><div class="foot-link-box"><div class="foot-link-title">关于我们</div><a href="'.getHost().'we/index.html" target="_blank"><div class="foot-link-text">关于我们</div></a><a href="'.getHost().'we/agreement.html" target="_blank"><div class="foot-link-text">用户协议</div></a><a href="'.getHost().'we/contact.html" target="_blank"><div class="foot-link-text">联系我们</div></a></div><div class="foot-link-box"><div class="foot-link-title">其他服务</div><!--<a href="'.getHost().'buy/vip.html" target="_blank" rel="nofollow"><div class="foot-link-text">开通VIP</div></a>--></div><div class="foot-link-box"><div class="foot-link-title">分发平台</div><a href="https://cuttlefish.baidu.com/shop/19c24593daef5ef7ba0d3c34" target="_blank"><div class="foot-link-text">百度文库</div></a></div><div class="foot-link-box-big"><div class="foot-link-title">联系我们</div><div class="foot-link-text">客服邮箱：mubangou@88.com</div><a href="https://www.wjx.cn/vm/Pb1okDm.aspx#" target="_blank" rel="nofollow"><div class="foot-link-text">在线版权投诉</div></a></div><div class="foot-link-box-big"><div class="foot-link-title">商务合作</div><div class="foot-link-text">联系邮箱：boolduck@email.cn</div></div></div></div><div class="foot-bottom">';
    if ($showFriendLink) {
        echo '<div class="foot-friend-link-box"><span>友情链接:</span>
        <a href="https://www.1ppt.com/" target="_blank">第一PPT</a>
        <a href="https://www.pptjia.com/" target="_blank">PPT家园</a>
        <a href="https://www.ypppt.com/" target="_blank">优品PPT</a>
        <a href="https://www.docer.com/" target="_blank">稻壳儿</a>
        <a href="http://www.yanj.cn/" target="_blank">演界网</a>
        <a href="https://www.v5ppt.com/" target="_blank">赞芽PPT</a>
        <a href="https://www.51pptmoban.com/" target="_blank">51PPT模板</a>
        <a href="https://www.wps.cn/" target="_blank">金山办公</a>
        </div>';
    }
    echo '<div class="foot-copyright">Copyright©2022-'.date("Y").'模板狗 | <a href="https://beian.miit.gov.cn/" target="_blank">京ICP备2022034289号-2</a> | <a href="https://mubangou.com/sitemap.xml" target="_blank">网站地图</a> | 加载耗时:'.((msectime()-$timestamp)/1000).'秒</div></div><div class="left-btn-box"><!--<div class="left-buy-vip" onclick="buyVip()"></div>--><div class="left-design">PPT定制<div class="left-design-img"></div></div><div class="left-go-top">回顶部</div></div></div>
    <!--统计-->
    <script>var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "https://hm.baidu.com/hm.js?df4dbe194eb436ab165935e361b84de0";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();
</script>
';
}

function getLogin($canNotClose = false) {
    echo '<!--用户登录弹窗--><div class="mark" id="login-mark"><div class="login"><div class="login-left"></div><div class="login-right"><div class="login-logo"></div><div class="login-slogan">不想找太久，就用模板狗</div><div class="login-wx"><div class="login-qrcode"><img alt="扫码登录" src="'.getHost().'img/wait.png" id="login-qrcode-img"></div><div class="login-tip">微信扫码确认注册或登录</div><div class="login-other">其他登录方式:<div class="login-phone-go">手机登录</div></div></div><div class="login-phone" style="display: none"><div class="login-phone-must" style="display: none">根据《移动互联网应用程序信息服务管理规定》要求，您需要绑定手机号码才能够继续使用。</div><div class="login-phone-agree"><i onclick="loginAgree()" id="login-agree-icon"></i>我已阅读并同意<a href="'.getHost().'we/agreement.html" target="_blank">用户协议</a><div class="login-agree-tip"></div></div><div class="login-phone-num"><label><input type="text" placeholder="输入手机号码注册/登录" id="login-phone"></label></div><div class="login-phone-code"><label><input type="text" placeholder="请输入验证码" id="login-code"></label><div class="login-phone-send">发送验证码</div><div class="login-phone-send-wait"></div></div><div class="login-phone-btn" style="display: none">立即登录</div><div class="login-phone-btn-disable">立即登录</div><div class="login-phone-fail" style="height: 73px"></div><div class="login-other">其他登录方式:<div class="login-wx-go">微信登录</div></div></div>';
    if (!$canNotClose) {
        echo '<div class="login-close" onclick="loginClose()">×</div>';
    }
    echo '<div class="login-way"></div></div></div></div>';
}


function getNormalHead($tags, $title) {
    $nearly = nearlyPPT($title, '0')["hits"]["hits"];
    echo '<div class="head-main"><div class="head-logo"><a href="'.getHost().'" target="_blank"><img src="'.getHost().'img/logo-inner.png" alt="模板狗" class="head-logo-pic"><img src="'.getHost().'img/img-collection.png" alt="模板狗" class="img-collection"></a></div><div class="head-tab">热门专题<div class="head-hot-topic">';
    for ($i = 0; $i < 10; $i++) {
        if (sizeof($tags) - 1 < $i) {
            break;
        }
        echo '<a href="'.getHost().'tag/' . $tags[$i]["_source"]["pinyin"] . '.html" target="_blank" class="head-hot-topic-a"><div class="head-hot-topic-once"><i class="head-top-' . ($i + 1) . '">' . ($i + 1) . '</i>' . $tags[$i]["_source"]["tag"] . '</div></a>';
    }
    echo '<div class="clear"></div><a href="'.getHost().'" target="_blank" class="head-hot-link">去首页>></a></div></div><div class="head-about">模板推荐<div class="head-hot-ppt">';
    $number = sizeof($nearly) <= 4 ? sizeof($nearly) : 4;
    for ($i = 0; $i < $number; $i++) {
        $about_ppt = $nearly[$i]["_source"];
        echo '<a href="'.getHost().'ppt/'.$about_ppt["uid"].'.html" target="_blank"><div class="ppt"><div class="img-box"><img src="'.$about_ppt["pic_url"].'?imageView2/4/w/500/h/300/format/webp/q/100" alt="'.$about_ppt["title"].'"></div><div class="ppt-go"><div class="ppt-free-btn">会员免费</div><div class="ppt-down-btn">立即下载</div></div><div class="ppt-copyright"><span class="copyright-small">版权</span><span class="copyright-big">版权可溯源<br>使用更安全</span></div><div class="ppt-title">'.$about_ppt["title"].'</div></div></a>';
    }
    echo '<div class="clear"></div><a href="'.getHost().'" target="_blank" class="head-hot-link">去首页>></a></div></div><div class="head-search"><label><input placeholder="不想找太久 就用模板狗" id="head-input"></label><div class="head-search-btn" onclick="search($(\'#head-input\').val())">搜索</div></div><div class="head-user"><div class="head-user-box" id="member-login" style="display: block"><img src="../img/touxiang.jpg" alt="我的账户"><div class="user-box"><div class="user-box-name"></div><div class="user-box-vip"></div><a href="'.getHost().'user.html" target="_blank" rel="nofollow"><div class="user-box-once">个人中心</div></a><div class="user-box-once" onclick="loginExit()">退出</div></div></div><div class="head-login" onclick="loginShow()" id="member-not-login" style="display: none"><div class="head-login-region">注册</div><div class="head-login-login">登录</div></div></div>';
    /*<div class="head-phone-vip" onclick="buyVip()"></div><div class="head-vip" onclick="buyVip()"><div class="head-vip-hot">商用授权</div><div class="head-vip-page"><div class="head-vip-page-title">商用VIP开通</div><div class="head-vip-page-title-tip">一次开通企业和个人都可商用，更划算</div><div class="head-vip-page-box"><div class="head-vip-page-once">模板下载</div><div class="head-vip-page-once">个人商用</div><div class="head-vip-page-once">雇主商用</div><div class="head-vip-page-once">专属客服</div><div class="head-vip-page-once">高速下载</div><div class="head-vip-page-once">会员标识</div></div><div class="head-vip-go"><div class="head-vip-bug-999"><div class="head-vip-buy-hot">推荐</div><div class="head-vip-buy-title">终身</div><div class="head-vip-buy-price"><span>￥</span>199</div><div class="head-vip-buy-go">20次下载/天</div></div><div class="head-vip-bug-365"><div class="head-vip-send">买一年,送一年</div><div class="head-vip-buy-title">年卡</div><div class="head-vip-buy-price"><span>￥</span>99</div><div class="head-vip-buy-go">15次下载/天</div></div><div class="head-vip-bug-30"><div class="head-vip-buy-title">月卡</div><div class="head-vip-buy-price"><span>￥</span>39</div><div class="head-vip-buy-go">5次下载/天</div></div></div></div></div>*/;
    echo '</div>';
}

function findVipByUuid($uuid) {
    return selectVipOrderByUuid($uuid);
}

function findPPTByUuid($uuid) {
    return selectPPTOrderByUuid($uuid);
}

function safeString($str) {
    return strip_tags(htmlspecialchars(stripslashes(trim($str))));
}

function updateVipPayState($uuid, $orderId) {
    updateVipOrderPay($uuid, $orderId);
}

function updatePPTPayState($uuid, $orderId) {
    updatePPTOrderPay($uuid, $orderId);
}

function flushMemberVipTypeAndTime($type, $time, $memberId) {
    updateMemberVipTypeAndTime($type, $time, $memberId);
}

function isStrExist($string, $str) {
    $string = (string) $string;
    $str = (string) $str;
    return strstr($string, $str)===false ? false : true;
}

function updateOrder($uuid, $tradeNo) {
    $vip = findVipByUuid($uuid);
    if (isset($vip["id"])) {
        updateVipPayState($uuid, $tradeNo);
        $memberId = $vip["member_id"];
        $member = selectMemberById($memberId);
        if (isset($member["id"])) {
            $type = $member["vip_type"] > $vip["vip_type"] ? $member["vip_type"] : $vip["vip_type"];
            $nowTime = time();
            $time = '';
            if ($vip["vip_type"] == '3') {
                $time = '2099-12-31 23:59:59';
            } else if ($vip["vip_type"] == '2') {
                //注意，买一年送一年
                $time = date('Y', $nowTime) + 2 . '-' . date('m-d H:i:s');
            } else if ($vip["vip_type"] == '1') {
                $time = date('Y-m-d H:i:s', strtotime('+1 month'));
            } else {
                echo 'error';
                exit;
            }
            flushMemberVipTypeAndTime($type, $time, $member["id"]);
        }
    } else {
        $ppt = findPPTByUuid($uuid);
        if (isset($ppt["id"])) {
            updatePPTPayState($uuid, $tradeNo);
        } else {
            $order3th = selectThirdByUuid($uuid);
            if (isset($order3th["id"])) {
                updateOrderCanDown($uuid);
            }
        }
    }
}

function findNearlyDown($memberId) {
    return selectNearlyDown($memberId);
}

function findNearlyBuy($memberId) {
    return selectNearlyBuy($memberId);
}

function findPPTByUids($uids) {
    if (sizeof($uids) == 0) {
        return [];
    }
    $curl = curl_init();

    $searchStr = '{"query": {"terms": {"uid": [';
    for ($i = 0; $i < sizeof($uids); $i++) {
        if ($i != 0) {
            $searchStr = $searchStr . ',';
        }
        $searchStr = $searchStr . '"' . $uids[$i] . '"';
    }
    $searchStr = $searchStr . ']}}}';

    curl_setopt_array($curl, array(
    CURLOPT_URL => '127.0.0.1:9200/ppt_info/_search',
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => '',
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 0,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => 'POST',
    CURLOPT_POSTFIELDS => $searchStr,
    CURLOPT_HTTPHEADER => array(
        'Content-Type: application/json'
    ),
    ));

    $response = curl_exec($curl);

    curl_close($curl);
    return json_decode($response, true);
}

function addThirdOrder($uuid, $pptUid, $channel) {
    insertThirdOrder($uuid, $pptUid, $channel);
}

function userHistoryBuy($uuid, $openId = '') {
    $order = selectThirdByUuid($uuid);
    if ($order['id']) {
        if ($openId == '') {
            $openId = $order['open_id'];
        }
        $payOld = selectHistory($openId, $order['ppt_uid']);
        if ($payOld['id']) {
            return true;
        }
    }
    return false;
}

function select3thOrderByUuid($uuid) {
    return selectThirdByUuid($uuid);
}

function updateOrderCanDown($uuid) {
    updateThirdOrderState1($uuid);
}

function getDownLink($uid) {
    $pptByUid = searchPPTByUid($uid);
    if (!isset($pptByUid["_source"])) {
        return '';
    }
    $ppt = $pptByUid["_source"];
    $deadLine = time() + 300;
    $file = 'https://file.mubangou.com/'.$ppt["down_url"] .'?e='.$deadLine;
    $hash = hash_hmac('sha1', $file, '**********', true);
    $find = array('+', '/');
    $replace = array('-', '_');
    $token = str_replace($find, $replace, base64_encode($hash));
    return $file.'&token=***********:'.$token;
}

function update3thOrderOpenId($uuid, $openId) {
    update3thOpenId($uuid, $openId);
}


?>