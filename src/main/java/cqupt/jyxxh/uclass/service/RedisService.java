package cqupt.jyxxh.uclass.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis操作类
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 19:43 2020/1/4
 */

@Service
public class RedisService {

    Logger logger= LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private JedisPool jedisPool;  //redis连接池


    /**
     * 向redis中存储课表（课表在第二个数据库）
     * @param key key（格式：kebiao_number,number为学号或者教师号)
     * @param kebiao 课表实体，嵌套的json字符串
     * @return boolea
     */
    public boolean setKeBiao(String key,String kebiao){
        try {
            // 1.获取一个redis连接
            Jedis jedisKebiao = jedisPool.getResource();
            // 2.选择第二个reids数据库
            jedisKebiao.select(1);
            // 3.执行存储操作
            jedisKebiao.set(key,kebiao);
            // 4.设置有效时间 20小时
            jedisKebiao.expire(key,72000);
            // 5.返回连接
            jedisKebiao.close();
        }catch (Exception e){
            //日志
            logger.error("添加课表缓存出错！key:[{}]",key);
            return false;
        }
        return true;
    }

    /**
     * 从缓存获取课表（第二个reids数据库）
     * 返回false说明数据获取失败，（1.缓存中没有该数据。2.出现未知错误。）
     * @param key key（格式：kebiao_number,number为学号或者教师号)
     * @return 课表的json字符串数据，或者“false”
     */
    public String getKebiao(String key){
        try{
            // 1.获取一个redis连接
            Jedis jedisGetKebiao = jedisPool.getResource();
            // 2.选择第二个redis数据库
            jedisGetKebiao.select(1);
            // 3.判断缓存中是否有该key的数据
            Boolean exists = jedisGetKebiao.exists(key);
            if (!exists){
                //缓存中没有，返回false，获取失败。
                jedisGetKebiao.close();
                return "false";
            }
            // 4.缓存中有获取并返回数据
            String jsonKebiao = jedisGetKebiao.get(key);
            // 5.归还redis连接
            jedisGetKebiao.close();

            return jsonKebiao;
        }catch (Exception e){
            //日志
            logger.error("从缓存获取课表数据数据出现未知错误！key：[{}]",key);
            return "false";
        }
    }

    /**
     * 添加成绩组成到缓存（redis第3个数据库）
     * @param key key（格式：cjzc_number，number是学号）
     * @param cjzc 成绩组成json字符串
     */
    public boolean setCjzc(String key, String cjzc){
        try{
            // 1.获取一个redis连接
            Jedis jedisCjzc = jedisPool.getResource();
            // 2.选择第3个数据库
            jedisCjzc.select(2);
            // 3.将成绩组成添加进redis缓存
            jedisCjzc.set(key, cjzc);
            // 4.设置有效时间,25天。
            jedisCjzc.expire(key,2160000);
            // 5.归还连接
            jedisCjzc.close();

        }catch (Exception e){
            //日志
            logger.error("添加成绩组成缓存出现未知错误！key：[{}]",key);
            return false;
        }
        return true;
    }

    /**
     * 从缓存获取成绩组成（redis第3个数据库）
     * 返回false说明数据获取失败，（1.缓存中没有该数据。2.出现未知错误。）
     * @param key key格式：cjzc_number，number是学号）
     * @return 成绩组成json字符串或者“false”
     */
    public String getCjzc(String key){
        try{
            // 1.获取redis连接
            Jedis jedisGetcjzc = jedisPool.getResource();
            // 2.选择第三个数据库
            jedisGetcjzc.select(2);
            // 3.判断缓存中有没有该key的数据
            Boolean exists = jedisGetcjzc.exists(key);
            if (!exists){
                //缓存中没有，返回“false”
                //归还连接
                jedisGetcjzc.close();
                return "false";
            }
            // 4.缓存中有
            String s = jedisGetcjzc.get(key);
            // 5.归还连接
            jedisGetcjzc.close();
            return s;
        }catch (Exception e){
            //日志
            logger.error("从缓存获取成绩组成数据出现未知错误！key:[{}]",key);
            return "false";
        }
    }

    /**
     * 添加课堂学生名单数据到缓存（redis第4个数据库）
     *
     * @param key key（格式："stulistdata_”+jxb，jxb是教学班号）
     * @param stuListData json字符串格式的数据。
     * @return boolean
     */
    public boolean setStuListData(String key,String stuListData){
        try {
            // 1.获取redis连接
            Jedis jedisSetlistData = jedisPool.getResource();
            // 2.选择第4个数据库
            jedisSetlistData.select(3);
            // 3.将数据添加进redis
            jedisSetlistData.set(key,stuListData);
            // 4.设置数据有效时间，30天
            jedisSetlistData.expire(key,2592000);
            // 5.归还redis连接
            jedisSetlistData.close();

            return true;
        }catch (Exception e){
            //日志
            logger.error("添加课堂学生名单出错！key:[{}]",key);
            return false;
        }
    }

    /**
     * 从缓存获取上课学生数据（redis第4个数据库）
     * 返回false说明数据获取失败，（1.缓存中没有该数据。2.出现未知错误。）
     * @param key key （格式："stulistdata_”+jxb，jxb是教学班号）
     * @return 学生数据的json字符串格式数据
     */
    public String getStuListData(String key) {
        try{
            // 1.获取jedis连接
            Jedis jedisGetStuListData = jedisPool.getResource();
            // 2.选择redis第4个数据库
            jedisGetStuListData.select(3);
            // 3.判断redis中有没有该key的数据
            Boolean exists = jedisGetStuListData.exists(key);
            if (!exists){
                //redis中没有该key数据，返回“fasle”。
                //归还连接
                jedisGetStuListData.close();
                return "false";
            }
            // 4.redis中有，查询并返回。
            String s = jedisGetStuListData.get(key);
            // 5.归还redis连接
            jedisGetStuListData.close();

            return s;
        }catch (Exception e){
            //日志
            logger.error("从缓存获取学生名单数据出现未知错误！key:[{}]",key);
            return "false";
        }
    }

    /**
     * 设置教务时间缓存（redis第一个数据库，默认）
     * @param key key（格式："schooltime_"+nowData，nowData是当天的日期，yyyy-MM-dd）
     * @param schooleTime json格式数据
     * @return boolean
     */
    public boolean setSchoolTime(String key,String schooleTime){
        try {
            // 1.获取redis连接，不选择数据库，使用默认的第一个数据库。
            Jedis jediSetSchoolTime = jedisPool.getResource();
            // 2.添加数据
            jediSetSchoolTime.set(key,schooleTime);
            // 3.设置有效时间为24小时
            jediSetSchoolTime.expire(key,86400);
            // 4.归还redis连接
            jediSetSchoolTime.close();

            return true;
        }catch (Exception e){
            //日志
            logger.error("添加教务时间缓存出错！key:[{}]",key);
            return false;
        }
    }


    /**
     * 获取教务时间（redis第1个数据库，默认）
     * @param key key（格式："schooltime_"+nowData，nowData是当天的日期，yyyy-MM-dd）
     * @return 教务时间的json数据
     */
    public String getSchoolTime(String key) {
        try {
            //1.获取redis连接,不选择数据库，使用默认的第一个数据库。
            Jedis jedsiGetSchoooltime = jedisPool.getResource();
            //2.判断该key的值是否存在
            Boolean exists = jedsiGetSchoooltime.exists(key);
            if (!exists){
                //不存在
                //归还redis连接
                jedsiGetSchoooltime.close();
                return "false";
            }
            //3.存在，根据key获取时间
            String s = jedsiGetSchoooltime.get(key);
            //4.归还redis连接
            jedsiGetSchoooltime.close();
            //5.返回
            return s;
        }catch (Exception e){
            //日志
            logger.error("从缓存获取教务时间数据出现未知错误！key:[{}]",key);
            return "false";
        }
    }
}
