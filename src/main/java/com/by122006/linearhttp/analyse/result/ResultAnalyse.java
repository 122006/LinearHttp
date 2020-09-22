package com.by122006.linearhttp.analyse.result;

import com.by122006.linearhttp.exceptions.*;

public interface ResultAnalyse {
    public void codeCheck(Integer a, String result) throws FailException;
    public <T> T analyse(String object,Class<T> t) throws FailException;
}
