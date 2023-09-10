package cc.pptshow.build.pptbuilder.service;

import cc.pptshow.build.pptbuilder.bean.ColorInfo;
import cc.pptshow.build.pptbuilder.bean.ColorStyle;

import java.util.List;

public interface ColorStyleService {

    List<ColorStyle> findAllStyle();

    ColorStyle findByWord(String color);

    List<ColorStyle> findAll();

    List<ColorStyle> findByIds(String colorType);

}
