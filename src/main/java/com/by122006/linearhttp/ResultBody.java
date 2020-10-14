package com.by122006.linearhttp;

import com.by122006.linearhttp.annotations.Param;
import com.by122006.linearhttp.interfaces.IParamsAnalyse;
import com.by122006.linearhttp.interfaces.IParamsHandler;
import com.by122006.linearhttp.interfaces.IRequestHandler;
import com.by122006.linearhttp.analyse.request.ResultBox;
import com.by122006.linearhttp.interfaces.IResultAnalyse;
import com.by122006.linearhttp.annotations.*;
import com.by122006.linearhttp.exceptions.*;
import com.by122006.linearhttp.utils.*;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

@Accessors(chain = true)
public class ResultBody<R, M> {

    private final LinearHttp.Function<R, M> result;
    LinearHttp<M> linearHttp;
    Class<M> requestClass;
    M m;

    private static final HashMap<Class<? extends IResultAnalyse>, IResultAnalyse> resultAnalyseMap = new HashMap<>();
    private static final HashMap<Class<? extends IRequestHandler>, IRequestHandler> requestHandlerHashMap = new HashMap<>();
    private static final HashMap<Class<? extends IParamsAnalyse>, IParamsAnalyse> paramsAnalyseHashMap = new HashMap<>();
    private static final HashMap<Class<? extends IParamsHandler>, IParamsHandler> paramsHandlerHashMap = new HashMap<>();

    IResultAnalyse iResultAnalyse;
    IRequestHandler iRequestHandler;
    IParamsAnalyse iParamsAnalyse;
    IParamsHandler iParamsHandler;

    public ResultBody(LinearHttp<M> linearHttp, LinearHttp.Function<R, M> result) {
        this.linearHttp = linearHttp;
        this.result = result;
        this.requestClass = linearHttp.requestClass;
        action();
    }

    private void action() {
        HttpRpc classAnnotation = requestClass.getAnnotation(HttpRpc.class);

        if ((iResultAnalyse = resultAnalyseMap.get(classAnnotation.dataAnalyse())) == null) {
            try {
                iResultAnalyse = classAnnotation.dataAnalyse().newInstance();
                resultAnalyseMap.put(classAnnotation.dataAnalyse(), iResultAnalyse);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    errorCallBack.action(e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }
        }
        if ((iRequestHandler = requestHandlerHashMap.get(classAnnotation.requestHandler())) == null) {
            try {
                iRequestHandler = classAnnotation.requestHandler().newInstance();
                requestHandlerHashMap.put(classAnnotation.requestHandler(), iRequestHandler);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    errorCallBack.action(e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }
        }
        if ((iParamsAnalyse = paramsAnalyseHashMap.get(classAnnotation.requestHandler())) == null) {
            try {
                iParamsAnalyse = classAnnotation.paramsAnalyse().newInstance();
                paramsAnalyseHashMap.put(classAnnotation.paramsAnalyse(), iParamsAnalyse);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    errorCallBack.action(e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }
        }
        if ((iParamsHandler = paramsHandlerHashMap.get(classAnnotation.requestHandler())) == null) {
            try {
                iParamsHandler = classAnnotation.paramsHandler().newInstance();
                paramsHandlerHashMap.put(classAnnotation.paramsHandler(), iParamsHandler);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    errorCallBack.action(e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return;
            }
        }
        String classUrl;
        try {
            Method getUrl = requestClass.getMethod("getUrl");
            getUrl.setAccessible(true);
            classUrl = (String) getUrl.invoke(null);
        } catch (Throwable e) {
            classUrl = classAnnotation.url();
        }
        String finalClassUrl = classUrl;
        Object o = Proxy.newProxyInstance(requestClass.getClassLoader(), new Class[]{requestClass}, (proxy, method, args) -> {
            Post post = method.getAnnotation(Post.class);
            Get get = method.getAnnotation(Get.class);
            if (post == null && get == null) return method.invoke(proxy);
            String requestName = method.getName();
            ResultBox resultBox;

            Parameter[] parameters = getParameters(method, args);
            parameters=iParamsHandler.handler(method,parameters);
            if (post != null) {
                String url = finalClassUrl+"/" + (StringUtil.isEmpty(post.path())
                        ?post.prePath() + "/" + requestName + "/"
                        : post.path() + "/");
                url = formatUrl(url,parameters);
                resultBox= iParamsAnalyse.post(url,classAnnotation,method,post,parameters, iRequestHandler);
            } else if (get != null) {
                String url = finalClassUrl+"/" + (StringUtil.isEmpty(get.path())
                        ? get.prePath() + "/" + requestName + "/"
                        : get.path() + "/");
                url = formatUrl(url,parameters);
                resultBox = iParamsAnalyse.get(url,classAnnotation,method,get,parameters, iRequestHandler);
            }else throw new RuntimeException("unknow request method");
            int httpCode = resultBox.getHttpCode();
            iResultAnalyse.codeCheck(httpCode, resultBox.getResult());
            Class<?> returnType = method.getReturnType();
            if (returnType == void.class || returnType == Void.class) {
                return null;
            } else
                return iResultAnalyse.analyse(resultBox.getResult(), method.getGenericReturnType());
        });
        m = requestClass.cast(o);
    }

    private Parameter[] getParameters(Method method, Object[] args) {
        if (args==null||args.length==0) return new Parameter[0];
        //java7没有getParameters方法
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        int parameterCount = parameterAnnotations.length;
        Parameter[] parameters = new Parameter[parameterCount];
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameters.length != args.length)
            throw new RuntimeException(String.format("传入参数%d与方法入参数%d不一致", args.length, parameters.length));
        for (int i = 0; i < parameterCount; i++) {
            parameters[i] = new Parameter();
            parameters[i].annotations = parameterAnnotations[i];
            parameters[i].type = parameterTypes[i];
            parameters[i].name = parameters[i].getAnnotation(Param.class).value();
            parameters[i].value = args[i];
        }

        return parameters;
    }

    public static class Parameter {
        public Annotation[] annotations;
        public Class<?> type;
        public String name;
        public Object value;

        public <T extends Annotation> T getAnnotation(Class<T> tClass) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == tClass) {
                    return (T) annotation;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }
    }


    public String formatUrl(String oUrl, Parameter[] parameters) {
        String end = "";
        if (oUrl.contains("?")) end = oUrl.substring(oUrl.indexOf("?"));
        int index = oUrl.indexOf("//");
        String head = "";
        if (index != -1) {
            head = oUrl.substring(0, index);
            oUrl = oUrl.substring(index + 2);
        } else {
            head = "http:";
        }
        while (oUrl.contains("//")) {
            oUrl = oUrl.replace("//", "/");
        }
        if (oUrl.endsWith("/")) oUrl = oUrl.substring(0, oUrl.length() - 1);
        String rUrl= head + "//" + oUrl + end;

        for (ResultBody.Parameter parameter:parameters){
            Param annotation = parameter.getAnnotation(Param.class);
            if (!StringUtil.isEmpty(annotation.replace())){
                rUrl=rUrl.replace("{"+parameter.name+"}",String.valueOf(parameter.value));
            }
        }
        return rUrl;
    }

    private void error(Throwable e) {
        if (errorCallBack != null) {
            try {
                errorCallBack.action(e);
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new RuntimeException(exception);
            }
        } else if (failCallBack != null) {
            try {
                failCallBack.action(Integer.MIN_VALUE, e.getMessage());
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new RuntimeException(exception);
            }
        } else throw new RuntimeException(e);
    }


    public void asyncExec() {
        new Thread(() -> {
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
            R data = null;
            try {
                data = result.action(m);
            } catch (FailException e) {
                if (failCallBack != null) failCallBack.action(e.error, e.getMessage());
                return;
            }
            if (successCallBack != null) successCallBack.action(data);
        } catch (Throwable e) {
            e.printStackTrace();
            error(e);
        } finally {
            try {
                if (finallyCallBack != null) finallyCallBack.action();
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public R lineRun() throws Exception {
        return result.action(m);
    }

//        public ResultBody<M> selfParams(CallBack<JSONObject> paramsResultAction) {
//            try {
//                paramsResultAction.action(linearHttp.re.getParams());
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            }
//            return this;
//        }

    @Setter
    LinearHttp.CallBack<R> successCallBack;
    @Setter
    LinearHttp.FailCallBack failCallBack;
    @Setter
    LinearHttp.ErrorCallBack errorCallBack;
    @Setter
    LinearHttp.EmptyAction finallyCallBack;

//        public ResultBody<M> addParams(String key, Object value) {
//            linearHttp.re.params.put(key, value);
//            return this;
//        }
//
//        /**
//         * 设置成功事件 成功的判定于对应的HttpRequest定义
//         *
//         * @param successCallBack
//         * @return
//         */
//        public ResultBody<M> setSuccessCallBack(CallBack<M> successCallBack) {
//            linearHttp.setSuccessCallBack(successCallBack);
//            return this;
//        }
//
//        public ResultBody<M> setFailCallBack(FailCallBack failCallBack) {
//            linearHttp.setFailCallBack(failCallBack);
//            return this;
//        }
//
//        public ResultBody<M> setErrorCallBack(ErrorCallBack errorCallBack) {
//            linearHttp.setErrorCallBack(errorCallBack);
//            return this;
//        }
//
//        public ResultBody<M> setFinallyCallBack(EmptyAction finallyCallBack) {
//            linearHttp.setFinallyCallBack(finallyCallBack);
//            return this;
//        }
}
