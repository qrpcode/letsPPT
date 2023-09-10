package cc.pptshow.build.pptbuilder.dao;

import cc.pptshow.build.pptbuilder.bean.FilePPTInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface FilePPTInfoMapper extends Mapper<FilePPTInfo> {

    String COLUMN = "`id`, `uid`, `channel`, `title`, `data_title_id` AS dataTitleId, `color_type` AS colorType, " +
            " `color_info_id` AS colorInfoId, `ppt_style` AS pptStyle, `page_size` AS pageSize, `file_format` AS fileFormat, " +
            " `down_url` AS downUrl, `pic_local_url` AS picLocalUrl, `pic_url` AS picUrl, `video_url` AS videoUrl, " +
            "`about_text` AS aboutText, `tags`, `source_json` AS sourceJson, `upload`, `designer_id` AS designerId, " +
            "`create_time` AS createTime";

    @Select("SELECT COUNT(*) FROM file_ppt_info WHERE uid LIKE '${date}%'")
    int selectCountByDate(@Param("date") String date);

    @Select("SELECT " + COLUMN + " FROM file_ppt_info LIMIT #{begin}, 1000")
    List<FilePPTInfo> findByBegin(@Param("begin") int begin);

    @Select("SELECT " + COLUMN + " FROM file_ppt_info where upload = 2 and id >= 2741 LIMIT #{count}")
    List<FilePPTInfo> selectGreatByCount(@Param("count") int count);

    @Select("SELECT " + COLUMN + " FROM file_ppt_info where upload = 1 and id >= 2741 LIMIT #{count}")
    List<FilePPTInfo> selectNormalByCount(@Param("count") int count);

    @Select("SELECT COUNT(*) FROM file_ppt_info where upload = 0")
    Long findNotUpdateCount();

    @Select("SELECT " + COLUMN + " FROM file_ppt_info where upload = -1001 and id >= 2741")
    List<FilePPTInfo> selectWait();

}
