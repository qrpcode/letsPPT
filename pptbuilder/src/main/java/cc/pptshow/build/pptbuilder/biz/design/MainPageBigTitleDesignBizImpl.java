package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.util.ColorUtil;
import cc.pptshow.build.pptbuilder.util.TextUtil;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.constant.BConstant.DEFAULT_FONT_COLOR;

/**
 * 纠正大标题的位置
 * 有的时候大标题位置可能偏下，进行纠正
 */
@Slf4j
@Service
@Design(order = 14, needIteration = false, onlyInPage = {PPTPage.HOME, PPTPage.BIG_TITLE})
public class MainPageBigTitleDesignBizImpl implements DesignBiz {

    @Resource
    private TextDesignHelper textDesignHelper;

    private static final int MAX_NUM_SIZE = 40;

    @Override
    public DesignResponse design(DesignRequest request) {
        List<PPTText> pptTexts = findBigTitle(request.getPptElements());
        if (CollectionUtils.isEmpty(pptTexts)) {
            return DesignResponse.buildByRequest(request);
        }
        for (PPTText pptText : pptTexts) {
            String allText = pptText.findAllText();
            if (TextUtil.isContainChinese(allText)
                    || (TextUtil.isAllNumber(allText)
                    && textDesignHelper.getFontSize(pptText) < MAX_NUM_SIZE)) {
                textDesignHelper.setFontFamily(pptText, request.getGlobalStyle().getTitleFontInfo().getFontCode());
            }
        }
        return DesignResponse.buildByRequest(request);
    }

    private List<PPTText> findBigTitle(List<PPTElement> pptElements) {
        List<PPTText> pptTexts = pptElements.stream()
                .filter(pptElement -> pptElement instanceof PPTText)
                .map(element -> (PPTText) element)
                .sorted(Comparator.comparingInt(text -> textDesignHelper.getFontSize(text)))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(pptTexts)) {
            return Lists.newArrayList();
        }
        List<PPTText> matchTexts = pptTexts.subList(pptTexts.size() / 2, pptTexts.size());
        List<Integer> fontSizes = matchTexts.stream()
                .map(text -> textDesignHelper.getFontSize(text))
                .collect(Collectors.toList());
        List<PPTText> texts = pptTexts.stream().filter(text -> !matchTexts.contains(text)
                && fontSizes.contains(textDesignHelper.getFontSize(text)))
                .collect(Collectors.toList());
        matchTexts.addAll(texts);
        return matchTexts;
    }
}
