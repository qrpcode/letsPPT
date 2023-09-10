package cc.pptshow.build.pptbuilder.biz.analysis.impl;

import cc.pptshow.build.pptbuilder.bean.DataTitle;
import cc.pptshow.build.pptbuilder.bean.KeywordStyle;
import cc.pptshow.build.pptbuilder.bean.NlpWordReplace;
import cc.pptshow.build.pptbuilder.biz.analysis.TitleAnalysis;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.StyleAnalysis;
import cc.pptshow.build.pptbuilder.domain.vo.NlpItemVo;
import cc.pptshow.build.pptbuilder.domain.vo.NlpVo;
import cc.pptshow.build.pptbuilder.service.DataTitleService;
import cc.pptshow.build.pptbuilder.service.EnumPPTStyleService;
import cc.pptshow.build.pptbuilder.service.KeywordStyleService;
import cc.pptshow.build.pptbuilder.service.NlpWordReplaceService;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class TitleAnalysisImpl implements TitleAnalysis {

    @Resource
    private KeywordStyleService keywordStyleService;

    @Resource
    private EnumPPTStyleService enumPPTStyleService;

    @Resource
    private DataTitleService dataTitleService;

    @Resource
    private NlpWordReplaceService nlpWordReplaceService;


    public static final List<String> IGNORE_WORD =
            Lists.newArrayList("ppt", "模板", "免费", "下载", "_", "-", "—", ":", "：", ",", "，",
                    "powerpoint", "通用");

    private static final List<String> YEAR_WORD =
            Lists.newArrayList("2003", "2007", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016",
                    "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "20XX", "2099", "2999", "201x", "202x");

    private static final List<String> BLACK_WORD = Lists.newArrayList("合集", "20套");

    private static final List<String> IGNORE_NOUN_WORD = Lists.newArrayList("活动", "方案", "策划", "报告",
            "工作", "主题", "背景", "风", "矢量", "黑金", "项目", "商务", "计划", "时尚", "风格", "孟菲斯", "欧美", "简洁",
            "古典风", "行业", "商业", "计划书", "互联网", "新媒体", "卡通", "国", "潮风", "中国", "极简", "高端", "质感", "我",
            "艺术", "创意", "部", "国学", "经典", "你好", "插画风", "文艺", "插画", "古典", "知识", "底图", "多边形", "圆点",
            "金牌", "曲线", "纹理", "韵味", "图案", "多边形", "圆点", "金牛", "曲线", "底图", "粒子", "提案", "公司",
            "潮流", "手绘", "大气", "个性", "矩形", "产品", "人物", "网页", "简介", "方格", "折线", "波纹", "团队", "企业",
            "员工", "执行力", "插图", "样式", "简介", "圆环", "方形", "方框", "市场", "品牌", "图片", "课件", "立方体", "大气",
            "你", "蓝橙", "蓝橙", "动画", "效果", "圆形", "动态", "风景", "几何", "学术", "一套", "一粒", "一组", "上册", "两个",
            "个人" , "个性", "中小学", "主题活动", "习俗", "习惯", "事业", "事情", "二套", "五个", "五型", "五套", "产品", "人事",
            "金莲", "人员", "人士", "人才", "人物", "人生", "仪式", "企业", "企业文化", "企业活动", "企划案",
            "伙伴", "你", "信息", "假期", "全民", "公司", "公开课", "六边形", "典礼", "军令状", "几何", "几何体", "铁军",
            "金粉", "制度", "前卫", "前携手同行", "力量", "动态", "动感", "动画", "励志类", "十五条", "十里",
            "单位", "卡片", "品牌", "员工", "向未来", "商业企业", "商业活动", "虎", "行政", "商务写字楼",
            "商务部门", "团", "团员", "团支部", "团日", "团队", "部门", "邀请函", "园", "进度", "逆行者",
            "国际", "图形", "图文", "图时尚", "图标", "图案", "图片", "图表", "圆圈", "圆形", "圆点", "圆环", "圆角", "圈",
            "地图", "场景", "坚定不移", "复古风", "外企", "多边形", "外观", "大会", "大全", "大事", "大气", "奔向终点", "季度"
            , "客户", "阴影", "宣传册", "宣讲会", "家园", "宽屏", "封面", "小人", "小清新", "小清", "工业", "工作会议",
            "销售部", "长假", "工作者", "工程", "工程师", "工程部", "市场", "市场部", "布局", "古诗词", "幻灯片", "PPT",
            "广告", "庆典", "底", "底图", "弧线", "当代", "形势", "形状", "彩球", "影视", "征程", "微粒体", "微软", "心", "心理",
            "必杀技", "必由之路", "思想", "青春", "思路", "非主流", "情感", "情调", "意义", "意识", "意识形态", "成员",
            "成就", "成本", "成绩", "我们", "战场", "战役", "战报", "战略", "战绩", "扁平风", "手册", "手势", "执行力", "技巧",
            "技术", "技术员", "投资计划", "韵味", "折线", "护眼", "拉线", "接班人", "插图", "播放器", "按钮", "指针",
            "故事", "效果", "效率", "数据", "数字", "文件夹", "文件袋", "文字", "文明", "斜线", "新一代", "新品", "传媒",
            "新学期", "新征程", "新时代", "", "新起点", "方向", "方块", "方形", "方格", "方框", "发布会", "半圆形", "时尚墨",
            "时间", "时间轴", "明城市", "智慧", "智能", "曲线", "有利条件", "机关", "拉链", "手绘", "极简风公司", "柔美",
            "柱状图", "标签", "校", "样式", "格子", "框架", "模板", "模版", "橙蓝", "欧洲", "学术", "正方形", "正能量",
            "比赛", "毕业季", "高雅", "毛玻璃", "气氛", "水云间", "水样", "汇演", "法则", "活力", "液体", "液态",
            "福", "麦克风", "高考", "温馨", "游戏", "演讲稿", "拓路", "教育", "照片",
            "照片墙", "版", "物业", "狼性企业", "班会", "班干部", "球体", "生涯", "盛会", "盛典", "目标", "矩形", "磨砂",
            "设计感", "礼仪", "科室", "立体方块", "立方体", "第一季度", "第二季", "简介", "简历", "科技感", "简洁唯美",
            "贫穷者", "马赛克", "黑红", "黑白", "简洁橙", "简洁科技", "简洁简洁",
            "黄紫", "轴", "黑橘", "简约蓝", "简约蓝紫", "管理者", "箭头", "粒子", "系", "素材",
            "紫红", "红圈", "红橙", "红绿", "红金", "科技", "视觉", "纹理", "线描", "线条", "线框", "细线", "绩效",
            "绿黑双", "网", "网格", "美好时光", "老外", "职业", "职场", "职工", "职称", "能力", "色系", "艺术感", "艺术玻璃", "范"
            , "荣耀", "莫兰迪", "菱形", "蓝橙", "蓝灰", "蓝灰扁平化公司", "蓝紫", "蓝红", "蓝绿");

    private static final List<String> IGNORE_TIME_WORD = Lists.newArrayList("月", "年");

    private static final String RAND_SUFFIX = "PPT模板";

    private static final List<String> PLACE_HOLDER = Lists.newArrayList("大气", "简约", "商务", "简洁", "通用");

    /**
     * 普通名词
     */
    private static final String COMMON_NOUN = "n";
    private static final String COMMON_NS = "ns";
    private static final String BACKGROUND_CN = "背景";
    private static final String COLOR = "色";

    private static final int STYLE_NUM = 3;

    @Override
    public StyleAnalysis analysisTitle(Long titleId) {
        DataTitle dataTitle = dataTitleService.findById(titleId);
        String title = dataTitle.getTitle().toLowerCase();
        NlpVo nlpVo = findNlpVo(dataTitle, title);
        return analysisTitle(nlpVo, title);
    }

    private StyleAnalysis analysisTitle(NlpVo nlpVo, String title) {
        if (matchBlackWord(title)) {
            return null;
        }
        title = filterIgnoreWord(title);
        if (title.length() < 4) {
            return null;
        }
        StyleAnalysis styleAnalysis = new StyleAnalysis();
        styleAnalysis.setStyle(findStyleInTitle(title));
        styleAnalysis.setElements(findNeedElements(nlpVo));
        //List<RGB> colorByTitle = nlpColorHelper.findColorByTitle(title);
        //styleAnalysis.setWebColor(colorByTitle);
        String backgroundColor = findBackgroundColor(nlpVo);
        styleAnalysis.setBackgroundColor(backgroundColor);
        styleAnalysis.setColor(findColor(nlpVo, backgroundColor));
        styleAnalysis.setNewTitles(buildNewTitle(title));
        styleAnalysis.setNlpVo(nlpVo);
        return styleAnalysis;
    }

    @Override
    public StyleAnalysis analysisTitle(String title) {
        NlpVo nlpVo = findNlpVo(null, title);
        return analysisTitle(nlpVo, title);
    }

    private List<String> buildNewTitle(String title) {
        //原始标题
        if (title.length() < 8) {
            title = RandUtil.randElement(PLACE_HOLDER) + title;
        }
        List<String> list = buildNearlyTitle(title);
        if (list.size() > 1) {
            list.remove(title);
        }
        return list.stream().map(l -> l + RAND_SUFFIX).collect(Collectors.toList());
    }

    private List<String> buildNearlyTitle(String title) {
        List<String> titles = Lists.newArrayList();
        titles.add(title);
        List<NlpWordReplace> wordReplaces = nlpWordReplaceService.selectAll();
        for (NlpWordReplace wordReplace : wordReplaces) {
            if (title.contains(wordReplace.getOldString())) {
                List<String> copyTitles = Lists.newArrayList(titles);
                for (String copyTitle : copyTitles) {
                    if (copyTitle.contains(wordReplace.getOldString())) {
                        titles.add(copyTitle.replace(wordReplace.getOldString(), wordReplace.getNewString()));
                    }
                }
            } else if (wordReplace.getCanReverse() == 1 && title.contains(wordReplace.getNewString())) {
                List<String> copyTitles = Lists.newArrayList(titles);
                for (String copyTitle : copyTitles) {
                    if (copyTitle.contains(wordReplace.getNewString())) {
                        titles.add(copyTitle.replace(wordReplace.getNewString(), wordReplace.getOldString()));
                    }
                }
            }
        }
        return Lists.newArrayList(Sets.newHashSet(titles));
    }

    private NlpVo findNlpVo(DataTitle dataTitle, String title) {
        NlpVo nlpVo;
        if (Objects.nonNull(dataTitle) && Strings.isNotBlank(dataTitle.getNlp())) {
            nlpVo = JSON.parseObject(dataTitle.getNlp(), NlpVo.class);
        } else {
            nlpVo = nlpText(title);
            if (Objects.nonNull(dataTitle)) {
                dataTitle.setNlp(JSON.toJSONString(nlpVo));
                dataTitleService.update(dataTitle);
            }
        }
        Assert.isTrue(Objects.nonNull(nlpVo), "NLP语义识别失败");
        return nlpVo;
    }

    private List<String> findStyleInTitle(String title) {
        List<KeywordStyle> keywordStyles = keywordStyleService.findAll().stream()
                .filter(keyword -> title.contains(keyword.getWord()))
                .collect(Collectors.toList());
        int[] styles = new int[7];
        for (KeywordStyle keywordStyle : keywordStyles) {
            styles[keywordStyle.getPptStyleId()] += keywordStyle.getScore();
        }
        int[] styleRandMap = toRandArray(styles);
        List<String> pptStyles = Lists.newArrayList();
        for (int i = 0; i < STYLE_NUM; i++) {
            pptStyles.add(enumPPTStyleService.findById(styleRandMap[RandUtil.round(0, styleRandMap.length - 1)]).getName());
        }
        return pptStyles;
    }

    private int[] toRandArray(int[] styles) {
        int sum = Arrays.stream(styles).sum();
        if (sum == 0) {
            return new int[]{1, 1, 2};
        }
        int[] randArr = new int[sum];
        int begin = 0;
        for (int i = 1; i < styles.length; i++) {
            for (int j = begin; j < begin + styles[i]; j++) {
                randArr[j] = i;
            }
            begin += styles[i];
        }
        return randArr;
    }

    private List<String> findNeedElements(NlpVo nlpVo) {
        List<String> nounWords = findAllNounWordInNlp(nlpVo);
        nounWords.removeAll(IGNORE_NOUN_WORD);
        nounWords = nounWords.stream()
                .filter(n -> !n.endsWith(COLOR))
                .filter(n -> IGNORE_TIME_WORD.stream().noneMatch(n::contains))
                .collect(Collectors.toList());
        return nounWords;
    }

    private String findBackgroundColor(NlpVo nlp) {
        if (nlp.getText().contains("黑金")) {
            return "黑色";
        } else if (nlp.getText().contains("红黑")) {
            return "黑色";
        } else if (nlp.getText().contains("蓝橙")) {
            return "蓝色";
        }
        List<String> maybeBackgroundWords = findAllMaybeBackgroundWords(nlp);
        return findNearlyColorWordInWords(maybeBackgroundWords);
    }

    private String findNearlyColorWordInWords(List<String> maybeBackgroundWords) {
        if (maybeBackgroundWords.size() < 2) {
            return EMPTY;
        }
        String maybeColor = maybeBackgroundWords.get(maybeBackgroundWords.size() - 2);
        return maybeColor.endsWith(COLOR) ? maybeColor : EMPTY;
    }

    private String findColor(NlpVo nlp, String backgroundColor) {
        //特殊处理
        if (nlp.getText().contains("黑金")) {
            return "金色";
        } else if (nlp.getText().contains("红黑")) {
            return "红色";
        } else if (nlp.getText().contains("蓝紫")) {
            return RandUtil.round(1, 2) == 1 ? "蓝色" : "紫色";
        } else if (nlp.getText().contains("蓝橙")) {
            return "橙色";
        }
        List<String> nounWords = findAllNounWordInNlp(nlp);
        nounWords.remove(backgroundColor);
        return findColorWordInWords(nounWords);
    }

    private String findColorWordInWords(List<String> nounWords) {
        return nounWords.stream()
                .filter(n -> n.endsWith(COLOR))
                .findFirst()
                .orElse(EMPTY);
    }

    private List<String> findAllMaybeBackgroundWords(NlpVo nlp) {
        List<NlpItemVo> items = nlp.getItems();
        List<String> bgWords = Lists.newArrayList();
        for (NlpItemVo item : items) {
            if (StringUtils.equals(item.getPos(), COMMON_NOUN)) {
                bgWords.addAll(item.getBasicWords());
            } else {
                bgWords = Lists.newArrayList();
            }
            if (item.equals(items.get(items.size() - 1)) && !item.getBasicWords().contains(BACKGROUND_CN)) {
                return Lists.newArrayList();
            }
            if (item.getBasicWords().contains(BACKGROUND_CN)) {
                return bgWords;
            }
        }
        return Lists.newArrayList();
    }

    @NotNull
    private List<String> findAllNounWordInNlp(NlpVo nlp) {
        return nlp.getItems().stream()
                .filter(n -> Strings.isBlank(n.getPos())
                        || StringUtils.equals(n.getPos(), COMMON_NOUN)
                        || StringUtils.equals(n.getPos(), COMMON_NS))
                .map(NlpItemVo::getItem)
                .collect(Collectors.toList());
    }

    private boolean matchBlackWord(String title) {
        for (String word : BLACK_WORD) {
            if (title.contains(word)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String filterIgnoreWord(String title) {
        List<String> words = Lists.newArrayList(IGNORE_WORD, YEAR_WORD).stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        for (String word : words) {
            title = title.replace(word, EMPTY);
        }
        return title;
    }

    @SneakyThrows
    public NlpVo nlpText(String text) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n\t\"text\": \"" + text + "\"\n}");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer?access_token=" + getBaiduToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String bodyStr = Objects.requireNonNull(response.body()).string();
        return JSON.parseObject(bodyStr, NlpVo.class);
    }

    @SneakyThrows
    public String getBaiduToken() {
        if (Strings.isEmpty(BConstant.BAIDU_NLP_TOKEN)) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials" +
                            "&client_id=*********&client_secret=**********")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String bodyStr = Objects.requireNonNull(response.body()).string();
            BConstant.BAIDU_NLP_TOKEN = JSON.parseObject(bodyStr).get("access_token").toString();
        }
        return BConstant.BAIDU_NLP_TOKEN;
    }

}
