package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.ColorInfo;
import cc.pptshow.build.pptbuilder.bean.ColorStyle;

import java.util.List;

public interface ColorInfoService {

    ColorInfo randColor();

    ColorInfo queryById(Integer colorStyleId);

    ColorInfo queryByStyleId(Integer colorStyle);

    ColorInfo findByColorStyleAndBackgroundColor(ColorStyle colorStyle, ColorStyle backgroundColorStyle);

    List<ColorInfo> findAll();

}
