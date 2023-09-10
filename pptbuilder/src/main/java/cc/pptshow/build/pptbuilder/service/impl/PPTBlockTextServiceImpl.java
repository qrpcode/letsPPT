package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.PPTBlockText;
import cc.pptshow.build.pptbuilder.dao.PPTBlockTextMapper;
import cc.pptshow.build.pptbuilder.service.PPTBlockTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class PPTBlockTextServiceImpl implements PPTBlockTextService {

    @Resource
    private PPTBlockTextMapper pptBlockTextMapper;

    @Override
    public PPTBlockText queryByBlockId(Long id) {
        PPTBlockText pptBlockTextSearch = PPTBlockText.buildByBlockId(id);
        return pptBlockTextMapper.selectOne(pptBlockTextSearch);
    }
}
