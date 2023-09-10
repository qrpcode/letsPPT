package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.BackgroundHelper;
import cc.pptshow.build.pptbuilder.biz.design.help.ShapeHierarchyDesignHelper;
import cc.pptshow.build.pptbuilder.biz.filter.region.PositionFilter;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.*;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 调整元素的层次
 */
@Slf4j
@Service
@Design(order = 0, needIteration = false)
public class HierarchyDesignBizImpl implements DesignBiz {

    @Resource
    private PositionFilter positionFilter;

    @Resource
    private ShapeHierarchyDesignHelper shapeHierarchyDesignHelper;

    @Resource
    private BackgroundHelper backgroundHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        List<PPTElement> bgElements = backgroundHelper.findBackgroundElement(request.getPptElements());
        List<PPTElement> pptElements = backgroundHelper.findNotBackgroundElement(request.getPptElements(), bgElements);
        List<PPTElement> newElements = Lists.newArrayList();
        assert pptElements != null;
        List<PPTElement> lineElements = buildPPTLine(pptElements);
        List<PPTText> textElements = positionFilter.findAllTextElements(pptElements);
        List<PPTShape> iconElements = positionFilter.findAllIconElements(pptElements);
        List<PPTElement> picAndShape = findAllPicAndShape(pptElements, lineElements, textElements, iconElements);

        newElements.addAll(bgElements);
        newElements.addAll(lineElements);
        newElements.addAll(shapeHierarchyDesignHelper.buildPPTShapes(picAndShape,
                textElements, request.getGlobalStyle(), request.getCanvas()));
        newElements.addAll(iconElements);
        newElements.addAll(textElements);

        DesignResponse designResponse = DesignResponse.buildByRequest(request);
        designResponse.setPptElements(newElements);
        return designResponse;
    }


    private List<PPTElement> findAllPicAndShape(List<PPTElement> pptElements,
                                                List<PPTElement> lineElements,
                                                List<PPTText> textElements,
                                                List<PPTShape> iconElements) {
        List<PPTElement> elements = Lists.newArrayList(pptElements);
        elements.removeAll(lineElements);
        elements.removeAll(textElements);
        elements.removeAll(iconElements);
        return elements;
    }

    private List<PPTElement> buildPPTLine(List<PPTElement> elements) {
        return elements.stream()
                .filter(element -> positionFilter.isLineElement(element))
                .collect(Collectors.toList());
    }

}
