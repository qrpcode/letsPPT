package cc.pptshow.build.pptbuilder.dao;

import cc.pptshow.build.pptbuilder.bean.ConvertPptPng;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface ConvertPptPngMapper extends Mapper<ConvertPptPng> {

    String COLUMN = " id, file_path AS filePath, img_path AS imgPath, state, object AS object ";

    @Select("SELECT " + COLUMN + " FROM convert_ppt_png WHERE state = 0 ORDER BY id LIMIT 1")
    ConvertPptPng queryLastNotConvert();

    @Update("UPDATE `convert_ppt_png` SET `state` = 2 WHERE `id` = ${id}")
    void updateFinishById(@Param("id") Long id);

    @Update("UPDATE `convert_ppt_png` SET `state` = 1 WHERE `id` = ${id}")
    void updateReadById(@Param("id") Long id);

    @Select("SELECT " + COLUMN + " FROM convert_ppt_png WHERE `id` = ${id}")
    ConvertPptPng selectById(@Param("id") long id);

}
