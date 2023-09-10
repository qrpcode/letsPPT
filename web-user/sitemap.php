<?php
header('Content-Type:application/xml; charset=utf-8');

$date = date('Y-m-d H:i:s');
$indexXml = '<?xml version="1.0" encoding="UTF-8"?><sitemapindex>';
for ($i = 1; $i <= 500; $i++) {
    if(is_file('sitemap_'.$i.'.xml')){
        $indexXml = $indexXml . '<sitemap><loc>https://mubangou.com/sitemap_'.$i.'.xml</loc><lastmod>' . $date . '</lastmod></sitemap>';
    }
}
$indexXml = $indexXml . '</sitemapindex>';
echo $indexXml;
?>