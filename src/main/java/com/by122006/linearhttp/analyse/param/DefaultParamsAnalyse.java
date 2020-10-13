package com.by122006.linearhttp.analyse.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.by122006.linearhttp.ResultBody;
import com.by122006.linearhttp.analyse.request.ResultBox;
import com.by122006.linearhttp.annotations.Get;
import com.by122006.linearhttp.annotations.HttpRpc;
import com.by122006.linearhttp.annotations.Param;
import com.by122006.linearhttp.annotations.Post;
import com.by122006.linearhttp.interfaces.IParamsAnalyse;
import com.by122006.linearhttp.interfaces.IParamsHandler;
import com.by122006.linearhttp.interfaces.IRequestHandler;
import com.by122006.linearhttp.utils.StringUtil;

import java.lang.reflect.Method;

public class DefaultParamsAnalyse implements IParamsAnalyse {

    @Override
    public ResultBox get(String url, HttpRpc httpRPC, Method method, Get get, ResultBody.Parameter[] parameters, IRequestHandler iRequestHandler) throws Exception {
        StringBuilder str = new StringBuilder();

        if (!url.contains("?")) {
            url += "?";
        }

        for (int i = 0; i < parameters.length; i++) {
            String name = parameters[i].name;
            str.append(name)
                    .append("=")
                    .append(parameters[i].value);
            if (i != parameters.length - 1) str.append("&");
        }

        String[] headers = get.headers().length == 0 ? httpRPC.headers() : get.headers();
        return iRequestHandler.get(headers, url+ str.toString());
    }

    @Override
    public ResultBox post(String url, HttpRpc httpRPC, Method method, Post post, ResultBody.Parameter[] parameters, IRequestHandler iRequestHandler) throws Exception {
        String str;

        if (parameters.length == 1) {
            Param annotation = parameters[0].getAnnotation(Param.class);
            if (annotation != null && annotation.unBox()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(parameters[0].name, parameters[0].value);
                str = jsonObject.toJSONString();
            } else {
                str = JSON.toJSONString(parameters[0].value);
            }
        } else {
            //多参数一定是拆分
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < parameters.length; i++) {
                jsonObject.put(parameters[i].name, parameters[i].value);
            }
            str = jsonObject.toJSONString();

        }
        String[] headers = post.headers().length == 0 ? httpRPC.headers() : post.headers();
        return iRequestHandler.post(headers, url, str);
    }




}
