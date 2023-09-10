package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.helper.ColorHelper;
import cc.pptshow.build.pptbuilder.domain.Canvas;
import cc.pptshow.build.pptbuilder.domain.CanvasShape;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.service.DataIconService;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static cc.pptshow.build.pptbuilder.constant.BConstant.ICON;

/**
 * 之前我们仅将icon标记为了我们需要的PPTImg对象
 * 现在我们给他加上真的图片
 */
@Slf4j
@Service
@Design(type = PPTBlockType.IMG, order = 12)
public class IconImgBuildDesignBizImpl implements DesignBiz {

    @Resource
    private DataIconService dataIconService;

    @Resource
    private ColorHelper colorHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTImg pptImg = (PPTImg) request.getPptElement();
        if (!StringUtils.equals(pptImg.getCss().getName(), ICON)) {
            return DesignResponse.buildByRequest(request);
        }
        List<PPTElement> elements = Lists.newArrayList(request.getPptElements());
        PPTElement pptIcon = request.getPptElement();
        CanvasShape iconShape = Canvas.buildCanvasByElement(pptIcon);
        elements.remove(pptImg);
        PPTElement topmostLayer = colorHelper.findTopmostLayer(iconShape, elements);
        String differentColor = colorHelper.findDifferentColor(topmostLayer,
                        request.getGlobalStyle().getColorInfo(), request.getGlobalStyle().getColorInfo().getFromColor());
        return new DesignResponse(buildByColor(differentColor, pptImg));
    }


    public PPTImg buildByColor(String color, PPTImg pptImg) {
        List<Integer> list = JSON.parseArray(pptImg.getFile(), Integer.class);
        pptImg.setFile(dataIconService.buildIconByColor(color, Sets.newHashSet(list)));
        return pptImg;
    }

}
