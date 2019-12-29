package cqupt.jyxxh.uclass.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.pojo.KeChengInfo;
import cqupt.jyxxh.uclass.utils.GetDataFromJWZX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


import java.util.ArrayList;
import java.util.Map;

/**
 * 获取用户课表
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 19:08 2019/12/2
 */

@Service
public class KebiaoService {

    Logger logger= LoggerFactory.getLogger(KebiaoService.class);

    @Autowired
    private GetDataFromJWZX getDataFromJWZX;    //去教务在线获取数据的工具类

    @Autowired
    private JedisPool jedisPool;                 //redis连接池

    @Value("${URLStuKebiaoFromJWZX}")
    private String URL_STUKEBIAO_FROM_JWZX;       //从教务在线获取学生课表的URL

    @Value("${URLTeaKebiaoFromJWZX}")
    private String URL_TEAKEBIAO_FROM_JWZX;       //从教务在线获取教师课表URL




    /**
     *  根据学号或者教师号获取课表
     *
     * @param number 学号或者教师号
     * @param type  类别，"s"代表学生，"t"代表老师
     * @return ArrayList<ArrayList<ArrayList<KeChengInfo>>>
     */
    public String getKebiao(String number, String type) throws JsonProcessingException {

        //课表 json格式字符串
        String kebiao=null;

        //实例化json操作对象
        ObjectMapper objectMapper=new ObjectMapper();

        //获取jedis,并选择第二个库（课表缓存都放在第二个库）。
        Jedis jedis = jedisPool.getResource();
        jedis.auth("root");
        jedis.select(1);


        //1.判断缓存中是否存在该用户课表，如果有就从缓存取。
        try{
            Boolean exists = jedis.exists("kebiao_" + number);
            if (exists){
                //存在从缓存取
                kebiao = jedis.get("kebiao_" + number);
                jedis.close();
                return kebiao;
            }
        }catch (Exception e){
            logger.debug("【课表缓存（KebiaoService.getkebiao）】出现未知错误！");
        }



        //2.根据学号或者教师号去获取教务在线的课表页（html）
        switch (type){
            //学生
            case "s":{

                ArrayList<ArrayList<ArrayList<KeChengInfo>>> stukebiaoByXh = getDataFromJWZX.getStukebiaoByXh(number);

                 kebiao = objectMapper.writeValueAsString(stukebiaoByXh);

                 //放进缓存
                jedis.set("kebiao_"+number,kebiao);
                jedis.close();
                break;
            }
            //老师
            case "t":{
                ArrayList<ArrayList<ArrayList<KeChengInfo>>> teaKebiaoByTeaId = getDataFromJWZX.getTeaKebiaoByTeaId(number);
                kebiao = objectMapper.writeValueAsString(teaKebiaoByTeaId);

                //放进缓存
                jedis.set("kebiao_"+number,kebiao);
                jedis.close();
                break;
            }
        }

        return kebiao;
    }

    /**
     * 获取教务时间，匹配课表
     * @return Map，教务时间
     */
    public Map<String,String> getSchoolTime(){

        return getDataFromJWZX.getSchoolTime();
    }


}
