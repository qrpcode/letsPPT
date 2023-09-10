package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.SignBox;
import cc.pptshow.build.pptbuilder.dao.SignBoxMapper;
import cc.pptshow.build.pptbuilder.service.SignBoxService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SignBoxServiceImpl implements SignBoxService {

    @Resource
    private SignBoxMapper signBoxMapper;


}
