package cc.pptshow.build.pptbuilder.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.binary.StringUtils;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum PPTBlockType {
    IMG("IMG", "图片"),
    SHAPE("SHAPE", "图形"),
    TEXT("TEXT", "文字"),
    LINE("LINE", "线条"),
    REGION("REGION", "嵌套");
    private final String code;
    private final String name;

    public static PPTBlockType findByCode(String pptBlockType) {
        return Arrays.stream(PPTBlockType.values())
                .filter(type -> StringUtils.equals(pptBlockType, type.getCode()))
                .findFirst()
                .orElse(null);
    }
}
