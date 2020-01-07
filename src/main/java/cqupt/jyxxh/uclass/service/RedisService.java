package cqupt.jyxxh.uclass.service;

import cqupt.jyxxh.uclass.pojo.KbStuListData;
import cqupt.jyxxh.uclass.pojo.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * @param number （number为学号或者教师号)
     * @param kebiao  课表实体，嵌套的json字符串
     * @return boolea
     */
    public boolean setKeBiao(String number,String kebiao){

        //操作redis的key
        String key="kebiao_"+number;

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
     * @param number （number为学号或者教师号)
     * @return 课表的json字符串数据，或者“false”
     */
    public String getKebiao(String number){

        //操作redis的key
        String key="kebiao_"+number;
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
     * @param xh 学号（xh是学号）
     * @param cjzc 成绩组成json字符串
     */
    public boolean setCjzc(String xh, String cjzc){

        // 操作redis的key
        String key="cjzc_"+xh;
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
     * @param xh （xh是学号）
     * @return 成绩组成json字符串或者“false”
     */
    public String getCjzc(String xh){

        // 操作redis的key
        String key="cjzc_"+xh;

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
     * @param jxb 教学班号
     * @param stuListData json字符串格式的数据。
     * @return boolean
     */
    public boolean setStuListData(String jxb,String stuListData){

        //操作redsi的key
        String key = "stulistdata_"+jxb;

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
     * @param jxb  教学班号
     * @return 学生数据的json字符串格式数据
     */
    public String getStuListData(String jxb) {

        //操作redsi的key
        String key = "stulistdata_"+jxb;
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
     * @param nowData 当前日期（nowData是当天的日期，yyyy-MM-dd）
     * @param schooleTime json格式数据
     * @return boolean
     */
    public boolean setSchoolTime(String nowData,String schooleTime){

        //操作redis的key
        String key ="schooltime_"+nowData;

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
     * @param nowData 当天日期（nowData是当天的日期，yyyy-MM-dd）
     * @return 教务时间的json数据
     */
    public String getSchoolTime(String nowData) {

        //操作redis的key
        String key ="schooltime_"+nowData;

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


    /**
     * 查询redis中指定签到码key的集合（签到码在redis默认的第一个数据库）
     *
     * @param jxb 教学班
     * @param week 周
     * @param work_day 星期几
     * @return 符合条件的key集合
     */
    public Set<String> keysQdm(String jxb, String week, String work_day) {

        //操作redis的key
        String key="(qdm)"+jxb+"<"+week+">("+work_day+")"+"*";//例：(qdm)A13191A2130440002<18>(2)*
        try {
            // 1. 获取一个redis连接
            Jedis jedisKeys = jedisPool.getResource();
            // 2. 执行查询操作
            Set<String> keys = jedisKeys.keys(key);
            // 3. 归还连接
            jedisKeys.close();
            // 4. 返回该结果集
            return keys;

        }catch (Exception e){
            logger.error("出现未知错误");
        }
        return null;
    }


    /**
     * 加载教学班的学生名单到缓存(redis第5个数据库)，用于课堂点名（签到）的。
     * 传进来的参数jxb,week,work_day,qdcs是用于做redis的key
     *
     * @param jxb 教学班
     * @param week 周数
     * @param work_day 星期几
     * @param qdcs 当堂课的第几次签到
     * @param students 学生们
     * @return boolean
     */
    public boolean loadStudentList(String jxb, String week, String work_day, String qdcs, List<Student> students) {
        try {
            //key的前缀( 教学班<周>(星期几)_签到次数: )
            String keyFirst=jxb+"<"+week+">("+work_day+")"+"_"+qdcs+":";//例：A13191A2130440002<18>(2)_1:

            // 1.获取redis连接
            Jedis jedisLoadStuList = jedisPool.getResource();
            // 2.选择第5个数据库
            jedisLoadStuList.select(4);
            // 3.循环加载list集合中的学生进缓存
            for (Student student:students){
                //这里将大量的信息都放在了key里面，方便后续使用。使用keys（）遍可以直接获取数据了。不用在各（）;
                String key=keyFirst+student.getXh()+"+"+student.getXm()+"=>"+student.getBj();//例：A13191A2130440002<18>(2)_1:2017214033+彭渝刚=>13001701
                String value=student.getXh();//例:2017214033
                jedisLoadStuList.set(key,value);
            }
            // 4.归还redis连接
            jedisLoadStuList.close();
            // 5.成功
            return true;
        }catch (Exception e){
            //日志
            logger.error("点名功能，加载学生名单进缓存失败！教学班：[{}]",jxb);
            return false;
        }
    }

    /**
     * 获取未签到学生名单（redis第5个数据库）
     * @param jxb 教学班
     * @param week 周数
     * @param work_day 星期几
     * @param qdcs 签到次数
     * @return Set 学生数据Set集合
     */
    public Set<String> getNotNoStus(String jxb, String week, String work_day, String qdcs) {
        //操作redis的key1。key1用于查询符合条件的key，也就是符合条件的学生数据的key。
        String keyFirst=jxb+"<"+week+">("+work_day+")"+"_"+qdcs+":*";//例：A13191A2130440002<18>(2)_1:*
        try {
            // 1.获取一个redis连接
            Jedis jedisGetNotOnStus = jedisPool.getResource();
            // 2.选择第五个数据库
            jedisGetNotOnStus.select(4);
            // 3.查询符合条件的学生key,key里面就包含了所需的必要数据。A13191A2130440002<18>(2)_1:2017214033+彭渝刚=>13001701
            Set<String> keys = jedisGetNotOnStus.keys(keyFirst);
            // 4.归还连接
            jedisGetNotOnStus.close();
            // 5.返回数据
            return keys;
        }catch (Exception e){
            //日志
            logger.error("获取未签到学生名单redisService出现未知错误！错误信息：[{}]",e.getMessage());
        }
        return null;
    }

    /**
     * 加载签到码到缓存（redis第一个数据库，默认）
     * @param jxb 教学班
     * @param week 周数
     * @param work_day 星期几
     * @param qdcs 签到次数，这是这堂课第几次签到
     * @param qdm 签到码
     * @return boolean
     */
    public boolean loadQdmToCache(String jxb, String week, String work_day, String qdcs, String qdm,String yxsj) {

        //操作redis的key
        String key="(qdm)"+jxb+"<"+week+">("+work_day+")"+"_"+qdcs;//例：(qdm)A13191A2130440002<18>(2)_1

        try {
            // 1.获取redis连接
            Jedis jedisLoadQdm = jedisPool.getResource();
            // 2.加载签到码到缓存
            jedisLoadQdm.set(key,qdm);
            // 3.设置签到码有效时间
            jedisLoadQdm.expire(key, Integer.parseInt(yxsj));
            // 4.归还连接
            jedisLoadQdm.close();
            // 5.返回true
            return true;
        }catch (Exception e){
            //日志
            logger.error("签到功能，加载签到码到缓存失败");
            return false;
        }
    }


    /**
     * 根据一卡通号获取代表用户登陆教务在线后的cookie（phpsessid）缓存
     * （redis第一个数据库，默认）
     * @param ykth 一卡通号
     * @return phpsessid或者“false”。
     */
    public String getPhpsessid(String ykth) {

        //操作redis的key
        String key="phpsessid_"+ykth;
        try {
            // 1.获取一个redis连接
            Jedis jedisGetPhpsessid = jedisPool.getResource();
            // 2.根据key判断有没有该数据
            Boolean exists = jedisGetPhpsessid.exists(key);
            if (!exists){
                //如果没有，返回”false“字符串
                return "false";
            }
            // 3.有就查询数据返回
            String s = jedisGetPhpsessid.get(key);
            // 4.归还连接
            jedisGetPhpsessid.close();
            // 5.返回数据
            return s;
        }catch (Exception e){
            //日志
            logger.error("获取登陆教务在线的phpsessid缓存出错！一卡通号：[{}]",ykth);
            return "false";
        }
    }

    /**
     * 将用户登陆教务在线后的cookie（PHPSESSID）放入缓存。有效时间25分钟
     * @param ykth 一卡通号
     * @param phpsessid phpsessid
     * @return boolean
     */
    public boolean setPhpsessid(String ykth, String phpsessid) {
        //操作redis的key
        String key="phpsessid_"+ykth;

        try {
            // 1.获取redis连接
            Jedis jedisSetphpsessid = jedisPool.getResource();
            // 2.添加进缓存
            jedisSetphpsessid.set(key,phpsessid);
            // 3.设置有效时间,25分钟。
            jedisSetphpsessid.expire(key,1500);
            // 4.归还连接
            jedisSetphpsessid.close();
            // 返回true；
            return true;
        }catch (Exception e){
            //日志
            logger.error("缓存操作出现未知错误");
            return false;
        }
    }


}
