package cc.pptshow.build.pptbuilder.util;

import cc.pptshow.build.pptbuilder.style.ProbabilityStyle;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RandUtil {

    public static <T> T randElement(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<T> newList = Lists.newArrayList(list);
        return newList.get(round(0, newList.size() - 1));
    }

    public static <T> T randInEnum(Class<? extends ProbabilityStyle> probabilityStyle) {
        if (!probabilityStyle.isEnum()) {
            throw new RuntimeException("不能将非枚举类型进行随机绑定！");
        }
        List<? extends ProbabilityStyle> list =
                Lists.newArrayList(probabilityStyle.getEnumConstants()).stream()
                        .filter(p -> p.getProbability() != 0)
                        .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("抱歉，转换集一个元素都没有");
        }
        List<Integer> probability = list.stream()
                .map(ProbabilityStyle::getProbability)
                .collect(Collectors.toList());
        int count = count(probability);
        int rand = round(1, count);
        int randCount = 0;
        for (int i = 0; i < probability.size(); i++) {
            randCount += probability.get(i);
            if (randCount > rand) {
                return (T) list.get(i);
            }
        }
        return (T) list.get(0);
    }

    public static <T> T round(List<T> list) {
        return list.get(round(0, list.size() - 1));
    }

    public static int round(int min, int max) {
        if (min == max) {
            return min;
        }
        final double d = Math.random();
        return ((int) Math.floor(d * (max - min + 1))) + min;
    }

    public static double round2(double min, double max) {
        int min2 = (int) min * 100;
        int max2 = (int) max * 100;
        return round(min2, max2) / 100.00;
    }

    private static int count(List<Integer> list) {
        int count = 0;
        for (Integer l : list) {
            count += l;
        }
        return count;
    }

    public static double num2Decimal (double d) {
        return (double) Math.round(d * 100) / 100;
    }
}
