package cc.pptshow.build.pptbuilder.style.content.direction.impl;

import cc.pptshow.build.pptbuilder.domain.qo.DirectionQo;
import cc.pptshow.build.pptbuilder.style.ProbabilityStyle;
import cc.pptshow.build.pptbuilder.style.content.direction.DispersedContent;
import cc.pptshow.build.pptbuilder.style.content.direction.HorizontalContent;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.ppt.element.PPTElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * 数字在上面的情况
 */
@Data
public class TopNumContent implements HorizontalContent, DispersedContent {

    private NumberType numberType;
    private BlockText blockText;
    private DecorationType decorationType;

    public static TopNumContent buildByRand() {
        TopNumContent topNumContent = new TopNumContent();
        topNumContent.setNumberType(RandUtil.randInEnum(NumberType.class));
        topNumContent.setBlockText(RandUtil.randInEnum(BlockText.class));
        topNumContent.setDecorationType(RandUtil.randInEnum(DecorationType.class));
        return topNumContent;
    }

    @Override
    public List<PPTElement> toElements(DirectionQo directionQo) {
        return null;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum NumberType implements ProbabilityStyle {
        BLOCK("块状的", 0, 20),
        TEXT("切割文字的", 1, 20);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum BlockText implements ProbabilityStyle {
        NUMBER_ONLY("只有序号", 0, 20),
        WITH_PART("包含part标识", 1, 20);
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
