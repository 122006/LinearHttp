package com.by122006.linearhttp.analyse.request;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HUCHandler implements RequestHandler {
    @Override
    public ResultBox post(String[] headers,String url,String content) throws Exception{
        URL oUrl = new URL(url);
        //使用创建的URL对象的openConnection()方法创建一个HttpURLConnection对象
        HttpURLConnection httpURLConnection = (HttpURLConnection)oUrl.openConnection();
        //使用URL对象创建HttpURLConnection对象
        httpURLConnection = (HttpURLConnection) oUrl.openConnection();
        //设置相应参数
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);    //可以创建输出流，将请求参数写入
        httpURLConnection.setRequestMethod("POST"); //请求方式为POST
        for(String header:headers){
            int i = header.indexOf(":");
            httpURLConnection.addRequestProperty(header.substring(0,i).trim(),header.substring(i+1).trim());
        }
        //将请求参数写入连接的输出流
        PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
        printWriter.print(content);
        printWriter.flush();
        int responseCode = httpURLConnection.getResponseCode();
        InputStreamReader inputStreamReader;
        if (responseCode!=200&&responseCode!=400){
            inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream(),"utf-8");
        }else {
            inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(),"utf-8");
        }
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line);
        }
        String response = stringBuilder.toString().trim();

        return ResultBox.of(response,responseCode);
    }

    @Override
    public ResultBox get(String[] headers,String url) throws Exception{
        URL oUrl = new URL(url);
        //使用创建的URL对象的openConnection()方法创建一个HttpURLConnection对象
        HttpURLConnection httpURLConnection = (HttpURLConnection)oUrl.openConnection();
        // 设置请求方法为 GET 请求
        httpURLConnection.setRequestMethod("GET");
        //使用输入流
        httpURLConnection.setDoInput(true);
        //GET 方式，不需要使用输出流
        httpURLConnection.setDoOutput(false);
        //设置超时
        httpURLConnection.setConnectTimeout(10000);
        httpURLConnection.setReadTimeout(1000);
        for(String header:headers){
            int i = header.indexOf(":");
            httpURLConnection.addRequestProperty(header.substring(0,i).trim(),header.substring(i+1).trim());
        }
        //连接
        httpURLConnection.connect();
        int responseCode = httpURLConnection.getResponseCode();
        InputStreamReader inputStreamReader;
        if (responseCode!=200&&responseCode!=400){
            inputStreamReader = new InputStreamReader(httpURLConnection.getErrorStream(),"utf-8");
        }else {
            inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(),"utf-8");
        }
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        String response = "";
        //每次读取一行，若非空则添加至 stringBuilder
        while((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line);
        }
        //读取所有的数据后，赋值给 response
        response = stringBuilder.toString().trim();

        return ResultBox.of(response,responseCode);
    }
}
