package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.ppt.constant.Constant;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 修改PPT的文字长度
 * 如果已经超过文本框了就给截断
 */
@Slf4j
@Service
@Design(type = PPTBlockType.TEXT, order = 15, excludePage = {PPTPage.HOME, PPTPage.BIG_TITLE})
public class TextLengthDesignBizImpl implements DesignBiz {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        if (textDesignHelper.isOnlyOneLine(pptText) || textDesignHelper.isMustOneLine(pptText)) {
            return DesignResponse.buildByRequest(request);
        }
        double realHeight = pptText.findMinHeightSize();
        double boxHeight = pptText.getCss().getHeight();
        while (realHeight > boxHeight) {
            if (textDesignHelper.isOnlyOneLine(pptText)) {
                break;
            }
            if (textDesignHelper.isOnlyOneLine(pptText) && findOnlyLineHeightSize(pptText) <= boxHeight) {
                textDesignHelper.setLineHeight(pptText, 1.0);
                break;
            }
            String allText = pptText.findAllText();
            allText = allText.substring(0, allText.length() - 1);
            textDesignHelper.setText(pptText, allText);
            realHeight = pptText.findMinHeightSize();
        }
        return new DesignResponse(pptText);
    }

    private double findOnlyLineHeightSize(PPTText pptText) {
        double lineHeight = pptText.getLineList().get(0).getCss().getLineHeight();
        return (pptText.findMinHeightSize() - Constant.PPT_TEXT_TOP_BOTTOM_SLIDES)
                / lineHeight + Constant.PPT_TEXT_TOP_BOTTOM_SLIDES;
    }

}
