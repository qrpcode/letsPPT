package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.PPTBlockShape;
import cc.pptshow.build.pptbuilder.dao.PPTBlockShapeMapper;
import cc.pptshow.build.pptbuilder.service.PPTBlockShapeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class PPTBlockShapeServiceImpl implements PPTBlockShapeService {

    @Resource
    private PPTBlockShapeMapper pptBlockShapeMapper;

    @Override
    public PPTBlockShape selectByBlockId(Long id) {
        PPTBlockShape pptBlockShapeSearch = new PPTBlockShape();
        pptBlockShapeSearch.setBlockId(id);
        return pptBlockShapeMapper.selectOne(pptBlockShapeSearch);
    }

}
