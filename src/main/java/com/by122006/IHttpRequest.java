package com.by122006;

import com.alibaba.fastjson.JSONObject;
import com.by122006.enums.RequestInfo;
import com.by122006.interfaces.HttpAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2020/4/13.
 */
public abstract class IHttpRequest {
    protected abstract String getUrl();



    public IHttpRequest() {
    }

    public JSONObject getParams() {
        return params;
    }

    public IHttpRequest addParams(String key, Object value) {
        params.put(key,value);
        return this;
    }

    public JSONObject params = new JSONObject();

    public Map<String, String> headers=new HashMap<>();

    protected JSONObject post(String urlEnd) {
        try {
            RequestInfo requestInfo=new RequestInfo();
            requestInfo.setHttpUrl(getUrl() + urlEnd);
            requestInfo.setJson(params);
            requestInfo.setHeader(headers);
            requestInfo.setCodeCheck(this::codeCheck);
            requestInfo.setMethod("POST");
            String s = HttpAction.ACTIVE_HTTP_ACTION.action(requestInfo);
            return JSONObject.parseObject(s);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    protected  void codeCheck(Integer a){

    }


    protected JSONObject get(String urlEnd) {
        try {
            RequestInfo requestInfo=new RequestInfo();
            requestInfo.setHttpUrl(getUrl() + urlEnd);
            requestInfo.setJson(params);
            requestInfo.setHeader(headers);
            requestInfo.setCodeCheck(this::codeCheck);
            requestInfo.setMethod("GET");
            String s = HttpAction.ACTIVE_HTTP_ACTION.action(requestInfo);
            return JSONObject.parseObject(s);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
