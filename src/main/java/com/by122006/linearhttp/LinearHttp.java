package com.by122006.linearhttp;

import lombok.experimental.Accessors;

import java.lang.reflect.*;


/**
 * Created by admin on 2020/4/10.
 */
@Accessors(chain = true)
public class LinearHttp<M> {

    final Class<M> requestClass;

    private LinearHttp(Class<M> requestClass) {
        this.requestClass = requestClass;
    }

    public static <M> LinearHttp<M> create(Class<M> rClass) {
        return new LinearHttp<M>(rClass);
    }


    public static <M> M with(Class<M> rClass) {
        ResultBody<Object, M> objectMResultBody = new ResultBody<>(rClass);
        return objectMResultBody.getM();
    }

    public interface CallBack<M> {
        public void action(M r) throws Exception;
    }

    /**
     * 需要返回值的方法引用，否则使用 {@link LinearHttp#action}
     *
     * @param re
     * @param <R>
     * @return
     */
    public <R> ResultBody<R, M> query(Function<R, M> re) {
        return new ResultBody<R, M>(this, re);
    }

    /**
     * 无需返回值的方法引用
     *
     * @param re
     * @param <R>
     * @return
     */
    public <R> ResultBody<R, M> action(EmptyFunction<M> re) {
        return new ResultBody<R, M>(this, m -> {
            re.action(m);
            return null;
        });
    }


    public interface FailCallBack {
        void action(int error, String message) throws Exception;
    }

    public interface ErrorCallBack {
        void action(Throwable throwable) throws Exception;
    }

    public interface EmptyAction {
        void action() throws Exception;
    }

    public static interface ResultAction<J> {
        J action() throws Exception;
    }

    public static interface Function<J, M> {
        J action(M m) throws Exception;
    }

    public static interface EmptyFunction<M> {
        void action(M m) throws Exception;
    }
    public static Throwable unwrapThrowable(Throwable wrapped){
        Throwable unwrapped = wrapped;
        while(true){
            if(unwrapped instanceof InvocationTargetException){
                unwrapped = ((InvocationTargetException)unwrapped).getTargetException();
            } else if(unwrapped instanceof UndeclaredThrowableException){
                unwrapped = ((UndeclaredThrowableException)unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }
}
