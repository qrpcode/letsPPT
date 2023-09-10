package cc.pptshow.build.pptbuilder.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelType {

    //共享模板
    GLOBAL("GLOBAL", "共享模板", "模板鸭"),

    //自有平台
    MBG("MBG", "模板狗", "模板狗"),
    WK7("7WK", "七文库", "七文库"),
    KUAI("KUAI", "快PPT", "快模板"),
    ZAO("ZAO", "造PPT", "造模板"),

    //第三方平台
    DOCER("DOCER", "稻壳网", "模板鸭"),
    YANJ("YANJ", "演界网", "模板鸭"),
    DOC88("DOC88", "道客巴巴", "模板鸭"),
    DOCIN("DOCIN", "豆丁网", "模板鸭"),
    RRDOC("RRDOC", "人人文库", "模板鸭"),
    BOOK118("BOOK118", "原创力文库", "模板鸭")
    ;
    private final String code;
    private final String name;
    private final String sign;

}
