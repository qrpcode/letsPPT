package cc.pptshow.build.pptbuilder.dao;

import cc.pptshow.build.pptbuilder.bean.ImgInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ImgInfoMapper extends Mapper<ImgInfo> {

    String column = " id, pic_uri picUri, color_Type colorType, pic_from picFrom, " +
            " from_url fromUrl, license, title, use_background useBackground ";

    /**
     * 随机办公图
     */
    @Select("SELECT " + column + " FROM image_info WHERE title LIKE '%办公%' ORDER BY rand() LIMIT 1")
    ImgInfo randWorkImg();

    @Select("SELECT " + column + " FROM image_info WHERE title LIKE '%${keyword}%'")
    List<ImgInfo> selectByKeyword(@Param("keyword") String keyword);

    @Select("SELECT " + column + " FROM image_info WHERE color_Type IN (${colorType})")
    List<ImgInfo> selectByTypes(@Param("colorType") String colorType);

}
