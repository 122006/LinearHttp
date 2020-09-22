package com.by122006.linearhttp.analyse.result;

import com.by122006.linearhttp.exceptions.*;

import java.lang.reflect.Type;

public interface ResultAnalyse {
    public void codeCheck(Integer a, String result) throws FailException;
    public <T> T analyse(String object, Type t) throws FailException;
}
