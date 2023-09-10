package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.FontInfo;
import cc.pptshow.build.pptbuilder.dao.FontInfoMapper;
import cc.pptshow.build.pptbuilder.service.FontInfoService;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FontInfoServiceImpl implements FontInfoService {

    @Resource
    private FontInfoMapper fontInfoMapper;

    private static final int TEXT = 1;
    private static final int TITLE = 2;

    @Override
    public List<FontInfo> findAll() {
        return fontInfoMapper.selectAll();
    }

    @Override
    public FontInfo randFont() {
        List<FontInfo> fontInfos = fontInfoMapper.selectAll();
        return fontInfos.get(RandUtil.round(0, fontInfos.size() - 1));
    }

    @Override
    public FontInfo findTextFontByStyle(Integer styleId) {
        return findFontByStyle(styleId, TEXT);
    }

    @Override
    public FontInfo findTitleFontByStyle(Integer styleId) {
        return findFontByStyle(styleId, TITLE);
    }

    public FontInfo findFontByStyle(Integer id, Integer type) {
        return fontInfoMapper.findByStyleId(id, type).stream().parallel().findAny().orElse(null);
    }

}
