package cqupt.jyxxh.uclass.utils;

import cqupt.jyxxh.uclass.service.EduAccountService;
import cqupt.jyxxh.uclass.service.RedisService;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

@Component
public class SimulationLogin {

    Logger logger = LoggerFactory.getLogger(SimulationLogin.class);

    @Value("${JWZX.URLAuthserverLogin}")
    private String URL_AUTHSERVER_LOGIN_TO_JWZX;          //统一身份认证平台URL（登陆教务在线）

    @Autowired
    private RedisService redisService;                 //操作redis的类

    @Autowired
    private EduAccountService eduAccountService;        //教务账户操作类


    /**
     *获取用户成功登陆教务在线后代表登陆状态的 cookie ，这个cookie可以用于继续模拟用户访问教务在线获取数据。有效时间应该是30分钟。
     *
     * @param ykth 一卡通号
     * @param password 密码
     * @return String  模拟登陆成功后，代表用户的登陆状态的cookie ("PHPSESSID=ST-225477-knWmfbSib4wz2mQ2d2eY-NlvE-ids1-1577527149129")
     * @throws IOException 发起http请求的相关异常
     */
    public  String getPhpsessid(String ykth, String password) throws Exception {


        // 模拟登陆教务在线后，获得的cookie值，PHPSESSID.
        String phpsessid;

        //1.先去查找缓存（redis）
        try {
            String data = redisService.getPhpsessid(ykth);
            if (!"false".equals(data)){
                //获取的数据不为“false”说明获取成功
                phpsessid=data;
                //直接返回数据
                return phpsessid;
            }
        }catch (Exception e){
            //日志
            logger.error("获取缓存步骤出现未知错误！");
        }

        //缓存没有，就去登陆教务在线获取。
        // 2.第一次GET请求学校统一认证平台，获取cookie值（JSESSIONID），以及表单值（lt、execution、_eventId、rmShown）。用于第二次POST请求。
        // 2.1 获取响应实体
        CloseableHttpResponse responseFirst = SendHttpRquest.getResponse(URL_AUTHSERVER_LOGIN_TO_JWZX);
        // 2.2 获取cookie值(JSSESSIONID)
        assert responseFirst != null;
        String jssessionid = getJssessionid(responseFirst);
        // 2.3 获取表单值（lt、execution、_eventId、rmShown）
        Map<String,String> form=getForm(responseFirst);

        // 3.第二次POST请求学校统一认证平台，获取身份校验成功后的重定向地址location。需要账号密码，以及第一次GET请求得到的Cookie值，还有表单值。
        String location ;
        // 3.1 获取相应实体
        CloseableHttpResponse responseSeconed = SendHttpRquest.postResponse(URL_AUTHSERVER_LOGIN_TO_JWZX, jssessionid, form, ykth, password);
        // 3.2判断响应状态码，200说明账号密码不正确。302说明账号密码正确，可以进行下一步
        if (302==responseSeconed.getStatusLine().getStatusCode()){
            //账号验证成功,获取重定向的地址location，重定向地址含有验证成功（模拟登陆成功）后的PHPSESSID。
            Header[] locations = responseSeconed.getHeaders("location");
            //名为location的头只有一个，直接用索引0获取该头的元素。
            HeaderElement[] elements = locations[0].getElements();
            //该头的元素也只有一个，直接用索引0获取数据.name是”http://jwzx.cqupt.edu.cn/tysfrz/index.php?ticket“ value是（其实也就是登陆成功后的PHPSESSID）”ST-192565-QbzPDtIP3vcpQkb4gUCa-NlvE-ids1-1576923576219“
            String name = elements[0].getName();
            //value其实就是验证成功（模拟登陆成功）后的PHPSESSID的值，但是此时这个PHPSESSID还不能用。还需要向“location”这个地址发起一次GET请求，做一下验证。
            String value = elements[0].getValue();
            //拼装url
             location=name+"="+value;
        }else {
            //进入这里，一般说明模拟登陆时，身份认证没通过。（应该是因为用户更改了密码）
            //为什么不在此方法开始时就做一次身份认证？（因为绝大多数时候，密码都是正确的，极少时候会有密码错误。如果每次都调用一下身份验证，有点浪费资源）
            //抛出异常,身份过期。
            // 同时删除该用户的教务账户。
            // 这样在下一次用户登陆的时候，就会被判定出来，身份过期，需要重新绑定了。
            eduAccountService.deleteEduAccount(ykth);
            throw new Exception("Identity is overdue");
        }

        // 4.第三次GET请求，地址是第二步POST请求成功后重定向的地址（location）。请求一次这个地址，成功后，获取的PHPSWSSID才能生效。
        CloseableHttpResponse response = SendHttpRquest.getResponse(location);


        // 5.至此，模拟登陆完成。返回PHPSESSID，用于访问教务在线。phpsessidValue =  “=ST-225477-knWmfbSib4wz2mQ2d2eY-NlvE-ids1-1577527149129”
        String phpsessidValue=location.substring(location.indexOf("="));

        //拼装一下phpsessid。完整的要返回的phpsessid是 “PHPSESSID=ST-225477-knWmfbSib4wz2mQ2d2eY-NlvE-ids1-1577527149129”
        phpsessid="PHPSESSID"+phpsessidValue;

        // 6.将该数据存入缓存（redis），有效时间20分钟。
        try {
            redisService.setPhpsessid(ykth,phpsessid);
        }catch (Exception e){
            //日志
            logger.error("将cookie加入缓存失败！统一身份：[{}]",ykth);
        }

        return phpsessid;
    }

    /**
     * 获取第一次请求统一身份认证平台得到的cookie（JSSESSIONID）
     * @param response 第一次请求统一身份认证平台的响应体
     * @return JSSESSIONID
     */
    private  String getJssessionid(CloseableHttpResponse response){
        // 2.2 解析响应体
        assert response != null;
        // 2.3 获取Set-Cookie头对象数组
        Header[] headers = response.getHeaders("Set-Cookie");
        // 2.3 这第一次请求 Set-Cookie头只有一个，故直接用0进行索引，获取唯一一个Set-Cookie实体的元素数组。
        HeaderElement[] elements = headers[0].getElements();
        // 2.4 elements也只有一个,name是cookie的名（JSSESSIONID），value是值（）
        String name = elements[0].getName();
        String value = elements[0].getValue();
        // 2.5 返回
        return name+"="+value;
    }

    /**
     * 获取第一次请求统一身份认证平台响应界面中隐藏的表单数据。
     * @param response 第一次请求统一身份认证平台的响应体
     * @return Map集合，统一身份认证平台界面隐藏的表单数据
     * @throws IOException 请求异常
     */
    private  Map<String, String> getForm(CloseableHttpResponse response) throws IOException {
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
