package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.build.pptbuilder.util.PositionUtil;
import cc.pptshow.ppt.constant.PPTNameConstant;
import cc.pptshow.ppt.domain.PPTImgCss;
import cc.pptshow.ppt.domain.PPTInnerLineCss;
import cc.pptshow.ppt.domain.PPTShapeCss;
import cc.pptshow.ppt.domain.PPTTextCss;
import cc.pptshow.ppt.domain.background.ImgBackground;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTInnerLine;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.util.PositionUtil.isIconShape;

public class Canvas {

    @Getter
    @Setter
    private double width;

    @Getter
    @Setter
    private double height;

    private final Map<CanvasShape, PPTElement> shapes = Maps.newHashMap();

    public Canvas(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public Canvas(double width, double height, List<PPTElement> elements) {
        this.width = width;
        this.height = height;
        for (PPTElement element : elements) {
            if ((element instanceof PPTShape) && !isIconShape(element)) {
                addShape((PPTShape) element, 0D);
            }
            if (element instanceof PPTImg) {
                addImg((PPTImg) element);
            }
        }
    }

    private void addImg(PPTImg img) {
        CanvasShape canvasShape = buildCanvasByImg(img);
        canvasShape.setAlpha(0D);
        shapes.put(canvasShape, img);
    }

    private CanvasShape buildCanvasByImg(PPTImg img) {
        PPTImgCss css = img.getCss();
        return getCanvasShapeBySize(css.getLeft(), css.getTop(), css.getWidth(), css.getHeight());
    }

    public void addShape(PPTShape pptShape, Double alpha) {
        if (Objects.isNull(alpha)) {
            alpha = 0D;
        }
        CanvasShape canvasShape = buildCanvasByShape(pptShape);
        canvasShape.setAlpha(alpha);
        shapes.put(canvasShape, pptShape);
    }

    public static CanvasShape buildCanvasByShape(PPTShape pptShape) {
        PPTShapeCss css = pptShape.getCss();
        return getCanvasShapeBySize(css.getLeft(), css.getTop(), css.getWidth(), css.getHeight());
    }

    public static CanvasShape buildCanvasByElement(PPTElement pptElement) {
        ElementLocation location = PositionUtil.findLocationByElement(pptElement);
        if (Objects.isNull(location)) {
            throw new RuntimeException("获取location对象失败！");
        }
        return getCanvasShapeBySize(location.getLeft(), location.getTop(), location.getWidth(), location.getHeight());
    }

    public List<CanvasShape> findMoreThanHarfIntersect(PPTElement pptShape, CanvasShape canvasShape) {
        List<CanvasShape> withoutSelfShapes = exceptMySelf(pptShape);
        double area = canvasShape.area() * 0.5;
        return findIntersect(withoutSelfShapes, canvasShape, area);
    }

    public List<CanvasShape> findOtherMoreThanHarfIntersectSelf(PPTElement pptShape, CanvasShape canvasShape) {
        List<CanvasShape> withoutSelfShapes = exceptMySelf(pptShape);
        return findOtherIntersect(withoutSelfShapes, canvasShape, 0.5);
    }

    private List<CanvasShape> exceptMySelf(PPTElement pptShape) {
        return shapes.keySet().stream()
                .filter(s -> !shapes.get(s).equals(pptShape))
                .collect(Collectors.toList());
    }

    public List<CanvasShape> findNearlyAllOnTop(PPTShape pptShape) {
        List<CanvasShape> withoutSelfShapes = exceptMySelf(pptShape);

        CanvasShape canvasShape = buildCanvasByShape(pptShape);
        double area = canvasShape.area();
        return findIntersect(withoutSelfShapes, canvasShape, area * 0.85);
    }

    public List<CanvasShape> findNearlyAllOnTop(PPTElement pptElement, CanvasShape canvasShape) {
        List<CanvasShape> withoutSelfShapes = exceptMySelf(pptElement);
        double area = canvasShape.area();
        return findIntersect(withoutSelfShapes, canvasShape, area * 0.85);
    }

    public List<CanvasShape> findAllAsBackground(PPTElement pptElement, CanvasShape canvasShape) {
        List<CanvasShape> withoutSelfShapes = exceptMySelf(pptElement);
        double area = canvasShape.area();
        return shapes.keySet().stream()
                .filter(withoutSelfShapes::contains)
                .filter(s -> s.intersectArea(canvasShape) >= s.area() * 0.85 && s.area() < area)
                .collect(Collectors.toList());
    }

    public List<CanvasShape> findIntersect(PPTShape pptShape) {
        List<CanvasShape> withoutSelfShapes = exceptMySelf(pptShape);
        CanvasShape canvasShape = buildCanvasByShape(pptShape);
        return findIntersect(withoutSelfShapes, canvasShape, 0);
    }

    public List<CanvasShape> findIntersect(List<CanvasShape> withoutSelfShapes, CanvasShape canvasShape, double min) {
        return shapes.keySet().stream()
                .filter(withoutSelfShapes::contains)
                .filter(s -> s.intersectArea(canvasShape) >= min)
                .collect(Collectors.toList());
    }

    public List<CanvasShape> findOtherIntersect(List<CanvasShape> withoutSelfShapes, CanvasShape canvasShape, double proportion) {
        return shapes.keySet().stream()
                .filter(withoutSelfShapes::contains)
                .filter(s -> s.intersectArea(canvasShape) >= s.area() * proportion)
                .collect(Collectors.toList());
    }

    public static CanvasShape buildCanvasByPPTText(PPTText pptText) {
        PPTTextCss css = pptText.getCss();
        return getCanvasShapeBySize(css.getLeft(), css.getTop(), css.getWidth(), css.getHeight());
    }

    public static CanvasShape buildCanvasByPPTTextOnlyTextPlace(PPTText pptText) {
        PPTTextCss css = pptText.getCss();
        CanvasShape canvasShape = getCanvasShapeBySize(css.getLeft(), css.getTop(),
                css.getWidth(), pptText.findMinHeightSize());
        PPTInnerLine pptInnerLine = pptText.getLineList().get(0);
        PPTInnerLineCss lineCss = pptInnerLine.getCss();
        String align = lineCss.getAlign();
        if (StringUtils.equals(align, PPTNameConstant.ALIGN_JUST)
                || StringUtils.equals(align, PPTNameConstant.ALIGN_DIST)) {
            return canvasShape;
        }
        double width = Math.min(pptText.findMinWidthSize(), css.getWidth());
        if (StringUtils.equals(align, PPTNameConstant.ALIGN_LEFT)) {
            canvasShape.setRight(width + css.getLeft());
        }
        if (StringUtils.equals(align, PPTNameConstant.ALIGN_RIGHT)) {
            canvasShape.setLeft(canvasShape.getRight() - width);
        }
        if (StringUtils.equals(align, PPTNameConstant.ALIGN_CENTER)) {
            double middle = (canvasShape.getRight() - canvasShape.getLeft()) / 2 + canvasShape.getLeft();
            canvasShape.setLeft(middle - (width / 2));
        }
        return canvasShape;
    }

    private static CanvasShape getCanvasShapeBySize(double left, double top, double width, double height) {
        CanvasShape canvasShape = new CanvasShape();
        canvasShape.setLeft(left);
        canvasShape.setTop(top);
        canvasShape.setRight(left + width);
        canvasShape.setBottom(top + height);
        return canvasShape;
    }

    public List<PPTElement> queryByShapes(List<CanvasShape> canvasShapes) {
        return canvasShapes.stream().map(shapes::get).collect(Collectors.toList());
    }

    public void add(CanvasShape canvasShape) {
        shapes.put(canvasShape, null);
    }

    public List<CanvasShape> allCanvasShape() {
        return Lists.newArrayList(shapes.keySet());
    }

    public List<CanvasShape> findImgIntersectLessThan25(CanvasShape canvasShape) {
        return shapes.keySet().stream()
                .filter(s -> s.intersectArea(canvasShape) < s.area() * 0.25 && s.intersectArea(canvasShape) >= 0)
                .filter(s -> PositionUtil.isImg(shapes.get(s)))
                .collect(Collectors.toList());
    }

    public List<CanvasShape> findImgIntersect(CanvasShape canvasShape) {
        return shapes.keySet().stream()
                .filter(s -> s.intersectArea(canvasShape) >= 0)
                .filter(s -> PositionUtil.isImg(shapes.get(s)))
                .collect(Collectors.toList());
    }


}
