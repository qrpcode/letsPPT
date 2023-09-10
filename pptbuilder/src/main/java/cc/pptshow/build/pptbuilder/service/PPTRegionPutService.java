package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;

import java.util.List;

public interface PPTRegionPutService {

    List<PPTRegionPut> queryByPageId(Integer pageTypeId);

    void update(PPTRegionPut pptRegionPut);

    PPTRegionPut selectById(Long homeRegionPutId);
}
