package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.PPTBlockShape;

public interface PPTBlockShapeService {

    PPTBlockShape selectByBlockId(Long id);

}
