package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.Canvas;
import cc.pptshow.build.pptbuilder.domain.CanvasShape;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.util.PositionUtil;
import cc.pptshow.ppt.constant.PPTNameConstant;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 判断主页的文字是否需要进行移动
 */
@Slf4j
@Service
@Design(order = 13, type = PPTBlockType.TEXT, onlyInPage = {PPTPage.HOME, PPTPage.BIG_TITLE})
public class MainPageTextCenterDesignBizImpl implements DesignBiz {

    private static final double halfPage = BConstant.PAGE_WIDTH / 2;

    @Resource
    private TextDesignHelper textDesignHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        if (CollectionUtils.isEmpty(request.getContexts())) {
            request.setContexts(Lists.newArrayList(isInCenter(request.getPptElements())));
        }
        Boolean isCenter = (Boolean) request.getContexts().get(0);
        if (isCenter) {
            moveToCenter((PPTText) request.getPptElement(), request.getPptElements());
        }
        return new DesignResponse(request.getPptElements());
    }

    private void moveToCenter(PPTText pptText, List<PPTElement> pptElements) {
        List<PPTText> haveOtherText = isHorizontalHaveOtherText(pptText, pptElements);
        if (CollectionUtils.isNotEmpty(haveOtherText)) {
            haveOtherText.add(pptText);
            elementsMoveToCenter(haveOtherText, pptElements);
            return;
        }
        textDesignHelper.setTextAlign(pptText, PPTNameConstant.ALIGN_CENTER);
        pptText.getCss().setLeft(halfPage - (pptText.getCss().getWidth() / 2));
        List<PPTShape> shapes = findOnlyCoverTextShapes(pptElements, pptText);
        for (PPTShape shape : shapes) {
            shape.getCss().setLeft(halfPage - (shape.getCss().getWidth() / 2));
        }
    }

    private void elementsMoveToCenter(List<PPTText> haveOtherText, List<PPTElement> pptElements) {
        double removeSize = findRemoveSize(haveOtherText);
        for (PPTText pptText : haveOtherText) {
            textDesignHelper.setTextAlign(pptText, PPTNameConstant.ALIGN_CENTER);
            pptText.getCss().setLeft(pptText.getCss().getWidth() + removeSize);
            List<PPTShape> shapes = findOnlyCoverTextShapes(pptElements, pptText);
            for (PPTShape shape : shapes) {
                shape.getCss().setLeft(shape.getCss().getWidth() + removeSize);
            }
        }
    }

    private double findRemoveSize(List<PPTText> haveOtherText) {
        double left = 0;
        double right = 0;
        for (PPTText pptText : haveOtherText) {
            left = Math.min(pptText.getCss().getLeft(), left);
            right = Math.max(pptText.getCss().getLeft() + pptText.getCss().getWidth(), right);
        }
        return halfPage - ((right - left) / 2) - left;
    }

    private List<PPTText> isHorizontalHaveOtherText(PPTText pptText, List<PPTElement> pptElements) {
        List<PPTElement> elements = Lists.newArrayList(pptElements);
        elements.remove(pptText);
        return elements.stream()
                .filter(element -> element instanceof PPTText)
                .map(element -> (PPTText) element)
                .filter(text -> PositionUtil.overlapProportion(text.getCss().getTop(),
                        text.getCss().getHeight(), pptText.getCss().getTop(), pptText.getCss().getHeight()) > 0.5)
                .collect(Collectors.toList());
    }

    private List<PPTShape> findOnlyCoverTextShapes(List<PPTElement> pptElements, PPTText pptText) {
        CanvasShape canvasShape = Canvas.buildCanvasByPPTTextOnlyTextPlace(pptText);
        double area = canvasShape.area();
        Map<PPTShape, CanvasShape> canvasShapeMap = pptElements.stream()
                .filter(element -> element instanceof PPTShape)
                .map(element -> (PPTShape) element)
                .distinct()
                .collect(Collectors.toMap(e -> e, Canvas::buildCanvasByElement));
        return canvasShapeMap.keySet().stream()
                .filter(pptElement -> canvasShapeMap.get(pptElement).intersectArea(canvasShape) == area)
                .filter(pptElement -> isNotCoverOtherElement(canvasShapeMap, pptElement))
                .collect(Collectors.toList());
    }

    private boolean isNotCoverOtherElement(Map<PPTShape, CanvasShape> canvasShapeMap, PPTShape shape) {
        return canvasShapeMap.keySet().stream()
                .filter(element -> !element.equals(shape))
                .noneMatch(element ->
                        canvasShapeMap.get(shape).intersectArea(canvasShapeMap.get(element))
                                == canvasShapeMap.get(element).area());
    }

    private Boolean isInCenter(List<PPTElement> pptElements) {
        List<PPTText> texts = pptElements.stream()
                .filter(element -> element instanceof PPTText)
                .map(e -> (PPTText) e)
                .filter(text -> textDesignHelper.getFontSize(text) > 14)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(texts)) {
            return false;
        } else if (texts.size() > 1) {
            return isInCenterWithManyTexts(texts);
        } else {
            return texts.get(0).getCss().getLeft() < halfPage
                    && (texts.get(0).getCss().getLeft() + texts.get(0).getCss().getWidth()) > halfPage;
        }

    }

    private boolean isInCenterWithManyTexts(List<PPTText> texts) {
        double leftMoveCount = findLeftMoveCount(texts);
        double middleMoveCount = findMiddleMoveCount(texts);
        return middleMoveCount > leftMoveCount;
    }

    private double findMiddleMoveCount(List<PPTText> texts) {
        List<Double> lefts = texts.stream()
                .map(text -> text.getCss().getLeft())
                .collect(Collectors.toList());
        Double avg = lefts.stream().collect(Collectors.averagingDouble(Double::doubleValue));
        return lefts.stream()
                .map(left -> Math.abs(avg - left))
                .collect(Collectors.summarizingDouble(Double::doubleValue))
                .getSum();
    }

    private double findLeftMoveCount(List<PPTText> texts) {
        List<Double> centers = texts.stream()
                .map(text -> text.getCss().getLeft() + (text.getCss().getWidth() / 2))
                .collect(Collectors.toList());
        return centers.stream()
                .map(center -> Math.abs(halfPage - center))
                .collect(Collectors.summarizingDouble(Double::doubleValue))
                .getSum();
    }
}
