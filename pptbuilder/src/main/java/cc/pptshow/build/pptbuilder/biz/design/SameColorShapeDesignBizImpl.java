package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.BackgroundHelper;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.biz.helper.ColorHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.Canvas;
import cc.pptshow.build.pptbuilder.domain.CanvasShape;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.ppt.domain.background.Background;
import cc.pptshow.ppt.domain.background.ColorBackGround;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 如果一个图形是文字下方的装饰图形，它的颜色应该比较浅
 */
@Slf4j
@Service
@Design(order = 23, type = PPTBlockType.SHAPE)
public class SameColorShapeDesignBizImpl implements DesignBiz {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Resource
    private BackgroundHelper backgroundHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTShape pptShape = (PPTShape) request.getPptElement();
        Background background = pptShape.getCss().getBackground();
        if (!(background instanceof ColorBackGround)
                || backgroundHelper.findBackgroundElement(request.getPptElements()).contains(pptShape)) {
            return DesignResponse.buildByRequest(request);
        }
        String color = ((ColorBackGround) background).getColor();
        CanvasShape shape = Canvas.buildCanvasByShape(pptShape);
        double sum = request.getPptElements().stream()
                .filter(element -> element instanceof PPTText)
                .map(element -> (PPTText) element)
                .filter(text -> textDesignHelper.getFontColor(text).equals(color)
                        || textDesignHelper.getFontColor(text).equals(BConstant.DEFAULT_FONT_COLOR))
                .map(text -> {
                    CanvasShape textCanvas = Canvas.buildCanvasByPPTTextOnlyTextPlace(text);
                    return textCanvas.intersectArea(shape);
                })
                .reduce(Double::sum)
                .orElse(0.00);
        if (sum > shape.area() * 0.5) {
            ColorBackGround backGround = (ColorBackGround) background;
            backGround.setAlpha(80.0);
        }
        return DesignResponse.buildByRequest(request);
    }
}
