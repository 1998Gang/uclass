package cqupt.jyxxh.uclass.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.dao.QianDaoMapper;
import cqupt.jyxxh.uclass.pojo.ClassStuInfo;
import cqupt.jyxxh.uclass.pojo.ClassStuList;
import cqupt.jyxxh.uclass.pojo.ClassStuListData;
import cqupt.jyxxh.uclass.pojo.qiandao.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

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

    /**
     * 日志操作对象
     */
    Logger logger = LoggerFactory.getLogger(QianDaoService.class);

    /**
     * redis操作功能类
     */
    @Autowired
    private  RedisService redisService;

    /**
     * service层类，用于获取一些数据。
     */
    @Autowired
    private  ComponentService componentService;

    /**
     * dao操作，与签到有关的dao操作类
     */
    @Autowired
    private  QianDaoMapper qianDaoMapper;

    /**
     * 教师发起签到
     * <p>
     * 教师发起签到步骤。1.先判断是否有其签到正在进行。如果有，签到失败！如果没有，进行第二步！
     * 2.将本堂课的学生名单加载进缓存（redis第5个数据库）。
     * 2.1   key:为  “(qdstu)QD-jxb<week>(work_day)_qdcs:xh” 注：(qdstu)QD-是字符串
     * 例：(qdstu)QD-A13191A2130440002<18>(2)_1:2017214033
     * 2.2   value: ClassStudenInfo的json字符串格式，签到状态统一为“QQ”，代表缺勤。
     * 3.将签到码加载进缓存（redis第1个数据库，默认。）
     * 3.1   key: “ (qdm)QD-jxb<week>(work_day)_qdcs  ”。注：(qdm)QD-是字符串
     * 例：(qdm)A13191A2130440002<18>(2)_1
     *
     * @param teaInitiateQdParams TeaInitiateQDParams，是发起签到需要的必要参数。
     * @return 发起签到成功，返回签到id（qdid），如果失败，返回“false”
     */
    public String teaCreatQiandao(TeaInitiateQdParams teaInitiateQdParams) {

        try {
            //1.获取参数
            String jxb = teaInitiateQdParams.getJxb();
            String week = teaInitiateQdParams.getWeek();
            //星期几（工作日）
            String work_day = teaInitiateQdParams.getWork_day();
            //签到次数，这是针对这堂课的第几次签到
            String qdcs = teaInitiateQdParams.getQdcs();
            //签到码
            String qdm = teaInitiateQdParams.getQdm();
            //签到码有效时间
            long yxsj = teaInitiateQdParams.getYxsj();

            //日志
            if (logger.isInfoEnabled()) {
                logger.debug("【教师发起签到(serviceb层)】接收参数jxb:[{}],week:[{}],work_day:[{}],qdcs:[{}],qdm:[{}],yxsj:[{}]", jxb, week, work_day, qdcs, qdm, yxsj);
            }
            //2.判断当前是否有签到真正进行（判断是否有针对这门这堂课的签到码）
            // 2.1 根据教学班，周，星期几获取签到签到码的key
            Set<String> keys = redisService.keysQdm(jxb, week, work_day);
            // 2.2 如果得到的set集合不等于0，说明有签到正在进行。
            if (keys.size() != 0) {
                //当前课程有签到正在进行，发起签到失败，返回false;
                return "false";
            }

            // 3.将本堂课学生名单加载到缓存（redis第5个数据库）。
            // 3.1 获取该教学班的学生名单
            ClassStuList classStuList = componentService.getClassStuList(jxb);
            if (classStuList==null) {
                //根据教学班的获取的名单为空，说明获取名单失败，可能是教学班错误！
                return "jxb error";
            }
            List<ClassStuInfo> classStuInfoList = classStuList.getClassStuInfoList();
            // 3.2 将该教学班学生名单加载到缓存（redis）中
            boolean isLoadList = redisService.loadQDStuList(jxb, week, work_day, qdcs, classStuInfoList);

            // 4.将签到码加载进缓存（redis第5个数据库），并设置有效时间。同还插入一条记录，记录有这么一次签到（不设有效时间）。
            String qddata = redisService.loadQdmToCache(jxb, week, work_day, qdcs, qdm, yxsj);

            // 5.名单加载成功，签到码加载返回值不为“false”。表示发起签到成功，返回签到id;
            if (!"false".equals(qddata) && isLoadList) {
                /*发起签到成功后，创建一个QianDaoDateEndurance类的实例对象，该对象表示一次将redis中数据持久化到mysql数据库的操作。
                * 并把该实例对象放进延时队列中，由后台线程去执行*/
                QianDaoDateEndurance qianDaoDateEndurance=new QianDaoDateEndurance(teaInitiateQdParams);
                DelayEnduranceToMysqlService.getDelayQueue().put(qianDaoDateEndurance);
                //返回签到ID
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
     * 教师根据签到ID获取本次签到的结果，从redis缓存中获取！
     * 每天中文，下午，晚上都会清空缓存，所有本次签到结果数据有效时间只有半天
     *
     * @param qdid 签到id 一次签到的唯一识别码
     * @return QianDaoResult
     */
    public QianDaoResult getQianDaoResultOneTimes(String qdid) {

        QianDaoResult qianDaoResult = new QianDaoResult();

        try {
            //请假人数
            int askForLeaveNum = 0;
            //迟到人数
            int beLateNum = 0;
            //未签到人数
            int notOnStuNum = 0;
            //总人数
            int allStuNum;
            //签了的人数
            int inStuNum;
            // 未签到学生名单（包含没签到，迟到，请假）

            List<ClassStuInfo> notOnTheStuList=new ArrayList<>(16);

            //jakson的操作对象
            ObjectMapper objectMapper = new ObjectMapper();

            // 1.获取本次点名还有缓存记录的的学生名单（包含了未签到，迟到，请假的学生）。（数据都在缓存里，redis第五个数据库）
            List<ClassStuInfo> thisTimeNotOnStus = redisService.getThisTimeNotOnStus(qdid);

            // 2.遍历 notNostus，获取请假人数，迟到人数，未签到人数，名单。 set集合中没一个字符串是一个ClassStudentInfo的json字符串数据。遍历将json字符串还原为ClassStudentInfo对象。
            for (ClassStuInfo classStuInfo : thisTimeNotOnStus) {
                //判断该记录类型 "CD"迟到 "QJ"请假 "QQ"缺勤
                switch (classStuInfo.getQdzt()) {
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
                    default:{}
                }
                //将java对象放入名单中
                notOnTheStuList.add(classStuInfo);
            }

            // 3.获取该教学班的总人数
            // 3.1 获取该教学班学生名单数据 qdi：QD-A13191A2130460001<18>(3)_2
            String jxb = qdid.substring(qdid.indexOf("-") + 1, qdid.indexOf("<"));
            ClassStuList classStuList = componentService.getClassStuList(jxb);
            // 3.2 总人数
            allStuNum = classStuList.getClassStuInfoList().size();

            //4.已经签了的人数,总人数-未签人数-迟到人数-请假人数
            inStuNum = allStuNum - notOnStuNum - beLateNum - askForLeaveNum;

            //5.获取签到时间,reids第5个数据库。
            String keyQdjl = "(qdjl)" + qdid;
            String qdsj = redisService.getValue(keyQdjl, 4);


            // 5.将数据添加到QianDaoResult对象中，返回。
            //整个班的学生人数
            qianDaoResult.setAllStuNum(allStuNum);
            //已经签到了的学生人数
            qianDaoResult.setInStuNum(inStuNum);
            //未签到学生人数
            qianDaoResult.setNotOnStuNum(notOnStuNum);
            //迟到人数
            qianDaoResult.setBeLateNum(beLateNum);
            //请假人数
            qianDaoResult.setAskForLeaveNum(askForLeaveNum);
            //未签到学生名单
            qianDaoResult.setNotOnTheStuList(notOnTheStuList);

            String week = qdid.substring(qdid.indexOf("<") + 1, qdid.indexOf(">"));
            //时间 周
            qianDaoResult.setWeek(week);

            String work_day = qdid.substring(qdid.indexOf("(") + 1, qdid.indexOf(")"));
            //时间 星期几
            qianDaoResult.setWork_day(work_day);

            String qdcs = qdid.substring(qdid.indexOf("_") + 1);
            //第几次签到
            qianDaoResult.setQdcs(qdcs);
            //教学班
            qianDaoResult.setJxb(jxb);
            //签到id
            qianDaoResult.setQdid(qdid);
            //签到时间
            qianDaoResult.setQdsj(qdsj);

        } catch (Exception e) {
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
    @Transactional(rollbackFor = {Exception.class})
    public boolean teaRetroactive(Map<String, String> param) {

        try {
            // 1.解析参数
            //签到id
            String qdid = param.get("qdid");
            //补签学生学号
            String xh = param.get("xh");
            //补签类型。QJ|CD|WT (补签类型,分别代表 请假|迟到|网络问题)
            String bqlx = param.get("bqlx");

            // 2.更改缓存中的签到数据
            boolean flage=false;
            flage = redisService.alterStuQiandaoStatus(qdid, xh, bqlx);

            // 3.更改mysql中的签到数据
            switch (bqlx){
                case "QJ":{
                    //请假,更改签到记录，以及学生的记录
                    qianDaoMapper.updataResultQj(qdid);
                    qianDaoMapper.updataNoQdStuQj(qdid,xh);
                    break;
                }
                case "CD":{
                    //迟到
                    qianDaoMapper.updataResultCd(qdid);
                    qianDaoMapper.updataNoQdStuCd(qdid,xh);
                    break;
                }
                case "WT":{
                    //网络问题
                    qianDaoMapper.updataResultWt(qdid);
                    qianDaoMapper.deleteNoQdStuWt(qdid,xh);
                    break;
                }
                default:{
                    break;
                }
            }
            // 3.返回
            return flage;
        } catch (Exception e) {
            //日志
            logger.error("补签出现未知错误");
            throw  e;
        }
    }

    /**
     * 获取签到剩余时间
     * * @teaInitiateQdParams jxb 教学班
     * * @teaInitiateQdParams week 周
     * * @teaInitiateQdParams work_day 星期几
     *
     * @return map集合装有剩余时间，签到id。{timeremaining,qdid,yxsj}
     * 如果没有获取到剩余时间 或者 获取到的剩余时间小于5秒，返回一个 空 map。
     */
    public Map<String, String> getQiandaoreaminTime(String jxb, String week, String work_day) {

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
                return redisService.delQdStu(qdid, xh);
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
     * 包括这门课一共签了几次到，该生的缺勤、请假、迟到、出勤次数等。
     * 以及每一次的签到状况。
     *
     * @param xh  学号
     * @param jxb 教学班号
     * @return StuQianDaoResult
     */
    public StuQianDaoHistory getStuQdHistory(String xh, String jxb) {

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
        StuQianDaoHistory stuQianDaoHistory = new StuQianDaoHistory();
        //添加签到记录
        stuQianDaoHistory.setRecords(records);
        //总签到次数
        stuQianDaoHistory.setTotal(total);
        //缺勤次数
        stuQianDaoHistory.setQqTime(qqtime);
        //请假次数
        stuQianDaoHistory.setQjTime(qjtime);
        //迟到次数
        stuQianDaoHistory.setCdTime(cdtime);
        //出勤次数
        stuQianDaoHistory.setCqTime(cqtime);
        //学号
        stuQianDaoHistory.setXh(xh);
        //教学班
        stuQianDaoHistory.setJxb(jxb);

        //8.返回
        return stuQianDaoHistory;
    }

    /**
     * 根据教学班获取本门课程的历史签到情况
     *
     * @param jxb 教学班
     * @return KcQianDaoResult
     */
    public KeChengQianDaoHistory getKcQdHistory(String jxb) {

        //对象
        KeChengQianDaoHistory keChengQianDaoHistory = new KeChengQianDaoHistory();
        //定义参数
        //有记录的总人次
        int total = 0;
        //有缺勤记录是人次
        int absenteeism = 0;
        //有迟到记录的人次
        int lateArrivals = 0;
        //有请假记录的人次
        int numberOfLeave = 0;
        //学生名单
        List<KeChengQdOneStuRecord> stuList = new ArrayList<>();


        // 1.获取的该课程有记录学生名单
        List<Map<String, String>> maps = qianDaoMapper.getkcQdRecord(jxb);

        //2.遍历该名单,获取数据。一个map代表一个学生的数据。生成一个KcOneStuRecord对象,并添加到stuList中并判断该该生的qdzt，修改相关记录的人次。
        for (Map<String, String> map : maps) {
            //有记录的总人次加一：
            total += 1;
            //为该生创建一个KcOneStuRecord对象。
            KeChengQdOneStuRecord keChengQdOneStuRecord = new KeChengQdOneStuRecord();
            //缺勤次数
            int qqtime = 0;
            //迟到次数
            int cdtime = 0;
            //请假次数
            int qjtime = 0;
            //获取数据
            //学号
            String xh = map.get("xh");
            //姓名
            String xm = map.get("xm");
            //所有的签到状态  例：“QJ,QQ,QQ,CD”
            String qdzts = map.get("qdzts");
            //解析签到状态,用“,”将qdzts分开,然后遍历统计各个签到状态的次数。
            String[] split = qdzts.split(",");
            for (String ss : split) {
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
            keChengQdOneStuRecord.setXh(xh);
            keChengQdOneStuRecord.setXm(xm);
            keChengQdOneStuRecord.setCdTime(cdtime);
            keChengQdOneStuRecord.setQqTime(qqtime);
            keChengQdOneStuRecord.setQjTime(qjtime);
            //将kcOneStuRecord对象添加到
            stuList.add(keChengQdOneStuRecord);
            //判断该生是否缺勤|迟到|请假，修改有相关记录的人次
            if (0 != qjtime) {
                //有请假记录,总的有请假记录的人次加一。
                numberOfLeave += 1;
            }
            if (0 != cdtime) {
                //有迟到记录，总的迟到记录人次加一。
                lateArrivals += 1;
            }
            if (0 != qqtime) {
                //有缺勤记录，总的缺勤记录人次加一。
                absenteeism += 1;
            }
        }

        //3.设置参数到kcQianDaoResult对象
        //教学班
        keChengQianDaoHistory.setJxb(jxb);
        //有记录的总人次
        keChengQianDaoHistory.setTotal(total);
        //有缺勤记录的人次
        keChengQianDaoHistory.setAbsenteeism(absenteeism);
        //有迟到记录的人次
        keChengQianDaoHistory.setLateArrivals(lateArrivals);
        //有请假记录的人次
        keChengQianDaoHistory.setNumberOfLeave(numberOfLeave);
        //有记录的学生名单
        keChengQianDaoHistory.setStuList(stuList);

        //返回
        return keChengQianDaoHistory;
    }

    /**
     *将签到数据持久到mysql数据这个操作封装成一个对象。该类的一个实例化对象就是一个持久化操作。
     */
    public class QianDaoDateEndurance implements DelayEnduranceToMysqlService.EnduranceToMysql{

        //日志操作对象
        Logger logger=LoggerFactory.getLogger(QianDaoDateEndurance.class);


        /**
         * 这次签到的结束时间
         */
        private long qdjssj;

        /**
         * 签到ID
         */
        private String qdid;

        /**
         * 构造方法
         * @param teaInitiateQdParams 发起一次提问的数据
         */
        public QianDaoDateEndurance(TeaInitiateQdParams teaInitiateQdParams){

            //时间单位为秒
            TimeUnit unit = TimeUnit.SECONDS;
            this.qdjssj=System.currentTimeMillis()+ unit.toMillis(teaInitiateQdParams.getYxsj());

            //签到ID
            this.qdid="QD-" + teaInitiateQdParams.getJxb() + "<" + teaInitiateQdParams.getWeek() + ">(" + teaInitiateQdParams.getWork_day() + ")" + "_" + teaInitiateQdParams.getQdcs();
        }

        /**
         * 将签到数据持久化到mysql的具体操作
         */
        @Override
        @Transactional(rollbackFor = Exception.class)
        public void endurance() {
            try {
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("将签到[{}]数据持久化到mysql操作开始！",qdid);
                }
                /*1.获取该次签到的签到数据*/
                QianDaoResult qianDaoResultOneTimes = getQianDaoResultOneTimes(qdid);
                //将本次签到的签到数据持久化到数据库
                qianDaoMapper.insertQiandaoResult(qianDaoResultOneTimes);
                //将本次签到由记录的学生名单持久化到数据库
                qianDaoMapper.insertNoQDStuInfo(qianDaoResultOneTimes.getNotOnTheStuList());

                //日志
                if (logger.isInfoEnabled()){
                    logger.info("将签到[{}]数据持久化到mysql成功,本次签到记录的学生数量为：[{}]",qdid,qianDaoResultOneTimes.getNotOnTheStuList().size());
                }
            }catch (Exception e){
                //日志
                logger.error("将签到[{}]数据持久化mysql失败，可能原因是数据冲突。具体错误信息：[{}]",qdid,e.getMessage());
                throw e;
            }
        }

        /**
         * 延时队列的跪规则
         * @param unit 时间参数的单位
         * @return long
         */
        @Override
        public long getDelay(@NotNull TimeUnit unit) {
            return qdjssj-System.currentTimeMillis();
        }

        /**
         * 延时队列的排队规则
         * @param o Delayed对象
         * @return int值
         */
        @Override
        public int compareTo(@NotNull Delayed o) {
            QianDaoService.QianDaoDateEndurance qianDaoDateEndurance= (QianDaoDateEndurance) o;
            long l=this.qdjssj-qianDaoDateEndurance.qdjssj;
            if (l <= 0) {
                return -1;
            }else {
                return 1;
            }
        }
    }


}
