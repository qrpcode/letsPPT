package cc.pptshow.build.pptbuilder.biz.filter.region;

import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.domain.qo.RegionFilterQo;

import java.util.List;

public interface RegionFilter {

    List<PPTBlockPut> filterPPTBlockPut(RegionFilterQo regionFilterQo);

}
