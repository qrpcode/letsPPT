package cc.pptshow.build.pptbuilder.style.home;

import cc.pptshow.build.pptbuilder.style.ProbabilityStyle;
import cc.pptshow.build.pptbuilder.style.home.title.HomeSideTitleElementStyle;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
public class HomeStyle {

    private HomeShowStyle homeShowStyle;
    private HomePicStyle homePicStyle;
    private HomePicSideShape homePicSideShape;
    private HomeSideTitleElementStyle homeSideTitleElementStyle;

    public static HomeStyle buildByRand() {
        HomeStyle style = new HomeStyle();
        HomeShowStyle homeShowStyle = RandUtil.randInEnum(HomeShowStyle.class);
        style.setHomeShowStyle(homeShowStyle);

        if (homeShowStyle.equals(HomeShowStyle.NORMAL)) {
            HomePicStyle homePicStyle = RandUtil.randInEnum(HomePicStyle.class);
            style.setHomePicStyle(homePicStyle);

            if (homePicStyle.equals(HomePicStyle.LEFT_PIC) || homePicStyle.equals(HomePicStyle.RIGHT_PIC)) {
                HomePicSideShape homePicSideShape = RandUtil.randInEnum(HomePicSideShape.class);
                style.setHomePicSideShape(homePicSideShape);

                HomeSideTitleElementStyle homeSideTitleElementStyle = HomeSideTitleElementStyle.buildByRand();
                style.setHomeSideTitleElementStyle(homeSideTitleElementStyle);
            }

        } else if (homeShowStyle.equals(HomeShowStyle.MIDDLE)) {
            //中心矩形框样式
            throw new RuntimeException("还没实现");
        }

        return style;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum HomeShowStyle implements ProbabilityStyle {
        MIDDLE("中心矩形框展示", 0, 0),
        NORMAL("常规平面样式", 1, 99999);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum HomePicStyle implements ProbabilityStyle {
        LEFT_PIC("首页左侧图片", 2, 10),
        TOP_PIC("首页上侧图片", 1, 0),
        RIGHT_PIC("首页右侧图片", 3, 0),
        LEFT_TOP_PIC("首页左上侧图片", 4, 0),
        LEFT_BOTTOM_PIC("首页左下侧图片", 5, 0),
        RIGHT_TOP_PIC("首页右上侧图片", 6, 0),
        RIGHT_BOTTOM_PIC("首页右下侧图片", 7, 0),
        LEFT_NO_PIC("首页无图片标题在左侧", 8, 0),
        RIGHT_NO_PIC("首页无图片标题在右侧", 9, 0),
        MIDDLE_NO_PIC("首页无图片标题在中间", 10, 0),
        ALL_FILL_PIC("首页图片平铺底层", 11, 0);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum HomePicSideShape implements ProbabilityStyle {
        ROUND_RECT("圆角矩形", 0, 0),
        PARALLELOGRAM("平行四边形", 1, 0),
        ELLIPSE("圆形", 2, 20),
        SIDE_PARALLELOGRAM("平行四边形完整覆盖", 3, 0),
        SIDE_RECT("矩形完整覆盖", 3, 0),
        SIDE_ELLIPSE("圆形完整覆盖", 3, 0),
        BORDER_LESS("无边界展示", 4, 0);
        private final String name;
        private final int code;
        private final int probability;
    }

}
