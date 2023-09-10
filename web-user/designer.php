<?php
require_once 'api/php_tool.php';
$designerPageSize = 50;
$timestamp = msectime();
$uri = analysisDesignerLink();
if ($uri == null || $uri["uid"] == '') {
    require '404.php';
    exit;
}
$designer = findDesigner($uri["uid"]);
if (!isset($designer["designer_name"])) {
    require '404.php';
    exit;
}
$name = $designer["designer_name"];
$designerId = $designer["id"];
$ppts = homePPTShow($onceSize * 4)["hits"]["hits"];
$tags = randTag(50)["hits"]["hits"];
$designerPPTReturn = findDesignerPPTs($designerId, $designerPageSize, $uri["page"]);
$designerPPTs = $designerPPTReturn["hits"]["hits"];
/*if (sizeof($designerPPTs) == 0) {
    require '404.php';
    exit;
}*/
$total = $designerPPTReturn["hits"]["total"]["value"];
$total = $total >= 10000 ? 10000 : $total;

$allPage = ceil(1.00 * $total / $designerPageSize);
?>
<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <meta charset="UTF-8">
    <title><?php echo $name ?>的PPT模板-模板狗</title>
    <meta name="keywords" content="<?php echo $name ?>,<?php echo $name ?>主页,<?php echo $name ?>设计师,<?php echo $name ?>PPT模板">
    <meta name="description" content="<?php echo $name ?>设计师在模板狗平台设计了诸多PPT模板作品，您可以通过访问<?php echo $name ?>的主页了解他的PPT模板设计风格和擅长的PPT模板设计领域，您可以下载<?php echo $name ?>设计的PPT模板资源。">
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" type="text/css" href="../css/global.css">
    <link rel="stylesheet" type="text/css" href="../css/list.css">
    <link rel="stylesheet" type="text/css" href="../css/inner.css">
    <script src="../js/jquery.min.js"></script>
    <script src="../js/global.js"></script>
</head>

<body>
    <div class="head" id="search-head">
    <?php echo getNormalHead($tags, 'ppt')?>
    </div>
    <div class="content">
        <div class="main">
            <div class="inner-crumbs">
                <a href="../" target="_blank">PPT模板</a>
                <span> > </span>
                <a href="" target="_blank"><?php echo $name ?></a>
            </div>
            <div class="designer-box">
                <div class="designer-user-img">
                    <img src="../img/user/user2.png" alt="用户">
                </div>
                <div class="designer-right">
                    <div class="designer-name"><?php echo $name ?></div>
                    <div class="designer-show">
                        <?php
                        if ($designerId % 3 == 0) {
                            echo '<span><i></i>2022创作之星<i></i></span>';
                        }
                        if ($designerId % 3 == 1) {
                            echo '<span><i></i>热门模板创作者<i></i></span>';
                        }
                        if ($designerId % 3 == 2) {
                            echo '<span><i></i>分享之星<i></i></span>';
                        }
                        if ($designerId % 2 == 0) {
                            echo '<span><i></i>模板宗师<i></i></span>';
                        }
                        if ($designerId % 2 == 1) {
                            echo '<span><i></i>勤写标兵<i></i></span>';
                        }
                        ?>
                        <span><i></i>优秀创作者<i></i></span>
                    </div>
                </div>
                <div class="clear"></div>
            </div>
        </div>
        <div class="list-box">
            <div class="ppt-box">
                <?php
                for ($i = 0; $i < sizeof($designerPPTs); $i++) {
                    $about_ppt = $designerPPTs[$i]["_source"];
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
                if ($uri["page"] != $allPage) {
                    echo '<a href="'.$uri["uid"].'_'.($uri["page"] + 1).'.html" target="_blank"><div class="ppt-more">下一页<br>更多模板<i></i></div></a>';
                }
                ?>
                <div class="ppt-box-clear"></div>
            </div>
            <div class="list-page">
                <?echo aboutAndNearlyPage($allPage, $uri);?>
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
        <div class="first-use" onclick="buyVip()"></div>
        <i class="mark-close" onclick="$('#first-use-box').css('display', 'none');">×</i>
    </div>

    <!--手势验证sdk-->
    <script src="https://v-cn.vaptcha.com/v3.js"></script>
    <script src="../js/login.js"></script>

</body>

</html>