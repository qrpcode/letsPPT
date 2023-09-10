package cc.pptshow.build.pptbuilder.biz.filter.region;

import cc.pptshow.build.pptbuilder.anno.ForRegion;
import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.qo.RegionFilterQo;
import cc.pptshow.build.pptbuilder.service.PPTBlockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@ForRegion(name = "是否最下面是文本框过滤")
public class TextShapeFilter implements RegionFilter {

    @Resource
    private PPTBlockService pptBlockService;

    @Override
    public List<PPTBlockPut> filterPPTBlockPut(RegionFilterQo regionFilterQo) {
        if (Objects.isNull(regionFilterQo.getOriginalBlockPut())) {
            return regionFilterQo.getBlockPuts();
        }
        List<PPTBlock> blocks = pptBlockService.findByPPTBlockPut(regionFilterQo.getOriginalBlockPut());
        boolean haveShape = isHaveBackgroundShape(blocks);
        List<PPTBlockPut> blockPuts = regionFilterQo.getBlockPuts();
        return blockPuts.stream()
                .filter(blockPut -> isHaveBackgroundShape(pptBlockService.findByPPTBlockPut(blockPut)) == haveShape)
                .collect(Collectors.toList());
    }

    public boolean isHaveBackgroundShape(List<PPTBlock> blocks) {
        double allArea = findAllArea(blocks);
        return blocks.stream()
                .filter(block -> StringUtils.equals(block.getPptBlockType(), PPTBlockType.SHAPE.getCode()))
                .anyMatch(block -> block.getWidthSize() * block.getHeightSize() > allArea * 0.8);
    }

    private double findAllArea(List<PPTBlock> blocks) {
        double bottom = 0;
        double right = 0;
        for (PPTBlock block : blocks) {
            right = Math.max(right, block.getLeftSize() + block.getWidthSize());
            bottom = Math.max(bottom, block.getTopSize() + block.getHeightSize());
        }
        return bottom * right;
    }

}
