package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import cc.pptshow.build.pptbuilder.domain.enums.Position;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CanvasShape {

    private double top;

    private double left;

    private double bottom;

    private double right;

    private double alpha;

    public double area() {
        return (right - left) * (bottom - top);
    }

    public double intersectArea(CanvasShape canvasShape) {
        return Math.max(0, Math.min(Math.min(bottom, canvasShape.getBottom()) - canvasShape.getTop(),
                canvasShape.getBottom() - Math.max(top, canvasShape.getTop()))) *
                Math.max(0, Math.min(Math.min(right, canvasShape.getRight()) - canvasShape.getLeft(),
                        canvasShape.getRight() - Math.max(left, canvasShape.getLeft())));
    }

    /**
     * 相交后彼此边长的交叉线条长度，只有相邻边数是2或者1的时候有意义，其他场景返回-1
     */
    public double intersectXEdgeSize(CanvasShape canvasShape) {
        if (isNotIntersectAble(canvasShape)) {
            return -1;
        }
        return Math.max(0, Math.min(right - canvasShape.getLeft(), canvasShape.getRight() - left));
    }

    private boolean isNotIntersectAble(CanvasShape canvasShape) {
        int edgeNumber = intersectEdgeNumber(canvasShape);
        return edgeNumber <= 0 || edgeNumber >= 3;
    }

    public double intersectYEdgeSize(CanvasShape canvasShape) {
        if (isNotIntersectAble(canvasShape)) {
            return -1;
        }
        return Math.max(0, Math.min(bottom - canvasShape.getTop(), canvasShape.getBottom() - top));
    }

    public static void main(String[] args) {
        PPTShape pptShape1 = JSON.parseObject("{\"css\":{\"angle\":0.0,\"background\":{\"color\":\"bg1\"},\"height\":2.54,\"left\":27.64,\"name\":\"shape\",\"shape\":{\"custGeom\":\"<a:prstGeom prst=\\\"ellipse\\\"><a:avLst /></a:prstGeom>\"},\"top\":8.29,\"width\":2.54}}", PPTShape.class);
        PPTShape pptShape2 = JSON.parseObject("{\"css\":{\"angle\":0.0,\"background\":{\"color\":\"bg1\"},\"height\":2.54,\"left\":21.51,\"name\":\"shape\",\"shape\":{\"custGeom\":\"<a:prstGeom prst=\\\"ellipse\\\"><a:avLst /></a:prstGeom>\"},\"top\":8.29,\"width\":2.54}}", PPTShape.class);
        PPTShape pptShape3 = JSON.parseObject("{\"css\":{\"angle\":0.0,\"background\":{\"color\":\"bg1\"},\"height\":2.54,\"left\":15.379999999999999,\"name\":\"shape\",\"shape\":{\"custGeom\":\"<a:prstGeom prst=\\\"ellipse\\\"><a:avLst /></a:prstGeom>\"},\"top\":8.29,\"width\":2.54}}", PPTShape.class);
        PPTShape pptShape4 = JSON.parseObject("{\"css\":{\"angle\":0.0,\"background\":{\"color\":\"bg1\"},\"height\":2.54,\"left\":9.24,\"name\":\"shape\",\"shape\":{\"custGeom\":\"<a:prstGeom prst=\\\"ellipse\\\"><a:avLst /></a:prstGeom>\"},\"top\":8.29,\"width\":2.54}}", PPTShape.class);
        CanvasShape shape1 = Canvas.buildCanvasByElement(pptShape1);
        CanvasShape shape2 = Canvas.buildCanvasByElement(pptShape2);
        CanvasShape shape3 = Canvas.buildCanvasByElement(pptShape3);
        CanvasShape shape4 = Canvas.buildCanvasByElement(pptShape4);
        List<CanvasShape> shapes = Lists.newArrayList(shape1, shape2, shape3, shape4);
        for (CanvasShape shape : shapes) {
            System.out.println(shape.area());
        }
        for (int i = 0; i < shapes.size(); i++) {
            for (int j = 0; j < shapes.size(); j++) {
                if (i == j) {
                    continue;
                }
                System.out.println(shapes.get(i).intersectArea(shapes.get(j)));
            }
        }
    }

    public int intersectEdgeNumber(CanvasShape canvasShape) {
        return intersectEdgeList(canvasShape).size();
    }

    public List<Position> intersectEdgeList(CanvasShape canvasShape) {
        if (intersectArea(canvasShape) <= 0) {
            return Lists.newArrayList();
        }
        List<Position> positions = Lists.newArrayList();
        if (left < canvasShape.getLeft()) {
            positions.add(Position.LEFT);
        }
        if (top < canvasShape.getTop()) {
            positions.add(Position.TOP);
        }
        if (bottom > canvasShape.getBottom()) {
            positions.add(Position.BOTTOM);
        }
        if (right > canvasShape.getRight()) {
            positions.add(Position.RIGHT);
        }
        return positions;
    }

    public static CanvasShape buildByPPTRegion(PPTRegion pptRegion) {
        CanvasShape canvasShape = new CanvasShape();
        canvasShape.setLeft(pptRegion.getLeftSize());
        canvasShape.setRight(pptRegion.getLeftSize() + pptRegion.getWidthSize());
        canvasShape.setTop(pptRegion.getTopSize());
        canvasShape.setBottom(pptRegion.getTopSize() + pptRegion.getHeightSize());
        return canvasShape;
    }

    public boolean haveIntersectArea(List<CanvasShape> shapes) {
        return shapes.stream().anyMatch(s -> intersectArea(s) > 0);
    }
}
