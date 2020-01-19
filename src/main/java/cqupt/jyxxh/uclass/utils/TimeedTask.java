package cqupt.jyxxh.uclass.utils;

import cqupt.jyxxh.uclass.dao.QianDaoMapper;
import cqupt.jyxxh.uclass.service.QianDaoService;
import cqupt.jyxxh.uclass.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * 定时任务
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:14 2020/1/8
 */

@Component
public class TimeedTask {

    Logger logger= LoggerFactory.getLogger(TimeedTask.class);

    @Autowired
    private RedisService redisService;          //redis操作类

    @Autowired
    private QianDaoService qianDaoService;       //签到操作类

    @Autowired
    private QianDaoMapper qianDaoMapper;     //dao操作,签到结果



    /**
     * 每天12点30分，18点30分 22点30分。将点名数据持久化到mysql数据库
     */
    @Transactional(rollbackFor = {Exception.class})
    @Scheduled(cron = " * 20 * * * *")
    public void insertQiandaoDataToMysql(){

        /*System.out.println("定时任务");
        try {
            // 1.获取缓存中有的签到记录，就是签到id，但是这个签到id有前缀 "(qdjl)"。例：(qdjl)A13191A2130460001<1>(1)_1
            Set<String> allQdjl = redisService.getAllQdjl();
            // 1.1.通过这些签到id获取签到数据
            List<QianDaoResult> qianDaoResultList=new ArrayList<>();
            for (String str:allQdjl){
                //1.1.1 去掉前缀 "(qdjl)"
                String qdid = str.substring(str.indexOf("(qdjl)") + 6);
                //1.1.2根据签到id获取签到记录数据
                QianDaoResult qianDaoResultOneTimes = qianDaoService.getQianDaoResultOneTimes(qdid);
                //1.1.3将签到记录数据插入到List集合
                qianDaoResultList.add(qianDaoResultOneTimes);
            }
            //1.2将签到记录数据存储到mysql数据库。如果有数据，想数据库插入。
            if (!(0==qianDaoResultList.size())){
                qianDaoMapper.insertQiandaoResult(qianDaoResultList);
            }

            //2.获取缓存中所有的学生数据。现在缓存中还有的学生就是有有问题的学生（迟到|缺勤|请假）
            List<ClassStudentInfo> allNoQdStu = redisService.getAllNoQdStu();
            //2.1将获取到的有记录的学生数据存储到mysql数据库。如果有数据，想数据库插入。
            if (!(0==allNoQdStu.size())){
                qianDaoMapper.insertClassStuInfo(allNoQdStu);
            }

            //3.清空redis第5个数据库，存放签到数据的缓存。
            redisService.flushDB(4);

            //日志
            if (logger.isInfoEnabled()){
                logger.info("定时任务，将签到数据持久化到mysql。有签到记录[{}]条，有记录的学生数据[{}]条",qianDaoResultList.size(),allNoQdStu.size());
            }
        }catch (Exception e){
            //日志
            logger.error("定时任务，将签到数据持久化mysql失败，可能原因是数据冲突。具体错误信息：[{}]",e.getMessage());
        }*/
    }
}
