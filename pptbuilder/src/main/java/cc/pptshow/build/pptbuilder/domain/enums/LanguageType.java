package cc.pptshow.build.pptbuilder.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 目前我们只有两种策略
 * 分别是 中文 和 英文
 */
@Getter
@AllArgsConstructor
public enum LanguageType {
    CHINESE("chinese", "中文"),
    ENGLISH("english", "英文"),
    ;
    private final String code;
    private final String name;

}
