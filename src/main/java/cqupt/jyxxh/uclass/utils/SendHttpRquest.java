package cqupt.jyxxh.uclass.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * 封装java网络请求，通过调用该类方法，来获得指定的网络资源。
 *
 * @author 彭渝刚
 * @since 1.0.0
 */

@Component
public  class SendHttpRquest {

    final Logger logger= LoggerFactory.getLogger(SendHttpRquest.class);


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
    public String getJsonfromhttp(String url,String param)  {
        //定义访问的返回值
        String result="";
        //创建URL对象
        URL url1= null;
        try {
            url1 = new URL(url+"?"+param);
            //打开输入流
            InputStream inputStream = url1.openStream();
            //定义bufferedInputstream接受读取返回值
            BufferedInputStream bufferedInputStream=new BufferedInputStream(inputStream);
            //定义接收数组 一次读取1024个字节
            byte [] bytes=new byte[1024];
            //定义bufferedInputStream read方法返回长度值
            int len=0;
            //循环读取BufferedInputStraeam
            while ((len=bufferedInputStream.read(bytes))!=-1){
                result +=new String(bytes,0,len);
            }
        } catch (MalformedURLException e) {
            logger.error("【网络请求（getJsonfromhttp）】 URL地址错误",e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("【网络请求（getJsonfromhttp）】 解析返回数据异常",e);
            e.printStackTrace();
        }

        //对获取的返回值进行转码
        String duResult = Parse.decodeUnicode(result);

        return duResult;
    }

    /**
     * 访问指定url，获取html页面
     * @param url  访问地址
     * @param param  参数列表
     * @return  html页面（字符串形式）
     * @throws IOException   创建get请求，参数异常。
     */
    public  String getHtmlFromHttp(String url,String param) throws IOException {

        String html=null;

        //1.生成httpclient
        CloseableHttpClient httpClient= HttpClients.createDefault();
        CloseableHttpResponse response=null;
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
                 logger.debug("【getHtmlFromHttp】获取html页面成功");
             }
        }else {
            //4.2 不为200
            if (logger.isDebugEnabled()){
                logger.debug("【getHtmlFromHttp】获取html失败");
            }
        }
        //5.返回获取的html界面，以字符串方式
        return html;
    }




}
