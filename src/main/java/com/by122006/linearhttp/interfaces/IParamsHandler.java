package com.by122006.linearhttp.interfaces;

import com.by122006.linearhttp.LinearHttp;
import com.by122006.linearhttp.ResultBody;
import com.by122006.linearhttp.analyse.request.ResultBox;
import com.by122006.linearhttp.annotations.Get;
import com.by122006.linearhttp.annotations.HttpRpc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public interface IParamsHandler {
    public List<ResultBody.Parameter> handler(Method method, List<ResultBody.Parameter> parameters) throws Exception;


}
