package cc.pptshow.build.pptbuilder.dao;

import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PPTBlockPutMapper extends Mapper<PPTBlockPut> {

    @Select("SELECT id, ppt_region_id AS pptRegionId, ppt_block_ids AS pptBlockIds " +
            " FROM ppt_block_put WHERE ppt_region_id IN (${pptRegionIds})")
    List<PPTBlockPut> selectByRegionIds(@Param("pptRegionIds") String pptRegionIds);

}
