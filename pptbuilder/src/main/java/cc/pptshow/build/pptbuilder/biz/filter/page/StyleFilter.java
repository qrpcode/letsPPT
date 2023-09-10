package cc.pptshow.build.pptbuilder.biz.filter.page;

import cc.pptshow.build.pptbuilder.anno.ForPage;
import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.service.PPTBlockPutService;
import cc.pptshow.build.pptbuilder.service.PPTBlockService;
import cc.pptshow.build.pptbuilder.service.PPTRegionService;
import cc.pptshow.build.pptbuilder.util.Safes;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTText;
import cc.pptshow.ppt.show.PPTShow;
import cc.pptshow.ppt.show.PPTShowSide;
import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.constant.BConstant.*;

/**
 * 大标题页面需要和首页近似
 * 主要考察的地方就是文字密度区域
 * 使用指定的算法获取到文字密度相对近似的PPT模板
 */
@Service
@ForPage(name = "风格化过滤", onlyInPage = PPTPage.BIG_TITLE)
public class StyleFilter implements PageFilter {

    @Resource
    private PPTRegionService pptRegionService;

    @Resource
    private PPTBlockPutService pptBlockPutService;

    @Resource
    private PPTBlockService pptBlockService;

    @Override
    public List<PPTRegionPut> filterPPTRegionPut(List<PPTRegionPut> regionPuts,
                                                 GlobalStyle globalStyle,
                                                 PPTShow pptShow) {
        List<PPTShowSide> sides = pptShow.getSides();
        PPTShowSide homeSide = sides.stream().findFirst().orElse(null);
        if (Objects.isNull(homeSide)) {
            return regionPuts;
        }
        return filterByHome(regionPuts, homeSide);
    }

    private List<PPTRegionPut> filterByHome(List<PPTRegionPut> regionPuts,
                                            PPTShowSide homeSide) {
        int[][] homeCoverArea = findCoverArea(homeSide.getElements());
        Map<PPTRegionPut, Integer> regionScoreMap = regionPuts.stream()
                .collect(Collectors.toMap(r -> BeanUtil.copyProperties(r, PPTRegionPut.class),
                        put -> findPutScore(put, homeCoverArea)));
        return findScoreTop50(regionScoreMap);
    }

    private List<PPTRegionPut> findScoreTop50(Map<PPTRegionPut, Integer> regionScoreMap) {
        Set<Map.Entry<PPTRegionPut, Integer>> entries = regionScoreMap.entrySet();
        List<Map.Entry<PPTRegionPut, Integer>> entryList = entries.stream()
                .sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        return entryList.subList((int) Math.floor(entryList.size() / 2.0), entryList.size())
                .stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private int findPutScore(PPTRegionPut put, int[][] homeCoverArea) {
        List<PPTBlock> blocks = Lists.newArrayList();
        List<PPTBlock> pptBlocks = Lists.newArrayList();
        boolean first = true;
        while (first || !CollectionUtils.isEmpty(pptBlocks)) {
            first = false;
            blocks.addAll(findBlocksByRegionPut(put));
            pptBlocks = Safes.of(blocks).stream()
                    .filter(p -> StringUtils.equals(p.getPptBlockType(), PPTBlockType.REGION.getCode()))
                    .collect(Collectors.toList());
            List<String> ids = pptBlocks.stream()
                    .map(PPTBlock::getRegionId)
                    .map(Object::toString)
                    .collect(Collectors.toList());
            put.setPptRegionIds(JOINER.join(ids));
        }
        int[][] coverArray = buildCoverArrayByRegionPut(blocks);
        return overlapStatistics(coverArray, homeCoverArea);
    }

    private int overlapStatistics(int[][] coverArray, int[][] homeCoverArea) {
        int k = 0;
        for (int i = 0; i < coverArray.length; i++) {
            for (int j = 0; j < coverArray[0].length; j++) {
                if (coverArray[i][j] == 1 && homeCoverArea[i][j] == 1) {
                    k++;
                }
            }
        }
        return k;
    }

    private int[][] buildCoverArrayByRegionPut(List<PPTBlock> put) {
        List<PPTBlock> pptBlocks = put.stream()
                .filter(pptBlock -> StringUtils.equals(pptBlock.getPptBlockType(), PPTBlockType.TEXT.getCode()))
                .collect(Collectors.toList());
        int[][] coverAreaArray = new int[PAGE_WIDTH_UNIT][PAGE_HEIGHT_UNIT];
        for (PPTBlock pptBlock : pptBlocks) {
            coverArea(coverAreaArray, pptBlock.getLeftSize(), pptBlock.getTopSize(),
                    pptBlock.getWidthSize(), pptBlock.getHeightSize());
        }
        return coverAreaArray;
    }

    private List<PPTBlock> findBlocksByRegionPut(PPTRegionPut put) {
        List<PPTRegion> regions = pptRegionService.queryRegionsByBlock(put);
        List<Long> ids = regions.stream().map(PPTRegion::getId).collect(Collectors.toList());
        List<PPTBlockPut> blockPuts = pptBlockPutService.selectByRegionIds(JOINER.join(ids));
        List<Long> blockIds = Safes.of(blockPuts).stream()
                .map(PPTBlockPut::getPptBlockIds)
                .map(s -> Lists.newArrayList(SPLITTER.split(s)))
                .flatMap(Collection::stream)
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return pptBlockService.findByIds(blockIds);
    }

    private int[][] findCoverArea(List<PPTElement> elements) {
        int[][] coverAreaArray = new int[PAGE_WIDTH_UNIT][PAGE_HEIGHT_UNIT];
        List<PPTText> pptTexts = elements.stream()
                .filter(e -> e instanceof PPTText)
                .map(e -> (PPTText) e)
                .collect(Collectors.toList());
        for (PPTText pptText : pptTexts) {
            coverArea(coverAreaArray, pptText.getCss().getLeft(), pptText.getCss().getTop(),
                    pptText.getCss().getWidth(), pptText.getCss().getHeight());
        }
        return coverAreaArray;
    }

    private void coverArea(int[][] coverAreaArray, double left, double top, double width, double height) {
        int intLeft = (int) Math.ceil(left * 100);
        int intTop = (int) Math.ceil(top * 100);
        for (int i = 0; i <= width * 100; i++) {
            for (int j = 0; j <= height * 100; j++) {
                if (intLeft + i <= PAGE_WIDTH_UNIT - 1 && intTop + j <= PAGE_HEIGHT_UNIT - 1) {
                    coverAreaArray[intLeft + i][intTop + j] = 1;
                }
            }
        }
    }

}
