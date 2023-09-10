package cc.pptshow.build.pptbuilder.biz.builder.inference;

import cc.pptshow.build.pptbuilder.anno.Inference;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTText;
import cc.pptshow.ppt.show.PPTShow;
import cc.pptshow.ppt.show.PPTShowSide;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Inference(page = PPTPage.THANK)
public class ThankPageBuilder implements InferenceBuilder {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Override
    public PPTShowSide buildPageByStyle(GlobalStyle globalStyle, PPTShow ppt) {
        PPTShowSide pptShowSide = findHomePage(ppt);
        return buildThankPageByHome(pptShowSide);
    }

    private PPTShowSide buildThankPageByHome(PPTShowSide pptShowSide) {
        List<PPTElement> elements = pptShowSide.getElements();
        List<PPTElement> copyElements = elements.stream().map(e -> e.clone()).collect(Collectors.toList());
        PPTText maxSize = textDesignHelper.findMaybeTitle(copyElements);
        if (Objects.isNull(maxSize)) {
            log.error("[感谢页面生成] 没找到最大的标题文本，可能有问题，elements：{}", elements);
        }
        textDesignHelper.setText(maxSize, findThankTextByLength(maxSize.findAllText().length()));
        PPTShowSide showSide = PPTShowSide.build();
        showSide.addAll(copyElements);
        showSide.setCss(pptShowSide.getCss());
        return showSide;
    }

    private PPTShowSide findHomePage(PPTShow ppt) {
        List<PPTShowSide> sides = ppt.getSides();
        if (CollectionUtils.isEmpty(sides)) {
            throw new RuntimeException("感谢页面需要根据首页生成，但是没有首页");
        }
        return sides.get(0);
    }

    private String findThankTextByLength(int length) {
        if (length <= 2) {
            return "感谢";
        } else if (length <= 4) {
            return "感谢观看";
        } else if (length == 5) {
            return "感谢您观看";
        } else if (length == 6) {
            return "感谢您的观看";
        } else if (length == 7) {
            return "感谢您倾听汇报";
        } else if (length == 8) {
            return "感谢倾听欢迎指正";
        } else if (length == 9) {
            return "感谢您倾听欢迎指正";
        } else if (length == 10) {
            return "感谢您的倾听欢迎指正";
        } else {
            return "感谢您的倾听，欢迎指正";
        }
    }

}
