package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.PPTPageModel;

import java.util.List;

public interface PPTPageModelService {

    List<PPTPageModel> findAll();

    Integer findByPageAndModelName(String pageName, String modelName);

    List<PPTPageModel> findByPageId(Integer id);

    PPTPageModel findById(Integer id);

}
