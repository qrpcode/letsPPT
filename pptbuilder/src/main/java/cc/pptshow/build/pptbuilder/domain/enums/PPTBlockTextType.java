package cc.pptshow.build.pptbuilder.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PPTBlockTextType {
    FIXED("fixed", "固定文本"),
    YEAR("year", "年份"),
    NUMBER("number", "序号"),
    BIG_TITLE("bigTitle", "同字数标题"),
    SIGNATURE("signature", "署名信息"),
    SLOGAN("slogan", "标语"),
    LIPSUM("lipsum", "乱数假文")
    ;
    private final String code;
    private final String name;

}
