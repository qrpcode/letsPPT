package cc.pptshow.build.pptbuilder.biz.filter.region;

import cc.pptshow.build.pptbuilder.anno.ForRegion;
import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockPut;
import cc.pptshow.build.pptbuilder.domain.ElementLocation;
import cc.pptshow.build.pptbuilder.domain.enums.Position;
import cc.pptshow.build.pptbuilder.domain.qo.RegionFilterQo;
import cc.pptshow.build.pptbuilder.service.PPTBlockService;
import cc.pptshow.build.pptbuilder.util.MathUtil;
import cc.pptshow.build.pptbuilder.util.PositionUtil;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTLine;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cc.pptshow.build.pptbuilder.constant.BConstant.ICON;
import static cc.pptshow.build.pptbuilder.constant.BConstant.LOGO;

/**
 * 非强制过滤
 * 过滤掉同等方向的内容
 */
@Slf4j
@Service
@ForRegion(name = "左右对齐风格过滤")
public class PositionFilter implements RegionFilter {

    /**
     * 图形宽度或者长度小于多少被视为线条
     */
    private static final double MIN_SHAPE_WIDTH = 0.1;

    @Resource
    private PPTBlockService pptBlockService;

    @Override
    public List<PPTBlockPut> filterPPTBlockPut(RegionFilterQo regionFilterQo) {
        List<PPTBlockPut> pptBlockPuts = regionFilterQo.getBlockPuts();
        Position position = regionFilterQo.getPosition();
        if (position.equals(Position.NULL) || position.equals(Position.TOP) || position.equals(Position.BOTTOM)) {
            return pptBlockPuts;
        }
        List<PPTBlockPut> blockPuts;
        if (position.equals(Position.LEFT)) {
            blockPuts = pptBlockPuts.stream().filter(this::maybeRight).collect(Collectors.toList());
        } else {
            blockPuts = pptBlockPuts.stream().filter(this::maybeLeft).collect(Collectors.toList());
        }
        return blockPuts;
    }

    private boolean maybeRight(PPTBlockPut pptBlockPut) {
        List<PPTBlock> blockPut = pptBlockService.findByPPTBlockPut(pptBlockPut);
        return !Position.LEFT.equals(mainEdge(blockPut));
    }

    private boolean maybeLeft(PPTBlockPut pptBlockPut) {
        List<PPTBlock> blockPut = pptBlockService.findByPPTBlockPut(pptBlockPut);
        return !Position.RIGHT.equals(mainEdge(blockPut));
    }

    private Position mainEdge(List<PPTBlock> blockPut) {
        List<Double> allLeft = blockPut.stream().map(PPTBlock::getLeftSize).collect(Collectors.toList());
        List<Double> allRight = blockPut.stream().map(p -> p.getLeftSize() + p.getWidthSize()).collect(Collectors.toList());
        double leftVariance = MathUtil.variance(allLeft);
        double rightVariance = MathUtil.variance(allRight);
        if (leftVariance * 2 < rightVariance) {
            return Position.LEFT;
        } else if (rightVariance * 2 < leftVariance) {
            return Position.RIGHT;
        } else {
            return Position.NULL;
        }
    }

    public boolean isLineElement(PPTElement e) {
        return e instanceof PPTLine || ((e instanceof PPTShape)
                && (((PPTShape) e).getCss().getWidth() < MIN_SHAPE_WIDTH
                || ((PPTShape) e).getCss().getHeight() < MIN_SHAPE_WIDTH));
    }

    public List<PPTElement> sortElements(List<PPTElement> backgrounds) {
        return backgrounds.stream().sorted((element1, element2) -> {
            ElementLocation location1 = PositionUtil.findLocationByElement(element1);
            ElementLocation location2 = PositionUtil.findLocationByElement(element2);
            if (Objects.isNull(location1) || Objects.isNull(location2)) {
                return 0;
            }
            return (int) ((location1.getWidth() * location1.getHeight())
                    - (location2.getWidth() * location2.getHeight()));
        }).collect(Collectors.toList());
    }

    public List<PPTText> findAllTextElements(List<PPTElement> pptElements) {
        return pptElements.stream()
                .filter(p -> p instanceof PPTText)
                .map(p -> (PPTText) p)
                .collect(Collectors.toList());
    }

    public List<PPTShape> findAllIconElements(List<PPTElement> pptElements) {
        return pptElements.stream()
                .filter(this::isIconShape)
                .map(s -> (PPTShape) s)
                .collect(Collectors.toList());
    }

    public boolean isIconShape(PPTElement pptElement) {
        return pptElement instanceof PPTShape && StringUtils.equals(((PPTShape) pptElement).getCss().getName(), ICON);
    }

    public boolean isLogoShape(PPTElement pptElement) {
        return pptElement instanceof PPTShape && StringUtils.equals(((PPTShape) pptElement).getCss().getName(), LOGO);
    }

}
