package cc.pptshow.build.pptbuilder.anno;

import cc.pptshow.build.pptbuilder.domain.enums.PPTPage;

import java.lang.annotation.*;

/**
 * 正常的页面应该是通过一些模板来进行生成
 * 但是一些特殊的页面是通过推理实现的
 * 比如感谢页面和首页一定是风格几乎一致的，所以我们单独采用一些算法来根据首页生成感谢页面
 *
 * 生成的类使用本注解标注，以表示对应关系，方便生成器感知和调用
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inference {

    PPTPage page() default PPTPage.OTHER;

}
