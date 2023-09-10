package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.KeywordStyle;
import cc.pptshow.build.pptbuilder.dao.KeywordStyleMapper;
import cc.pptshow.build.pptbuilder.service.KeywordStyleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class KeywordStyleServiceImpl implements KeywordStyleService {

    @Resource
    private KeywordStyleMapper keywordStyleMapper;

    public void save(KeywordStyle keywordStyle) {
        keywordStyleMapper.insert(keywordStyle);
    }

    @Override
    public List<KeywordStyle> findAll() {
        return keywordStyleMapper.selectAll();
    }

}
