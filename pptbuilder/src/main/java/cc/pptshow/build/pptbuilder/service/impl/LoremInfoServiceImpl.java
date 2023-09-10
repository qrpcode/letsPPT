package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.LoremInfo;
import cc.pptshow.build.pptbuilder.dao.LoremInfoMapper;
import cc.pptshow.build.pptbuilder.domain.enums.LanguageType;
import cc.pptshow.build.pptbuilder.service.LoremInfoService;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LoremInfoServiceImpl implements LoremInfoService {

    @Resource
    private LoremInfoMapper loremInfoMapper;

    public String getRandByLanguage(LanguageType languageType) {
        LoremInfo loremInfo = new LoremInfo();
        loremInfo.setLanguage(languageType.getCode());
        List<LoremInfo> infos = loremInfoMapper.select(loremInfo);
        if (CollectionUtils.isEmpty(infos)) {
            throw new RuntimeException("系统里面没有关于" + languageType.getCode() + "的实现！");
        }
        return RandUtil.round(infos).getLorem();
    }

}
