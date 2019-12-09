package cqupt.jyxxh.uclass.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.utils.SendHttpRquest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 *
 * 对微信数据接口操作的类
 *
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:19 2019/11/2
 */
@Service
public class GetInfoFromWxService {

    private final Logger logger= LoggerFactory.getLogger(GetInfoFromWxService.class);    //日志

    @Value("${code2Session}")
    private  String code2Session;      //微信的auth.code2Session网址
    @Value("${AppID}")
    private  String appid;             //小程序的appid
    @Value("${AppSercet}")
    private String appSercet;         //小程序的appSercet


    @Autowired
    private  SendHttpRquest sendHttpRquest;      //发起网络请求的工具类




    /**
     * 访问微信的接口 用code换取openid与session_key的json字符串，并解析字符串返回指定键的值
     *
     * @param code 微信临时身份验证码
     * @param key  想要获取的参数类型我
     * @return  返回openid或者sessionkey
     */
    private String getOpenIdOrSessionkey(String code, String key)  {
        //访问接口地址
        String url=code2Session;
        //访问参数拼接
        String param="appid="+appid+"&secret="+appSercet+"&js_code="+code+"&grant_type=authorization_code";
        //发起访问 返回json字符串
        String result=sendHttpRquest.getJsonfromhttp(url,param);
        //创建jackson核心对象
        ObjectMapper objectMapper=new ObjectMapper();
        //解析json字符串，返回指定键的值
        String value=null;
        try {
            JsonNode jsonNode=objectMapper.readTree(result).findValue(key);
            value=jsonNode.toString();
        }catch (Exception e){

            if (logger.isErrorEnabled()){
                logger.error("【获取{}失败（GetInfoFromWxService.getOpenIdOrSessionkey）】code:[{}]可能是无效的，微信接口返回值:[{}]",key,code,result);
            }
            System.out.println();
        }
        return value;
    }

    /**
     *  获取openid
     *
     * @param code 微信小程序临时身份验证
     * @return openid
     */
    public String getOpenid(String code)  {
        String openid=null;
        //捕获一个潜在的异常，
        try{
            openid = getOpenIdOrSessionkey(code,"openid").replace("\"","");
        }catch (Exception ignored){
        }
        return openid;
    }


    /**
     * 获取session_key
     *
     * @param code 微信小程序临时身份验证
     * @return sessionkey
     */
    public String getSessionkey(String code)  {
        String sessionkey=null;
        //捕获一个潜在的异常
        try{
            sessionkey = getOpenIdOrSessionkey(code,"session_key").replace("\'","");
        }catch (Exception ignored){
        }
        return sessionkey;
    }
}
