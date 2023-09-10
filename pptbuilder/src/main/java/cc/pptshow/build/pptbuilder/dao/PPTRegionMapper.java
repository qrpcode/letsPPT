package cc.pptshow.build.pptbuilder.dao;

import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PPTRegionMapper extends Mapper<PPTRegion> {

    String COLUMN = " ppt_region.id, ppt_region.ppt_model_id AS pptModelId, ppt_region.left_size AS leftSize, " +
            " ppt_region.top_size AS topSize, " +
            " ppt_region.width_size AS widthSize, ppt_region.height_size AS heightSize, ppt_region.alignment," +
            " ppt_region.alignment_group AS alignmentGroup, special_type AS specialType";

    @Select("SELECT " + COLUMN + " FROM ppt_region WHERE id IN (${pptRegionIds})")
    List<PPTRegion> selectByIds(@Param("pptRegionIds") String pptRegionIds);

    @Select("SELECT " + COLUMN + "FROM ppt_region LEFT JOIN ppt_block_put ON ppt_region.id = " +
            " ppt_block_put.ppt_region_id WHERE ppt_block_put.id IS null ")
    List<PPTRegion> selectNullBlockRegions();

    @Select("SELECT " + COLUMN + " FROM ppt_region WHERE ppt_model_id = #{pptModelId}" +
            " AND width_size >= #{widthSize} * 0.98 AND width_size <= #{widthSize} * 1.02 "+
            " AND height_size >= #{heightSize} * 0.98 AND height_size <= #{heightSize} * 1.02 ")
    List<PPTRegion> selectNearlyRegionByModel(@Param("pptModelId") Integer pptModelId,
                                        @Param("widthSize") Double widthSize,
                                        @Param("heightSize") Double heightSize);

    @Select("SELECT " + COLUMN + " FROM ppt_region WHERE " +
            " width_size * height_size >= #{area} * 0.8 AND width_size * height_size <= #{area} * 1.2 LIMIT 100 ")
    List<PPTRegion> selectNearlyRegionByArea(@Param("area") Double area);
}
