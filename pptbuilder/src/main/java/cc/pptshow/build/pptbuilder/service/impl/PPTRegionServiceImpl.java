package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.dao.PPTRegionMapper;
import cc.pptshow.build.pptbuilder.service.PPTRegionService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class PPTRegionServiceImpl implements PPTRegionService {

    @Resource
    private PPTRegionMapper pptRegionMapper;

    @Override
    public List<PPTRegion> queryRegionsByBlock(PPTRegionPut pptRegionPut) {
        return pptRegionMapper.selectByIds(pptRegionPut.getPptRegionIds());
    }

    @Override
    public PPTRegion queryById(Long regionId) {
        return pptRegionMapper.selectByPrimaryKey(regionId);
    }

    @Override
    public List<PPTRegion> queryByArea(double area) {
        return pptRegionMapper.selectNearlyRegionByArea(area);
    }

    @Override
    public List<PPTRegion> queryByIds(List<Long> regionIds) {
        if (CollectionUtils.isEmpty(regionIds)) {
            return Lists.newArrayList();
        }
        String ids = BConstant.JOINER.join(regionIds);
        return pptRegionMapper.selectByIds(ids);
    }

}
