package cc.pptshow.build.pptbuilder.style.home.title;

import cc.pptshow.build.pptbuilder.style.ProbabilityStyle;
import cc.pptshow.build.pptbuilder.style.home.HomeAuthStyle;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * 如果文本时靠边的Style形式
 */
@Data
public class HomeSideTitleElementStyle implements HomeTitleElementStyle {

    private LineStyle lineStyle;
    private TopLineStyle topLineStyle;
    private TopLineBigStyle topLineBigStyle;
    private TopLineSmallStyle topLineSmallStyle;
    private TitleDecorate titleDecorate;
    private LoremStyle loremStyle;

    private HomeAuthStyle homeAuthStyle;

    public static HomeSideTitleElementStyle buildByRand() {
        HomeSideTitleElementStyle style = new HomeSideTitleElementStyle();
        LineStyle lineStyle = RandUtil.randInEnum(LineStyle.class);
        style.setLineStyle(lineStyle);
        style.setTitleDecorate(RandUtil.randInEnum(TitleDecorate.class));
        style.setLoremStyle(RandUtil.randInEnum(LoremStyle.class));

        if (lineStyle.equals(LineStyle.TWO_LINE)) {
            TopLineStyle topLineStyle = RandUtil.randInEnum(TopLineStyle.class);
            style.setTopLineStyle(topLineStyle);

            if (topLineStyle.equals(TopLineStyle.BIG)) {
                TopLineBigStyle topLineBigStyle = RandUtil.randInEnum(TopLineBigStyle.class);
                style.setTopLineBigStyle(topLineBigStyle);
            }

            if (topLineStyle.equals(TopLineStyle.SMALL)) {
                TopLineSmallStyle topLineSmallStyle = RandUtil.randInEnum(TopLineSmallStyle.class);
                style.setTopLineSmallStyle(topLineSmallStyle);
            }
        }

        style.setHomeAuthStyle(HomeAuthStyle.buildByRand());
        return style;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum LineStyle implements ProbabilityStyle {
        ONE_LINE("单行展示", 1, 10),
        TWO_LINE("双行展示", 2, 150);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum TopLineStyle implements ProbabilityStyle {
        SMALL("上行小字展示", 1, 50),
        BIG("上行大字展示", 2, 50),
        LOGO("上行展示LOGO", 3, 50);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum TopLineBigStyle implements ProbabilityStyle {
        YEAR("年份", 1, 50),
        YEAR_KEYWORD("年份和关键字", 2, 50),
        KEYWORD("关键字", 3, 50);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum TopLineSmallStyle implements ProbabilityStyle {
        SLOGAN("标语", 1, 50),
        LOREM("乱数假文", 2, 50),
        EN_KEYWORD("英文关键词", 3, 50),
        CN_KEYWORD("中文关键词", 4, 50);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum TitleDecorate implements ProbabilityStyle {
        NONE("无装饰", 0, 20),
        TOP_COLOR("顶部圆形装饰", 1, 0),
        UNDER_ALL_COLOR("下方色块背景", 2, 0),
        UNDER_COLOR("下方色块", 3, 0);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum LoremStyle implements ProbabilityStyle {
        LOREM("乱数假文", 0, 80),
        KEYWORD("关键词", 1, 20);
        private final String name;
        private final int code;
        private final int probability;
    }
}
