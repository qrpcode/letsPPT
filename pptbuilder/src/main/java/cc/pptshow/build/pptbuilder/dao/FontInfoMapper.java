package cc.pptshow.build.pptbuilder.dao;

import cc.pptshow.build.pptbuilder.bean.FontInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface FontInfoMapper extends Mapper<FontInfo> {

    String COLUMN = " `id`, `font_name` AS fontName, `font_code` AS fontCode," +
            " `from_url` AS fromUrl, `ppt_style_id` AS pptStyleId, type ";

    @Select("SELECT " + COLUMN + " FROM font_info WHERE (ppt_style_id = 0 or ppt_style_id = ${id}) and type = #{type}")
    List<FontInfo> findByStyleId(@Param("id") Integer id, @Param("type") Integer type);

}
