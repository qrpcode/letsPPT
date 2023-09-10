package cc.pptshow.build.pptbuilder.biz.builder.element;

import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockImg;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockImgType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.qo.BuilderQo;
import cc.pptshow.build.pptbuilder.service.ImgInfoService;
import cc.pptshow.build.pptbuilder.service.PPTBlockImgService;
import cc.pptshow.ppt.domain.PPTImgCss;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static cc.pptshow.build.pptbuilder.constant.BConstant.SYS_PATH;

@Service
public class ImgElementBuild implements ElementBuilder {

    @Resource
    private PPTBlockImgService pptBlockImgService;

    @Resource
    private ImgInfoService imgInfoService;

    @Override
    public List<PPTBlockType> canBuildTypes() {
        return Lists.newArrayList(PPTBlockType.IMG);
    }

    @Override
    public List<PPTElement> buildElement(BuilderQo builderQo) {
        PPTBlock pptBlock = builderQo.getPptBlock();
        PPTBlockImg blockImg = pptBlockImgService.queryByBlock(pptBlock);
        PPTImg pptImg = new PPTImg();
        if (blockImg.getTheme().equals(PPTBlockImgType.LOGO.getCode())) {
            pptImg.setFile(SYS_PATH + "logo.png");
            pptImg.setCss(PPTImgCss.build()
                    .setWidth(pptBlock.getWidthSize())
                    .setHeight(pptBlock.getHeightSize())
                    .setLeft(pptBlock.getLeftSize() + builderQo.getLeftSize())
                    .setTop(pptBlock.getTopSize() + builderQo.getTopSize())
            );
        } else {
            pptImg.setFile(imgInfoService.randInnerImg());
            pptImg.setCss(PPTImgCss.build()
                    .setWidth(pptBlock.getWidthSize())
                    .setHeight(pptBlock.getHeightSize())
                    .setLeft(pptBlock.getLeftSize() + builderQo.getLeftSize())
                    .setTop(pptBlock.getTopSize() + builderQo.getTopSize())
            );
        }
        return Lists.newArrayList(pptImg);
    }
}
