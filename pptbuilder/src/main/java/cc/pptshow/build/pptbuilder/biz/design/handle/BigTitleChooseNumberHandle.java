package cc.pptshow.build.pptbuilder.biz.design.handle;

import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.ppt.domain.PPTSideCss;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTInnerLine;
import cc.pptshow.ppt.element.impl.PPTInnerText;
import cc.pptshow.ppt.element.impl.PPTText;
import cc.pptshow.ppt.show.PPTShowSide;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BigTitleChooseNumberHandle {

    public PPTShowSide replaceNumber(PPTShowSide bigTitlePage, int bigNumberCount) {
        PPTShowSide newSide = new PPTShowSide();
        PPTSideCss newSideCss = new PPTSideCss();
        newSideCss.setBackground(bigTitlePage.getCss().getBackground());
        newSide.setCss(newSideCss);
        newSide.setPageSwitchingType(bigTitlePage.getPageSwitchingType());
        newSide.setAutoPagerTime(bigTitlePage.getAutoPagerTime());
        List<PPTElement> elements = bigTitlePage.getElements();
        //这里都是深克隆
        List<PPTElement> pptElements = elements.stream()
                .map(PPTElement::clone)
                .peek(element -> {
                    if (element instanceof PPTText) {
                        PPTText pptText = (PPTText) element;
                        for (PPTInnerLine pptInnerLine : pptText.getLineList()) {
                            for (PPTInnerText innerText : pptInnerLine.getTextList()) {
                                String text = innerText.getText();
                                text = text.replace(BConstant.CN_BIG_NUMBER.get(0), BConstant.CN_BIG_NUMBER.get(bigNumberCount - 1));
                                text = text.replace(BConstant.CN_SMALL_NUMBER.get(0), BConstant.CN_SMALL_NUMBER.get(bigNumberCount - 1));
                                text = text.replace(BConstant.EN_NUMBER.get(0), BConstant.EN_NUMBER.get(bigNumberCount - 1));
                                text = text.replace("1", Integer.toString(bigNumberCount));
                                innerText.setText(text);
                            }
                        }
                    }
                })
                .collect(Collectors.toList());
        newSide.addAll(pptElements);
        return newSide;
    }

}
