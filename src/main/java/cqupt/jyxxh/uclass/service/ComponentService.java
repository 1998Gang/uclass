package cqupt.jyxxh.uclass.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.dao.ComponentsMapper;
import cqupt.jyxxh.uclass.dao.StudentMapper;
import cqupt.jyxxh.uclass.dao.TeacherMapper;
import cqupt.jyxxh.uclass.pojo.ClassStuInfo;
import cqupt.jyxxh.uclass.pojo.Feeback;
import cqupt.jyxxh.uclass.pojo.KbStuListData;
import cqupt.jyxxh.uclass.pojo.SchoolTime;
import cqupt.jyxxh.uclass.utils.GetDataFromJWZX;
import cqupt.jyxxh.uclass.utils.Parse;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private StudentMapper studentMapper;         //学生信息操作接口

    @Autowired
    private TeacherMapper teacherMapper;         //教师信息操作接口

    @Autowired
    private ComponentsMapper componentsMapper;   //扩展功能信息操作接口

    /**
     * 获取教务时间，匹配课表
     * @return Map，教务时间
     */
    public SchoolTime  getSchoolTime(){

        //教务时间，json字符串格式。
        SchoolTime schoolTime = null;

        //操作josn的对象
        ObjectMapper objectMapper=new ObjectMapper();

        // 获取当前日期
        Date date=new Date();
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
        String nowData = simpleDateFormat.format(date);

        //1.去redis缓存拿教务时间
        try {
            String data = redisService.getSchoolTime(nowData);
            if (!"false".equals(data)){
                //缓存中有,将json字符串数据转为schoooltime对象
               schoolTime = objectMapper.readValue(data, SchoolTime.class);
                //直接返回
                return schoolTime;
            }
        }catch (Exception e){
            logger.error("【获取教务时间（ComponentService.getSchoolTime）】从缓存获取教务时间出现未知错误！");
        }


        //2.缓存没有，去教务在线获取
        try {
            //2.1 获取教务时间
            schoolTime = getDataFromJWZX.getSchoolTime();
            //2.2将schooltime对象转为json字符串
            String schooltimeJson = objectMapper.writeValueAsString(schoolTime);
            //2.3将教务时间放入redis缓存
            try {
                redisService.setSchoolTime(nowData,schooltimeJson);
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
     * @return KbStuListData类的json字符串，如果教学班是错误的，查出的数据是空的。
     */
    public KbStuListData getKbStuListData(String jxb)  {
        //教学班学生名单数据。
        KbStuListData kbStuListData=null;
        //json操作对象
        ObjectMapper objectMapper=new ObjectMapper();

        // 1.先从缓存中取
        try {
            //1.1 从缓存获取数据
            String data = redisService.getStuListData(jxb);
            //1.2 判断
            if (!"false".equals(data)){
                //将json字符串转为KbStuListData对象
                kbStuListData = objectMapper.readValue(data, KbStuListData.class);
                //直接返回数据
                return kbStuListData;
            }
        }catch (Exception e){
            logger.error("【获取学生名单(ComponentService.getKbStuListData)】从缓存获取数据出现未知错误！");
        }

        // 2.去教务在线获取
        try {

            //2.1.根据教学班获取学生名单。
            List<ClassStuInfo> ClassStuList = getDataFromJWZX.getKbStuList(jxb);

            //2.2.解析名单数据，得到kbStuListData类（包含学生名单，统计数据）。
            kbStuListData = Parse.parseStuListToKbStuListData(ClassStuList);

            //2.3 将KbStuListData对象转换为json字符串格式。
            String data = objectMapper.writeValueAsString(kbStuListData);

            //2.4将数据添加到缓存(redis第4个数据库)
            try {
                redisService.setStuListData(jxb,data);
            }catch (Exception e){
                logger.error("【获取学生名单(ComponentService.getKbStuListData)】将数据添加到缓存出现未知错误！");
            }

            // 2.5 返回数据
            return kbStuListData;
        } catch (IOException e) {
            logger.error("【获取学生名单（ComponentService.getKbStuListData）】出现未知错误！");
        }

        return kbStuListData;
    }

    /**
     * 用户添加完善自己的电话邮箱等信息
     * @param params map集合 {ykth=‘一卡通号’,'email'='邮箱','phone'='电话号','accountType'='用户类型（"s"为学生，"t"为老师）'}
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addMailAndPhone(Map<String, String> params) {
        try{
            //判断用户类型执行不同操作
            switch(params.get("accountType")){
                case "s":{
                    studentMapper.addStuEmailAndPhone(params);
                    break;
                }
                case "t":{
                    teacherMapper.addTeaEmailAndPhone(params);
                    break;
                }
                default:{
                    break;
                }
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 用户反馈使用问题
     * @param feeback 反馈问题实体
      */
    public boolean feebackProblems(Feeback feeback) {
        try {
            //操作dao层
            componentsMapper.insterFeebackInfo(feeback);
            //成功，返回true
            return true;
        }catch (Exception e){
            logger.error("添加用户反馈数据失败！");
        }
        return false;
    }
}
