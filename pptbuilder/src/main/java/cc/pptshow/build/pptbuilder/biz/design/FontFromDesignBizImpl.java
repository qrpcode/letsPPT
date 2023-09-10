package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.FromHelper;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 添加字体信息
 * 不会修改PPT元素
 */
@Slf4j
@Service
@Design(order = 20, needIteration = false)
public class FontFromDesignBizImpl implements DesignBiz {

    @Resource
    private FromHelper fromHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        fromHelper.addFrom(request.getGlobalStyle(), request.getGlobalStyle().getTextFontInfo());
        fromHelper.addFrom(request.getGlobalStyle(), request.getGlobalStyle().getTitleFontInfo());
        return DesignResponse.buildByRequest(request);
    }
}
