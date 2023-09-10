package cc.pptshow.build.pptbuilder.style.home;

import cc.pptshow.build.pptbuilder.style.ProbabilityStyle;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.ppt.element.PPTElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
public class HomeAuthStyle {

    private AuthShowStyle authShowStyle;
    private AuthorShapeStyle authorShapeStyle;
    private final String reporter = "汇报人：张卷王";

    public static HomeAuthStyle buildByRand() {
        HomeAuthStyle homeAuthStyle = new HomeAuthStyle();
        homeAuthStyle.setAuthShowStyle(RandUtil.randInEnum(AuthShowStyle.class));
        homeAuthStyle.setAuthorShapeStyle(RandUtil.randInEnum(AuthorShapeStyle.class));
        return homeAuthStyle;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum AuthShowStyle implements ProbabilityStyle {
        COVER("不透明全覆盖", 0, 10),
        LINEAR("线性包裹", 1, 10),
        TRANSLUCENT("半透明", 2, 5);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum AuthorShapeStyle implements ProbabilityStyle {
        ROUND_RECT("圆角矩形", 0, 5),
        RECT("矩形", 1, 10),
        ELLIPSE_RECT("椭圆矩形", 2, 100);
        private final String name;
        private final int code;
        private final int probability;
    }

}
