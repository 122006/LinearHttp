package com.by122006.linearhttp.interfaces;

import com.by122006.linearhttp.LinearHttp;
import com.by122006.linearhttp.ResultBody;
import com.by122006.linearhttp.analyse.request.ResultBox;
import com.by122006.linearhttp.annotations.Get;
import com.by122006.linearhttp.annotations.HttpRpc;

import java.lang.reflect.Method;
import java.util.HashMap;

public interface IParamsHandler {
    public ResultBody.Parameter[] handler(Method method, ResultBody.Parameter[] parameters) throws Exception;


    public HashMap<String, LinearHttp.ResultAction<?>> addParams() throws Exception;
}
