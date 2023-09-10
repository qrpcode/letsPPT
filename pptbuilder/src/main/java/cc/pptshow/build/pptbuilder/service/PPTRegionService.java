package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;

import java.util.List;

public interface PPTRegionService {

    List<PPTRegion> queryRegionsByBlock(PPTRegionPut pptRegionPut);

    PPTRegion queryById(Long regionId);

    List<PPTRegion> queryByArea(double area);

    List<PPTRegion> queryByIds(List<Long> regionIds);

}
