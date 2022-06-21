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
     * false（默认）: 这个类会作为参数列表的一个字段进行转化</p>
     * true： 这个类的字段会被转化为参数列表的字段</p>
     * 请不要注解于数组集合基础类型等无法解析的参数
     *
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
