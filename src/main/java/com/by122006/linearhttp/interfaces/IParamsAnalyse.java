package com.by122006.linearhttp.interfaces;

import com.by122006.linearhttp.ResultBody;
import com.by122006.linearhttp.analyse.request.ResultBox;
import com.by122006.linearhttp.annotations.Get;
import com.by122006.linearhttp.annotations.HttpRpc;
import com.by122006.linearhttp.annotations.Post;
import com.by122006.linearhttp.exceptions.FailException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 参数解析拦截器
 */
public interface IParamsAnalyse {
    public ResultBox get(String url, HttpRpc httpRPC, Method method, Get get, ResultBody.Parameter[] parameters, IRequestHandler iRequestHandler) throws Exception;
    public ResultBox post(String url,HttpRpc httpRPC,Method method, Post post, ResultBody.Parameter[] parameters,IRequestHandler iRequestHandler) throws Exception;
}