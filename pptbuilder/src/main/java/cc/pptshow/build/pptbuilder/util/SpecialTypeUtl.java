package cc.pptshow.build.pptbuilder.util;

import cc.pptshow.build.pptbuilder.domain.GlobalStyle;
import cc.pptshow.build.pptbuilder.domain.enums.LanguageType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpecialTypeUtl {

    /**
     * 确定当前的特殊类型
     * 这里目前是写死的，数据库id不能变，后期可以改用MVEL表达式等实现
     */
    public static int findSpecialType(GlobalStyle globalStyle) {
        if (globalStyle.getPptStyle().getId().equals(3)) {
            return 1;
        } else if (globalStyle.getLanguageType().equals(LanguageType.ENGLISH)) {
            return 2;
        }
        return 0;
    }

}
