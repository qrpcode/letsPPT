package cc.pptshow.build.pptbuilder.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PPTBackgroundType {
    NONE("none", "无需背景"),
    LEFT("left", "背景主体在左侧"),
    RIGHT("right", "背景主体在右侧"),
    TOP("top", "背景主体在上侧"),
    BOTTOM("bottom", "背景主体在下侧"),
    AROUND("around", "环绕背景"),
    MARK("mark", "遮罩层背景")
    ;
    private final String code;
    private final String name;
}
