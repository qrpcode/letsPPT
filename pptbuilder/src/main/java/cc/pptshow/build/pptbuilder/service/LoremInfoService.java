package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.domain.enums.LanguageType;

public interface LoremInfoService {

    String getRandByLanguage(LanguageType languageType);

}
