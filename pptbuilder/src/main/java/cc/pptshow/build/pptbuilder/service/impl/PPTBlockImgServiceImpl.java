package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockImg;
import cc.pptshow.build.pptbuilder.dao.PPTBlockImgMapper;
import cc.pptshow.build.pptbuilder.service.PPTBlockImgService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PPTBlockImgServiceImpl implements PPTBlockImgService {

    @Resource
    private PPTBlockImgMapper pptBlockImgMapper;

    @Override
    public PPTBlockImg queryByBlock(PPTBlock pptBlock) {
        PPTBlockImg search = new PPTBlockImg();
        search.setBlockId(pptBlock.getId());
        return pptBlockImgMapper.selectOne(search);
    }
}
