package cc.pptshow.build.pptbuilder.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PPTPage {
    INNER_TITLE(11),
    HOME(3),
    CONTENTS(1),
    BIG_TITLE(4),
    THANK(5),
    OTHER(-1);
    private final int id;

}
