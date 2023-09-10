package cc.pptshow.build.pptbuilder.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum PPTGroupStyle {
    NONE("none", "无形状风格"),
    SMOOTH("smooth", "圆滑"),
    SQUARE("square", "方形的"),
    DIAGONAL("diagonal", "对角圆滑的");
    private final String code;
    private final String name;

    public static PPTGroupStyle buildByCode(String code) {
        return Arrays.stream(PPTGroupStyle.values())
                .filter(s -> s.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

}
