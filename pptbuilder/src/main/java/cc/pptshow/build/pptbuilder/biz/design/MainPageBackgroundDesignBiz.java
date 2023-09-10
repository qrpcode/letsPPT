package cc.pptshow.build.pptbuilder.biz.design;

import cc.pptshow.build.pptbuilder.anno.Design;
import cc.pptshow.build.pptbuilder.bean.PPTRegionPut;
import cc.pptshow.build.pptbuilder.domain.DesignRequest;
import cc.pptshow.build.pptbuilder.domain.DesignResponse;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.PPTBackgroundType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;
import cc.pptshow.build.pptbuilder.service.EnumPPTBackgroundService;
import cc.pptshow.build.pptbuilder.service.ImgInfoService;
import cc.pptshow.build.pptbuilder.util.RandUtil;
import cc.pptshow.ppt.domain.Gradient;
import cc.pptshow.ppt.domain.PPTImgCss;
import cc.pptshow.ppt.domain.PPTShapeCss;
import cc.pptshow.ppt.domain.PPTSideCss;
import cc.pptshow.ppt.domain.background.Background;
import cc.pptshow.ppt.domain.background.GradientBackground;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.show.PPTShowSide;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cc.pptshow.build.pptbuilder.constant.BConstant.*;
import static cc.pptshow.build.pptbuilder.constant.BConstant.SPLITTER;

/**
 * 查看当前是否需要添加背景
 * 如果需要就得加上背景图案
 */
@Slf4j
@Service
@Design(order = -2, needIteration = false, onlyInPage = {PPTPage.HOME, PPTPage.BIG_TITLE})
public class MainPageBackgroundDesignBiz implements DesignBiz{

    private static final double BORDER_SIZE = 0.5;

    @Resource
    private ImgInfoService imgInfoService;

    @Resource
    private EnumPPTBackgroundService enumPPTBackgroundService;

    @Override
    public DesignResponse design(DesignRequest request) {
        List<PPTElement> bgElements = buildSlideBackground(request.getGlobalStyle(),
                request.getPptRegionPut(), request.getPptShowSide());
        bgElements.addAll(request.getPptElements());
        return new DesignResponse(bgElements);
    }

    @NotNull
    private List<PPTElement> buildSlideBackground(GlobalStyle globalStyle,
                                                  PPTRegionPut pptRegionPut,
                                                  PPTShowSide pptShowSide) {
        List<PPTElement> elements = Lists.newArrayList();
        String backgroundColor = globalStyle.getColorInfo().getBackgroundColor();
        PPTBackgroundType backgroundType = enumPPTBackgroundService.findById(pptRegionPut.getPageBackground());
        if (Objects.isNull(backgroundType) || backgroundType.equals(PPTBackgroundType.NONE)) {
            if (!StringUtils.equals(backgroundColor, WHITE)) {
                pptShowSide.setCss(PPTSideCss.build().setBackground(getOnlyColorBackground(globalStyle)));
            }
            return Lists.newArrayList();
        } else if (backgroundType.equals(PPTBackgroundType.LEFT)) {
            PPTImg pptImg = getBackgroundImgFullHeight(globalStyle);
            elements.add(pptImg);
            PPTShape pptShape = getPPTShapeOnImg(globalStyle, PPTBackgroundType.LEFT);
            elements.add(pptShape);
        } else if (backgroundType.equals(PPTBackgroundType.RIGHT)) {
            PPTImg pptImg = getBackgroundImgFullHeight(globalStyle);
            elements.add(pptImg);
            PPTShape pptShape = getPPTShapeOnImg(globalStyle, PPTBackgroundType.RIGHT);
            elements.add(pptShape);
        } else if (backgroundType.equals(PPTBackgroundType.TOP)) {
            PPTImg pptImg = getBackgroundImgFullHeight(globalStyle);
            elements.add(pptImg);
            PPTShape pptShape = getPPTShapeOnImg(globalStyle, PPTBackgroundType.TOP);
            elements.add(pptShape);
        } else if (backgroundType.equals(PPTBackgroundType.BOTTOM)) {
            PPTImg pptImg = getBackgroundImgFullHeight(globalStyle);
            elements.add(pptImg);
            PPTShape pptShape = getPPTShapeOnImg(globalStyle, PPTBackgroundType.BOTTOM);
            elements.add(pptShape);
        } else if (backgroundType.equals(PPTBackgroundType.AROUND)) {
            PPTImg pptImg = getBackgroundImgFullHeight(globalStyle);
            elements.add(pptImg);
            PPTShape pptShape = getPPTShapeOnImg(globalStyle, PPTBackgroundType.AROUND);
            elements.add(pptShape);
        } else if (backgroundType.equals(PPTBackgroundType.MARK)) {
            PPTImg pptImg = getBackgroundImgFullHeight(globalStyle);
            elements.add(pptImg);
            PPTShape pptShape = getPPTShapeOnImg(globalStyle, PPTBackgroundType.MARK);
            elements.add(pptShape);
        }
        return elements;
    }

    /**
     * 处理左侧、右侧、覆盖case
     */
    private PPTImg getBackgroundImgFullHeight(GlobalStyle globalStyle) {
        PPTImg pptImg = new PPTImg();
        String file = getBackgroundImgFilePathByStyle(globalStyle);
        pptImg.setFile(file);
        pptImg.setCss(PPTImgCss.build().setHeight(MAX_HEIGHT).setWidth(MAX_WIDTH).setLeft(0).setTop(0));
        return pptImg;
    }

    private PPTShape getPPTShapeOnImg(GlobalStyle globalStyle, PPTBackgroundType type) {
        PPTShape pptShape = new PPTShape();
        String color = globalStyle.getColorInfo().getBackgroundColor();
        PPTShapeCss pptShapeCss = PPTShapeCss.build()
                .setBackground(new GradientBackground()
                        .setGradientDirection(getGlobalTiltByType(globalStyle, type))
                        .setGradient(getGradientByBackgroundType(color, type))
                );
        buildCssLocation(pptShapeCss, type);
        pptShape.setCss(pptShapeCss);
        return pptShape;
    }

    private void buildCssLocation(PPTShapeCss pptShapeCss, PPTBackgroundType type) {
        if (type.equals(PPTBackgroundType.AROUND)) {
            pptShapeCss.setLeft(BORDER_SIZE).setTop(BORDER_SIZE).setWidth(PAGE_WIDTH - (2 * BORDER_SIZE))
                    .setHeight(PAGE_HEIGHT - (2 * BORDER_SIZE));
        } else {
            pptShapeCss.setLeft(0).setTop(0).setWidth(PAGE_WIDTH).setHeight(PAGE_HEIGHT);
        }
    }

    private double getGlobalTiltByType(GlobalStyle globalStyle, PPTBackgroundType type) {
        return (type.equals(PPTBackgroundType.TOP) || type.equals(PPTBackgroundType.BOTTOM)) ?
                globalStyle.getGlobalTilt() + 90 : globalStyle.getGlobalTilt();
    }

    @NotNull
    private ArrayList<Gradient> getGradientByBackgroundType(String color, PPTBackgroundType type) {
        if (type.equals(PPTBackgroundType.RIGHT) || type.equals(PPTBackgroundType.TOP)) {
            return Lists.newArrayList(
                    Gradient.build().setColor(color).setProportion(42).setAlpha(0.0),
                    Gradient.build().setColor(color).setProportion(25).setAlpha(100.0),
                    Gradient.build().setColor(color).setProportion(0).setAlpha(100.0)
            );
        } else if (type.equals(PPTBackgroundType.LEFT) || type.equals(PPTBackgroundType.BOTTOM)) {
            return Lists.newArrayList(
                    Gradient.build().setColor(color).setProportion(0).setAlpha(0.0),
                    Gradient.build().setColor(color).setProportion(68).setAlpha(0.0),
                    Gradient.build().setColor(color).setProportion(75).setAlpha(100.0)
            );
        } else if (type.equals(PPTBackgroundType.AROUND)) {
            return Lists.newArrayList(
                    Gradient.build().setColor(color).setProportion(0),
                    Gradient.build().setColor(color).setProportion(100)
            );
        } else if (type.equals(PPTBackgroundType.MARK)) {
            return Lists.newArrayList(
                    Gradient.build().setColor(color).setProportion(0).setAlpha(40.0),
                    Gradient.build().setColor(color).setProportion(100).setAlpha(40.0)
            );
        }
        throw new RuntimeException("可能存在没有被实现的功能！");
    }

    private Background getOnlyColorBackground(GlobalStyle globalStyle) {
        return new GradientBackground()
                .setGradientDirection(globalStyle.getGlobalLight())
                .setGradient(Lists.newArrayList(
                        Gradient.build().setAlpha(RandUtil.round(10, 30) * 1.0).setProportion(0).setColor(globalStyle.getColorInfo().getBackgroundColor()),
                        Gradient.build().setProportion(100).setColor(globalStyle.getColorInfo().getBackgroundColor())
                ));
    }

    private String getBackgroundImgFilePathByStyle(GlobalStyle globalStyle) {
        Integer colorType = globalStyle.getColorInfo().getBackgroundColorType();
        if (Objects.isNull(colorType) || colorType == 0) {
            List<String> types = Lists.newArrayList(SPLITTER.split(globalStyle.getColorInfo().getColorType()));
            colorType = Integer.parseInt(Objects.requireNonNull(RandUtil.randElement(types)));
        }
        return imgInfoService.queryOnceBackgroundByRand(colorType);
    }

}
