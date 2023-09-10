package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.FontInfo;

import java.util.List;

public interface FontInfoService {

    List<FontInfo> findAll();

    FontInfo randFont();

    FontInfo findTextFontByStyle(Integer styleId);

    FontInfo findTitleFontByStyle(Integer styleId);

}
