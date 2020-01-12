package cqupt.jyxxh.uclass.service;

import checkers.units.quals.C;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.pojo.ClassStudentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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

    @Autowired
    private JedisPool jedisPool;  //redis连接池


    /**
     * 向redis中存储课表（课表在第二个数据库）
     *
     * @param number （number为学号或者教师号)
     * @param kebiao 课表实体，嵌套的json字符串
     * @return boolea
     */
    public boolean setKeBiao(String number, String kebiao) {

        //操作redis的key
        String key = "kebiao_" + number;

        try {
            // 1.获取一个redis连接
            Jedis jedisKebiao = jedisPool.getResource();
            // 2.选择第二个reids数据库
            jedisKebiao.select(1);
            // 3.执行存储操作
            jedisKebiao.set(key, kebiao);
            // 4.设置有效时间 20小时
            jedisKebiao.expire(key, 72000);
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
        String key = "kebiao_" + number;
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
        String key = "cjzc_" + xh;
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
        String key = "cjzc_" + xh;

        try {
            // 1.获取redis连接
            Jedis jedisGetcjzc = jedisPool.getResource();
            // 2.选择第三个数据库
            jedisGetcjzc.select(2);
            // 3.判断缓存中有没有该key的数据
            Boolean exists = jedisGetcjzc.exists(key);
            if (!exists) {
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
        } catch (Exception e) {
            //日志
            logger.error("从缓存获取成绩组成数据出现未知错误！key:[{}]", key);
            return "false";
        }
    }


    /**
     * 添加课堂学生名单数据到缓存（redis第4个数据库）
     *
     * @param jxb         教学班号
     * @param stuListData json字符串格式的数据。
     * @return boolean
     */
    public boolean setStuListData(String jxb, String stuListData) {

        //操作redsi的key
        String key = "stulistdata_" + jxb;

        try {
            // 1.获取redis连接
            Jedis jedisSetlistData = jedisPool.getResource();
            // 2.选择第4个数据库
            jedisSetlistData.select(3);
            // 3.将数据添加进redis
            jedisSetlistData.set(key, stuListData);
            // 4.设置数据有效时间，30天
            jedisSetlistData.expire(key, 2592000);
            // 5.归还redis连接
            jedisSetlistData.close();

            return true;
        } catch (Exception e) {
            //日志
            logger.error("添加课堂学生名单出错！key:[{}]", key);
            return false;
        }
    }

    /**
     * 从缓存获取课堂学生数据（redis第4个数据库）
     * 返回false说明数据获取失败，（1.缓存中没有该数据。2.出现未知错误。）
     *
     * @param jxb 教学班号
     * @return 学生数据的json字符串格式数据
     */
    public String getStuListData(String jxb) {

        //操作redsi的key
        String key = "stulistdata_" + jxb;
        try {
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
        } catch (Exception e) {
            //日志
            logger.error("从缓存获取学生名单数据出现未知错误！key:[{}]", key);
            return "false";
        }
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
        String key = "schooltime_" + nowData;

        try {
            // 1.获取redis连接，不选择数据库，使用默认的第一个数据库。
            Jedis jediSetSchoolTime = jedisPool.getResource();
            // 2.添加数据
            jediSetSchoolTime.set(key, schooleTime);
            // 3.设置有效时间为24小时
            jediSetSchoolTime.expire(key, 86400);
            // 4.归还redis连接
            jediSetSchoolTime.close();

            return true;
        } catch (Exception e) {
            //日志
            logger.error("添加教务时间缓存出错！key:[{}]", key);
            return false;
        }
    }

    /**
     * 获取教务时间（redis第1个数据库，默认）
     *
     * @param nowData 当天日期（nowData是当天的日期，yyyy-MM-dd）
     * @return 教务时间的json数据
     */
    public String getSchoolTime(String nowData) {

        //操作redis的key
        String key = "schooltime_" + nowData;

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
        String key = "phpsessid_" + ykth;
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
        String key = "phpsessid_" + ykth;

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
        String key = "(qdm)" + jxb + "<" + week + ">(" + work_day + ")" + "*";//例：(qdm)A13191A2130440002<18>(2)*
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
            logger.error("出现未知错误");
        }
        return null;
    }

    /**
     * 获取签到码(redis第5个数据库)
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
            // 3.获取签到码
            String s = jedisGetQdm.get(key);
            // 4.归还连接
            jedisGetQdm.close();
            // 5.返回数据
            return s;
        } catch (Exception e) {
            //日志
            logger.error("获取签到码出现未知错误！错误信息：[{}]", e.getMessage());
            return "false";
        }
    }

    /**
     * 加载教学班的学生名单到缓存(redis第5个数据库)，用于课堂点名（签到）的。
     * 传进来的参数jxb,week,work_day,qdcs是用于做redis的key
     * key:(qdstu)A13191A2130440002<18>(2)_1:2017214033
     * value:由classStudenInfo对象转换的的json字符串
     *
     * @param jxb      教学班
     * @param week     周数
     * @param work_day 星期几
     * @param qdcs     当堂课的第几次签到
     * @param students 学生们
     * @return boolean
     */
    public boolean loadStudentList(String jxb, String week, String work_day, String qdcs, List<ClassStudentInfo> students) {
        try {
            //key的前缀( 教学班<周>(星期几)_签到次数: )
            String keyFirst = "(qdstu)" + jxb + "<" + week + ">(" + work_day + ")" + "_" + qdcs + ":";//例：A13191A2130440002<18>(2)_1:

            // 1.获取redis连接
            Jedis jedisLoadStuList = jedisPool.getResource();
            // 2.选择第5个数据库
            jedisLoadStuList.select(4);
            // 3.循环加载list集合中的学生进缓存。
            ObjectMapper objectMapper = new ObjectMapper();
            for (ClassStudentInfo classStudent : students) {
                //key为这次点名的唯一识别加学生学号。
                String key = keyFirst + classStudent.getXh();//例：A13191A2130440002<18>(2)_1:2017214033
                //设置签到状态为"QQ"(缺勤)
                classStudent.setQdzt("QQ");
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
     * 获取指定课堂有记录的学生名单（包含未签到，迟到，请假）（redis第5个数据库）
     *
     * @param qdid 签到id 一次签到的唯一识别码
     * @return Set 学生数据Set集合
     */
    public Set<String> getNotOnStus(String qdid) {
        //操作redis的key。key用于查询符合条件的key，也就是指定签到的所有学生记录。
        String keyFirst = "(qdstu)" + qdid + ":*";//例：(qdstu)A13191A2130440002<18>(2)_1:*
        try {

            // 1.获取一个redis连接
            Jedis jedisGetNotOnStus = jedisPool.getResource();
            // 2.选择第五个数据库
            jedisGetNotOnStus.select(4);
            // 3.查询符合条件的key
            Set<String> keys = jedisGetNotOnStus.keys(keyFirst);
            // 4.根据keys获取数据
            Iterator<String> iterator = keys.iterator();//迭代器
            Set<String> stuList = new HashSet<>();
            for (int i = 1; i <= keys.size(); i++) {
                String s = jedisGetNotOnStus.get(iterator.next());
                stuList.add(s);
            }
            // 4.归还连接
            jedisGetNotOnStus.close();
            // 5.返回数据
            return stuList;
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
        String key = "(qdstu)"+qdid + ":" + xh; //例：(qdstu)A13191A2130440002<18>(2)_1:2017214033

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
                    ClassStudentInfo classStudentInfo = objectMapper.readValue(s, ClassStudentInfo.class);
                    classStudentInfo.setQdzt(bqlx);
                    String s1 = objectMapper.writeValueAsString(classStudentInfo);
                    jedisDel.set(key, s1);
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
     *
     * @param jxb      教学班
     * @param week     周数
     * @param work_day 星期几
     * @param qdcs     签到次数，这是这堂课第几次签到
     * @param qdm      签到码
     * @param yxsj     本次签到的有效时间
     * @return 签到id，或者”false“
     */
    public String loadQdmToCache(String jxb, String week, String work_day, String qdcs, String qdm, String yxsj) {

        //操作redis的key（用于添加签到码）
        String qdid = jxb + "<" + week + ">(" + work_day + ")" + "_" + qdcs;
        String keyQdm = "(qdm)" + qdid+":"+yxsj;//例：(qdm)A13191A2130440002<18>(2)_1:120
        String keyQdjl = "(qdjl)" + qdid;//例：(qdjl)A13191A2130440002<18>(2)_1

        try {
            // 1.获取redis连接
            Jedis jedisLoadQdm = jedisPool.getResource();
            // 2.选择第5个数据库
            jedisLoadQdm.select(4);
            // 2.加载签到码到缓存
            jedisLoadQdm.set(keyQdm, qdm);
            // 3.设置签到码有效时间
            jedisLoadQdm.expire(keyQdm, Integer.parseInt(yxsj));
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
    public boolean delStu(String qdid, String xh) {
        //操作redis的key
        String key = "(qdstu)"+qdid + ":" + xh;
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
     * @return List集合，元素的ClassStudentInfo,如果出现错误，返回null;
     */
    public List<ClassStudentInfo> getAllNoQdStu() {
        //操作redis的key
        String key="(qdstu)*";
        //操作json字符串的对象
        ObjectMapper objectMapper=new ObjectMapper();

        try {
            //1.获取redis连接
            Jedis jedisGetAllStuKeys = jedisPool.getResource();
            //2.选择di5个数据库里
            jedisGetAllStuKeys.select(4);
            //3.获取所有学生的key
            Set<String> keys = jedisGetAllStuKeys.keys(key);
            //4.通过keys获取学生数据
            Iterator<String> iterator = keys.iterator();//迭代器
            List<ClassStudentInfo> classStudentInfoList=new ArrayList<>();
            while (iterator.hasNext()){
                //获代表学生的key，例：(qdstu)A13191A2130460001<1>(1)_1:2014213950
                String stukey = iterator.next();
                //根据key获取value
                String classStuInfoOfString = jedisGetAllStuKeys.get(stukey);
                //将每个学生存储的json字符串转为java对象
                ClassStudentInfo classStudentInfo = objectMapper.readValue(classStuInfoOfString, ClassStudentInfo.class);
                //获取签到id（qdid）  例：A13191A2130460001<1>(1)_1
                String qdid=stukey.substring(stukey.indexOf("(qdstu)")+7,stukey.indexOf(":"));
                //将签到id（qdid）设置到该学生对象中
                classStudentInfo.setQdid(qdid);
                //将该学生对象加入到list集合中
                classStudentInfoList.add(classStudentInfo);
            }
            //5.归还reids连接
            jedisGetAllStuKeys.close();
            //6.返回list集合
            return classStudentInfoList;
        } catch (JsonProcessingException e) {
            //日志
            logger.error("获取所有学生记录缓存出现错误！错误信息：[{}]",e.getMessage());
        }
        return null;
    }


    /**
     * 获取指定key的剩余时间
     *
     * @param key   key
     * @param index 索引，取第几个数据库获取。
     * @return long 剩余时间 返回-2说明没有找到该key，返回-3说明程序出错了，返回-1说明该key永久有效。
     */
    public long getTimeRemain(String key, int index) {
        try {
            //1.获取redis连接
            Jedis jedisTtl = jedisPool.getResource();
            //2.选择第5个数据库
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
     * @param key key
     * @param index redis数据库索引，表示第几个数据库。
     * @return value 成功获取就返回value，失败就返回"false";
     */
    public String getValue(String key,int index){
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
        }catch (Exception e){
            //日志
            logger.error("根据指定key：[{}]获取value失败！",key);
            return "false";
        }
    }

    /**
     * 清空选定的redis数据库
     * @param index 数据库索引
     * @return boolean
     */
    public boolean flushDB(int index){
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
            if ("OK".equals(s)){
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("【redis操作类(RedisService)】已清空redis第[{}]个数据库",index+1);
                }
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            //日志
            logger.error("清空redis第[{}]数据库出现未知错误！错误信息：[{}]",index+1,e.getMessage());
            return false;
        }
    }
}
