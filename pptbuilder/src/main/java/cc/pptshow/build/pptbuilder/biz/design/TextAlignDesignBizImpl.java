package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.domain.Canvas;
import cc.pptshow.build.pptbuilder.domain.CanvasShape;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.ppt.constant.PPTNameConstant;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据文字对齐方式纠正文本框大小
 * 解决文本框太窄，文本被迫换行问题
 */
@Slf4j
@Service
@Design(type = PPTBlockType.TEXT, order = 7)
public class TextAlignDesignBizImpl implements DesignBiz {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        adjustmentTextAlign(pptText, request.getPptElements());
        return new DesignResponse(pptText);
    }

    public void adjustmentTextAlign(PPTText pptText, List<PPTElement> elements) {
        String align = pptText.getLineList().get(0).getCss().getAlign();
        if (PPTNameConstant.ALIGN_LEFT.equals(align)) {
            if (isMaybeMoreLong(pptText)
                    && isNotHaveRightText(elements, pptText)) {
                adjustmentTextMoreRight(pptText);
            }
        } else if (PPTNameConstant.ALIGN_RIGHT.equals(align)) {
            if (isMaybeMoreLong(pptText)
                    && isNotHaveLeftText(elements, pptText)) {
                adjustmentTextMoreLeft(pptText);
            }
        } else if (PPTNameConstant.ALIGN_CENTER.equals(align)) {
            if (isMaybeMoreLong(pptText)) {
                if (isNotHaveLeftAndRightText(elements, pptText, 0.25)) {
                    adjustmentTextMoreWidth(pptText, 0.25);
                } else if (isNotHaveLeftAndRightText(elements, pptText, 0.1)) {
                    adjustmentTextMoreWidth(pptText, 0.1);
                }
            }
        }
    }

    private boolean isMaybeMoreLong(PPTText pptText) {
        PPTText clone = pptText.clone();
        //最大只允许宽容两个汉字宽度
        clone.getLineList().get(0).getTextList().get(0).setText(pptText.findAllText() + "汉字");
        return !textDesignHelper.isOnlyOneLine(pptText)
                && pptText.getCss().getWidth() < pptText.findMinWidthSize() * 1.1
                && (clone.findMinWidthSize() - pptText.findMinWidthSize()) + pptText.getCss().getWidth()
                >= pptText.findMinWidthSize();
    }

    /**
     * 如果当前图形向右侧拓展25%的宽度，是否会导致原本不重叠的文字发生重叠
     */
    private boolean isNotHaveRightText(List<PPTElement> elements, PPTText pptText) {
        PPTText cloneText = pptText.clone();
        adjustmentTextMoreRight(cloneText);
        return isHaveNoNewIntersectElements(elements, cloneText, pptText);
    }
    private void adjustmentTextMoreRight(PPTText pptText) {
        double width = pptText.getCss().getWidth() * 1.25;
        pptText.getCss().setWidth(width);
    }

    private boolean isNotHaveLeftText(List<PPTElement> elements, PPTText pptText) {
        PPTText cloneText = pptText.clone();
        adjustmentTextMoreLeft(cloneText);
        return isHaveNoNewIntersectElements(elements, cloneText, pptText);
    }

    private boolean isNotHaveLeftAndRightText(List<PPTElement> elements, PPTText pptText, double moreSizeMultiple) {
        PPTText clone = pptText.clone();
        adjustmentTextMoreWidth(clone, moreSizeMultiple);
        return isHaveNoNewIntersectElements(elements, clone, pptText);
    }

    private boolean isHaveNoNewIntersectElements(List<PPTElement> elements, PPTText cloneText, PPTText oldText) {
        List<PPTElement> pptElements = Lists.newArrayList(elements);
        pptElements.remove(oldText);
        CanvasShape cloneTextShape = Canvas.buildCanvasByPPTText(cloneText);
        CanvasShape oldTextShape = Canvas.buildCanvasByPPTText(oldText);
        boolean textIntersect = pptElements.stream()
                .filter(e -> e instanceof PPTText)
                .map(e -> (PPTText) e)
                .map(Canvas::buildCanvasByPPTTextOnlyTextPlace)
                .noneMatch(s -> cloneTextShape.intersectArea(s) > 0 && oldTextShape.intersectArea(s) == 0);
        boolean otherIntersect = pptElements.stream()
                .map(Canvas::buildCanvasByElement)
                .noneMatch(s -> s.intersectArea(cloneTextShape) > 0 && s.intersectArea(oldTextShape) == 0);
        return otherIntersect && textIntersect;
    }

    private void adjustmentTextMoreWidth(PPTText pptText, double moreSizeMultiple) {
        double moveSize = pptText.getCss().getWidth() * (moreSizeMultiple / 2);
        pptText.getCss().setLeft(pptText.getCss().getLeft() - moveSize).setWidth(pptText.getCss().getWidth() + (moveSize * 2));
    }

    private void adjustmentTextMoreLeft(PPTText pptText) {
        pptText.getCss()
                .setLeft(pptText.getCss().getLeft() - (pptText.getCss().getWidth() * 0.25))
                .setWidth(pptText.getCss().getWidth() * 1.25);
    }

}
