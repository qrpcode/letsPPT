package cc.pptshow.build.pptbuilder.biz.builder.impl;

import cc.pptshow.build.pptbuilder.bean.*;
import cc.pptshow.build.pptbuilder.biz.analysis.TitleAnalysis;
import cc.pptshow.build.pptbuilder.biz.builder.PPTBuildBiz;
import cc.pptshow.build.pptbuilder.biz.builder.PageBuildBiz;
import cc.pptshow.build.pptbuilder.biz.builder.helper.AboutBuildHelper;
import cc.pptshow.build.pptbuilder.biz.builder.helper.ChannelHelper;
import cc.pptshow.build.pptbuilder.biz.builder.helper.MediaHelper;
import cc.pptshow.build.pptbuilder.biz.builder.helper.TagHelper;
import cc.pptshow.build.pptbuilder.biz.design.help.ImgHelper;
import cc.pptshow.build.pptbuilder.biz.helper.GlobalStyleHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.*;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.domain.qo.PPTPageBuildQo;
import cc.pptshow.build.pptbuilder.service.*;
import cc.pptshow.build.pptbuilder.util.*;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.SerializablePPTElement;
import cc.pptshow.ppt.show.PPTShow;
import cc.pptshow.ppt.show.PPTShowSide;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.constant.BConstant.SYS_PATH;

@Slf4j
@Service
public class PPTBuildBizImpl implements PPTBuildBiz {

    @Resource
    private GlobalStyleHelper globalStyleHelper;

    @Resource
    private PageBuildBiz pageBuildBiz;

    @Resource
    private TitleAnalysis titleAnalysis;

    @Resource
    private ColorInfoService colorInfoService;

    @Resource
    private ColorStyleService colorStyleService;

    @Resource
    private EnumPPTStyleService enumPPTStyleService;

    @Resource
    private FontInfoService fontInfoService;

    @Resource
    private PPTPageTypeService pptPageTypeService;

    @Resource
    private PPTFileDataService pptFileDataService;

    @Resource
    private LoremInfoService loremInfoService;

    @Resource
    private FilePPTInfoService filePPTInfoService;

    @Resource
    private ChannelHelper channelHelper;

    @Resource
    private NullColorTitleService nullColorTitleService;

    @Resource
    private ImgHelper imgHelper;

    public static final int ALL_INNER_PAGE = 16;

    @SneakyThrows
    @Override
    public FilePPTInfo buildByRequire(BuildRequire buildRequire) {
        GlobalStyle globalStyle = buildStyleByRequire(buildRequire);
        if (filePPTInfoService.haveSameTitle(globalStyle.getTitle())) {
            return null;
        }
        String uuid = filePPTInfoService.getUid(globalStyle);
        globalStyle.setUuid(uuid);
        new File(SYS_PATH + "print/" + uuid + "/").mkdirs();
        String pptPath = SYS_PATH + "print/" + uuid + "/" + System.currentTimeMillis() + ".pptx";
        PPTShow ppt = new PPTShow();
        int bigTitlePageCount = 0;
        for (int i = 0; i < globalStyle.getPptPageIds().size(); i++) {
            Integer pptPageId = globalStyle.getPptPageIds().get(i);
            if (pptPageId.equals(PPTPage.BIG_TITLE.getId())) {
                bigTitlePageCount++;
            }
            ppt.add(pageBuildBiz.buildPageByStyle(pptPageId, globalStyle, ppt, bigTitlePageCount, i));
        }
        ppt.toFile(pptPath);
        FilePPTInfo filePPTInfo = channelHelper.buildFileInfo(pptPath, globalStyle, buildRequire.getDataTitleId());
        filePPTInfoService.insertFilePPTInfo(filePPTInfo);
        return filePPTInfo;
    }

    @Override
    public void buildByFileData(Long id) {
        PPTFileData pptFileData = pptFileDataService.selectById(id);
        SerializableGlobalStyle serializableGlobalStyle =
                JSON.parseObject(pptFileData.getGlobalStyle(), SerializableGlobalStyle.class);
        GlobalStyle globalStyle = SerializableGlobalStyle.buildGlobalStyle(serializableGlobalStyle);
        List<SerializablePPTElement> serializablePPTElements =
                JSON.parseArray(pptFileData.getElements(), SerializablePPTElement.class);
        List<PPTElement> elements = SerializablePPTElement.buildPPTElements(serializablePPTElements);
        PPTRegionPut regionPut = JSON.parseObject(pptFileData.getPptRegionPut(), PPTRegionPut.class);

        PPTPageBuildQo pptPageBuildQo = new PPTPageBuildQo();
        pptPageBuildQo.setPptElements(elements);
        pptPageBuildQo.setPageTypeId(pptFileData.getPageId());
        pptPageBuildQo.setGlobalStyle(globalStyle);
        pptPageBuildQo.setPptRegionPut(regionPut);
        pptPageBuildQo.setBuildContext(HashBasedTable.create());
        pptPageBuildQo.setColorContext(HashBasedTable.create());
        pptPageBuildQo.setBigTitlePageCount(1);
        PPTShowSide pptShowSide = pageBuildBiz.pptPageBuild(pptPageBuildQo);

        PPTShow ppt = new PPTShow();
        ppt.add(pptShowSide);
        ppt.toFile("C:/Users/qrp19/Desktop/" + System.currentTimeMillis() + ".pptx");
    }

    public GlobalStyle buildStyleByRequire(BuildRequire buildRequire) {
        GlobalStyle globalStyle = new GlobalStyle();
        globalStyle.setUuid(UUID.randomUUID().toString());
        StyleAnalysis styleAnalysis = titleAnalysis.analysisTitle(buildRequire.getTitle());
        EnumPPTStyle pptStyle = buildPPTStyle(styleAnalysis);
        globalStyle.setPptStyle(pptStyle);
        globalStyle.setColorInfo(buildColorInfo(buildRequire, styleAnalysis, pptStyle));
        globalStyle.setTextFontInfo(buildTextFontInfo(pptStyle));
        globalStyle.setTitleFontInfo(buildTitleFontInfo(pptStyle));
        globalStyle.setTitle(buildRequire.getTitle());
        globalStyle.setEnTitle(TranslationUtil.cn2En(buildRequire.getTitle()));
        globalStyle.setPptPageIds(buildPageIds(buildRequire.getPageTypes()));
        globalStyle.setNormalFontSize(RandUtil.round(13, 15));
        globalStyle.setNlpVo(styleAnalysis.getNlpVo());
        globalStyle.setImgElements(styleAnalysis.getElements());
        globalStyle.setLorem(loremInfoService.getRandByLanguage(globalStyle.getLanguageType()));
        globalStyle.setChannelType(buildRequire.getChannelType());
        imgHelper.initializationImgInfo(globalStyle);
        globalStyleHelper.buildTitle(globalStyle);
        return globalStyle;
    }

    private List<Integer> buildPageIds(List<Integer> pageTypes) {
        List<Integer> pageIds = Lists.newArrayList();
        List<PPTPageType> titlePages = pptPageTypeService.findAllCanPutTitlePage();
        Set<Integer> titlePageIds = toTitlePages(titlePages);
        List<Integer> needPages = filterNeedPageIds(pageTypes, titlePageIds);
        //首页
        pageIds.add(pptPageTypeService.findHomeNumber());
        //引言
        if (needPages.contains(pptPageTypeService.findIntroductionNumber())) {
            pageIds.add(pptPageTypeService.findIntroductionNumber());
        }
        //目录
        pageIds.add(pptPageTypeService.findContentsNumber());

        List<Integer> allInnerPages = findAllInnerPages(titlePages, needPages);
        pageIds.add(pptPageTypeService.findBigTitleNumber());
        pageIds.addAll(allInnerPages.subList(0, 4));
        pageIds.add(pptPageTypeService.findBigTitleNumber());
        pageIds.addAll(allInnerPages.subList(5, 8));
        pageIds.add(pptPageTypeService.findBigTitleNumber());
        pageIds.addAll(allInnerPages.subList(9, 12));
        pageIds.add(pptPageTypeService.findBigTitleNumber());
        pageIds.addAll(allInnerPages.subList(13, 16));
        pageIds.add(pptPageTypeService.findThankNumber());
        return pageIds;
    }

    private List<Integer> findAllInnerPages(List<PPTPageType> titlePages, List<Integer> needPages) {
        needPages.remove(pptPageTypeService.findIntroductionNumber());
        List<Integer> all = Lists.newArrayList();
        for (PPTPageType titlePage : titlePages) {
            Integer maxRepeat = titlePage.getMaxRepeat();
            maxRepeat -= findElementSize(needPages, titlePage.getId());
            for (int i = 0; i < maxRepeat; i++) {
                all.add(titlePage.getId());
            }
        }
        Collections.shuffle(all);
        needPages.addAll(all.subList(0, ALL_INNER_PAGE - needPages.size()));
        return needPages;
    }

    private Integer findElementSize(List<Integer> needPages, Integer id) {
        return (int) needPages.stream().filter(page -> page.equals(id)).count();
    }

    private Set<Integer> toTitlePages(List<PPTPageType> titlePages) {
        return titlePages.stream().map(PPTPageType::getId).collect(Collectors.toSet());
    }

    private List<Integer> filterNeedPageIds(List<Integer> pageTypes, Set<Integer> titlePageIds) {
        return pageTypes.stream().filter(titlePageIds::contains).collect(Collectors.toList());
    }

    private FontInfo buildTitleFontInfo(EnumPPTStyle pptStyle) {
        return fontInfoService.findTitleFontByStyle(pptStyle.getId());
    }

    private EnumPPTStyle buildPPTStyle(StyleAnalysis styleAnalysis) {
        String styleName = styleAnalysis
                .getStyle()
                .stream()
                .parallel()
                .findAny()
                .orElseThrow(RuntimeException::new);
        return enumPPTStyleService.findByName(styleName);
    }

    private FontInfo buildTextFontInfo(EnumPPTStyle pptStyle) {
        return fontInfoService.findTextFontByStyle(pptStyle.getId());
    }

    private ColorInfo buildColorInfo(BuildRequire buildRequire,
                                     StyleAnalysis styleAnalysis,
                                     EnumPPTStyle pptStyle) {
        //如果存在联网查询颜色，那就优先联网查询
        //这里由于返回颜色和实际偏差太大，频繁出现问题所以不使用了
        //ColorInfo info = buildColorInfoByWebColor(styleAnalysis.getWebColor());
        ColorInfo info = null;
        //如果存在颜色近似词也可以（比如：灰色写的灰，蓝色写的天空色）
        info = findTitleColorByNearlyWord(styleAnalysis);
        //用户如果定义了自定义的颜色，优先使用用户指定的
        if (Objects.isNull(info)) {
            info = findUserSetColor(buildRequire);
        }
        //如果不存在颜色指定词，但是在标题中存在颜色词就按照解析的颜色来
        if (Objects.isNull(info)) {
            info = findTitleColor(styleAnalysis);
        }
        //如果风格有指定风格要求，就用模板指定的（比如：党建风要求红色）
        if (Objects.isNull(info)) {
            info = findPPTStyleColor(pptStyle);
        }
        //如果是模板主题命中了一些，那就采用命中的
        if (Objects.isNull(info)) {
            info = findPPTColorByTitle(styleAnalysis);
        }
        //颜色随机
        if (Objects.isNull(info)) {
            info = colorInfoService.randColor();
        }
        return info;
    }

    private ColorInfo findTitleColorByNearlyWord(StyleAnalysis styleAnalysis) {
        List<ColorStyle> colorStyles = colorStyleService.findAll();
        String title = styleAnalysis.getNlpVo().getText();
        List<ColorStyle> styles = colorStyles.stream().filter(color -> {
            List<String> words = Lists.newArrayList(BConstant.SPLITTER.split(color.getNearlyWord()));
            return words.stream().anyMatch(title::contains);
        }).collect(Collectors.toList());
        List<Integer> styleIds = styles.stream().map(ColorStyle::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(styles)) {
            List<ColorInfo> allColors = colorInfoService.findAll();
            List<ColorInfo> infos = allColors.stream()
                    .filter(colorInfo -> {
                        String colorType = colorInfo.getColorType();
                        List<String> types = Lists.newArrayList(BConstant.SPLITTER.split(colorType))
                                .stream()
                                .filter(type -> styleIds.contains(Integer.parseInt(type)))
                                .collect(Collectors.toList());
                        return !CollectionUtils.isEmpty(types);
                    })
                    .collect(Collectors.toList());
            return RandUtil.randElement(infos);
        }
        return null;
    }

    private ColorInfo findPPTColorByTitle(StyleAnalysis styleAnalysis) {
        List<ColorStyle> colorStyles = colorStyleService.findAll();
        String title = styleAnalysis.getNlpVo().getText();
        List<ColorStyle> infos = colorStyles.stream()
                .filter(colorInfo -> Strings.isNotEmpty(colorInfo.getMatchWord())
                        && Safes.of(Lists.newArrayList(BConstant.SPLITTER.split(colorInfo.getMatchWord()))).stream()
                        .filter(Strings::isNotEmpty)
                        .anyMatch(title::contains))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(infos)) {
            List<ColorInfo> allColors = colorInfoService.findAll();
            List<Integer> types = infos.stream().map(ColorStyle::getId).collect(Collectors.toList());
            List<ColorInfo> matchInfos = allColors.stream()
                    .filter(colorInfo -> Lists.newArrayList(BConstant.SPLITTER.split(colorInfo.getColorType()))
                            .stream().anyMatch(type -> types.contains(Integer.parseInt(type))))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(matchInfos)) {
                return RandUtil.randElement(matchInfos);
            }
            nullColorTitleService.addNullTitle(title);
            //throw new RuntimeException("没有名称匹配，名称是:" + title);
        }
        nullColorTitleService.addNullTitle(title);
        //throw new RuntimeException("没有名称匹配，名称是:" + title);
        return null;
    }

    private ColorInfo buildColorInfoByWebColor(List<RGB> webColor) {
        webColor = Safes.of(webColor).stream()
                .filter(color -> !(color.getR() == color.getG() && color.getG() == color.getB()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(webColor)) {
            return null;
        }
        ColorInfo colorInfo = new ColorInfo();
        colorInfo.setFromColor(ColorUtil.toHexOnlyText(webColor.get(0)));
        if (webColor.size() > 1) {
            colorInfo.setToColor(ColorUtil.toHexOnlyText(webColor.get(1)));
        }
        colorInfo.setBackgroundColor("FFFFFF");
        colorInfo.setColorType("1");
        return colorInfo;
    }

    private ColorInfo findPPTStyleColor(EnumPPTStyle pptStyle) {
        if (Objects.nonNull(pptStyle.getMustColorStyle()) && pptStyle.getMustColorStyle() > 0) {
            ColorInfo info3 = colorInfoService.queryByStyleId(pptStyle.getMustColorStyle());
            if (Objects.nonNull(info3)) {
                return info3;
            }
        }
        return null;
    }

    private ColorInfo findTitleColor(StyleAnalysis styleAnalysis) {
        ColorStyle colorStyle = colorStyleService.findByWord(styleAnalysis.getColor());
        ColorStyle backgroundColorStyle = colorStyleService.findByWord(styleAnalysis.getBackgroundColor());
        if (Objects.nonNull(colorStyle) || Objects.nonNull(backgroundColorStyle)) {
            ColorInfo info = colorInfoService.findByColorStyleAndBackgroundColor(colorStyle, backgroundColorStyle);
            if (Objects.nonNull(info)) {
                return info;
            }
        }
        return null;
    }

    private ColorInfo findUserSetColor(BuildRequire buildRequire) {
        if (Objects.nonNull(buildRequire.getColorStyle())) {
            ColorInfo info = colorInfoService.queryByStyleId(buildRequire.getColorStyle());
            if (Objects.nonNull(info)) {
                return info;
            }
        }
        return null;
    }


}
