package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.PPTBlockLine;

public interface PPTBlockLineService {
    void insert(PPTBlockLine pptBlockLine);

    PPTBlockLine findByBlockId(Long id);
}
