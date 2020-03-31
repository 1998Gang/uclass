package cqupt.jyxxh.uclass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.pojo.ClassStuInfo;
import cqupt.jyxxh.uclass.pojo.qiandao.KeChengQianDaoHistory;
import cqupt.jyxxh.uclass.pojo.qiandao.StuQianDaoHistory;
import cqupt.jyxxh.uclass.pojo.tiwen.AnswerData;
import cqupt.jyxxh.uclass.pojo.tiwen.KeChengTiWenHistory;
import cqupt.jyxxh.uclass.pojo.tiwen.StuTiWenHistory;
import cqupt.jyxxh.uclass.pojo.tiwen.WTZT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * redis操作类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 19:43 2020/1/4
 */

@Service
public class RedisService {

    Logger logger = LoggerFactory.getLogger(RedisService.class);

    /**
     * redis连接池
     */
    @Autowired
    private  JedisPool jedisPool;


    /**
     * 向redis中存储课表（课表在第二个数据库）
     *
     * @param number （number为学号或者教师号)
     * @param kebiao 课表实体，嵌套的json字符串
     * @return boolea
     */
    public boolean setKeBiao(String number, String kebiao) {

        //操作redis的key
        String key = "(kebiao)" + number;

        try {
            // 1.获取一个redis连接
            Jedis jedisKebiao = jedisPool.getResource();
            // 2.选择第二个reids数据库
            jedisKebiao.select(1);
            // 3.执行存储操作
            jedisKebiao.set(key, kebiao);
            // 4.设置有效时间 6小时
            jedisKebiao.expire(key, 21600);
            // 5.返回连接
            jedisKebiao.close();
        } catch (Exception e) {
            //日志
            logger.error("添加课表缓存出错！key:[{}]", key);
            return false;
        }
        return true;
    }

    /**
     * 从缓存获取课表（第二个reids数据库）
     * 返回false说明数据获取失败，（1.缓存中没有该数据。2.出现未知错误。）
     *
     * @param number （number为学号或者教师号)
     * @return 课表的json字符串数据，或者“false”
     */
    public String getKebiao(String number) {

        //操作redis的key
        String key = "(kebiao)" + number;
        try {
            // 1.获取一个redis连接
            Jedis jedisGetKebiao = jedisPool.getResource();
            // 2.选择第二个redis数据库
            jedisGetKebiao.select(1);
            // 3.判断缓存中是否有该key的数据
            Boolean exists = jedisGetKebiao.exists(key);
            if (!exists) {
                //缓存中没有，返回false，获取失败。
                jedisGetKebiao.close();
                return "false";
            }
            // 4.缓存中有获取并返回数据
            String jsonKebiao = jedisGetKebiao.get(key);
            // 5.归还redis连接
            jedisGetKebiao.close();

            return jsonKebiao;
        } catch (Exception e) {
            //日志
            logger.error("从缓存获取课表数据数据出现未知错误！key：[{}]", key);
            return "false";
        }
    }


    /**
     * 添加成绩组成到缓存（redis第3个数据库）
     *
     * @param xh   学号（xh是学号）
     * @param cjzc 成绩组成json字符串
     */
    public boolean setCjzc(String xh, String cjzc) {

        // 操作redis的key
        String key = "(cjzc)" + xh;
        try {
            // 1.获取一个redis连接
            Jedis jedisCjzc = jedisPool.getResource();
            // 2.选择第3个数据库
            jedisCjzc.select(2);
            // 3.将成绩组成添加进redis缓存
            jedisCjzc.set(key, cjzc);
            // 4.设置有效时间,25天。
            jedisCjzc.expire(key, 2160000);
            // 5.归还连接
            jedisCjzc.close();

        } catch (Exception e) {
            //日志
            logger.error("添加成绩组成缓存出现未知错误！key：[{}]", key);
            return false;
        }
        return true;
    }

    /**
     * 从缓存获取成绩组成（redis第3个数据库）
     * 返回false说明数据获取失败，（1.缓存中没有该数据。2.出现未知错误。）
     *
     * @param xh （xh是学号）
     * @return 成绩组成json字符串或者“false”
     */
    public String getCjzc(String xh) {

        // 操作redis的key
        String key = "(cjzc)" + xh;


        // 1.获取redis连接
        Jedis jedisGetcjzc = jedisPool.getResource();
        // 2.选择第三个数据库
        jedisGetcjzc.select(2);
        // 3.获取数据
        String s = jedisGetcjzc.get(key);
        if ("NULL".equals(s) || null == s) {
            return null;
        }
        // 4.归还连接
        jedisGetcjzc.close();
        return s;

    }


    /**
     * 添加课堂学生名单数据到缓存（redis第4个数据库）
     *
     * @param jxb         教学班号
     * @param classStuList json字符串格式的数据。
     * @return boolean
     */
    public boolean setClassStuList(String jxb, String classStuList) {

        //操作redsi的key
        String key = "classstulist_" + jxb;


        // 1.获取redis连接
        Jedis jedisSetlistData = jedisPool.getResource();
        // 2.选择第4个数据库
        jedisSetlistData.select(3);
        // 3.将数据添加进redis
        jedisSetlistData.set(key, classStuList);
        // 4.设置数据有效时间，3天
        jedisSetlistData.expire(key, 259200);
        // 5.归还redis连接
        jedisSetlistData.close();

        return true;

    }

    /**
     * 从缓存获取课堂学生数据（redis第4个数据库）
     * 返回false说明数据获取失败，（1.缓存中没有该数据。2.出现未知错误。）
     *
     * @param jxb 教学班号
     * @return 学生数据的json字符串格式数据
     */
    public String getClassStuList(String jxb) {

        //操作redsi的key
        String key = "classstulist_" + jxb;

        // 1.获取jedis连接
        Jedis jedisGetStuListData = jedisPool.getResource();
        // 2.选择redis第4个数据库
        jedisGetStuListData.select(3);
        // 3.判断redis中有没有该key的数据
        Boolean exists = jedisGetStuListData.exists(key);
        if (!exists) {
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
    }


    /**
     * 设置教务时间缓存（redis第一个数据库，默认）
     *
     * @param nowData     当前日期（nowData是当天的日期，yyyy-MM-dd）
     * @param schooleTime json格式数据
     * @return boolean
     */
    public boolean setSchoolTime(String nowData, String schooleTime) {

        //操作redis的key
        String key = "(schooltime)" + nowData;


        // 1.获取redis连接，不选择数据库，使用默认的第一个数据库。
        Jedis jediSetSchoolTime = jedisPool.getResource();
        // 2.添加数据
        jediSetSchoolTime.set(key, schooleTime);
        // 3.设置有效时间为24小时
        jediSetSchoolTime.expire(key, 86400);
        // 4.归还redis连接
        jediSetSchoolTime.close();

        return true;

    }

    /**
     * 获取教务时间（redis第1个数据库，默认）
     * key格式  schooltime_yyyy-MM-dd
     *
     * @param nowData 当天日期（nowData是当天的日期，yyyy-MM-dd）
     * @return 教务时间的json数据
     */
    public String getSchoolTime(String nowData) {

        //操作redis的key
        String key = "(schooltime)" + nowData;

        try {
            //1.获取redis连接,不选择数据库，使用默认的第一个数据库。
            Jedis jedsiGetSchoooltime = jedisPool.getResource();
            //2.判断该key的值是否存在
            Boolean exists = jedsiGetSchoooltime.exists(key);
            if (!exists) {
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
        } catch (Exception e) {
            //日志
            logger.error("从缓存获取教务时间数据出现未知错误！key:[{}]", key);
            return "false";
        }
    }


    /**
     * 根据一卡通号获取代表用户登陆教务在线后的cookie（phpsessid）缓存
     * （redis第一个数据库，默认）
     *
     * @param ykth 一卡通号
     * @return phpsessid或者“false”。
     */
    public String getPhpsessid(String ykth) {

        //操作redis的key
        String key = "(phpsessid)" + ykth;
        try {
            // 1.获取一个redis连接
            Jedis jedisGetPhpsessid = jedisPool.getResource();
            // 2.根据key判断有没有该数据
            Boolean exists = jedisGetPhpsessid.exists(key);
            if (!exists) {
                //如果没有，返回”false“字符串
                return "false";
            }
            // 3.有就查询数据返回
            String s = jedisGetPhpsessid.get(key);
            // 4.归还连接
            jedisGetPhpsessid.close();
            // 5.返回数据
            return s;
        } catch (Exception e) {
            //日志
            logger.error("获取登陆教务在线的phpsessid缓存出错！一卡通号：[{}]", ykth);
            return "false";
        }
    }

    /**
     * 将用户登陆教务在线后的cookie（PHPSESSID）放入缓存。有效时间25分钟
     *
     * @param ykth      一卡通号
     * @param phpsessid phpsessid
     * @return boolean
     */
    public boolean setPhpsessid(String ykth, String phpsessid) {
        //操作redis的key
        String key = "(phpsessid)" + ykth;

        try {
            // 1.获取redis连接
            Jedis jedisSetphpsessid = jedisPool.getResource();
            // 2.添加进缓存
            jedisSetphpsessid.set(key, phpsessid);
            // 3.设置有效时间,25分钟。
            jedisSetphpsessid.expire(key, 1500);
            // 4.归还连接
            jedisSetphpsessid.close();
            // 返回true；
            return true;
        } catch (Exception e) {
            //日志
            logger.error("缓存操作出现未知错误");
            return false;
        }
    }


    /**
     * 根据具体签到时间匹配现有的签到码的key（redis第5个数据库）
     * <p>
     * 用于判断同一堂课是否有签到正在进行
     *
     * @param jxb      教学班
     * @param week     周
     * @param work_day 星期几
     * @return 符合条件的key集合
     */
    public Set<String> keysQdm(String jxb, String week, String work_day) {

        //操作redis的key
        String key = "(qdm)QD-" + jxb + "<" + week + ">(" + work_day + ")" + "*";//例：(qdm)QD-A13191A2130440002<18>(2)*
        try {
            // 1. 获取一个redis连接
            Jedis jedisKeys = jedisPool.getResource();
            // 2.选择第5个数据库
            jedisKeys.select(4);
            // 3. 执行查询操作
            Set<String> keys = jedisKeys.keys(key);
            // 4. 归还连接
            jedisKeys.close();
            // 5. 返回该结果集
            return keys;

        } catch (Exception e) {
            logger.error("根据week，jxb,work_day匹配签到码出现未知错误");
        }
        return null;
    }

    /**
     * 根据签到id获取签到码(redis第5个数据库)
     *
     * @param qdid 签到id
     * @return String 签到码 如果出现错误，返回"fasle"
     */
    public String getQdm(String qdid) {

        //操作redis的key
        String key = "(qdm)" + qdid;
        try {
            // 1.获取redis连接
            Jedis jedisGetQdm = jedisPool.getResource();
            // 2.选择第5个数据库
            jedisGetQdm.select(4);
            // 3.获取value,qdm_yxsj。前为签到码，后为这次签到的有效时间
            String value = jedisGetQdm.get(key);
            // 4.解析value
            String qdm = value.substring(0, value.indexOf("_"));
            // 5.归还连接
            jedisGetQdm.close();
            // 6.返回数据
            return qdm;
        } catch (Exception e) {
            //日志
            logger.error("获取签到码出现未知错误！错误信息：[{}]", e.getMessage());
            return "false";
        }
    }

    /**
     * 根据签到id获取签到的设置的有效时间(redis第5个数据库)
     *
     * @param qdid 签到id
     * @return String 有效时间如果出现错误，返回"fasle"
     */
    public String getYxsj(String qdid) {

        //操作redis的key
        String key = "(qdm)" + qdid;
        try {
            // 1.获取redis连接
            Jedis jedisGetYxsj = jedisPool.getResource();
            // 2.选择第5个数据库
            jedisGetYxsj.select(4);
            // 3.获取value,qdm_yxsj。前为签到码，后为这次签到的有效时间
            String value = jedisGetYxsj.get(key);
            // 4.解析value
            String yxsj = value.substring(value.indexOf("_") + 1);
            // 5.归还连接
            jedisGetYxsj.close();
            // 6.返回数据
            return yxsj;
        } catch (Exception e) {
            //日志
            logger.error("获取签到设置的有效时间出现未知错误！错误信息：[{}]", e.getMessage());
            return "false";
        }
    }

    /**
     * 加载教学班的学生名单到缓存(redis第5个数据库)，用于课堂点名（签到）的。
     * 传进来的参数jxb,week,work_day,qdcs是用于做redis的key
     * <p>
     * key:(qdstu)QD-A13191A2130440002<18>(2)_1:2017214033
     * value:由classStudenInfo对象转换的的json字符串
     *
     * @param jxb      教学班
     * @param week     周数
     * @param work_day 星期几
     * @param qdcs     当堂课的第几次签到
     * @param classStuInfoList 学生们
     * @return boolean
     */
    public boolean loadQDStuList(String jxb, String week, String work_day, String qdcs, List<ClassStuInfo> classStuInfoList) {
        try {
            //代表该值是学生信息的key的前缀
            String keyFirst = "(qdstu)";//例：(qdstu)
            // 签到id（qdid）：QD-教学班<周>(星期几)_签到次数
            String qdid = "QD-" + jxb + "<" + week + ">(" + work_day + ")" + "_" + qdcs;//例：QD-A13191A2130440002<18>(2)_1
            // 1.获取redis连接
            Jedis jedisLoadStuList = jedisPool.getResource();
            // 2.选择第5个数据库
            jedisLoadStuList.select(4);
            // 3.循环加载list集合中的学生进缓存。
            ObjectMapper objectMapper = new ObjectMapper();
            for (ClassStuInfo classStudent : classStuInfoList) {
                //key为这次点名的唯一识别加学生学号。
                String key = keyFirst + qdid + ":" + classStudent.getXh();//例：(qdstu)QD-A13191A2130440002<18>(2)_1:2017214033
                //设置签到状态为"QQ"(缺勤)
                classStudent.setQdzt("QQ");
                //设置签到id
                classStudent.setQdid(qdid);
                //把ClassStudentInfo对象转为json字符串，作为value。
                jedisLoadStuList.set(key, objectMapper.writeValueAsString(classStudent));
            }
            // 4.归还redis连接
            jedisLoadStuList.close();
            // 5.成功
            return true;
        } catch (Exception e) {
            //日志
            logger.error("点名功能，加载学生名单进缓存失败！教学班：[{}]", jxb);
            return false;
        }
    }

    /**
     * 获取本次签到有记录的学生名单（包含未签到，迟到，请假）（redis第5个数据库）
     *
     * @param qdid 签到id 一次签到的唯一识别码
     * @return List<ClassInfo></> 学生数据Set集合
     */
    public List<ClassStuInfo> getThisTimeNotOnStus(String qdid) {
        //操作redis的key。key用于查询符合条件的key，也就是指定签到的所有学生记录。
        //例：(qdstu)A13191A2130440002<18>(2)_1:*
        String keyFirst = "(qdstu)" + qdid + ":*";

        //jakson的操作对象
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            // 1.获取一个redis连接
            Jedis jedisGetNotOnStus = jedisPool.getResource();
            // 2.选择第五个数据库
            jedisGetNotOnStus.select(4);
            // 3.查询符合条件的key
            Set<String> keys = jedisGetNotOnStus.keys(keyFirst);
            // 4.根据keys获取数据
            //迭代器
            Iterator<String> iterator = keys.iterator();
            List<ClassStuInfo> stuInfoList=new ArrayList<>(16);

            for (int i = 1; i <= keys.size(); i++) {
                String s = jedisGetNotOnStus.get(iterator.next());
                ClassStuInfo classStuInfo = objectMapper.readValue(s, ClassStuInfo.class);
                stuInfoList.add(classStuInfo);
            }
            // 4.归还连接
            jedisGetNotOnStus.close();
            // 5.返回数据
            return stuInfoList;
        } catch (Exception e) {
            //日志
            logger.error("获取未签到学生名单redisService出现未知错误！错误信息：[{}]", e.getMessage());
        }
        return null;
    }

    /**
     * 教师修改学生签到记录状态(redis第5个数据库)
     * <p>
     * 1.如果补签类型是CD（迟到），教师就将缓存中对应学生数据的value修改为"CD".
     * 2.如果补签类型的QJ(请假)，教师奖缓存中学生数据的value修改为"QJ".
     * 3.如果补签类型是WT(网络问题)，表示学生可能因为网络或者其他问题，没有签到成功，但是人在！教师删除对应学生数据，表示该学生成功签到！
     *
     * @param qdid 签到id
     * @param xh   学号
     * @param bqlx 补签类型
     * @return boolean
     */
    public boolean alterStuQiandaoStatus(String qdid, String xh, String bqlx) {

        //操作redis的key
        String key = "(qdstu)" + qdid + ":" + xh; //例：(qdstu)A13191A2130440002<18>(2)_1:2017214033

        try {
            // 1.获取redis连接
            Jedis jedisDel = jedisPool.getResource();
            // 2.选择第5个数据库
            jedisDel.select(4);
            //3.根据不同的类型执行不同操作
            switch (bqlx) {
                case "WT": {
                    //网络或者其他问题，删除记录
                    jedisDel.del(key);
                    break;
                }
                case "CD":
                case "QJ": {
                    //请假，或者 迟到。
                    //获取原有记录
                    String s = jedisDel.get(key);
                    //更改记录
                    ObjectMapper objectMapper = new ObjectMapper();
                    ClassStuInfo classStuInfo = objectMapper.readValue(s, ClassStuInfo.class);
                    classStuInfo.setQdzt(bqlx);
                    String s1 = objectMapper.writeValueAsString(classStuInfo);
                    jedisDel.set(key, s1);
                    break;
                }
                default:{
                    break;
                }
            }
            // 4.归还连接
            jedisDel.close();
            // 5.返回true
            return true;
        } catch (Exception e) {
            //日志
            logger.error("修改学生签到状态出现未知错误！错误信息：[{}]", e.getMessage());
            return false;
        }
    }

    /**
     * 加载签到码和签到记录到缓存，（redis第5个数据库）
     * <p>
     * 【签到码】
     * key：例(qdm)QD-A13191A2130440002<18>(2)_1
     * value：签到码与本次签到的有效时间（qdm_yxsj）
     * 【签到记录】
     * key：例 (qdjl)QD-A13191A2130440002<18>(2)_1
     * value：当前时间，年月日（yy-MM-dd）
     *
     * @param jxb      教学班
     * @param week     周数
     * @param work_day 星期几
     * @param qdcs     签到次数，这是这堂课第几次签到
     * @param qdm      签到码
     * @param yxsj     本次签到的有效时间
     * @return 签到id，或者”false“
     */
    public String loadQdmToCache(String jxb, String week, String work_day, String qdcs, String qdm, long yxsj) {

        //操作redis的key（用于添加签到码）
        String qdid = "QD-" + jxb + "<" + week + ">(" + work_day + ")" + "_" + qdcs;
        /*例：(qdm)QD-A13191A2130440002<18>(2)_1*/
        String keyQdm = "(qdm)" + qdid;
        /*例：(qdjl)QD-A13191A2130440002<18>(2)_1*/
        String keyQdjl = "(qdjl)" + qdid;

        try {
            // 1.获取redis连接
            Jedis jedisLoadQdm = jedisPool.getResource();
            // 2.选择第5个数据库
            jedisLoadQdm.select(4);
            // 2.加载签到码到缓存，同时把签到的有效时间也作为value。例：签到码为：1234 有效时间为120秒。value为 1234_120
            String value = qdm + "_" + yxsj;
            jedisLoadQdm.set(keyQdm, value);
            // 3.设置该签到码有效时间
            jedisLoadQdm.expire(keyQdm, Integer.parseInt(String.valueOf(yxsj)));
            // 4.加载签到记录到缓存,value为当前时间
            Date date = new Date();
            String sdate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            jedisLoadQdm.set(keyQdjl, sdate);
            // 5.归还连接
            jedisLoadQdm.close();
            // 5.返回true
            return qdid;
        } catch (Exception e) {
            //日志
            logger.error("签到功能，加载签到码到缓存失败");
            return "false";
        }
    }


    /**
     * 删除学生签到缓存记录数据（redis第5个数据库）
     *
     * @param qdid 签到id
     * @param xh   学号
     * @return boolean
     */
    public boolean delQdStu(String qdid, String xh) {
        //操作redis的key
        String key = "(qdstu)" + qdid + ":" + xh;
        try {
            // 1.获取redis连接
            Jedis jedisDelStu = jedisPool.getResource();
            // 2.选择第5个数据库
            jedisDelStu.select(4);
            // 3.删除记录
            Long del = jedisDelStu.del(key);
            // 4.归还连接
            jedisDelStu.close();
            // 5.如果删除操作的返回结果为1，表示删除成功！
            return del == 1;

        } catch (Exception e) {
            //日志
            logger.error("删除学生签到缓存记录出现未知错误！错误信息：[{}]", e.getMessage());
            return false;
        }
    }

    /**
     * 获取缓存中保存的所有签到记录。(redis第5个数据库)
     *
     * @return set<String></>
     */
    public Set<String> getAllQdjl() {

        // 操作redis的key
        String key = "(qdjl)*";
        try {
            //1.获取redis连接
            Jedis jedisGetqdjl = jedisPool.getResource();
            //2.选择redis数据库
            jedisGetqdjl.select(4);
            //3.获取所有以 “(qdjl)” 开头的key。就是签到记录
            Set<String> keys = jedisGetqdjl.keys(key);
            //4.归还连接
            jedisGetqdjl.close();
            //5.返回
            return keys;
        } catch (Exception e) {
            //日志
            logger.error("redisservice获取签到记录失败");
        }
        return null;
    }

    /**
     * 获取缓存中所有有记录的学生信息（保存迟到|请假|缺勤）
     *
     * @return List集合，元素的ClassStudentInfo,如果出现错误，返回null;
     */
    public List<ClassStuInfo> getAllNoQdStu() {
        //操作redis的key
        String key = "(qdstu)*";
        //操作json字符串的对象
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //1.获取redis连接
            Jedis jedisGetAllStuKeys = jedisPool.getResource();
            //2.选择di5个数据库里
            jedisGetAllStuKeys.select(4);
            //3.获取所有学生的key
            Set<String> keys = jedisGetAllStuKeys.keys(key);
            //4.通过keys获取学生数据
            Iterator<String> iterator = keys.iterator();//迭代器
            List<ClassStuInfo> classStuInfoList = new ArrayList<>();
            while (iterator.hasNext()) {
                //获代表学生的key，例：(qdstu)A13191A2130460001<1>(1)_1:2014213950
                String stukey = iterator.next();
                //根据key获取value
                String classStuInfoOfString = jedisGetAllStuKeys.get(stukey);
                //将每个学生存储的json字符串转为java对象
                ClassStuInfo classStuInfo = objectMapper.readValue(classStuInfoOfString, ClassStuInfo.class);
                //将该学生对象加入到list集合中
                classStuInfoList.add(classStuInfo);
            }
            //5.归还reids连接
            jedisGetAllStuKeys.close();
            //6.返回list集合
            return classStuInfoList;
        } catch (JsonProcessingException e) {
            //日志
            logger.error("获取所有学生记录缓存出现错误！错误信息：[{}]", e.getMessage());
        }
        return null;
    }


    /**
     * 获取指定key的剩余时间，需要指定数据库
     *
     * @param key   key
     * @param index 索引，取第几个数据库获取。
     * @return long 剩余时间 返回-2说明没有找到该key，返回-3说明程序出错了，返回-1说明该key永久有效。
     */
    public long getTimeRemain(String key, int index) {
        try {
            //1.获取redis连接
            Jedis jedisTtl = jedisPool.getResource();
            //2.选择第数据库
            jedisTtl.select(index);
            //3.获取剩余时间
            Long ttl = jedisTtl.ttl(key);
            //4.归还连接
            jedisTtl.close();
            //5.返回数据
            return ttl;
        } catch (Exception e) {
            //日志
            logger.error("获取剩余时间出现未知错误！错误信息：[{}]", e.getMessage());
            return -3;
        }
    }

    /**
     * 获取指定key是value
     *
     * @param key   key
     * @param index redis数据库索引，表示第几个数据库。
     * @return value 成功获取就返回value，失败就返回"false";
     */
    public String getValue(String key, int index) {
        try {
            //1.获取redis连接
            Jedis jedisGetValue = jedisPool.getResource();
            //2.选择数据库
            jedisGetValue.select(index);
            //3.获取数据并返回
            String value = jedisGetValue.get(key);
            //4.归还连接
            jedisGetValue.close();
            //5.返回数据
            return value;
        } catch (Exception e) {
            //日志
            logger.error("根据指定key：[{}]获取value失败！", key);
            return "false";
        }
    }

    /**
     * 清空选定的redis数据库
     *
     * @param index 数据库索引
     * @return boolean
     */
    public boolean flushDB(int index) {
        try {
            //1.获取redis连接
            Jedis delDB = jedisPool.getResource();
            //2.选择数据库
            delDB.select(index);
            //3.执行删除操作
            String s = delDB.flushDB();
            //4.归还连接
            delDB.close();
            //5.如果s为”OK“表示清空成功
            if ("OK".equals(s)) {
                //日志
                if (logger.isInfoEnabled()) {
                    logger.info("【redis操作类(RedisService)】已清空redis第[{}]个数据库", index + 1);
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            //日志
            logger.error("清空redis第[{}]数据库出现未知错误！错误信息：[{}]", index + 1, e.getMessage());
            return false;
        }
    }

    /**
     * 将学生的某一门课签到历史数据放进缓存。(redis第6个数据库)
     *
     * @param stuQianDaoHistory StuQianDaoResult学生单门课程的签到历史结果
     */
    public boolean setStuQDHistory(StuQianDaoHistory stuQianDaoHistory) {
        //操作redis的key  (sqdh)xh_jxb。  例：(sqdh)2017214033_A13191A2130460001
        String key = "(sqdh)" + stuQianDaoHistory.getXh() + "_" + stuQianDaoHistory.getJxb();
        try {
            //1.获取redis连接
            Jedis jedisSetstuQdHistory = jedisPool.getResource();
            //2.选择第6个数据库
            jedisSetstuQdHistory.select(5);
            //3.将java对象转为json字符串
            ObjectMapper objectMapper = new ObjectMapper();
            String stuqdhisJson = objectMapper.writeValueAsString(stuQianDaoHistory);
            //4.将数据放入缓存，有效时间为半小时
            jedisSetstuQdHistory.set(key, stuqdhisJson);
            jedisSetstuQdHistory.expire(key, 1800);
            //5.归还连接
            jedisSetstuQdHistory.close();
            //6.返回true
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            //日志
            logger.error("添加学生历史签到数据缓存出现未知错误！错误信息：[{}]", e.getMessage());
            return false;
        }


    }

    /**
     * 获取学生某门课程签到历史记录缓存（redis第6个数据库）
     *
     * @param xh  学号
     * @param jxb 教学班
     * @return StuQianDaoHistory
     */
    public StuQianDaoHistory getStuQDhistory(String xh, String jxb) {
        //操作redis的key (sqh)xh_jxb。  例：(sqh)2017214033_A13191A2130460001
        String key = "(sqh)" + xh + "_" + jxb;

        //结果
        StuQianDaoHistory stuQianDaoHistory = null;
        try {
            //1.获取redis连接
            Jedis jedisGetQDH = jedisPool.getResource();
            //2.选择第6个数据库
            jedisGetQDH.select(5);
            //3.获取数据
            String stuqdhisJson = jedisGetQDH.get(key);
            //4.判断数据是否获取成功
            if (null == stuqdhisJson) {
                //失败，没有该数据，返回空的java对象
                return stuQianDaoHistory;
            }
            //5.成功将json字符串转为java对象
            ObjectMapper objectMapper = new ObjectMapper();
            stuQianDaoHistory = objectMapper.readValue(stuqdhisJson, StuQianDaoHistory.class);
            //6.关闭连接
            jedisGetQDH.close();
            //7.返回
            return stuQianDaoHistory;

        } catch (Exception e) {
            //日志
            logger.error("获取学生课程历史签到数据出现未知错误！错误信息：[{}]", e.getMessage());
            return stuQianDaoHistory;
        }
    }


    /**
     * 根据教学班，周，星期几获取符合条件的提问记录的key(redis第7个数据库)
     *
     * @param jxb      教学班
     * @param week     周
     * @param work_day 星期几
     * @return Set集合
     */
    public Set<String> keysTwjl(String jxb, String week, String work_day) {
        //操作redis的key。例：(tw)TW-A13191A2130460001<12>(1)*
        String key = "(twjl)TW-" + jxb + "<" + week + ">(" + work_day + ")" + "*";
        //实例化一个set集合
        Set<String> keys = new HashSet<>();
        try {
            //1.获取redis链接
            Jedis jedisKeys = jedisPool.getResource();
            //2.选择第7个数据库
            jedisKeys.select(6);
            //3.执行keys查询操作
            keys = jedisKeys.keys(key);
            //4.归还链接
            jedisKeys.close();
            //5.返回数据
            return keys;
        } catch (Exception e) {
            //日志
            logger.error("根据jxb、week、work_day获取符合条件的key出现未知错误！");
            //出现错误，返回空set集合
            return keys;
        }
    }

    /**
     * 根据教学班 周 星期几三个参数，获取符合条件的 提问控制 数据（redis第7个数据库）
     * 因为 提问控制 数据是设置了有效时间的（本次提问的有效时间），如果返回值不为空，说明本门课程当前有提问真正进行。不允许再次发起提问。
     *
     * @param week     周
     * @param jxb      教学班
     * @param work_day 星期几
     * @return Set集合，key
     */
    public Set<String> keysTwkz(String week, String jxb, String work_day) {
        //操作redis的key
        String key = "(twkz)TW-" + jxb + "<" + week + ">(" + work_day + ")_" + "*";
        try {
            //1.获取redis链接
            Jedis jedisKeys = jedisPool.getResource();
            //2.选择第7个reids数据库
            jedisKeys.select(6);
            //3.执行keys命令
            Set<String> keys = jedisKeys.keys(key);
            //4.归还链接
            jedisKeys.close();
            //5.返回该keys集合
            return keys;
        } catch (Exception e) {
            //日志
            logger.error("根据jxb,week,work_day获取相应提问记录出现未知错误！错误信息：[{}]", e.getMessage());
            //返回空keys
            return null;
        }
    }

    /**
     * 课堂提问，加载学生名单进缓存(reids第7个数据库)
     * 在老师发起提问之后，把该课学生名单全部加载到缓存，回答一个学生，删除一个学生的记录，代表该生以及回答了。
     *
     * @param classStuInfoList 课堂学生名单
     * @param work_day         星期几
     * @param week             周
     * @param jxb              教学班
     * @param twcs             提问次数
     * @return String 加载成功，就返回提问id（twid）。加载失败，返回"false"字符串
     */
    public String loadTWStuList(List<ClassStuInfo> classStuInfoList, String work_day, String week, String jxb, String twcs) {
        //操作redis的key
        //提问ID：TW-A13191A2130440002<18>(2)_1
        String twid = "TW-" + jxb + "<" + week + ">(" + work_day + ")_" + twcs;
        //前缀
        String firstKey = "(twstu)";
        try {
            //1.获取redis链接
            Jedis jedisLoadStu = jedisPool.getResource();
            //2.选择数据库
            jedisLoadStu.select(6);
            //3.将课程成的学生名单加载到缓存
            //3.1 jackson操作对象，用于将java对象转为json字符串
            ObjectMapper objectMapper = new ObjectMapper();
            for (ClassStuInfo classStuInfo : classStuInfoList) {
                //key为前缀+twid+“:”+学号
                String key = firstKey + twid + ":" + classStuInfo.getXh();
                //将twid添加到学生信息中
                classStuInfo.setTwid(twid);
                //存进缓存，value为课堂学生信息的josn字符串
                jedisLoadStu.set(key, objectMapper.writeValueAsString(classStuInfo));
            }
            //4.归还链接
            jedisLoadStu.close();
            //5.返回
            return twid;
        } catch (Exception e) {
            //日志
            logger.error("课堂提问，加载学生名单进缓存出现未知错误！错误信息:[{}]", e.getMessage());
            //返回false
            return "false";
        }
    }

    /**
     * 将教师发起的提问题目加载到缓存（redis第7个数据库）
     * <p>
     * 1.提问控制(twkz)：该值的有效时间为本次提问的有效时间，用于控制学生的答题时间。value值是 ”问题主体 （WTZT类的实例对象）“
     * 2.提问记录(twjl)：value值是这次提问的 ”问题主体（WTZT类的实例对象）“，不设有效时间
     *
     * @param wtzt     问题主体
     * @param week     周
     * @param work_day 星期几
     * @param twcs     提问次数，这是这堂课第几次提问
     * @param jxb      教学班
     * @param yxsj     有效时间，本次提问的有效时间
     * @return String 提问id，发起成功，就返回提问id（twid）。发起失败，返回"false"字符串
     */
    public String loadTWToCache(WTZT wtzt, String week, String work_day, String twcs, String jxb, long yxsj) {
        //操作redis的key
        //提问id 例：TW-A13191A2130440002<18>(2)_1
        String twid = "TW-" + jxb + "<" + week + ">(" + work_day + ")_" + twcs;
        //问题主体的前缀
        String firstKeyTwkz = "(twkz)";
        //提问记录的前缀
        String firstKeyTWJL = "(twjl)";
        //jackson操作对象，将java对象转为json对象
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //1.获取redis链接
            Jedis jedisLoad = jedisPool.getResource();

            //2.选择redis数据库，第7个
            jedisLoad.select(6);

            //3.将wtzt JAVA对象转为json字符串
            String wtztJson = objectMapper.writeValueAsString(wtzt);

            //4.加载提问控制 进缓存，用于回显给学生端，以及后续数据处理。设有效时间，用于控制回答时间。
            String keyTwkz = firstKeyTwkz + twid;
            jedisLoad.set(keyTwkz, wtztJson);
            jedisLoad.expire(keyTwkz, Integer.parseInt(String.valueOf(yxsj)));

            //5.加载提问记录进缓存，value值是 问题主体。
            String keyTWJL = firstKeyTWJL + twid;
            jedisLoad.set(keyTWJL, wtztJson);

            //6.归还链接
            jedisLoad.close();

            //7.返回提问id
            return twid;

        } catch (Exception e) {
            e.printStackTrace();
            //日志
            logger.error("加载问题主体进缓存出现未知错误！错误信息：[{}]", e.getMessage());
            //返回“fasle”
            return "false";
        }
    }


    /**
     * 获取问题主体
     * 以前缀 “(twjl)”开头的数据存储的是问题主体，并且是不过期的。
     * redis第7个数据库
     * <p>
     * 每次发起提问，会产生一个提问记录。提问记录value值也是问题主体。
     *
     * @param twid 提问id
     * @return WTZT
     */
    public WTZT getWtzt(String twid) {

        //操作redis的key。
        String key = "(twjl)" + twid;

        try {
            //1.获取redis链接
            Jedis jedisGet = jedisPool.getResource();
            //2.选择redis第7个数据库
            jedisGet.select(6);
            //3.获取
            String wtztJson = jedisGet.get(key);
            //4.将json数据转为java对象
            ObjectMapper objectMapper = new ObjectMapper();
            WTZT wtzt = objectMapper.readValue(wtztJson, WTZT.class);
            //5.归还链接
            jedisGet.close();
            //6.返回数据
            return wtzt;
        } catch (JsonProcessingException e) {
            //日志
            logger.error("从缓存获取问题主体出现未知错误！错误信息：[{}]", e.getMessage());
        }
        return null;
    }

    /**
     * 将学生提交的答案放进缓存
     * redis第7个数据库
     *
     * @param answerData 答案数据
     * @return boolean
     */
    public boolean setStuAnswer(AnswerData answerData) {
        //操作redis的key
        //例：(answer)TW-A13191A2130460001<12>(1)_1:2017214033
        String key = "(answer)" + answerData.getTwid() + ":" + answerData.getXh();
        try {
            //1.获取redis链接
            Jedis jedisSetAnswer = jedisPool.getResource();
            //2.选择第7个数据库
            jedisSetAnswer.select(6);
            //3.将java对象转为json字符串
            ObjectMapper objectMapper = new ObjectMapper();
            String answerDataJson = objectMapper.writeValueAsString(answerData);
            //4.将数据加入到缓存
            jedisSetAnswer.set(key, answerDataJson);
            //5.归还链接
            jedisSetAnswer.close();
            //6.返回true
            return true;
        } catch (JsonProcessingException e) {
            //日志
            logger.error("将学生答案放进缓存出现未知错误！");
            //返回false
            return false;
        }
    }

    /**
     * 根据提问id和学号删除代表没有签到的数据
     * redis第7个数据库
     *
     * @param twid 提问id
     * @param xh   学号
     * @return boolean
     */
    public boolean delTwStu(String twid, String xh) {
        //操作redis的key
        String key = "(twstu)" + twid + ":" + xh;
        try {
            //1.获取redis链接
            Jedis jedisDel = jedisPool.getResource();
            //2.选择第7个数据库
            jedisDel.select(6);
            //3.执行删除操作
            Long del = jedisDel.del(key);
            //4.归还链接
            jedisDel.close();
            //5.返回true
            return true;
        } catch (Exception e) {
            //日志
            logger.error("删除学生为答题记录出现未知错误！错误信息：[{}]", e.getMessage());
            //返回false
            return false;
        }
    }

    /**
     * 根据提问id获取回答了问题学生的key
     * reids第7个数据库
     *
     * @param twid 提问id
     * @return Set<String></String>
     */
    public Set<String> keysAnswerStu(String twid) {
        //操作redis的key
        String key = "(answer)" + twid + "*";
        try {
            //1.获取redis链接
            Jedis jedisKeys = jedisPool.getResource();
            //2.选择第7个数据库
            jedisKeys.select(6);
            //3.执行操作
            Set<String> keys = jedisKeys.keys(key);
            //4.归还链接
            jedisKeys.close();
            //5.返回
            return keys;
        } catch (Exception e) {
            //日志
            logger.error("获取回答了的学生的keys出现未知错误！错误信息：[{}]", e.getMessage());
            return null;
        }
    }

    /**
     * 根据提问Id获取学生的回答answerData
     *
     * @param key key
     * @return String 获取成功就是返回answerdata的json字符串，失败就返回"false"
     */
    public String getAnswerData(String key) {
        try {
            //1.获取链接
            Jedis jedis = jedisPool.getResource();
            //2.选择第7个redis数据库
            jedis.select(6);
            //3.获取数据
            String answerDataJson = jedis.get(key);
            //4.归还链接
            jedis.close();
            //5.返回
            return answerDataJson;
        } catch (Exception e) {
            //日志
            logger.error("获取学生的答案出现未知错误！错误原因：[{}]", e.getMessage());
        }
        return "false";
    }

    /**
     * 获取未答题学生名单
     * reids第7个数据库
     *
     * @param twid 提问id
     * @return map集合，元素为未答题学生的key
     */
    public Set<String> keysNoAnswerStuKey(String twid) {
        //操作redis的key
        String key = "(twstu)" + twid + "*";
        try {
            //1.获取redis链接
            Jedis jedisKeys = jedisPool.getResource();
            //2.选择第7个redis数据库
            jedisKeys.select(6);
            //3.执行
            Set<String> keys = jedisKeys.keys(key);
            //4.归还链接
            jedisKeys.close();
            //5.返回
            return keys;
        } catch (Exception e) {
            //日志
            logger.error("获取未答题学生key出现未知错误！错误原因：[{}]", e.getMessage());
            return null;
        }
    }

    /**
     * 获取没有回答问题的学生
     *
     * @param key key
     * @return 获取成就返回ClassStudentInfo的json字符串，失败返回"false"
     */
    public String getNoAnswerStu(String key) {
        try {
            //1.获取redis链接
            Jedis jedisget = jedisPool.getResource();
            //2.选择第7个redis数据库
            jedisget.select(6);
            //3.执行
            String classStuInfoJson = jedisget.get(key);
            //4.归还链接
            jedisget.close();
            //5.返回
            return classStuInfoJson;
        } catch (Exception e) {
            //日志
            logger.error("获取未答题学生key出现未知错误！错误原因：[{}]", e.getMessage());
            return "false";
        }
    }

    /**
     * 获取缓存中所有的提问记录（redis第7个数据库）
     *
     * @return 提问记录的Set集合
     */
    public Set<String> getAllTwjl() {

        //操作redis的key
        String key = "(twjl)*";
        try {
            //1.获取redis连接
            Jedis jedisGet = jedisPool.getResource();
            //2.选择第7个数据库
            jedisGet.select(6);
            //3.执行,查找所有以 “(twjl)”开头的key
            Set<String> keys = jedisGet.keys(key);
            //4.归还连接
            jedisGet.close();
            //5.返回数据
            return keys;
        } catch (Exception e) {
            //日志
            logger.error("获取所有提问记录key出现位置错误！错误原因：[{}]", e.getMessage());
            return null;
        }
    }

    /**
     * 获取缓存中某一次提问没有回答问题的学生数据（redis第7个数据库）
     *
     * @param twid 提问id
     * @return 没有回答问题的学生集合
     */
    public List<ClassStuInfo> getThisTimeNoAnStu(String twid) {
        //操作redis的key
        String key = "(twstu)"+twid+"*";
        //操作json字符串的对象
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //1.获取redis连接
            Jedis jedisGetAllStuKeys = jedisPool.getResource();
            //2.选择di5个数据库里
            jedisGetAllStuKeys.select(6);
            //3.获取所有学生的key
            Set<String> keys = jedisGetAllStuKeys.keys(key);
            //4.通过keys获取学生数据
            Iterator<String> iterator = keys.iterator();//迭代器
            List<ClassStuInfo> classStuInfoList = new ArrayList<>();
            while (iterator.hasNext()) {
                //获代表学生的key，例：(twstu)TW-A13191A2130460001<1>(1)_1:2014213950
                String stukey = iterator.next();
                //根据key获取value
                String classStuInfoOfString = jedisGetAllStuKeys.get(stukey);
                //将每个学生存储的json字符串转为java对象
                ClassStuInfo classStuInfo = objectMapper.readValue(classStuInfoOfString, ClassStuInfo.class);
                //将该学生对象加入到list集合中
                classStuInfoList.add(classStuInfo);
            }
            //5.归还reids连接
            jedisGetAllStuKeys.close();
            //6.返回list集合
            return classStuInfoList;
        } catch (JsonProcessingException e) {
            //日志
            logger.error("获取所有未回答提问学生记录缓存出现错误！错误信息：[{}]", e.getMessage());
        }
        return null;
    }

    /**
     * 将课程的历史提问数据放任缓存 （redis第6个数据库）
     *
     * @param keChengTiWenHistory keChengTiWenHistory
     */
    public void setKCTWHistory(KeChengTiWenHistory keChengTiWenHistory) throws JsonProcessingException {
        //操作redis的key
        String key = "(kctwh)" + keChengTiWenHistory.getJxb();//例：(kctwh)A13191A2130460001

        //1.获取redis连接
        Jedis resource = jedisPool.getResource();
        //2.选择第6个数据库
        resource.select(5);
        //3. 将KeChengTiWenHistory转为json格式数据
        ObjectMapper objectMapper = new ObjectMapper();
        String KCTWHistoryJson = objectMapper.writeValueAsString(keChengTiWenHistory);
        //4.存入数据
        resource.set(key, KCTWHistoryJson);
        //5.设置有效时间,2小时
        resource.expire(key, 7200);
        //5.归还redis连接
        resource.close();
    }

    /**
     * 通过教学班号获取相应课程的课堂提问 回答情况历史数据（redis第6个数据库）
     *
     * @param jxb 教学班号
     * @return KeChengTiWenHistory
     */
    public KeChengTiWenHistory getKCTWHistory(String jxb) throws JsonProcessingException {
        //操作redis的key
        String key = "(kctwh)" + jxb;

        //1.获取redis连接
        Jedis resource = jedisPool.getResource();
        //2.
        resource.select(5);
        //3.
        String result = resource.get(key);
        if ("NULL".equals(result)||null==result) {
            resource.close();
            return null;
        }
        //4.将结果转换为java对象
        ObjectMapper objectMapper = new ObjectMapper();
        KeChengTiWenHistory keChengTiWenHistory = objectMapper.readValue(result, KeChengTiWenHistory.class);
        //4.归还连接
        resource.close();
        //5.返回结果
        return keChengTiWenHistory;
    }

    /**
     * 将学生某门课程的历史答题情况数据放进redis缓存（redis第6个数据库）
     * key：     例：(stwh)A13191A2130460001-2017214033
     *
     * @param stuTiWenHistory StuTiWenHistory
     */
    public void setStuTWHistory(StuTiWenHistory stuTiWenHistory) throws JsonProcessingException {
        //操作redis 的key
        String key = "(stwh)" + stuTiWenHistory.getJxb() + "-" + stuTiWenHistory.getXh();//例：(stwh)A13191A2130460001-2017214033

        //1.获取reids连接
        Jedis resource = jedisPool.getResource();
        //2.选择第6个数据库
        resource.select(5);
        //3.将java对象转为json格式数据
        ObjectMapper objectMapper = new ObjectMapper();
        String stutTWHistoryJson = objectMapper.writeValueAsString(stuTiWenHistory);
        //4.数据放进数据库
        resource.set(key, stutTWHistoryJson);
        //5.设置有效时间,2小时
        resource.expire(key, 7200);
        //6.归还redis连接
        resource.close();
    }

    /**
     * 获取某个学生的某门课程的历史答题情况(redis第6个数据库)
     *
     * @param jxb 教学班
     * @param xh  学号
     * @return StuTiWenHistory
     */
    public StuTiWenHistory getStuTWHistory(String jxb, String xh) throws JsonProcessingException {
        //操作redis的key
        String key = "(stwh)" + jxb + "-" + xh;
        //1.获取reids连接
        Jedis resource = jedisPool.getResource();
        //2.选择数据库
        resource.select(5);
        //3.获取数据
        String s = resource.get(key);
        if ("NULL".equals(s) || null == s) {
            resource.close();
            //没有返回空
            return null;
        }
        //4.json转为java对象
        ObjectMapper objectMapper = new ObjectMapper();
        StuTiWenHistory stuTiWenHistory = objectMapper.readValue(s, StuTiWenHistory.class);
        //5.归还连接
        resource.close();
        //6.返回数据
        return stuTiWenHistory;
    }

    /**
     * 将课程的历史签到数据放进缓存（redis第6个数据库）
     *
     * @param keChengQianDaoHistory KeChengQianDaoHistory类
     */
    public void setKCQDHistory(KeChengQianDaoHistory keChengQianDaoHistory) throws JsonProcessingException {
        //操作redis的key
        String key = "(kcqdh)" + keChengQianDaoHistory.getJxb();//例：(kcqdh)A13191A2130460001
        //1.获取redis 连接
        Jedis resource = jedisPool.getResource();
        //2.选择数据库
        resource.select(5);
        //3.将java对象转为json格式数据
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(keChengQianDaoHistory);
        //4.放进redis
        resource.set(key, s);
        //5.设置有效时间，2小时
        resource.expire(key, 7200);
        //6.归还连接
        resource.close();
    }

    /**
     * 获取课程的历史签到数据（redis第6个数据库）
     *
     * @param jxb 教学班
     * @return KeChengQianDaoHistory
     */
    public KeChengQianDaoHistory getKCQDHistroy(String jxb) throws JsonProcessingException {
        //操作redis的key
        String key = "(kcqdh)" + jxb;
        //1.获取连接
        Jedis resource = jedisPool.getResource();
        //2.选择数据库
        resource.select(5);
        //3.获取数据
        String s = resource.get(key);
        if ("NULL".equals(s) || null == s) {
            resource.close();
            return null;
        }
        //4.将json对象转为java对象
        ObjectMapper objectMapper = new ObjectMapper();
        KeChengQianDaoHistory keChengQianDaoHistory = objectMapper.readValue(s, KeChengQianDaoHistory.class);
        //5.归还连接
        resource.close();
        //6.返回数据
        return keChengQianDaoHistory;
    }

    /**
     * 将教学班与教师的对应关系放进缓存（redis第8个数据库）
     * <p>
     * key：（dygx）jxb 例：(dygx)A13191A2130460001
     * value: teaId 教师号
     *
     * @param jxbs  某个老师所教课程的教学班集合
     * @param teaId 教师号 （tesId）
     */
    public void setJxbDYTeaId(Set<String> jxbs, String teaId) {
        //操作redis的key的前缀
        String firstKey = "(dygx)";
        //1.获取redis连接
        Jedis resource = jedisPool.getResource();
        //2.选择数据库
        resource.select(7);
        //3.加载数据进缓存
        Pipeline pipelined = resource.pipelined();
        for (String s : jxbs) {
            String key = firstKey + s;
            pipelined.set(key, teaId);
            //有效时间150天
            pipelined.expire(key, 12960000);
        }
        pipelined.sync();
        //4.归还连接
        pipelined.close();
        resource.close();
    }

    /**
     * 根据教学班获取对应的教师号(redis第8个数据库)
     * @param jxb 教学班号
     * @return String 教师号
     */
    public String getTeaIdByJxb(String jxb){
        //操作redis的key
        String key="(dygx)"+jxb;
        //1.获取redis连接
        Jedis resource = jedisPool.getResource();
        //2.选择数据库
        resource.select(7);
        //3.获取数据
        String teaId = resource.get(key);
        if ("NULL".equals(teaId)||null==teaId){
            resource.close();
            return null;
        }
        //4.归还连接
        resource.close();
        //5.返回
        return teaId;
    }

}
