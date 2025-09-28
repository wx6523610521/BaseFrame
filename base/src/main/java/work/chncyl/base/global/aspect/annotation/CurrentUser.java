package work.chncyl.base.global.aspect.annotation;

import java.lang.annotation.*;

/**
 * 当前登录用户自动注入注解
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
    /*@AliasFor("detail")
    boolean value() default false;

    @AliasFor("value")
    boolean detail() default false;*/
}
