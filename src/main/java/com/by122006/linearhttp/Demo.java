package com.by122006.linearhttp;

import com.alibaba.fastjson.JSONObject;
import com.by122006.linearhttp.analyse.param.DefaultParamsAnalyse;
import com.by122006.linearhttp.annotations.Param;
import com.by122006.linearhttp.analyse.request.HUCHandler;
import com.by122006.linearhttp.analyse.result.DefaultDataAnalyse;
import com.by122006.linearhttp.annotations.*;

import java.util.List;

public class Demo {
    public static void main(String[] args) {
        LinearHttp.create(HttpTest.class)//定义需要使用的接口类
                .query(a -> a.heartBeat(3))//指定网络请求方法并填充参数
                //业务成功回调，直接使用接口定义格式的返回值进行业务处理
                .setSuccessCallBack(System.out::println)
                //【可选】ErrorCallBack，若未定义会throwable.getMessage()并转发至FailCallBack回调
                .setErrorCallBack(throwable -> System.out.println("请求失败：" + throwable.getMessage()))
                //FailCallBack定义处理网络请求中发生的任何错误，若未定义会抛出至上级
                .setFailCallBack((errorCode, msg) -> System.out.println("请求失败：" + msg))
                //【可选】网络请求结束回调
                .setFinallyCallBack(()->{})
                //开始同步执行请求 异步请求：asyncExec()
                .exec();
    }
}

@HttpRpc(
        url = "http://localhost:8080/sz/ads-admin"//定义网络请求的根url
        , dataAnalyse = DefaultDataAnalyse.class//【可选】返回数据解析器,可继承或重写该类定义返回结构
        , requestHandler = HUCHandler.class//【可选】请求处理器，默认使用HttpURLConnection进行转发
        , paramsAnalyse = DefaultParamsAnalyse.class//【可选】rpc参数解析器，可以重写以定义特殊的解析规则
        , headers={}//【可选】默认请求头
)
interface HttpTest {
    /**
     * @Get(prePath = "/connect") prePath：定义前置拼接url段 分隔斜杠可省略
     * 方法名：heartBeat 拼接至url最后
     * @Get @Post 定义请求方法
     * @param deviceId  @Param("deviceId") 【字段名必填！】定义传递字段，注解指定字段名.
     * @return 返回期望的数据格式，会尽量匹配改值，特殊处理请重写{@see analyse.result.DefaultDataAnalyse}
     * <br>如果不需要解析返回值，请返回Void（如果使用action()调用可返回void）
     */
    @Get(prePath = "/connect")
    String[] heartBeat(@Param("deviceId") int deviceId);

    /**
     * Post请求参数支持自动装箱，多个参数会合并为一个json进行数据传递而不用定义数据类</br>
     * 如果参数只有一个，请指定@Param(unBox=true)进行指定装箱，反之反序列化为String传递
     */
    @Post(prePath = "/device")
    JSONObject invokeDevice(@Param("loginKey") String loginKey, @Param("deviceMac") String deviceMac);

    /**
     * 使用path参数将忽略 prePath及方法名
     */
    @Get(path = "/special/schedule")
    List<String> abc123(@Param("deviceId") int deviceId);
}
