package com.by122006;

import com.alibaba.fastjson.JSONObject;
import com.by122006.enums.RequestInfo;
import com.by122006.interfaces.HttpAction;
import lombok.SneakyThrows;

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
        params.put(key, value);
        return this;
    }

    public JSONObject params = new JSONObject();

    public Map<String, String> headers = new HashMap<>();


    @SneakyThrows
    protected JSONObject post(String urlEnd) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setHttpUrl(getUrl() + urlEnd);
        requestInfo.setJson(params);
        requestInfo.setHeader(headers);
        requestInfo.setCodeCheck(this::codeCheck);
        requestInfo.setMethod("POST");
        if (LinearHttp.ACTIVE_HTTP_ACTION == null) {
            throw new RuntimeException("未设置网络处理方法，请调用LinearHttp.init()方法");
        }
        String s = LinearHttp.ACTIVE_HTTP_ACTION.action(requestInfo);
        return JSONObject.parseObject(s);
    }

    protected void codeCheck(Integer a) {

    }


    protected JSONObject get(String urlEnd) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setHttpUrl(getUrl() + urlEnd);
        requestInfo.setJson(params);
        requestInfo.setHeader(headers);
        requestInfo.setCodeCheck(this::codeCheck);
        requestInfo.setMethod("GET");
        if (LinearHttp.ACTIVE_HTTP_ACTION == null) {
            throw new RuntimeException("未设置网络处理方法，请调用LinearHttp.init()方法");
        }
        String s = LinearHttp.ACTIVE_HTTP_ACTION.action(requestInfo);
        return JSONObject.parseObject(s);
    }
}
