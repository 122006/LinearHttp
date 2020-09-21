package com.by122006.analyse.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    public String value();

    /**
     * 如果是post请求且只有一个参数：</p>
     * false（默认）: 这是一个封装类，解析该入参为string进行处理，复杂对象使用json解析</p>
     * true： 这是一个类拆分字段（只有一个字段），{"字段名":xx}
     */
    public boolean unBox() default false;
}
