package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.EnumPPTStyle;
import cc.pptshow.build.pptbuilder.dao.EnumPPTStyleMapper;
import cc.pptshow.build.pptbuilder.service.EnumPPTStyleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EnumPPTStyleServiceImpl implements EnumPPTStyleService {

    @Resource
    private EnumPPTStyleMapper enumPPTStyleMapper;

    @Override
    public List<EnumPPTStyle> findAll() {
        return enumPPTStyleMapper.selectAll();
    }

    @Override
    public EnumPPTStyle findByCode(String code) {
        EnumPPTStyle search = new EnumPPTStyle();
        search.setCode(code);
        return enumPPTStyleMapper.select(search).stream().findFirst().orElse(null);
    }

    @Override
    public EnumPPTStyle findById(Integer id) {
        return enumPPTStyleMapper.selectByPrimaryKey(id);
    }

    @Override
    public EnumPPTStyle findByName(String styleName) {
        EnumPPTStyle search = new EnumPPTStyle();
        search.setName(styleName);
        return enumPPTStyleMapper.select(search).stream().findFirst().orElse(null);
    }
}
