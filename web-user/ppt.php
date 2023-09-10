<?php
require_once 'api/php_tool.php';
$timestamp = msectime();
$uid = findPPTUid();
$pptByUid = searchPPTByUid($uid, 'MBG');
if (!isset($pptByUid["_source"])) {
    require '404.php';
    exit;
}
$ppt = $pptByUid["_source"];
$designerId = $ppt["designer_id"];
if ($designerId == null || $designerId < 0) {
    $designerId = $ppt['uid'] % 50 + 1;
}
$designer = findDesigner($designerId);
$nearly = nearlyPPT($ppt["title"], $ppt["uid"])["hits"]["hits"];
$tags = nearlyTags($ppt["title"])["hits"]["hits"];
$tagId = 0;
$pptTags = findTags($ppt["tags"])["hits"]["hits"];
?>

<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <meta charset="UTF-8">
    <title><?php echo $ppt["title"] ?>-模板狗</title>
    <meta name="keywords" content="<?php
    for ($i = 0; $i < sizeof($pptTags); $i++) {
        if ($i > 0) {
            echo ',';
        }
        echo $pptTags[$i]["_source"]["tag"].'PPT';
    }
    ?>">
    <meta name="description" content="<?php echo $ppt["about_text"] ?>">
    <meta name="viewport" content="width=device-width">
    <link rel="shortcut icon" href="../favicon.ico">
    <link rel="stylesheet" type="text/css" href="../css/global.css">
    <link rel="stylesheet" type="text/css" href="../css/list.css">
    <link rel="stylesheet" type="text/css" href="../css/inner.css">
    <link rel="stylesheet" type="text/css" href="../css/mark.css">
    <script>let uid = '<?php echo $uid?>'</script>
    <script src="../js/jquery.min.js"></script>
    <script src="../js/global.js"></script>
    <script src="../js/item.js"></script>
</head>

<body>
    <div class="head" id="search-head">
        <div class="head-main">
            <div class="head-logo">
                <a href="<? echo getHost() ?>" target="_blank">
                    <img src="../img/logo-inner.png" alt="模板狗" class="head-logo-pic">
                    <img src="../img/img-collection.png" alt="模板狗" class="img-collection">
                </a>
            </div>
            <div class="head-tab">
                热门专题
                <div class="head-hot-topic">
                    <?php
                    for ($tagId = 0; $tagId < 10; $tagId++) {
                        if (sizeof($tags) - 1 < $tagId) {
                            break;
                        }
                        echo '<a href="../tag/' . $tags[$tagId]["_source"]["pinyin"] . '.html" target="_blank" class="head-hot-topic-a"><div class="head-hot-topic-once"><i class="head-top-' . ($tagId + 1) . '">' . ($tagId + 1) . '</i>' . $tags[$tagId]["_source"]["tag"] . '</div></a>';
                    }
                    ?>
                    <div class="clear"></div>
                    <a href="../about/<?php echo $ppt["uid"] ?>.html" target="_blank" class="head-hot-link">查看全部>></a>
                </div>
            </div>
            <div class="head-about">
                相似推荐
                <div class="head-hot-ppt">
                    <?php
                    $number = sizeof($nearly) <= 4 ? sizeof($nearly) : 4;
                    for ($i = 0; $i < $number; $i++) {
                        $about_ppt = $nearly[$i]["_source"];
                    ?>
                        <a href="<?php echo $about_ppt["uid"] ?>.html" target="_blank">
                            <div class="ppt">
                                <div class="img-box"><img src="<?php echo $about_ppt["pic_url"] ?>?imageView2/4/w/500/h/300/format/webp/q/100" alt="<?php echo $about_ppt["title"] ?>"></div>
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
                    <div class="clear"></div>
                    <a href="../nearly/<?php echo $ppt["uid"] ?>.html" target="_blank" class="head-hot-link">查看全部>></a>
                </div>
            </div>
            <div class="head-search">
                <label>
                    <input placeholder="不想找太久 就用模板狗" id="head-input">
                </label>
                <div class="head-search-btn" onclick="search($('#head-input').val())">搜索</div>
            </div>
            <div class="head-user">
                <div class="head-user-box" id="member-login" style="display: block">
                    <img src="../img/touxiang.jpg" alt="我的账户">
                    <div class="user-box">
                        <div class="user-box-name"></div>
                        <div class="user-box-vip"></div>
                        <?php /*<a href="../buy/vip.html" target="_blank" rel="nofollow">
                            <div class="user-box-once">开通VIP</div>
                        </a>*/ ?>
                        <a href="../user.html" target="_blank">
                            <div class="user-box-once">个人中心</div>
                        </a>
                        <div class="user-box-once" onclick="loginExit()">退出</div>
                    </div>
                </div>
                <div class="head-login" onclick="loginShow()" id="member-not-login" style="display: none">
                    <div class="head-login-region">注册</div>
                    <div class="head-login-login">登录</div>
                </div>
            </div>
            <div class="head-phone-vip" onclick="buyVip()"></div>

            <?php /*<div class="head-vip" onclick="buyVip()">
                <div class="head-vip-hot">商用授权</div>
                <div class="head-vip-page">
                    <div class="head-vip-page-title">商用VIP开通</div>
                    <div class="head-vip-page-title-tip">一次开通企业和个人都可商用，更划算</div>
                    <div class="head-vip-page-box">
                        <div class="head-vip-page-once">模板下载</div>
                        <div class="head-vip-page-once">个人商业</div>
                        <div class="head-vip-page-once">雇主商用</div>
                        <div class="head-vip-page-once">专属客服</div>
                        <div class="head-vip-page-once">高速下载</div>
                        <div class="head-vip-page-once">会员标识</div>
                    </div>
                    <div class="head-vip-go">
                        <div class="head-vip-bug-999">
                            <div class="head-vip-buy-hot">推荐</div>
                            <div class="head-vip-buy-title">终身</div>
                            <div class="head-vip-buy-price"><span>￥</span>199</div>
                            <div class="head-vip-buy-go">20次下载/天</div>
                        </div>
                        <div class="head-vip-bug-365">
                            <div class="head-vip-send">买一年,送一年</div>
                            <div class="head-vip-buy-title">年卡</div>
                            <div class="head-vip-buy-price"><span>￥</span>99</div>
                            <div class="head-vip-buy-go">15次下载/天</div>
                        </div>
                        <div class="head-vip-bug-30">
                            <div class="head-vip-buy-title">月卡</div>
                            <div class="head-vip-buy-price"><span>￥</span>39</div>
                            <div class="head-vip-buy-go">5次下载/天</div>
                        </div>
                    </div>
                </div>
            </div>*/ ?>
        </div>
    </div>
    <div class="head" id="title-head">
        <div class="head-title-main">
            <div class="title-box">
                <h1 class="title"><?php echo $ppt["title"] ?></h1>
                <div class="baidu-search" onclick='javascript:window.open("https://www.baidu.com/s?ie=UTF-8&wd=<?php echo urlencode($ppt["title"]) ?>%20site%3Amubangou.com")'>百度一下</div>
            </div>
            <div class="head-sale">
                <?php /*<a href="../buy/<?php echo $ppt["uid"] ?>.html" target="_blank" class="can-not-down" rel="nofollow">
                    <div class="sale-buy">以18.9元购买此模板<div class="sale-buy-hot">商用授权</div>
                    </div>
                </a>
                <a href="../buy/vip.html" target="_blank" class="can-not-down" rel="nofollow">
                    <div class="sale-down">升级会员免费下载</div>
                </a>*/ ?>
                <div class="sale-down can-not-down" onclick="loginShow()">登录后免费下载</div>
                <div class="sale-down can-down">立即下载</div>
            </div>
        </div>
    </div>
    <div class="content">
        <div class="main">
            <div class="about-topic">
                <span>相关专栏</span>
                <?php
                $nowLength = 0;
                while ($nowLength < 75) {
                    $tagId++;
                    $nowLength += mb_strlen($tags[$tagId]["_source"]["tag"]) + 1;
                    if ($nowLength > 75) {
                        break;
                    }
                    if (sizeof($tags) - 1 < $tagId) {
                        break;
                    }
                    echo '<a href="../tag/' . $tags[$tagId]["_source"]["pinyin"] . '.html" target="_blank" class="about-topic-link">' . $tags[$tagId]["_source"]["tag"] . '</a>';
                }
                ?>
                <a href="../about/<?php echo $ppt["uid"] ?>.html" target="_blank" class="about-topic-all">查看全部相关专栏>></a>
                <div class="ppt-box-clear"></div>
            </div>
            <div class="inner-crumbs">
                <a href="../" target="_blank">PPT模板</a>
                <span> > </span>
                <?php
                echo '<a href="../tag/' . $tags[0]["_source"]["pinyin"] . '.html" target="_blank">' . $tags[0]["_source"]["tag"] . '</a>';
                ?>
                <span> > </span>
                <a href="" target="_blank"><?php echo $ppt["title"] ?></a>
            </div>
            <div class="title-box">
                <h1 class="title"><?php echo $ppt["title"] ?></h1>
                <div class="baidu-search" onclick='javascript:window.open("https://www.baidu.com/s?ie=UTF-8&wd=<?php echo urlencode($ppt["title"]) ?>%20site%3Amubangou.com")'>百度一下</div>
                <div class="title-tab-box">
                    <div class="ppt-time">发布时间: <?php echo $ppt["create_time"] ?></div>
                    <?php
                    for ($i = 0; $i < sizeof($pptTags); $i++) {
                        echo '<a href="../tag/' . $pptTags[$i]["_source"]["pinyin"] . '.html" target="_blank"><div class="title-tab">' . $pptTags[$i]["_source"]["tag"] . '</div></a>';
                    }
                    ?>
                    <div class="ppt-box-clear"></div>
                </div>
                <div class="ppt-box-clear"></div>
            </div>
            <div class="main-show">
                <div class="left-pic">
                    <video src="<?php echo $ppt["video_url"] ?>" controls="controls" loop="loop" autoplay="autoplay" muted poster="<?php echo $ppt["pic_url"] ?>?imageMogr2/crop/x720"></video>
                    <img src="<?php echo $ppt["pic_url"] ?>" alt="<?php echo $ppt["title"] ?>">
                    <div class="left-foot-tab">
                        <div class="left-foot-down" onclick="showDownBox()">下载作品<i></i></div>
                        <div class="left-foot-about" onclick="showAboutBox()">作品介绍<i></i></div>
                        <div class="left-complaint" onclick="goComplaint()">侵权投诉</div>
                    </div>
                    <div class="left-foot-down-box">
                        <?php /*<a href="../buy/vip.html" target="_blank" class="can-not-down" rel="nofollow">
                            <div class="sale-down">升级会员免费下载</div>
                        </a>
                        <a href="../buy/<?php echo $ppt["uid"] ?>.html" target="_blank" class="can-not-down" rel="nofollow">
                            <div class="sale-buy">以18.9元购买此模板<div class="sale-buy-hot">商用授权</div>
                            </div>
                        </a>*/ ?>
                        <div class="sale-down can-not-down" onclick="loginShow()">登录后免费下载</div>
                        <div class="sale-down can-down">立即下载</div>
                        <div class="ppt-box-clear"></div>
                    </div>
                    <div class="left-foot-about-box">
                        <p><?php echo $ppt["about_text"] ?></p>
                        <p>图片水印: 图片水印仅用于防盗防刷，无其他含义。实际文件不包含水印图片。</p>
                        <div class="ppt-box-clear"></div>
                    </div>
                </div>
                <div class="right-down">
                    <div class="sale" style="height: 60px;">
                        <div class="sale-safe">原创素材 | 允许商业用途</div>
                        <?php /*
                        <div class="sale-price">需支付: ￥<span class="sale-price-num">18.9</span><span class="sale-price-free">会员免费</span></div>
                        <a href="../buy/vip.html" target="_blank" class="can-not-down" rel="nofollow">
                            <div class="sale-down">升级会员免费下载</div>
                        </a>
                        <a href="../buy/<?php echo $ppt["uid"] ?>.html" target="_blank" class="can-not-down" rel="nofollow">
                            <div class="sale-buy">以18.9元购买此模板</div>
                        </a>*/ ?>
                        <div class="sale-down can-not-down" onclick="loginShow()" style="margin-top: 17px;">登录后免费下载</div>
                        <div class="sale-down can-down" style="margin-top: 17px;">立即下载</div>
                    </div>
                    <!--<div class="right-yx"></div>-->
                    <div class="about">
                        <div class="about-title">模板详情</div>
                        <div class="about-once">
                            <div class="about-once-title">模板作者</div>
                            <div class="about-once-text"><?php echo $designer["designer_name"]?> <a href="../designer/<?php echo $designerId?>.html" target="_blank">Ta的作品集 >></a></div>
                            <div class="clear"></div>
                        </div>
                        <div class="about-once">
                            <div class="about-once-title">模板页数</div>
                            <div class="about-once-text"><?php echo $ppt["page_size"] ?>页</div>
                            <div class="clear"></div>
                        </div>
                        <div class="about-once">
                            <div class="about-once-title">模板格式</div>
                            <div class="about-once-text"><?php echo $ppt["file_format"] ?></div>
                            <div class="clear"></div>
                        </div>
                        <?php /*
                        <div class="about-once">
                            <div class="about-once-title">授权形式</div>
                            <div class="about-once-text">企业商用授权</div>
                            <div class="clear"></div>
                        </div>
                        <div class="about-tip">
                            本模板授权主体仅为PPT本身设计部分。其中字体、图片、视频、音乐均来自第三方平台，嵌入模板仅为展示模板效果，此类素材溯源地址见本页，您无需购买模板即可下载。
                            这些资源我们仅作为效果展示且并未用于商业用途，亦不在购买后的授权范围内。
                            如果您不希望我们使用您的素材进行展示效果可以发送邮件到 pptmuban@88.com，我们会尽快修改。
                        </div>*/ ?>
                    </div>
                    <div class="source">
                        <div class="source-title">
                            <div class="source-title-main">版权溯源</div>
                            <div class="source-title-tip">版权可溯源，使用更安全</div>
                        </div>
                        <?php
                        foreach ($ppt["source_json"] as $source) {
                        ?>
                            <div class="source-once">
                                <div class="source-once-title">
                                    <?php echo $source["name"] ?>
                                </div>
                                <div class="source-name">
                                    <?php echo $source["from"] ?>
                                </div>
                                <a href="<?php echo $source["link"] ?>" target="_blank" rel="nofollow">
                                    <div class="source-link">可商用溯源链接</div>
                                </a>
                                <div class="source-clear"></div>
                            </div>
                        <?PHP
                        }
                        ?>
                        <div class="source-show-all">展开全部</div>
                    </div>
                    <div class="recommend">
                        <div class="recommend-title">相关推荐</div>
                        <?php
                        $size = sizeof($nearly);
                        $number = 1;
                        $begin = 0;
                        if ($size <= 4) {
                            $number = $size;
                            $begin = 0;
                        } else if ($size <= 20) {
                            $number = $size;
                            $begin = 4;
                        } else {
                            $number = 22;
                            $begin = 4;
                        }
                        for ($i = $begin; $i < $number; $i++) {
                            $about_ppt = $nearly[$i]["_source"];
                        ?>
                            <a href="<?php echo $about_ppt["uid"] ?>.html" target="_blank">
                                <div class="ppt">
                                    <div class="img-box"><img src="<?php echo $about_ppt["pic_url"] ?>?imageView2/4/w/500/h/200/format/webp/q/100" alt="<?php echo $about_ppt["title"] ?>"></div>
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
                        <div class="ppt-box-clear"></div>
                        <a href="../nearly/<?php echo $ppt["uid"] ?>.html" target="_blank" class="about-ppt-all">查看全部相关模板>></a>
                    </div>
                </div>
                <div class="main-clean"></div>
            </div>
        </div>
        <div class="list-box">
            <div class="ppt-box">
                <?php
                for ($i = $size - 1; $i > 0 && $i >= $size - 12; $i--) {
                    $about_ppt = $nearly[$i]["_source"];
                ?>
                    <a href="<?php echo $about_ppt["uid"] ?>.html" target="_blank">
                        <div class="ppt">
                            <div class="img-box"><img src="<?php echo $about_ppt["pic_url"] ?>?imageView2/4/w/500/h/300/format/webp/q/100" alt="<?php echo $about_ppt["title"] ?>"></div>
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
                <a href="../" target="_blank"><div class="ppt-more">去首页<br>更多模板<i></i></div></a>
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
                <div class="wrap-title-once" id="link-box-title-2"><a href="../" target="_blank">PPT下载</a></div>
                <div class="wrap-title-once" id="link-box-title-3"><a href="../" target="_blank">PPT模板下载</a></div>
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

    
    <div class="phone-buy">
    <?php /*
        <a href="../buy/<?php echo $ppt["uid"] ?>.html" target="_blank" class="can-not-down" rel="nofollow">
            <div class="sale-buy">以18.9元购买此模板<div class="sale-buy-hot">商用授权</div>
            </div>
        </a>
        <a href="../buy/vip.html" target="_blank" class="can-not-down" rel="nofollow">
            <div class="sale-down">升级会员免费下载</div>
        </a> */?>
        <div class="sale-down can-not-down" onclick="loginShow()">登录后免费下载</div>
        <div class="sale-down can-down">立即下载</div>
    </div>

    <?php echo getFoot($timestamp);?>

    <?php echo getLogin()?>
    
    <div class="black-mark" id="first-use-box" style="display: none">
        <a href="buy/vip.html" target="_blank" rel="nofollow">
            <div class="first-use"></div>
        </a>
        <i class="mark-close" onclick="$('#first-use-box').css('display', 'none');">×</i>
    </div>

    <!--手势验证sdk-->
    <script src="https://v-cn.vaptcha.com/v3.js"></script>
    <script src="../js/login.js"></script>
</body>

</html>