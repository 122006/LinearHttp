package com.by122006.linearhttp;

import com.alibaba.fastjson.JSONObject;
import com.by122006.linearhttp.analyse.param.Param;
import com.by122006.linearhttp.annotations.Get;
import com.by122006.linearhttp.annotations.HttpRpc;
import com.by122006.linearhttp.annotations.Post;

public class Demo {
    public static void main(String[] args) {
        LinearHttp.create(HttpTest.class)
                .query(a -> a.heartBeat(3))
                .setSuccessCallBack(System.out::println)
                .setFailCallBack((a, b) -> System.out.println("请求失败："+b))
                .exec();
        System.out.println("end1");
        LinearHttp.create(HttpTest.class)
                .query(a -> a.invokeDevice("123456","123"))
                .setSuccessCallBack(System.out::println)
                .setFailCallBack((a, b) -> System.out.println("请求失败："+b))
                .exec();
        System.out.println("end2");
    }
}


@HttpRpc
interface HttpTest {
    static String getUrl(){
        return "http://10.1.44.122:8080/sz/ads-admin";
    }
    @Get(prePath = "/deviceConnect")
    String[] heartBeat(@Param("deviceId") int deviceId);
    @Post(prePath = "/deviceConnect")
    JSONObject invokeDevice(@Param("loginKey") String loginKey, @Param("deviceMac") String deviceMac);
}
