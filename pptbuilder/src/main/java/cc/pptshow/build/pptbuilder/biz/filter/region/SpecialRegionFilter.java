package cc.pptshow.build.pptbuilder.biz.filter.region;

import cc.pptshow.build.pptbuilder.anno.ForRegion;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.qo.RegionFilterQo;
import cc.pptshow.build.pptbuilder.service.PPTRegionService;
import cc.pptshow.build.pptbuilder.util.Safes;
import cc.pptshow.build.pptbuilder.util.SpecialTypeUtl;
import cn.hutool.core.collection.SpliteratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对系统里面特殊用途的PPT页面进行过滤
 */
@Slf4j
@Service
@ForRegion(force = false, name = "特殊用途过滤")
public class SpecialRegionFilter implements RegionFilter {

    @Resource
    private PPTRegionService pptRegionService;

    @Override
    public List<PPTBlockPut> filterPPTBlockPut(RegionFilterQo regionFilterQo) {
        GlobalStyle globalStyle = regionFilterQo.getGlobalStyle();
        int specialType = SpecialTypeUtl.findSpecialType(globalStyle);
        List<Long> regionIds = Safes.of(regionFilterQo.getBlockPuts()).stream()
                .map(PPTBlockPut::getPptRegionId).collect(Collectors.toList());
        List<PPTRegion> regions = pptRegionService.queryByIds(regionIds);
        List<Long> filterRegionIds = Safes.of(regions).stream()
                .filter(r -> r.getSpecialType().equals(BConstant.SPECIAL_DEFAULT)
                        || r.getSpecialType().equals(specialType))
                .map(PPTRegion::getId)
                .collect(Collectors.toList());
        return Safes.of(regionFilterQo.getBlockPuts()).stream()
                .filter(block -> filterRegionIds.contains(block.getPptRegionId()))
                .collect(Collectors.toList());
    }

}
