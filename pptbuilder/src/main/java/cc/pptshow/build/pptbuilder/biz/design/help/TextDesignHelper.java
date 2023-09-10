package cc.pptshow.build.pptbuilder.biz.design.help;

import cc.pptshow.build.pptbuilder.bean.FontInfo;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.TextCount;
import cc.pptshow.build.pptbuilder.util.MathUtil;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.build.pptbuilder.util.TextUtil;
import cc.pptshow.ppt.constant.PPTNameConstant;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTInnerLine;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.constant.BConstant.*;
import static org.apache.xerces.util.XMLSymbols.EMPTY_STRING;

@Slf4j
@Service
public class TextDesignHelper {

    /**
     * 修改文本组件css样式，修改成够用即可
     */
    public PPTText chooseTextCssOnlyTextNeed(PPTText pptText) {
        double width = pptText.findMinWidthSize();
        if (width > pptText.getCss().getWidth()) {
            return pptText;
        }
        double oldWidth = pptText.getCss().getWidth();
        double deleteWidth = oldWidth - width;
        String align = pptText.getLineList().get(0).getCss().getAlign();
        if (StringUtils.equals(align, PPTNameConstant.ALIGN_LEFT)) {
            pptText.getCss().setWidth(width);
        } else if (StringUtils.equals(align, PPTNameConstant.ALIGN_RIGHT)) {
            pptText.getCss().setLeft(pptText.getCss().getLeft() + deleteWidth).setWidth(width);
        } else if (StringUtils.equals(align, PPTNameConstant.ALIGN_CENTER)) {
            pptText.getCss().setLeft(pptText.getCss().getLeft() + (deleteWidth / 2)).setWidth(width);
        }
        return pptText;
    }

    public void setFontSize(PPTText pptText, int fontSize) {
        pptText.getLineList()
                .forEach(pptInnerLine -> pptInnerLine.getTextList()
                        .forEach(pptInnerText ->
                                pptInnerText.getCss().setFontSize(fontSize)));
    }

    public int getFontSize(PPTText pptText) {
        return pptText.getLineList().get(0).getTextList().get(0).getCss().getFontSize();
    }

    public PPTText findMaybeTitle(List<PPTElement> elements) {
        List<PPTText> texts = elements.stream()
                .filter(e -> e instanceof PPTText)
                .map(e -> (PPTText) e)
                .collect(Collectors.toList());
        List<PPTText> chineseTexts = texts.stream()
                .filter(text -> TextUtil.isContainChinese(text.findAllText()))
                .collect(Collectors.toList());
        List<PPTText> noChinese = texts.stream().filter(t -> !chineseTexts.contains(t)).collect(Collectors.toList());
        PPTText maxSizeChineseText = findMaxSizeText(chineseTexts);
        if (Objects.nonNull(maxSizeChineseText)) {
            return maxSizeChineseText;
        }
        return findMaxSizeText(noChinese);
    }

    @Nullable
    private PPTText findMaxSizeText(List<PPTText> chineseTexts) {
        PPTText pptText = null;
        for (PPTText text : chineseTexts) {
            if (text.findAllText().length() > 4
                    && (Objects.isNull(pptText)
                    || getFontSize(pptText) < getFontSize(text))
            ) {
                pptText = text;
            }
        }
        return pptText;
    }

    public boolean isOnlyOneLine(PPTText pptText) {
        double nowWidth = pptText.getCss().getWidth();
        double oneLineWidth = pptText.findMinWidthSize();
        return oneLineWidth < nowWidth;
    }

    public void setNormalFontFamily(PPTText pptText, FontInfo textFontInfo) {
        setFontFamily(pptText, textFontInfo.getFontCode());
    }

    public void setFontFamily(PPTText pptText, String fontFamily) {
        pptText.getLineList()
                .forEach(pptInnerLine -> pptInnerLine.getTextList()
                        .forEach(pptInnerText ->
                                pptInnerText.getCss().setFontFamily(fontFamily)));
    }

    public void setAllTextColor(PPTText pptText, String finalColor) {
        pptText.getLineList()
                .forEach(pptInnerLine -> pptInnerLine.getTextList()
                        .forEach(pptInnerText -> pptInnerText.getCss().setColor(finalColor)));
    }

    public String getFontColor(PPTText pptText) {
        if (CollectionUtils.isEmpty(pptText.getLineList())
                || CollectionUtils.isEmpty(pptText.getLineList().get(0).getTextList())) {
            return null;
        }
        return pptText.getLineList().get(0).getTextList().get(0).getCss().getColor();
    }

    public void setTextAlign(PPTText pptText, String align) {
        pptText.getLineList().forEach(line -> line.getCss().setAlign(align));
    }

    public void setLineHeight(PPTText pptText, double lineHeight) {
        for (PPTInnerLine pptInnerLine : pptText.getLineList()) {
            pptInnerLine.getCss().setLineHeight(lineHeight);
        }
    }

    public double getLineHeight(PPTText pptText) {
        return pptText.getLineList().get(0).getCss().getLineHeight();
    }

    public CharSequence getTextAlign(PPTText pptText) {
        PPTInnerLine pptInnerLine = pptText.getLineList().stream().findFirst().orElse(null);
        if (Objects.isNull(pptInnerLine)) {
            return PPTNameConstant.ALIGN_LEFT;
        }
        String align = pptInnerLine.getCss().getAlign();
        return Strings.isNotEmpty(align) ? align : PPTNameConstant.ALIGN_LEFT;
    }

    public int getLineCount(PPTText pptText) {
        double nowWidth = pptText.getCss().getWidth();
        double oneLineWidth = pptText.findMinWidthSize();
        return (int) Math.ceil(oneLineWidth / nowWidth);
    }

    public void setText(PPTText pptText, String allText) {
        pptText.getLineList().get(0).getTextList().get(0).setText(allText);
    }

    public void buildEnglishText(PPTText onceText, GlobalStyle globalStyle) {
        String enTitle = globalStyle.getEnTitle();
        String allText = onceText.findAllText().trim();
        if (allText.startsWith(CONTENT)) {
            return;
        }
        if (allText.length() == 4 && allText.startsWith("20")) {
            setText(onceText, BConstant.YEAR_TEXT);
            return;
        } else if (allText.startsWith("20") && allText.length() >= 6 && allText.length() <= 12) {
            setText(onceText, BConstant.DATE_TEXT);
            return;
        }
        List<String> words = Lists.newArrayList(BConstant.BLANK_SPLITTER.split(enTitle));
        words = copyByTime(words, allText.length());
        StringBuilder useStr = new StringBuilder();
        int i = 0;
        while (useStr.length() + words.get(i).length() <= allText.length()) {
            useStr.append(words.get(i++)).append(BLANK);
        }
        setText(onceText, useStr.toString());
    }

    private List<String> copyByTime(List<String> words, int length) {
        List<String> copy = Lists.newArrayList();
        for (int i = 0; i <= length / 2 + 1; i++) {
            copy.addAll(words);
        }
        return copy;
    }

    public String buildRandText(int length, List<String> adTexts) {
        List<String> adAllTexts = Lists.newArrayList(BConstant.HOME_RAND_TEXTS);
        adAllTexts.removeAll(adTexts);
        List<String> filterTexts = adAllTexts.stream().filter(t -> t.length() == length).collect(Collectors.toList());
        log.info("length:{}, texts:{}", length, adAllTexts);
        return CollectionUtils.isNotEmpty(filterTexts) ? RandUtil.round(filterTexts) : EMPTY_STRING;
    }

    public void buildChineseText(PPTText pptText, int start, GlobalStyle globalStyle, boolean isHomePage) {
        String allText = pptText.findAllText().trim();
        if (isHomePage) {
            if (isMaybeName(allText)) {
                setCopyright(pptText, globalStyle);
                return;
            }
            if (isMaybeTime(pptText)) {
                setText(pptText, "日期：" + BConstant.DATE_TEXT);
                return;
            }
        }
        if (allText.length() - start > 8) {
            String lorem = globalStyle.getLorem();
            String subStr = (globalStyle.getTitle() + lorem + lorem + lorem + lorem + lorem + lorem + lorem).substring(0, allText.length());
            setText(pptText,  allText.substring(0, start) + subStr);
        } else if (allText.length() == 1) {
            setText(pptText, "X");
        } else {
            String randText = buildRandText(allText.length(), Lists.newArrayList());
            if (Strings.isNotBlank(randText)) {
                setText(pptText, allText.substring(0, start) + randText);
            } else {
                fillTitle(pptText, 0, globalStyle);
            }
        }
    }

    public boolean isMaybeName(PPTText pptText) {
        return isMaybeName(pptText.findAllText());
    }

    public boolean isMaybeName(String allText) {
        return allText.contains("人") || allText.contains("团队")
                || allText.contains("汇报") || allText.contains("答辩");
    }

    public boolean isMaybeTime(PPTText pptText) {
        if (Objects.isNull(pptText)) {
            return false;
        }
        String allText = pptText.findAllText();
        return allText.contains("日期") || allText.contains("时间");
    }

    public void setCopyright(PPTText pptText, GlobalStyle globalStyle) {
        String allText = pptText.findAllText().trim();
        TextCount textCount = TextUtil.findTextCount(allText);
        int textLength = textCount.getChineseCount() + (textCount.getExceptChineseCount() / 2);
        if (textLength < 4) {
            setText(pptText, globalStyle.getChannelType().getSign());
        } else if (textLength < 8) {
            setText(pptText, "汇报人：" + globalStyle.getChannelType().getSign());
        } else if (textLength < 10) {
            setText(pptText, "演讲汇报人：" + globalStyle.getChannelType().getSign());
        } else if (textLength < 12) {
            setText(pptText, "本次演讲汇报人：" + globalStyle.getChannelType().getSign());
        } else if (textLength < 14) {
            setText(pptText, "本次汇报的演讲嘉宾：" + globalStyle.getChannelType().getSign());
        } else {
            setText(pptText, "汇演报告现场演讲嘉宾：" + globalStyle.getChannelType().getSign());
        }
    }

    public PPTText findMaxLong(List<PPTText> pptTexts) {
        PPTText pptText = null;
        for (PPTText pptElement : pptTexts) {
            if (Objects.isNull(pptText) || pptText.findAllText().length() < pptElement.findAllText().length()) {
                pptText = pptElement;
            }
        }
        return pptText;
    }

    public void setCopyrightByOneTexts(List<PPTText> onlyOneTexts, GlobalStyle globalStyle) {
        int size = onlyOneTexts.size();
        sortOneTexts(onlyOneTexts);
        if (size == 1) {
            setOneTexts(onlyOneTexts, "〇");
        }
        if (size == 2) {
            setOneTexts(onlyOneTexts, "作者");
        } else if (size == 3) {
            setOneTexts(onlyOneTexts, globalStyle.getChannelType().getName());
        } else if (size == 4) {
            setOneTexts(onlyOneTexts, globalStyle.getChannelType().getName() + "网");
        } else if (size == 5) {
            setOneTexts(onlyOneTexts, globalStyle.getChannelType().getName() + "提供");
        } else if (size == 6) {
            setOneTexts(onlyOneTexts, "作者：" + globalStyle.getChannelType().getName());
        } else if (size == 7) {
            setOneTexts(onlyOneTexts, "演讲者：" + globalStyle.getChannelType().getName());
        } else if (size == 8) {
            setOneTexts(onlyOneTexts, "模板作者：" + globalStyle.getChannelType().getName());
        } else {
            String from = "----------------------------------------------------------";
            setOneTexts(onlyOneTexts, from.substring(0, size - 6) + "作者：" + globalStyle.getChannelType().getName());
        }
    }

    private void setOneTexts(List<PPTText> onlyOneTexts, String s) {
        for (int i = 0; i < onlyOneTexts.size(); i++) {
            setText(onlyOneTexts.get(i), s.substring(i, i + 1));
        }
    }

    private void sortOneTexts(List<PPTText> onlyOneTexts) {
        List<Double> lefts = onlyOneTexts.stream().map(text -> text.getCss().getLeft()).collect(Collectors.toList());
        List<Double> tops = onlyOneTexts.stream().map(text -> text.getCss().getTop()).collect(Collectors.toList());
        if (MathUtil.variance(lefts) > MathUtil.variance(tops)) {
            //left方差大应该
            onlyOneTexts.sort(Comparator.comparing(text -> text.getCss().getTop()));
        } else {
            onlyOneTexts.sort(Comparator.comparing(text -> text.getCss().getLeft()));
        }
    }

    @NotNull
    public List<PPTText> findAllTexts(List<PPTElement> pptElements) {
        return pptElements.stream()
                .filter(e -> e instanceof PPTText)
                .map(e -> (PPTText) e)
                .collect(Collectors.toList());
    }

    public void buildChineseTextMayBeTitle(PPTText text, GlobalStyle globalStyle) {
        String allText = text.findAllText().replace(BLANK, EMPTY_STRING);
        if (allText.contains("目录") || allText.length() < 3) {
            return;
        }
        int start = TextUtil.getChineseStart(allText);
        int fontSize = allText.length() - start;
        if (fontSize > globalStyle.getNormalFontSize() || allText.length() < 8 || allText.contains("标题")) {
            fillTitle(text, start, globalStyle);
        } else {
            buildChineseText(text, start, globalStyle, false);
        }
    }

    private void fillTitle(PPTText text, int start, GlobalStyle globalStyle) {
        String allText = text.findAllText();
        List<String> loremTitles = filterTextsByLength(globalStyle.getLoremTitles(), allText.length() - start);
        if (CollectionUtils.isNotEmpty(loremTitles)) {
            setText(text, allText.substring(0, start) + loremTitles.get(0));
        }
        List<String> titles = filterTextsByLength(TITLE_RAND_TEXTS, allText.length() - start);
        if (CollectionUtils.isEmpty(titles)) {
            titles = Lists.newArrayList(TITLE_RAND_TEXTS.get(TITLE_RAND_TEXTS.size() - 1));
        }
        String roundTitle = RandUtil.round(titles);
        globalStyle.getLoremTitles().add(roundTitle);
        setText(text, allText.substring(0, start) + roundTitle);
    }

    private List<String> filterTextsByLength(List<String> texts, int length) {
        return texts.stream()
                .filter(title -> title.length() == length)
                .collect(Collectors.toList());
    }

    public void sortByTop(List<PPTText> numberTexts) {
        numberTexts.sort(Comparator.comparing(text -> text.getCss().getTop()));
    }

    public boolean isMustOneLine(PPTText pptText) {
        String allText = pptText.findAllText();
        return BConstant.YEAR_TEXT.equals(allText) || (TextUtil.isAllNumber(allText) && allText.length() <= 4);
    }
}
