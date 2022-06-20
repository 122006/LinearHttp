package com.by122006.linearhttp.analyse.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.by122006.linearhttp.ResultBody;
import com.by122006.linearhttp.analyse.request.ResultBox;
import com.by122006.linearhttp.annotations.Get;
import com.by122006.linearhttp.annotations.HttpRpc;
import com.by122006.linearhttp.annotations.Param;
import com.by122006.linearhttp.annotations.Post;
import com.by122006.linearhttp.interfaces.IParamsAnalyse;
import com.by122006.linearhttp.interfaces.IRequestHandler;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

public class DefaultParamsAnalyse implements IParamsAnalyse {

    @Override
    public ResultBox get(String url, HttpRpc httpRPC, Method method, Get get, List<ResultBody.Parameter> parameters, IRequestHandler iRequestHandler) throws Exception {
        StringBuilder str = new StringBuilder();

        if (!url.contains("?")) {
            url += "?";
        }
        Set<String> header=new HashSet<>();
        header.addAll(Arrays.asList(httpRPC.headers()));
        header.addAll(Arrays.asList(get.headers()));
        for (int i = 0; i < parameters.size(); i++) {
            ResultBody.Parameter parameter = parameters.get(i);
            String name = parameter.name;
            Object value = parameter.value;
            final Param annotation = parameter.getAnnotation(Param.class);
            if (annotation!=null&&annotation.header()){
                header.add(name +": "+value);
                continue;
            }
            if (value!=null&&value.getClass().isArray()){
                int len = Array.getLength(value);
                StringBuilder re= new StringBuilder();
                for(int a = 0; a < len; a++) {
                    Object item = Array.get(value, a);
                    if (a!=0) re.append(",");
                    re.append(item);
                }
                value=re;
            }else  if (value instanceof Collection){
                Iterator<?> iterator = ((Collection<?>) value).iterator();
                StringBuilder re= new StringBuilder();
                while (iterator.hasNext()) {
                    Object string = iterator.next();
                    re.append(string);
                    if (iterator.hasNext()) re.append(",");
                }
                value=re;
            }
            str.append(name)
                    .append("=")
                    .append(value);
            if (i != parameters.size() - 1) str.append("&");
        }

        return iRequestHandler.get(header.toArray(new String[0]), url+ str.toString());
    }

    @Override
    public ResultBox post(String url, HttpRpc httpRPC, Method method, Post post, List<ResultBody.Parameter> parameters, IRequestHandler iRequestHandler) throws Exception {
        String str;
        Set<String> header=new HashSet<>();
        header.addAll(Arrays.asList(httpRPC.headers()));
        header.addAll(Arrays.asList(post.headers()));
        List<ResultBody.Parameter> deleteList=new ArrayList<>();
        for (ResultBody.Parameter parameter:parameters){
            if (parameter.getAnnotation(Param.class).header()){
                header.add(parameter.name +": "+parameter.value);
                deleteList.add(parameter);
                continue;
            }
        }
        parameters.removeAll(deleteList);
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
            //多参数
            JSONObject jsonObject = new JSONObject();
            for (ResultBody.Parameter parameter:parameters) {
                Param annotation = parameters.get(0).getAnnotation(Param.class);
                if (annotation != null && annotation.unBox()) {
                    JSONObject object= (JSONObject) JSONObject.toJSON(parameters.get(0).value);
                    jsonObject.putAll(object);
                } else {
                    jsonObject.put(parameter.name, parameter.value);
                }
            }
            str = jsonObject.toJSONString();
        }
        return iRequestHandler.post(header.toArray(new String[0]), url, str);
    }




}
