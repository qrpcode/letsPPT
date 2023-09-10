package cc.pptshow.build.pptbuilder.biz.builder.element;

import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockText;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockTextType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.qo.BuilderQo;
import cc.pptshow.build.pptbuilder.service.PPTBlockTextService;
import cc.pptshow.ppt.constant.PPTNameConstant;
import cc.pptshow.ppt.domain.PPTInnerLineCss;
import cc.pptshow.ppt.domain.PPTInnerTextCss;
import cc.pptshow.ppt.domain.PPTTextCss;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTInnerLine;
import cc.pptshow.ppt.element.impl.PPTInnerText;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static cc.pptshow.build.pptbuilder.constant.BConstant.MORE_WIDTH_TEXT;

@Slf4j
@Service
public class TextElementBuilder implements ElementBuilder {

    @Resource
    private PPTBlockTextService pptBlockTextService;

    @Override
    public List<PPTBlockType> canBuildTypes() {
        return Lists.newArrayList(PPTBlockType.TEXT);
    }

    @Override
    public List<PPTElement> buildElement(BuilderQo builderQo) {
        PPTBlock pptBlock = builderQo.getPptBlock();
        PPTBlockText blockText = pptBlockTextService.queryByBlockId(pptBlock.getId());
        //TODO: 根据随机标识生成随机文本
        PPTInnerText pptInnerText = PPTInnerText.build(blockText.getText());
        pptInnerText.setCss(PPTInnerTextCss.build().setFontSize(blockText.getFontSize()));
        PPTInnerLine pptInnerLine = PPTInnerLine.build(pptInnerText);
        pptInnerLine.setCss(PPTInnerLineCss.build()
                .setAlign(blockText.getAlign())
                .setLineHeight(blockText.getLineHeight()));
        double realWidth = pptBlock.getWidthSize();
        double moreWidth = (StringUtils.equals(blockText.getTheme(), PPTBlockTextType.BIG_TITLE.getCode()) ?
                MORE_WIDTH_TEXT : 1.0) * realWidth;
        double realLeft = pptBlock.getLeftSize() + builderQo.getLeftSize();
        if (blockText.getAlign().equals(PPTNameConstant.ALIGN_RIGHT)) {
            realLeft = realLeft - (moreWidth - realWidth);
        } else if (blockText.getAlign().equals(PPTNameConstant.ALIGN_CENTER)) {
            realLeft = realLeft - ((moreWidth - realWidth) / 2);
        }
        PPTText pptText = PPTText.build(pptInnerLine);
        pptText.setCss(PPTTextCss.build()
                .setWidth(realWidth)
                .setHeight(pptBlock.getHeightSize())
                .setLeft(realLeft)
                .setTop(pptBlock.getTopSize() + builderQo.getTopSize()));
        return Lists.newArrayList(pptText);
    }

}
