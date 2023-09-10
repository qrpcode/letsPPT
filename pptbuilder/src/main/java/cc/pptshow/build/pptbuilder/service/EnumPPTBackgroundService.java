package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.EnumPPTBackground;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBackgroundType;

import java.util.List;

public interface EnumPPTBackgroundService {

    List<EnumPPTBackground> findAll();

    EnumPPTBackground findByCode(String code);

    PPTBackgroundType findById(int id);

}
