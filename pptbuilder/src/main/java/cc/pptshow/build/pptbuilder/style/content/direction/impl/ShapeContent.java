package cc.pptshow.build.pptbuilder.style.content.direction.impl;

import cc.pptshow.build.pptbuilder.domain.qo.DirectionQo;
import cc.pptshow.build.pptbuilder.style.ProbabilityStyle;
import cc.pptshow.build.pptbuilder.style.content.direction.HorizontalContent;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.ppt.element.PPTElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Data
public class ShapeContent implements HorizontalContent {

    private Style style;
    private PositionStyle positionStyle;
    private ShapeStyle shapeStyle;
    private DecorationType decorationType;

    public static ShapeContent buildByRand() {
        ShapeContent shapeContent = new ShapeContent();
        shapeContent.setStyle(RandUtil.randInEnum(Style.class));
        shapeContent.setPositionStyle(RandUtil.randInEnum(PositionStyle.class));
        shapeContent.setShapeStyle(RandUtil.randInEnum(ShapeStyle.class));
        shapeContent.setDecorationType(RandUtil.randInEnum(DecorationType.class));
        return shapeContent;
    }

    @Override
    public List<PPTElement> toElements(DirectionQo directionQo) {
        return null;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum Style implements ProbabilityStyle {
        ALL_IN_SHAPE("所有元素都在图形中", 0, 50),
        ORDER_IN_SHAPE("只有序号在图形中", 1, 50);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum OrderInShapeStyle implements ProbabilityStyle {
        HOLLOW("环形的", 0, 50),
        SOLID("完全填充的", 1, 50);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum PositionStyle implements ProbabilityStyle {
        NEXT_TO("紧挨着的", 0, 50),
        DISPERSE("分散的", 1, 50);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum ShapeStyle implements ProbabilityStyle {
        ROUND_RECT("圆角矩形", 0, 50),
        DIAMOND("菱形", 1, 50),
        CIRCULAR("圆形", 2, 50);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum DecorationType implements ProbabilityStyle {
        ENGLISH("英文内容", 0, 50),
        KEYWORD("关键词", 1, 30);
        private final String name;
        private final int code;
        private final int probability;
    }

}
