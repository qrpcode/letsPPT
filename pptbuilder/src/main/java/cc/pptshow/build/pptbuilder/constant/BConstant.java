package cc.pptshow.build.pptbuilder.constant;

import cc.pptshow.build.pptbuilder.domain.ChannelConfig;
import cc.pptshow.build.pptbuilder.util.ConfigUtil;
import cc.pptshow.ppt.constant.Constant;
import cc.pptshow.ppt.domain.animation.InAnimationType;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class BConstant {
    public static final Map<String, String> WATER_MARK = Maps.newHashMap();

    public static Map<String, ChannelConfig> config = Maps.newHashMap();

    public static final String SYS_PATH;

    public static String BAIDU_NLP_TOKEN;

    static {
        FileReader fileReader = new FileReader("C:\\ppt_build_path.txt");
        SYS_PATH = fileReader.readString();

        WATER_MARK.put("MBG", SYS_PATH + "water.png");
        WATER_MARK.put("KUAI", SYS_PATH + "water.png");
        WATER_MARK.put("GLOBAL", SYS_PATH + "water.png");

        ConfigUtil.flushConfig();

        log.info("[缓存清理] {} 正在清理缓存...", System.getProperty("java.io.tmpdir") + Constant.PATH);
        try {
            FileUtil.clean(System.getProperty("java.io.tmpdir") + Constant.PATH);
        } catch (Throwable t) {
            log.info("[缓存清理错误]", t);
        }
        log.info("[缓存清理] 缓存清理成功");
    }

    public static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static final String ACCESS_KEY = "****************";
    public static final String SECRET_KEY = "****************"; // AccessKey的值

    public static final String IMG_SYS_PATH = SYS_PATH + "img\\";
    public static final String MUSIC_SYS_PATH = SYS_PATH + "music\\";

    public static final double PAGE_WIDTH = 33.87;
    public static final double PAGE_HEIGHT = 19.05;
    public static final double SIZE_PX = 85.03100088574;

    public static final int PAGE_WIDTH_UNIT = 3387;
    public static final int PAGE_HEIGHT_UNIT = 1905;

    /**
     * 元素占整个屏幕的至少多少倍就被视为背景元素
     */
    public static final double MIN_BG_SIZE = 0.6;
    public static final double HALF = 0.5;

    public static final double HOME_TITLE_TOP = 4.9;
    public static final double HOME_TITLE_BOTTOM = 10.9;

    public static final double PAGE_CORNER_MAX_X = 0.25;
    public static final double PAGE_CORNER_MAX_Y = 0.33;

    public static final String YEAR_TEXT = "20NN";
    public static final String DATE_TEXT = "20NN-01-01";

    public static final String BLANK = " ";
    public static final Splitter BLANK_SPLITTER = Splitter.on(BLANK);

    public static final String SPLIT = ",";
    public static final Splitter SPLITTER = Splitter.on(SPLIT);
    public static final Joiner JOINER = Joiner.on(SPLIT);

    public static final String PPT_TIP = "请在灰色区域内创作，请勿移动灰色区域！";
    public static final String TEMPLATE_COLOR = "DDDDDD";
    public static final String DEFAULT_FONT_COLOR = "333333";

    public static final double FROM_SIZE = 0.0;

    public static final double TEXT_MIN_BACKGROUND = 0.8;

    public static final String WHITE = "FFFFFF";

    public static final String COLOR_HEAD = "#";

    public static final String PAGE_ELEMENT = "page";
    public static final String GROUP = "group";

    /**
     * 文本经常会因为字体等原因出现原有的一行长度放不下，所以需要乘上一个倍数
     */
    public static final double MORE_WIDTH_TEXT = 1.2;

    public static final double MAX_WIDTH = 33.87;
    public static final double MAX_HEIGHT = 19.05;

    public static final String ICON = "icon";
    public static final String LOGO = "logo";

    /**
     * 首页在db里面存储的信息
     */
    public static final int HOME_PAGE_ID = 3;
    /**
     * 大标题页在db里面存储的id
     */
    public static final int BIG_TITLE_PAGE_ID = 4;
    /**
     * 页面标题在db里面存储的信息
     */
    public static final int TITLE_PAGE_ID = 11;

    /**
     * 获取以系统分隔符结尾的系统缓存路径
     */
    public static final String TMPDIR = System.getProperty("java.io.tmpdir").endsWith(Constant.SEPARATOR) ?
            System.getProperty("java.io.tmpdir") : System.getProperty("java.io.tmpdir") + Constant.SEPARATOR;

    public static final String NULL_COLOR = "NULL";

    /**
     * 可供内页标题使用的变化形式
     * 注释掉的是兼容WPS不兼容OFFICE的
     */
    public static final List<InAnimationType> TitleInAnimationTypes = Lists.newArrayList(
            InAnimationType.BLINDS,
            InAnimationType.WIPE,
            //InAnimationType.LADDER,
            InAnimationType.WHEEL,
            InAnimationType.SPLITTING,
            InAnimationType.BOARD,
            //InAnimationType.CUT,
            InAnimationType.LINE,
            //InAnimationType.DISSOLVE,
            //InAnimationType.GRADIENT,
            //InAnimationType.CYCLOTRON_GRADIENT,
            //InAnimationType.ZOOM_GRADIENT,
            InAnimationType.ZOOM
    );

    /**
     * 可供元素的变化形式
     * 注释掉的是兼容WPS不兼容OFFICE的
     */
    public static final List<InAnimationType> ElementInAnimationTypes = Lists.newArrayList(
            InAnimationType.BLINDS,
            InAnimationType.WIPE,
            //InAnimationType.LADDER,
            //InAnimationType.FLY_INTO,
            InAnimationType.BOX,
            InAnimationType.WHEEL,
            InAnimationType.SPLITTING,
            InAnimationType.BOARD,
            //InAnimationType.CUT,
            InAnimationType.SECTOR,
            InAnimationType.EXPANSION,
            InAnimationType.LINE,
            //InAnimationType.DISSOLVE,
            //InAnimationType.GRADIENT,
            //InAnimationType.CYCLOTRON_GRADIENT,
            //InAnimationType.ZOOM_GRADIENT,
            InAnimationType.ZOOM
            //InAnimationType.UNFOLD
            //InAnimationType.CYCLOTRON
    );

    /**
     * 乱数假文
     */
    public static final String LOREM_IPSUM_EN = "Lorem ipsum dolor sit amet consectetur adipiscing elit, sed do eiusmod " +
            "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco" +
            " laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate" +
            " velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, " +
            "sunt in culpa qui officia deserunt mollit anim id est laborum.";

    public static final String LOREM_IPSUM_CN = "标题文本内容输入框百天听表活听，法识及制又确划目叫，全有弦记声串些。把求回总石十太农，" +
            "气发就肃多马。基式者例该容属立老标候，型科代光联几米到往较五，就眼隶志严足感反想。还划主断技民式军斗安习济，张制力组要平置装指，" +
            "她称事陕它革器难。音具一利着没外太，八张角已被和，实角李列辅易。前确社打至级极备状王带五流着非光，形步火以劳家抄这十和。 " +
            "即快果很十性治，构少选满较六体，装陕苏声矿。素制切青声很容此月，门所气要完必维水，全积李强度感积。她值了广看美先元万复就华亲" +
            "车商，过型眼意究里图维己弦话。金对合据却写素复那治族品，前了务华改刷圆承地但。 各厂代资车改切这其，受技划划江示系完，张细极" +
            "求县识足。 治斯真日拉力义支经与，事例看录快那于。而才影流别会量，风速证层派但，空屈受热真。况又组与观社花关主，前须传代拉当军，" +
            "方更领维佣十。度速表速林半用养度去回，无量口表团状活加数广，色己口青管询毛马。 例进山林元，价全主心月，验隶适证。动相效四值" +
            "系快又品保统，极手型名此眼白主叫包三，为说村问林县细白十。 先非政其总确置派主条写养劳加，号方金更照条来民个者龙快，节织改" +
            "全而二些位共居。 片般际得红规件金际治火铁场由，机石此约部王它矿响必无。 农斯位增类民指机说叫，法选但层安务方存龙，前求极" +
            "么实连否。场查属上任定度布往育，常其约究到整指电，战花杨便求性白就。步相现收制况省况，斗队高重例称又矿角，准向听证压园。会" +
            "期酸县调格被价标切场日报形手志立因五，个结指志本年切支吹指呈二再领火。证热争空色使安，复基对照山，起居四交千。";

    public static final String TITLE_REMARK = "title";

    public static final Integer SPECIAL_DEFAULT = 0;

    public static final List<String> HOME_RAND_TEXTS = Lists.newArrayList(
            "版权", "正版", "演示", "模板", "幻灯",
            "易修改", "有版权", "幻灯片", "好模板", "办公稿", "可编辑",
            "高端模板", "幻灯模板", "演示模板", "办公模板", "大气模板", "展示模板", "商务模板", "商务办公", "工作商务", "正版授权", "版权模板", "正版模板",
            "幻灯片模板", "简约风模板", "大气感模板", "风格化模板", "办公类模板", "全用途模板", "简洁风模板", "工作风模板", "版权好模板", "正版好模板",
            "大气简约模板", "大气简洁模板", "简洁风格模板", "简约模板风格", "办公风格模板", "办公用途模板", "汇报用户模板", "演讲风格模板",
            "幻灯片模板展", "商务风格模板", "商务简约模板", "商务简洁模板", "商务大气模板", "大气商务模板", "新版演示模板", "正版授权模板",
            "幻灯片模板展示", "有版权幻灯模板", "商务大气幻灯片", "幻灯片演示模板", "幻灯片办公模板", "大气幻灯片模板", "正版幻灯片模板",
            "简约幻灯片模板", "简约幻灯片展示", "办公大气好模板", "易修改演讲模板", "商务风办公模板", "商务幻灯片演讲", "商务幻灯片汇报",
            "汇报商务风演示", "汇报幻灯片文档", "商务风汇报大气", "办公幻灯片资源", "大气风幻灯模板", "商务风格幻灯片", "简约汇报幻灯片",
            "数据分析培训汇报", "汇报工作商务规划", "数据复盘总结汇报", "汇报总结产品优化", "品牌汇报数据分析", "分析汇报简约商务",
            "商务简约培训工作", "商务复盘汇报总结", "汇报工作商务大气", "汇报工作商务风格", "品牌策划产品研发", "汇报风格商务简约",
            "数据分析培训幻灯片", "商务复盘汇报幻灯片", "汇报工作商务数据集","商务总结产品规划图","品牌策划产品确认会"
            );

    public static final List<String> CN_BIG_NUMBER = Lists.newArrayList("壹", "贰", "叁", "肆", "伍",
            "陆", "柒", "捌", "玖", "拾");

    public static final List<String> CN_SMALL_NUMBER = Lists.newArrayList("一", "二", "三", "四", "五",
            "六", "七", "八", "九", "十");

    public static final List<String> EN_NUMBER = Lists.newArrayList("one", "two", "three", "four", "five");

    public static final List<String> TITLE_RAND_TEXTS = Lists.newArrayList(
            "关键词", "小标题",
            "关键词语", "标题内容", "关键信息", "重点词语", "核心概要", "重点信息",
            "请输入标题", "请录入标题", "请输入概要", "请输入内容", "请输入文本",
            "在这里输标题", "点击录入标题", "点击输入标题", "单击输入标题", "单击编辑文本", "单击编写文本",
            "在这里输入标题", "单击可输入标题", "单击可修改文本", "单击这里改文本", "请单击编辑标题",
            "点击这里输入标题", "请在这里输入标题", "请在这里修改标题", "单击这里修改标题", "单击这里修改文本", "点击这里修改标题",
            "在这里输入您的标题", "请在这输入标题内容", "在这里输入您的标题", "在这里输入您的文本",
            "请在这里输入您的标题", "单击这里输入您的标题",
            "在这里单击输入您的标题",
            "请在这里单击输入您的标题",
            "请在这里单击并输入您的标题",
            "请在这里单击输入您的需要标题",
            "在这里单击输入您准备需要的标题",
            "请在这里单击输入您准备需要的标题",
            "单击这里输入标题，单击这里输入标题",
            "请单击这里输入标题，单击这里输入标题",
            "请在这里单击输入标题，单击这里输入标题",
            "请在这里单击输入标题，请单击这里输入标题",
            "请在这里单击输入标题，请单击这里后输入标题",
            "请在这里单击并输入标题，请单击这里后输入标题",
            "请单击这里后输入标题，请单击这里后输入您的标题",
            "请单击这里后输入您的标题，单击这里后输入您的标题",
            "请单击这里后输入您的标题，请单击这里后输入您的标题",
            "请在这里单击后输入您的标题，请单击这里后输入您的标题",
            "请单击这里输入标题，单击这里输入标题，单击这里输入标题",
            "请在这里单击输入标题，单击这里输入标题，单击这里输入标题",
            "请在这里单击输入标题，在这里单击输入标题，单击这里输入标题",
            "请在这里单击输入标题，在这里单击输入标题，请这里单击输入标题"
    );

    public static final String CONTENT = "CONTENT";

    public static final String JPG = "jpg";
}
