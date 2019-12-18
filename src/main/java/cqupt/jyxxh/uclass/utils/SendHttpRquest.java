package cqupt.jyxxh.uclass.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import java.io.IOException;



import java.net.URL;


/**
 * 封装java网络请求，通过调用该类方法，来获得指定的网络资源。
 *
 * @author 彭渝刚
 * @since 1.0.0
 */

@Component
public  class SendHttpRquest {

    final static Logger logger= LoggerFactory.getLogger(SendHttpRquest.class);

    //httplient对象
    private static CloseableHttpClient httpClient= HttpClients.createDefault();

    /**
     * 访问指定的接口获得json数据
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

            //使用Jsoup,请求时间10秒，
            Document document = Jsoup.parse(urlReal, 10000);

            //获取body中的json数据
            json= document.body().text();

            return Parse.decodeUnicode(json);


        }catch (Exception e){
            e.printStackTrace();
        }

        //对获取的返回值进行转码
        return json;
    }

    /**
     * 访问指定url(带参数)，获取html页面
     * @param url  访问地址
     * @param param  参数列表
     * @return  html页面（字符串形式）
     * @throws IOException   创建get请求，参数异常。
     */
    public static String getHtmlWithParam(String url, String param) throws IOException {

        String html=null;

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
                logger.debug("【网络请求工具类（getHtmlWithParam）】获取html失败");
            }
        }
        //5.返回获取的html界面，以字符串方式
        return html;
    }


    /**
     * 访问指定url，带cookie。
     * @param url 访问地址
     * @param cookie cookie
     * @return html页面（字符串形式）
     * @throws IOException 发起请求异常
     */
    public static String getHtmlWithCookie(String url,String cookie) throws IOException {

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
     * 访问指定url，获取html页面,不许要参数
     * @param url 访问地址
     * @return html页面（字符串）
     * @throws IOException 发起请求异常
     */
    public static String getHtml(String url) throws IOException {
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




}
