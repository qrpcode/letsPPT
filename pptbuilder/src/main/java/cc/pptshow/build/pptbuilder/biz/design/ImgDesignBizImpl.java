package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.bean.ImgInfo;
import cc.pptshow.build.pptbuilder.biz.design.help.ImgHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.vo.NlpItemVo;
import cc.pptshow.build.pptbuilder.domain.vo.NlpVo;
import cc.pptshow.build.pptbuilder.service.ImgInfoService;
import cc.pptshow.ppt.domain.background.ImgBackground;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTShape;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Design(type = {PPTBlockType.IMG, PPTBlockType.SHAPE}, order = 2)
public class ImgDesignBizImpl implements DesignBiz {

    @Resource
    private ImgHelper imgHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTElement pptElement = request.getPptElement();
        String randInnerImg = imgHelper.findRandImg(request.getGlobalStyle());
        if (pptElement instanceof PPTImg) {
            PPTImg img = (PPTImg) request.getPptElement();
            if (StringUtils.equals(img.getCss().getName(), BConstant.ICON)) {
                return new DesignResponse(img);
            }
            String cut = imgHelper.cutImgByWidthAndHeight(randInnerImg, img.getCss().getWidth(), img.getCss().getHeight());
            return new DesignResponse(img.setFile(cut));
        } else {
            PPTShape shape = (PPTShape) request.getPptElement();
            if (shape.getCss().getBackground() instanceof ImgBackground) {
                String cut = imgHelper.cutImgByWidthAndHeight(randInnerImg, shape.getCss().getWidth(), shape.getCss().getHeight());
                ((ImgBackground) shape.getCss().getBackground()).setImg(cut);
            }
            return new DesignResponse(shape);
        }
    }

}
