package cc.pptshow.build.pptbuilder.biz.builder.element;

import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockLine;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.qo.BuilderQo;
import cc.pptshow.build.pptbuilder.service.PPTBlockLineService;
import cc.pptshow.ppt.domain.PPTLineCss;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTLine;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LineElementBuild implements ElementBuilder {

    @Resource
    private PPTBlockLineService pptBlockLineService;

    @Override
    public List<PPTBlockType> canBuildTypes() {
        return Lists.newArrayList(PPTBlockType.LINE);
    }

    @Override
    public List<PPTElement> buildElement(BuilderQo builderQo) {
        PPTBlock pptBlock = builderQo.getPptBlock();
        PPTBlockLine blockLine = pptBlockLineService.findByBlockId(pptBlock.getId());
        PPTLine pptLine = new PPTLine();
        PPTLineCss.LineType lineType = Arrays.stream(PPTLineCss.LineType.values())
                .filter(l -> l.getCode().equals(blockLine.getLineType()))
                .collect(Collectors.toList()).stream()
                .findFirst()
                .orElse(null);
        pptLine.setCss(new PPTLineCss()
                .setWidth(pptBlock.getWidthSize())
                .setHeight(pptBlock.getHeightSize())
                .setLeft(pptBlock.getLeftSize() + builderQo.getLeftSize())
                .setTop(pptBlock.getTopSize() + builderQo.getTopSize())
                .setLineWidth(blockLine.getLineWidth())
                .setType(lineType)
        );
        return Lists.newArrayList(pptLine);
    }
}
