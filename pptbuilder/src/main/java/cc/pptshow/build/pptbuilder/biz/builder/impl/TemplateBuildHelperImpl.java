package cc.pptshow.build.pptbuilder.biz.builder.impl;

import cc.pptshow.build.pptbuilder.bean.PPTPageModel;
import cc.pptshow.build.pptbuilder.bean.PPTPageType;
import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import cc.pptshow.build.pptbuilder.biz.builder.TemplateBuildHelper;
import cc.pptshow.build.pptbuilder.domain.PPTTemplateElement;
import cc.pptshow.build.pptbuilder.domain.PPTTemplateGroup;
import cc.pptshow.build.pptbuilder.domain.PPTTemplatePage;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.vo.TemplateBuildQo;
import cc.pptshow.build.pptbuilder.domain.vo.TemplateNearlyVo;
import cc.pptshow.build.pptbuilder.service.PPTPageModelService;
import cc.pptshow.build.pptbuilder.service.PPTPageTypeService;
import cc.pptshow.build.pptbuilder.service.PPTRegionService;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TemplateBuildHelperImpl implements TemplateBuildHelper {

    private static final String NORMAL = "innerNormal";

    @Resource
    private PPTPageModelService pptPageModelService;

    @Resource
    private PPTPageTypeService pptPageTypeService;

    @Resource
    private PPTRegionService pptRegionService;

    @Override
    public List<TemplateNearlyVo> inferTemplate(TemplateBuildQo templateBuildQo) {
        List<TemplateNearlyVo> nearlyVos = Lists.newArrayList();
        nearlyVos.addAll(findNearlyGroupInThisPage(templateBuildQo));
        nearlyVos.addAll(findNearlyInDb(templateBuildQo));
        return filterRepeat(nearlyVos);
    }

    private List<TemplateNearlyVo> filterRepeat(List<TemplateNearlyVo> nearlyVos) {
        List<TemplateNearlyVo> returnNearlyVos = Lists.newArrayList();
        for (TemplateNearlyVo nearlyVo : nearlyVos) {
            if (!returnNearlyVos.contains(nearlyVo)) {
                returnNearlyVos.add(nearlyVo);
            }
        }
        return returnNearlyVos;
    }

    private List<TemplateNearlyVo> findNearlyInDb(TemplateBuildQo templateBuildQo) {
        List<PPTTemplateElement> elements = JSON.parseArray(
                JSON.toJSONString(templateBuildQo.getThisGroupElements()), PPTTemplateElement.class);
        double area = findArea(elements);
        String page = Optional.ofNullable(templateBuildQo.getPage()).map(PPTTemplatePage::getPage).orElse(null);
        List<PPTRegion> regions = pptRegionService.queryByArea(area);
        Map<Integer, Integer> map = Maps.newHashMap();
        for (PPTRegion region : regions) {
            if (map.containsKey(region.getPptModelId())) {
                map.put(region.getPptModelId(), map.get(region.getPptModelId()) + 1);
            } else {
                map.put(region.getPptModelId(), 1);
            }
        }
        LinkedHashMap<Integer, Integer> sortAsc = sortAsc(map);
        List<Integer> modelIds = Lists.newArrayList(sortAsc.keySet());
        if (Strings.isNotBlank(page)) {
            PPTPageType byEnName = pptPageTypeService.findByEnName(page);
            List<Integer> byPageId = pptPageModelService.findByPageId(byEnName.getId())
                    .stream().map(PPTPageModel::getId).collect(Collectors.toList());
            modelIds = modelIds.stream().filter(byPageId::contains).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(modelIds)) {
            return Lists.newArrayList();
        }
        if (modelIds.size() > 3) {
            modelIds = modelIds.subList(0, 3);
        }
        return modelIds.stream().map(this::getTemplateNearlyVoByModelId).collect(Collectors.toList());
    }

    // Map的value值升序排序
    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortAsc(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        LinkedHashMap<K, V> returnMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        return returnMap;
    }

    private List<TemplateNearlyVo> findNearlyGroupInThisPage(TemplateBuildQo templateBuildQo) {
        List<TemplateNearlyVo> lists = Lists.newArrayList();
        List<Map<String, Object>> thisGroupElements = templateBuildQo.getThisGroupElements();
        if (CollectionUtils.isEmpty(templateBuildQo.getElements())
                || CollectionUtils.isEmpty(thisGroupElements)) {
            return Lists.newArrayList();
        }
        for (int i = 0; i < templateBuildQo.getElements().size(); i++) {
            List<PPTTemplateElement> element = templateBuildQo.getElements().get(i);
            if (CollectionUtils.isNotEmpty(element) && element.size() == thisGroupElements.size()) {
                if (findTextNumberInMap(thisGroupElements) == findTextNumber(element)
                        && findShapeNumberInMap(thisGroupElements) == findShapeNumber(element)
                        && findImgNumberInMap(thisGroupElements) == findImgNumber(element)
                        && isNearlyArea(element, thisGroupElements)) {
                    if (Objects.isNull(templateBuildQo.getGroups().get(i).getElement())) {
                        continue;
                    }
                    TemplateNearlyVo nearlyVo = getTemplateNearlyVoByModelId(
                            Integer.parseInt(templateBuildQo.getGroups().get(i).getElement()));
                    lists.add(nearlyVo);
                }
            }
        }
        return lists;
    }

    @NotNull
    private TemplateNearlyVo getTemplateNearlyVoByModelId(Integer modelId) {
        PPTPageModel pageModel = pptPageModelService.findById(modelId);
        PPTPageType pageType = pptPageTypeService.findById(pageModel.getPptPageTypeId());
        return new TemplateNearlyVo(pageModel.getPptPageModelName(),
                pageType.getPageTypeNameEn(), Integer.toString(pageModel.getId()));
    }

    private void putNearlyPageName(TemplateBuildQo templateBuildQo) {
        PPTTemplateGroup group = templateBuildQo.getGroups().stream()
                .filter(t -> !StringUtils.equals(NORMAL, t.getPurpose()))
                .filter(t -> Strings.isNotBlank(t.getPurpose()))
                .findFirst()
                .orElse(null);
        if (Objects.nonNull(group)) {
            for (PPTTemplateGroup buildVoGroup : templateBuildQo.getGroups()) {
                if (Strings.isEmpty(buildVoGroup.getPurpose())) {
                    buildVoGroup.setPurpose(group.getPurpose());
                }
            }
        }
    }

    private boolean isNearlyArea(List<PPTTemplateElement> list, List<Map<String, Object>> thisList) {
        List<PPTTemplateElement> elements = JSON.parseArray(JSON.toJSONString(thisList), PPTTemplateElement.class);
        double area1 = findArea(elements);
        double area2 = findArea(list);
        return (area1 / area2 < 0 && area1 / area2 >= 0.75) || (area1 / area2 > 0 && area2 / area1 >= 0.75);
    }

    private double findArea(List<PPTTemplateElement> elements) {
        double left = 9999;
        double top = 9999;
        double right = 0;
        double bottom = 0;
        for (PPTTemplateElement element : elements) {
            left = Math.min(element.getLeft(), left);
            top = Math.min(element.getTop(), top);
            right = Math.max(element.getLeft() + element.getWidth(), right);
            bottom = Math.max(element.getTop() + element.getHeight(), bottom);
        }
        return (right - left) * (bottom - top);
    }

    private int findImgNumber(List<PPTTemplateElement> templateElements) {
        return findNumberByType(templateElements, PPTBlockType.IMG);
    }

    private int findShapeNumber(List<PPTTemplateElement> templateElements) {
        return findNumberByType(templateElements, PPTBlockType.SHAPE);
    }

    private int findTextNumberInMap(List<Map<String, Object>> lists) {
        return findCountByType(lists, PPTBlockType.TEXT.getCode());
    }

    private int findImgNumberInMap(List<Map<String, Object>> lists) {
        return findCountByType(lists, PPTBlockType.IMG.getCode());
    }

    private int findShapeNumberInMap(List<Map<String, Object>> lists) {
        return findCountByType(lists, PPTBlockType.SHAPE.getCode());
    }

    private int findCountByType(List<Map<String, Object>> lists, String type) {
        return (int) lists.stream().filter(map -> map.get("type").equals(type)).count();
    }

    private int findTextNumber(List<PPTTemplateElement> templateElements) {
        return findNumberByType(templateElements, PPTBlockType.TEXT);
    }

    private int findNumberByType(List<PPTTemplateElement> templateElements, PPTBlockType img) {
        return (int) templateElements.stream()
                .filter(e -> StringUtils.equals(e.getType(), img.getCode()))
                .count();
    }

    private boolean isNeedInfer(PPTTemplateGroup group) {
        return Strings.isEmpty(group.getPurpose()) || Strings.isEmpty(group.getElement());
    }

    private void inferGroupSign(PPTTemplateGroup group, List<PPTTemplateElement> elementList) {
        if (isNeedInfer(group)) {
            maybeLine(group, elementList);
        }
        if (isNeedInfer(group)) {
            maybeIcon(group, elementList);
        }
    }

    private void maybeLine(PPTTemplateGroup group, List<PPTTemplateElement> elementList) {
        if (elementList.size() == 1 && StringUtils.equals(elementList.get(0).getType(), PPTBlockType.LINE.getCode())) {
            group.setPurpose("innerNormal");
            group.setElement("43");
        }
    }

    private void maybeIcon(PPTTemplateGroup group, List<PPTTemplateElement> elementList) {
        if (group.getHeight() * group.getWidth() < 4) {
            if (findTextNumber(elementList) == 0) {
                group.setPurpose("innerNormal");
                group.setElement("80");
            }
        }
    }
}
