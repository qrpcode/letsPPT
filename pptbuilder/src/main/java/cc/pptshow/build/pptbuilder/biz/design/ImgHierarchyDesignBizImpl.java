package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.BackgroundHelper;
import cc.pptshow.build.pptbuilder.domain.Canvas;
import cc.pptshow.build.pptbuilder.domain.CanvasShape;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.ppt.domain.background.Background;
import cc.pptshow.ppt.domain.background.ColorBackGround;
import cc.pptshow.ppt.domain.background.ImgBackground;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTShape;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 图片的层级调整
 */
@Slf4j
@Service
@Design(order = 1, needIteration = false)
public class ImgHierarchyDesignBizImpl implements DesignBiz {

    @Resource
    private BackgroundHelper backgroundHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        List<PPTElement> pptElements = request.getPptElements();
        List<PPTElement> backgroundElement = backgroundHelper.findBackgroundElement(pptElements);
        int size = pptElements.size();
        for (int i = 0; i < size; i++) {
            Pair<List<PPTElement>, PPTShape> underShape = findImgUnderShape(pptElements, backgroundElement);
            if (Objects.isNull(underShape)) {
                return new DesignResponse(pptElements);
            }
            pptElements = moveTop(pptElements, underShape.getLeft(), underShape.getRight());
        }
        return new DesignResponse(pptElements);
    }

    private List<PPTElement> moveTop(List<PPTElement> pptElements, List<PPTElement> imgs, PPTShape shape) {
        List<PPTElement> elements = Lists.newArrayList();
        for (PPTElement pptElement : pptElements) {
            if (!imgs.contains(pptElement)) {
                elements.add(pptElement);
            }
            if (shape.equals(pptElement)) {
                elements.addAll(imgs);
            }
        }
        return elements;
    }

    public Pair<List<PPTElement>, PPTShape> findImgUnderShape(List<PPTElement> pptElements,
                                                              List<PPTElement> backgroundElement) {
        List<PPTElement> imgs = Lists.newArrayList();
        for (PPTElement pptElement : pptElements) {
            if (backgroundElement.contains(pptElement)) {
                continue;
            }
            if (isImg(pptElement)) {
                imgs.add(pptElement);
            } else if (pptElement instanceof PPTShape) {
                List<PPTElement> coverImg = findAllCover(imgs, (PPTShape) pptElement);
                if (CollectionUtils.isNotEmpty(coverImg)) {
                    return Pair.of(coverImg, (PPTShape) pptElement);
                }
            }
        }
        return null;
    }

    private List<PPTElement> findAllCover(List<PPTElement> imgs, PPTShape pptShape) {
        CanvasShape canvasShape = Canvas.buildCanvasByShape(pptShape);
        return imgs.stream().filter(img -> {
            CanvasShape shape = Canvas.buildCanvasByElement(img);
            return shape.intersectArea(canvasShape) > shape.area() * 0.8;
        }).collect(Collectors.toList());
    }

    public boolean isImg(PPTElement pptElement) {
        return pptElement instanceof PPTImg
                || (pptElement instanceof PPTShape
                && ((PPTShape) pptElement).getCss().getBackground() instanceof ImgBackground);
    }
}
