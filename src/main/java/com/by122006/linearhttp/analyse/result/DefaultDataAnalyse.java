package com.by122006.linearhttp.analyse.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.by122006.linearhttp.exceptions.*;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class DefaultDataAnalyse implements ResultAnalyse {


    @Override
    public void codeCheck(Integer a, String result) throws FailException {
        String msg = null;
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            msg = jsonObject.getString("msg");
        } catch (Exception e) {
            msg = result;
        }
        if (a != 200) throw new FailException(Integer.MIN_VALUE, "请求失败: " + msg);
    }

    @Override
    public <T> T analyse(String object, Type t) throws FailException {
        JSONObject jsonObject = JSONObject.parseObject(object);
        int errorCode = jsonObject.getIntValue("code");
        String msg = jsonObject.getString("msg");
        if (errorCode != 0 && errorCode != 200) {
            throw new FailException(errorCode, msg);
        }
        Object data = jsonObject.get("result");
        Class clazz=t instanceof Class? (Class) t : (Class) ((ParameterizedType) t).getRawType();
        if (data == null) {
            return null;
        } else if (data.getClass() == t) {
            return (T) data;
        } else if (data.getClass().isAssignableFrom(clazz)) {
            return (T) data;
        } else if (clazz.isAssignableFrom(String.class)) {
            return (T) String.valueOf(data);
        } else if (clazz.isAssignableFrom(Map.class)) {
            return (T) JSON.parseObject(String.valueOf(data));
        } else if (clazz.isAssignableFrom(List.class)) {
            Type[] pt = ((ParameterizedType) t).getActualTypeArguments();
            if (pt.length==0) return (T) JSON.parseArray(String.valueOf(data));
            return (T) JSON.parseArray(String.valueOf(data), (Class) pt[0]);
        } else if (clazz.isArray()) {
            if (data instanceof String)
                return (T) JSONArray.parseArray((String) data).toArray(getArray(clazz.getComponentType(),0));
            else if (data instanceof JSONArray)
                return (T) ((JSONArray)data).toArray(getArray(clazz.getComponentType(),0));
        } else {
            return JSONObject.parseObject(String.valueOf(data), t);
        }
        throw new RuntimeException("无法解析返回类型");
    }
    public static <T>  T[] getArray(Class<T> componentType,int length) {
        return (T[]) Array.newInstance(componentType, length);
    }
}
