package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.NullColorTitle;
import cc.pptshow.build.pptbuilder.dao.NullColorTitleMapper;
import cc.pptshow.build.pptbuilder.service.NullColorTitleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class NullColorTitleServiceImpl implements NullColorTitleService {

    @Resource
    private NullColorTitleMapper nullColorTitleMapper;

    @Override
    public void addNullTitle(String title) {
        NullColorTitle nullColorTitle = new NullColorTitle();
        nullColorTitle.setTitle(title);
        nullColorTitleMapper.insert(nullColorTitle);
    }

}
