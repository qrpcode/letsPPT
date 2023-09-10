package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.biz.filter.region.PositionFilter;
import cc.pptshow.build.pptbuilder.constant.BConstant;
import cc.pptshow.build.pptbuilder.domain.*;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.ppt.domain.PPTImgCss;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTShape;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

import static cc.pptshow.build.pptbuilder.constant.BConstant.MAX_HEIGHT;
import static cc.pptshow.build.pptbuilder.constant.BConstant.MAX_WIDTH;

/**
 * 将icon都换成img
 * 这一步还没上色，所以不管详细图片
 */
@Slf4j
@Service
@Design(type = PPTBlockType.SHAPE, order = 1)
public class IconDesignBizImpl implements DesignBiz {

    @Resource
    private PositionFilter positionFilter;

    @Override
    public DesignResponse design(DesignRequest request) {
        if (!positionFilter.isIconShape(request.getPptElement())) {
            return DesignResponse.buildByRequest(request);
        }
        List<Object> contexts = request.getContexts();
        Assert.isTrue(CollectionUtils.isEmpty(contexts) || contexts.size() == 2, "当前上下文不合法");
        if (CollectionUtils.isEmpty(contexts)) {
            Canvas canvas = new Canvas(MAX_WIDTH, MAX_HEIGHT);
            Set<Integer> iconIndexes = Sets.newHashSet();
            request.setContexts(Lists.newArrayList(canvas, iconIndexes));
        }
        PPTImg pptImg = buildTopIconElement(request);
        pptImg.getCss().setName(BConstant.ICON);
        return new DesignResponse(pptImg, request);
    }

    private PPTImg buildTopIconElement(DesignRequest request) {
        PPTShape iconShape = (PPTShape) request.getPptElement();
        CanvasShape canvasShape = Canvas.buildCanvasByShape(iconShape);
        double findSize = findIconSize(canvasShape, (Canvas) request.getContexts().get(0));
        String iconPath = JSON.toJSONString((Set<Integer>) request.getContexts().get(1));
        PPTImg pptImg = buildPPTImgByIcon(iconShape, findSize, iconPath);
        log.info("[icon pptImg]: {}", pptImg.getCss());
        return pptImg;
    }

    private PPTImg buildPPTImgByIcon(PPTShape pptShape, double findSize, String iconPath) {
        PPTImg pptImg = new PPTImg();
        pptImg.setFile(iconPath);
        double left = pptShape.getCss().getLeft() + (pptShape.getCss().getWidth() / 2) - (findSize / 2);
        double top = pptShape.getCss().getTop() + (pptShape.getCss().getHeight() / 2) - (findSize / 2);
        pptImg.setCss(PPTImgCss.build().setWidth(findSize).setHeight(findSize).setLeft(left).setTop(top));
        return pptImg;
    }

    private double findIconSize(CanvasShape canvasShape, Canvas iconCanvas) {
        List<CanvasShape> canvasShapes = iconCanvas.allCanvasShape();
        for (CanvasShape shape : canvasShapes) {
            double area = shape.area();
            if (canvasShape.area() > area * 0.85 || canvasShape.area() < area * 1.15) {
                return shape.getRight() - shape.getLeft();
            }
        }
        iconCanvas.add(canvasShape);
        return ((canvasShape.getRight() - canvasShape.getLeft())
                + (canvasShape.getBottom() - canvasShape.getTop())) / 2;
    }


}
