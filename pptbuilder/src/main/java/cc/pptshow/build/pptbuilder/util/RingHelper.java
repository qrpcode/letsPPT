package cc.pptshow.build.pptbuilder.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;

import java.util.Set;

public class RingHelper {

    private final Set<Object> set = Sets.newHashSet();

    public static RingHelper build() {
        return new RingHelper();
    }

    public boolean add(Object object) {
        return set.add(object);
    }

    public String log() {
        return JSON.toJSONString(set);
    }

}
