package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.biz.helper.LayerHelper;
import cc.pptshow.build.pptbuilder.domain.Canvas;
import cc.pptshow.build.pptbuilder.domain.CanvasShape;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.service.PPTPageTypeService;
import cc.pptshow.ppt.constant.PPTNameConstant;
import cc.pptshow.ppt.domain.PPTTextCss;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.constant.BConstant.MAX_WIDTH;

/**
 * 内页的针对可能居中的文本判定
 * 不包含 主页、大标题页 这俩会采用独立的判定
 */
@Slf4j
@Service
@Design(type = PPTBlockType.TEXT,
        order = 5,
        excludePage = {PPTPage.HOME, PPTPage.BIG_TITLE})
public class TextCenterDesignBizImpl implements DesignBiz {

    private static final double NORMAL_MOVE_PROPORTION = 4.0;

    @Resource
    private PPTPageTypeService pptPageTypeService;

    @Resource
    private LayerHelper layerHelper;

    @Resource
    private TextDesignHelper textDesignHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        List<PPTElement> pptElements = request.getPptElements();
        List<PPTElement> backgroundElements =
                layerHelper.findHalfIntersectBackgroundElements(request.getCanvas(), pptText);
        adjustmentTextCenter(pptText, request.getPageId(), backgroundElements, pptElements);
        return new DesignResponse(pptText);
    }

    private void adjustmentTextCenter(PPTText pptText, Integer pageTypeId,
                                      List<PPTElement> backgroundElements,
                                      List<PPTElement> pptElements) {
        //是不是可能和某个元素是对齐的
        PPTShape shape = findMaybeCenterBackgroundElement(pptText, backgroundElements);
        if (Objects.nonNull(shape)) {
            boolean haveMiniTitle = isHaveMiniTitleNotCenter(shape,
                    pptElements, backgroundElements, textDesignHelper.getFontSize(pptText));
            if (!haveMiniTitle) {
                adjustmentTextToElementCenter(pptText, shape);
            }
        }
        if (isMaybeCenter(pptText, pageTypeId, NORMAL_MOVE_PROPORTION)) {
            //如果命中了迁移算法
            adjustmentTextToCenter(pptText);
        }
    }

    private boolean isHaveMiniTitleNotCenter(PPTShape shape, List<PPTElement> pptElements,
                                             List<PPTElement> backgroundElements, int fontSize) {
        CanvasShape canvasShape = Canvas.buildCanvasByElement(shape);
        return pptElements.stream()
                .filter(p -> p instanceof PPTText)
                .anyMatch(text -> {
                    PPTText pptText = (PPTText) text;
                    CanvasShape textShape = Canvas.buildCanvasByPPTTextOnlyTextPlace(pptText);
                    boolean sameSize = canvasShape.intersectArea(textShape) == textShape.area();
                    int thisSize = textDesignHelper.getFontSize(pptText);
                    PPTShape pptShape = findMaybeCenterBackgroundElement(pptText, backgroundElements);
                    return sameSize && thisSize > fontSize && Objects.isNull(pptShape);
                });
    }

    private PPTShape findMaybeCenterBackgroundElement(PPTText pptText, List<PPTElement> backgroundElements) {
        List<PPTShape> pptShapes = backgroundElements.stream()
                .filter(e -> e instanceof PPTShape)
                .map(e -> (PPTShape) e)
                .filter(e -> {
                    PPTText cloneText = pptText.clone();
                    PPTTextCss css = textDesignHelper.chooseTextCssOnlyTextNeed(cloneText).getCss();
                    return isMaybeInShapeCenter(css, e);
                })
                .collect(Collectors.toList());
        return findMostNearlyOnce(pptText, pptShapes);
    }

    @Nullable
    private PPTShape findMostNearlyOnce(PPTText pptText, List<PPTShape> pptShapes) {
        if (CollectionUtils.isEmpty(pptShapes)) {
            return null;
        }
        double middle = pptText.getCss().getLeft() + (pptText.getCss().getWidth() / 2);
        PPTShape pptShape = null;
        double diffSize = Double.MAX_VALUE;
        for (PPTShape shape : pptShapes) {
            double diff = Math.abs(shape.getCss().getLeft() + (shape.getCss().getWidth() / 2) - middle);
            if (diff < diffSize) {
                pptShape = shape;
                diffSize = diff;
            }
        }
        return pptShape;
    }

    private boolean isMaybeInShapeCenter(PPTTextCss css, PPTShape pptShape) {
        return canCenterByMove(css, pptShape) || textNearlyBoxSize(css, pptShape);
    }

    private boolean canCenterByMove(PPTTextCss css, PPTShape pptShape) {
        //如果图案压中轴线并且移动＜1/4图形宽度就可以到居中我们认为这个图案应该居中
        double centerPoint = pptShape.getCss().getLeft() + pptShape.getCss().getWidth() / 2;
        return isMaybeInCenter(css, centerPoint, NORMAL_MOVE_PROPORTION);
    }

    private boolean textNearlyBoxSize(PPTTextCss css, PPTShape pptShape) {
        //如果文字大小和图形基本都一致了那就认为这个就是应该居中这个图形的
        double textArea = css.getWidth() * css.getHeight();
        double shapeArea = pptShape.getCss().getWidth() * pptShape.getCss().getHeight();
        double proportion1 = textArea / shapeArea;
        double proportion2 = shapeArea / textArea;
        return (proportion1 >= 0.75 && proportion1 <= 1) || (proportion2 >= 0.75 && proportion2 <= 1);
    }

    private boolean isMaybeInCenter(PPTTextCss css, double centerPoint, double maxMoveProportion) {
        //如果图案压中轴线并且移动＜ 1/maxMoveProportion 图形宽度就可以到居中我们认为这个图案应该居中
        return css.getLeft() < centerPoint
                && (css.getLeft() + css.getWidth()) > centerPoint
                && Math.abs(css.getLeft() + (css.getWidth() / 2) - centerPoint) < (css.getWidth() / maxMoveProportion);
    }

    private void adjustmentTextToElementCenter(PPTText pptText, PPTShape element) {
        pptText.getLineList().get(0).getCss().setAlign(PPTNameConstant.ALIGN_CENTER);
        pptText.getCss().setLeft(
                element.getCss().getLeft() + ((element.getCss().getWidth() - pptText.getCss().getWidth()) / 2));
    }

    public boolean isMaybeCenter(PPTText pptText, Integer pageTypeId, double maxMoveProportion) {
        List<Integer> pageIds = pptPageTypeService.findTextMaybeCenterPageIds();
        if (pageIds.contains(pageTypeId)) {
            PPTTextCss css = pptText.getCss();
            return isMaybeInCenter(css, MAX_WIDTH / 2, maxMoveProportion);
        }
        return false;
    }

    public void adjustmentTextToCenter(PPTText pptText) {
        pptText.getLineList().get(0).getCss().setAlign(PPTNameConstant.ALIGN_CENTER);
        pptText.getCss().setLeft(MAX_WIDTH / 2 - (pptText.getCss().getWidth() / 2));
    }

}
