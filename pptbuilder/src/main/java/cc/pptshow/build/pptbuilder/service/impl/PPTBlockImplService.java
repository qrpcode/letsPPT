package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.dao.PPTBlockMapper;
import cc.pptshow.build.pptbuilder.service.PPTBlockService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PPTBlockImplService implements PPTBlockService {

    @Resource
    private PPTBlockMapper pptBlockMapper;

    @Override
    public List<PPTBlock> findByPPTBlockPut(PPTBlockPut pptBlockPut) {
        String pptBlockIds = pptBlockPut.getPptBlockIds();
        if (Strings.isBlank(pptBlockIds)) {
            throw new NullPointerException("PPT元素信息储存的有问题，block组为" + pptBlockPut.getId());
        }
        return pptBlockMapper.selectByIds(pptBlockIds);
    }


    @Override
    public List<PPTBlock> findByIds(List<Long> ids) {
        String idStr = BConstant.JOINER.join(ids);
        return pptBlockMapper.selectByIds(idStr);
    }
}
