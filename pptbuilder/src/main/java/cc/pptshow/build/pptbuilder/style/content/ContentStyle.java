package cc.pptshow.build.pptbuilder.style.content;

import cc.pptshow.build.pptbuilder.style.ProbabilityStyle;
import cc.pptshow.build.pptbuilder.style.content.direction.Direction;
import cc.pptshow.build.pptbuilder.style.content.direction.impl.NormalContent;
import cc.pptshow.build.pptbuilder.style.content.direction.impl.TopNumContent;
import cc.pptshow.build.pptbuilder.style.home.HomeStyle;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
public class ContentStyle {

    private ContentDirection contentDirection;
    private Style style;
    private Direction direction;
    private TitleStyle titleStyle;
    private TypeSetting typeSetting;
    private TextLength textLength;

    public static ContentStyle buildByRand() {
        ContentStyle contentStyle = new ContentStyle();
        ContentDirection contentDirection = RandUtil.randInEnum(ContentDirection.class);
        contentStyle.setContentDirection(contentDirection);
        contentStyle.setStyle(RandUtil.randInEnum(Style.class));
        if (contentDirection.equals(ContentDirection.LEFT)) {
            contentStyle.setDirection(NormalContent.buildByRand());
        } else if (contentDirection.equals(ContentDirection.RIGHT)) {
            contentStyle.setDirection(NormalContent.buildByRand());
        } else if (contentDirection.equals(ContentDirection.TOP)) {
            contentStyle.setDirection(TopNumContent.buildByRand());
        }
        return contentStyle;
    }

    public static ContentStyle buildByHomeStyle(HomeStyle homeStyle) {
        ContentStyle contentStyle = new ContentStyle();
        contentStyle.setStyle(RandUtil.randInEnum(Style.class));
        contentStyle.setTitleStyle(RandUtil.randInEnum(TitleStyle.class));
        contentStyle.setTypeSetting(RandUtil.randInEnum(TypeSetting.class));
        contentStyle.setTextLength(RandUtil.randInEnum(TextLength.class));
        if (homeStyle.getHomePicStyle().equals(HomeStyle.HomePicStyle.LEFT_PIC)
                || homeStyle.getHomePicStyle().equals(HomeStyle.HomePicStyle.LEFT_BOTTOM_PIC)
                || homeStyle.getHomePicStyle().equals(HomeStyle.HomePicStyle.LEFT_TOP_PIC)) {
            contentStyle.setContentDirection(ContentDirection.LEFT);
            contentStyle.setDirection(NormalContent.buildByRand());
        } else if (homeStyle.getHomePicStyle().equals(HomeStyle.HomePicStyle.RIGHT_PIC)
                || homeStyle.getHomePicStyle().equals(HomeStyle.HomePicStyle.RIGHT_TOP_PIC)
                || homeStyle.getHomePicStyle().equals(HomeStyle.HomePicStyle.RIGHT_BOTTOM_PIC)) {
            contentStyle.setContentDirection(ContentDirection.RIGHT);
            contentStyle.setDirection(NormalContent.buildByRand());
        }
        return contentStyle;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum Style implements ProbabilityStyle {
        HOME_SHARE("从主页提取图形", 0, 25),
        ORIGINALITY("通过主页创意延伸", 1, 0),
        HOME_PIC("主页图片图形相关", 2, 0),
        HOME_COLOR("主页相关的颜色组合", 3, 0);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum ContentDirection implements ProbabilityStyle {
        LEFT("左侧", 0, 20),
        RIGHT("右侧", 1, 5),
        TOP("顶侧", 2, 25);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum TitleStyle implements ProbabilityStyle {
        EN("只有英文", 0, 20),
        CN("只有中文", 1, 5),
        EN_BIG_CN("大的英文和小的中文", 2, 25),
        CN_BIG_EN("大的中文和小的英文", 2, 25);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum TypeSetting implements ProbabilityStyle {
        X("横向排版", 0, 100),
        Y("纵向排版", 1, 0),
        CN_Y_EN_X("中文纵向英文横向", 2, 0);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum TextLength implements ProbabilityStyle {
        SIX("六个字", 0, 0),
        FOUR("四个字", 1, 10);
        private final String name;
        private final int code;
        private final int probability;
    }

}
