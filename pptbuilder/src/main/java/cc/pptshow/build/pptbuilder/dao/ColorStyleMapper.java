package cc.pptshow.build.pptbuilder.dao;

import cc.pptshow.build.pptbuilder.bean.ColorStyle;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ColorStyleMapper extends Mapper<ColorStyle> {

    String COLUMN = " id, color, color_style_name AS colorStyleName, nearly_word AS nearlyWord ";

    @Select("SELECT " + COLUMN + " FROM color_style WHERE nearly_word LIKE '%,${color},%' " +
            " or nearly_word LIKE '${color},%' or nearly_word LIKE '%,${color}' or nearly_word = '${color}' ")
    List<ColorStyle> findByWord(@Param("color") String color);

    @Select("SELECT " + COLUMN + " FROM color_style WHERE id IN (-1, ${types})")
    List<ColorStyle> selectByIds(@Param("types") String colorType);

}
