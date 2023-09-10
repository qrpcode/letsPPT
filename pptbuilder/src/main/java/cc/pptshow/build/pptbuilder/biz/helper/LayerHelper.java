package cc.pptshow.build.pptbuilder.biz.helper;

import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.domain.Canvas;
import cc.pptshow.build.pptbuilder.domain.CanvasShape;
import cc.pptshow.build.pptbuilder.domain.ElementLocation;
import cc.pptshow.build.pptbuilder.service.PPTRegionService;
import cc.pptshow.build.pptbuilder.util.PositionUtil;
import cc.pptshow.ppt.domain.background.NoBackground;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class LayerHelper {

    @Resource
    private PPTRegionService pptRegionService;

    public int findLayerInElements(List<PPTElement> elements) {
        List<PPTElement> pptElements = elements.stream()
                .filter(element ->
                        element instanceof PPTImg ||
                                ((element instanceof PPTShape)
                                        && !(((PPTShape) element).getCss().getBackground() instanceof NoBackground))
                ).collect(Collectors.toList());
        log.info("[findLayerInElements] form:{}, to:{}", JSON.toJSONString(elements), JSON.toJSONString(pptElements));
        return pptElements.size();
    }


    public List<PPTElement> findHalfIntersectBackgroundElements(Canvas canvas, PPTText topElement) {
        CanvasShape canvasShape = Canvas.buildCanvasByPPTTextOnlyTextPlace(topElement);
        List<CanvasShape> intersects = canvas.findMoreThanHarfIntersect(topElement, canvasShape);
        return canvas.queryByShapes(intersects);
    }


    public CanvasShape findElementsToShape(List<PPTElement> titleElements) {
        List<ElementLocation> locations = titleElements.stream()
                .map(PositionUtil::findLocationByElement)
                .filter(Objects::nonNull).collect(Collectors.toList());
        double top = locations.stream().map(ElementLocation::getTop)
                .mapToDouble(Double::new)
                .min()
                .orElse(0D);
        double bottom = locations.stream().map(ElementLocation::getBottom)
                .mapToDouble(Double::new)
                .max()
                .orElse(0D);
        double left = locations.stream().map(ElementLocation::getLeft)
                .mapToDouble(Double::new)
                .min()
                .orElse(0D);
        double right = locations.stream().map(ElementLocation::getRight)
                .mapToDouble(Double::new)
                .max()
                .orElse(0D);
        CanvasShape canvasShape = new CanvasShape();
        canvasShape.setLeft(left);
        canvasShape.setRight(right);
        canvasShape.setBottom(bottom);
        canvasShape.setTop(top);
        return canvasShape;
    }

    public List<PPTRegionPut> findOverlap(CanvasShape titleShape, List<PPTRegionPut> regionPuts) {
        if (Objects.isNull(titleShape)) {
            return Lists.newArrayList();
        }
        return regionPuts.stream().filter(r -> {
            List<PPTRegion> regions = pptRegionService.queryRegionsByBlock(r);
            return regions.stream()
                    .map(CanvasShape::buildByPPTRegion)
                    .anyMatch(c -> c.intersectArea(titleShape) > 0);
        }).collect(Collectors.toList());
    }
}
