package cc.pptshow.build.pptbuilder.biz.filter.page;

import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.ppt.show.PPTShow;

import java.util.List;

public interface PageFilter {

    /**
     * 过滤页面选择方案
     */
    List<PPTRegionPut> filterPPTRegionPut(List<PPTRegionPut> regionPuts, GlobalStyle globalStyle, PPTShow pptShow);

}
