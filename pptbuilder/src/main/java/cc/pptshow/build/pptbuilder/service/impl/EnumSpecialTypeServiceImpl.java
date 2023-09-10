package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.EnumSpecialType;
import cc.pptshow.build.pptbuilder.dao.EnumSpecialTypeMapper;
import cc.pptshow.build.pptbuilder.service.EnumSpecialTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class EnumSpecialTypeServiceImpl implements EnumSpecialTypeService {

    @Resource
    private EnumSpecialTypeMapper enumSpecialTypeMapper;

    @Override
    public List<EnumSpecialType> findAll() {
        return enumSpecialTypeMapper.selectAll();
    }

}
