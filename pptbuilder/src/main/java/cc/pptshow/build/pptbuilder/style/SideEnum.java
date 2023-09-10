package cc.pptshow.build.pptbuilder.style;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum SideEnum {
    LEFT("left"),
    RIGHT("right"),
    MIDDLE("middle");
    private final String code;
}
