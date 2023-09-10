package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.domain.Canvas;
import cc.pptshow.build.pptbuilder.domain.CanvasShape;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.ppt.domain.background.ImgBackground;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTShape;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 如果一张图片10%以下和一个图形的30%以上相交，就把图片放到图形的下一层
 */
@Slf4j
@Service
@Design(order = 14,
        needIteration = false,
        excludePage = {PPTPage.HOME, PPTPage.BIG_TITLE})
public class BigImgUnderDesignBiz implements DesignBiz {

    @Override
    public DesignResponse design(DesignRequest request) {
        List<PPTElement> pptElements = request.getPptElements();
        List<PPTElement> newElements = Lists.newArrayList();
        for (int i = 0; i < pptElements.size(); i++) {
            PPTElement pptElement = pptElements.get(i);
            if (newElements.contains(pptElement)) {
                continue;
            }
            if (pptElement instanceof PPTShape) {
                CanvasShape shape = Canvas.buildCanvasByElement(pptElement);
                List<PPTElement> elements = pptElements.subList(i + 1, pptElements.size());
                List<PPTElement> images = elements.stream()
                        .filter(isShowWithImg())
                        .filter(element -> {
                            CanvasShape imgShape = Canvas.buildCanvasByElement(element);
                            double area = imgShape.intersectArea(shape);
                            return area < 0.1 * imgShape.area() && area > 0.3 * shape.area();
                        }).collect(Collectors.toList());
                newElements.addAll(images.stream()
                        .filter(img -> !newElements.contains(img))
                        .collect(Collectors.toList()));
            }
            newElements.add(pptElement);
        }
        return new DesignResponse(newElements);
    }

    @NotNull
    private Predicate<PPTElement> isShowWithImg() {
        return element -> element instanceof PPTImg
                || (element instanceof PPTShape
                && ((PPTShape) element).getCss().getBackground() instanceof ImgBackground);
    }

}
