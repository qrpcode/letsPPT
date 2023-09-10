package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.LanguageType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.util.TextUtil;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cc.pptshow.build.pptbuilder.constant.BConstant.CONTENT;

/**
 * 根据当前语言环境预设不同的乱数假文
 * 因为我们后续有各种长度策略，如果同时存在中英文会导致不准确
 */
@Slf4j
@Service
@Design(order = -1,
        excludePage = {PPTPage.HOME, PPTPage.BIG_TITLE, PPTPage.CONTENTS},
        type = {PPTBlockType.TEXT})
public class LanguageDesignBizImpl implements DesignBiz {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        String allText = pptText.findAllText();
        if (TextUtil.isAllNumber(allText) || allText.startsWith(CONTENT)) {
            if (allText.length() == 4) {
                textDesignHelper.setText(pptText, BConstant.YEAR_TEXT);
            }
            return DesignResponse.buildByRequest(request);
        }
        if (request.getGlobalStyle().getLanguageType().equals(LanguageType.CHINESE)) {
            if (!TextUtil.isContainChinese(allText)) {
                String lorem = BConstant.LOREM_IPSUM_CN.substring(0, allText.length() / 2);
                textDesignHelper.setText(pptText, lorem);
            }
        } else {
            if (TextUtil.isContainChinese(allText)) {
                String lorem = BConstant.LOREM_IPSUM_EN.substring(0, allText.length() * 2);
                textDesignHelper.setText(pptText, lorem);
            }
        }
        return DesignResponse.buildByRequest(request);
    }

}
