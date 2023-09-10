package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.EnumPPTBackground;
import cc.pptshow.build.pptbuilder.dao.EnumPPTBackgroundMapper;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBackgroundType;
import cc.pptshow.build.pptbuilder.service.EnumPPTBackgroundService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnumPPTBackgroundServiceImpl implements EnumPPTBackgroundService {

    @Resource
    private EnumPPTBackgroundMapper enumPPTBackgroundMapper;

    @Override
    public List<EnumPPTBackground> findAll() {
        return enumPPTBackgroundMapper.selectAll();
    }

    @Override
    public EnumPPTBackground findByCode(String code) {
        EnumPPTBackground search = new EnumPPTBackground();
        search.setCode(code);
        return enumPPTBackgroundMapper.select(search).stream().findFirst().orElse(null);
    }

    @Override
    public PPTBackgroundType findById(int id) {
        EnumPPTBackground background = enumPPTBackgroundMapper.selectByPrimaryKey(id);
        return Arrays.stream(PPTBackgroundType.values())
                .filter(p -> p.getCode().equals(background.getCode()))
                .findFirst()
                .orElse(null);
    }
}
