package cc.pptshow.build.pptbuilder.biz.builder.helper;

import cc.pptshow.build.pptbuilder.bean.ColorStyle;
import cc.pptshow.build.pptbuilder.bean.PPTPageType;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.service.ColorStyleService;
import cc.pptshow.build.pptbuilder.service.PPTPageTypeService;
import cc.pptshow.build.pptbuilder.util.Safes;
import com.google.common.collect.Lists;
import org.apache.commons.compress.utils.Sets;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AboutBuildHelper {

    @Resource
    private PPTPageTypeService pptPageTypeService;

    @Resource
    private ColorStyleService colorStyleService;

    public String buildByStyle(GlobalStyle globalStyle) {
        StringBuilder about = new StringBuilder(globalStyle.getTitle() + "是由"
                + globalStyle.getChannelType().getName() + "提供的一款精美PPT模板下载资源，"
                + "本PPT模板共有" + globalStyle.getPptPageIds().size() + "页，其中");
        List<Integer> pageIds = Lists.newArrayList(globalStyle.getPptPageIds());
        Set<Integer> haveSay = Sets.newHashSet();
        for (Integer pageId : pageIds) {
            if (haveSay.add(pageId)) {
                PPTPageType type = pptPageTypeService.findById(pageId);
                long count = pageIds.stream().filter(id -> id.equals(pageId)).count();
                about.append(type.getPageTypeName()).append("有").append(count).append("页、");
            }
        }
        about = new StringBuilder(about.substring(0, about.length() - 1));
        about.append("。");
        List<ColorStyle> colorStyles = colorStyleService.findByIds(globalStyle.getColorInfo().getColorType());
        List<String> colorNames = Safes.of(colorStyles).stream().map(ColorStyle::getColorStyleName).collect(Collectors.toList());
        String colorNameJoin = BConstant.JOINER.join(colorNames);
        about.append("PPT模板整体采用").append(colorNameJoin)
                .append("颜色，PPT模板的标题字体采用").append(globalStyle.getTitleFontInfo().getFontName())
                .append("，PPT模板的普通文本字体采用").append(globalStyle.getTitleFontInfo().getFontName());
        about.append("。感谢您浏览").append(globalStyle.getTitle()).append("相关内容，更多PPT模板仅在")
                .append(globalStyle.getChannelType().getName()).append("。");

        return about.toString();
    }

}
