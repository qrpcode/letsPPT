package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.ConvertPptPng;
import cc.pptshow.build.pptbuilder.dao.ConvertPptPngMapper;
import cc.pptshow.build.pptbuilder.service.ConvertPPTPngService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Service
public class ConvertPPTPngServiceImpl implements ConvertPPTPngService {

    @Resource
    private ConvertPptPngMapper convertPptPngMapper;

    @Transactional(rollbackFor = Exception.class)
    public ConvertPptPng signLastNotConvert() {
        ConvertPptPng convertPptPng = convertPptPngMapper.queryLastNotConvert();
        if (Objects.nonNull(convertPptPng)) {
            convertPptPng.setState(1);
            convertPptPngMapper.updateReadById(convertPptPng.getId());
            return convertPptPng;
        }
        return null;
    }

    @Override
    public void updateFinish(Long id) {
        convertPptPngMapper.updateFinishById(id);
    }

    @Override
    public void insert(ConvertPptPng pptPng) {
        convertPptPngMapper.insert(pptPng);
    }

    @Override
    public ConvertPptPng selectById(long id) {
        return convertPptPngMapper.selectById(id);
    }

}
