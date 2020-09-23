package com.by122006.linearhttp.interfaces;

import com.by122006.linearhttp.analyse.request.ResultBox;

public interface IRequestHandler {

    public ResultBox post(String[] headers, String url, String content) throws Exception;

    public ResultBox get(String[] headers,String url) throws Exception;

}
