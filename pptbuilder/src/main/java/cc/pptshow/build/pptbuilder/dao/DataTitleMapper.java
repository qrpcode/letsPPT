package cc.pptshow.build.pptbuilder.dao;

import cc.pptshow.build.pptbuilder.bean.DataTitle;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DataTitleMapper extends Mapper<DataTitle> {

    @Select("SELECT id, title, real_title AS realTitle, build_success AS buildSuccess, nlp FROM data_title " +
            " WHERE char_length(real_title) < 2 OR real_title IS null LIMIT 100")
    List<DataTitle> findLimit100WithoutAnalysisTitle();

    @Select("SELECT id, title, real_title AS realTitle, build_success AS buildSuccess, nlp FROM data_title " +
            " WHERE build_success = 0 ORDER BY id ASC LIMIT 100")
    List<DataTitle> find100NotSuccess();


}
