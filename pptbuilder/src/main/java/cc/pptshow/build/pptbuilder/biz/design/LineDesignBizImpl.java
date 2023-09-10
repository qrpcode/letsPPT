package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.helper.ColorHelper;
import cc.pptshow.build.pptbuilder.biz.filter.region.PositionFilter;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.ppt.element.impl.PPTLine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 线条颜色填充
 */
@Slf4j
@Service
@Design(type = PPTBlockType.LINE, order = 2)
public class LineDesignBizImpl implements DesignBiz {

    @Resource
    private ColorHelper colorHelper;

    @Resource
    private PositionFilter positionFilter;

    @Override
    public DesignResponse design(DesignRequest request) {
        if (!positionFilter.isLineElement(request.getPptElement())) {
            return DesignResponse.buildByRequest(request);
        }
        ((PPTLine) request.getPptElement()).getCss()
                .setColor(colorHelper.getShapeContrastColor(request.getGlobalStyle()));
        return DesignResponse.buildByRequest(request);
    }
}
