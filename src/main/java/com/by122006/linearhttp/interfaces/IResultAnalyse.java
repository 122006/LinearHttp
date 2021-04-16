package com.by122006.linearhttp.interfaces;

import com.by122006.linearhttp.exceptions.*;

import java.lang.reflect.Type;

public interface IResultAnalyse {
    /**
     * @param statusCode HTTP Status Code
     * @param result
     * @throws FailException
     */
    public void codeCheck(Integer statusCode, String result) throws FailException;
    public <T> T analyse(String object, Type t) throws FailException;
}
