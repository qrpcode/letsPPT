package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.context.PPTBlockContext;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.LanguageType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.domain.vo.NlpItemVo;
import cc.pptshow.build.pptbuilder.service.PPTRegionService;
import cc.pptshow.build.pptbuilder.util.TextUtil;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 设定首页的文字
 */
@Slf4j
@Service
@Design(type = PPTBlockType.TEXT, order = 20,
        needIteration = false, onlyInPage = PPTPage.HOME)
public class HomeTextDesignImpl implements DesignBiz {

    private static final Long TIME_OR_SAY = 2L;

    @Resource
    private TextDesignHelper textDesignHelper;

    @Resource
    private PPTRegionService pptRegionService;

    @Override
    public DesignResponse design(DesignRequest request) {
        List<PPTElement> pptElements = request.getPptElements();
        List<PPTText> allTexts = textDesignHelper.findAllTexts(pptElements);
        List<PPTText> texts = buildBigTitleText(request, pptElements, allTexts);
        allTexts.removeAll(texts);
        //检查时间日期框内容
        List<PPTText> nameAndTimeTexts = buildNameAndTime(allTexts, request.getGlobalStyle());
        allTexts.removeAll(nameAndTimeTexts);
        //一个字符的需要分类后再确定内容
        for (PPTText onceText : allTexts) {
            String allText = onceText.findAllText();
            if (!TextUtil.isContainChinese(allText)) {
                textDesignHelper.buildEnglishText(onceText, request.getGlobalStyle());
            } else {
                textDesignHelper.buildChineseText(onceText, TextUtil.getChineseStart(allText), request.getGlobalStyle(), true);
            }
        }
        return DesignResponse.buildByRequest(request);
    }

    private List<PPTText> buildNameAndTime(List<PPTText> allTexts, GlobalStyle globalStyle) {
        List<PPTText> pptTexts = allTexts.stream()
                .filter(this::isMaybeNameOrTime)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(pptTexts)) {
            return Lists.newArrayList();
        }
        List<PPTText> returnTexts = Lists.newArrayList(pptTexts);
        List<PPTText> onlyOneTexts = pptTexts.stream()
                .filter(text -> text.findAllText().length() == 1).collect(Collectors.toList());
        pptTexts.removeAll(onlyOneTexts);
        //如果是单字的肯定是演讲者，不会有人蛋疼到把年份一个数字拆一个字符框的
        if (CollectionUtils.isEmpty(onlyOneTexts)) {
            //没有单字情况
            if (pptTexts.size() == 1) {
                setTimeAndNameIn1Texts(globalStyle, pptTexts);
            } else if (pptTexts.size() == 2) {
                setTimeAndNameIn2Texts(globalStyle, pptTexts);
            } else {
                setTimeAndNameInMoreThan2Texts(globalStyle, pptTexts);
            }
        } else {
            pptTexts.forEach(p -> textDesignHelper.setText(p, "日期：" + BConstant.DATE_TEXT));
            textDesignHelper.setCopyrightByOneTexts(onlyOneTexts, globalStyle);
        }
        return returnTexts;
    }

    private boolean isMaybeNameOrTime(PPTText element) {
        PPTBlockPut blockPut = PPTBlockContext.findUsePPTBlockPut(element);
        if (Objects.nonNull(blockPut)) {
            PPTRegion pptRegion = pptRegionService.queryById(blockPut.getPptRegionId());
            if (pptRegion.getId().equals(TIME_OR_SAY)) {
                return true;
            }
        }
        return (textDesignHelper.isMaybeName(element)
                || textDesignHelper.isMaybeTime(element)) && textDesignHelper.getFontSize(element) < 25;
    }

    private void setTimeAndNameInMoreThan2Texts(GlobalStyle globalStyle, List<PPTText> pptTexts) {
        PPTText pptText = textDesignHelper.findMaxLong(pptTexts);
        textDesignHelper.setText(pptText, "日期：" + BConstant.DATE_TEXT);
        pptTexts.remove(pptText);
        pptTexts.forEach(p -> textDesignHelper.setCopyright(p, globalStyle));
    }

    private void setTimeAndNameIn1Texts(GlobalStyle globalStyle, List<PPTText> pptTexts) {
        //只有一个
        PPTText pptText = pptTexts.get(0);
        if (textDesignHelper.isMaybeTime(pptText)) {
            textDesignHelper.setText(pptText, "日期：" + BConstant.DATE_TEXT);
        } else {
            textDesignHelper.setCopyright(pptText, globalStyle);
        }
    }

    private void setTimeAndNameIn2Texts(GlobalStyle globalStyle, List<PPTText> pptTexts) {
        List<PPTText> timeTexts = pptTexts.stream()
                .filter(text -> textDesignHelper.isMaybeTime(text)).collect(Collectors.toList());
        if (timeTexts.size() == 1) {
            textDesignHelper.setText(timeTexts.get(0), "日期：" + BConstant.DATE_TEXT);
            pptTexts.removeAll(timeTexts);
            textDesignHelper.setCopyright(pptTexts.get(0), globalStyle);
        } else {
            PPTText firstPPTText = pptTexts.get(0);
            PPTText secondPPTText = pptTexts.get(1);
            if (firstPPTText.findAllText().length() > secondPPTText.findAllText().length()) {
                textDesignHelper.setText(firstPPTText, "日期：" + BConstant.DATE_TEXT);
                textDesignHelper.setCopyright(secondPPTText, globalStyle);
            } else {
                textDesignHelper.setText(secondPPTText, "日期：" + BConstant.DATE_TEXT);
                textDesignHelper.setCopyright(firstPPTText, globalStyle);
            }
        }
    }


    @NotNull
    private List<PPTText> buildBigTitleText(DesignRequest request, List<PPTElement> pptElements, List<PPTText> allTexts) {
        PPTText titleText = textDesignHelper.findMaybeTitle(pptElements);
        int fontSize = textDesignHelper.getFontSize(titleText);
        List<PPTText> texts = findSameSizeTexts(allTexts, fontSize);
        setBigTitleText(texts, request.getGlobalStyle());
        return texts;
    }

    @NotNull
    private List<PPTText> findSameSizeTexts(List<PPTText> allTexts, int fontSize) {
        return Lists.newArrayList(allTexts).stream()
                .filter(e -> textDesignHelper.getFontSize(e) == fontSize)
                .collect(Collectors.toList());
    }

    private void setBigTitleText(List<PPTText> texts, GlobalStyle globalStyle) {
        if (texts.size() == 1) {
            PPTText titleText = texts.get(0);
            buildByOnlyText(titleText, globalStyle);
        } else {
            //多组文字的时候，字数多的一组使用我们的文字策略，另外一组直接采用缺省值
            PPTText titleText = findLongText(texts, globalStyle);
            buildByOnlyText(titleText, globalStyle);
            texts.remove(titleText);
            buildDefaultText(texts, globalStyle);
        }
    }

    private void buildDefaultText(List<PPTText> texts, GlobalStyle globalStyle) {
        List<String> adTexts = Lists.newArrayList();
        for (PPTText text : texts) {
            String allText = findAllTextByLanguage(text, globalStyle);
            String textStr = textDesignHelper.buildRandText(allText.length(), adTexts);
            textDesignHelper.setText(text, textStr);
        }
    }

    private PPTText findLongText(List<PPTText> texts, GlobalStyle globalStyle) {
        PPTText text = null;
        int max = 0;
        for (PPTText pptText : texts) {
            String allText = findAllTextByLanguage(pptText, globalStyle);
            if (allText.length() > max) {
                text = pptText;
                max = allText.length();
            }
        }
        return text;
    }

    private void buildByOnlyText(PPTText titleText, GlobalStyle globalStyle) {
        String cleanTitle = globalStyle.getTitle()
                .replace(" ", "")
                .replace("ppt", "")
                .replace("模板", "");
        String allText = findAllTextByLanguage(titleText, globalStyle);
        if (cleanTitle.length() == allText.length()) {
            //如果正好标题和原来页面字数一样，那就直接使用
            textDesignHelper.setText(titleText, cleanTitle);
        } else if (cleanTitle.length() < allText.length()) {
            //如果标题字段短了
            addTextToLength(titleText, cleanTitle, allText);
        } else {
            StringBuilder newTitleText = new StringBuilder();
            List<NlpItemVo> items = globalStyle.getNlpVo().getItems();
            for (NlpItemVo item : items) {
                //减去模板两个字
                if (newTitleText.length() + item.getItem().length() <= allText.length() - 2) {
                    newTitleText.append(item.getItem());
                } else {
                    break;
                }
            }
            addTextToLength(titleText, newTitleText.toString(), allText);
        }
    }

    private void addTextToLength(PPTText titleText, String cleanTitle, String allText) {
        if (allText.length() - cleanTitle.length() < 2) {
            textDesignHelper.setText(titleText, cleanTitle);
        } else if (allText.length() - cleanTitle.length() < 4) {
            textDesignHelper.setText(titleText, cleanTitle + "模板");
        } else if (allText.length() - cleanTitle.length() < 6) {
            textDesignHelper.setText(titleText, cleanTitle + "演示模板");
        } else if (allText.length() - cleanTitle.length() < 8) {
            textDesignHelper.setText(titleText, cleanTitle + "精品演示模板");
        } else if (allText.length() - cleanTitle.length() < 10) {
            textDesignHelper.setText(titleText, cleanTitle + "原创设计演示模板");
        } else {
            textDesignHelper.setText(titleText, cleanTitle + "原创设计演示PPT模板");
        }
    }

    private String findAllTextByLanguage(PPTText titleText, GlobalStyle globalStyle) {
        String allText = titleText.findAllText();
        if (globalStyle.getLanguageType().equals(LanguageType.CHINESE)) {
            if (!TextUtil.isContainChinese(allText)) {
                allText = BConstant.LOREM_IPSUM_CN.substring(0, allText.length() / 2);
            }
            return allText;
        } else if (globalStyle.getLanguageType().equals(LanguageType.ENGLISH)) {
            if (TextUtil.isContainChinese(allText)) {
                allText = BConstant.LOREM_IPSUM_EN.substring(0, allText.length() * 2);
            }
            return allText;
        }
        throw new RuntimeException("出现一种没有实现的case！");
    }

}
