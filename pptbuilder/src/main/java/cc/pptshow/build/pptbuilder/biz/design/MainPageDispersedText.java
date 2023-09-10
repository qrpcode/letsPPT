package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.ppt.constant.PPTNameConstant;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 如果大标题在中间居中了
 * 而且文字的宽度占比不到整个文本框的80%
 */
@Slf4j
@Service
@Design(order = 16, needIteration = false, type = PPTBlockType.TEXT, onlyInPage = {PPTPage.HOME, PPTPage.BIG_TITLE})
public class MainPageDispersedText implements DesignBiz {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = textDesignHelper.findMaybeTitle(request.getPptElements());
        if (Objects.isNull(pptText)) {
            return DesignResponse.buildByRequest(request);
        }
        double minWidthSize = pptText.findMinWidthSize();
        //如果占比不到80%
        if (minWidthSize / pptText.getCss().getWidth() < 0.8 && isInCenter(pptText)) {
            textDesignHelper.setTextAlign(pptText, PPTNameConstant.ALIGN_DIST);
        }
        return DesignResponse.buildByRequest(request);
    }

    public boolean isInCenter(PPTText pptText) {
        return Math.abs(BConstant.PAGE_WIDTH / 2 -
                (pptText.getCss().getLeft() + (pptText.getCss().getWidth() / 2))) < 0.1
                && StringUtils.equals(textDesignHelper.getTextAlign(pptText),  PPTNameConstant.ALIGN_DIST);
    }

}
