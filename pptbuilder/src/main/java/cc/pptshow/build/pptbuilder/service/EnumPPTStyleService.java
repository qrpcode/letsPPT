package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.EnumPPTStyle;

import java.util.List;

public interface EnumPPTStyleService {

    List<EnumPPTStyle> findAll();

    EnumPPTStyle findByCode(String code);

    EnumPPTStyle findById(Integer id);

    EnumPPTStyle findByName(String styleName);

}
