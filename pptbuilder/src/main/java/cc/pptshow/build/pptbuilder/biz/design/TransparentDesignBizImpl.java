package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.ppt.domain.background.Background;
import cc.pptshow.ppt.domain.background.ColorBackGround;
import cc.pptshow.ppt.element.impl.PPTShape;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 针对完全透明的元素改成半透明（50%）
 */
@Slf4j
@Service
@Design(type = PPTBlockType.SHAPE, order = 22)
public class TransparentDesignBizImpl implements DesignBiz {
    @Override
    public DesignResponse design(DesignRequest request) {
        PPTShape pptShape = (PPTShape) request.getPptElement();
        Background background = pptShape.getCss().getBackground();
        if (background instanceof ColorBackGround) {
            ColorBackGround colorBackGround = (ColorBackGround) background;
            if (Objects.nonNull(colorBackGround.getAlpha()) && colorBackGround.getAlpha() == 100) {
                colorBackGround.setAlpha(0.0);
            }
        }
        return DesignResponse.buildByRequest(request);
    }
}
