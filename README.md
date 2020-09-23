## [LinearHttp 线性请求框架]

网络请求封装器，更优美地使用RPC方式进行调用

> 支持android及Java web开发，支持Java7及以上

* 实例 [Demo文件及逐行解析->Demo.java](src\main\java\com\by122006\linearhttp\Demo.java)

    > 进行网络请求
                                                                                  >
         public class Demo {
             public static void main(String[] args) {
                 LinearHttp.create(HttpTest.class)
                         .query(a -> a.heartBeat(3))
                         .setSuccessCallBack(System.out::println)
                         .setFailCallBack((a, b) -> System.out.println("请求失败："+b))
                         .exec();
             }
         }
         
    > 请求接口定义

         @HttpRpc(url = "http://localhost:8080/sz/ads-admin")
         interface HttpTest {
             @Get(prePath = "/connect")
             String[] heartBeat(@Param("deviceId") int deviceId);
             @Post(prePath = "/device")
             JSONObject invokeDevice(@Param("loginKey") String loginKey, @Param("deviceMac") String deviceMac);
             @Get(path = "/special/schedule")
             List<String> abc123(@Param("deviceId") int deviceId);
         }

* 支持功能
    1. RPC框架
    2. 链式调用响应成功失败
    3. 支持自定义参数解析器、请求封装器、网络请求方法
    4. 弱类型匹配
    5. 支持java web及android,支持java7

* 插件引入

    Step 1. 在你的根目录项目`build.gradle`文件中加入以下仓库目录及插件依赖

	    allprojects {
        	repositories {
        		...
        		maven { url 'https://jitpack.io' }
        	}
        }
		dependencies {
            implementation 'com.github.122006:LinearHttp:当前版本号'
        }


    当前版本号：[![](https://jitpack.io/v/122006/LinearHttp.svg)](https://jitpack.io/#122006/LinearHttp)



* 注意事项

