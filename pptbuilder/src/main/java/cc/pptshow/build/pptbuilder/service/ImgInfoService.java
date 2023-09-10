package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.ImgInfo;

import java.util.List;

public interface ImgInfoService {

    String queryOnceBackgroundByRand(Integer backgroundColorType);

    String randInnerImg();

    ImgInfo insert(ImgInfo imgInfo);

    List<ImgInfo> findByKeyword(String item);

    List<ImgInfo> findByColorTypes(String colorType);

}
