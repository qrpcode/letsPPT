package cc.pptshow.build.pptbuilder.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReadElementColor {
    SHAPE("SHAPE", "#3333FF"),
    TEXT("TEXT", "#FF6633"),
    IMG("IMG", "#FF33FF"),
    LINE("LINE", "#29F500");
    private final String code;
    private final String color;
}
