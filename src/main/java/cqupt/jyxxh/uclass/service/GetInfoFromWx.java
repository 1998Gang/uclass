package cqupt.jyxxh.uclass.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.utils.SendHttpRquest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;


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
public class GetInfoFromWx {

    final Logger logger= LoggerFactory.getLogger(GetInfoFromWx.class);    //日志

    @Value("${code2Session}")
    private  String code2Session;      //微信的auth.code2Session网址
    @Value("${AppID}")
    private  String appid;             //小程序的appid
    @Value("${AppSercet}")
    private String appSercet;         //小程序的appSercet


    @Autowired
    private SendHttpRquest sendHttpRquest;      //发起网络请求的工具类

    /**
     * 访问微信的接口 用code换取openid与session_key的json字符串，并解析字符串返回指定键的值
     *
     * @param code
     * @param key
     * @return
     * @throws IOException
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
        String value="nullOpenid";
        try {
            JsonNode jsonNode=objectMapper.readTree(result).findValue(key);
            value=jsonNode.toString();
        }catch (Exception e){
            if (logger.isErrorEnabled()){
                logger.error("【获取openid或sessionkey（GetInfoFromWx）】 code换openid，sessionkey，微信接口返回值[{}]",result);
                logger.error("【获取openid或sessionkey（GetInfoFromWx）】 获取{}失败，code:[{}]可能是无效的",key,code,e);
            }
        }
        return value;
    }

    /**
     *  获取openid
     *
     * @param code
     * @return
     */
    public String getOpenid(String code)  {
        String openid;
        openid = getOpenIdOrSessionkey(code,"openid");
        return openid;
    }


    /**
     * 获取session_key
     *
     * @param code
     * @return
     */
    public String getSessionkey(String code)  {
        String sessionkey= null;
        sessionkey = getOpenIdOrSessionkey(code,"session_key");
        return sessionkey;
    }
}
