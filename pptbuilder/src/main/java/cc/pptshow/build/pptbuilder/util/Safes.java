package cc.pptshow.build.pptbuilder.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Safes {

    public static <T> List<T> of(List<T> list) {
        if (Objects.isNull(list)) {
            return Lists.newArrayList();
        }
        return list;
    }

    public static <T> Set<T> of(Set<T> set) {
        if (Objects.isNull(set)) {
            return Sets.newHashSet();
        }
        return set;
    }

}
