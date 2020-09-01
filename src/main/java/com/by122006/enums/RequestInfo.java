package com.by122006.enums;

import com.annimon.stream.function.Consumer;

import java.util.HashMap;
import java.util.Map;

public class RequestInfo {
    public String httpUrl;
    public String method;
    public Map<String, Object> json;
    public Map<String,String> header;
    public Consumer<Integer> codeCheck;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public Map<String, Object> getJson() {
        return json;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setJson(Map<String, Object> json) {
        this.json = json;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }


    public Consumer<Integer> getCodeCheck() {
        return codeCheck;
    }

    public void setCodeCheck(Consumer<Integer> codeCheck) {
        this.codeCheck = codeCheck;
    }
}
