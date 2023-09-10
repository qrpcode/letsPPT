package cc.pptshow.build.pptbuilder.anno;

import cc.pptshow.build.pptbuilder.domain.enums.PPTBlockType;
import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;

import java.lang.annotation.*;

/**
 * 表示是处理程序
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Design {

    PPTBlockType[] type() default {PPTBlockType.TEXT, PPTBlockType.IMG, PPTBlockType.LINE, PPTBlockType.SHAPE};

    PPTPage[] excludePage() default {};

    PPTPage[] onlyInPage() default {};

    /**
     * 数字越小越先执行
     */
    int order() default 0;

    /**
     * 是否需要将所有可处理元素进行迭代处理
     */
    boolean needIteration() default true;

}
