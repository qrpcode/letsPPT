package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.bean.PPTPageModel;
import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.context.PPTBlockContext;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.service.PPTPageModelService;
import cc.pptshow.build.pptbuilder.service.PPTRegionService;
import cc.pptshow.build.pptbuilder.util.TextUtil;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * 修改PPT的文字大小
 */
@Slf4j
@Service
@Design(type = PPTBlockType.TEXT, order = 4, excludePage = {PPTPage.HOME, PPTPage.BIG_TITLE})
public class TextSizeDesignBizImpl implements DesignBiz {
    @Resource
    private TextDesignHelper textDesignHelper;

    @Resource
    private PPTRegionService pptRegionService;

    @Resource
    private PPTPageModelService pptPageModelService;

    @Override
    public DesignResponse design(DesignRequest request) {
        PPTText pptText = (PPTText) request.getPptElement();
        //根据通用样式设置文段通用字体大小，如果导致文本溢出也没关系后续有移除操作
        adjustmentTextSizeToGlobalSize(pptText, request.getGlobalStyle());
        //如果是文本段落，执行最小指定字号规则
        adjustmentTextSizeIfNormalText(pptText, request.getGlobalStyle());
        return new DesignResponse(pptText);
    }

    private void adjustmentTextSizeIfNormalText(PPTText pptText, GlobalStyle globalStyle) {
        PPTBlockPut pptBlockPut = PPTBlockContext.findUsePPTBlockPut(pptText);
        if (Objects.isNull(pptBlockPut)) {
            return;
        }
        PPTRegion pptRegion = pptRegionService.queryById(pptBlockPut.getPptRegionId());
        Assert.notNull(pptRegion, "pptRegion不可以是null");
        Integer pptModelId = pptRegion.getPptModelId();
        PPTPageModel pageModel = pptPageModelService.findById(pptModelId);
        Assert.notNull(pageModel, "pptPageModel不可以是null");
        if (pageModel.getNormalText() == 1) {
            if (textDesignHelper.getFontSize(pptText) < globalStyle.getNormalFontSize()) {
                textDesignHelper.setFontSize(pptText, globalStyle.getNormalFontSize());
            }
        }
    }

    private void adjustmentTextSizeToGlobalSize(PPTText pptText, GlobalStyle globalStyle) {
        int fontSize = textDesignHelper.getFontSize(pptText);
        if (fontSize >= 11 && fontSize <= 16) {
            textDesignHelper.setFontSize(pptText, globalStyle.getNormalFontSize());
        }
        int lineCount = textDesignHelper.getLineCount(pptText);
        if ((lineCount >= 2 && fontSize >= 17 && fontSize <= 19) || (lineCount >= 3 && fontSize >= 20 && fontSize <= 21)) {
            textDesignHelper.setFontSize(pptText, globalStyle.getNormalFontSize());
        }
        if (!TextUtil.isContainChinese(pptText.findAllText()) && fontSize >= 9 && fontSize <= 10) {
            textDesignHelper.setFontSize(pptText, globalStyle.getNormalFontSize());
        }
    }

}
