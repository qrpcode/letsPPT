package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockImg;

public interface PPTBlockImgService {

    PPTBlockImg queryByBlock(PPTBlock pptBlock);

}
