package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.PPTFileData;

public interface PPTFileDataService {

    void insert(PPTFileData pptFileData);

    PPTFileData selectById(Long id);

}
