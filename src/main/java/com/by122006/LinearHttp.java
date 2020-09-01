package com.by122006;

import com.alibaba.fastjson.JSONObject;
import com.annimon.stream.function.Function;
import com.by122006.exceptions.FailException;
import com.by122006.interfaces.HttpAction;


/**
 * Created by admin on 2020/4/10.
 */
public class LinearHttp<R extends IHttpRequest> {
    public static <T extends HttpAction> void init(T httpAction){
        ACTIVE_HTTP_ACTION=httpAction;
    }

    public static HttpAction ACTIVE_HTTP_ACTION = null;
    private final R request;
    //    private CallBack<JSON> callBack;

//    private Function<R , ResultAction<?>> re;
//    public StationWeb setCallBack(CallBack<JSON> callBack) {
//        this.callBack = callBack;
//        return this;
//    }

    private LinearHttp(R request) {
        this.request = request;
    }

    public static <R extends IHttpRequest> LinearHttp<R> create(Class<R> rClass) {
        LinearHttp<R> linearHttp = null;
        try {
            linearHttp = new LinearHttp<R>(rClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("web请求配置错误");
        }
        return linearHttp;
    }

    public interface CallBack<M> {
        public void action(M r) throws Exception;
    }

    public <M> ResultBody<M> request(Function<R, ResultAction<M>> re) {
        return new ResultBody<M>(this, re.apply(request));
    }



    public static class ResultBody<T> {

        private final ResultAction<T> result;
        LinearHttp linearHttp;

        public ResultBody(LinearHttp linearHttp, ResultAction<T> result) {
            this.linearHttp = linearHttp;
            this.result = result;
        }


        public void syncExec() {
            new Thread(()->{
                try {
                    exec();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        /**
         * 执行网络请求.请不要在UI线程/主线程运行
         */
        public void exec() {

            try {
                T data = null;
                try {
                    data = result.action();
                } catch (FailException e) {
                    if (linearHttp.failCallBack != null) linearHttp.failCallBack.action(e.error, e.getMessage());
                    return;
                }
                if (linearHttp.successCallBack != null) linearHttp.successCallBack.action(data);
            } catch (Exception e) {
                e.printStackTrace();
                if (linearHttp.errorCallBack != null) {
                    try {
                        linearHttp.errorCallBack.action(e);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        throw new RuntimeException(exception);
                    }
                } else if (linearHttp.failCallBack != null) {
                    try {
                        linearHttp.failCallBack.action(Integer.MIN_VALUE, e.getMessage());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        throw new RuntimeException(exception);
                    }
                }else throw new RuntimeException(e);
            } finally {
                try {
                    if (linearHttp.finallyCallBack != null) linearHttp.finallyCallBack.action();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }

        public T lineRun() throws Exception {
            return result.action();
        }

        public ResultBody<T> selfParams(CallBack<JSONObject> paramsResultAction) {
            try {
                paramsResultAction.action(linearHttp.request.getParams());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return this;
        }

        public ResultBody<T> addParams(String key, Object value) {
            linearHttp.request.params.put(key, value);
            return this;
        }

        /**
         * 设置成功事件 成功的判定于对应的HttpRequest定义
         * @param successCallBack
         * @return
         */
        public ResultBody<T> setSuccessCallBack(CallBack<T> successCallBack) {
            linearHttp.setSuccessCallBack(successCallBack);
            return this;
        }

        public ResultBody<T> setFailCallBack(FailCallBack failCallBack) {
            linearHttp.setFailCallBack(failCallBack);
            return this;
        }

        public ResultBody<T> setErrorCallBack(ErrorCallBack errorCallBack) {
            linearHttp.setErrorCallBack(errorCallBack);
            return this;
        }

        public ResultBody<T> setFinallyCallBack(EmptyAction finallyCallBack) {
            linearHttp.setFinallyCallBack(finallyCallBack);
            return this;
        }
    }


    CallBack<?> successCallBack;

    FailCallBack failCallBack;
    ErrorCallBack errorCallBack;
    EmptyAction finallyCallBack;

    public void setFinallyCallBack(EmptyAction finallyCallBack) {
        this.finallyCallBack = finallyCallBack;
    }

    public void setErrorCallBack(ErrorCallBack errorCallBack) {
        this.errorCallBack = errorCallBack;
    }

    public void setSuccessCallBack(CallBack<?> successCallBack) {
        this.successCallBack = successCallBack;
    }

    public void setFailCallBack(FailCallBack failCallBack) {
        this.failCallBack = failCallBack;
    }

    public interface FailCallBack {
        public void action(int error, String message) throws Exception;
    }

    public interface ErrorCallBack {
        public void action(Throwable throwable) throws Exception;
    }

    public static interface EmptyAction {
        public void action() throws Exception;
    }

    public static interface ResultAction<J> {
        public J action() throws Exception;
    }

}
