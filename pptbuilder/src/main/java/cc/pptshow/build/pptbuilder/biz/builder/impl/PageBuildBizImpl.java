package cc.pptshow.build.pptbuilder.biz.builder.impl;

import cc.pptshow.build.pptbuilder.anno.ForPage;
import cc.pptshow.build.pptbuilder.anno.ForRegion;
import cc.pptshow.build.pptbuilder.anno.Inference;
import cc.pptshow.build.pptbuilder.bean.*;
import cc.pptshow.build.pptbuilder.biz.builder.PageBuildBiz;
import cc.pptshow.build.pptbuilder.biz.builder.element.ElementBuilder;
import cc.pptshow.build.pptbuilder.biz.builder.inference.InferenceBuilder;
import cc.pptshow.build.pptbuilder.biz.design.handle.BigTitleChooseNumberHandle;
import cc.pptshow.build.pptbuilder.biz.filter.page.PageFilter;
import cc.pptshow.build.pptbuilder.biz.filter.region.RegionFilter;
import cc.pptshow.build.pptbuilder.context.PPTBlockContext;
import cc.pptshow.build.pptbuilder.dao.*;
import cc.pptshow.build.pptbuilder.domain.PPTRegionGroup;
import cc.pptshow.build.pptbuilder.domain.SerializableGlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.*;
import cc.pptshow.build.pptbuilder.domain.qo.BuilderQo;
import cc.pptshow.build.pptbuilder.domain.qo.PPTPageBuildQo;
import cc.pptshow.build.pptbuilder.domain.qo.RegionFilterQo;
import cc.pptshow.build.pptbuilder.service.*;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.biz.design.handle.DesignHandle;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.ppt.domain.*;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.SerializablePPTElement;
import cc.pptshow.ppt.element.impl.*;
import cc.pptshow.ppt.show.PPTShow;
import cc.pptshow.ppt.show.PPTShowSide;
import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.constant.BConstant.*;

@Slf4j
@Service
public class PageBuildBizImpl implements PageBuildBiz {

    @Resource
    private PPTRegionMapper pptRegionMapper;

    @Resource
    private PPTBlockService pptBlockService;

    @Resource
    private PPTRegionPutService pptRegionPutService;

    @Resource
    private PPTBlockPutService pptBlockPutService;

    @Resource
    private DesignHandle designHandle;

    @Resource
    private PPTPageTypeService pptPageTypeService;

    @Resource
    private PPTPageModelService pptPageModelService;

    @Resource
    private PPTRegionService pptRegionService;

    @Autowired
    private List<PageFilter> pageFilters;

    @Autowired
    private List<RegionFilter> regionFilters;

    @Autowired
    private List<ElementBuilder> elementBuilders;

    @Resource
    private PPTFileDataService pptFileDataService;

    @Autowired
    private List<InferenceBuilder> inferenceBuilders;

    @Resource
    private BigTitleChooseNumberHandle bigTitleChooseNumberHandle;

    @Override
    public PPTShowSide buildPageByStyle(Integer pageTypeId,
                                        GlobalStyle globalStyle,
                                        PPTShow ppt,
                                        int bigTitlePageCount,
                                        int pageNum) {
        Map<Integer, InferenceBuilder> builderMap = buildInferenceMap();
        if (builderMap.containsKey(pageTypeId)) {
            return builderMap.get(pageTypeId).buildPageByStyle(globalStyle, ppt);
        }
        return buildByTemplate(pageTypeId, globalStyle, ppt, bigTitlePageCount, pageNum);
    }

    @NotNull
    private Map<Integer, InferenceBuilder> buildInferenceMap() {
        return inferenceBuilders.stream()
                .collect(Collectors.toMap(build -> Optional.ofNullable(build.getClass()
                        .getAnnotation(Inference.class))
                        .map(Inference::page)
                        .map(PPTPage::getId)
                        .orElse(0), b -> b));
    }

    private PPTShowSide buildByTemplate(Integer pageTypeId,
                                        GlobalStyle globalStyle,
                                        PPTShow ppt,
                                        int bigTitlePageCount,
                                        int pageNumber) {
        log.info("======== {} PAGE TYPE {} =======", globalStyle.hashCode(), pageTypeId);
        PPTBlockContext.init();
        try {
            if (pageTypeId == BIG_TITLE_PAGE_ID) {
                return buildBigTitlePage(globalStyle, ppt, bigTitlePageCount, pageNumber);
            }
            return buildNewPageByKeyWord(pageTypeId, globalStyle, ppt, bigTitlePageCount, pageNumber);
        } finally {
            PPTBlockContext.remove();
        }
    }

    private PPTShowSide buildBigTitlePage(GlobalStyle globalStyle,
                                          PPTShow ppt,
                                          int bigTitlePageCount,
                                          int pageNumber) {
        if (Objects.isNull(globalStyle.getBigTitlePage())) {
            globalStyle.setBigTitlePage(buildNewPageByKeyWord(BIG_TITLE_PAGE_ID,
                    globalStyle, ppt, bigTitlePageCount, pageNumber));
        }
        return copyAndChooseNumber(globalStyle.getBigTitlePage(), bigTitlePageCount);
    }

    private PPTShowSide copyAndChooseNumber(PPTShowSide bigTitlePage, int bigTitlePageCount) {
        return bigTitleChooseNumberHandle.replaceNumber(bigTitlePage, bigTitlePageCount);
    }

    private PPTShowSide buildNewPageByKeyWord(Integer pageTypeId, GlobalStyle globalStyle, PPTShow ppt,
                                              int bigTitlePageCount, int putNumber) {
        //找到元素组合
        PPTRegionPut pptRegionPut = randPPTRegions(pageTypeId, globalStyle, ppt, putNumber);
        List<PPTRegion> pptRegions = pptRegionService.queryRegionsByBlock(pptRegionPut);
        PPTRegionGroup pptRegionGroup = PPTRegionGroup.buildPPTRegionGroup(pptRegions, pptPageModelService.findAll());
        List<PPTElement> pptElements = randPPTElementsByRegions(pptRegions, pptRegionGroup, globalStyle);
        long id = saveFileElements(globalStyle, pageTypeId, pptElements, pptRegionPut);
        PPTPageBuildQo pptPageBuildQo = new PPTPageBuildQo();
        pptPageBuildQo.setPageTypeId(pageTypeId);
        pptPageBuildQo.setPptElements(pptElements);
        pptPageBuildQo.setGlobalStyle(globalStyle);
        pptPageBuildQo.setPptRegionPut(pptRegionPut);
        pptPageBuildQo.setBuildContext(PPTBlockContext.getBuildContext());
        pptPageBuildQo.setColorContext(PPTBlockContext.getColorContext());
        pptPageBuildQo.setBigTitlePageCount(bigTitlePageCount);
        PPTShowSide pptShowSide = pptPageBuild(pptPageBuildQo);
        //pptShowSide.getElements().add(debugBuildId(pageTypeId, id));
        return pptShowSide;
    }

    private PPTElement debugBuildId(Integer pageTypeId, long id) {
        PPTInnerText innerText = PPTInnerText.build("[" + pageTypeId + "]DataId:" + id);
        innerText.setCss(PPTInnerTextCss.build().setFontSize(12).setColor("FF0000"));
        PPTText pptText = PPTText.build(PPTInnerLine.build(Lists.newArrayList(innerText)));
        if (pageTypeId.equals(TITLE_PAGE_ID)) {
            pptText.setCss(PPTTextCss.build().setTop(1).setHeight(1).setWidth(4.5).setLeft(0));
        } else {
            pptText.setCss(PPTTextCss.build().setTop(0).setHeight(1).setWidth(4.5).setLeft(0));
        }
        return pptText;
    }

    @Override
    public PPTShowSide pptPageBuild(PPTPageBuildQo pptPageBuildQo) {
        PPTBlockContext.setBuildContext(pptPageBuildQo.getBuildContext());
        PPTBlockContext.setColorContext(pptPageBuildQo.getColorContext());
        GlobalStyle globalStyle = pptPageBuildQo.getGlobalStyle();
        PPTShowSide pptShowSide = PPTShowSide.build();
        List<PPTElement> pptElements = designHandle.design(pptPageBuildQo.getPptElements(),
                globalStyle, pptPageBuildQo.getPageTypeId(), pptShowSide, pptPageBuildQo.getPptRegionPut(),
                pptPageBuildQo.getBigTitlePageCount());
        List<PPTElement> pageTitle = Lists.newArrayList();
        if (pptPageTypeService.isPageNeedTitle(pptPageBuildQo.getPageTypeId())) {
            pageTitle = pptPageBuildQo.getGlobalStyle().getTitleElements();
        }
        if (pptPageBuildQo.getPageTypeId() != TITLE_PAGE_ID) {
            pptElements.addAll(pageTitle);
        }
        pptShowSide.addAll(pptElements);
        return pptShowSide;
    }

    private Long saveFileElements(GlobalStyle globalStyle,
                                  Integer pageTypeId,
                                  List<PPTElement> pptElements,
                                  PPTRegionPut pptRegionPut) {
        PPTFileData pptFileData = new PPTFileData();
        pptFileData.setUuid(globalStyle.getUuid());
        pptFileData.setGlobalStyle(JSON.toJSONString(SerializableGlobalStyle.buildByGlobalStyle(globalStyle)));
        pptFileData.setElements(SerializablePPTElement.toJson(pptElements));
        pptFileData.setPageId(pageTypeId);
        pptFileData.setPptRegionPut(JSON.toJSONString(pptRegionPut));
        pptFileData.setBuildContext(JSON.toJSONString(PPTBlockContext.getBuildContext()));
        pptFileData.setColorContext(JSON.toJSONString(PPTBlockContext.getColorContext()));
        pptFileDataService.insert(pptFileData);
        return pptFileData.getId();
    }

    private List<PPTElement> randPPTElementsByRegions(List<PPTRegion> pptRegions,
                                                      PPTRegionGroup pptRegionGroup,
                                                      GlobalStyle globalStyle) {
        List<PPTRegion> regionsWithoutNull = filterNullRegion(pptRegions);
        List<PPTElement> elements = Lists.newArrayList();
        for (PPTRegion pptRegion : regionsWithoutNull) {
            BuilderQo builderQo = new BuilderQo().setPptRegion(pptRegion)
                    .setPptRegionGroup(pptRegionGroup)
                    .setLeftSize(FROM_SIZE)
                    .setTopSize(FROM_SIZE)
                    .setGlobalStyle(globalStyle);
            List<PPTElement> elementList = buildPPTElementsByRegion(builderQo);
            elements.addAll(elementList);
        }
        return elements;
    }

    private List<PPTRegion> filterNullRegion(List<PPTRegion> pptRegions) {
        return pptRegions.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<PPTElement> buildPPTElementsByRegion(BuilderQo builderQo) {
        PPTRegion pptRegion = builderQo.getPptRegion();
        Position position = builderQo.getPptRegionGroup().findPosition(pptRegion);
        PPTPageModel pageModel = pptPageModelService.findById(pptRegion.getPptModelId());
        if (pageModel.getFillIcon() == 1) {
            return fillByIcon(builderQo);
        }
        if (pageModel.getFillLogo() == 1) {
            return fillByLogo(builderQo);
        }
        BuilderQo cloneQo = builderQo.clone().setPosition(position).setPageModel(pageModel);
        PPTBlockPut pptBlockPut = findPPTRegionBlockPut(cloneQo);
        cloneQo.setPptBlockPut(pptBlockPut)
                .setLeftSize(builderQo.getLeftSize() + pptRegion.getLeftSize())
                .setTopSize(builderQo.getTopSize() + pptRegion.getTopSize());
        return buildPPTElementsByPPTBlockPut(cloneQo);
    }

    private PPTBlockPut findPPTRegionBlockPut(BuilderQo builderQo) {
        log.info("[findPPTRegionBlockPut] region: {}", builderQo.getPptRegion().getId());
        PPTBlockPut pptBlockPut = pptBlockPutService
                .selectByRegionIds(Long.toString(builderQo.getPptRegion().getId())).stream()
                .findFirst()
                .orElse(null);
        builderQo.setOriginalBlockPut(pptBlockPut);
        //可以使用替代元素
        if (builderQo.getPageModel().getNoSubstitution() == 0) {
            pptBlockPut = findBlockByRegionId(builderQo);
        } else {
            //如果必须使用原始元素，就不用随机替换规则了
            Assert.notNull(pptBlockPut,
                    "系统不存在pptRegion={}的原始实现", builderQo.getPptRegion().getId());
        }
        Assert.notNull(pptBlockPut, "此处PPTBlock不可以为null");
        return pptBlockPut;
    }

    private PPTBlockPut findBlockByRegionId(BuilderQo builderQo) {
        PPTBlockPut needSameBlockPut = builderQo.getPptRegionGroup().needSameWith(builderQo.getPptRegion());
        if (Objects.nonNull(needSameBlockPut)) {
            return needSameBlockPut;
        }
        List<PPTBlockPut> blockPuts = findCanUseBlockPut(builderQo.getPptRegion(), builderQo.getPptRegionGroup());
        //强制过滤项
        List<RegionFilter> forceFilters = filterForceFilters(regionFilters);
        for (RegionFilter forceFilter : forceFilters) {
            log.info("[forceFilter过滤] forceFilter: {}, 过滤前元素个数: {}", getFilterName(forceFilter), blockPuts.size());
            blockPuts = forceFilter.filterPPTBlockPut(new RegionFilterQo(blockPuts, builderQo));
            log.info("[forceFilter过滤] forceFilter: {}, 过滤后元素个数: {}", getFilterName(forceFilter), blockPuts.size());
        }
        //非强制过滤项
        List<RegionFilter> notForceFilters = filterNotForceFilters(regionFilters);
        for (RegionFilter notForceFilter : notForceFilters) {
            List<PPTBlockPut> copyBlockPuts = Lists.newArrayList(blockPuts);
            List<PPTBlockPut> filterBlockPuts = notForceFilter.filterPPTBlockPut(new RegionFilterQo(copyBlockPuts, builderQo));
            if (CollectionUtils.isNotEmpty(filterBlockPuts)) {
                blockPuts = filterBlockPuts;
            } else {
                break;
            }
        }
        PPTBlockPut pptBlockPut = RandUtil.randElement(blockPuts);
        builderQo.getPptRegionGroup().addInBlockPutMap(builderQo.getPptRegion(), pptBlockPut);
        return pptBlockPut;
    }

    @NotNull
    private String getFilterName(RegionFilter notForceFilter) {
        return notForceFilter.getClass().getAnnotation(ForRegion.class).name();
    }

    @NotNull
    private List<PPTBlockPut> findCanUseBlockPut(PPTRegion pptRegion, PPTRegionGroup pptRegionGroup) {
        List<PPTBlockPut> pptBlockPuts = findBlockPutListByRegion(pptRegion);
        List<PPTBlockPut> newBlockPuts = Lists.newArrayList(pptBlockPuts);
        newBlockPuts.removeAll(pptRegionGroup.canNotSameWith(pptRegion));
        return newBlockPuts;
    }

    private List<RegionFilter> filterForceFilters(List<RegionFilter> regionFilters) {
        return regionFilters.stream()
                .filter(filter -> filter.getClass().getAnnotation(ForRegion.class).force())
                .collect(Collectors.toList());
    }

    private List<RegionFilter> filterNotForceFilters(List<RegionFilter> regionFilters) {
        return regionFilters.stream()
                .filter(filter -> !filter.getClass().getAnnotation(ForRegion.class).force())
                .collect(Collectors.toList());
    }

    private List<PPTBlockPut> findBlockPutListByRegion(PPTRegion pptRegion) {
        List<PPTRegion> regions = findModelRegion(pptRegion);
        List<Long> ids = regions.stream().map(PPTRegion::getId).collect(Collectors.toList());
        ids.add(-1L);
        log.info("[findBlockPutListByRegion] ids:{}", ids);
        return pptBlockPutService.selectByRegionIds(Joiner.on(",").join(ids));
    }

    private List<PPTRegion> findModelRegion(PPTRegion pptRegion) {
        List<PPTRegion> findRegions =
                pptRegionMapper.selectNearlyRegionByModel(pptRegion.getPptModelId(),
                        pptRegion.getWidthSize(), pptRegion.getHeightSize());
        if (CollectionUtils.isEmpty(findRegions)) {
            log.error("[查询内容为空] pptRegion：{}", pptRegion);
        }
        return findRegions;
    }

    private List<PPTElement> buildPPTElementsByPPTBlockPut(BuilderQo builderQo) {
        List<PPTBlock> pptBlocks = pptBlockService.findByPPTBlockPut(builderQo.getPptBlockPut());
        List<PPTElement> pptElements = Lists.newArrayList();
        for (PPTBlock pptBlock : pptBlocks) {
            BuilderQo cloneQo = builderQo.clone().setPptBlock(pptBlock);
            List<PPTElement> elements = buildPPTElementsByPPTBlock(cloneQo);
            pptElements.addAll(elements);
            if (CollectionUtils.isNotEmpty(elements) && elements.size() == 1) {
                PPTBlockContext.saveBuildLog(builderQo.getPptBlockPut(), pptBlock, elements.get(0));
            }
        }
        return pptElements;
    }

    private List<PPTElement> buildPPTElementsByPPTBlock(BuilderQo builderQo) {
        PPTBlockType type = PPTBlockType.findByCode(builderQo.getPptBlock().getPptBlockType());
        if (type.equals(PPTBlockType.REGION)) {
            return buildRecursion(builderQo);
        }
        ElementBuilder elementBuilder = elementBuilders.stream()
                .filter(builder -> builder.canBuildTypes().contains(type))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        Assert.notNull(elementBuilder, "当前的属性无法匹配对应生成器，属性："
                + builderQo.getPptBlock().getPptBlockType());
        return elementBuilder.buildElement(builderQo);
    }

    /**
     * Region对象实现中存在嵌套
     * 在这个方法里面组合出来参数进行递归调用
     */
    private List<PPTElement> buildRecursion(BuilderQo builderQo) {
        PPTBlock block = builderQo.getPptBlock();
        PPTRegion pptRegion = pptRegionService.queryById(block.getRegionId());
        pptRegion.setLeftSize(block.getLeftSize());
        pptRegion.setTopSize(block.getTopSize());
        BuilderQo cloneQo = builderQo.clone().setPptRegion(pptRegion);
        return buildPPTElementsByRegion(cloneQo);
    }

    private PPTRegionPut randPPTRegions(Integer pageTypeId, GlobalStyle globalStyle, PPTShow ppt, Integer pageNum) {
        Assert.notNull(pageTypeId, "待查询pageTypeId不能是空的");
        List<PPTRegionPut> regionPuts = pptRegionPutService.queryByPageId(pageTypeId);
        Assert.isTrue(CollectionUtils.isNotEmpty(regionPuts),
                "系统不存在pageTypeId=" + pageTypeId + "相关内容！");
        //强制过滤项
        List<PageFilter> forceFilters = findForceFilters(pageTypeId);
        log.info("[forceFilters] pageTypeId:{} forceFilters: {}", pageTypeId, forceFilters);
        for (PageFilter filter : forceFilters) {
            log.info("[before filter] {}, regionPuts:{}", filter.getClass().getName(),
                    regionPuts.stream().map(PPTRegionPut::getId).collect(Collectors.toList()));
            regionPuts = filter.filterPPTRegionPut(regionPuts, globalStyle, ppt);
            log.info("[after filter] {}, regionPuts:{}", filter.getClass().getName(),
                    regionPuts.stream().map(PPTRegionPut::getId).collect(Collectors.toList()));
            Assert.isTrue(CollectionUtils.isNotEmpty(regionPuts),
                    "在" + filter.getClass().getSimpleName() + "过滤后出现全部元素都被过滤掉，所以无法继续合成文件");
        }
        //非强制过滤项
        List<PageFilter> notForceFilters = findNotForceFilters(pageTypeId);
        log.info("[notForceFilters] pageTypeId:{} notForceFilters: {}", pageTypeId, notForceFilters);
        for (PageFilter filter : notForceFilters) {
            if (CollectionUtils.isEmpty(regionPuts) || regionPuts.size() == 1) {
                break;
            }
            List<PPTRegionPut> filterRegionPuts = filter.filterPPTRegionPut(Lists.newArrayList(regionPuts), globalStyle, ppt);
            if (CollectionUtils.isNotEmpty(filterRegionPuts)) {
                regionPuts = filterRegionPuts;
            }
        }
        PPTRegionPut pptRegionPut = RandUtil.randElement(regionPuts);
        //在公共样式里面声明这一页已经被添加为的内容
        globalStyle.getRegionPuts().put(pageNum, pptRegionPut);
        return pptRegionPut;
    }

    private List<PageFilter> findNotForceFilters(Integer pageTypeId) {
        return pageFilters.stream()
                .filter(pageFilter -> !pageFilter.getClass().getAnnotation(ForPage.class).force())
                .filter(pageFilter -> isContainsPage(pageFilter, pageTypeId))
                .collect(Collectors.toList());
    }

    private List<PageFilter> findForceFilters(Integer pageTypeId) {
        return pageFilters.stream()
                .filter(pageFilter -> pageFilter.getClass().getAnnotation(ForPage.class).force())
                .filter(pageFilter -> isContainsPage(pageFilter, pageTypeId))
                .collect(Collectors.toList());
    }

    private boolean isContainsPage(PageFilter filter, Integer pageTypeId) {
        PPTPage[] pptPages = filter.getClass().getAnnotation(ForPage.class).onlyInPage();
        return pptPages.length == 0 || Arrays.stream(pptPages).anyMatch(page -> page.getId() == pageTypeId);
    }

    private List<PPTElement> fillByIcon(BuilderQo builderQo) {
        PPTRegion pptRegion = builderQo.getPptRegion();
        log.info("[pptRegion icon]:{}, left:{}, top:{}", pptRegion, builderQo.getLeftSize(), builderQo.getTopSize());
        double left = pptRegion.getLeftSize() + builderQo.getLeftSize();
        double top = pptRegion.getTopSize() + builderQo.getTopSize();
        double leftMiddle = left + (pptRegion.getWidthSize() / 2);
        double topMiddle = top + (pptRegion.getHeightSize() / 2);
        double size = (pptRegion.getWidthSize() + pptRegion.getHeightSize()) / 2;
        PPTShape pptShape = new PPTShape(PPTShapeCss.build()
                .setHeight(size)
                .setWidth(size)
                .setLeft(leftMiddle - (size / 2))
                .setTop(topMiddle - (size / 2))
                .setName(ICON));
        log.info("[pptShape icon]:{}", pptShape.getCss());
        return Lists.newArrayList(pptShape);
    }

    private List<PPTElement> fillByLogo(BuilderQo builderQo) {
        PPTRegion pptRegion = builderQo.getPptRegion();
        log.info("[pptRegion logo]:{}, left:{}, top:{}", pptRegion, builderQo.getLeftSize(), builderQo.getTopSize());
        PPTShape pptShape = new PPTShape(PPTShapeCss.build()
                .setHeight(pptRegion.getHeightSize())
                .setWidth(pptRegion.getWidthSize())
                .setLeft(pptRegion.getLeftSize())
                .setTop(pptRegion.getTopSize())
                .setName(LOGO));
        log.info("[pptShape logo]:{}", pptShape.getCss());
        return Lists.newArrayList(pptShape);
    }
}
