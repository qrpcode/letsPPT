package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.DataTitle;

import java.util.List;

public interface DataTitleService {

    List<DataTitle> findLimit100WithoutAnalysisTitle();

    DataTitle findById(Long id);

    void update(DataTitle dataTitle);

    List<DataTitle> find100NotSuccess();

}
