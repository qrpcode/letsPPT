package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBackgroundType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.service.EnumPPTBackgroundService;
import cc.pptshow.build.pptbuilder.service.ImgInfoService;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.ppt.domain.Gradient;
import cc.pptshow.ppt.domain.PPTImgCss;
import cc.pptshow.ppt.domain.PPTShapeCss;
import cc.pptshow.ppt.domain.PPTSideCss;
import cc.pptshow.ppt.domain.background.Background;
import cc.pptshow.ppt.domain.background.GradientBackground;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.show.PPTShowSide;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cc.pptshow.build.pptbuilder.constant.BConstant.*;

/**
 * 查看当前是否需要添加背景
 * 如果需要就得加上背景图案
 */
@Slf4j
@Service
@Design(order = -2, needIteration = false, excludePage = {PPTPage.HOME, PPTPage.BIG_TITLE})
public class PageBackgroundDesignBiz implements DesignBiz{

    @Override
    public DesignResponse design(DesignRequest request) {
        GlobalStyle globalStyle = request.getGlobalStyle();
        String backgroundColor = globalStyle.getColorInfo().getBackgroundColor();
        if (!StringUtils.equals(backgroundColor, WHITE)) {
            request.getPptShowSide().setCss(PPTSideCss.build().setBackground(getOnlyColorBackground(globalStyle)));
        }
        return new DesignResponse(request.getPptElements());
    }

    private Background getOnlyColorBackground(GlobalStyle globalStyle) {
        return new GradientBackground()
                .setGradientDirection(globalStyle.getGlobalLight())
                .setGradient(Lists.newArrayList(
                        Gradient.build().setAlpha(RandUtil.round(10, 30) * 1.0).setProportion(0).setColor(globalStyle.getColorInfo().getBackgroundColor()),
                        Gradient.build().setProportion(100).setColor(globalStyle.getColorInfo().getBackgroundColor())
                ));
    }

}
