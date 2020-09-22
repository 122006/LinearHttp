package com.by122006.linearhttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Post {
    /**
     * 请求头信息，覆盖类headers
     */
    String[] headers() default {};

    /**
     * 相对路径 当非empty的时候会使用该值
     */
    String path() default "";

    /**
     * 所在前url内容，如果path存在即忽略
     */
    String prePath() default "";
}
