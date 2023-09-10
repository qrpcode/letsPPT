package cc.pptshow.build.pptbuilder.biz.filter.page;

import cc.pptshow.build.pptbuilder.anno.ForPage;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.ppt.show.PPTShow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 一个PPT中不能出现两个一样的页面
 */
@Slf4j
@Service
@ForPage(force = true, name = "页面相同元素过滤")
public class SamePageFilter implements PageFilter {

    @Override
    public List<PPTRegionPut> filterPPTRegionPut(List<PPTRegionPut> regionPuts,
                                                 GlobalStyle globalStyle,
                                                 PPTShow pptShow) {
        log.info("[SamePageFilter] regionPutIds:{}, regionPutIds:{}",
                regionPuts.stream().map(PPTRegionPut::getId).collect(Collectors.toList()),
                globalStyle.getRegionPuts().values().stream().map(PPTRegionPut::getId).collect(Collectors.toList()));
        regionPuts.removeAll(globalStyle.getRegionPuts().values());
        return regionPuts;
    }

}
