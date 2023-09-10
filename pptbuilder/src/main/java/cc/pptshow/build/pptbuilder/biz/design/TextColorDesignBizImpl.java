package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.BackgroundHelper;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.biz.helper.ColorHelper;
import cc.pptshow.build.pptbuilder.domain.*;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.util.ColorUtil;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

import static cc.pptshow.build.pptbuilder.constant.BConstant.DEFAULT_FONT_COLOR;
import static cc.pptshow.build.pptbuilder.constant.BConstant.WHITE;

/**
 * 根据下方色块颜色修正文字颜色
 */
@Slf4j
@Service
@Design(type = PPTBlockType.TEXT, order = 13)
public class TextColorDesignBizImpl implements DesignBiz {

    @Resource
    private ColorHelper colorHelper;

    @Resource
    private TextDesignHelper textDesignHelper;

    @Resource
    private BackgroundHelper backgroundHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        CanvasShape textShape = Canvas.buildCanvasByPPTText(pptText);
        List<PPTElement> elements = Lists.newArrayList(request.getPptElements());
        elements.remove(pptText);
        elements.removeAll(backgroundHelper.findBackgroundElement(request.getPptElements()));
        PPTElement topmostLayer = colorHelper.findHalfTopmostLayerWithoutText(textShape, elements);
        String differentColor = colorHelper.findDifferentColor(topmostLayer
                , request.getGlobalStyle().getColorInfo(), findDefaultColor(request.getGlobalStyle()));
        textDesignHelper.setAllTextColor((PPTText) request.getPptElement(), differentColor);
        return new DesignResponse(pptText);
    }

    private String findDefaultColor(GlobalStyle globalStyle) {
        if (ColorUtil.isDeepColor(globalStyle.getColorInfo().getBackgroundColor())) {
            return WHITE;
        } else {
            return DEFAULT_FONT_COLOR;
        }
    }

}
