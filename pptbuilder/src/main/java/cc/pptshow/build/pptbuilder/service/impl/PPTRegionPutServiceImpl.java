package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.dao.PPTRegionPutMapper;
import cc.pptshow.build.pptbuilder.service.PPTRegionPutService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PPTRegionPutServiceImpl implements PPTRegionPutService {

    @Resource
    private PPTRegionPutMapper pptRegionPutMapper;

    @Override
    public List<PPTRegionPut> queryByPageId(Integer pageTypeId) {
        PPTRegionPut pptRegionPut = new PPTRegionPut();
        pptRegionPut.setPptPageId(pageTypeId);
        return pptRegionPutMapper.select(pptRegionPut);
    }

    @Override
    public void update(PPTRegionPut pptRegionPut) {
        pptRegionPutMapper.updateByPrimaryKey(pptRegionPut);
    }

    @Override
    public PPTRegionPut selectById(Long homeRegionPutId) {
        return pptRegionPutMapper.selectByPrimaryKey(homeRegionPutId);
    }
}
