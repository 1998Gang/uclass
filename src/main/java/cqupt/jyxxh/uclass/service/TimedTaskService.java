package cqupt.jyxxh.uclass.service;

import checkers.units.quals.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

/**
 * 定时任务
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 18:52 2020/2/13
 */

@EnableScheduling
public class TimedTaskService {

    @Autowired
    private QianDaoService qianDaoService;  //签到功能实现类

    @Autowired
    private TiWenService tiWenService;    //课堂提问功能实现类


    /**
     * 每天定时缓存中的签到数据，持久化到mysql数据库
     * 12：30
     * 18：30
     * 23：30
     * 三个时间
     */
    @Scheduled(cron = "0 30 12,18,23 * *")
    public void qdData(){
        qianDaoService.persistentQianDaoData();
    }

    /**
     * 每天定时将缓冲中的提问数据，持久化到mysql数据库
     * 每天23：00
     */
    @Scheduled(cron = "0 0 23 * *")
    public void twData(){
        tiWenService.persistentTiWenData();
    }

}
