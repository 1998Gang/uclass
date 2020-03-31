package cqupt.jyxxh.uclass.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.dao.ComponentsMapper;
import cqupt.jyxxh.uclass.dao.StudentMapper;
import cqupt.jyxxh.uclass.dao.TeacherMapper;
import cqupt.jyxxh.uclass.pojo.*;
import cqupt.jyxxh.uclass.pojo.qiandao.KeChengQdOneStuRecord;
import cqupt.jyxxh.uclass.pojo.tiwen.KeChengTwOneStuRecord;
import cqupt.jyxxh.uclass.utils.GetDataFromJWZX;
import cqupt.jyxxh.uclass.utils.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * u课堂一些组件功能的srvice类
 * 1.获取教务时间
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 15:27 2020/1/3
 */

@Service
public class ComponentService {

    Logger logger = LoggerFactory.getLogger(ComponentService.class);

    /**
     * 从教务在线获取数据的工具类
     */
    @Autowired
    private GetDataFromJWZX getDataFromJWZX;

    /**
     * 操作redis的操作类
     */
    @Autowired
    private RedisService redisService;

    /**
     * 提问功能有关操作对象
     */
    @Autowired
    private TiWenService tiWenService;

    /**
     * 签到功能有关操作对象
     */
    @Autowired
    private QianDaoService qianDaoService;

    /**
     * 学生信息操作接口
     */
    @Autowired
    private StudentMapper studentMapper;

    /**
     * 教师信息操作接口
     */
    @Autowired
    private TeacherMapper teacherMapper;

    /**
     * 扩展功能信息操作接口
     */
    @Autowired
    private ComponentsMapper componentsMapper;


    /**
     * 获取教务时间，匹配课表
     *
     * @return Map，教务时间
     */
    public SchoolTime getSchoolTime() {

        //教务时间，json字符串格式。
        SchoolTime schoolTime = null;

        //操作josn的对象
        ObjectMapper objectMapper = new ObjectMapper();

        // 获取当前日期
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowData = simpleDateFormat.format(date);

        //1.去redis缓存拿教务时间
        try {
            String data = redisService.getSchoolTime(nowData);
            if (!"false".equals(data)) {
                //缓存中有,将json字符串数据转为schoooltime对象
                schoolTime = objectMapper.readValue(data, SchoolTime.class);
                //直接返回
                return schoolTime;
            }
        } catch (Exception e) {
            logger.error("从缓存获取教务时间出现未知错误！");
        }


        //2.缓存没有，去教务在线获取
        try {
            //2.1 获取教务时间
            schoolTime = getDataFromJWZX.getSchoolTime();
            //2.2将schooltime对象转为json字符串
            String schooltimeJson = objectMapper.writeValueAsString(schoolTime);
            //2.3将教务时间放入redis缓存
            try {
                redisService.setSchoolTime(nowData, schooltimeJson);
            } catch (Exception e) {
                logger.error("将教务时间添加到缓存出现未知错误！");
            }
        } catch (Exception e) {
            logger.error("从教务在线获取教务时间出现未知错误！");
        }
        return schoolTime;
    }

    /**
     * 获取该教学班的学生名单
     *
     * @param jxb 教学班号
     * @return classStuList
     */
    public ClassStuList getClassStuList(String jxb) {
        //json操作对象
        ObjectMapper objectMapper = new ObjectMapper();

        //返回对象
        ClassStuList classStuListNoQdAndTwData;

        // 1.先从缓存中取
        try {
            //1.1 从缓存获取课程名单数据
            String data = redisService.getClassStuList(jxb);
            //1.2 判断
            if (!"false".equals(data)) {
                //将json字符串转为KbStuListData对象
                classStuListNoQdAndTwData = objectMapper.readValue(data, ClassStuList.class);

                //日志
                if (logger.isDebugEnabled()) {
                    logger.debug("获取课程[{}]学生名单成功！(缓存)", jxb);
                }

                //返回数据
                return classStuListNoQdAndTwData;
            }
        } catch (Exception e) {
            logger.error("从缓存获取学生名单数据数据出现未知错误！");
        }

        // 2.去教务在线获取
        try {

            //2.1.根据教学班获取学生名单。没有学生的签到数据以及回答问题数据
            classStuListNoQdAndTwData = getDataFromJWZX.getClassStuListFromJwzx(jxb);

            //2.2 将该名单放进缓存（redis第4个数据库）
            String jsonClassStuList = objectMapper.writeValueAsString(classStuListNoQdAndTwData);
            try {
                redisService.setClassStuList(jxb, jsonClassStuList);
            } catch (Exception e) {
                logger.error("将课程名单数据添加到缓存出现未知错误！");
            }

            //日志
            if (logger.isDebugEnabled()) {
                logger.debug("获取课程[{}]学生名单成功！(教务在线)", jxb);
            }

            // 2.5 返回数据
            return classStuListNoQdAndTwData;

        } catch (IOException e) {
            logger.error("出现未知错误！");
        }

        return null;
    }

    /**
     * 获取解析之后的教学班学生名单。（名单人数，所含专业、班级等，不同选课状态的学生。）以及学生的签到 回答问题等数据
     *
     * @param jxb 教学班
     * @return KbStuListData类的json字符串，如果教学班是错误的，查出的数据是空的。
     */
    public ClassStuListData getClassStuListData(String jxb) {
        try {
            //1 根据教学班获取学生名单（没有学生的签到数据以及回答问题数据）。
            ClassStuList classStuListNoQdAndTwData = this.getClassStuList(jxb);

            //2 将学生的签到数据 以及问题回答数据加载到名单中
            ClassStuList classStuListHasQdAndTwData = this.addTwAndQdData(classStuListNoQdAndTwData, jxb);

            //3 解析名单，得到kbStuListData类（包含学生名单，统计数据,学生的签到数据，回答问题数据）并返回
            ClassStuListData classStuListData = Parse.parseStuListToKbStuListData(classStuListHasQdAndTwData);

            //日志
            if (logger.isDebugEnabled()) {
                logger.debug("获取课程[{}]学生名单解析数据成功！", jxb);
            }

            return classStuListData;

        } catch (Exception e) {
            logger.error("获取课程[{}]学生名单解析数据出错！", jxb);
            return null;
        }
    }

    /**
     * 用户添加完善自己的电话邮箱等信息
     *
     * @param params map集合 {ykth=‘一卡通号’,'email'='邮箱','phone'='电话号','accountType'='用户类型（"s"为学生，"t"为老师）'}
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addMailAndPhone(Map<String, String> params) {
        try {
            //判断用户类型执行不同操作
            switch (params.get("accountType")) {
                case "s": {
                    studentMapper.addStuEmailAndPhone(params);
                    break;
                }
                case "t": {
                    teacherMapper.addTeaEmailAndPhone(params);
                    break;
                }
                default: {
                    break;
                }
            }
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 用户反馈使用问题
     *
     * @param feeback 反馈问题实体
     */
    public boolean feebackProblems(Feeback feeback) {
        try {
            //操作dao层
            componentsMapper.insterFeebackInfo(feeback);
            //成功，返回true
            return true;
        } catch (Exception e) {
            logger.error("添加用户反馈数据失败！");
        }
        return false;
    }

    /**
     * 用来给教学班的学生名单，加上学生的签到数据、和答题数据。
     *
     * @param classStuList ClassStuLsit
     * @return ClassStuList 学生名单
     */
    private ClassStuList addTwAndQdData(ClassStuList classStuList, String jxb) {
        try {
            //1.获取该门课程的历史提问数据（有记录的学生名单）
            List<KeChengTwOneStuRecord> noAnswerStuList = tiWenService.getKcTwHistory(jxb).getNoAnswerStuList();
            //1.1 将数据取出，以HashMap格式存储，{xh=wdTimes}
            HashMap<String, Integer> noAnStuMap = new HashMap<>(16);
            for (KeChengTwOneStuRecord chengTwOneStuRecord : noAnswerStuList) {
                noAnStuMap.put(chengTwOneStuRecord.getXh(), chengTwOneStuRecord.getWdTime());
            }
            //2.获取该门课程的历史签到数据（有记录的学生名单）
            List<KeChengQdOneStuRecord> stuList = qianDaoService.getKcQdHistory(jxb).getStuList();
            //2.1 将数据取出，以HashMap格式存储，{xh={qdTime=1,qjTime=2,cdTime=3}}
            HashMap<String, HashMap<String, Integer>> noQdStuMap = new HashMap<>(16);
            for (KeChengQdOneStuRecord keChengQdOneStuRecord : stuList) {
                //组装学生的各个状态的次数
                HashMap<String, Integer> hashMap = new HashMap<>(3);
                hashMap.put("qqTime", keChengQdOneStuRecord.getQqTime());
                hashMap.put("qjTime", keChengQdOneStuRecord.getQjTime());
                hashMap.put("cdTime", keChengQdOneStuRecord.getCdTime());
                //将各个状态数据放进代表改学生的键值对中
                noQdStuMap.put(keChengQdOneStuRecord.getXh(), hashMap);
            }


            //3.遍历课程的学生名单，将有记录的学生数据进行组装
            List<ClassStuInfo> classStuInfoList = classStuList.getClassStuInfoList();
            for (ClassStuInfo classStuInfo : classStuInfoList) {
                String xh = classStuInfo.getXh();
                if (noAnStuMap.containsKey(xh)) {
                    //如果该学生有未答题记录，取出记录，并赋值
                    classStuInfo.setWdTime(noAnStuMap.get(xh));
                }
                if (noQdStuMap.containsKey(xh)) {
                    //如果该学生有未签到数据，取出记录，并赋值
                    classStuInfo.setQjTime(noQdStuMap.get(xh).get("qjTime"));
                    classStuInfo.setCdTime(noQdStuMap.get(xh).get("cdTime"));
                    classStuInfo.setQqTime(noQdStuMap.get(xh).get("qqTime"));
                }
            }

            //日志
            if (logger.isDebugEnabled()) {
                logger.debug("给课程[{}]学生名单添加学生的历史签到、回答数据成功！", jxb);
            }

            return classStuList;
        } catch (Exception e) {
            //日志
            logger.error("给课程[{}]学生名单添加学生的历史签到、回答数据失败！", jxb);
            return classStuList;
        }
    }
}
