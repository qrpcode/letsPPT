package cc.pptshow.build.pptbuilder.util;

import cc.pptshow.build.pptbuilder.bean.ColorInfo;
import cc.pptshow.build.pptbuilder.domain.RGB;
import cc.pptshow.build.pptbuilder.style.PPTColorStyle;
import cc.pptshow.ppt.domain.Gradient;
import cc.pptshow.ppt.domain.background.Background;
import cc.pptshow.ppt.domain.background.GradientBackground;
import cn.hutool.core.lang.Assert;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 颜色工具类
 */
public class ColorUtil {

    private static final String s = "0123456789ABCDEF";
    private static final Pattern color = Pattern.compile("([0-9a-fA-F]{6})");
    private static final int MIN_LIGHT = 150;

    public static RGB convertHexToRGB(String hex) {
        RGB rgb = null;
        if (hex != null) {
            hex = hex.toUpperCase();
            if (hex.charAt(0) == '#') {
                hex = hex.substring(1);
            }
            if (color.matcher(hex).matches()) {
                String a, c, d;
                String[] str = new String[3];
                for (int i = 0; i < 3; i++) {
                    a = hex.length() == 6 ? hex.substring(i * 2, i * 2 + 2) : hex.charAt(i) + hex.substring(i, i + 1);
                    c = a.substring(0, 1);
                    d = a.substring(1, 2);
                    str[i] = String.valueOf(s.indexOf(c) * 16 + s.indexOf(d));
                }
                rgb = new RGB(Integer.parseInt(str[0]), Integer.parseInt(str[1]), Integer.parseInt(str[2]));
            }
        }
        return rgb;
    }

    /**
     * 是否是浅色系，浅色系不能作为文字颜色
     */
    public static boolean isLightColor(String color) {
        RGB rgb = convertHexToRGB(color);
        return rgb.getR() > MIN_LIGHT && rgb.getG() > MIN_LIGHT && rgb.getB() > MIN_LIGHT;
    }

    public static String toMainColor(ColorInfo colorInfo) {
        String mainColor = null;
        if (Strings.isNullOrEmpty(colorInfo.getToColor())) {
            mainColor = colorInfo.getFromColor();
        } else {
            boolean fromLight = ColorUtil.isLightColor(colorInfo.getFromColor());
            boolean toLight = ColorUtil.isLightColor(colorInfo.getToColor());
            if (fromLight && !toLight) {
                mainColor = colorInfo.getToColor();
            } else if (!fromLight && toLight) {
                mainColor = colorInfo.getFromColor();
            } else {
                int round = RandUtil.round(1, 2);
                if (round == 1) {
                    mainColor = colorInfo.getFromColor();
                } else {
                    mainColor = colorInfo.getToColor();
                }
            }
        }
        return mainColor;
    }

    public static Background alphaBackground(String color, int alpha) {
        List<Gradient> gradients = Lists.newArrayList(
                Gradient.build().setColor(color).setAlpha(100.00 - alpha).setProportion(0),
                Gradient.build().setColor(color).setAlpha(100.00 - alpha).setProportion(100));
        return new GradientBackground().setGradientDirection(0.0).setGradient(gradients);
    }

    public static boolean isDeepColor(RGB rgb) {
        double grayLevel = getGrayLevel(rgb);
        return checkGrayInDeep(grayLevel);
    }

    private static boolean checkGrayInDeep(double grayLevel) {
        return !(grayLevel >= 192);
    }

    public static boolean isDeepColor(String hex, double alpha) {
        RGB rgb = convertHexToRGB(hex);
        double grayLevel = getGrayLevel(rgb) * ((100 - alpha) / 100.00);
        return checkGrayInDeep(grayLevel);
    }

    public static boolean isDeepColor(String hex) {
        RGB rgb = convertHexToRGB(hex);
        double grayLevel = getGrayLevel(rgb);
        return checkGrayInDeep(grayLevel);
    }

    public static boolean isDeepColor(List<Gradient> gradients) {
        if (CollectionUtils.isEmpty(gradients)) {
            return false;
        }
        //TODO: 应该还得算比例，这里就不算了
        AtomicReference<Double> gray = new AtomicReference<>((double) 0);
        gradients.stream()
                .sorted(Comparator.comparing(Gradient::getProportion))
                .map(ColorUtil::getGrayLevelWithAlpha)
                .forEach(g -> gray.updateAndGet(v -> v + g));
        return checkGrayInDeep(gray.get() / gradients.size());
    }

    private static double getGrayLevelWithAlpha(Gradient gradient) {
        return getGrayLevel(convertHexToRGB(gradient.getColor()))
                * (100 - Optional.ofNullable(gradient.getAlpha()).orElse(0D)) * 0.01;
    }

    private static double getGrayLevel(RGB rgb) {
        return rgb.getR() * 0.299 + rgb.getG() * 0.587 + rgb.getB() * 0.114;
    }

    public static String toHex(RGB rgb) {
        return "#" + toHexOnlyText(rgb);
    }

    public static String toHexOnlyText(RGB rgb) {
        int red = rgb.getR();
        int green = rgb.getG();
        int blue = rgb.getB();
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0
                || blue > 255) {
            return "";
        }
        String redStr = Integer.toHexString(red);
        String greenStr = Integer.toHexString(green);
        String blueStr = Integer.toHexString(blue);
        return (redStr + greenStr + blueStr).toUpperCase();
    }

    public static RGB getLightColor(RGB rgb, double light) {
        Assert.isTrue(light >= 0 && light <= 1, "范围需要在0-1之间");
        return new RGB(
                (int)((255 - rgb.getR()) * light + rgb.getR()),
                (int)((255 - rgb.getG()) * light + rgb.getG()),
                (int)((255 - rgb.getB()) * light + rgb.getB())
        );
    }

    public static RGB getDeepColor(RGB rgb, double deep) {
        Assert.isTrue(deep >= 0 && deep <= 1, "范围需要在0-1之间");
        return new RGB(
                (int)((255 - rgb.getR()) * deep),
                (int)((255 - rgb.getG()) * deep),
                (int)((255 - rgb.getB()) * deep)
        );
    }

    public static boolean isHexColor(String colorStr) {
        Matcher m = color.matcher(colorStr);
        return m.matches();
    }

}
