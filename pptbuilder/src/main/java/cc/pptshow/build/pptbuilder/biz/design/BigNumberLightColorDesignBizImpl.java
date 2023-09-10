package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.bean.ColorInfo;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.biz.helper.ColorHelper;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.util.TextUtil;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cc.pptshow.build.pptbuilder.constant.BConstant.DEFAULT_FONT_COLOR;

/**
 * 如果一个文字特别的大，那么就给它颜色设置的浅一些
 */
@Slf4j
@Service
@Design(order = 22,
        type = PPTBlockType.TEXT,
        onlyInPage = {PPTPage.HOME, PPTPage.BIG_TITLE})
public class BigNumberLightColorDesignBizImpl implements DesignBiz {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Resource
    private ColorHelper colorHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        String allText = pptText.findAllText();
        ColorInfo colorInfo = request.getGlobalStyle().getColorInfo();
        String lightFromColor = colorHelper.getLightFromColor(colorInfo);
        if (!TextUtil.isContainChinese(allText) && textDesignHelper.getFontSize(pptText) > 70) {
            String fontColor = textDesignHelper.getFontColor(pptText);
            if (fontColor.equals(DEFAULT_FONT_COLOR) || fontColor.equals(colorInfo.getFromColor())) {
                textDesignHelper.setAllTextColor(pptText, lightFromColor);
            } else {
                textDesignHelper.setAllTextColor(pptText, colorHelper.getLightColor(colorInfo.getFromColor()));
            }
        }
        return DesignResponse.buildByRequest(request);
    }

}
