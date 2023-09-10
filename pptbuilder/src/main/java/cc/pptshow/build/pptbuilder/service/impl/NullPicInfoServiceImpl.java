package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.NullPicInfo;
import cc.pptshow.build.pptbuilder.dao.NullPicInfoMapper;
import cc.pptshow.build.pptbuilder.service.NullPicInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NullPicInfoServiceImpl implements NullPicInfoService {

    @Resource
    private NullPicInfoMapper nullPicInfoMapper;

    @Override
    public void insertKeyWords(List<String> imgElements) {
        for (String imgElement : imgElements) {
            NullPicInfo nullPicInfo = new NullPicInfo();
            nullPicInfo.setTitle(imgElement);
            nullPicInfoMapper.insertSelective(nullPicInfo);
        }
    }
}
