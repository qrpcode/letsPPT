package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.PPTBlockText;

public interface PPTBlockTextService {

    PPTBlockText queryByBlockId(Long id);

}
