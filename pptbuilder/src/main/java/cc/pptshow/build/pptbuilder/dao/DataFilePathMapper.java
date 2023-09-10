package cc.pptshow.build.pptbuilder.dao;

import cc.pptshow.build.pptbuilder.bean.DataFilePath;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface DataFilePathMapper extends Mapper<DataFilePath> {

    String COLUMN = " id, path, now_page nowPage, img_path imgPath ";

    @Select("SELECT " + COLUMN + " FROM data_file_path WHERE now_page = -1 ORDER BY id DESC LIMIT 1")
    DataFilePath selectLastUnmark();

    @Select("SELECT " + COLUMN + " FROM data_file_path WHERE img_path IS NULL OR img_path = '' ORDER BY id LIMIT 1")
    DataFilePath findLastNoImgPath();

}
