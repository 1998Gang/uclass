package cqupt.jyxxh.uclass.utils;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 【工具类】
 * 模拟登陆教务在线，走的是统一身份认证。
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 18:55 2019/12/19
 */
public class SimulationLogin {


    /**
     *
     * @param url  登陆教务在线URL（统一认证平台方式）https://ids.cqupt.edu.cn/authserver/login?service=http%3A%2F%2Fjwzx.cqupt.edu.cn%2Ftysfrz%2Findex.php
     * @param ykth 一卡通号
     * @param password 密码
     * @return PHPSESSID ，模拟登陆成功后，代表用户的登陆状态的cookie
     * @throws IOException 发起http请求的相关异常
     */
    public static String getPhpsessid(String url, String ykth, String password) throws IOException {
        //1.模拟登陆教务在线后，获得的cookie值，PHPSESSID.
        String phpsessid;

        // 2.第一次GET请求学校统一认证平台，获取cookie值（JSESSIONID），以及表单值（lt、execution、_eventId、rmShown）。用于第二次POST请求。
        // 2.1 获取响应实体
        CloseableHttpResponse responseFirst = SendHttpRquest.getResponse(url);
        // 2.2 获取cookie值(JSSESSIONID)
        assert responseFirst != null;
        String jssessionid = getJssessionid(responseFirst);
        // 2.3 获取表单值（lt、execution、_eventId、rmShown）
        Map<String,String> form=getForm(responseFirst);

        // 3.第二次POST请求学校统一认证平台，获取身份校验成功后的重定向地址location。需要账号密码，以及第一次GET请求得到的Cookie值，还有表单值。
        String location;
        // 3.1 获取相应实体
        CloseableHttpResponse responseSeconed = SendHttpRquest.postResponse(url, jssessionid, form, ykth, password);
        // 3.2判断响应状态码，200说明账号密码不正确。302说明账号密码正确，可以进行下一步
        if (302==responseSeconed.getStatusLine().getStatusCode()){
            //账号验证成功,获取重定向的地址location，重定向地址含有验证成功（模拟登陆成功）后的PHPSESSID。
            Header[] locations = responseSeconed.getHeaders("location");
            //名为location的头只有一个，直接用索引0获取该头的元素。
            HeaderElement[] elements = locations[0].getElements();
            //该头的元素也只有一个，直接用索引0获取数据.name是”http://jwzx.cqupt.edu.cn/tysfrz/index.php?ticket“ value是PHPSESSID”ST-192565-QbzPDtIP3vcpQkb4gUCa-NlvE-ids1-1576923576219“
            String name = elements[0].getName();
            //value其实就是验证成功（模拟登陆成功）后的PHPSESSID，但是此时这个PHPSESSID还不能用，还需要请求location，做以下验证。
            String value = elements[0].getValue();
             location=name+"="+value;

        }else {
            //账号验证失败
            return null;
        }

        // 4.第三次GET请求，身份验证成功后的重定向地址（location）。验证第二步POST请求成功后的地址。
        CloseableHttpResponse response = SendHttpRquest.getResponse(location);



        // 5.至此，模拟登陆完成。返回PHPSESSID，用于访问教务在线。
        phpsessid=location.substring(location.indexOf("="));
        return phpsessid;
    }


    /**
     * 获取第一次请求统一身份认证平台得到的cookie（JSSESSIONID）
     * @param response 第一次请求统一身份认证平台的响应体
     * @return JSSESSIONID
     */
    private static String getJssessionid(CloseableHttpResponse response){
        // 2.2 解析响应体
        assert response != null;
        // 2.3 获取Set-Cookie头对象数组
        Header[] headers = response.getHeaders("Set-Cookie");
        // 2.3 这第一次请求 Set-Cookie头只有一个，故直接用0进行索引，获取唯一一个Set-Cookie实体的元素数组。
        HeaderElement[] elements = headers[0].getElements();
        // 2.4 elements也只有一个,name是cookie的名（JSSESSIONID），value是值（）
        String name = elements[0].getName();
        String value = elements[0].getValue();
        String jssession=name+value;
        // 2.5 返回
        return jssession;
    }

    /**
     * 获取第一次请求统一身份认证平台响应界面中隐藏的表单数据。
     * @param response 第一次请求统一身份认证平台的响应体
     * @return Map集合，统一身份认证平台界面隐藏的表单数据
     * @throws IOException 请求异常
     */
    private static Map<String, String> getForm(CloseableHttpResponse response) throws IOException {
        // 1.实例化一个map集合用于存放获取的表单值
        Map<String,String> form=new HashMap<>();
        // 2.解析相应页面，获取（lt、execution、_eventId、rmShown）值
        HttpEntity authserver_loginEntity = response.getEntity();
        String s = EntityUtils.toString(authserver_loginEntity);
        Document parse = Jsoup.parse(s);
        Elements input = parse.getElementsByTag("input");
        for (Element element:input){
            String name = element.attr("name");
            String value = element.attr("value");

            if ("lt".equals(name)||"execution".equals(name)||"_eventId".equals(name)||"rmShown".equals(name)){
                form.put(name,value);
            }

        }
        return form;
    }
}
