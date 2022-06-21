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
        Set<String> header = new HashSet<>();
        header.addAll(Arrays.asList(httpRPC.headers()));
        header.addAll(Arrays.asList(get.headers()));
        for (int i = 0; i < parameters.size(); i++) {
            ResultBody.Parameter parameter = parameters.get(i);
            String name = parameter.name;
            Object value = parameter.value;
            if (value == null) continue;
            final Param annotation = parameter.getAnnotation(Param.class);
            if (annotation != null && annotation.header()) {
                header.add(name + ": " + value);
                continue;
            } else if (annotation != null && annotation.unBox()) {
                String object = JSONObject.toJSONString(parameter.value);
                final JSONObject jsonObject = JSON.parseObject(object);
                final List<String> keys = new ArrayList<>(jsonObject.keySet());
                for (int i1 = 0; i1 < keys.size(); i1++) {
                    String key = keys.get(i);
                    String value2 = jsonObject.getString(key);
                    str.append(name)
                            .append("=")
                            .append(value2);
                    if (i1 != keys.size() - 1) str.append("&");
                }
            } else if (value != null && value.getClass().isArray()) {
                int len = Array.getLength(value);
                StringBuilder re = new StringBuilder();
                for (int a = 0; a < len; a++) {
                    Object item = Array.get(value, a);
                    if (a != 0) re.append(",");
                    re.append(isBaseType(item) ? item : JSON.toJSONString(item));
                }
                str.append(name)
                        .append("=")
                        .append(re);
            } else if (value instanceof Collection) {
                Iterator<?> iterator = ((Collection<?>) value).iterator();
                StringBuilder re = new StringBuilder();
                while (iterator.hasNext()) {
                    Object string = iterator.next();
                    re.append(isBaseType(string) ? string : JSON.toJSONString(string));
                    if (iterator.hasNext()) re.append(",");
                }
                str.append(name)
                        .append("=")
                        .append(re);
            } else {
                str.append(name)
                        .append("=")
                        .append(value);
            }
            if (i != parameters.size() - 1) str.append("&");

        }

        return iRequestHandler.get(header.toArray(new String[0]), url + str.toString());
    }

    @Override
    public ResultBox post(String url, HttpRpc httpRPC, Method method, Post post, List<ResultBody.Parameter> parameters, IRequestHandler iRequestHandler) throws Exception {
        String str;
        Set<String> header = new HashSet<>();
        header.addAll(Arrays.asList(httpRPC.headers()));
        header.addAll(Arrays.asList(post.headers()));
        List<ResultBody.Parameter> deleteList = new ArrayList<>();
        for (ResultBody.Parameter parameter : parameters) {
            if (parameter.getAnnotation(Param.class).header()) {
                header.add(parameter.name + ": " + parameter.value);
                deleteList.add(parameter);
                continue;
            }
        }
        parameters.removeAll(deleteList);
        //多参数
        JSONObject jsonObject = new JSONObject();
        for (ResultBody.Parameter parameter : parameters) {
            Param annotation = parameter.getAnnotation(Param.class);
            if (annotation != null && annotation.unBox()) {
                //先序列化一次，防止丢失注解信息
                String object = JSONObject.toJSONString(parameter.value);
                jsonObject.putAll(JSONObject.parseObject(object));
            } else if (parameter.value == null) {
                continue;
            } else if (isBaseType(parameter.value)) {
                jsonObject.put(parameter.name, parameter.value);
            } else {
                String object = JSONObject.toJSONString(parameter.value);
                jsonObject.put(parameter.name, object.startsWith("[") ? JSONArray.parseArray(object) :object.startsWith("{") ? JSONObject.parseObject(object) : object);
            }
        }
        str = jsonObject.toJSONString();
        return iRequestHandler.post(header.toArray(new String[0]), url, str);
    }

    public static boolean isBaseType(Object object) {
        Class className = object.getClass();
        if (className.equals(java.lang.Integer.class) ||
                className.equals(java.lang.Byte.class) ||
                className.equals(java.lang.Long.class) ||
                className.equals(java.lang.Double.class) ||
                className.equals(java.lang.Float.class) ||
                className.equals(java.lang.Character.class) ||
                className.equals(java.lang.Short.class) ||
                className.equals(java.lang.Boolean.class)||
                className.equals(java.lang.String.class)) {
            return true;
        }
        return false;
    }

}
