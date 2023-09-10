package cc.pptshow.build.pptbuilder.biz.builder.element;

import cc.pptshow.build.pptbuilder.bean.PPTBlock;
import cc.pptshow.build.pptbuilder.bean.PPTBlockShape;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.qo.BuilderQo;
import cc.pptshow.build.pptbuilder.service.PPTBlockShapeService;
import cc.pptshow.ppt.domain.PPTShapeCss;
import cc.pptshow.ppt.domain.background.*;
import cc.pptshow.ppt.domain.border.Border;
import cc.pptshow.ppt.domain.border.ColorBorder;
import cc.pptshow.ppt.domain.shape.SelfShape;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTShape;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cc.pptshow.build.pptbuilder.constant.BConstant.ICON;
import static cc.pptshow.build.pptbuilder.constant.BConstant.SYS_PATH;

@Slf4j
@Service
public class ShapeElementBuilder implements ElementBuilder {

    @Resource
    private PPTBlockShapeService pptBlockShapeService;

    @Override
    public List<PPTBlockType> canBuildTypes() {
        return Lists.newArrayList(PPTBlockType.SHAPE);
    }

    @Override
    public List<PPTElement> buildElement(BuilderQo builderQo) {
        PPTBlock pptBlock = builderQo.getPptBlock();
        PPTBlockShape pptBlockShape = pptBlockShapeService.selectByBlockId(pptBlock.getId());
        SerializableBackground background = JSON.parseObject(pptBlockShape.getBackgroundColor(), SerializableBackground.class);
        Background shapeBackground = background.buildBackground();
        if (shapeBackground instanceof ImgBackground) {
            log.info("[PPT IMG BG]");
            ((ImgBackground) shapeBackground).setImg(SYS_PATH + "sucai1.jpg");
        }
        ColorBorder colorBorder = null;
        if (pptBlockShape.getBorderSize() > 0) {
            colorBorder = new ColorBorder().setWidth(pptBlockShape.getBorderSize());
        }
        PPTShape pptShape = new PPTShape();
        pptShape.setCss(PPTShapeCss.build()
                .setWidth(pptBlock.getWidthSize())
                .setHeight(pptBlock.getHeightSize())
                .setLeft(pptBlock.getLeftSize() + builderQo.getLeftSize())
                .setTop(pptBlock.getTopSize() + builderQo.getTopSize())
                .setShape(new SelfShape().setCustGeom(pptBlockShape.getShapeXml()))
                .setBackground(shapeBackground)
                .setBorder(colorBorder)
                .setFlipY(BooleanUtils.toBoolean(pptBlockShape.getFlipY()))
                .setFlipX(BooleanUtils.toBoolean(pptBlockShape.getFlipX()))
                .setAngle(pptBlockShape.getAngle()));

        emptyShapeHelper(shapeBackground, colorBorder, pptShape, builderQo.getGlobalStyle().getColorInfo().getFromColor());
        return Lists.newArrayList(pptShape);
    }

    private void emptyShapeHelper(Background shapeBackground, ColorBorder colorBorder,
                                  PPTShape pptShape, String fromColor) {
        if (Objects.isNull(shapeBackground) || shapeBackground instanceof NoBackground) {
            if (Objects.isNull(colorBorder) && !StringUtils.equals(pptShape.getCss().getName(), ICON)) {
                ColorBackGround colorBackGround = ColorBackGround.buildByColor(fromColor);
                pptShape.getCss().setBackground(colorBackGround);
            }
        }
    }
}
