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
import com.by122006.linearhttp.interfaces.IRequestHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DefaultParamsAnalyse implements IParamsAnalyse {

    @Override
    public ResultBox get(String url, HttpRpc httpRPC, Method method, Get get, List<ResultBody.Parameter> parameters, IRequestHandler iRequestHandler) throws Exception {
        StringBuilder str = new StringBuilder();

        if (!url.contains("?")) {
            url += "?";
        }

        for (int i = 0; i < parameters.size(); i++) {
            String name = parameters.get(i).name;
            str.append(name)
                    .append("=")
                    .append(parameters.get(i).value);
            if (i != parameters.size() - 1) str.append("&");
        }

        String[] headers = get.headers().length == 0 ? httpRPC.headers() : get.headers();
        return iRequestHandler.get(headers, url+ str.toString());
    }

    @Override
    public ResultBox post(String url, HttpRpc httpRPC, Method method, Post post, List<ResultBody.Parameter> parameters, IRequestHandler iRequestHandler) throws Exception {
        String str;

        if (parameters.size() == 1) {
            Param annotation = parameters.get(0).getAnnotation(Param.class);
            if (annotation != null && annotation.unBox()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(parameters.get(0).name, parameters.get(0).value);
                str = jsonObject.toJSONString();
            } else {
                str = JSON.toJSONString(parameters.get(0).value);
            }
        } else {
            //多参数一定是拆分
            JSONObject jsonObject = new JSONObject();
            for (ResultBody.Parameter parameter:parameters) {
                jsonObject.put(parameter.name, parameter.value);
            }
            str = jsonObject.toJSONString();

        }
        String[] headers = post.headers().length == 0 ? httpRPC.headers() : post.headers();
        return iRequestHandler.post(headers, url, str);
    }




}
