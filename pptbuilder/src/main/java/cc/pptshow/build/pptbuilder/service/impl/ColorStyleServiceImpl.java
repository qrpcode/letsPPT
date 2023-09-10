package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.ColorInfo;
import cc.pptshow.build.pptbuilder.bean.ColorStyle;
import cc.pptshow.build.pptbuilder.dao.ColorStyleMapper;
import cc.pptshow.build.pptbuilder.service.ColorStyleService;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ColorStyleServiceImpl implements ColorStyleService {

    @Resource
    private ColorStyleMapper colorStyleMapper;

    @Override
    public List<ColorStyle> findAllStyle() {
        return colorStyleMapper.selectAll();
    }

    @Override
    public ColorStyle findByWord(String color) {
        log.info("[ColorStyle findByWord] color: {}", color);
        if (Strings.isEmpty(color)) {
            return null;
        }
        List<ColorStyle> styles = colorStyleMapper.findByWord(color);
        return RandUtil.randElement(styles);
    }

    @Override
    public List<ColorStyle> findAll() {
        return colorStyleMapper.selectAll();
    }

    @Override
    public List<ColorStyle> findByIds(String colorType) {
        return colorStyleMapper.selectByIds(colorType);
    }


}
