package cqupt.jyxxh.uclass.service;

import checkers.oigj.quals.O;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.pojo.KbStuListData;
import cqupt.jyxxh.uclass.pojo.Student;
import cqupt.jyxxh.uclass.utils.GetDataFromJWZX;
import cqupt.jyxxh.uclass.utils.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * u课堂一些组件功能的srvice类
 * 1.获取教务时间
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 15:27 2020/1/3
 */

@Service
public class ComponentService {

    Logger logger= LoggerFactory.getLogger(ComponentService.class);

    @Autowired
    private GetDataFromJWZX getDataFromJWZX;    //从教务在线获取数据的工具类

    @Autowired
    private RedisService redisService;           //操作redis的操作类

    /**
     * 获取教务时间，匹配课表
     * @return Map，教务时间
     */
    public String  getSchoolTime(){

        //教务时间，json字符串格式。
        String schoolTime = null;

        //操作josn的对象
        ObjectMapper objectMapper=new ObjectMapper();

        // 获取当前日期
        Date date=new Date();
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
        String nowData = simpleDateFormat.format(date);

        //操作redis的key
        String key ="schooltime_"+nowData;

        //1.去redis缓存拿教务时间
        try {
            String data = redisService.getSchoolTime(key);
            if (!"false".equals(data)){
                //缓存中有
                schoolTime=data;
                //直接返回
                return schoolTime;
            }
        }catch (Exception e){
            logger.error("【获取教务时间（ComponentService.getSchoolTime）】从缓存获取教务时间出现未知错误！");
        }


        //2.缓存没有，去教务在线获取
        try {
            //2.1 获取教务时间，得到的数据是map集合
            Map<String, String> schoolTimeMap= getDataFromJWZX.getSchoolTime();
            //2.2将map集合转为json字符串
            schoolTime = objectMapper.writeValueAsString(schoolTimeMap);
            //2.3将教务时间放入redis缓存
            try {
                redisService.setSchoolTime(key,schoolTime);
            }catch (Exception e){
                logger.error("【获取教务时间（ComponentService.getSchoolTime）】将教务时间添加到缓存出现未知错误！");
            }
        }catch (Exception e){
            logger.error("【获取教务时间（ComponentService.getSchoolTime）】从教务在线获取教务时间出现未知错误！");
        }

        return schoolTime;
    }

    /**
     * 获取解析之后的教学班学生名单。（名单人数，所含专业、班级等，不同选课状态的学生。）
     * @param jxb 教学班
     * @return String
     */
    public String getKbStuListData(String jxb)  {
        //教学班学生名单数据。
        String stuListData=null;

        //json操作对象
        ObjectMapper objectMapper=new ObjectMapper();

        //操作redsi的key
        String key = "stulistdata_"+jxb;

        // 1.先从缓存中取
        try {
            //1.1 从缓存获取数据
            String data = redisService.getStuListData(key);
            //1.2 判断
            if (!"false".equals(data)){
                //缓存中有
                stuListData=data;
                //直接返回数据
                return stuListData;
            }
        }catch (Exception e){
            logger.error("【获取学生名单(ComponentService.getKbStuListData)】从缓存获取数据出现未知错误！");
        }

        // 2.去教务在线获取
        try {

            //2.1.根据教学班获取学生名单。
            List<Student> stuList = getDataFromJWZX.getKbStuList(jxb);

            //2.2.解析名单，获取该名单的具体数据,。
            KbStuListData kbStuListData = Parse.parseStuListToKbStuListData(stuList);

            //2.3 将KbStuListData转换为json字符串格式。
            stuListData = objectMapper.writeValueAsString(kbStuListData);

            //2.4将数据添加到缓存
            try {
                redisService.setStuListData(key,stuListData);
            }catch (Exception e){
                logger.error("【获取学生名单(ComponentService.getKbStuListData)】将数据添加到缓存出现未知错误！");
            }
        } catch (IOException e) {
            logger.error("【获取学生名单（ComponentService.getKbStuListData）】出现未知错误！");
        }

        return stuListData;
    }
}
