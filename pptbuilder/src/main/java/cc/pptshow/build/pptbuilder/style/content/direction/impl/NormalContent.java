package cc.pptshow.build.pptbuilder.style.content.direction.impl;

import cc.pptshow.build.pptbuilder.domain.qo.DirectionQo;
import cc.pptshow.build.pptbuilder.exception.PPTBuildException;
import cc.pptshow.build.pptbuilder.style.ProbabilityStyle;
import cc.pptshow.build.pptbuilder.style.content.direction.DispersedContent;
import cc.pptshow.build.pptbuilder.style.content.direction.HorizontalContent;
import cc.pptshow.build.pptbuilder.style.content.direction.StackContent;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.ppt.domain.PPTInnerLineCss;
import cc.pptshow.ppt.domain.PPTInnerTextCss;
import cc.pptshow.ppt.domain.PPTShapeCss;
import cc.pptshow.ppt.domain.background.ColorBackGround;
import cc.pptshow.ppt.domain.shape.Ellipse;
import cc.pptshow.ppt.domain.shape.Rect;
import cc.pptshow.ppt.domain.shape.RoundRect;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTInnerLine;
import cc.pptshow.ppt.element.impl.PPTInnerText;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * 常规标题
 * 左侧数字，右侧上中文，右侧下可能有英文
 */
@Data
public class NormalContent implements HorizontalContent, StackContent, DispersedContent {

    private static final double SHAPE_WIDTH = 1.5;
    private static final double SHAPE_HEIGHT = 1.5;
    private static final double SHAPE_LEFT = 0.5;
    private static final double SHAPE_TOP = 0.5;
    private static final int SHAPE_ROUND = 500;

    private static final String BORDER_COLOR_TEXT = "FFFFFF";

    private Style style;
    private NumStyle numStyle;
    private NumBorderStyle numBorderStyle;
    private NumShapeStyle numShapeStyle;
    private LowerDecoration lowerDecoration;
    private DecorationType decorationType;
    private KeywordStyle keywordStyle;

    public static NormalContent buildByRand() {
        NormalContent normalContent = new NormalContent();
        normalContent.setStyle(RandUtil.randInEnum(Style.class));
        normalContent.setNumStyle(RandUtil.randInEnum(NumStyle.class));
        normalContent.setNumBorderStyle(RandUtil.randInEnum(NumBorderStyle.class));
        normalContent.setLowerDecoration(RandUtil.randInEnum(LowerDecoration.class));
        normalContent.setDecorationType(RandUtil.randInEnum(DecorationType.class));
        normalContent.setKeywordStyle(RandUtil.randInEnum(KeywordStyle.class));
        normalContent.setNumShapeStyle(RandUtil.randInEnum(NumShapeStyle.class));
        return normalContent;
    }

    @Override
    public List<PPTElement> toElements(DirectionQo directionQo) {
        List<PPTElement> elements = Lists.newArrayList();
        if (numBorderStyle.equals(NumBorderStyle.SHAPE_NUM)) {
            PPTShape pptShape = toPPTNumBorder(directionQo);
            elements.add(pptShape);
            elements.add(toNumber(directionQo.getId()));
        } else if (numBorderStyle.equals(NumBorderStyle.ONLY_NUM)) {

        } else if (numBorderStyle.equals(NumBorderStyle.BACKGROUND_NUM)) {

        }
        return Lists.newArrayList();
    }

    private PPTElement toNumber(int id) {
        PPTInnerText innerText = PPTInnerText.build("0" + id);
        innerText.setCss(PPTInnerTextCss.build().setColor(BORDER_COLOR_TEXT));
        PPTInnerLine innerLine = PPTInnerLine.build(innerText);
        innerLine.setCss(PPTInnerLineCss.build().setLineHeight(1.0));
        return PPTText.build(innerLine);
    }

    private PPTShape toPPTNumBorder(DirectionQo directionQo) {
        PPTShape pptShape;
        if (numShapeStyle.equals(NumShapeStyle.ELLIPSE)) {
            pptShape = PPTShape.build(PPTShapeCss.build()
                    .setShape(new Ellipse())
                    .setWidth(SHAPE_WIDTH)
                    .setHeight(SHAPE_HEIGHT)
                    .setLeft(SHAPE_LEFT)
                    .setTop(SHAPE_TOP)
                    .setBackground(new ColorBackGround().setColor(directionQo.getMainColor())));
        } else if (numShapeStyle.equals(NumShapeStyle.RECT)) {
            pptShape = PPTShape.build(PPTShapeCss.build()
                    .setShape(new Rect())
                    .setWidth(SHAPE_WIDTH)
                    .setHeight(SHAPE_HEIGHT)
                    .setLeft(SHAPE_LEFT)
                    .setTop(SHAPE_TOP)
                    .setBackground(new ColorBackGround().setColor(directionQo.getMainColor())));
        } else if (numShapeStyle.equals(NumShapeStyle.ROUND_RECT)) {
            pptShape = PPTShape.build(PPTShapeCss.build()
                    .setShape(new RoundRect().setFillet(SHAPE_ROUND))
                    .setWidth(SHAPE_WIDTH)
                    .setHeight(SHAPE_HEIGHT)
                    .setLeft(SHAPE_LEFT)
                    .setTop(SHAPE_TOP)
                    .setBackground(new ColorBackGround().setColor(directionQo.getMainColor())));
        } else {
            throw PPTBuildException.noSuchCanNotBuild();
        }
        return pptShape;
    }


    @Getter
    @ToString
    @AllArgsConstructor
    public enum Style implements ProbabilityStyle {
        NO_LINE("无框线包裹", 0, 50),
        BETWEEN_LINE("数字和文字中间有竖线", 1, 10),
        ALL_LINE("线条包裹", 2, 5);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum NumBorderStyle implements ProbabilityStyle {
        ONLY_NUM("数字序号无装饰", 0, 20),
        SHAPE_NUM("数字使用图形包裹", 1, 60),
        BACKGROUND_NUM("数字后使用色块装饰", 2, 30);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum NumStyle implements ProbabilityStyle {
        NUMBER("数字形式", 0, 20),
        ICON("图标风格", 1, 0),
        TEXT("中文编号形式", 2, 0);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum NumShapeStyle implements ProbabilityStyle {
        RECT("矩形", 0, 20),
        ROUND_RECT("圆角矩形", 1, 60),
        ELLIPSE("圆形", 2, 30);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum LowerDecoration implements ProbabilityStyle {
        HIVE("有装饰", 0, 50),
        NONE("没有装饰", 1, 5);
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

    @Getter
    @ToString
    @AllArgsConstructor
    public enum KeywordStyle implements ProbabilityStyle {
        LINE("竖线分割", 0, 50),
        ROUND_RECT("圆角矩形标识", 1, 30);
        private final String name;
        private final int code;
        private final int probability;
    }

}
