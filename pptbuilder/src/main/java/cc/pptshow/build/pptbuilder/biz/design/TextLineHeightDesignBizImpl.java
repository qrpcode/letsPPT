package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.ppt.element.impl.PPTInnerLine;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 修改行距
 */
@Slf4j
@Service
@Design(type = PPTBlockType.TEXT, order = 3, excludePage = {PPTPage.HOME, PPTPage.BIG_TITLE, PPTPage.INNER_TITLE})
public class TextLineHeightDesignBizImpl implements DesignBiz {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        adjustmentTextLineHeight(pptText);
        return new DesignResponse(pptText);
    }

    /**
     * 多行文本只要能放下都给到1.5倍行距
     */
    private void adjustmentTextLineHeight(PPTText pptText) {
        if (!isMaybeOneLine(pptText)) {
            for (PPTInnerLine pptInnerLine : pptText.getLineList()) {
                if (textDesignHelper.getFontSize(pptText) <= 36) {
                    pptInnerLine.getCss().setLineHeight(Math.max(pptInnerLine.getCss().getLineHeight(), 1.5));
                }
            }
        } else {
            double lineHeight = textDesignHelper.getLineHeight(pptText);
            if ((lineHeight < 1.0 || lineHeight >= 1.5)
                    || (pptText.findMinHeightSize() > pptText.getCss().getHeight())) {
                    pptText.getLineList().forEach(line -> line.getCss().setLineHeight(1.0));
            }
        }
    }

    /**
     * 可能是一个单行文本
     *    原始文本不到两行且行距<1.5
     *    原始文本是单行文本
     */
    private boolean isMaybeOneLine(PPTText pptText) {
        if (pptText.findAllText().length() <= 3) {
            return true;
        }
        double lineHeight = textDesignHelper.getLineHeight(pptText);
        if (lineHeight <= 1.5 && pptText.findMinWidthSize() / pptText.getCss().getWidth() < 2) {
            return true;
        }
        return textDesignHelper.isOnlyOneLine(pptText);
    }

}
