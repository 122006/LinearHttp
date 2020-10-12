package com.by122006.linearhttp.annotations;

import com.by122006.linearhttp.analyse.param.DefaultParamsAnalyse;
import com.by122006.linearhttp.analyse.param.DefaultParamsHandler;
import com.by122006.linearhttp.analyse.request.HUCHandler;
import com.by122006.linearhttp.interfaces.IParamsAnalyse;
import com.by122006.linearhttp.interfaces.IParamsHandler;
import com.by122006.linearhttp.interfaces.IRequestHandler;
import com.by122006.linearhttp.analyse.result.DefaultDataAnalyse;
import com.by122006.linearhttp.interfaces.IResultAnalyse;

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
    Class<? extends IResultAnalyse> dataAnalyse() default DefaultDataAnalyse.class;
    /**
     * 网络请求实现类
     */
    Class<? extends IRequestHandler> requestHandler() default HUCHandler.class;
    /**
     * 网络请求实现类
     */
    Class<? extends IParamsAnalyse> paramsAnalyse() default DefaultParamsAnalyse.class;
    /**
     * 网络请求常量类
     */
    Class<? extends IParamsHandler> paramsHandler() default DefaultParamsHandler.class;
    /**
     * url 包含端口号
     */
    String url() default "";

    /**
     * 请求头信息，被方法的headers覆盖
     */
    String[] headers() default {"Content-Type: application/json;charset=utf-8"};
}
