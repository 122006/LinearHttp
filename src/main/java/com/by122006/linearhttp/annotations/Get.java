package com.by122006.linearhttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Get {
    /**
     * 请求头信息，覆盖类headers
     */
    String[] headers() default {};

    /**
     * 相对路径 当非empty的时候会使用该值而不是方法名
     * <br>分隔符自适应
     */
    String path() default "";

    /**
     * 所在前url内容，如果path存在会忽略。<br><br>会和方法名拼接，并加到接口指定的url后
     * <br><br>分隔符自适应
     */
    String prePath() default "";
}
