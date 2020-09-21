package com.by122006.annotations;

import com.by122006.analyse.request.HUCHandler;
import com.by122006.analyse.request.RequestHandler;
import com.by122006.analyse.result.DefaultDataAnalyse;
import com.by122006.analyse.result.ResultAnalyse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRpc {
    /**
     * 返回值解析类
     */
    Class<? extends ResultAnalyse> dataAnalyse() default DefaultDataAnalyse.class;
    /**
     * 网络请求实现类
     */
    Class<? extends RequestHandler> requestHandler() default HUCHandler.class;
    /**
     * url 包含端口号
     */
    String url() default "";

    /**
     * 请求头信息，被方法的headers覆盖
     */
    String[] headers() default {"Content-Type: application/json;charset=utf-8"};
}
