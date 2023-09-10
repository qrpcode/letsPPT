package cc.pptshow.build.pptbuilder.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PPTBlockImgType {
    LOGO("logo", "LOGO图")
    ;
    private final String code;
    private final String name;
}
