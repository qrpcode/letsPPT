package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.build.pptbuilder.bean.ColorInfo;
import cc.pptshow.build.pptbuilder.bean.EnumPPTStyle;
import cc.pptshow.build.pptbuilder.bean.FontInfo;
import cc.pptshow.build.pptbuilder.bean.ImgInfo;
import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;
import cc.pptshow.build.pptbuilder.domain.enums.LanguageType;
import cc.pptshow.build.pptbuilder.domain.vo.FromVo;
import cc.pptshow.build.pptbuilder.domain.vo.NlpVo;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.ppt.element.SerializablePPTElement;
import cc.pptshow.ppt.show.PPTShowSide;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class SerializableGlobalStyle {

    private String title;

    private String enTitle;

    private String lorem;

    private List<String> loremTitles = Lists.newArrayList();

    //**********************【渠道相关】**********************

    private ChannelType channelType = ChannelType.MBG;

    //**********************【风格相关】**********************

    /**
     * 颜色风格
     */
    private ColorInfo colorInfo;

    /**
     * PPT风格
     */
    private EnumPPTStyle pptStyle;

    /**
     * 标题艺术文本字体
     */
    private FontInfo titleFontInfo;

    /**
     * 内页文本字体
     */
    private FontInfo textFontInfo;

    /**
     * 文字文本字体大小
     */
    private int normalFontSize = RandUtil.round(13, 15);

    /**
     * 幻灯片文字风格
     * 默认是中文幻灯片
     */
    private String languageType = LanguageType.CHINESE.getCode();

    /**
     * 需要哪些页面列表
     * 一般情况是 24 页
     * 1首页 1目录 4大标题 1感谢 1版权说明 16内页
     */
    private List<Integer> pptPageIds;

    /**
     * NLP语义解析结果
     */
    private NlpVo nlpVo;

    /**
     * 标题中的名词（供图片选择使用）
     */
    private List<String> imgElements;

    /**
     * 可供使用的图片信息
     */
    private List<ImgInfo> imgInfos;

    /**
     * 模板版权信息库
     */
    private List<FromVo> copyrightFromList = Lists.newArrayList();


    //********************【随机生成，无需指定】******************
    /**
     * 全局光角度
     */
    private double globalLight = RandUtil.round(0, 140) * 1.0;

    /**
     * 整体倾斜风格，倾斜指的是背景的切线倾斜角度
     */
    private double globalTilt = RandUtil.round(150, 210) * 1.0;


    //********************【运行时使用】******************
    /**
     * 如果页面空旷处理
     */
    private List<SerializablePPTElement> openHomePageUnderElements = Lists.newArrayList();
    private List<SerializablePPTElement> openHomePageUpElements = Lists.newArrayList();
    /**
     * 页面内的标题元素
     */
    private List<SerializablePPTElement> titleElements = Lists.newArrayList();
    /**
     * 页面内标题的图形范围
     */
    private CanvasShape titleShape;
    /**
     * 大标题页
     */
    private PPTShowSide bigTitlePage;
    /**
     * 颜色使用记录
     */
    private final Map<String, String> colorMap = Maps.newHashMap();


    public static SerializableGlobalStyle buildByGlobalStyle(GlobalStyle globalStyle) {
        SerializableGlobalStyle style = new SerializableGlobalStyle();
        style.setTitle(globalStyle.getTitle());
        style.setEnTitle(globalStyle.getEnTitle());
        style.setLorem(globalStyle.getLorem());
        style.setChannelType(globalStyle.getChannelType());
        style.setColorInfo(globalStyle.getColorInfo());
        style.setPptStyle(globalStyle.getPptStyle());
        style.setTitleFontInfo(globalStyle.getTitleFontInfo());
        style.setTextFontInfo(globalStyle.getTextFontInfo());
        style.setNormalFontSize(globalStyle.getNormalFontSize());
        style.setLanguageType(globalStyle.getLanguageType().getCode());
        style.setPptPageIds(globalStyle.getPptPageIds());
        style.setGlobalLight(globalStyle.getGlobalLight());
        style.setGlobalTilt(globalStyle.getGlobalTilt());
        style.setOpenHomePageUnderElements(SerializablePPTElement
                .buildSerializablePPTElements(globalStyle.getOpenHomePageUnderElements()));
        style.setOpenHomePageUpElements(SerializablePPTElement
                .buildSerializablePPTElements(globalStyle.getOpenHomePageUpElements()));
        style.setTitleElements(SerializablePPTElement.buildSerializablePPTElements(globalStyle.getTitleElements()));
        style.setTitleShape(globalStyle.getTitleShape());
        style.setBigTitlePage(globalStyle.getBigTitlePage());
        style.getColorMap().putAll(globalStyle.getColorMap());
        style.setNlpVo(globalStyle.getNlpVo());
        style.setImgElements(globalStyle.getImgElements());
        style.setImgInfos(globalStyle.getImgInfos());
        style.setLoremTitles(globalStyle.getLoremTitles());
        style.setCopyrightFromList(globalStyle.getCopyrightFromList());
        return style;
    }

    public static GlobalStyle buildGlobalStyle(SerializableGlobalStyle style) {
        GlobalStyle globalStyle = new GlobalStyle();
        globalStyle.setTitle(style.getTitle());
        globalStyle.setEnTitle(style.getEnTitle());
        globalStyle.setLorem(style.getLorem());
        globalStyle.setChannelType(style.getChannelType());
        globalStyle.setColorInfo(style.getColorInfo());
        globalStyle.setPptStyle(style.getPptStyle());
        globalStyle.setTitleFontInfo(style.getTitleFontInfo());
        globalStyle.setTextFontInfo(style.getTextFontInfo());
        globalStyle.setLanguageType(Arrays.stream(LanguageType.values())
                .filter(s -> s.getCode().equals(style.getLanguageType())).findFirst().orElse(LanguageType.CHINESE));
        globalStyle.setNormalFontSize(style.getNormalFontSize());
        globalStyle.setPptPageIds(style.getPptPageIds());
        globalStyle.setGlobalLight(style.getGlobalLight());
        globalStyle.setGlobalTilt(style.getGlobalTilt());
        globalStyle.setOpenHomePageUnderElements(SerializablePPTElement
                .buildPPTElements(style.getOpenHomePageUnderElements()));
        globalStyle.setOpenHomePageUpElements(SerializablePPTElement
                .buildPPTElements(style.getOpenHomePageUpElements()));
        globalStyle.setTitleElements(SerializablePPTElement.buildPPTElements(style.getTitleElements()));
        globalStyle.setTitleShape(style.getTitleShape());
        globalStyle.setBigTitlePage(style.getBigTitlePage());
        globalStyle.getColorMap().putAll(style.getColorMap());
        globalStyle.setNlpVo(style.getNlpVo());
        globalStyle.setImgElements(style.getImgElements());
        globalStyle.setImgInfos(style.getImgInfos());
        globalStyle.setLoremTitles(style.getLoremTitles());
        globalStyle.setCopyrightFromList(style.getCopyrightFromList());
        return globalStyle;
    }
}
