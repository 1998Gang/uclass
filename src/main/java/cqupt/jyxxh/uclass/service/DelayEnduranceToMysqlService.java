package cqupt.jyxxh.uclass.service;


import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 23:03 2020/3/24
 *
 * 将课堂签到、提问产生的缓存数据持久化到mysql数据库
 */
@Service
public class DelayEnduranceToMysqlService implements ServletContextListener {


    /**
     * 延时队列要执行的延时操作都装这个里面。
     */
    private static DelayQueue<EnduranceToMysql> delayQueue=new DelayQueue<>();

    /**
     * 获取该队列
     * @return DelayQueue<EnduranceToMysql>
     */
    public static DelayQueue<EnduranceToMysql> getDelayQueue() {
        return delayQueue;
    }


    /**
     * 监听ServletContext对象的创建，也就是项目一启动，就调用该方法。
     *
     * 该方法会显示的创建一个线程池，因为该方法是在项目启动之初就会执行的，所以spring的配置还没有加载完成，只能在这里显示的创建线程池。
     *
     * 该方法的作用为，将延时队列里的任务取出，并用该方法内部定义的线程池中的线程去执行。
     *
     * 该方法内部为死循环，但使用DelayQueue类的take()方法，实现阻塞。
     * @param sce 1
     */
    @Override
    public  void contextInitialized(ServletContextEvent sce) {
        /*创建一个线程池*/
        ThreadPoolTaskExecutor threadPoolTaskExecutor=new ThreadPoolTaskExecutor();
        //设置核心线程数，为4
        threadPoolTaskExecutor.setCorePoolSize(4);
        //设置最大线程数 为8
        threadPoolTaskExecutor.setMaxPoolSize(4);
        //设置线程池中阻塞队列的长度
        threadPoolTaskExecutor.setQueueCapacity(4);
        //设置线程的存活时间,20秒
        threadPoolTaskExecutor.setKeepAliveSeconds(20);
        //设置线程池的拒绝策略,该任务被线程池拒绝，由调用 `execute()`方法的线程执行该任务。
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //设置线程池的头名字
        threadPoolTaskExecutor.setThreadNamePrefix("Thread_Endurance_To_Mysql");
        //初始化该线程池
        threadPoolTaskExecutor.initialize();

        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    EnduranceToMysql take = null;
                    try {
                        take = delayQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    EnduranceToMysql finalTake = take;
                    threadPoolTaskExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            assert finalTake != null;
                            finalTake.endurance();
                        }
                    });
                }
            }
        });
    }


    /**
     * 持久化缓存数据到数据库的接口
     */
    public interface EnduranceToMysql extends Delayed{
        /**
         * 延时任务执行的主体
         */
        void endurance();
    }
}
