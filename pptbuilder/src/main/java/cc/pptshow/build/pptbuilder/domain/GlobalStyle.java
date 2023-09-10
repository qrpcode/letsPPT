package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.build.pptbuilder.bean.*;
import cc.pptshow.build.pptbuilder.domain.enums.ChannelType;
import cc.pptshow.build.pptbuilder.domain.enums.LanguageType;
import cc.pptshow.build.pptbuilder.domain.vo.FromVo;
import cc.pptshow.build.pptbuilder.domain.vo.NlpVo;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.show.PPTShowSide;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class GlobalStyle {

    private String uuid;

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
    private LanguageType languageType = LanguageType.CHINESE;

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
    private List<PPTElement> openHomePageUnderElements = Lists.newArrayList();
    private List<PPTElement> openHomePageUpElements = Lists.newArrayList();
    /**
     * 页面内的标题元素
     */
    private List<PPTElement> titleElements = Lists.newArrayList();
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


    public String findColor(String oldColor) {
        if (colorMap.containsKey(oldColor)) {
            return colorMap.get(oldColor);
        }
        Set<String> nowColors = colorMap.keySet();
        if (!nowColors.contains(colorInfo.getFromColor())) {
            colorMap.put(oldColor, colorInfo.getFromColor());
            return colorInfo.getFromColor();
        }
        if (Strings.isNotBlank(colorInfo.getToColor()) && nowColors.contains(colorInfo.getToColor())) {
            colorMap.put(oldColor, colorInfo.getToColor());
            return colorInfo.getToColor();
        }
        return null;
    }

    private Map<Integer, PPTRegionPut> regionPuts = Maps.newHashMap();

}
