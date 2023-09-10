package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.util.ColorUtil;
import cc.pptshow.ppt.domain.Gradient;
import cc.pptshow.ppt.domain.background.Background;
import cc.pptshow.ppt.domain.background.ColorBackGround;
import cc.pptshow.ppt.domain.background.GradientBackground;
import cc.pptshow.ppt.element.impl.PPTShape;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 兜底，如果颜色没有被替换，就替换成fromColor
 */
@Slf4j
@Service
@Design(order = 12, type = {PPTBlockType.SHAPE})
public class ColorFilterDesignBizImpl implements DesignBiz {

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTShape pptShape = (PPTShape) request.getPptElement();
        Background background = pptShape.getCss().getBackground();
        String fromColor = request.getGlobalStyle().getColorInfo().getFromColor();
        if (background instanceof ColorBackGround) {
            ColorBackGround colorBackGround = (ColorBackGround) background;
            if (!ColorUtil.isHexColor(colorBackGround.getColor())) {
                colorBackGround.setColor(fromColor);
            }
        } else if (background instanceof GradientBackground) {
            GradientBackground gradientBackground = (GradientBackground) background;
            for (Gradient gradient : gradientBackground.getGradient()) {
                if (!ColorUtil.isHexColor(gradient.getColor())) {
                    gradient.setColor(fromColor);
                }
            }
        }
        return DesignResponse.buildByRequest(request);
    }

}
