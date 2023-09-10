<?php

require 'api/php_tool.php';
$ppts = homePPTShow(10000)["hits"]["hits"];

$siteMapStr = '';
$nowMapSize = 0;
$nowMapCount = 0;

$date = date('Y-m-d H:i:s');

$begin = $_GET["begin"];

if ($begin == -1) {
    //首页
    addSiteMap("<url><loc>https://www.mubangou.com/</loc><lastmod>" . $date . "</lastmod></url>");

    //PPT
    for ($i = 0; $i < sizeof($ppts); $i++) {
        addSiteMap('<url><title>' . $ppts[$i]["_source"]["title"] . '</title><loc>https://mubangou.com/ppt/' . $ppts[$i]["_source"]["uid"] . '.html</loc><lastmod>' . $ppts[$i]["_source"]["create_time"] . '</lastmod></url>');
    }

    //PPT About页
    for ($i = 0; $i < sizeof($ppts); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/about/' . $ppts[$i]["_source"]["uid"] . '.html</loc><lastmod>' . $date . '</lastmod></url>');
    }

    //PPT Nearly页
    for ($i = 0; $i < sizeof($ppts); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/nearly/' . $ppts[$i]["_source"]["uid"] . '.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    
    buildNewSiteMap();
}

if ($begin >= 0) {
    $tagBeginId = $begin * 1800;
    $tags = searchTagById($tagBeginId, $tagBeginId + 1800)["hits"]["hits"];

    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_p1.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_p2.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_p3.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_p4.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_p5.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_p6.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_p7.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_p8.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_p9.html</loc><lastmod>' . $date . '</lastmod></url>');
    }

    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c2.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c4.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c5.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c6.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c7.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c8.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c9.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c10.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c11.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c12.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c13.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c14.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c15.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c16.html</loc><lastmod>' . $date . '</lastmod></url>');
    }
    for ($i = 0; $i < sizeof($tags); $i++) {
        addSiteMap('<url><loc>https://mubangou.com/tag/' . $tags[$i]["_source"]["pinyin"] . '_c17.html</loc><lastmod>' . $date . '</lastmod></url>');
    }

    $nowMapCount = $begin + 1;
    buildNewSiteMap();
}

function addSiteMap($urlXml) {
    global $siteMapStr, $nowMapSize;
    if ($siteMapStr == '') {
        $siteMapStr = buildXmlHead();
    }
    $siteMapStr = $siteMapStr . $urlXml;

    $nowMapSize++;

    //if ($nowMapSize >= 45000) {
    //    buildNewSiteMap();
    //}
}

function buildNewSiteMap() {
    global $siteMapStr, $nowMapSize, $nowMapCount;
    $siteMapStr = $siteMapStr . '</urlset>';
    $nowMapSize = 0;
    $nowMapCount++;
    echo "sitemap_".$nowMapCount.".xml";
    file_put_contents("sitemap_".$nowMapCount.".xml", $siteMapStr);
    $siteMapStr = '';
}

function buildXmlHead() {
    return '<?xml version="1.0" encoding="UTF-8"?>
<urlset  xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:mobile="http://www.baidu.com/schemas/sitemap-mobile/1/">';
}

function buildIndexMap() {
    global $nowMapCount, $date;
    $indexXml = '<?xml version="1.0" encoding="UTF-8"?><sitemapindex>';
    for($i = 1; $i <= $nowMapCount; $i++) {
        $indexXml = $indexXml . '<sitemap><loc>https://mubangou.com/sitemap_'.$i.'.xml</loc><lastmod>' . $date . '</lastmod></sitemap>';
    }
    $indexXml = $indexXml . '</sitemapindex>';
    file_put_contents("sitemap.xml", $indexXml);
}

?>