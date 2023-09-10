<?php
if(is_array($_GET) && count($_GET) > 0) {
    if(isset($_GET["s"])) {
        $para = $_GET["s"];
        buildPageByFrom($para);
    } else {
        require 'home.php';
    }
} else {
    require 'home.php';
}

function buildPageByFrom($str) {
    if (indexStartWith($str, "/ppt")) {
        require 'ppt.php';
    } else if (indexStartWith($str, "/tag")) {
        require 'tag.php';
    } else if (indexStartWith($str, "/nearly")) {
        require 'nearly.php';
    } else if (indexStartWith($str, "/about")) {
        require 'about.php';
    } else if (indexStartWith($str, "/designer")) {
        require 'designer.php';
    } else if (indexStartWith($str, "/user.html")) {
        require 'user.php';
    } else if (indexStartWith($str, "/sitemap.xml")) {
        require 'sitemap.php';
    } else if (indexStartWith($str, "/buy/vip.html")) {
        require 'vip.php';
    } else if (indexStartWith($str, "/buy")) {
        require 'buy_ppt.php';
    } else {
        require '404.php';
    }
}

function indexStartWith($str, $needle) {
    return strpos($str, $needle) === 0;    
}

?>