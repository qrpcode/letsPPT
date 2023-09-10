package cc.pptshow.build.pptbuilder.biz.design.help;

import cc.pptshow.build.pptbuilder.biz.helper.ColorHelper;
import cc.pptshow.build.pptbuilder.biz.filter.region.PositionFilter;
import cc.pptshow.build.pptbuilder.domain.Canvas;
import cc.pptshow.build.pptbuilder.domain.CanvasShape;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.Position;
import cc.pptshow.build.pptbuilder.service.ImgInfoService;
import cc.pptshow.build.pptbuilder.util.Safes;
import cc.pptshow.ppt.domain.background.Background;
import cc.pptshow.ppt.domain.background.ColorBackGround;
import cc.pptshow.ppt.domain.background.ImgBackground;
import cc.pptshow.ppt.domain.background.NoBackground;
import cc.pptshow.ppt.domain.border.Border;
import cc.pptshow.ppt.domain.border.ColorBorder;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTLine;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShapeHierarchyDesignHelper {

    @Resource
    private PositionFilter positionFilter;

    @Resource
    private ImgInfoService imgInfoService;

    @Resource
    private ColorHelper colorHelper;

    public List<PPTElement> buildPPTShapes(List<PPTElement> pptElements,
                                            List<PPTText> pptTexts,
                                            GlobalStyle globalStyle,
                                            Canvas canvas) {
        List<PPTElement> elements = Lists.newArrayList();
        List<PPTElement> textBackgroundElements = findTextBackgroundElements(pptTexts, pptElements);
        List<PPTElement> sortElements =
                positionFilter.sortElements(pptElements.stream()
                        .filter(e -> !positionFilter.isLineElement(e))
                        .collect(Collectors.toList()));
        log.info("[sortElements] sortElements:{}", sortElements);
//        List<CanvasShape> textShapes = pptTexts.stream()
//                .map(Canvas::buildCanvasByPPTTextOnlyTextPlace).collect(Collectors.toList());
        for (PPTElement element : sortElements) {
            if (elements.contains(element)) {
                continue;
            }
            CanvasShape canvasShape = Canvas.buildCanvasByElement(element);
            //找小图形在大图片上面的case
            List<CanvasShape> intersects = canvas.findNearlyAllOnTop(element, canvasShape);
            //找小图像在大图形上面的case
            List<CanvasShape> images = canvas.findImgIntersectLessThan25(canvasShape);
            //找大图形是中图片的背景case
            List<CanvasShape> forBackground = canvas.findAllAsBackground(element, canvasShape);
            List<PPTElement> bgElements = canvas.queryByShapes(intersects);
            List<PPTElement> forBgElements = canvas.queryByShapes(forBackground);
            elements.removeAll(bgElements);
            elements.removeAll(forBgElements);
            List<PPTElement> thisBackgroundElements = safeElements(pptElements, bgElements);
//            //如果是文字背景元素，所有的图片背景都需要在下层
//            if (textBackgroundElements.contains(element)) {
//                List<CanvasShape> imgIntersect = canvas.findImgIntersect(canvasShape);
//                List<CanvasShape> textUnderImgShapes = Safes.of(imgIntersect).stream()
//                        .filter(shape -> shape.haveIntersectArea(textShapes)).collect(Collectors.toList());
//                List<PPTElement> textUnderImgElements = canvas.queryByShapes(textUnderImgShapes)
//                        .stream().filter(e -> !bgElements.contains(e)).collect(Collectors.toList());
//                elements.removeAll(textUnderImgElements);
//                thisBackgroundElements.addAll(textUnderImgElements);
//            }
            List<PPTElement> forBackgroundElements = safeElements(pptElements, forBgElements);
            elements.addAll(canvas.queryByShapes(images).stream()
                    .filter(e -> !elements.contains(e))
                    .collect(Collectors.toList()));
            log.info("[buildPPTShapes] intersects:{}, bgElements:{}, thisBackgroundElements:{}",
                    JSON.toJSONString(intersects), JSON.toJSONString(bgElements), JSON.toJSONString(thisBackgroundElements));
            List<PPTElement> allElements = findListForElement(element, thisBackgroundElements, forBackgroundElements);
            elements.addAll(buildTopAndBackgroundElements(globalStyle, allElements, textBackgroundElements, canvas));
        }
        return elements;
    }

    private List<PPTElement> findListForElement(PPTElement element,
                                                List<PPTElement> thisBackgroundElements,
                                                List<PPTElement> forBackgroundElements) {
        List<PPTElement> allElements = Lists.newArrayList(thisBackgroundElements);
        allElements.add(element);
        allElements.addAll(forBackgroundElements);
        allElements = positionFilter.sortElements(allElements);
        return allElements;
    }

    @NotNull
    private List<PPTElement> safeElements(List<PPTElement> pptElements, List<PPTElement> bgElements) {
        return bgElements.stream()
                .filter(pptElements::contains)
                .collect(Collectors.toList());
    }

    private List<PPTElement> buildTopAndBackgroundElements(GlobalStyle globalStyle,
                                                           List<PPTElement> allElements,
                                                           List<PPTElement> textBackgroundElements,
                                                           Canvas canvas) {
        List<PPTElement> elements = Lists.newArrayList();
        Collections.reverse(allElements);
        int layers = 0;
        for (PPTElement pptElement : allElements) {
            log.info("[allElements] {}", allElements);
            if (pptElement instanceof PPTShape) {
                Background shapeBackground = ((PPTShape) pptElement).getCss().getBackground();
                if (shapeBackground instanceof ImgBackground) {
                    addRandImgInShape((ImgBackground) shapeBackground);
                } else if (isNoFillShape((PPTShape) pptElement)) {
                    addColorBorderInShape(globalStyle, layers, (PPTShape) pptElement);
                } else {
                    layers++;
                    if (isNotAllFillColorBackground(shapeBackground)) {
                        addColorInShapeWithAlpha(globalStyle, layers, pptElement, ((ColorBackGround) shapeBackground).getAlpha());
                    } else if (textBackgroundElements.contains(pptElement)) {
                        addColorWithFullAlphaInShape(globalStyle, layers, pptElement);
                    } else {
                        addColorInShape(globalStyle, canvas, layers, pptElement);
                    }
                }
            } else if (pptElement instanceof PPTLine) {
                ((PPTLine) pptElement).getCss().setColor(colorHelper.getShapeContrastColor(globalStyle));
            }
            elements.add(pptElement);
        }
        return elements;
    }

    private void addColorWithFullAlphaInShape(GlobalStyle globalStyle, int layers, PPTElement background) {
        addColorInShapeWithAlpha(globalStyle, layers, background, 0D);
    }

    private void addColorInShape(GlobalStyle globalStyle, Canvas canvas, int layers, PPTElement background) {
        Double alpha = colorHelper.findAlpha((PPTShape) background, canvas, globalStyle);
        addColorInShapeWithAlpha(globalStyle, layers, background, alpha);
    }

    private void addColorInShapeWithAlpha(GlobalStyle globalStyle, int layers, PPTElement background, Double alpha) {
        String color;
        if (layers % 2 == 1) {
            color = colorHelper.getShapeContrastColor(globalStyle);
        } else {
            color = colorHelper.getShapeNormalColor(globalStyle);
        }
        log.info("[buildTopAndBackgroundElements] color:{}, alpha:{}, background:{}",
                color, alpha, JSON.toJSONString(background));
        ((PPTShape) background).getCss().setBackground(ColorBackGround.buildByColor(color, alpha)).setBorder(null);
    }

    private boolean isNotAllFillColorBackground(Background shapeBackground) {
        return shapeBackground instanceof ColorBackGround
                && Objects.nonNull(((ColorBackGround) shapeBackground).getAlpha())
                && ((ColorBackGround) shapeBackground).getAlpha() != 0;
    }

    private void addRandImgInShape(ImgBackground shapeBackground) {
        shapeBackground.setImg(imgInfoService.randInnerImg());
    }

    private boolean isNoFillShape(PPTShape pptShape) {
        return (pptShape.getCss().getBackground() instanceof NoBackground);
    }

    private void addColorBorderInShape(GlobalStyle globalStyle, int layers, PPTShape background) {
        String color;
        if (layers % 2 == 1) {
            color = colorHelper.getShapeNormalColor(globalStyle);
        } else {
            color = colorHelper.getShapeContrastColor(globalStyle);
        }
        Border border = background.getCss().getBorder();
        if (border instanceof ColorBorder) {
            ((ColorBorder) border).setColor(color);
        }
    }

    private List<PPTElement> findTextBackgroundElements(List<PPTText> textElements, List<PPTElement> backgrounds) {
        List<CanvasShape> textShapes = textElements.stream()
                .map(Canvas::buildCanvasByPPTTextOnlyTextPlace)
                .collect(Collectors.toList());
        List<PPTShape> intersectElements = backgrounds.stream()
                .filter(p -> p instanceof PPTShape)
                .map(p -> (PPTShape) p)
                .filter(element -> {
                    CanvasShape canvasShape = Canvas.buildCanvasByElement(element);
                    return textShapes.stream().anyMatch(t -> t.intersectArea(canvasShape) > 0);
                })
                .collect(Collectors.toList());
        Set<PPTShape> needRemoveElements = Sets.newHashSet();
        for (PPTShape pptShape : intersectElements) {
            if (needRemoveElements.contains(pptShape)) {
                break;
            }
            List<PPTShape> shapes = Lists.newArrayList(intersectElements);
            shapes.removeAll(needRemoveElements);
            if (isOtherElementCover(pptShape, shapes, textShapes) && isTransitionElement(pptShape, backgrounds)) {
                needRemoveElements.add(pptShape);
            }
        }
        intersectElements.removeAll(needRemoveElements);
        return intersectElements.stream().map(e -> (PPTElement) e).collect(Collectors.toList());
    }

    /**
     * 自己覆盖的文字是不是能被别人cover住，如果其他图形能够cover住，本身即使走正常透明逻辑也不会影响
     */
    private boolean isOtherElementCover(PPTShape pptShape, List<PPTShape> shapes, List<CanvasShape> textShapes) {
        shapes.remove(pptShape);
        return textShapes.stream().anyMatch(text -> {
            for (PPTShape shape : shapes) {
                CanvasShape canvasShape = Canvas.buildCanvasByElement(shape);
                if (canvasShape.intersectArea(text) == text.area()) {
                    return false;
                }
            }
            return true;
        });
    }

    /**
     * 是否是过渡元素
     * 过渡元素：上下边 或 左右边 彼此相邻的元素类型不一致，相邻不一致元素至少长度占到一半
     * 目前可以视为不一致的仅包含两种：图片、空白  图片、图形
     * 比如：左侧是一个图片，右侧是空的那就是一个过渡元素
     * <p>
     * Tips: 其实还应该考虑对角线场景，但是不好写这里先忽略掉
     */
    private boolean isTransitionElement(PPTShape pptShape, List<PPTElement> allElements) {
        ArrayList<PPTElement> pptElements = Lists.newArrayList(allElements);
        pptElements.remove(pptShape);
        CanvasShape canvasShape = Canvas.buildCanvasByElement(pptShape);
        List<PPTElement> intersectElements = findIntersectElements(pptShape, pptElements, canvasShape);
        if (intersectElements.size() == 2) {
            List<Position> edges1 = Canvas.buildCanvasByElement(intersectElements.get(0)).intersectEdgeList(canvasShape);
            List<Position> edges2 = Canvas.buildCanvasByElement(intersectElements.get(1)).intersectEdgeList(canvasShape);
            Set<Position> positionSet = Sets.newHashSet();
            positionSet.addAll(edges1);
            positionSet.addAll(edges2);
            return isMatchTransitionElements(intersectElements, positionSet);
        } else {
            return intersectElements.size() == 1 && intersectElements.get(0) instanceof PPTImg;
        }
    }

    @NotNull
    private List<PPTElement> findIntersectElements(PPTShape pptShape, ArrayList<PPTElement> pptElements, CanvasShape canvasShape) {
        return pptElements.stream()
                .filter(p -> (p instanceof PPTShape) || (p instanceof PPTImg))
                .filter(p -> {
                    CanvasShape shape = Canvas.buildCanvasByElement(p);
                    return shape.intersectArea(canvasShape) > 0
                            && shape.intersectEdgeNumber(canvasShape) == 1
                            && (shape.intersectXEdgeSize(canvasShape) > 0.5 * pptShape.getCss().getWidth()
                            || shape.intersectYEdgeSize(canvasShape) >= 0.5 * pptShape.getCss().getHeight());
                })
                .collect(Collectors.toList());
    }

    private boolean isMatchTransitionElements(List<PPTElement> intersectElements, Set<Position> positionSet) {
        return positionSet.size() == 2
                && ((positionSet.contains(Position.LEFT) && positionSet.contains(Position.RIGHT))
                || (positionSet.contains(Position.TOP) && positionSet.contains(Position.BOTTOM)))
                && intersectElements.stream().anyMatch(e -> e instanceof PPTImg);
    }

}
