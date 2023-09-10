package cc.pptshow.build.pptbuilder.dao;

import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PPTBlockMapper extends Mapper<PPTBlock> {

    String COLUMN = " id, ppt_block_type AS pptBlockType, region_id AS regionId, left_size AS leftSize," +
            " top_size AS topSize, width_size AS widthSize, height_size AS heightSize ";

    @Select("SELECT " + COLUMN + " FROM ppt_block WHERE id IN (${pptBlockIds})")
    List<PPTBlock> selectByIds(@Param("pptBlockIds") String pptBlockIds);
}
