package cc.pptshow.build.pptbuilder.biz.design.help;

import cc.pptshow.build.pptbuilder.domain.ElementLocation;
import cc.pptshow.build.pptbuilder.util.PositionUtil;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 元素分组算法
 * 互相有接触的元素被视为同一组，可以按照任意元素类型分也可以按照文字分
 */
@Service
public class ElementGroupHelper {

    public List<PPTElement> findElementsNotText(List<PPTElement> elements) {
        return Lists.newArrayList(elements).stream()
                .filter(e -> !(e instanceof PPTText))
                .collect(Collectors.toList());
    }

    public List<List<PPTElement>> buildElementGroupOnlyText(List<PPTElement> elements) {
        return buildElementGroup(Lists.newArrayList(elements).stream()
                .filter(e -> e instanceof PPTText)
                .collect(Collectors.toList()));
    }

    public List<List<PPTElement>> buildElementGroup(List<PPTElement> elements) {
        List<List<PPTElement>> group = Lists.newArrayList();
        List<PPTElement> copyList = Lists.newArrayList(elements);
        //只要原来的元素组里面还有东西就需要继续分
        while (CollectionUtils.isNotEmpty(copyList)) {
            List<PPTElement> groupElements = Lists.newArrayList();
            PPTElement groupRootElement = copyList.get(0);
            copyList.remove(0);
            groupElements.add(groupRootElement);
            List<PPTElement> intersectElements = findIntersectElements(groupElements, copyList);
            while (CollectionUtils.isNotEmpty(intersectElements)) {
                groupElements.addAll(intersectElements);
                copyList.removeAll(intersectElements);
                intersectElements = findIntersectElements(groupElements, copyList);
            }
            group.add(groupElements);
        }
        return group;
    }

    private List<PPTElement> findIntersectElements(List<PPTElement> groupElements,
                                                   List<PPTElement> copyList) {
        if (CollectionUtils.isEmpty(copyList)) {
            return Lists.newArrayList();
        }
        return copyList.stream()
                .filter(element -> groupElements.stream()
                        .anyMatch(e -> PositionUtil.calculateOverlappingArea(e, element) > 0))
                .collect(Collectors.toList());
    }

    public void sortByLeft(List<List<PPTElement>> elementGroups) {
        elementGroups.sort(Comparator.comparing(this::findLeft));
    }

    private double findLeft(List<PPTElement> elements) {
        double left = Integer.MAX_VALUE;
        for (PPTElement element : elements) {
            ElementLocation location = PositionUtil.findLocationByElement(element);
            if (Objects.nonNull(location)) {
                left = Math.min(left, location.getLeft());
            }
        }
        return left;
    }

    public void sortByTop(List<List<PPTElement>> elementGroups) {
        elementGroups.sort(Comparator.comparing(this::findTop));
    }

    private double findTop(List<PPTElement> elements) {
        double top = Integer.MAX_VALUE;
        for (PPTElement element : elements) {
            ElementLocation location = PositionUtil.findLocationByElement(element);
            if (Objects.nonNull(location)) {
                top = Math.min(top, location.getTop());
            }
        }
        return top;
    }

    public List<List<PPTElement>> merge(List<List<PPTElement>> elementGroups, int maxGroup) {
        if (CollectionUtils.isEmpty(elementGroups) || elementGroups.size() < maxGroup) {
            return elementGroups;
        }
        int floor = (int) Math.floor(elementGroups.size() * 1.00 / maxGroup);
        int last = elementGroups.size() - (floor * maxGroup);
        List<List<PPTElement>> newElementGroups = Lists.newArrayList();
        int fromIndex = 0;
        for (int i = 0; i < maxGroup; i++) {
            List<List<PPTElement>> lists;
            if (last > 0) {
                last--;
                lists = elementGroups.subList(fromIndex, fromIndex + floor + 1);
                fromIndex += floor + 1;
            } else {
                lists = elementGroups.subList(fromIndex, fromIndex + floor);
                fromIndex += floor;
            }
            List<PPTElement> group = lists.stream().flatMap(Collection::stream).collect(Collectors.toList());
            newElementGroups.add(group);
        }
        return newElementGroups;
    }

}
