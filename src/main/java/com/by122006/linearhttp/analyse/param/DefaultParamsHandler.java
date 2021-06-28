package com.by122006.linearhttp.analyse.param;

import com.by122006.linearhttp.LinearHttp;
import com.by122006.linearhttp.ResultBody;
import com.by122006.linearhttp.annotations.AddParams;
import com.by122006.linearhttp.annotations.Param;
import com.by122006.linearhttp.interfaces.IParamsHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultParamsHandler implements IParamsHandler {
    @Override
    public List<ResultBody.Parameter> handler(Method method, List<ResultBody.Parameter> parameters) throws Exception{
        HashMap<String, LinearHttp.ResultAction<?>> stringResultActionHashMap = defaultAddParams();
        List<ResultBody.Parameter> list=new ArrayList<>();
        for(Map.Entry<String, LinearHttp.ResultAction<?>> co:stringResultActionHashMap.entrySet()){
            list.add(createParameter(co.getKey(),co.getValue().action()));
        }
        parameters.addAll(list);
        AddParams addParams = method.getAnnotation(AddParams.class);

        String[] strings=null;
        if (addParams!=null&&(strings=addParams.value()).length > 0) {
            List<ResultBody.Parameter> adds = handle(strings);
            parameters.addAll(adds);
            return parameters;
        } else
            return parameters;
    }
    public HashMap<String, LinearHttp.ResultAction<?>> defaultAddParams() throws Exception {
        return new HashMap<>();
    }
    public HashMap<String, LinearHttp.ResultAction<?>> addParams() throws Exception {
        return new HashMap<>();
    }

    public List<ResultBody.Parameter> handle(String[] addParamsKey) throws Exception {
        ArrayList<ResultBody.Parameter> list=new ArrayList<>();
        for (String str:addParamsKey) {
            ResultBody.Parameter parameter = createParameter(str, addParams().get(str).action());
            list.add(parameter);
        }

        return list;
    }

    public ResultBody.Parameter createParameter(String name, Object value) {
        ResultBody.Parameter parameter=new ResultBody.Parameter();
        Param param = new Param(){
            @Override
            public Class<? extends Annotation> annotationType() {
                return Param.class;
            }

            @Override
            public String value() {
                return name;
            }

            @Override
            public boolean unBox() {
                return false;
            }

            @Override
            public boolean restful() {
                return false;
            }

            @Override
            public String restfulStr() {
                return "";
            }

            @Override
            public boolean header() {
                return false;
            }

        };
        parameter.annotations=new Annotation[]{param};
        parameter.name=name;
        parameter.type=value.getClass();
        parameter.value=value;
        return parameter;
    }
    public ResultBody.Parameter createHeader(String name, Object value) {
        ResultBody.Parameter parameter=new ResultBody.Parameter();
        Param param = new Param(){
            @Override
            public Class<? extends Annotation> annotationType() {
                return Param.class;
            }

            @Override
            public String value() {
                return name;
            }

            @Override
            public boolean unBox() {
                return false;
            }

            @Override
            public boolean restful() {
                return false;
            }

            @Override
            public String restfulStr() {
                return "";
            }

            @Override
            public boolean header() {
                return true;
            }

        };
        parameter.annotations=new Annotation[]{param};
        parameter.name=name;
        parameter.type=value.getClass();
        parameter.value=value;
        return parameter;
    }


}
