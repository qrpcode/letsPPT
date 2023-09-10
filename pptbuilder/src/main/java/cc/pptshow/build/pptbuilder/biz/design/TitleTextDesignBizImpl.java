package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.ppt.element.impl.PPTText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 首页、大标题页判定是不是应该居中显示
 */
@Slf4j
@Service
@Design(type = PPTBlockType.TEXT, order = 4, onlyInPage = PPTPage.INNER_TITLE)
public class TitleTextDesignBizImpl implements DesignBiz {

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText text = (PPTText) request.getPptElement();
        //需要重写
        return DesignResponse.buildByRequest(request);
    }
}
