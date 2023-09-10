package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.PPTFileData;
import cc.pptshow.build.pptbuilder.dao.PPTFileDataMapper;
import cc.pptshow.build.pptbuilder.service.PPTFileDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PPTFileDataServiceImpl implements PPTFileDataService {

    @Resource
    private PPTFileDataMapper pptFileDataMapper;

    @Override
    public void insert(PPTFileData pptFileData) {
        pptFileDataMapper.insert(pptFileData);
    }

    @Override
    public PPTFileData selectById(Long id) {
        return pptFileDataMapper.selectByPrimaryKey(id);
    }

}
