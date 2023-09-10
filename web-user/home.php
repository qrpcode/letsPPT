<?php
require 'api/php_tool.php';
$timestamp = msectime();
$onceSize = 50;
$ppts = homePPTShow($onceSize * 4)["hits"]["hits"];
$tags = randTag(50)["hits"]["hits"];
?>
<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <meta charset="UTF-8">
    <meta name="sogou_site_verification" content="HA81BANKQi">
    <meta name="viewport" content="width=device-width">
    <title>PPT模板_PPT模版下载_免费PPT模板下载 - 模板狗</title>
    <meta name="keywords" content="PPT模板,免费PPT模板,PPT下载,PPT模板下载,PPT素材,幻灯片模板,PPT,模板狗">
    <meta name="description" content="模板狗为您提供海量PPT模板下载资源，您可以方便快捷免费的下载幻灯片模板、PPT素材等资源，同时我们提供高效的PPT模板搜索技术，方便您快捷的下载PPT模板资源，不想找太久就用模板狗。">
    <link rel="stylesheet" type="text/css" href="css/index.css">
    <link rel="stylesheet" type="text/css" href="css/list.css">
    <link rel="stylesheet" type="text/css" href="css/global.css">
    <link rel="stylesheet" type="text/css" href="css/mark.css">
    <script src="js/jquery.min.js"></script>
    <script src="js/global.js"></script>
    <script>
        let pageList = [];
        let colorList = [];
        let styleList = [];
    </script>
</head>

<body>
    <div class="main-bg">
        <div class="crumbs">
            <div class="crumbs-left">
            </div>
            <div class="logo-box">
                <img src="img/logo.png" alt="模板狗-不想找太久，就用模板狗">
                <div class="logo-slogan">不想找太久，就用模板狗</div>
            </div>
            <div class="crumbs-right">
                <div class="index-user-box" id="index-member-login" style="display: none">
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
                <div class="index-login" onclick="loginShow()" id="index-member-not-login" style="display: none">
                    <div class="index-login-region">注册</div>
                    <div class="index-login-login">登录</div>
                </div>
            </div>
        </div>
        <div class="vip-box">
            <?php /*<a href="buy/vip.html" target="_blank">*/ ?>
                <div class="vip-tip">全站原创PPT模板登录即可免费下载</div>
                <div class="vip-buy-button" onclick="loginShow()">立即登录</div>
                <div class="vip-buy-button-min" onclick="loginShow()">登录即可下载</div>
                <div class="vip-sale"><span class="vip-fff">完整素材引用溯源 </span>支持 <span class="vip-big"> 商业用途</span></div>
            <?php /*</a>*/?>
        </div>
        <div class="search-box">
            <div class="search-input-box">
                <div class="search-box-input">
                    <input placeholder="搜索可商用的PPT好模板" id="home-big-input">
                </div>
                <div class="search-input-button" onclick="searchHome($('#home-big-input').val())">搜索</div>
            </div>
            <div class="search-hot">
                <span class="search-hot-title">热门搜索</span>
                <?php
                for ($i = 10; $i <= 15; $i++) {
                    $tag = $tags[$i]["_source"];
                    echo '<a href="tag/' . $tag['pinyin'] . '.html" target="_blank"><span class="search-hot-keyword">' . $tag['tag'] . '</span></a>';
                }
                ?>
            </div>
            <div class="search-choose">
                <div class="choose-title-box">
                    <div class="choose-title">我希望模板有<span>选填</span></div>
                </div>
                <div class="choose-list">
                    <div class="choose-box" id="home-page-select-1" onclick="homePageChoose(1)">
                        <div class="choose-icon">
                            <img src="img/icon/home_1.png" alt="折线图">
                        </div>
                        <div class="choose-icon-title">折线图</div>
                    </div>
                    <div class="choose-box" id="home-page-select-2" onclick="homePageChoose(2)">
                        <div class="choose-icon">
                            <img src="img/icon/home_2.png" alt="柱状图">
                        </div>
                        <div class="choose-icon-title">柱状图</div>
                    </div>
                    <div class="choose-box" id="home-page-select-3" onclick="homePageChoose(3)">
                        <div class="choose-icon">
                            <img src="img/icon/home_3.png" alt="扇形图">
                        </div>
                        <div class="choose-icon-title">扇形图</div>
                    </div>
                    <div class="choose-box" id="home-page-select-4" onclick="homePageChoose(4)">
                        <div class="choose-icon">
                            <img src="img/icon/home_4.png" alt="组织架构">
                        </div>
                        <div class="choose-icon-title">组织架构</div>
                    </div>
                    <div class="choose-box" id="home-page-select-5" onclick="homePageChoose(5)">
                        <div class="choose-icon">
                            <img src="img/icon/home_5.png" alt="时间轴">
                        </div>
                        <div class="choose-icon-title">时间轴</div>
                    </div>
                    <div class="choose-box" id="home-page-select-6" onclick="homePageChoose(6)">
                        <div class="choose-icon">
                            <img src="img/icon/home_6.png" alt="对比介绍">
                        </div>
                        <div class="choose-icon-title">对比介绍</div>
                    </div>
                    <div class="choose-box" id="home-page-select-7" onclick="homePageChoose(7)">
                        <div class="choose-icon">
                            <img src="img/icon/home_7.png" alt="引言页">
                        </div>
                        <div class="choose-icon-title">引言页</div>
                    </div>
                    <div class="choose-box" id="home-page-select-8" onclick="homePageChoose(8)">
                        <div class="choose-icon">
                            <img src="img/icon/home_8.png" alt="金字塔">
                        </div>
                        <div class="choose-icon-title">金字塔</div>
                    </div>
                    <div class="choose-box" id="home-page-select-9" onclick="homePageChoose(9)">
                        <div class="choose-icon">
                            <img src="img/icon/home_9.png" alt="荣誉页">
                        </div>
                        <div class="choose-icon-title">荣誉页</div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="choose-title-box">
                    <div class="choose-title">我希望模板颜色是<span>选填</span></div>
                    <div class="color-tab-box">
                        <div class="color-tab color-4" style="background-color: #BD120F" onclick="colorChoose(4)"></div>
                        <div class="color-tab color-5" style="background-color: #F05246" onclick="colorChoose(5)"></div>
                        <div class="color-tab color-6" style="background-color: #853DE4" onclick="colorChoose(6)"></div>
                        <div class="color-tab color-7" style="background-color: #3628A5" onclick="colorChoose(7)"></div>
                        <div class="color-tab color-8" style="background-color: #3C7DF3" onclick="colorChoose(8)"></div>
                        <div class="color-tab color-9" style="background-color: #62E3C6" onclick="colorChoose(9)"></div>
                        <div class="color-tab color-10" style="background-color: #67CD80" onclick="colorChoose(10)"></div>
                        <div class="color-tab color-11" style="background-color: #D7F960" onclick="colorChoose(11)"></div>
                        <div class="color-tab color-12" style="background-color: #F9CC9E" onclick="colorChoose(12)"></div>
                        <div class="color-tab color-13" style="background-color: #E7B730" onclick="colorChoose(13)"></div>
                        <div class="color-tab color-14" style="background-color: #E88C30" onclick="colorChoose(14)"></div>
                        <div class="color-tab color-15" style="background-color: #C67D54" onclick="colorChoose(15)"></div>
                        <div class="color-tab color-16" style="background-color: #7B4512" onclick="colorChoose(16)"></div>
                        <div class="color-tab color-2" style="background-color: #000000" onclick="colorChoose(2)"></div>
                        <div class="color-tab color-17" style="background-color: #E0E0E0" onclick="colorChoose(17)"></div>

                        <div class="del-all-block" title="清除筛选" onclick="colorNoChoose()">清除筛选</div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="choose-title-box">
                    <div class="choose-title">我希望模板风格是<span>选填</span></div>
                    <div class="type-box">
                        <div class="type-once" onclick="styleChoose(1)" id="style-1">商务风<span>×</span></div>
                        <div class="type-once" onclick="styleChoose(2)" id="style-2">简约风<span>×</span></div>
                        <div class="type-once" onclick="styleChoose(3)" id="style-3">党建风<span>×</span></div>
                        <div class="type-once" onclick="styleChoose(4)" id="style-4">中国风<span>×</span></div>
                        <div class="type-once" onclick="styleChoose(5)" id="style-5">创意风<span>×</span></div>
                        <div class="type-once" onclick="styleChoose(6)" id="style-6">科技风<span>×</span></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>
    </div>
    <div class="clear"></div>

    <div class="content">
        <div class="list-box">
            <div class="list-tab-box">
                <div class="list-tab-hot" id="home-box-0">特别推荐</div>
                <div class="list-tab" id="home-box-1">最新上传</div>
                <div class="list-tab" id="home-box-2">热门下载</div>
                <div class="list-tab" id="home-box-3">大家在看</div>
            </div>
            <div class="ppt-box">
                <?php
                $number = sizeof($ppts) <= $onceSize * 4 ? sizeof($ppts) : $onceSize * 4;
                for ($i = 0; $i < $number; $i++) {
                    $about_ppt = $ppts[$i]["_source"];
                ?>
                    <a href="ppt/<?php echo $about_ppt["uid"] ?>.html" target="_blank">
                        <div class="ppt box-<?php echo $i % 4 ?>">
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
                <div class="ppt-box-clear"></div>
            </div>
            <div class="list-page">
                <div class="page-num-hot">1</div>
                <a href="https://mubangou.com/tag/PPTmoban_2.html"><div class="page-num">2</div></a>
                <a href="https://mubangou.com/tag/PPTmoban_3.html"><div class="page-num">3</div></a>
                <a href="https://mubangou.com/tag/PPTmoban_4.html"><div class="page-num">4</div></a>
                <a href="https://mubangou.com/tag/PPTmoban_5.html"><div class="page-num">5</div></a>
                <a href="https://mubangou.com/tag/PPTmoban_6.html"><div class="page-num">6</div></a>
                <a href="https://mubangou.com/tag/PPTmoban_7.html"><div class="page-num">7</div></a>
                <a href="https://mubangou.com/tag/PPTmoban_8.html"><div class="page-num">8</div></a>
                <a href="https://mubangou.com/tag/PPTmoban_9.html"><div class="page-num">9</div></a>
                <a href="https://mubangou.com/tag/PPTmoban_10.html"><div class="page-num">10</div></a>
                <a href="https://mubangou.com/tag/PPTmoban_11.html"><div class="page-num">11</div></a>
                <a href="https://mubangou.com/tag/PPTmoban_12.html"><div class="page-num">12</div></a>
            </div>
        </div>
    </div>
    <?php echo getFoot($timestamp, true);?>

    <div class="head" id="search-head" style="display: none">
        <?php echo getNormalHead($tags, 'ppt')?>
    </div>

    <?php echo getLogin()?>

    <div class="black-mark" id="first-use-box" style="display: none">
        <a href="buy/vip.html" target="_blank">
            <div class="first-use"></div>
        </a>
        <i class="mark-close" onclick="$('#first-use-box').css('display', 'none');">×</i>
    </div>

    <!--手势验证sdk-->
    <script src="https://v-cn.vaptcha.com/v3.js"></script>
    <script src="js/login.js?262"></script>
    <script src="js/home.js"></script>

</body>

</html>