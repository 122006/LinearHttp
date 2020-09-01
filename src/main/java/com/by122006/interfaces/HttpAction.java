package com.by122006.interfaces;

import com.by122006.enums.RequestInfo;

public interface HttpAction {
    String action(RequestInfo request);
    static HttpAction ACTIVE_HTTP_ACTION = null;
}
