package cc.pptshow.build.pptbuilder.biz.design.help;

import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTShape;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BackgroundHelper {

    public List<PPTElement> findNotBackgroundElement(List<PPTElement> pptElements, List<PPTElement> bgElements) {
        pptElements.removeAll(bgElements);
        return pptElements;
    }

    public List<PPTElement> findBackgroundElement(List<PPTElement> pptElements) {
        return pptElements.stream().filter(p -> (
                p instanceof PPTShape
                        && ((PPTShape) p).getCss().getHeight() == BConstant.PAGE_HEIGHT
                        && ((PPTShape) p).getCss().getWidth() == BConstant.PAGE_WIDTH)
                || (
                p instanceof PPTImg
                        && ((PPTImg) p).getCss().getHeight() == BConstant.PAGE_HEIGHT
                        && ((PPTImg) p).getCss().getWidth() == BConstant.PAGE_WIDTH))
                .collect(Collectors.toList());
    }

}
