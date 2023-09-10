package cc.pptshow.build.pptbuilder.service.impl;

import cc.pptshow.build.pptbuilder.bean.ColorInfo;
import cc.pptshow.build.pptbuilder.bean.ColorStyle;
import cc.pptshow.build.pptbuilder.dao.ColorInfoMapper;
import cc.pptshow.build.pptbuilder.service.ColorInfoService;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class ColorInfoServiceImpl implements ColorInfoService {

    @Resource
    private ColorInfoMapper colorInfoMapper;

    @Override
    public ColorInfo randColor() {
        List<ColorInfo> colorInfos = colorInfoMapper.selectAll();
        return colorInfos.get(RandUtil.round(0, colorInfos.size() - 1));
    }

    @Override
    public ColorInfo queryById(Integer colorStyleId) {
        return colorInfoMapper.selectByPrimaryKey(colorStyleId);
    }

    @Override
    public ColorInfo queryByStyleId(Integer colorStyle) {
        ColorInfo colorInfo = new ColorInfo();
        colorInfo.setColorType(Integer.toString(colorStyle));
        return colorInfoMapper.select(colorInfo).stream().parallel().findAny().orElse(null);
    }

    @Override
    public ColorInfo findByColorStyleAndBackgroundColor(ColorStyle colorStyle, ColorStyle backgroundColorStyle) {
        ColorInfo colorInfo = new ColorInfo();
        if (Objects.nonNull(colorStyle)) {
            colorInfo.setColorType(Integer.toString(colorStyle.getId()));
        }
        if (Objects.nonNull(backgroundColorStyle)) {
            colorInfo.setBackgroundColorType(backgroundColorStyle.getId());
        }
        return colorInfoMapper.select(colorInfo).stream().parallel().findAny().orElse(null);
    }

    @Override
    public List<ColorInfo> findAll() {
        return colorInfoMapper.selectAll();
    }

}
