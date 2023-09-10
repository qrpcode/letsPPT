package cc.pptshow.build.pptbuilder.biz.helper;

import cc.pptshow.build.pptbuilder.biz.builder.impl.PageBuildBizImpl;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.build.pptbuilder.util.Safes;
import cc.pptshow.ppt.domain.animation.AnimationAttribute;
import cc.pptshow.ppt.domain.animation.InAnimation;
import cc.pptshow.ppt.domain.animation.InAnimationType;
import cc.pptshow.ppt.show.PPTShow;
import cc.pptshow.ppt.show.PPTShowSide;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cc.pptshow.build.pptbuilder.constant.BConstant.TITLE_PAGE_ID;

@Service
public class GlobalStyleHelper {

    @Resource
    private PageBuildBizImpl pageBuildBiz;

    @Resource
    private LayerHelper layerHelper;

    public void buildTitle(GlobalStyle globalStyle) {
        //初始化设置一个标题
        PPTShowSide pptShowSide = pageBuildBiz.buildPageByStyle(TITLE_PAGE_ID, globalStyle, new PPTShow(), 0, 1000);
        InAnimationType animationType = RandUtil.round(BConstant.TitleInAnimationTypes);
        InAnimation inAnimation = new InAnimation();
        inAnimation.setTimeMs(500);
        inAnimation.setAnimationAttribute(AnimationAttribute.LEFT);
        inAnimation.setInAnimationType(animationType);
        Safes.of(pptShowSide.getElements()).forEach(p -> {
            p.setRemark(BConstant.TITLE_REMARK);
            p.setInAnimation(inAnimation);
        });
        globalStyle.setTitleElements(pptShowSide.getElements());
        globalStyle.setTitleShape(layerHelper.findElementsToShape(globalStyle.getTitleElements()));
    }

}
