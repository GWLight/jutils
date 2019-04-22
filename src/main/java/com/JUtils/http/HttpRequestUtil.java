/**
 * 文件说明
 *
 * @Description:扩展说明
 * @Copyright: 2015 com.njusoft.dss Inc. All right reserved
 * @Version: 1.2.18.14
 */
package com.shhn.pmm.util;

import com.shhn.pmm.exception.SmsException;

import com.shhn.pmm.exception.SmsException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/** 
  * @ClassName HttpRequestUtil  
 * @Description 模拟请求 
  * @author meng  
 * @date 2016年1月6日  
 *   
 */
public class HttpRequestUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

    /**
     * post
     * @param url
     * @param params
     * @return
     */
    public static String Post(String url, Map<String, String> params) {
        // 创建HttpGet对象
        HttpPost httpPost = new HttpPost(url);
        // 创建HttpClient对象
        org.apache.http.client.HttpClient client = new DefaultHttpClient();
        HttpResponse response = null;

        try {

            //组装请求参数，key-value形式的
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, "GBK"));
            }
            //这个是直接post一串字符串的方式，如json串，并不用带key
			/*StringEntity reqEntity = new StringEntity(reqMsg, "GBK");//解决中文乱码问题
			reqEntity.setContentEncoding("GBK");
			reqEntity.setContentType("application/json");
			httpPost.setEntity(reqEntity);*/

            HttpEntity entity = null;
            String result = null;
            //执行post方法
            response = client.execute(httpPost);
            EntityUtils.consume(entity);
            // 判断类型
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 取出服务器返回的数据
                String content = EntityUtils.toString(response.getEntity(), "utf-8");
                System.out.println("content----->" + content);
                return content;
            }
        } catch (IOException e) {
            System.out.println("--------------------------URL访问异常------------------");
            e.printStackTrace();
            return null;
        }
        return null;
    }
    /**  
     * @Title: postRequest  
     * @Description: 模拟POST请求 
     * @param @param url
     * @param @param params
     * @param @return      
     * @return String  
     * @author meng  
     * @date 2016年1月6日 下午5:49:25    
     * @throws  
     */
    public static String postRequest(String url, Map<String, String> params) {
        //构造HttpClient的实例
        HttpClient httpClient = new HttpClient();

        //创建POST方法的实例
        PostMethod postMethod = new PostMethod(url);

        //设置请求头信息
        postMethod.setRequestHeader("Connection", "close");

//        postMethod.addRequestHeader("Content-Type","application/json;charset=utf-8");
        //添加参数
        for (Map.Entry<String, String> entry : params.entrySet()) {
            postMethod.addParameter(entry.getKey(), entry.getValue());
        }

        //使用系统提供的默认的恢复策略,设置请求重试处理，用的是默认的重试处理：请求三次
        httpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);

        //接收处理结果
        String result = null;
        try {
            //执行Http Post请求
            httpClient.executeMethod(postMethod);

            //返回处理结果
            result = postMethod.getResponseBodyAsString();
        } catch (HttpException e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            // 发生网络异常
            System.out.println("发生网络异常!");
            e.printStackTrace();
        } finally {
            //释放链接
            postMethod.releaseConnection();

            //关闭HttpClient实例
            if (httpClient != null) {
                ((SimpleHttpConnectionManager) httpClient.getHttpConnectionManager()).shutdown();
                httpClient = null;
            }
        }
        return result;
    }

    /**  
     * @Title: getRequest  
     * @Description: 模拟GET请求 
     * @param @param url
     * @param @param params
     * @param @return      
     * @return String  
     * @author meng  
     * @date 2016年1月6日 下午5:49:00    
     * @throws  
     */
    public static String getRequest(String url, Map<String, String> params) {
        //构造HttpClient实例
        HttpClient client = new HttpClient();

        //拼接参数
        String paramStr = "";
        for (String key : params.keySet()) {
            paramStr = paramStr + "&" + key + "=" + params.get(key);
        }
        paramStr = paramStr.substring(1);

        //创建GET方法的实例
        GetMethod method = new GetMethod(url + "?" + paramStr);

        //接收返回结果
        String result = null;
        try {
            //执行HTTP GET方法请求
            client.executeMethod(method);

            //返回处理结果
            result = method.getResponseBodyAsString();
        } catch (HttpException e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            // 发生网络异常
            System.out.println("发生网络异常!");
            e.printStackTrace();
        } finally {
            //释放链接
            method.releaseConnection();

            //关闭HttpClient实例
            if (client != null) {
                ((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
                client = null;
            }
        }
        return result;
    }



    /**
     *	发送短信通道
     *
     * @param url
     * @param appkey
     * @param secret
     * @param smsType
     * @param smsFreeSignName
     * @param smsParamString
     * @param recNum
     * @param smsTemplateCode
     * @return
     * @throws
     */
    public static void postRequest(String url, String appkey, String secret, String smsType, String smsFreeSignName, String smsParamString, String recNum, String smsTemplateCode) throws SmsException {

    }
}