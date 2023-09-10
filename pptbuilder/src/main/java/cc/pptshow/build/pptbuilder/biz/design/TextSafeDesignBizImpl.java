package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.ppt.domain.PPTTextCss;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 文本安全线设置
 * 不允许文本框超过页面之外，目前只检查左右两个边
 * 页面内至少距离边距0.5cm
 */
@Slf4j
@Service
@Design(type = PPTBlockType.TEXT, order = 0)
public class TextSafeDesignBizImpl implements DesignBiz {

    private static final double MIN_SIZE = 0.5;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        PPTTextCss css = pptText.getCss();
        if (css.getLeft() < MIN_SIZE) {
            css.setWidth(css.getWidth() + css.getLeft() - MIN_SIZE);
            css.setLeft(MIN_SIZE);
        }
        if (css.getLeft() + css.getWidth() > BConstant.PAGE_WIDTH - MIN_SIZE) {
            css.setWidth(BConstant.PAGE_WIDTH - MIN_SIZE - css.getLeft());
        }
        if (css.getTop() + css.getHeight() > BConstant.PAGE_HEIGHT - MIN_SIZE
                && css.getTop() < BConstant.PAGE_HEIGHT - MIN_SIZE) {
            css.setHeight(BConstant.PAGE_HEIGHT - MIN_SIZE - css.getTop());
        }
        return DesignResponse.buildByRequest(request);
    }
}
