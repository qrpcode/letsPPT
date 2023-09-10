package cc.pptshow.build.pptbuilder.dao;

import cc.pptshow.build.pptbuilder.bean.DataTag;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DataTagMapper extends Mapper<DataTag> {

    @Select("SELECT * FROM data_tag WHERE pinyin = '' LIMIT 50000")
    List<DataTag> selectNullPinyinLimit1000();

    @Select("SELECT * FROM data_tag WHERE tag = #{tag} and id < #{id} LIMIT 1")
    DataTag selectSameWordAndIdMin(@Param("tag") String tag, @Param("id") Integer id);

    @Select("SELECT * FROM data_tag LIMIT #{begin}, 1000")
    List<DataTag> findByBegin(@Param("begin") Integer beginId);

    @Select("SELECT * FROM data_tag WHERE tag = #{tag}")
    List<DataTag> findByTagName(@Param("tag") String tag);

    @Select("SELECT * FROM data_tag WHERE tag = #{tag} ORDER BY id ASC LIMIT 1")
    DataTag findMinTag(@Param("tag") String item);

}
