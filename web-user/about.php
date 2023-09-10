<?php
require_once 'api/php_tool.php';
$pageSize = 10;
$timestamp = msectime();
$uri = analysisAboutLink();
if ($uri == null || $uri["uid"] == '') {
    require '404.php';
    exit;
}
$pptByUid = searchPPTByUid($uri["uid"], 'MBG');
if (!isset($pptByUid["_source"])) {
    require '404.php';
    exit;
}
$ppt = $pptByUid["_source"];
$pptTags = findTags($ppt["tags"])["hits"]["hits"];
$tags = nearlyTags($ppt["title"])["hits"]["hits"];

$listTagsFind = nearlyTagsByPage($ppt["title"], $pageSize, $uri['page']);
$listTags = $listTagsFind["hits"]["hits"];
$total = $listTagsFind["hits"]["total"]["value"];
?>
<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <meta charset="UTF-8">
    <link rel="shortcut icon" href="../favicon.ico">
    <title><?php echo $ppt['title'] ?>专栏<?php if ($uri['page'] > 1) echo '-第' . $uri['page'] . '页' ?>-模板狗</title>
    <meta name="keywords" content="<?php echo $ppt['title'] ?>PPT,<?php echo $ppt['title'] ?>PPT专栏,<?php echo $ppt['title'] ?>PPT模板专栏,<?php echo $ppt['title'] ?>幻灯片专栏">
    <meta name="description" content="模板狗为您提供有关<?php echo $ppt['title'] ?>的相关专栏列表供您参考选择，模板狗平台提供<?php echo $ppt['title'] ?>PPT模板下载、<?php echo $ppt['title'] ?>幻灯片模板下载等服务，我们通过优质的质量和良好的服务帮助您快速找到需要的PPT模板资源。">
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" type="text/css" href="../css/global.css">
    <link rel="stylesheet" type="text/css" href="../css/list.css">
    <link rel="stylesheet" type="text/css" href="../css/inner.css">
    <script src="../js/jquery.min.js"></script>
    <script src="../js/global.js"></script>
</head>

<body>
    <div class="head" id="search-head">
        <?php echo getNormalHead($tags, $ppt["title"])?>
    </div>
    <div class="content">
        <div class="main">
            <div class="inner-crumbs">
                <a href="../" target="_blank">PPT模板</a>
                <span> > </span>
                <a href="../ppt/<?php echo $uri["uid"] ?>.html" target="_blank">简约商务风格工作总结计划PPT模板</a>
                <span> > </span>
                <a href="">简约商务风格工作总结计划相关专栏</a>
            </div>
            <div class="topic-title-box">
                <h1><?php echo $ppt['title'] ?>专栏</h1>
                <div class="topic-title-box-time">
                    <span>创建时间: <?php echo $ppt['create_time'] ?></span>
                </div>
                <p>
                    <a href="../" target="_blank">模板狗</a>小编整理了有关<?php echo $ppt['title'] ?>的相关专栏，方便大家选择需要的
                    <a href="../" target="_blank">PPT模板</a>内容进行下载使用。同时我们还提供各种其他类型的PPT模板，大家也可以按需选择哦！
                </p>
            </div>
            <h2 class="tab-title"><i></i>专栏灵感来源设计模板</h2>
            <div class="from-ppt">
                <a href="../ppt/<?php echo $ppt['uid'] ?>.html" target="_blank">
                    <div class="from-left">
                        <img src="<?php echo $ppt['pic_url'] ?>?imageView2/4/w/500/h/400/format/webp/q/100" alt="<?php echo $ppt['title'] ?>">
                    </div>
                </a>
                <div class="from-right">
                    <a href="../ppt/<?php echo $ppt['uid'] ?>.html" target="_blank"><h3 class="title"><?php echo $ppt['title'] ?></h3></a>
                    <div class="title-tab-box">
                        <div class="ppt-time">发布时间: <?php echo $ppt['create_time'] ?></div>
                        <?php
                        for ($i = 0; $i < sizeof($pptTags); $i++) {
                            echo '<a href="../tag/' . $pptTags[$i]["_source"]["pinyin"] . '.html" target="_blank"><div class="title-tab">' . $pptTags[$i]["_source"]["tag"] . '</div></a>';
                        }
                        ?>
                    </div>
                    <a href="../ppt/<?php echo $ppt['uid'] ?>.html" target="_blank">
                        <div class="from-right-go">去详情页查看 >></div>
                    </a>
                </div>
                <div class="clear"></div>
            </div>
            <h2 class="tab-title"><i></i><?php echo $ppt['title'] ?>专栏列表</h2>
            <div class="main-show">
                <div class="topic-list-box">
                    <?php
                    for ($i = 0; $i < sizeof($listTags); $i++) {
                        $thisTag = $listTags[$i]['_source'];
                        echo '<div class="topic-once"><div class="topic-head"><h2 class="topic-name"><a href="../tag/' . $thisTag['pinyin'] . '.html" target="_blank">' . $thisTag['tag'] . '</a></h2><div class="topic-go"><a href="../tag/' . $thisTag['pinyin'] . '.html" target="_blank">立即前往>></a></div></div><div class="topic-ppt-box">';

                        $ppts = nearlyPPTByPage($thisTag['tag'], 1, 4, [], [])["hits"]["hits"];
                        for ($j = 0; $j < 4; $j++) {
                            $thisPpt = $ppts[$j]['_source'];
                            echo '<a href="../ppt/' . $thisPpt['uid'] . '.html" target="_blank"><div class="ppt"><div class="img-box"><img src="' . $thisPpt['pic_url'] . '?imageView2/4/w/500/h/300/format/webp/q/100" alt="' . $thisPpt['title'] . '"></div><div class="ppt-go"><div class="ppt-free-btn">会员免费</div><div class="ppt-down-btn">立即下载</div></div><div class="ppt-copyright"><span class="copyright-small">版权</span><span class="copyright-big">版权可溯源<br>使用更安全</span></div><div class="ppt-title">' . $thisPpt['title'] . '</div></div></a>';
                        }
                        echo '<a href="../tag/' . $thisTag['pinyin'] . '.html" target="_blank"><div class="ppt-topic-box-more">查看全部 </div></a></div></div>';
                    }
                    ?>
                </div>
                <div class="recommend-ppt-box"></div>
                <div class="main-clean"></div>
            </div>
            <div class="list-page">
                <?php
                $allPage = ceil(1.00 * $total / $pageSize);
                aboutAndNearlyPage($allPage, $uri);
                ?>
            </div>
        </div>
        <div class="wrap">
            <div class="wrap-title">
                为您推荐
                <svg class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
                    <path d="M185.884 327.55 146.3 367.133 512.021 732.779 877.7 367.133 838.117 327.55 511.997 653.676Z" fill="#bfbfbf"></path>
                </svg>
            </div>
            <div class="wrap-title-box">
                <div class="wrap-title-once" id="link-box-title-1"><a href="../" target="_blank">PPT模板</a></div>
                <div class="wrap-title-once" id="link-box-title-2"><a href="../" target="_blank">PPT专题</a></div>
                <div class="wrap-title-once" id="link-box-title-3"><a href="../" target="_blank">相关素材</a></div>
                <div class="ppt-box-clear"></div>
            </div>
            <div class="wrap-link-box">
                <?php
                $tagId = 11;
                $str1 = '';
                $str2 = '';
                $str3 = '';
                while (isset($tags[$tagId])) {
                    $html = '<a href="../tag/' . $tags[$tagId]["_source"]["pinyin"] . '.html" target="_blank">' . $tags[$tagId]["_source"]["tag"] . '</a>';
                    if ($tagId % 3 == 0) {
                        $str1 = $str1 . $html;
                    } else if ($tagId % 3 == 1) {
                        $str2 = $str2 . $html;
                    } else {
                        $str3 = $str3 . $html;
                    }
                    $tagId++;
                }

                ?>
                <div class="wrap-link-box-once" id="link-box-1">
                    <?php echo $str1; ?>
                </div>
                <div class="wrap-link-box-once" id="link-box-2" style="display: none">
                    <?php echo $str2; ?>
                </div>
                <div class="wrap-link-box-once" id="link-box-3" style="display: none">
                    <?php echo $str3; ?>
                </div>
            </div>
        </div>
    </div>

    <?php echo getFoot($timestamp);?>

    <?php echo getLogin()?>

    <div class="black-mark" id="first-use-box" style="display: none">
        <a href="buy/vip.html" target="_blank">
            <div class="first-use"></div>
        </a>
        <i class="mark-close" onclick="$('#first-use-box').css('display', 'none');">×</i>
    </div>

    <!--手势验证sdk-->
    <script src="https://v-cn.vaptcha.com/v3.js"></script>
    <script src="../js/login.js"></script>
</body>

</html>