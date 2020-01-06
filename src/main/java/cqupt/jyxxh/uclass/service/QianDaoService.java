package cqupt.jyxxh.uclass.service;

import cqupt.jyxxh.uclass.pojo.KbStuListData;
import cqupt.jyxxh.uclass.pojo.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 签到功能service类
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 1:18 2020/1/6
 */
@Service
public class QianDaoService {

    Logger logger= LoggerFactory.getLogger(QianDaoService.class);

    @Autowired
    private RedisService redisService;          //redis操作功能类

    @Autowired
    private ComponentService componentService;   //service层类，用于获取一些数据。


    /**
     * 教师发起签到
     * @param param map集合，是发起签到需要的必要参数。
     * @return boolean
     */
    public boolean teaCreatQiandao(Map<String, String> param) {

        try {
            //1.获取参数
            String jxb = param.get("jxb");//教学班
            String week = param.get("week");//上课周
            String work_day = param.get("work_day");//星期几（工作日）
            String qdcs = param.get("qdcs");//签到次数，这是针对这堂课的第几次签到
            String qdm = param.get("qdm");//签到码
            String yxsc = param.get("yxsc");//签到码有效时间

            //2.根据教学班,周数，星期几判断当堂课是否有签到正在进行。
            Set<String> strings = redisService.keysQdm(jxb,week,work_day);
            if (strings.size()!=0){
                //当前课程有签到正在进行，发起签到失败，返回false;
                return false;
            }

            // 3.将本堂课学生名单加载到缓存（redis）。
            // 3.1 获取该教学班的学生名单
            KbStuListData kbStuListData = componentService.getKbStuListData(jxb);
            List<Student> students = kbStuListData.getStudents();
            // 3.2 将该教学班学生名单加载到缓存（redis）中
            boolean isLoadList = redisService.loadStudentList(jxb, week, work_day, qdcs, students);

            // 4.将签到码加载进缓存（redis），并设置有效时间。
            boolean isloadQdm = redisService.loadQdmToCache(jxb, week, work_day, qdcs, qdm);

            // 5.名单加载，签到码加载同时成功。发起签到成功，返回true;
            if (isLoadList&&isloadQdm){
                //两个都成功
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            //日志
            logger.error("发起签到service发送未知错误！");
            return false;
        }

    }
}
