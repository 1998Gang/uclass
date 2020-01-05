package cqupt.jyxxh.uclass.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import java.io.IOException;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 封装java网络请求，通过调用该类方法，来获得指定的网络资源。
 *
 * @author 彭渝刚
 * @since 1.0.0
 */

@Component
public  class SendHttpRquest {

    final static Logger logger= LoggerFactory.getLogger(SendHttpRquest.class);



    /**
     * GET请求，访问指定的接口获得json数据
     *
     *例如：https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
     *
     *
     * @param url      访问地址（只是前面地址，不加参数）  https://api.weixin.qq.com/sns/jscode2session
     * @param param    访问参数列表（完整）              appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
     * @return        访问结果，json字符串
     */
    public static String getJson(String url, String param)  {
        //定义访问的返回值
        String json=null;
        //声明URL对象
        URL urlReal;
        try {
            //实例化URL对象
            urlReal = new URL(url+"?"+param);

            //使用Jsoup,最长时间10秒。
            Document document = Jsoup.parse(urlReal, 10000);

            //获取body中的json数据
            json= document.body().text();

            //转码并返回
            return Parse.decodeUnicode(json);

        }catch (Exception e){
            //日志
            logger.error("【HTTP请求JSON（SendHttpRquest.getJson）】请求失败！请求超时！");
        }

        return json;
    }

    /**
     * GET请求，访问指定url(带参数)，获取html页面
     * @param url  访问地址
     * @param param  参数列表
     * @return  html页面（字符串形式）
     * @throws IOException   创建get请求，参数异常。
     */
    public static String getHtmlWithParam(String url, String param) throws IOException {

        String html=null;

        CloseableHttpClient httpClient= HttpClients.createDefault();

        //1.定义请求响应结果
        CloseableHttpResponse response;
        //2.创建get请求
        HttpGet httpGet=new HttpGet(url+"?"+param);
        //3.发送请求
        response=httpClient.execute(httpGet);
        //4.判断http响应码,200进行解析
        if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            //4.1 获取响应实体
            HttpEntity entity = response.getEntity();
             html = EntityUtils.toString(entity, "utf-8");
             if (logger.isDebugEnabled()){
                 logger.debug("【网络请求工具类（getHtmlWithParam）】获取html页面成功");
             }
        }else {
            //4.2 不为200
            if (logger.isDebugEnabled()){
                logger.debug("【网络请求工具类（getHtmlWithParam）】获取html页面失败");
            }
        }
        //5.返回获取的html界面，以字符串方式
        return html;
    }

    /**
     * 访问指定url，获取html页面,不许要参数
     * @param url 访问地址
     * @return html页面（字符串）
     * @throws IOException 发起请求异常
     */
    public static String getHtml(String url) throws IOException {

        CloseableHttpClient httpClient= HttpClients.createDefault();

        String html=null;

        //1.生成httpclient

        CloseableHttpResponse response;
        //2.创建get请求
        HttpGet httpGet=new HttpGet(url);
        //3.发送请求
        response=httpClient.execute(httpGet);
        //4.判断http响应码,200进行解析
        if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            //4.1 获取响应实体
            HttpEntity entity = response.getEntity();
            html = EntityUtils.toString(entity, "utf-8");
            if (logger.isDebugEnabled()){
                logger.debug("【网络请求工具类（getHtml）】获取html页面成功");
            }
        }else {
            //4.2 不为200
            if (logger.isDebugEnabled()){
                logger.debug("【【网络请求工具类（getHtml）】获取html失败");
            }
        }
        //5.返回获取的html界面，以字符串方式
        return html;
    }

    /**
     * GET请求，访问指定url，携带cookie，获取html页面。。
     * @param url 访问地址
     * @param cookie cookie
     * @return html页面（字符串形式）
     * @throws IOException 发起请求异常
     */
    public static String getHtmlWithCookie(String url,String cookie) throws IOException {

        CloseableHttpClient httpClient= HttpClients.createDefault();

        String html=null;

        //1.创建get请求
        HttpGet httpGet=new HttpGet(url);
        //2.为get请求设置cookie
        httpGet.setHeader("Cookie",cookie);
        //3.发起请求
        CloseableHttpResponse response = httpClient.execute(httpGet);
        //4.判断响应代码,如果为200,进行下一步。
        if (HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){
            //4.1将请求结果（html页面）转换为string类型
            html = EntityUtils.toString(response.getEntity(), "utf-8");

            //日志
            if (logger.isDebugEnabled()){
                logger.debug("【网络请求工具类（getHtmlWithCookie）】获取html页面成功");
            }
        }else {
            //4.2,请求失败
            if (logger.isInfoEnabled()){
                logger.debug("【网络请求工具类（getHtmlWithCookie）】获取html失败");
            }
        }


        //5.返回
        return html;
    }

    /**
     * GET请求，访问指定url，获取请求响应。
     * @param url 请求地址
     * @return response,响应对象
     */
    public static CloseableHttpResponse getResponse(String url) throws IOException {

        CloseableHttpClient httpClient= HttpClients.createDefault();


        CloseableHttpResponse response;

        // 1.创建一个get请求。
        HttpGet httpGet=new HttpGet(url);

        // 2.发起请求

         response = httpClient.execute(httpGet);

        // 3.判断响应状态码，如果为200，请求成功，返回该响应体对象。
        if (200==response.getStatusLine().getStatusCode()){
            return response;
        }else {
            return null;
        }

    }


    /**
     * POST请求，访问学校统一认证平台，校验用户账户,获取响应体对象。
     *
     * @param url  学校统一认证平台（跳转教务在线）
     * @param jssessionid 第一次访问统一认证平台获取的cookie
     * @param form 第一次访问统一认证平台获取的form表单数据，需要与账户密码一起提交。
     * @param ykth 一卡通号号（统一认证码）
     * @param password  统一认证密码
     */
    public static CloseableHttpResponse postResponse(String url, String jssessionid, Map<String, String> form, String ykth, String password) throws IOException {

        CloseableHttpClient httpClient= HttpClients.createDefault();

        // 1.创建一个POST请求
        HttpPost httpPost=new HttpPost(url);

        //设置cookie策略，这一步我也不知道什么原理，反正没有这一段会有一个警告。
        RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        httpPost.setConfig(defaultConfig);

        // 2.设置请求头数据，cookie
        httpPost.setHeader("Cookie",jssessionid);

        // 3.设置请求参数。表单数据，请求参数。
        List<NameValuePair> basicNameValuePairList=new ArrayList<>();
        basicNameValuePairList.add(new BasicNameValuePair("username",ykth));//统一认证码
        basicNameValuePairList.add(new BasicNameValuePair("password",password));//密码
        basicNameValuePairList.add(new BasicNameValuePair("lt",form.get("lt")));
        basicNameValuePairList.add(new BasicNameValuePair("execution",form.get("execution")));
        basicNameValuePairList.add(new BasicNameValuePair("_eventId",form.get("_eventId")));
        basicNameValuePairList.add(new BasicNameValuePair("rmShown",form.get("rmShown")));
        //      3.2创建from表单实体
        UrlEncodedFormEntity urlEncodedFormEntity=new UrlEncodedFormEntity(basicNameValuePairList,"utf-8");
        //       3.3将from表单添加到post请求里
        httpPost.setEntity(urlEncodedFormEntity);

        // 4.发起请求
        CloseableHttpResponse response = httpClient.execute(httpPost);

        return response;

    }
}
