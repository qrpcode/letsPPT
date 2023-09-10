package cc.pptshow.build.pptbuilder.style;

import cc.pptshow.build.pptbuilder.util.RandUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Data
public class PPTColorStyle {

    private String backgroundColor;
    private List<String> colors;
    private String colorTitle;

    private ColorTypeStyle colorTypeStyle;
    private ColorStyleGradient colorStyleGradient;
    private ColorStyleSingleColor colorStyleSingleColor;

    public static PPTColorStyle buildByRand() {
        PPTColorStyle PPTColorStyle = new PPTColorStyle();
        ColorTypeStyle colorTypeStyle = RandUtil.randInEnum(ColorTypeStyle.class);
        PPTColorStyle.setColorTypeStyle(colorTypeStyle);

        if (colorTypeStyle.equals(ColorTypeStyle.GRADIENT)) {
            ColorStyleGradient colorStyleGradient = RandUtil.randInEnum(ColorStyleGradient.class);
            PPTColorStyle.setColorStyleGradient(colorStyleGradient);
        }

        return PPTColorStyle;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum ColorTypeStyle implements ProbabilityStyle {
        SINGLE_COLOR("纯色", 0, 0),
        BICOLOR("双色", 1, 0),
        GRADIENT("渐变色", 2, 99999999);
        private final String name;
        private final int code;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum ColorStyleGradient implements ProbabilityStyle {
        Yellow("明黄色亮暗渐变", "FDEB71", "F8D800", 10),
        Blue("浅蓝色亮暗渐变", "ABDCFF", "0396FF", 10),
        Pink("粉色亮暗渐变", "FEB692", "EA5455", 10),
        Purple("紫色亮暗渐变", "CE9FFC", "7367F0", 10),
        Cyan("青色亮暗渐变", "90F7EC", "32CCBC", 10),
        CreamYellowToPink("奶黄色和桃粉色渐变", "FFF6B7", "F6416C", 10)
        ;
        private final String name;
        private final String from;
        private final String to;
        private final int probability;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public enum ColorStyleSingleColor {
        DeepSkyBlue("深天蓝", "00BFFF", 10),
        MediumBlue("适中的蓝色", "0000CD", 10),
        RoyalBlue("皇军蓝", "4169E1", 10),
        SkyBlue("天蓝色", "87CEEB", 10),
        LightSkyBlue("淡蓝色", "87CEFA", 10),
        DarkTurquoise("深绿宝石", "00CED1", 10),
        MediumTurquoise("适中的绿宝石", "48D1CC", 10),
        LightSeaGreen("浅海洋绿", "20B2AA", 10),
        SeaGreen("海洋绿", "2E8B57", 10),
        ForestGreen("森林绿", "228B22", 10),
        Green("纯绿", "008000", 10),
        PaleGodenrod("灰秋麒麟", "EEE8AA", 10),
        Gold("金色", "FFD700", 10),
        GoldEnrod("秋麒麟", "DAA520", 10),
        Orange("橙色", "FFA500", 10),
        BlueViolet("深紫罗兰的蓝色", "8A2BE2", 10);
        private final String name;
        private final String code;
        private final int probability;
    }

}
