package cc.pptshow.build.pptbuilder.domain;

import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.bean.PPTPageModel;
import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import cc.pptshow.build.pptbuilder.biz.helper.LayerHelper;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.enums.GroupType;
import cc.pptshow.build.pptbuilder.domain.enums.Position;
import cc.pptshow.build.pptbuilder.exception.PPTBuildException;
import cc.pptshow.build.pptbuilder.util.MathUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Slf4j
public class PPTRegionGroup {

    /**
     * 分组细节，同组元素应当具有相同实现
     */
    private Map<List<PPTRegion>, GroupType> regionGroupTypeMap = Maps.newHashMap();

    /**
     * 方位元素，执行元素应该是什么方位的
     */
    private Map<PPTRegion, Position> regionPositionMap = Maps.newHashMap();

    /**
     * 类型和实现的对应关系
     */
    private Map<PPTRegion, PPTBlockPut> regionPutMap = Maps.newHashMap();

    private static final double NEARLY_AREA_MULTIPLE = 0.1;
    /**
     * 最大误差比例，在计算是不是等比例排列的时候使用
     */
    private static final double MAX_ERROR_MULTIPLE = 0.1;
    private static final int MUST_REPLACE = 1;
    private static final int MUST_NOT_REPLACE = -1;

    public static PPTRegionGroup buildPPTRegionGroup(List<PPTRegion> modelRegions, List<PPTPageModel> pageModels) {
        PPTRegionGroup pptRegionGroup = new PPTRegionGroup();
        Map<Integer, List<PPTRegion>> modelRegionsMap = findModelMapByRegions(modelRegions);
        for (Integer modelId : modelRegionsMap.keySet()) {
            //如果是一个元素排列超过1次才有必要做实现差异优化
            if (modelRegionsMap.get(modelId).size() > 1) {
                List<PPTRegion> model = modelRegionsMap.get(modelId);
                findModelRegionsPosition(model, pageModels, pptRegionGroup);
            }
        }
        return pptRegionGroup;
    }

    public Position findPosition(PPTRegion pptRegion) {
        return Optional.ofNullable(regionPositionMap.get(pptRegion)).orElse(Position.NULL);
    }

    /**
     * 确定在一组内容中应该的位置排序
     */
    private static void findModelRegionsPosition(List<PPTRegion> regions,
                                                 List<PPTPageModel> pageModels,
                                                 PPTRegionGroup pptRegionGroup) {
        List<List<PPTRegion>> regionGroups = groupBySize(regions);
        for (List<PPTRegion> nearlyRegions : regionGroups) {
            if (isOnlyOneElement(nearlyRegions)) {
                pptRegionGroup.saveBuildSameRegion(nearlyRegions);
            }
            if (isMustSameRegion(nearlyRegions, pageModels)) {
                pptRegionGroup.saveBuildSameRegion(nearlyRegions);
            } else if (isMustNotSameRegion(nearlyRegions, pageModels)) {
                pptRegionGroup.saveNotSameRegion(nearlyRegions);
            } else if (isThreeRegion(nearlyRegions)) {
                pptRegionGroup.saveIntervalSameRegion(nearlyRegions);
            } else {
                if (isHaveSameInterval(nearlyRegions)) {
                    pptRegionGroup.saveBuildSameRegion(nearlyRegions);
                } else if (isMaybeLeftAndRight(nearlyRegions)) {
                    pptRegionGroup.saveLeftAndRightRegion(nearlyRegions);
                } else if (isMaybeUpAndDown(nearlyRegions)) {
                    pptRegionGroup.saveUpAndDownRegion(nearlyRegions);
                } else {
                    pptRegionGroup.saveBuildNormalRegion(nearlyRegions);
                }
            }
        }
    }

    private void saveIntervalSameRegion(List<PPTRegion> nearlyRegions) {
        PPTRegion middle = findMiddleRegion(nearlyRegions);
        nearlyRegions.remove(middle);
        regionGroupTypeMap.put(nearlyRegions, GroupType.SAME);
    }

    private PPTRegion findMiddleRegion(List<PPTRegion> nearlyRegions) {
        assert nearlyRegions.size() == 3;
        List<Double> tops = nearlyRegions.stream().map(PPTRegion::getTopSize).collect(Collectors.toList());
        List<Double> lefts = nearlyRegions.stream().map(PPTRegion::getLeftSize).collect(Collectors.toList());
        if (MathUtil.variance(tops) > MathUtil.variance(lefts)) {
            nearlyRegions.sort(Comparator.comparingDouble(PPTRegion::getTopSize));
        } else {
            nearlyRegions.sort(Comparator.comparingDouble(PPTRegion::getLeftSize));
        }
        return nearlyRegions.get(1);
    }

    private static boolean isThreeRegion(List<PPTRegion> nearlyRegions) {
        if (nearlyRegions.size() != 3) {
            return false;
        }
        double left = nearlyRegions.stream().map(PPTRegion::getLeftSize).mapToDouble(Double::new).min().orElse(0D);
        double top = nearlyRegions.stream().map(PPTRegion::getTopSize).mapToDouble(Double::new).min().orElse(0D);
        double right = nearlyRegions.stream().map(p -> (p.getLeftSize() + p.getWidthSize())).mapToDouble(Double::new).min().orElse(0D);
        double bottom = nearlyRegions.stream().map(p -> (p.getTopSize() + p.getHeightSize())).mapToDouble(Double::new).min().orElse(0D);
        CanvasShape shape = new CanvasShape();
        shape.setLeft(left);
        shape.setTop(top);
        shape.setBottom(bottom);
        shape.setRight(right);
        return shape.area() > BConstant.PAGE_WIDTH * BConstant.PAGE_HEIGHT * 0.5;
    }

    private void saveBuildNormalRegion(List<PPTRegion> nearlyRegions) {
        for (PPTRegion nearlyRegion : nearlyRegions) {
            regionPositionMap.put(nearlyRegion, Position.NULL);
        }
        regionGroupTypeMap.put(nearlyRegions, GroupType.NORMAL);
    }

    private void saveUpAndDownRegion(List<PPTRegion> nearlyRegions) {
        double middle = findUpAndDownMiddle(nearlyRegions);
        List<PPTRegion> topElements = findTopElements(nearlyRegions, middle);
        List<PPTRegion> bottomElements = findBottomElements(nearlyRegions, middle);
        for (PPTRegion topElement : topElements) {
            regionPositionMap.put(topElement, Position.TOP);
        }
        for (PPTRegion bottomElement : bottomElements) {
            regionPositionMap.put(bottomElement, Position.BOTTOM);
        }
        regionGroupTypeMap.put(topElements, GroupType.SAME);
        regionGroupTypeMap.put(bottomElements, GroupType.SAME);
    }

    private void saveLeftAndRightRegion(List<PPTRegion> nearlyRegions) {
        double middle = findLeftAndRightMiddle(nearlyRegions);
        List<PPTRegion> leftElements = findLeftElements(nearlyRegions, middle);
        List<PPTRegion> rightElements = findRightElements(nearlyRegions, middle);
        for (PPTRegion leftElement : leftElements) {
            regionPositionMap.put(leftElement, Position.LEFT);
        }
        for (PPTRegion rightElement : rightElements) {
            regionPositionMap.put(rightElement, Position.RIGHT);
        }
        regionGroupTypeMap.put(leftElements, GroupType.SAME);
        regionGroupTypeMap.put(rightElements, GroupType.SAME);
    }

    private static boolean isMaybeUpAndDown(List<PPTRegion> nearlyRegions) {
        if (CollectionUtils.isEmpty(nearlyRegions)) {
            return false;
        }
        double middle = findUpAndDownMiddle(nearlyRegions);
        boolean noMiddleElement = nearlyRegions.stream()
                .noneMatch(r -> r.getTopSize() < middle && r.getTopSize() + r.getHeightSize() > middle);
        List<PPTRegion> topElements = findTopElements(nearlyRegions, middle);
        List<PPTRegion> bottomElements = findBottomElements(nearlyRegions, middle);
        if (topElements.size() == nearlyRegions.size() || bottomElements.size() == nearlyRegions.size()) {
            log.error("[isMaybeUpAndDown] nearlyRegions:{}, top:{}, bottom: {}", nearlyRegions, topElements, bottomElements);
            throw new PPTBuildException("死循环提醒！构建出现错误！");
        }
        return noMiddleElement && !isMaybeUpAndDown(topElements) && !isMaybeUpAndDown(bottomElements);
    }

    private static List<PPTRegion> findBottomElements(List<PPTRegion> nearlyRegions, double middle) {
        return nearlyRegions.stream()
                .filter(r -> r.getTopSize() > middle)
                .collect(Collectors.toList());
    }

    private static List<PPTRegion> findTopElements(List<PPTRegion> nearlyRegions, double middle) {
        return nearlyRegions.stream()
                .filter(r -> r.getTopSize() + r.getHeightSize() < middle)
                .collect(Collectors.toList());
    }

    private static double findUpAndDownMiddle(List<PPTRegion> nearlyRegions) {
        Double topMost = nearlyRegions.stream()
                .map(PPTRegion::getTopSize)
                .min(Double::compare)
                .orElse(0D);
        Double bottomMost = nearlyRegions.stream()
                .map(r -> r.getTopSize() + r.getHeightSize())
                .max(Double::compare)
                .orElse(0D);
        return (bottomMost - topMost) / 2 + topMost;
    }

    private static boolean isMaybeLeftAndRight(List<PPTRegion> nearlyRegions) {
        if (CollectionUtils.isEmpty(nearlyRegions)) {
            return false;
        }
        double middle = findLeftAndRightMiddle(nearlyRegions);
        boolean noMiddleElement = nearlyRegions.stream()
                .noneMatch(r -> r.getLeftSize() < middle && r.getLeftSize() + r.getWidthSize() > middle);
        List<PPTRegion> leftElements = findLeftElements(nearlyRegions, middle);
        List<PPTRegion> rightElements = findRightElements(nearlyRegions, middle);
        return noMiddleElement && !isMaybeLeftAndRight(leftElements) && !isMaybeLeftAndRight(rightElements);
    }

    private static List<PPTRegion> findRightElements(List<PPTRegion> nearlyRegions, double middle) {
        return nearlyRegions.stream()
                .filter(r -> r.getLeftSize() > middle)
                .collect(Collectors.toList());
    }

    private static List<PPTRegion> findLeftElements(List<PPTRegion> nearlyRegions, double middle) {
        return nearlyRegions.stream()
                .filter(r -> r.getLeftSize() + r.getWidthSize() < middle)
                .collect(Collectors.toList());
    }

    private static double findLeftAndRightMiddle(List<PPTRegion> nearlyRegions) {
        Double leftmost = nearlyRegions.stream()
                .map(PPTRegion::getLeftSize)
                .min(Double::compare)
                .orElse(0D);
        Double rightmost = nearlyRegions.stream()
                .map(r -> r.getLeftSize() + r.getWidthSize())
                .max(Double::compare)
                .orElse(0D);
        return (rightmost - leftmost) / 2 + leftmost;
    }

    private static boolean isHaveSameInterval(List<PPTRegion> nearlyRegions) {
        if (CollectionUtils.isEmpty(nearlyRegions) || nearlyRegions.size() <= 2) {
            return true;
        }
        nearlyRegions.sort((region1, region2) -> {
            int compare = Double.compare(region1.getLeftSize(), region2.getLeftSize());
            if (compare != 0) {
                return compare;
            } else {
                return Double.compare(region1.getTopSize(), region2.getTopSize());
            }
        });
        List<Double> horizontalDiffSizes = Lists.newArrayList();
        List<Double> verticalDiffSizes = Lists.newArrayList();
        for (int i = 1; i < nearlyRegions.size(); i++) {
            horizontalDiffSizes.add(nearlyRegions.get(i).getLeftSize() - nearlyRegions.get(i - 1).getWidthSize());
            verticalDiffSizes.add(nearlyRegions.get(i).getTopSize() - nearlyRegions.get(i - 1).getTopSize());
        }
        double horizontalAvg = horizontalDiffSizes.stream().collect(Collectors.averagingDouble(s -> s));
        double verticalAvg = verticalDiffSizes.stream().collect(Collectors.averagingDouble(s -> s));
        double maxHorizontalDiff = nearlyRegions.get(0).getLeftSize() * MAX_ERROR_MULTIPLE;
        double maxVerticalDiff = nearlyRegions.get(0).getHeightSize() * MAX_ERROR_MULTIPLE;
        boolean isHorizontalDeviation = isHaveDiffMoreThanMax(horizontalDiffSizes, horizontalAvg, maxHorizontalDiff);
        boolean isVerticalDeviation = isHaveDiffMoreThanMax(verticalDiffSizes, verticalAvg, maxVerticalDiff);
        return !isHorizontalDeviation && !isVerticalDeviation;
    }

    private static boolean isHaveDiffMoreThanMax(List<Double> horizontalDiffSizes, double horizontalAvg, double maxHorizontalDiff) {
        return horizontalDiffSizes.stream()
                .map(s -> Math.abs(Math.abs(s) - Math.abs(horizontalAvg)))
                .anyMatch(s -> s > maxHorizontalDiff);
    }

    private void saveNotSameRegion(List<PPTRegion> nearlyRegions) {
        for (PPTRegion nearlyRegion : nearlyRegions) {
            regionPositionMap.put(nearlyRegion, Position.NULL);
        }
        regionGroupTypeMap.put(nearlyRegions, GroupType.NOT_SAME);
    }

    private static boolean isMustNotSameRegion(List<PPTRegion> nearlyRegions, List<PPTPageModel> pageModels) {
        if (CollectionUtils.isEmpty(nearlyRegions)) {
            return false;
        }
        PPTPageModel pageModel = findPPTModel(nearlyRegions, pageModels);
        return pageModel.getNeedRepeat().equals(MUST_NOT_REPLACE);
    }

    private static boolean isMustSameRegion(List<PPTRegion> nearlyRegions, List<PPTPageModel> pageModels) {
        if (CollectionUtils.isEmpty(nearlyRegions)) {
            return false;
        }
        PPTPageModel pageModel = findPPTModel(nearlyRegions, pageModels);
        return pageModel.getNeedRepeat().equals(MUST_REPLACE);
    }

    private static PPTPageModel findPPTModel(List<PPTRegion> nearlyRegions, List<PPTPageModel> pageModels) {
        Integer pptModelId = nearlyRegions.get(0).getPptModelId();
        return pageModels.stream()
                .filter(p -> p.getId().equals(pptModelId))
                .findFirst()
                .orElseThrow(PPTBuildException::new);
    }

    private void saveBuildSameRegion(List<PPTRegion> nearlyRegions) {
        for (PPTRegion nearlyRegion : nearlyRegions) {
            regionPositionMap.put(nearlyRegion, Position.NULL);
        }
        regionGroupTypeMap.put(nearlyRegions, GroupType.SAME);
    }

    private static boolean isOnlyOneElement(List<PPTRegion> nearlyRegions) {
        return CollectionUtils.isNotEmpty(nearlyRegions) && nearlyRegions.size() == 1;
    }

    private static List<List<PPTRegion>> groupBySize(List<PPTRegion> regions) {
        List<List<PPTRegion>> regionGroups = Lists.newArrayList();
        for (PPTRegion region : regions) {
            boolean contains = regionGroups.stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())
                    .contains(region);
            if (contains) {
                continue;
            }
            double area = region.getWidthSize() * region.getHeightSize();
            List<PPTRegion> pptRegions = regions.stream().filter(r -> {
                double thisArea = r.getWidthSize() * r.getHeightSize();
                return thisArea >= area * (1 - NEARLY_AREA_MULTIPLE) && thisArea <= area * (1 + NEARLY_AREA_MULTIPLE);
            }).collect(Collectors.toList());
            regionGroups.add(pptRegions);
        }
        return regionGroups;
    }

    private static Map<Integer, List<PPTRegion>> findModelMapByRegions(List<PPTRegion> modelRegions) {
        Map<Integer, List<PPTRegion>> modelRegionsMap = Maps.newHashMap();
        for (PPTRegion pptRegion : modelRegions) {
            if (CollectionUtils.isEmpty(modelRegionsMap.get(pptRegion.getPptModelId()))) {
                modelRegionsMap.put(pptRegion.getPptModelId(), Lists.newArrayList());
            }
            modelRegionsMap.get(pptRegion.getPptModelId()).add(pptRegion);
        }
        return modelRegionsMap;
    }


    public PPTBlockPut needSameWith(PPTRegion pptRegion) {
        for (List<PPTRegion> regions : regionGroupTypeMap.keySet()) {
            if (regions.contains(pptRegion)) {
                GroupType groupType = regionGroupTypeMap.get(regions);
                if (groupType.equals(GroupType.SAME)) {
                    PPTRegion oldRegion = regions.stream()
                            .filter(r -> regionPutMap.containsKey(r))
                            .findFirst()
                            .orElse(null);
                    return regionPutMap.get(oldRegion);
                } else if (groupType.equals(GroupType.NORMAL)) {
                    List<PPTRegion> pptRegionList = regionPutMap.keySet()
                            .stream().filter(r -> r.getPptModelId().equals(pptRegion.getPptModelId()))
                            .collect(Collectors.toList());
                    for (PPTRegion region : pptRegionList) {
                        if (region.getWidthSize().equals(pptRegion.getWidthSize())
                                && region.getHeightSize().equals(pptRegion.getHeightSize())) {
                            log.info("[存在region的实现] pptRegionId:{}", pptRegion.getId());
                            return regionPutMap.get(region);
                        }
                    }
                }
            }
        }
        return null;
    }

    public Collection<PPTBlockPut> canNotSameWith(PPTRegion pptRegion) {
        List<List<PPTRegion>> notSameList = regionGroupTypeMap.keySet().stream()
                .filter(map -> regionGroupTypeMap.get(map).equals(GroupType.NOT_SAME))
                .collect(Collectors.toList());
        for (List<PPTRegion> regions : notSameList) {
            if (regions.contains(pptRegion)) {
                return regionPutMap.keySet().stream()
                        .filter(regions::contains)
                        .map(r -> regionPutMap.get(r))
                        .collect(Collectors.toList());
            }
        }
        return Lists.newArrayList();
    }

    public void addInBlockPutMap(PPTRegion pptRegion, PPTBlockPut pptBlockPut) {
        regionPutMap.put(pptRegion, pptBlockPut);
    }
}
