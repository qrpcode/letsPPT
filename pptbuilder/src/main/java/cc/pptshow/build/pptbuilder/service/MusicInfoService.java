package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.MusicInfo;

public interface MusicInfoService {

    MusicInfo findRandOneByStyle(Integer id);

}
