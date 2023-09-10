package cc.pptshow.build.pptbuilder.biz.filter.region;

import cc.pptshow.build.pptbuilder.anno.ForRegion;
import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.qo.RegionFilterQo;
import cc.pptshow.build.pptbuilder.service.PPTBlockService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 一致风格校验
 * 如果原来不包含文字，那新的模板块也不能带文字
 * 如果原来使用一块图形做背景新的也需要使用一块图形作为背景
 */
@Slf4j
@Service
@ForRegion(name = "一致风格校验")
public class SameStyleFilter implements RegionFilter {

    @Resource
    private PPTBlockService pptBlockService;

    @Override
    public List<PPTBlockPut> filterPPTBlockPut(RegionFilterQo regionFilterQo) {
        PPTBlockPut originalBlockPut = regionFilterQo.getOriginalBlockPut();
        if (Objects.isNull(originalBlockPut)
                || originalNotHaveBlock(originalBlockPut)) {
            return regionFilterQo.getBlockPuts();
        }
        List<PPTBlock> pptBlocks = pptBlockService.findByPPTBlockPut(originalBlockPut);
        boolean haveShapeBackground = isHaveShapeBackground(pptBlocks);
        boolean haveText = isHaveText(pptBlocks);
        return regionFilterQo.getBlockPuts().stream()
                .filter(blockPut -> {
                    List<PPTBlock> putBlocks = pptBlockService.findByPPTBlockPut(blockPut);
                    return isHaveShapeBackground(putBlocks) == haveShapeBackground
                            && isHaveText(putBlocks) == haveText;
                })
                .collect(Collectors.toList());
    }

    private boolean originalNotHaveBlock(PPTBlockPut originalBlockPut) {
        List<PPTBlock> pptBlocks = pptBlockService.findByPPTBlockPut(originalBlockPut);
        return Lists.newArrayList(BConstant.SPLITTER.split(originalBlockPut.getPptBlockIds())).size() != pptBlocks.size();
    }

    private boolean isHaveShapeBackground(List<PPTBlock> pptBlocks) {
        double allAreaSize = findAllAreaSize(pptBlocks);
        return pptBlocks.stream()
                .filter(pptBlock -> pptBlock.getPptBlockType().equals(PPTBlockType.SHAPE.getCode()))
                .anyMatch(pptBlock -> pptBlock.getWidthSize() * pptBlock.getHeightSize() > allAreaSize * 0.85);
    }

    private double findAllAreaSize(List<PPTBlock> pptBlocks) {
        List<Double> doubles = pptBlocks.stream()
                .map(this::getBlockMaxSize)
                .sorted(Double::compareTo)
                .collect(Collectors.toList());
        return doubles.get(doubles.size() - 1);
    }

    private double getBlockMaxSize(PPTBlock pptBlock) {
        return (pptBlock.getLeftSize() + pptBlock.getWidthSize())
                * (pptBlock.getTopSize() + pptBlock.getHeightSize());
    }

    private boolean isHaveText(List<PPTBlock> pptBlocks) {
        return pptBlocks.stream()
                .anyMatch(pptBlock -> pptBlock.getPptBlockType().equals(PPTBlockType.TEXT.getCode()));
    }

}
