package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 首页上下 1/4 以内的区域最大字号不应该超过20
 */
@Slf4j
@Service
//@Design(order = 5, type = PPTBlockType.TEXT, onlyInPage = {PPTPage.HOME, PPTPage.BIG_TITLE})
public class MainPageMaxFontSize /*implements DesignBiz*/ {

    @Resource
    private TextDesignHelper textDesignHelper;

    //@Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        if (isNotInMiddle(pptText)) {
            int fontSize = textDesignHelper.getFontSize(pptText);
            if (fontSize > 20) {
                textDesignHelper.setFontSize(pptText, 20);
            }
        }
        return DesignResponse.buildByRequest(request);
    }

    private boolean isNotInMiddle(PPTText pptText) {
        return pptText.getCss().getTop() + pptText.getCss().getHeight() < BConstant.PAGE_HEIGHT * 0.25
                || pptText.getCss().getTop() > BConstant.PAGE_HEIGHT * 0.75;
    }

}
