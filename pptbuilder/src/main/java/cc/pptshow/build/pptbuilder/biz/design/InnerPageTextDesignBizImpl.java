package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.build.pptbuilder.util.TextUtil;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.xerces.util.XMLSymbols.EMPTY_STRING;

/**
 * 目录页面
 */
@Slf4j
@Service
@Design(order = 21, needIteration = false, excludePage = {PPTPage.HOME})
public class InnerPageTextDesignBizImpl implements DesignBiz {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Override
    public DesignResponse design(DesignRequest request) {
        List<PPTText> allTexts = textDesignHelper.findAllTexts(request.getPptElements());
        List<PPTText> numberTexts = findAllNumberTexts(allTexts);
        allTexts.removeAll(numberTexts);
        List<List<PPTText>> titleTexts = findAllTitleText(numberTexts, allTexts);
        fillNumberText(numberTexts, request.getGlobalStyle(), request.getBigTitleNumber(), request.getPageId());
        allTexts.removeAll(titleTexts.stream().flatMap(Collection::stream).collect(Collectors.toList()));
        fillTitleText(titleTexts);
        for (PPTText text : allTexts) {
            String allText = text.findAllText();
            if (TextUtil.isAllNumber(allText) || BConstant.YEAR_TEXT.equals(allText)) {
                continue;
            }
            if (TextUtil.isContainChinese(allText)) {
                textDesignHelper.buildChineseTextMayBeTitle(text, request.getGlobalStyle());
            } else {
                textDesignHelper.buildEnglishText(text, request.getGlobalStyle());
            }
        }
        return DesignResponse.buildByRequest(request);
    }

    private void fillTitleText(List<List<PPTText>> titleTexts) {
        for (List<PPTText> pptTexts : titleTexts) {
            for (PPTText pptText : pptTexts) {
                String allText = pptText.findAllText();
                List<String> filterTexts = BConstant.HOME_RAND_TEXTS.stream()
                        .filter(t -> t.length() == allText.length()).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(filterTexts)) {
                    textDesignHelper.setText(pptText, RandUtil.randElement(filterTexts));
                    break;
                }
                List<String> titleFilterTexts = BConstant.TITLE_RAND_TEXTS.stream()
                            .filter(t -> t.length() == allText.length()).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(titleFilterTexts)) {
                    textDesignHelper.setText(pptText, RandUtil.randElement(titleFilterTexts));
                }
            }
        }
    }

    private void fillNumberText(List<PPTText> numberTexts, GlobalStyle globalStyle,
                                int bigTitleNumber, Integer pageId) {
        List<PPTElement> titleElements = globalStyle.getTitleElements();
        if (pageId.equals(PPTPage.BIG_TITLE.getId())) {
            fillTitleNumber(numberTexts, bigTitleNumber);
            return;
        }
        List<PPTText> titleTexts = numberTexts.stream()
                .filter(text -> titleElements.stream().anyMatch(title -> title.equals(text)))
                .collect(Collectors.toList());
        numberTexts.removeAll(titleTexts);
        fillTitleNumber(titleTexts, bigTitleNumber);
        fillNormalNumber(numberTexts);
    }

    private void fillNormalNumber(List<PPTText> numberTexts) {
        textDesignHelper.sortByTop(numberTexts);
        for (int i = 0; i < Math.min(numberTexts.size(), 10); i++) {
            buildNumberText(i + 1, numberTexts.get(i));
        }
    }

    private void fillTitleNumber(List<PPTText> titleTexts, int bigTitleNumber) {
        for (PPTText titleText : titleTexts) {
            buildNumberText(bigTitleNumber, titleText);
        }
    }

    private void buildNumberText(int bigTitleNumber, PPTText titleText) {
        String allText = titleText.findAllText();
        String numberText = getCleanNumberStr(titleText);
        String newText;
        if (BConstant.CN_BIG_NUMBER.contains(numberText)) {
            newText = BConstant.CN_BIG_NUMBER.get(bigTitleNumber - 1);
        } else if (BConstant.CN_SMALL_NUMBER.contains(numberText)) {
            newText = BConstant.CN_SMALL_NUMBER.get(bigTitleNumber - 1);
        } else if (BConstant.EN_NUMBER.contains(numberText.toLowerCase())) {
            newText = BConstant.EN_NUMBER.get(bigTitleNumber - 1).toUpperCase();
        } else if (TextUtil.isAllNumber(numberText) && numberText.length() == 2) {
            newText = "0" + bigTitleNumber;
        } else {
            newText = Integer.toString(bigTitleNumber);
        }
        textDesignHelper.setText(titleText, TextUtil.replaceText(allText, numberText, newText));
    }

    private List<List<PPTText>> findAllTitleText(List<PPTText> numberTexts,
                                                 List<PPTText> allTexts) {
        Map<Integer, List<PPTText>> fontSizeMap = buildMapByFontSize(numberTexts, allTexts);
        List<List<PPTText>> sameSizeList = fontSizeMap.values().stream()
                .filter(list -> list.size() == numberTexts.size()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(sameSizeList)) {
            return sameSizeList;
        } else {
            return Lists.newArrayList();
        }
    }


    @NotNull
    private Map<Integer, List<PPTText>> buildMapByFontSize(List<PPTText> numberTexts, List<PPTText> allTexts) {
        Map<Integer, List<PPTText>> fontSizeMap = Maps.newHashMap();
        for (PPTText pptText : allTexts) {
            String thisText = pptText.findAllText();
            if (numberTexts.contains(pptText) || thisText.contains("目录")
                    || thisText.toLowerCase().contains("content")) {
                int fontSize = textDesignHelper.getFontSize(pptText);
                if (fontSizeMap.containsKey(fontSize)) {
                    fontSizeMap.get(fontSize).add(pptText);
                } else {
                    fontSizeMap.put(fontSize, Lists.newArrayList(pptText));
                }
            }
        }
        return fontSizeMap;
    }

    public List<PPTText> findAllNumberTexts(List<PPTText> texts) {
        return texts.stream().filter(text -> {
            String str = getCleanNumberStr(text);
            return TextUtil.isAllNumber(str) || BConstant.CN_BIG_NUMBER.contains(str)
                    || BConstant.CN_SMALL_NUMBER.contains(str)
                    || BConstant.EN_NUMBER.contains(str.toLowerCase());
        }).collect(Collectors.toList());
    }

    @NotNull
    private String getCleanNumberStr(PPTText text) {
        return text.findAllText()
                .replace(".", EMPTY_STRING)
                .replace(" ", EMPTY_STRING)
                .replace("、", EMPTY_STRING)
                .replace(":", EMPTY_STRING)
                .replace("PART", EMPTY_STRING)
                .replace("part", EMPTY_STRING)
                .replace("Part", EMPTY_STRING)
                .replace("NO", EMPTY_STRING)
                .replace("No", EMPTY_STRING)
                .replace("no", EMPTY_STRING)
                .replace("：", EMPTY_STRING);
    }

}
