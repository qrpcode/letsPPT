package cc.pptshow.build.pptbuilder.biz.filter.page;

import cc.pptshow.build.pptbuilder.anno.ForPage;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.bean.PPTRegion;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.service.PPTBlockPutService;
import cc.pptshow.build.pptbuilder.service.PPTRegionPutService;
import cc.pptshow.build.pptbuilder.service.PPTRegionService;
import cc.pptshow.ppt.show.PPTShow;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.constant.BConstant.SPLITTER;

/**
 * 底层实现上不能存在没实现的元素
 */
@Service
@ForPage(force = true, name = "空实现过滤")
public class NullPageFilter implements PageFilter {

    @Resource
    private PPTRegionService pptRegionService;

    @Resource
    private PPTBlockPutService pptBlockPutService;

    @Resource
    private PPTRegionPutService pptRegionPutService;

    @Override
    public List<PPTRegionPut> filterPPTRegionPut(List<PPTRegionPut> regionPuts,
                                                 GlobalStyle globalStyle,
                                                 PPTShow pptShow) {
        List<PPTRegionPut> notCheckList = regionPuts.stream()
                .filter(regionPut -> Objects.isNull(regionPut.getCompleteType()))
                .collect(Collectors.toList());
        for (PPTRegionPut pptRegionPut : notCheckList) {
            //过滤region没实现
            List<PPTRegion> regions = pptRegionService.queryRegionsByBlock(pptRegionPut);
            List<String> ids = Lists.newArrayList(SPLITTER.split(pptRegionPut.getPptRegionIds()));
            regions.forEach(region -> ids.remove(Long.toString(region.getId())));
            if (!CollectionUtils.isEmpty(ids)) {
                pptRegionPut.setCompleteType(0);
            }
            //过滤block没实现
            for (PPTRegion region : regions) {
                PPTBlockPut pptBlockPut = pptBlockPutService
                        .selectByRegionIds(Long.toString(region.getId())).stream()
                        .findFirst()
                        .orElse(null);
                if (Objects.isNull(pptBlockPut)) {
                    pptRegionPut.setCompleteType(0);
                }
            }
            if (Objects.isNull(pptRegionPut.getCompleteType())) {
                pptRegionPut.setCompleteType(1);
            }
            pptRegionPutService.update(pptRegionPut);
        }
        return regionPuts.stream()
                .filter(regionPut -> regionPut.getCompleteType() == 1)
                .collect(Collectors.toList());
    }
}
