package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.KeywordStyle;

import java.util.List;

public interface KeywordStyleService {

    void save(KeywordStyle keywordStyle);

    List<KeywordStyle> findAll();

}
