package cc.pptshow.build.pptbuilder.anno;

import java.lang.annotation.*;

/**
 * 针对RegionFilter的过滤
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ForRegion {

    /**
     * 是否是强制的，如果是可能会过滤到为空
     * 如果不是强制的只剩一个元素的时候会不再过滤
     */
    boolean force() default false;

    /**
     * 过滤器名称
     */
    String name() default "";

}
