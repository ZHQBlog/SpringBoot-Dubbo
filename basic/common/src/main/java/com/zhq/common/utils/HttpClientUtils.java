package com.zhq.common.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//HttpClient工具类，模拟发送http请求
public class HttpClientUtils {

    public static String doGet(String url, Map<String, String> param){
        String result="";
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            //创建uri对象
            URIBuilder uriBuilder = new URIBuilder(url);
            if (param!=null){
                for (String key : param.keySet()) {
                    uriBuilder.addParameter(key, param.get(key));
                }
            }
            URI uri = uriBuilder.build();
            //创建http的get请求
            HttpGet httpGet = new HttpGet(uri);
            //执行请求
            response = httpClient.execute(httpGet);
            //判断返回的状态码是否时200
            if (response.getStatusLine().getStatusCode()==200){
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String doGetAndHeaders(String url, Map<String, String> param){

        String result="";
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            //创建uri对象
            URIBuilder uriBuilder = new URIBuilder(url);
            URI uri = uriBuilder.build();
            //创建http的get请求
            HttpGet httpGet = new HttpGet(uri);
            //添加头部信息
            if (param != null){
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }
            //执行请求
            response = httpClient.execute(httpGet);
            //判断返回的状态码是否时200
            if (response.getStatusLine().getStatusCode()==200){
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String doGet(String url){
        return doGet(url, null);
    }

    public static String doPost(String url, Map<String, String> param){
        String result="";
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            //创建http的post请求
            HttpPost httpPost = new HttpPost(url);
            //创建参数列表
            if (param!=null){
                List<NameValuePair> pairList = new ArrayList<>();
                for (String key : param.keySet()) {
                    pairList.add(new BasicNameValuePair(key, param.get(key)));
                }
                //模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairList);
                httpPost.setEntity(entity);
            }
            //执行请求
            response = httpClient.execute(httpPost);
            //判断返回的状态码是否时200
            if (response.getStatusLine().getStatusCode()==200){
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String doPost(String url){
        return doPost(url, null);
    }

    public static String doPostJson(String url, String json){
        String result="";
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            //创建http的post请求
            HttpPost httpPost = new HttpPost(url);
            //创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            //执行请求
            response = httpClient.execute(httpPost);
            //判断返回的状态码是否时200
            if (response.getStatusLine().getStatusCode()==200){
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
