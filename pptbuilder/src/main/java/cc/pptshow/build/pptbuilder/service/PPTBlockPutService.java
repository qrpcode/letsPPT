package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;

import java.util.List;

public interface PPTBlockPutService {

    void save(PPTBlockPut pptBlockPut);

    List<PPTBlockPut> selectByRegionIds(String ids);

}
