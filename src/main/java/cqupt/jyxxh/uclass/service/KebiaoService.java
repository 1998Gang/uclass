package cqupt.jyxxh.uclass.service;


import checkers.units.quals.A;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.pojo.KeChengInfo;
import cqupt.jyxxh.uclass.pojo.StuKcMoreInfo;
import cqupt.jyxxh.uclass.utils.GetDataFromJWZX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 获取用户课表
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 19:08 2019/12/2
 */

@Service
public class KebiaoService {

    Logger logger = LoggerFactory.getLogger(KebiaoService.class);

    @Autowired
    private GetDataFromJWZX getDataFromJWZX;    //去教务在线获取数据的工具类

    @Autowired
    private RedisService redisService;          //reids操作类


    /**
     * 根据学号或者教师号获取课表
     *
     *先去教务在线获取，如果获取成功，就返回，并放进缓存，有效期6小时
     *
     * 教师获取课表后，还会将教学班号加载进缓存，与教师号对应。
     *
     *
     * @param number 学号或者教师号
     * @param type   类别，"s"代表学生，"t"代表老师
     * @return ArrayList<ArrayList < ArrayList < KeChengInfo>>>
     */
    public String getKebiao(String number, String type) throws IOException {

        //课表 json格式字符串
        String kebiao = null;

        //实例化json操作对象
        ObjectMapper objectMapper = new ObjectMapper();

        //1.先去redis中获取
        try {
            String data = redisService.getKebiao(number);
            if (!"false".equals(data)) {
                //从缓存获取数据成功！
                kebiao = data;
                //直接返回。
                return kebiao;
            }
        } catch (Exception e) {
            logger.debug("【获取课表缓存（KebiaoService.getkebiao）】出现未知错误！");
        }

        //2.根据学号或者教师号去获取教务在线的课表页（html）
        switch (type) {
            //学生
            case "s": {
                ArrayList<ArrayList<ArrayList<KeChengInfo>>> stukebiaoByXh = getDataFromJWZX.getStukebiaoByXh(number);
                //将嵌套集合转位json字符串
                kebiao = objectMapper.writeValueAsString(stukebiaoByXh);

                //放进缓存。
                try {
                    boolean b = redisService.setKeBiao(number, kebiao);
                } catch (Exception e) {
                    logger.debug("【添加学生课表缓存（KebiaoService.getkebiao）】出现未知错误！");
                }
                break;
            }
            //老师
            case "t": {
                ArrayList<ArrayList<ArrayList<KeChengInfo>>> teaKebiaoByTeaId = getDataFromJWZX.getTeaKebiaoByTeaId(number);

               //将教学班与教师的对应关系放进redis 有效期150天，用于根据教学班找到对应教师。 redis数据格式： key=jxb(教学班)，value=teaId(教师号)
                try{
                    //获取该教师所教的所有教学班，
                    Set<String> jxbs=new HashSet<>();
                    for (ArrayList<ArrayList<KeChengInfo>> kss:teaKebiaoByTeaId){
                        for (ArrayList<KeChengInfo> ks:kss){
                            for (KeChengInfo k:ks){
                                if (k!=null){
                                    jxbs.add(k.getJxb());
                                }
                            }
                        }
                    }
                    //将教学班与教师id放进缓存，有效时间150天
                    redisService.setJxbDYTeaId(jxbs,number);
                }catch (Exception e){
                    logger.error("缓存教学班与教师关系出现未知错误！");
                }

                //将嵌套集合转位json字符串
                kebiao = objectMapper.writeValueAsString(teaKebiaoByTeaId);

                //将课表集合 结果放进缓存。
                try {
                    boolean b = redisService.setKeBiao(number, kebiao);
                } catch (Exception e) {
                    logger.debug("【添加教师课表缓存（KebiaoService.getkebiao）】出现未知错误！");
                }
                break;
            }
        }
        return kebiao;
    }

    /**
     * 根据学号和教学班获取该课程完整的历史数据
     * 包含教师数据，成绩组成，签到历史，答题历史
     * @param xh 学号
     * @param jxb 教学班
     * @return StuKcMoreInfo
     */
    public StuKcMoreInfo getKcStuMoreInfo(String xh, String jxb) {
        try {
            //1.获取教师数据
            //1.1 根据教学班获取该班的教师id，在redis缓存（第8个数据库）
            String teaId = redisService.getTeaIdByJxb(jxb);
            //1.2根据teaid获取数据库中的教师数据




        }catch (Exception e){

        }
        return null;
    }
}
