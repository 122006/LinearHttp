package com.by122006.linearhttp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.by122006.linearhttp.analyse.param.Param;
import com.by122006.linearhttp.analyse.request.RequestHandler;
import com.by122006.linearhttp.analyse.request.ResultBox;
import com.by122006.linearhttp.analyse.result.ResultAnalyse;
import com.by122006.linearhttp.annotations.*;
import com.by122006.linearhttp.exceptions.*;
import com.by122006.linearhttp.utils.*;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;

@Accessors(chain = true)
public class ResultBody<R, M> {

    private final LinearHttp.Function<R, M> result;
    LinearHttp<M> linearHttp;
    Class<M> requestClass;
    M m;

    private static final HashMap<Class<? extends ResultAnalyse>, ResultAnalyse> resultAnalyseMap = new HashMap<>();
    private static final HashMap<Class<? extends RequestHandler>, RequestHandler> requestHandlerHashMap = new HashMap<>();

    ResultAnalyse resultAnalyse;
    RequestHandler requestHandler;

    public ResultBody(LinearHttp<M> linearHttp, LinearHttp.Function<R, M> result) {
        this.linearHttp = linearHttp;
        this.result = result;
        this.requestClass = linearHttp.requestClass;
        action();
    }

    private void action() {
        HttpRpc classAnnotation = requestClass.getAnnotation(HttpRpc.class);

        if ((resultAnalyse = resultAnalyseMap.get(classAnnotation.dataAnalyse())) == null) {
            try {
                resultAnalyse = classAnnotation.dataAnalyse().newInstance();
                resultAnalyseMap.put(classAnnotation.dataAnalyse(), resultAnalyse);
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
        if ((requestHandler = requestHandlerHashMap.get(classAnnotation.requestHandler())) == null) {
            try {
                requestHandler = classAnnotation.requestHandler().newInstance();
                requestHandlerHashMap.put(classAnnotation.requestHandler(), requestHandler);
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
            classUrl= (String) getUrl.invoke(null);
        } catch (Throwable e) {
            classUrl=classAnnotation.url();
        }
        String finalClassUrl = classUrl;
        Object o = Proxy.newProxyInstance(requestClass.getClassLoader(), new Class[]{requestClass}, (proxy, method, args) -> {
            if (method.isDefault()) return method.invoke(args);
            Post post = method.getAnnotation(Post.class);
            Get get = method.getAnnotation(Get.class);
            String requestName = method.getName();
            ResultBox resultBox;

            if (post != null) {
                String url = StringUtil.isEmpty(post.path())
                        ? finalClassUrl + "/" + post.prePath() + "/" + requestName + "/"
                        : post.path() + "/";
                url=formatUrl(url);
                String[] headers = post.headers().length == 0 ? classAnnotation.headers() : post.headers();
                Parameter[] parameters = getParameters(method);
                if (parameters.length != args.length)
                    throw new RuntimeException(String.format("传入参数%d与方法入参数%d不一致", args.length, parameters.length));
                String str;
                if (args.length == 1) {
                    Param annotation = parameters[0].getAnnotation(Param.class);
                    String name = annotation != null && !StringUtil.isEmpty(annotation.value())
                            ? annotation.value()
                            : parameters[0].getName();
                    if (annotation != null && annotation.unBox()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(name, args);
                        str = jsonObject.toJSONString();
                    } else {
                        str = JSON.toJSONString(args[0]);
                    }
                } else {
                    //多参数一定是拆分
                    JSONObject jsonObject = new JSONObject();
                    for (int i = 0; i < args.length; i++) {
                        Param annotation = parameters[i].getAnnotation(Param.class);
                        String name = annotation != null && !StringUtil.isEmpty(annotation.value())
                                ? annotation.value()
                                : parameters[i].getName();
                        jsonObject.put(name, args[i]);
                    }
                    str = jsonObject.toJSONString();

                }
                resultBox = requestHandler.post(headers, url, str);
            } else if (get != null) {
                String url = StringUtil.isEmpty(get.path())
                        ? finalClassUrl + "/" + get.prePath() + "/" + requestName + "/"
                        : get.path() + "/";
                url=formatUrl(url);
                String[] headers = get.headers().length == 0 ? classAnnotation.headers() : get.headers();

                Parameter[] parameters = getParameters(method);
                if (parameters.length != args.length)
                    throw new RuntimeException(String.format("传入参数%d与方法入参数%d不一致", args.length, parameters.length));

                StringBuilder str = new StringBuilder();
                if (!url.contains("?")){
                    url+="?";
                }
                for (int i = 0; i < args.length; i++) {
                    Param annotation = parameters[i].getAnnotation(Param.class);
                    String name = annotation != null && !StringUtil.isEmpty(annotation.value())
                            ? annotation.value()
                            : parameters[i].getName();
                    str.append(name)
                            .append("=")
                            .append(args[i]);
                    if (i != args.length - 1) str.append("&");
                }
                resultBox = requestHandler.get(headers, url  + str);
            } else {
                throw new RuntimeException("没有定义Get或Post注解");
            }
            int httpCode = resultBox.getHttpCode();
            resultAnalyse.codeCheck(httpCode,resultBox.getResult());
            Class<?> returnType = method.getReturnType();
            if (returnType == void.class || returnType == Void.class) {
                return null;
            } else
                return resultAnalyse.analyse(resultBox.getResult(), returnType);
        });
        m = requestClass.cast(o);
    }

    private Parameter[] getParameters(Method method) {
        //java7没有getParameters方法
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        int parameterCount = parameterAnnotations.length;
        Parameter[] parameters=new Parameter[parameterCount];
        Class<?>[] parameterTypes = method.getParameterTypes();
        for(int i = 0; i< parameterCount; i++){
            parameters[i]=new Parameter();
            parameters[i].annotations= parameterAnnotations[i];
            parameters[i].type= parameterTypes[i];
            parameters[i].name= "arg"+i;
        }
        return parameters;
    }

    public static class Parameter{
        Annotation[] annotations;
        Class<?> type;
        String name;
        public <T extends Annotation> T getAnnotation(Class<T> tClass){
            for (Annotation annotation:annotations){
                if (annotation.annotationType()==tClass){
                    return (T) annotation;
                }
            }
            return null;
        }
        public String getName(){
            return name;
        }
    }


    public String formatUrl(String oUrl){
        String end="";
        if (oUrl.contains("?")) end=oUrl.substring(oUrl.indexOf("?"));
        int index=oUrl.indexOf("//");
        String head="";
        if (index!=-1){
            head=oUrl.substring(0,index);
            oUrl=oUrl.substring(index+2);
        }else {
            head="http:";
        }
        while (oUrl.contains("//")) {
            oUrl = oUrl.replace("//", "/");
        }
        if (oUrl.endsWith("/")) oUrl=oUrl.substring(0,oUrl.length()-1);
        return head+"//"+oUrl+end;
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
