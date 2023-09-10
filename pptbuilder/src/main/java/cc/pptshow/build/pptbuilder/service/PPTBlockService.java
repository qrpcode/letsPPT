package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;

import java.util.List;

public interface PPTBlockService {

    List<PPTBlock> findByPPTBlockPut(PPTBlockPut pptBlockPut);

    List<PPTBlock> findByIds(List<Long> ids);

}
