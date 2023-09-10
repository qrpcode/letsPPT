<?php
require_once 'api/php_tool.php';
$timestamp = msectime();
$uriData = analysisTagLink();
if ($uriData == null || $uriData["page"] * getListPageSize() > 10000) {
    require '404.php';
    exit;
}
$tag = [];
$tagName = '';
if (isset($uriData["searchId"])) {
    $tagSearch = findTagById($uriData["searchId"]);
    if (isset($tagSearch["id"])) {
        $tagName = $tagSearch["tag"];
    }
} else {
    $tag = findTag($uriData["pinyin"])["hits"]["hits"];
    $tagName = $tag[0]["_source"]["tag"];
    if (sizeof($tag) == 0) {
        require '404.php';
        exit;
    }
}
$tagTitle = $uriData["headTitle"] . $tagName;
$tags = nearlyTags($tagTitle)["hits"]["hits"];

$pptsReturn = nearlyPPTByPage($tagName, $uriData["page"], getListPageSize(), $uriData["colorFilterArray"], $uriData["pageFilterArray"]);

$total = $pptsReturn["hits"]["total"]["value"];
$total = $total >= 10000 ? 10000 : $total;

$ppts = $pptsReturn["hits"]["hits"];
if (sizeof($ppts) == 0) {
    if ($uriData["page"] != 1) {
        require '404.php';
        exit;
    } else {
        $pptsReturn = nearlyPPTByPage('模板', 1, getListPageSize(), [], []);
        $ppts = $pptsReturn["hits"]["hits"];
    }
}
$allPage = ceil(1.00 * $total / getListPageSize());
$tags = nearlyTags($tagName)["hits"]["hits"];
?>
<!DOCTYPE html>
<html lang="zh">

<head>
    <meta charset="UTF-8">
    <title><?php echo $tagTitle ?>PPT模板_<?php echo $tagTitle ?>PPT下载-模板狗</title>
    <meta name="keywords" content="<?php echo $tagTitle ?>,<?php echo $tagTitle ?>PPT,<?php echo $tagTitle ?>PPT模板,<?php echo $tagTitle ?>模板,<?php echo $tagTitle ?>幻灯片">
    <meta name="description" content="模板狗为您提供海量<?php echo $tagTitle ?>PPT模板下载资源，<?php echo $tagTitle ?>相关的PPT模板您可以方便快捷在模板狗进行下载。我们搜索到超过<?echo $total >= 150 ? $total : 265;?>个相关的PPT模板，您可以根据自己需要选择下载哦。">
    <link rel="shortcut icon" href="../favicon.ico">
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" type="text/css" href="../css/global.css">
    <link rel="stylesheet" type="text/css" href="../css/list.css">
    <link rel="stylesheet" type="text/css" href="../css/inner.css">
    <script src="../js/jquery.min.js"></script>
    <script>
        let pinyin = "<?php echo $uriData["pinyin"] ?>";
        let page = <?php echo $uriData["page"] ?>;
        let pageList = [<?php echo $uriData["pageFilter"] ?>];
        let colorList = [<?php echo $uriData["colorFilter"] ?>];
    </script>
    <script src="../js/global.js"></script>
</head>

<body>
    <div class="head" id="search-head">
        <?php echo getNormalHead($tags, $tagName)?>
    </div>
    <div class="content">
        <div class="main">
            <div class="inner-crumbs">
                <a href="../" target="_blank">PPT模板</a>
                <span> > </span>
                <a href="" target="_blank"><?php echo $tagTitle ?></a>
            </div>
            <div class="topic-title-box">
                <h1><?php echo $tagTitle ?></h1>
                <p>
                    模板狗专栏频道为您提供<?php echo $tagTitle ?>ppt模板下载专栏，方便大家快速找到需要的PPT模板。
                    同时模板狗也为您提供各种类型的PPT模板、PPT文档、PPT母版等内容可供下载，并且支持定制自己需要的模板。更多的有关<?php echo $tagTitle ?>PPT模板都可以在模板狗找到哦！
                </p>
            </div>
            <div class="inner-search-box">
                <div class="search-page-box">
                    <div class="inner-choose-title">我希望模板包含：</div>
                    <div class="search-page-once-box">
                        <a href="<?php echo getPageChooseLink($uriData, 1) ?>">
                            <div class="search-page-once" id="page-once-1">折线图</div>
                        </a>
                        <a href="<?php echo getPageChooseLink($uriData, 2) ?>">
                            <div class="search-page-once" id="page-once-2">柱状图</div>
                        </a>
                        <a href="<?php echo getPageChooseLink($uriData, 3) ?>">
                            <div class="search-page-once" id="page-once-3">扇形图</div>
                        </a>
                        <a href="<?php echo getPageChooseLink($uriData, 4) ?>">
                            <div class="search-page-once" id="page-once-4">组织架构</div>
                        </a>
                        <a href="<?php echo getPageChooseLink($uriData, 5) ?>">
                            <div class="search-page-once" id="page-once-5">时间轴</div>
                        </a>
                        <a href="<?php echo getPageChooseLink($uriData, 6) ?>">
                            <div class="search-page-once" id="page-once-6">对比介绍</div>
                        </a>
                        <a href="<?php echo getPageChooseLink($uriData, 7) ?>">
                            <div class="search-page-once" id="page-once-7">引言页</div>
                        </a>
                        <a href="<?php echo getPageChooseLink($uriData, 8) ?>">
                            <div class="search-page-once" id="page-once-8">金字塔</div>
                        </a>
                        <a href="<?php echo getPageChooseLink($uriData, 9) ?>">
                            <div class="search-page-once" id="page-once-9">荣誉页</div>
                        </a>
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="search-color-box">
                    <div class="inner-choose-color-title">我希望模板颜色是：</div>
                    <div class="color-tab-box">
                        <a href="<?php echo getColorChooseLink($uriData, 4) ?>">
                            <div class="color-tab color-4" style="background-color: #BD120F"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 5) ?>">
                            <div class="color-tab color-5" style="background-color: #F05246"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 6) ?>">
                            <div class="color-tab color-6" style="background-color: #853DE4"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 7) ?>">
                            <div class="color-tab color-7" style="background-color: #3628A5"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 8) ?>">
                            <div class="color-tab color-8" style="background-color: #3C7DF3"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 9) ?>">
                            <div class="color-tab color-9" style="background-color: #62E3C6"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 10) ?>">
                            <div class="color-tab color-10" style="background-color: #67CD80"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 11) ?>">
                            <div class="color-tab color-11" style="background-color: #D7F960"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 12) ?>">
                            <div class="color-tab color-12" style="background-color: #F9CC9E"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 13) ?>">
                            <div class="color-tab color-13" style="background-color: #E7B730"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 14) ?>">
                            <div class="color-tab color-14" style="background-color: #E88C30"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 15) ?>">
                            <div class="color-tab color-15" style="background-color: #C67D54"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 16) ?>">
                            <div class="color-tab color-16" style="background-color: #7B4512"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 2) ?>">
                            <div class="color-tab color-2" style="background-color: #000000"></div>
                        </a>
                        <a href="<?php echo getColorChooseLink($uriData, 17) ?>">
                            <div class="color-tab color-17" style="background-color: #E0E0E0"></div>
                        </a>

                        <div class="del-all-block" title="清除筛选" onclick="colorNoChoose()">清除筛选</div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>
        <div class="list-box">
            <div class="ppt-box">
                <?php
                for ($i = 0; $i < sizeof($ppts); $i++) {
                    $about_ppt = $ppts[$i]["_source"];
                ?>
                    <a href="../ppt/<?php echo $about_ppt["uid"] ?>.html" target="_blank">
                        <div class="ppt">
                            <div class="img-box"><img src="<?php echo $about_ppt["pic_url"] ?>" alt="<?php echo $about_ppt["title"] ?>"></div>
                            <div class="ppt-go">
                                <div class="ppt-free-btn">会员免费</div>
                                <div class="ppt-down-btn">立即下载</div>
                            </div>
                            <div class="ppt-copyright"><span class="copyright-small">版权</span><span class="copyright-big">版权可溯源<br>使用更安全</span></div>
                            <div class="ppt-title"><?php echo $about_ppt["title"] ?></div>
                        </div>
                    </a>
                <?
                }
                ?>
                <?php
                if ($uriData["page"] != $allPage) {
                    echo '<a href="'.$uriData["pinyin"].'_'.($uriData["page"] + 1).'.html" target="_blank"><div class="ppt-more">下一页<br>更多模板<i></i></div></a>';
                }
                ?>
                <div class="ppt-box-clear"></div>
            </div>
            <div class="list-page">
                <?php
                $linkUri = getLinkUri($uriData);
                if ($allPage <= 12) {
                    for ($i = 1; $i <= $allPage; $i++) {
                        if ($uriData["page"] == $i) {
                            echo '<div class="page-num-hot">' . $i . '</div>';
                        } else if ($i == 1) {
                            echo '<a href="' . $uriData["pinyin"] . $linkUri . '.html"><div class="page-num">' . $i . '</div></a>';
                        } else {
                            echo '<a href="' . $uriData["pinyin"] . '_' . $i . $linkUri  . '.html"><div class="page-num">' . $i . '</div></a>';
                        }
                    }
                } else if ($uriData["page"] <= 4) {
                    for ($i = 1; $i <= 10; $i++) {
                        if ($uriData["page"] == $i) {
                            echo '<div class="page-num-hot">' . $i . '</div>';
                        } else if ($i == 1) {
                            echo '<a href="' . $uriData["pinyin"] . $linkUri . '.html"><div class="page-num">' . $i . '</div></a>';
                        } else {
                            echo '<a href="' . $uriData["pinyin"] . '_' . $i . $linkUri  . '.html"><div class="page-num">' . $i . '</div></a>';
                        }
                    }
                    echo '<div class="page-long">...</div>';
                    echo '<a href="' . $uriData["pinyin"] . '_' . $allPage . $linkUri  . '.html"><div class="page-num">' . $allPage . '</div></a>';
                } else if ($uriData["page"] + 4 >= $allPage) {
                    echo '<a href="' . $uriData["pinyin"] . $linkUri . '.html"><div class="page-num">' . $i . '</div></a>';
                    echo '<div class="page-long">...</div>';
                    for ($i = $allPage - 10; $i <= $allPage; $i++) {
                        if ($uriData["page"] == $i) {
                            echo '<div class="page-num-hot">' . $i . '</div>';
                        } else {
                            echo '<a href="' . $uriData["pinyin"] . '_' . $i . $linkUri  . '.html"><div class="page-num">' . $i . '</div></a>';
                        }
                    }
                } else {
                    echo '<a href="' . $uriData["pinyin"] . $linkUri . '.html"><div class="page-num">' . $i . '</div></a>';
                    echo '<div class="page-long">...</div>';
                    for ($i = $uriData["page"] - 3; $i <= $uriData["page"] + 4; $i++) {
                        if ($uriData["page"] == $i) {
                            echo '<div class="page-num-hot">' . $i . '</div>';
                        } else {
                            echo '<a href="' . $uriData["pinyin"] . '_' . $i . $linkUri  . '.html"><div class="page-num">' . $i . '</div></a>';
                        }
                    }
                    echo '<div class="page-long">...</div>';
                    echo '<a href="' . $uriData["pinyin"] . '_' . $allPage . $linkUri  . '.html"><div class="page-num">' . $allPage . '</div></a>';
                }
                ?>
            </div>
            <div class="about-topic">
                <span>相关专栏</span>
                <?php
                $nowLength = 0;
                $tagId = 0;
                while ($nowLength < 80) {
                    $tagId++;
                    if (sizeof($tags) - 1 < $tagId) {
                        break;
                    }
                    $nowLength += mb_strlen($tags[$tagId]["_source"]["tag"]) + 1;
                    if ($nowLength > 80) {
                        break;
                    }
                    echo '<a href="../tag/' . $tags[$tagId]["_source"]["pinyin"] . '.html" target="_blank" class="about-topic-link">' . $tags[$tagId]["_source"]["tag"] . '</a>';
                }
                ?>
                <div class="ppt-box-clear"></div>
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
        <a href="../buy/vip.html" target="_blank">
            <div class="first-use"></div>
        </a>
        <i class="mark-close" onclick="$('#first-use-box').css('display', 'none');">×</i>
    </div>

    <!--手势验证sdk-->
    <script src="https://v-cn.vaptcha.com/v3.js"></script>
    <script src="../js/login.js"></script>
    <script>
        buildChoose();
    </script>

</body>

</html>