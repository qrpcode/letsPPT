package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.PPTBlockLine;
import cc.pptshow.build.pptbuilder.dao.PPTBlockLineMapper;
import cc.pptshow.build.pptbuilder.service.PPTBlockLineService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PPTBlockLineServiceImpl implements PPTBlockLineService {

    @Resource
    private PPTBlockLineMapper pptBlockLineMapper;

    @Override
    public void insert(PPTBlockLine pptBlockLine) {
        pptBlockLineMapper.insertSelective(pptBlockLine);
    }

    @Override
    public PPTBlockLine findByBlockId(Long id) {
        PPTBlockLine pptBlockLine = new PPTBlockLine();
        pptBlockLine.setBlockId(id);
        return pptBlockLineMapper.selectOne(pptBlockLine);
    }

}
