package cc.pptshow.build.pptbuilder.biz.helper;

import cc.pptshow.build.pptbuilder.bean.ColorInfo;
import cc.pptshow.build.pptbuilder.biz.design.help.TextDesignHelper;
import cc.pptshow.build.pptbuilder.domain.Canvas;
import cc.pptshow.build.pptbuilder.domain.CanvasShape;
import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.service.ImgInfoService;
import cc.pptshow.build.pptbuilder.util.ColorUtil;
import cc.pptshow.build.pptbuilder.util.PositionUtil;
import cc.pptshow.build.pptbuilder.util.TextUtil;
import cc.pptshow.ppt.domain.Gradient;
import cc.pptshow.ppt.domain.background.*;
import cc.pptshow.ppt.domain.border.ColorBorder;
import cc.pptshow.ppt.element.PPTElement;
import cc.pptshow.ppt.element.impl.PPTImg;
import cc.pptshow.ppt.element.impl.PPTLine;
import cc.pptshow.ppt.element.impl.PPTShape;
import cc.pptshow.ppt.element.impl.PPTText;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cc.pptshow.build.pptbuilder.constant.BConstant.*;
import static org.apache.xerces.util.XMLSymbols.EMPTY_STRING;

/**
 * 次序颜色调整辅助类
 */
@Service
public class ColorHelper {

    @Resource
    private TextDesignHelper textDesignHelper;

    @Resource
    private ImgInfoService imgInfoService;

    public List<PPTElement> optimizeElementsColor(List<PPTElement> elements,
                                                  GlobalStyle globalStyle) {
        List<PPTElement> returnPPTElements = Lists.newArrayList();
        List<PPTElement> lineElements = Lists.newArrayList();
        Canvas canvas = new Canvas(MAX_WIDTH, MAX_HEIGHT);
        for (PPTElement element : elements) {
            if (!(element instanceof PPTText)) {
                if (element instanceof PPTShape) {
                    returnPPTElements.add(optimizeShape(globalStyle, (PPTShape) element, canvas));
                } else if (element instanceof PPTImg) {
                    returnPPTElements.add(optimizeImg((PPTImg) element));
                } else if (element instanceof PPTLine) {
                    lineElements.add(element);
                } else {
                    returnPPTElements.add(element);
                }
            }
        }
        returnPPTElements = putShowImgTop(returnPPTElements);
        for (PPTElement element : elements) {
            if (element instanceof PPTText) {
                returnPPTElements.add(optimizeText(globalStyle, (PPTText) element, elements));
            }
        }
        lineElements.addAll(returnPPTElements);
        return lineElements;
    }

    private List<PPTElement> putShowImgTop(List<PPTElement> elements) {
        List<PPTElement> imgTopList = Lists.newArrayList();
        List<PPTElement> needTopElements = Lists.newArrayList();
        for (PPTElement element : elements) {
            if (!isImg(element)) {
                imgTopList.add(element);
            } else {
                double textArea = PositionUtil.calculateElementArea(element);
                List<PPTElement> overElements = queryOverElements(element, elements, textArea)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(overElements)) {
                    needTopElements.add(element);
                } else {
                    imgTopList.add(element);
                }
            }
        }
        imgTopList.addAll(needTopElements);
        return imgTopList;
    }

    private boolean isImg(PPTElement pptElement) {
        if (pptElement instanceof PPTImg) {
            return true;
        } else {
            return pptElement instanceof PPTShape
                    && ((PPTShape) pptElement).getCss().getBackground() instanceof ImgBackground;
        }
    }

    private PPTElement optimizeImg(PPTImg element) {
        element.setFile(imgInfoService.randInnerImg());
        return element;
    }

    private PPTShape optimizeShape(GlobalStyle globalStyle, PPTShape element, Canvas canvas) {
        ColorInfo colorInfo = globalStyle.getColorInfo();
        Double alpha = findAlpha(element, canvas, globalStyle);
        if (element.getCss().getBackground() instanceof ColorBackGround) {
            optimizeColorBackgroundShape(globalStyle, element, colorInfo, alpha);
        } else if (element.getCss().getBackground() instanceof GradientBackground) {
            gradientBackgroundChoose(globalStyle, element);
        } else if (element.getCss().getBackground() instanceof ImgBackground) {
            ((ImgBackground) element.getCss().getBackground()).setImg(imgInfoService.randInnerImg());
        }
        if (element.getCss().getBorder() instanceof ColorBorder) {
            if (((ColorBorder) element.getCss().getBorder()).getWidth() > 0) {
                ((ColorBorder) element.getCss().getBorder()).setColor(colorInfo.getFromColor());
            }
        }
        canvas.addShape(element, alpha);
        return element;
    }

    /**
     * 寻找合适透明度
     */
    public Double findAlpha(PPTShape element, Canvas canvas, GlobalStyle globalStyle) {
        /*if (ColorUtil.isDeepColor(globalStyle.getColorInfo().getBackgroundColor())) {
            return 0D;
        }*/
        double area = element.getCss().getWidth() * element.getCss().getHeight();
        List<CanvasShape> intersectElements =
                canvas.findOtherMoreThanHarfIntersectSelf(element, Canvas.buildCanvasByShape(element));
        if (CollectionUtils.isEmpty(intersectElements)) {
            return 0D;
        }
        if (canvas.queryByShapes(intersectElements).stream().anyMatch(e -> e instanceof PPTText)) {
            return 0D;
        }
        double alpha = 0D;
        if (area > MAX_HEIGHT * MAX_WIDTH * 0.25) {
            alpha = (area / (MAX_HEIGHT * MAX_WIDTH)) * 80;
            alpha = Math.max(alpha, 80);
            alpha = Math.max(alpha, 0);
        }
        return alpha;
    }

    private void optimizeColorBackgroundShape(GlobalStyle globalStyle, PPTShape element, ColorInfo colorInfo, Double alpha) {
        String color = ((ColorBackGround) element.getCss().getBackground()).getColor();
        String styleColor = globalStyle.findColor(color);
        if (Strings.isNotEmpty(styleColor)) {
            element.getCss().setBackground(buildColorBackground(styleColor, alpha));
        } else {
            element.getCss().setBackground(buildColorBackground(colorInfo.getFromColor(), alpha));
        }
    }

    private ColorBackGround buildColorBackground(String color, Double alpha) {
        if (Objects.isNull(alpha)) {
            return ColorBackGround.buildByColor(color);
        } else {
            return ColorBackGround.buildByColor(color, alpha);
        }
    }

    private void gradientBackgroundChoose(GlobalStyle globalStyle, PPTShape element) {
        GradientBackground background = (GradientBackground) element.getCss().getBackground();
        List<Gradient> gradients = background.getGradient();
        if (gradients.size() == 2 && Strings.isNotBlank(globalStyle.getColorInfo().getToColor())) {
            if (background.getGradientDirection() < 180) {
                gradients.get(1).setColor(globalStyle.getColorInfo().getToColor());
                gradients.get(0).setColor(globalStyle.getColorInfo().getFromColor());
            } else {
                gradients.get(0).setColor(globalStyle.getColorInfo().getToColor());
                gradients.get(1).setColor(globalStyle.getColorInfo().getFromColor());
            }
        } else if (Strings.isBlank(globalStyle.getColorInfo().getToColor())) {
            if (background.getGradientDirection() < 180) {
                for (int i = 0; i < gradients.size(); i++) {
                    gradients.get(i)
                            .setColor(globalStyle.getColorInfo().getFromColor())
                            .setAlpha(10.0 * i);
                }
            } else {
                for (int i = 0; i < gradients.size(); i++) {
                    gradients.get(gradients.size() - 1 - i)
                            .setColor(globalStyle.getColorInfo().getFromColor())
                            .setAlpha(10.0 * i);
                }
            }
        } else {
            gradients.clear();
            gradients.addAll(Lists.newArrayList(
                    new Gradient().setColor(globalStyle.getColorInfo().getFromColor()).setProportion(0),
                    new Gradient().setColor(globalStyle.getColorInfo().getToColor()).setProportion(100)
            ));
        }
        element.getCss().setBackground(background);
    }

    private PPTElement optimizeText(GlobalStyle globalStyle, PPTText pptText, List<PPTElement> allElements) {
        String color = DEFAULT_FONT_COLOR;
        if (isHaveBackground(pptText, allElements, globalStyle)) {
            color = WHITE;
        }
        String finalColor = color;
        textDesignHelper.setAllTextColor(pptText, finalColor);
        String fontName = globalStyle.getTextFontInfo().getFontCode();
        if (TextUtil.isBigTitle(pptText.findAllText())) {
            fontName = globalStyle.getTitleFontInfo().getFontCode();
        }
        String finalFontName = fontName;
        pptText.getLineList()
                .forEach(pptInnerLine -> pptInnerLine.getTextList()
                        .forEach(pptInnerText ->
                                pptInnerText.getCss().setFontFamily(finalFontName)));
        return pptText;
    }

    /**
     * 判断一个文字对象是不是在一个深色图形上方的
     */
    private boolean isHaveBackground(PPTText pptText, List<PPTElement> allElements, GlobalStyle globalStyle) {
        double textArea = PositionUtil.calculateElementArea(pptText);
        List<PPTElement> overElements = queryOverElements(pptText, allElements, textArea)
                .filter(pptElement -> {
                    if (pptElement instanceof PPTShape) {
                        Background background = ((PPTShape) pptElement).getCss().getBackground();
                        return isHaveDeepColor(background);
                    }
                    return true;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(overElements)) {
            return ColorUtil.isDeepColor(globalStyle.getColorInfo().getBackgroundColor());
        } else {
            return true;
        }
    }

    private Stream<PPTElement> queryOverElements(PPTElement element,
                                                 List<PPTElement> allElements,
                                                 double textArea) {
        return allElements.stream()
                .filter(pptElement -> !element.equals(pptElement))
                .filter(pptElement ->
                        PositionUtil.calculateOverlappingArea(element, pptElement)
                                >= TEXT_MIN_BACKGROUND * textArea);
    }

    private boolean isHaveDeepColor(Background background) {
        if (background instanceof ColorBackGround) {
            return ColorUtil.isDeepColor(((ColorBackGround) background).getColor());
        } else if (background instanceof GradientBackground) {
            return ColorUtil.isDeepColor(((GradientBackground) background).getGradient());
        }
        return false;
    }

    public String getShapeContrastColor(GlobalStyle globalStyle) {
        return globalStyle.getColorInfo().getFromColor();
    }

    public String getShapeNormalColor(GlobalStyle globalStyle) {
        return globalStyle.getColorInfo().getBackgroundColor();
    }

    public String findDifferentColor(PPTElement topmostLayer, ColorInfo colorInfo, String defaultColor) {
        String lightFromColor = getLightColor(colorInfo.getFromColor(), 0.9);
        if (topmostLayer instanceof PPTImg) {
            return lightFromColor;
        } else if (topmostLayer instanceof PPTShape) {
            Background background = ((PPTShape) topmostLayer).getCss().getBackground();
            if (background instanceof ColorBackGround) {
                String color = ((ColorBackGround) background).getColor();
                Double alpha = ((ColorBackGround) background).getAlpha();
                if (Objects.isNull(alpha)) {
                    alpha = 0.0;
                }
                if (StringUtils.equals(color, colorInfo.getFromColor())) {
                    return lightFromColor;
                } else {
                    boolean deepColor = ColorUtil.isDeepColor(color, alpha);
                    boolean deepFromColor = ColorUtil.isDeepColor(colorInfo.getFromColor());
                    if ((deepColor && !deepFromColor) || (!deepColor && deepFromColor)) {
                        return colorInfo.getFromColor();
                    } else {
                        if (deepColor) {
                            return lightFromColor;
                        } else {
                            return getDeepColor(colorInfo.getFromColor(), 0.3);
                        }
                    }
                }
            } else {
                return lightFromColor;
            }
        } else {
            return defaultColor;
        }
    }


    public String getLightFromColor(ColorInfo colorInfo) {
        return getLightColor(colorInfo.getFromColor());
    }

    public String getLightColor(String color) {
        return getLightColor(color, 0.5);
    }

    public String getLightColor(String color, double light) {
        String lightFromColor = ColorUtil.toHex(ColorUtil.getLightColor(
                ColorUtil.convertHexToRGB(color), light));
        lightFromColor = lightFromColor.startsWith(COLOR_HEAD) ? lightFromColor.replace(COLOR_HEAD, EMPTY_STRING) : lightFromColor;
        return lightFromColor;
    }

    private String getDeepColor(String fromColor, double deep) {
        String deepFromColor = ColorUtil.toHex(ColorUtil.getDeepColor(
                ColorUtil.convertHexToRGB(fromColor), deep));
        deepFromColor = deepFromColor.startsWith(COLOR_HEAD)
                ? deepFromColor.replace(COLOR_HEAD, EMPTY_STRING) : deepFromColor;
        return deepFromColor;
    }

    public PPTElement findTopmostLayer(CanvasShape shape, List<PPTElement> elements) {
        return findCoverElement(shape, elements, 1);
    }

    public PPTElement findHalfTopmostLayerWithoutText(CanvasShape textShape, List<PPTElement> elements) {
        return findCoverElement(textShape, elements, 0.7);
    }

    @Nullable
    private PPTElement findCoverElement(CanvasShape textShape, List<PPTElement> elements, double proportion) {
        Collections.reverse(elements);
        for (PPTElement element : elements) {
            if (element instanceof PPTShape && ((PPTShape) element).getCss().getBackground() instanceof NoBackground) {
                continue;
            }
            if (element instanceof PPTText) {
                continue;
            }
            if (Canvas.buildCanvasByElement(element).intersectArea(textShape) >= textShape.area() * proportion) {
                return element;
            }
        }
        return null;
    }
}
