package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.util.TextUtil;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
@Design(type = PPTBlockType.TEXT, order = 3, excludePage = PPTPage.HOME)
public class TextSizeAdjustmentDesignBizImpl implements DesignBiz {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        //先根据实际情况进行调整
        adjustmentTextSize(pptText, request.getGlobalStyle().getNormalFontSize());
        return new DesignResponse(pptText);
    }

    private void adjustmentTextSize(PPTText pptText, int normalFontSize) {
        int minFontSize = findMinFontSize(pptText);
        if (textDesignHelper.getFontSize(pptText) < minFontSize) {
            textDesignHelper.setFontSize(pptText, minFontSize);
        }
        double needHeight = getMinHeightSize(pptText);
        //如果文字太大
        if (getMinHeightSize(pptText) > pptText.getCss().getHeight()) {
            while (getMinHeightSize(pptText) > pptText.getCss().getHeight()) {
                int fontSize = textDesignHelper.getFontSize(pptText);
                if (fontSize == minFontSize) {
                    break;
                }
                textDesignHelper.setFontSize(pptText, --fontSize);
            }
        }
        //如果文字太小
        if (pptText.getCss().getHeight() > needHeight
                && textDesignHelper.getFontSize(pptText) < normalFontSize) {
            while (pptText.getCss().getHeight() > getMinHeightSize(pptText)
                    && textDesignHelper.getFontSize(pptText) < normalFontSize) {
                int fontSize = textDesignHelper.getFontSize(pptText);
                textDesignHelper.setFontSize(pptText, ++fontSize);
            }
        }
    }


    private double getMinHeightSize(PPTText pptText) {
        if (textDesignHelper.isOnlyOneLine(pptText)) {
            return pptText.findMinHeightSize() / textDesignHelper.getLineHeight(pptText);
        }
        return pptText.findMinHeightSize();
    }

    /**
     * 获取文本的最小行距
     */
    private int findMinFontSize(PPTText pptText) {
        if (TextUtil.isContainChinese(pptText.findAllText())) {
            return 12;
        }
        return 9;
    }

}
