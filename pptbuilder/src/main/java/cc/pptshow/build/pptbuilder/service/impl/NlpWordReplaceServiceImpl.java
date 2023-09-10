package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.NlpWordReplace;
import cc.pptshow.build.pptbuilder.dao.NlpWordReplaceMapper;
import cc.pptshow.build.pptbuilder.service.NlpWordReplaceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NlpWordReplaceServiceImpl implements NlpWordReplaceService {

    @Resource
    private NlpWordReplaceMapper nlpWordReplaceMapper;

    public List<NlpWordReplace> selectAll() {
        return nlpWordReplaceMapper.selectAll();
    }

}
