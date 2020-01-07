package cqupt.jyxxh.uclass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.pojo.KbStuListData;
import cqupt.jyxxh.uclass.pojo.QianDaoResult;
import cqupt.jyxxh.uclass.pojo.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 签到功能service类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 1:18 2020/1/6
 */
@Service
public class QianDaoService {

    Logger logger = LoggerFactory.getLogger(QianDaoService.class);

    @Autowired
    private RedisService redisService;          //redis操作功能类

    @Autowired
    private ComponentService componentService;   //service层类，用于获取一些数据。


    /**
     * 教师发起签到
     *
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
            String yxsj = param.get("yxsj");//签到码有效时间

            //2.根据教学班,周数，星期几判断当堂课是否有签到正在进行。
            Set<String> strings = redisService.keysQdm(jxb, week, work_day);
            if (strings.size() != 0) {
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
            boolean isloadQdm = redisService.loadQdmToCache(jxb, week, work_day, qdcs, qdm, yxsj);

            // 5.名单加载，签到码加载同时成功。发起签到成功，返回true;
            //两个都成功
            return isLoadList && isloadQdm;
        } catch (Exception e) {
            //日志
            logger.error("发起签到service发送未知错误！");
            return false;
        }
    }

    /**
     * 教师获取某一次签到的结果
     *
     * @param jxb      教学班
     * @param week     上课周
     * @param work_day 星期几
     * @param qdcs     签到次数（针对这堂课，比如有点教师一堂课发起两次签到）
     * @return QianDaoResult
     */
    public QianDaoResult teaGetQianDaoResult(String jxb, String week, String work_day, String qdcs) {

        QianDaoResult qianDaoResult = new QianDaoResult();

        try {
            // 1.先获取本次点名未到的学生名单。（数据都在缓存里，redis第五个数据库）
            Set<String> notNoStus = redisService.getNotNoStus(jxb, week, work_day, qdcs);
            // 1.2.遍历notNoStus,获取学生具体信息
            Set<Map<String, String>> notOnTheStuList = new HashSet<>();
            for (String stu : notNoStus) {
                //stu="A13191A2130440002<18>(2)_1:2017214033+彭渝刚=>13001701"
                // map集合，装一个学生的数据
                Map<String, String> s = new HashMap<>();
                String xh = stu.substring(stu.indexOf(":") + 1, stu.indexOf("+"));
                String xm = stu.substring(stu.indexOf("+") + 1, stu.indexOf("=>"));
                String bj = stu.substring(stu.indexOf("=>") + 2);
                s.put("xh", xh);
                s.put("xm", xm);
                s.put("bj", bj);
                //将一个学的信息添加到Set集合
                notOnTheStuList.add(s);
            }

            // 2.获取未签到学生人数
            int notOnStu = notOnTheStuList.size();

            // 3.获取总人数
            // 3.1 通过教学班号，获取这节课的学生数据。
            String stuListData = redisService.getStuListData(jxb);
            // 3.2 将获取到的学生数据转为java对象，并获取名单总人数。
            int allStu = 0;
            ObjectMapper objectMapper = new ObjectMapper();
            KbStuListData kbStuListData = objectMapper.readValue(stuListData, KbStuListData.class);
            allStu = kbStuListData.getHeadcount();

            // 4.获取已签到人数
            int inStu = allStu - notOnStu;

            // 5.将数据添加到QianDaoResult对象中，返回。
            qianDaoResult.setAllStu(allStu);
            qianDaoResult.setInStu(inStu);
            qianDaoResult.setNotOnStu(notOnStu);
            qianDaoResult.setNotOnTheStuList(notOnTheStuList);
        } catch (JsonProcessingException e) {
            //日志
            logger.error("获取签到结果出现未知错误");
            e.printStackTrace();
        }

        return qianDaoResult;
    }
}
