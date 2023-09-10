package cc.pptshow.build.pptbuilder.biz.filter.region;

import cc.pptshow.build.pptbuilder.anno.ForRegion;
import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.domain.qo.RegionFilterQo;
import cc.pptshow.build.pptbuilder.service.PPTBlockService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.constant.BConstant.SPLITTER;

/**
 * 强制过滤
 * 过滤掉没有实现的内容
 */
@Service
@ForRegion(force = true, name = "空实现过滤")
public class NullElementFilter implements RegionFilter {

    @Resource
    private PPTBlockService pptBlockService;

    @Override
    public List<PPTBlockPut> filterPPTBlockPut(RegionFilterQo regionFilterQo) {
        List<PPTBlockPut> blockPuts = regionFilterQo.getBlockPuts();
        return blockPuts;
        /*.stream()
                .filter(blockPut -> {
                    List<PPTBlock> pptBlocks = pptBlockService.findByPPTBlockPut(blockPut);
                    List<String> ids = Lists.newArrayList(SPLITTER.split(blockPut.getPptBlockIds()));
                    ids.removeAll(pptBlocks.stream()
                            .map(PPTBlock::getId)
                            .map(id -> Long.toString(id))
                            .collect(Collectors.toList()));
                    return CollectionUtils.isEmpty(ids);
                })
                .collect(Collectors.toList());*/
    }

}
