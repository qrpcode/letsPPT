package cc.pptshow.build.pptbuilder.biz.filter.page;

import cc.pptshow.build.pptbuilder.anno.ForPage;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ForPage(force = true, name = "Debug使用")
public class DebugFilter /*implements PageFilter*/ {

    //@Override
    public List<PPTRegionPut> filterPPTRegionPut(List<PPTRegionPut> regionPuts, GlobalStyle globalStyle) {
        List<PPTRegionPut> pptRegionPuts = regionPuts.stream().filter(r -> r.getId() == 102L).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pptRegionPuts)) {
            return Lists.newArrayList(pptRegionPuts.get(0));
        }
        return regionPuts;
    }
}
