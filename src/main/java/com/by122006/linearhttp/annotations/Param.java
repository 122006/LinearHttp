package com.by122006.linearhttp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    public String value() default "";

    /**
     * 如果是post请求且只有一个参数：</p>
     * false（默认）: 这是一个封装类，解析该入参为string进行处理，复杂对象使用json解析</p>
     * true： 这是一个类拆分字段（只有一个字段），{"字段名":xx}
     */
    public boolean unBox() default false;
    /**
     * 替换url中的指定字符(restful风格)，如果为true会替换url中的"{value}"内容，且不认为其为参数
     */
    public boolean restful() default false;
    /**
     * 替换url中的指定字符(restful风格)，如果指定字符串会替换url中的"{字符串}"内容，且不认为其为参数
     */
    public String restfulStr() default "";
    /**
     * true 会添加该键值对到header中，且不认为其为参数
     */
    public boolean header() default false;
}
