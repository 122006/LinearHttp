package com.by122006.linearhttp.analyse.param;

import com.alibaba.fastjson.support.odps.udf.CodecCheck;
import com.by122006.linearhttp.LinearHttp;
import com.by122006.linearhttp.ResultBody;
import com.by122006.linearhttp.annotations.AddParams;
import com.by122006.linearhttp.annotations.Param;
import com.by122006.linearhttp.interfaces.IParamsHandler;
import com.by122006.linearhttp.utils.ArraysUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class DefaultParamsHandler implements IParamsHandler {
    @Override
    public ResultBody.Parameter[] handler(Method method, ResultBody.Parameter[] parameters) throws Exception {
        AddParams addParams = method.getAnnotation(AddParams.class);
        String[] strings = addParams.addParams();
        if (strings.length > 0) {
            ResultBody.Parameter[] adds = handle(strings);
            return ArraysUtil.concat(parameters, adds);
        } else
            return parameters;
    }
    @Override
    public HashMap<String, LinearHttp.ResultAction<?>> addParams() throws Exception {
        return new HashMap<>();
    }

    public ResultBody.Parameter[] handle(String[] addParamsKey) throws Exception {
        ArrayList<ResultBody.Parameter> list=new ArrayList<>();
        for (String str:addParamsKey) {
            ResultBody.Parameter parameter = createParameter(str, addParams().get(str).action());
            list.add(parameter);
        }

        return list.toArray(new ResultBody.Parameter[0]);
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
        };
        parameter.annotations=new Annotation[]{param};
        parameter.name=name;
        parameter.type=value.getClass();
        parameter.value=value;
        return parameter;
    }


}
