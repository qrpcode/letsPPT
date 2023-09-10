package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.filter.region.PositionFilter;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.ppt.domain.PPTImgCss;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTShape;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cc.pptshow.build.pptbuilder.constant.BConstant.SYS_PATH;

/**
 * 将LOGO标签都替换成真实的LOGO
 */
@Slf4j
@Service
@Design(type = PPTBlockType.SHAPE, order = 1)
public class LogoDesignBizImpl implements DesignBiz {

    @Resource
    private PositionFilter positionFilter;

    @Override
    public DesignResponse design(DesignRequest request) {
        if (!positionFilter.isLogoShape(request.getPptElement())) {
            return DesignResponse.buildByRequest(request);
        }
        PPTImg pptImg = buildLogoElement(request);
        pptImg.getCss().setName(BConstant.LOGO);
        return new DesignResponse(pptImg, request);
    }

    private PPTImg buildLogoElement(DesignRequest request) {
        PPTShape pptShape = (PPTShape) request.getPptElement();
        double proportion = pptShape.getCss().getWidth() / pptShape.getCss().getHeight();
        return buildByProportion(proportion, pptShape);
    }

    private PPTImg buildByProportion(double proportion, PPTShape pptShape) {
        PPTImg pptImg = new PPTImg();
        PPTImgCss pptImgCss = new PPTImgCss();
        pptImg.setCss(pptImgCss);
        pptImgCss.setWidth(pptShape.getCss().getWidth());
        pptImgCss.setHeight(pptShape.getCss().getHeight());
        if (proportion <= 1.25) {
            pptImg.setFile(SYS_PATH + "logo\\1-1.png");
        } else if (proportion > 1.25 && proportion <= 1.75) {
            pptImg.setFile(SYS_PATH + "logo\\2-3.png");
        } else if (proportion > 1.75 && proportion <= 2.25) {
            pptImg.setFile(SYS_PATH + "logo\\1-2.png");
        } else if (proportion > 2.25 && proportion <= 2.75) {
            pptImg.setFile(SYS_PATH + "logo\\2-5.png");
        } else if (proportion > 2.75 && proportion <= 3.25) {
            pptImg.setFile(SYS_PATH + "logo\\1-3.png");
        } else if (proportion > 3.25 && proportion <= 3.75) {
            pptImg.setFile(SYS_PATH + "logo\\2-7.png");
        } else if (proportion > 3.75 && proportion <= 4.25) {
            pptImg.setFile(SYS_PATH + "logo\\1-4.png");
        } else if (proportion > 4.25 && proportion <= 4.75) {
            pptImg.setFile(SYS_PATH + "logo\\2-9.png");
        } else if (proportion > 4.75 && proportion <= 5.25) {
            pptImg.setFile(SYS_PATH + "logo\\1-5.png");
        } else {
            pptImg.setFile(SYS_PATH + "logo\\2-11.png");
        }
        return pptImg;
    }

}
