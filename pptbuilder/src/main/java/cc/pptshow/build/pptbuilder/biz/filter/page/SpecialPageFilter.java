package cc.pptshow.build.pptbuilder.biz.filter.page;

import cc.pptshow.build.pptbuilder.anno.ForPage;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.util.Safes;
import cc.pptshow.build.pptbuilder.util.SpecialTypeUtl;
import cc.pptshow.ppt.show.PPTShow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 特殊用途过滤
 */
@Slf4j
@Service
@ForPage(force = true, name = "特殊用途过滤")
public class SpecialPageFilter implements PageFilter {

    @Override
    public List<PPTRegionPut> filterPPTRegionPut(List<PPTRegionPut> regionPuts,
                                                 GlobalStyle globalStyle,
                                                 PPTShow pptShow) {
        int specialType = SpecialTypeUtl.findSpecialType(globalStyle);
        return Safes.of(regionPuts).stream()
                .filter(put -> put.getSpecialType().equals(BConstant.SPECIAL_DEFAULT)
                        || put.getSpecialType().equals(specialType))
                .collect(Collectors.toList());
    }
}
