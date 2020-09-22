package com.by122006.linearhttp.analyse.request;

public interface RequestHandler {

    public ResultBox post(String[] headers,String url,String content) throws Exception;

    public ResultBox get(String[] headers,String url) throws Exception;

}
