package cqupt.jyxxh.uclass.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


/**
 * 定时任务
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 18:52 2020/2/13
 */


@Service
@EnableScheduling
public class TimedTaskService {

    /**
     * 日志
     */
    Logger logger= LoggerFactory.getLogger(TimedTaskService.class);

    /**
     * redis操作对象
     */
    @Autowired
    private RedisService redisService;


    /**
     * 每天定时清空缓存中的签到数据 以及提问数据
     * 签到相关缓存在redis第5个数据库
     * 提问相关缓存在redis第7个数据库
     * 23：30
     */
    @Scheduled(cron = "0 30 23 * * *")
    public void qdData(){
        try {
            redisService.flushDB(4);
            redisService.flushDB(6);
            if (logger.isInfoEnabled()){
                logger.info("定时任务成功清除redis第5个（签到相关）、第7个（提问相关）数据库！");
            }
        }catch (Exception e){
            logger.error("调用定时任务[persistentQianDaoData()]失败！");
        }

    }
}
