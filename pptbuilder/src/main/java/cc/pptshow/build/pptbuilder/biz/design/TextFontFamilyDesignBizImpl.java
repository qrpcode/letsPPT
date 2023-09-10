package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.biz.helper.LayerHelper;
import cc.pptshow.build.pptbuilder.domain.*;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.util.ColorUtil;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static cc.pptshow.build.pptbuilder.constant.BConstant.DEFAULT_FONT_COLOR;
import static cc.pptshow.build.pptbuilder.constant.BConstant.WHITE;

/**
 * 修改文字默认字体
 * 会修改为免费商用的小米字体（思源黑体改的）
 */
@Slf4j
@Service
@Design(type = PPTBlockType.TEXT, order = 2)
public class TextFontFamilyDesignBizImpl implements DesignBiz {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = buildTopTextElement(request);
        return new DesignResponse(pptText);
    }

    public PPTText buildTopTextElement(DesignRequest request) {
        PPTText pptElement = (PPTText) request.getPptElement();
        textDesignHelper.setNormalFontFamily(pptElement, request.getGlobalStyle().getTextFontInfo());
        return pptElement;
    }

}
