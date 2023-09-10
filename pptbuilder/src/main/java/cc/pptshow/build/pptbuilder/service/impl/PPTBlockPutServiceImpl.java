package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.dao.PPTBlockPutMapper;
import cc.pptshow.build.pptbuilder.service.PPTBlockPutService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class PPTBlockPutServiceImpl implements PPTBlockPutService {

    @Resource
    private PPTBlockPutMapper pptBlockPutMapper;

    @Override
    public void save(PPTBlockPut pptBlockPut) {
        if (Strings.isEmpty(pptBlockPut.getPptBlockIds())) {
            return;
        }
        pptBlockPutMapper.insertSelective(pptBlockPut);
    }

    @Override
    public List<PPTBlockPut> selectByRegionIds(String ids) {
        if (Strings.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return pptBlockPutMapper.selectByRegionIds(ids);
    }

}
