package com.by122006.linearhttp.interfaces;

import com.by122006.linearhttp.exceptions.*;

import java.lang.reflect.Type;

public interface IResultAnalyse {
    public void codeCheck(Integer a, String result) throws FailException;
    public <T> T analyse(String object, Type t) throws FailException;
}
