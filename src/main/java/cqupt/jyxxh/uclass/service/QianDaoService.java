package cqupt.jyxxh.uclass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.dao.QianDaoMapper;
import cqupt.jyxxh.uclass.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 签到功能service类
 * <p>
 * QQ|QJ|CD|WT  这是签到后的状态，分别代表  缺勤|请假|迟到|网络问题（网络问题补签，就是已签到。）
 * <p>
 * 教师发起签到，会把该教学班的学生名单加载到缓存（redis第5个数据库中），签到状态默认为QQ（缺勤）
 * 学生签到成功，会将本人的数据从缓存中删除。表示签到成功（没有记录）！
 * 教师补签，状态为WT，会将指定学生数据从缓存中删除。表示补签成功（没有记录）！
 * 其他状态，都会有记录。（QQ|QJ|CD）
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

    @Autowired
    private QianDaoMapper qianDaoMapper;         //dao操作，与签到有关的dao操作类


    /**
     * 教师发起签到
     * <p>
     * 教师发起签到步骤。1.先判断是否有其签到正在进行。如果有，签到失败！如果没有，进行第二步！
     * 2.将本堂课的学生名单加载进缓存（redis第5个数据库）。
     * 2.1   key:为  “(qdstu)QD#jxb<week>(work_day)_qdcs:xh” 注：(qdstu)QD#是字符串
     * 例：(qdstu)QD#A13191A2130440002<18>(2)_1:2017214033
     * 2.2   value: ClassStudenInfo的json字符串格式，签到状态统一为“QQ”，代表缺勤。
     * 3.将签到码加载进缓存（redis第1个数据库，默认。）
     * 3.1   key: “ (qdm)QD#jxb<week>(work_day)_qdcs  ”。注：(qdm)QD#是字符串
     * 例：(qdm)A13191A2130440002<18>(2)_1
     *
     * @param param map集合，是发起签到需要的必要参数。
     * @return 发起签到成功，返回签到id（qdid），如果失败，返回“false”
     */
    public String teaCreatQiandao(Map<String, String> param) {

        try {
            //1.获取参数
            String jxb = param.get("jxb");//教学班
            String week = param.get("week");//上课周
            String work_day = param.get("work_day");//星期几（工作日）
            String qdcs = param.get("qdcs");//签到次数，这是针对这堂课的第几次签到
            String qdm = param.get("qdm");//签到码
            String yxsj = param.get("yxsj");//签到码有效时间

            //日志
            if (logger.isInfoEnabled()) {
                logger.debug("【教师发起签到(serviceb层)】接收参数jxb:[{}],week:[{}],work_day:[{}],qdcs:[{}],qdm:[{}],yxsj:[{}]", jxb, week, work_day, qdcs, qdm, yxsj);
            }
            //2.判断当前是否有签到真正进行（判断是否有针对这门这堂课的签到码）
            // 2.1 根据教学班，周，星期几获取key
            Set<String> strings = redisService.keysQdm(jxb, week, work_day);
            // 2.2 如果得到的set不等于0，说明有签到正在进行。
            if (strings.size() != 0) {
                //当前课程有签到正在进行，发起签到失败，返回false;
                return "false";
            }

            // 3.将本堂课学生名单加载到缓存（redis第5个数据库）。
            // 3.1 获取该教学班的学生名单
            KbStuListData kbStuListData = componentService.getKbStuListData(jxb);
            if (kbStuListData.getHeadcount() == 0) {
                //根据教学班查出的数据总人数为0，说明获取名单失败，可能是教学班错误！
                return "jxb error";
            }
            List<ClassStudentInfo> students = kbStuListData.getStudents();
            // 3.2 将该教学班学生名单加载到缓存（redis）中
            boolean isLoadList = redisService.loadStudentList(jxb, week, work_day, qdcs, students);

            // 4.将签到码加载进缓存（redis第5个数据库），并设置有效时间。同还插入一条记录，记录有这么一次签到（不设有效时间）。
            String qddata = redisService.loadQdmToCache(jxb, week, work_day, qdcs, qdm, yxsj);

            // 5.名单加载成功，签到码加载返回值不为“false”。表示发起签到成功，返回签到id;
            if (!"false".equals(qddata) && isLoadList) {
                return qddata;
            }

        } catch (Exception e) {
            //日志
            logger.error("发起签到service发送未知错误！");
            return "false";
        }
        return "false";
    }

    /**
     * 获取某一次签到的结果
     *
     * @param qdid 签到id 一次签到的唯一识别码
     * @return QianDaoResult
     */
    public QianDaoResult getQianDaoResultOneTimes(String qdid) {

        QianDaoResult qianDaoResult = new QianDaoResult();

        try {
            int askForLeaveNum = 0;//请假人数
            int beLateNum = 0;//迟到人数
            int notOnStuNum = 0;//未签到人数
            int allStuNum;//总人数
            int inStuNum;//签了的人数
            Set<ClassStudentInfo> notOnTheStuList = new HashSet<>();// 未签到学生名单（包含没签到，迟到，请假）

            //jakson的操作对象
            ObjectMapper objectMapper = new ObjectMapper();

            // 1.获取本次点名还有缓存记录的的学生名单（包含了未签到，迟到，请假的学生）。（数据都在缓存里，redis第五个数据库）
            Set<String> notNoStus = redisService.getNotOnStus(qdid);

            // 2.遍历 notNostus，获取请假人数，迟到人数，未签到人数，名单。 set集合中没一个字符串是一个ClassStudentInfo的json字符串数据。遍历将json字符串还原为ClassStudentInfo对象。
            for (String s : notNoStus) {
                ClassStudentInfo classStudentInfo = objectMapper.readValue(s, ClassStudentInfo.class);
                //判断该记录类型 "CD"迟到 "QJ"请假 "QQ"缺勤
                switch (classStudentInfo.getQdzt()) {
                    case "CD": {
                        beLateNum += 1;
                        break;
                    }
                    case "QJ": {
                        askForLeaveNum += 1;
                        break;
                    }
                    case "QQ": {
                        notOnStuNum += 1;
                        break;
                    }
                }
                //将java对象放入名单中
                notOnTheStuList.add(classStudentInfo);
            }

            // 3.获取总人数
            // 3.1 获取该教学班学生名单数据
            String jxb = qdid.substring(0, qdid.indexOf("<"));
            KbStuListData kbStuListData = componentService.getKbStuListData(jxb);
            // 3.2 总人数
            allStuNum = kbStuListData.getHeadcount();

            //4.已经签了的人数,总人数-未签人数-迟到人数-请假人数
            inStuNum = allStuNum - notOnStuNum - beLateNum - askForLeaveNum;

            //5.获取签到时间,reids第5个数据库。
            String keyQDJL = "(qdjl)" + qdid;
            String qdsj = redisService.getValue(keyQDJL, 4);


            // 5.将数据添加到QianDaoResult对象中，返回。
            qianDaoResult.setAllStuNum(allStuNum);//整个班的学生人数
            qianDaoResult.setInStuNum(inStuNum);//已经签到了的学生人数
            qianDaoResult.setNotOnStuNum(notOnStuNum); //未签到学生人数
            qianDaoResult.setBeLateNum(beLateNum);//迟到人数
            qianDaoResult.setAskForLeaveNum(askForLeaveNum);//请假人数
            qianDaoResult.setNotOnTheStuList(notOnTheStuList); //未签到学生名单

            String week = qdid.substring(qdid.indexOf("<") + 1, qdid.indexOf(">"));
            qianDaoResult.setWeek(week);//时间 周

            String work_day = qdid.substring(qdid.indexOf("(") + 1, qdid.indexOf(")"));
            qianDaoResult.setWork_day(work_day);//时间 星期几

            String qdcs = qdid.substring(qdid.indexOf("_") + 1);
            qianDaoResult.setQdcs(qdcs);   //第几次签到

            qianDaoResult.setJxb(jxb);      //教学班
            qianDaoResult.setQdid(qdid); //签到id
            qianDaoResult.setQdsj(qdsj);  //签到时间

        } catch (JsonProcessingException e) {
            //日志
            logger.error("获取签到结果出现未知错误");
            e.printStackTrace();
        }
        return qianDaoResult;
    }

    /**
     * 教师给学生补签
     * 补签就是老师操作，根据补签类型，选择删除学生记录或者修改学生的状态条件。
     * 教师发起一次签到，会将该课学生名单加载到(redis第5个数据库），一个学生一条数据，value都是”QQ“表示缺勤。
     * 学生签到，验证码正确以后，回将缓存中的自己的数据删除。没有删除的，就是没有签到的。
     * 老师给学生补签。1.如果补签类型是CD（迟到），教师就将缓存中对应学生的数据修改为"CD".
     * 2.如果补签类型的QJ(请假)，教师奖缓存中学生的数据修改为"QJ".
     * 3.如果补签类型是WT(网络问题)，表示学生可能因为网络或者其他问题，没有签到成功，但是人在！教师删除对应学生数据，表示该学生成功签到！
     *
     * @param param map {"qdid":"签到id","xh":"补签学生学号","bqlx":"补签类型"}
     * @return boolean 返回补签是否成功。
     */
    public boolean teaRetroactive(Map<String, String> param) {

        try {
            // 1.解析参数

            String qdid = param.get("qdid");//签到id
            String xh = param.get("xh");//补签学生学号
            String bqlx = param.get("bqlx");//补签类型。QJ|CD|WT (补签类型,分别代表 请假|迟到|网络问题)

            // 2.操作缓存
            // 3.返回
            return redisService.alterStuQiandaoStatus(qdid, xh, bqlx);
        } catch (Exception e) {
            //日志
            logger.error("补签出现未知错误");
            return false;
        }
    }

    /**
     * 获取签到剩余时间
     * * @param jxb 教学班
     * * @param week 周
     * * @param work_day 星期几
     *
     * @return map集合装有剩余时间，签到id。{timeremaining,qdid,yxsj}
     * 如果没有获取到剩余时间 或者 获取到的剩余时间小于5秒，返回一个 空 map。
     */
    public Map<String, String> getQiandaoreaminTime(String jxb, String week, String work_day)  {

        Map<String, String> remainTimeResult = new HashMap<>();
        try {
            // 1.根据教学班，周，星期几获取完整的签到码的key
            Set<String> keys = redisService.keysQdm(jxb, week, work_day);
            // 1.1 如果strings的长度为0，说明没有签到在进行。
            if (keys.size() == 0) {
                //返回一个空的map集合（表示获取失败）
                return remainTimeResult;
            }

            // 2.得到key了，取获取剩余时间,还是得循环。但是keys的长度只能为0 或者 1 同一时间，不会有两个相同的签到码key。所以这里的key是唯一的。
            for (String key : keys) {
                // 2.1 获取剩余时间。签到码在redis的第5个数据库，所以还得传参数4。
                long timeRemain = redisService.getTimeRemain(key, 4);
                // 2.2 如果获得得到的时间小于6秒，直接返回一个空map集合（表示获取失败）。因为小于7秒，加上延迟，签到成功的可能性较低。
                if (timeRemain <= 6) {
                    return remainTimeResult;
                }
                // 2.3 考虑到网络延迟，这里将获取的时间减去3秒，在返回。
                timeRemain -= 3;
                // 2.4 获取签到id(qdid) 第一步获取的key是带前缀 “（qdm）” 。需要将前缀去掉才是签到id（qdid）。
                String qdid = key.substring(key.indexOf("(qdm)") + 5);
                // 2.5 通过签到id（qdid），获取这次签到设置的有效时间
                String yxsj = redisService.getYxsj(qdid);
                // 2.5封装结果
                remainTimeResult.put("timeremaining", String.valueOf(timeRemain));
                remainTimeResult.put("qdid", qdid);
                remainTimeResult.put("yxsj", yxsj);
                //返回结果
                return remainTimeResult;
            }
        } catch (Exception e) {
            //日志
            logger.error("获取点名剩余时间出现未知错误！错误信息：[{}]", e.getMessage());
        }
        return remainTimeResult;
    }

    /**
     * 学生签到
     *
     * @param param map集合{"qdid":"签到id","xh":"学号","qdm":"签到码"}
     * @return boolean
     */
    public boolean stuQiandao(Map<String, String> param) {

        //参数
        String qdid = param.get("qdid");   //签到id
        String qdm = param.get("qdm");  //签到码
        String xh = param.get("xh"); //学号


        try {
            // 1.获取签到码
            String qdmTrue = redisService.getQdm(qdid);


            if ("false".equals(qdmTrue)) {
                //教师设置的签到码获取失败，签到失败！
                return false;
            }
            // 2.两个签到码对比
            if (qdm.equals(qdmTrue)) {
                //2.1两个签到码一致，删除该学生在缓存中的数据（redis第5个数据库）
                //签到码验证成功，且删除了缓存记录！视为签到成功。
                //签到码验证成功，但没有删除缓存记录！视为签到失败。
                return redisService.delStu(qdid, xh);
            } else {
                //两个签到码不一致，签到失败！
                return false;
            }
        } catch (Exception e) {
            //日志
            logger.error("签到service出现未知错误！错误信息：[{}]", e.getMessage());
        }
        return false;
    }

    /**
     * 学生根据学号和教学班号获取本门课的历史签到记录
     *
     * @param xh  学号
     * @param jxb 教学班号
     * @return StuQianDaoResult
     */
    public StuQianDaoHistory getStuQDhistory(String xh, String jxb) {
        //先尝试从缓存获取
        try {
            StuQianDaoHistory stuQDhistory = redisService.getStuQDhistory(xh, jxb);
            if (!(stuQDhistory ==null)){
                //获取成功，直接返回
                return stuQDhistory;
            }
        }catch (Exception e){
            //日志
            logger.error("获取学生课程历史签到记录缓存信息失败！");
        }


        //封装参数。用于操作去数据库查询数据
        Map<String, String> xhAndJxb = new HashMap<>();
        xhAndJxb.put("xh", xh);
        xhAndJxb.put("jxb", jxb);
        //1.查询历史签到数据
        List<StuSingleRecord> records = qianDaoMapper.getStuQdRecord(xhAndJxb);

        //2.获取总签到次数
        int total = records.size();

        //3.缺勤次数
        int qqtime = 0;
        //4.请假次数
        int qjtime = 0;
        //5.迟到次数
        int cdtime = 0;
        //6.出勤次数
        int cqtime = 0;

        for (StuSingleRecord stuSingleRecord : records) {
            String qdzt = stuSingleRecord.getQdzt();

            if ("CD".equals(qdzt)) {
                cdtime += 1;
                continue;
            }
            if ("QQ".equals(qdzt)) {
                qqtime += 1;
                continue;
            }
            if ("QJ".equals(qdzt)) {
                qjtime += 1;
                continue;
            }
            stuSingleRecord.setQdzt("CQ");
            cqtime += 1;
        }

        //7.创建StuQianDaoResult对象，并填充数据
        StuQianDaoHistory stuQianDaoHistory =new StuQianDaoHistory();
        stuQianDaoHistory.setRecords(records);//添加签到记录
        stuQianDaoHistory.setTotal(total);//总签到次数
        stuQianDaoHistory.setQqtime(qqtime);//缺勤次数
        stuQianDaoHistory.setQjtime(qjtime);//请假次数
        stuQianDaoHistory.setCdtime(cdtime);//迟到次数
        stuQianDaoHistory.setCqtime(cqtime);//出勤次数
        stuQianDaoHistory.setXh(xh);//学号
        stuQianDaoHistory.setJxb(jxb);//教学班

        //8.将该生的该门课的签到数据，添加进缓存
        try {
            redisService.setStuQDHistory(stuQianDaoHistory);
        }catch (Exception e){
            //日志
            logger.error("将学生签到历史数据缓加缓存失败！");
        }

        //8.返回
        return stuQianDaoHistory;
    }

    /**
     * 获取课程的历史签到情况
     * @param jxb 教学班
     * @return KcQianDaoResult
     */
    public KcQianDaoHistory getKcQDhistory(String jxb){

        //对象
        KcQianDaoHistory kcQianDaoHistory =new KcQianDaoHistory();
        //定义参数
        int total=0;//有记录的总人次
        int absenteeism=0;//有缺勤记录是人次
        int lateArrivals=0;//有迟到记录的人次
        int numberOfLeave=0;//有请假记录的人次
        List<KcOneStuRecord> stuList=new ArrayList<>();//学生名单


        // 1.获取的该课程有记录学生名单
        List<Map<String, String>> maps = qianDaoMapper.getkcQdRecord(jxb);

        //2.遍历该名单,获取数据。
        // 一个map代表一个学生的数据。
        // 生成一个KcOneStuRecord对象,并添加到stuList中
        // 并判断该该生的qdzt，修改相关记录的人次。
        for (Map<String,String> map:maps){
            //有记录的总人次加一：
            total+=1;
            //为该生创建一个KcOneStuRecord对象。
            KcOneStuRecord kcOneStuRecord=new KcOneStuRecord();
            int qqtime=0;//缺勤次数
            int cdtime=0;//迟到次数
            int qjtime=0;//请假次数
            //获取数据
            String xh = map.get("xh");//学号
            String xm = map.get("xm");//姓名
            String qdzts = map.get("qdzts");//所有的签到状态  例：“QJ,QQ,QQ,CD”
            //解析签到状态,用“,”将qdzts分开,然后遍历统计各个签到状态的次数。
            String[] split = qdzts.split(",");
            for (String ss:split) {
                switch (ss) {
                    case "QQ": {
                        qqtime += 1;
                        break;
                    }
                    case "QJ": {
                        qjtime += 1;
                        break;
                    }
                    case "CD": {
                        cdtime += 1;
                        break;
                    }
                    default:
                        throw new IllegalStateException("Unexpected value: " + ss);
                }
            }
            //添加到kcOneStuRecord对象中
            kcOneStuRecord.setXh(xh);
            kcOneStuRecord.setXm(xm);
            kcOneStuRecord.setCdTime(cdtime);
            kcOneStuRecord.setQqTime(qqtime);
            kcOneStuRecord.setQjTime(qjtime);
            //将kcOneStuRecord对象添加到
            stuList.add(kcOneStuRecord);
            //判断该生是否缺勤|迟到|请假，修改有相关记录的人次
            if (0!=qjtime){
                //有请假记录,总的有请假记录的人次加一。
                numberOfLeave+=1;
            }
            if (0!=cdtime){
                //有迟到记录，总的迟到记录人次加一。
                lateArrivals+=1;
            }
            if (0!=qqtime){
                //有缺勤记录，总的缺勤记录人次加一。
                absenteeism+=1;
            }
        }

        //3.设置参数到kcQianDaoResult对象
        kcQianDaoHistory.setJxb(jxb);//教学班
        kcQianDaoHistory.setTotal(total);//有记录的总人次
        kcQianDaoHistory.setAbsenteeism(absenteeism);//有缺勤记录的人次
        kcQianDaoHistory.setLateArrivals(lateArrivals);//有迟到记录的人次
        kcQianDaoHistory.setNumberOfLeave(numberOfLeave);//有请假记录的人次
        kcQianDaoHistory.setStuList(stuList);//有记录的学生名单

        //返回
        return kcQianDaoHistory;
    }
}
