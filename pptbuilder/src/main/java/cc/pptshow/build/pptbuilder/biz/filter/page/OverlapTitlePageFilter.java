package cc.pptshow.build.pptbuilder.biz.filter.page;

import cc.pptshow.build.pptbuilder.anno.ForPage;
import cc.pptshow.build.pptbuilder.bean.PPTPageType;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.biz.helper.LayerHelper;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.service.PPTPageTypeService;
import cc.pptshow.ppt.show.PPTShow;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 不能和标题相重叠
 */
@Service
@ForPage(force = true, name = "覆盖标题元素过滤")
public class OverlapTitlePageFilter implements PageFilter {

    @Resource
    private PPTPageTypeService pptPageTypeService;

    @Resource
    private LayerHelper layerHelper;

    @Override
    public List<PPTRegionPut> filterPPTRegionPut(List<PPTRegionPut> regionPuts,
                                                 GlobalStyle globalStyle,
                                                 PPTShow pptShow) {
        Integer pptPageId = regionPuts.get(0).getPptPageId();
        if (pptPageTypeService.isPageNeedTitle(pptPageId)) {
            regionPuts.removeAll(layerHelper.findOverlap(globalStyle.getTitleShape(), regionPuts));
        }
        return regionPuts;
    }

}
