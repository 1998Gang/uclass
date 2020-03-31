package cqupt.jyxxh.uclass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.dao.TiWenMapper;
import cqupt.jyxxh.uclass.pojo.ClassStuInfo;
import cqupt.jyxxh.uclass.pojo.ClassStuList;
import cqupt.jyxxh.uclass.pojo.tiwen.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 课堂提问功能service层
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 1:00 2020/1/19
 */
@Service
public class TiWenService {

    Logger logger = LoggerFactory.getLogger(TiWenService.class);

    /**
     * reids操作类
     */
    @Autowired
    private  RedisService redisService;

    /**
     * service 层，用于获取一些数据
     */
    @Autowired
    private  ComponentService componentService;

    /**
     * 提问功能的dao操作
     */
    @Autowired
    private  TiWenMapper tiWenMapper;


    /**
     * 教师发起课堂提问提问方法
     * 发起提问完成，会创建一个持久化数据的任务，扔进延时队列，当本次提问的有效时间过期后，就会执行该持久化任务。
     * @param tiWenData 提问数据
     * @return String 发起成功，就返回提问id(twid)。发起失败，就返回"false"字符串。教学班错误返回“jxb is error”
     */
    public String askQuestion(TiWenData tiWenData) {
        try {
            //1.判断当前是否有提问正在进行
            // 1.1根据教学班、周、星期几三个参数， 看redis缓存中是否有与这三个参数都相同的 提问控制数据。
            Set<String> keys = redisService.keysTwkz(tiWenData.getWeek(),tiWenData.getJxb(),tiWenData.getWork_day());
            // 1.2如果该set集合不为0，说明该课目前正有提问在进行，发起提问失败
            if (keys.size() != 0) {
                return "false";
            }

            //2.加载学生名单进缓存
            ClassStuList classStuList = componentService.getClassStuList(tiWenData.getJxb());
            if (classStuList==null){
                return "jxb is error";
            }
            String str1 = redisService.loadTWStuList(classStuList.getClassStuInfoList(), tiWenData.getWork_day(), tiWenData.getWeek(), tiWenData.getJxb(), tiWenData.getTwcs());

            //3.把题目加载进redis缓存，并且加载一个提问控制数据进缓存（设置有效时间，以控制学生答题时间）
            String str2 = redisService.loadTWToCache(tiWenData.getWtzt(), tiWenData.getWeek(), tiWenData.getWork_day(), tiWenData.getTwcs(), tiWenData.getJxb(), tiWenData.getYxsj());

            //4.只要步骤2与步骤3其中一个失败，本次发起签到就不算成功！
            if (!("false".equals(str1) || "false".equals(str2))) {

                //教师发起提问成功，创建一个将本次提问数据持久化到msyql的操作,并加入到延时队列，交给后台线程执行持久化操作
                TiWenDateEndurance tiWenDateEndurance = new TiWenDateEndurance(tiWenData);
                DelayEnduranceToMysqlService.getDelayQueue().put(tiWenDateEndurance);
                //返回提问ID
                return str2;
            }
        } catch (Exception e) {
            //日志
            logger.error("发起提问service出现未知错误！错误信息：[{}]", e.getMessage());
        }
        return "false";
    }

    /**
     * 根据教学班，周，星期几获取课堂提问的题目和剩余时间等数据
     *
     * @param jxb      教学班
     * @param week     周
     * @param work_day 星期几
     * @return map集合  获取成功一个包含三个数据，twid、timeremaining、wtzt。提问id、该问题的剩余时间、问题主体（也就是题目）
     * 获取失败，返回的map集合是一个空集合。
     */
    public Map<String, Object> stuGetTopicAndTime(String jxb, String week, String work_day) {

        //实例化一个map集合
        Map<String, Object> resultMap = new HashMap<>(16);
        try {
            //1.根据jxb week work_day 三个参数，查看缓存中是否有符合条件的 提问控制 数据。如果有说明有提问正在进行，反之亦然。
            Set<String> keys = redisService.keysTwkz(week, jxb, work_day);
            //todo
            System.out.println(keys);
            //1.1判断keys的长度。
            if (keys.size() == 0) {
                //长度为0，说明当前没有提问正在进行，可能是时间过了。返回一个空的map集合
                return resultMap;
            }

            //2.根据这个key获相关数据
            for (String key : keys) {

                //2.1.1获取剩余时间
                long timeRemain = redisService.getTimeRemain(key, 6);
                //todo
                System.out.println(timeRemain);
                //2.1.2如果剩余时间小于6秒，直接返回空map集合，表示获取失败。因为时间太短，加上延迟，基本无用。
                if (timeRemain <= 6) {
                    return resultMap;
                }
                //2.1.3考虑延时，在剩余时间减去3秒
                timeRemain -= 3;

                //2.2获取提问id。key: (twkz)TW-A13191A2130460001<12>(1)_1  twid:TW-A13191A2130460001<12>(1)_1
                String twid = key.substring(key.indexOf("(twkz)") + 6);

                //2.3根据提问Id 获取问题主体
                WTZT wtzt = redisService.getWtzt(twid);
                //todo
                System.out.println(wtzt);

                //2.4将数据添加到集合中
                //提问id
                resultMap.put("twid", twid);
                //本次提问的剩余时间
                resultMap.put("timeremaining", timeRemain);
                //问题主体
                resultMap.put("wtzt", wtzt);
            }

            //3.获取成功，返回该数据
            return resultMap;

        } catch (Exception e) {
            //日志
            logger.error("学生获取题目以及剩余时间出现未知错误！错误信息：[{}]", e.getMessage());
            //返回一个空map集合
            return resultMap;
        }
    }

    /**
     * 学生提交答案
     *
     * @param answerData 提交的答案实体。
     * @return boolean
     */
    public boolean stuPutAnswer(AnswerData answerData) {
        try {
            //1.将答案放进缓存
            boolean isSetTrue = redisService.setStuAnswer(answerData);

            //2.将答案成功放进了缓存，删除加载进缓存代表学生没有回答问题的数据。
            if (isSetTrue) {
                //删除成功，返回true。
                return redisService.delTwStu(answerData.getTwid(), answerData.getXh());
            } else {
                //答案放进缓存失败！直接返回false。
                return false;
            }

        } catch (Exception e) {
            //日志
            logger.error("学生提交答案出现未知错误！错误信息：[{}]", e.getMessage());
            return false;
        }
    }

    /**
     * 教师获取当次提问的结果
     *
     * @param twid 提问id
     * @return TiWenResult
     */
    public TiWenResult teaGetResult(String twid) {

        //实例化一个jackson操作类，将json字符串转为java对象
        ObjectMapper objectMapper = new ObjectMapper();

        //定义一个TiWenResult对象
        TiWenResult tiWenResult = new TiWenResult();

        try {
            //1.根据提问id(twid)获取问题主体，根据问题主体判断问题类型
            WTZT wtzt = redisService.getWtzt(twid);
            //todo
            System.out.println(wtzt);

            //2.获取未答题学生名单
            List<Map<String, String>> noAnswerStuList = new ArrayList<>();
            //2.1根据twid获取本次未回答学生的key
            List<ClassStuInfo> thisTimeNoAnStus = redisService.getThisTimeNoAnStu(twid);
            //2.2遍历该名单，只要学号，姓名 以及学院信息
            for (ClassStuInfo classStuInfo:thisTimeNoAnStus){
                //实例化一个map集合
                Map<String, String> noAnswerStu = new HashMap<>(16);
                //给map集合添加数据
                //学号
                noAnswerStu.put("xh", classStuInfo.getXh());
                //姓名
                noAnswerStu.put("xm", classStuInfo.getXm());
                //学院名
                noAnswerStu.put("yxm", classStuInfo.getYxm());
                //将map集合添加到List集合noAnswerStuList。
                noAnswerStuList.add(noAnswerStu);
            }

            //3.往tiWenResult中添加数据
            //提问id
            tiWenResult.setTwid(twid);
            // 未回答问题的学生名单
            tiWenResult.setNoAnswerStuList(noAnswerStuList);


            //4.添加答案统计结果
            //3.1判断问题类型
            switch (wtzt.getQuestiontype()) {
                case "sub": {
                    //主观题
                    List<AnswerData> subResult = getSubResult(twid);
                    //添加主观题结果
                    tiWenResult.setSubResult(subResult);
                    //添加问题类型，主观题
                    tiWenResult.setQuestiontype("sub");
                    break;
                }
                case "obj": {
                    //课观题
                    List<ObjResult> objResults = getObjResults(twid, wtzt);
                    //添加客观题结果
                    tiWenResult.setObjResults(objResults);
                    //添加问题类型，客观题
                    tiWenResult.setQuestiontype("obj");
                    break;
                }
                default:{

                }
            }
        } catch (Exception e) {
            //日志
            logger.error("获取提问结果出现未知错误！错误信息:[{}]", e.getMessage());
            e.printStackTrace();
        }
        return tiWenResult;
    }

    /**
     * 教师获取本门课程的历史答题情况
     * @param jxb 教学班号
     * @return KeChengTiWenHistory
     */
    public KeChengTiWenHistory getKcTwHistory(String jxb){
        try {

            //通过该教学班查询该课程未答题的学生名单
            List<KeChengTwOneStuRecord> kctwHistory = tiWenMapper.getKCTWHistory(jxb);
            //通过教学班号获取该门课程的历史提问次数
            int askTimes = tiWenMapper.getKcAskTimes(jxb);
            //封装数据
            KeChengTiWenHistory keChengTiWenHistory=new KeChengTiWenHistory();
            keChengTiWenHistory.setJxb(jxb);
            keChengTiWenHistory.setTotal(kctwHistory.size());
            keChengTiWenHistory.setNoAnswerStuList(kctwHistory);
            keChengTiWenHistory.setAskTimes(askTimes);

            //返回实体
            return keChengTiWenHistory;
        }catch (Exception e){
            logger.error("教师获取本门课程的历史答题情况失败！");
        }
        return null;
    }

    /**
     * 根据学号与教学班获取某学生某门课程的提问回答情况
     * @param xh 学号
     * @param jxb 教学班
     * @return StuTiWenHistory
     */
    public StuTiWenHistory getStuTwHistory(String xh, String jxb){
        try {

            //定义返回参数
            //学生提问历史记录主体
            StuTiWenHistory stuTiWenHistory=new StuTiWenHistory();
            //该门课总提问次数
            int total=0;
            //回答次数
            int hdTimes=0;
            //未答次数
            int wdTimes=0;

            //封装参数请求参数。
            Map<String,String> xhAndJxb=new HashMap<>();
            xhAndJxb.put("xh",xh);
            xhAndJxb.put("jxb",jxb);

            //根据学号和教学班，查询数据库。获取该生该课的回答记录。
            List<StuTWRecord> stuTwRecordList = tiWenMapper.getStuTWRecord(xhAndJxb);

            //遍历回答记录,获取响应的次数
            for (StuTWRecord stuTWRecord:stuTwRecordList){
                String isAnswer = stuTWRecord.getIsAnswer();

                //如果isAnswer为null，说明该生本次提问回答了问题，如果不为null，说明没有回答。
                if (null==isAnswer){
                    hdTimes+=1;
                    //更改属性为true
                    stuTWRecord.setIsAnswer("true");
                }else {
                    wdTimes+=1;
                    //更改属性为false
                    stuTWRecord.setIsAnswer("false");
                }
            }
            //该门课程提问总次数
            total=stuTwRecordList.size();

            //封装数据
            stuTiWenHistory.setJxb(jxb);
            stuTiWenHistory.setXh(xh);
            stuTiWenHistory.setTotal(total);
            stuTiWenHistory.setHdTimes(hdTimes);
            stuTiWenHistory.setWdTimes(wdTimes);
            stuTiWenHistory.setStuTWRecordList(stuTwRecordList);

            //返回
            return stuTiWenHistory;
        }catch (Exception e){
            logger.error("根据学号教学班获取学生回答记录出错！");
        }
        return null;
    }

    /**
     * 获取客观题结果统计
     *
     * @param twid 提问id
     * @param wtzt 问题主体
     * @return TiWenResult
     */
    private List<ObjResult> getObjResults(String twid, WTZT wtzt) throws JsonProcessingException {
        //1.统计各个选择题提交的答案。例：{1={A=2,B=5,D=10},2={A=3,B=6,C=8}}
        Map<String, Map<String, Integer>> countOptions = countOptions(twid);
        //todo
        System.out.println(countOptions);

        //实例化客观题统计对象，一个objResult是一个选择题的结果统计
        List<ObjResult> objResults = new ArrayList<>();

        //2.获取问题。有多少个Question，就有多少个ObjResulst
        List<Question> questions = wtzt.getQuestions();
        //2.1遍历将questions,一个question是一道选择题
        for (Question question : questions) {
            //选项的集合。一个question（objResult）有一个letters。
            List<Letter> letters = new ArrayList<>();

            //获取该题题号
            String th = question.getTh();
            //获取该题设置的选项,例：[A,B,C,D,E]
            Set<String> options = question.getObjOptins();

            //获取这些选项的总次数
            //总选择次数
            int total=0;
            //标识，用来标识这些选项中，选择次数最多的。
            int max=0;
            //标识，用来标识被选择最多的选项。默认位A。
            String maxOption="A";
            //本题的选项以及对应的次数
            Map<String, Integer> stringIntegerMap = countOptions.get(th);
            //todo
            System.out.println(stringIntegerMap+"本题的选项以及对应次数");
            Set<String> keys = stringIntegerMap.keySet();
            for (String key:keys){
                //其中某一个答案的选择次数。
                Integer integer = stringIntegerMap.get(key);
                //叠加到总次数里
                total+=integer;
                //判断这些选项中的最大值。
                if (integer>=max){
                    max=integer;
                    maxOption=key;
                }
            }

            //遍历这个选项集合(题目)，一个选项为一个Letter
            //选项的id
            int id = 1;
            for (String option : options) {
                Letter letter = new Letter();
                //设置该选项的id
                letter.setId(id);
                //设置该选项的字母
                letter.setLetter(option);
                //该题选择该选项的次数.和 选择该选项的占比 。 要确保统计结果中响应的题有该选项，否则会空指针异常
                if (countOptions.containsKey(th)&&countOptions.get(th).containsKey(option)) {
                    Integer num = countOptions.get(th).get(option);
                    letter.setNum(num);
                    letter.setPresent((int)Math.round((double)num*100/total));
                }
                //设置底色
                if (option.equals(maxOption)){
                    //选择次数最多的那个选项
                    letter.setBackground("#8CDCDF");
                }else {
                    //其他选项
                    letter.setBackground("#C9C9C9");
                }


                //将该选项加入到letters中
                letters.add(letter);
                //每一次循环，id都加1。
                id+=1;
            }

            //实例化一个ObjResult
            ObjResult objResult = new ObjResult();
            //设置题号
            objResult.setTh(question.getTh());
            //将所有选项加入到该题中
            objResult.setLetters(letters);

            //将该objResult加入到List集合objResults中
            objResults.add(objResult);
        }
        return objResults;
    }

    /**
     * 获取主观题的统计结果
     *
     * @param twid 提问id
     * @return List<AnswerData>
     */
    private List<AnswerData> getSubResult(String twid) throws JsonProcessingException {

        //jackson操作类，将json字符串转未java对象
        ObjectMapper objectMapper = new ObjectMapper();

        //1.实例化一个TiWenResult对象
        TiWenResult tiWenResult = new TiWenResult();

        //2.获取回答了的学生的的答案
        List<AnswerData> subResult = new ArrayList<>();
        //2.1根据twid获取回答了的学生的key
        Set<String> answerkeys = redisService.keysAnswerStu(twid);
        //2.2循环获取
        for (String key : answerkeys) {
            String answerDataJson = redisService.getAnswerData(key);
            AnswerData answerData = objectMapper.readValue(answerDataJson, AnswerData.class);
            subResult.add(answerData);
        }

        //3.返回
        return subResult;
    }

    /**
     * 统计学生们提交上来的客观题（选择题答案）
     *
     * @param twid 提问id
     * @return 嵌套的map集合，外城map代表有多到题，里层map代表不同的选项
     * 例：{1={A=2,B=5,D=10},2={A=3,B=6,C=8}}
     */
    private Map<String, Map<String, Integer>> countOptions(String twid) throws JsonProcessingException {
        //Jackson操作类，将json字符串转为java对象
        ObjectMapper objectMapper = new ObjectMapper();

        //装统计结果的集合
        Map<String, Map<String, Integer>> count = new HashMap<>();

        //1.根据twid获取本次提问学生提交的答案的keys
        Set<String> keys = redisService.keysAnswerStu(twid);
        //todo
        System.out.println(keys);

        //2.循环取出这些学的是答案。
        for (String key : keys) {
            //取出该学生答案
            String answerDataJson = redisService.getAnswerData(key);
            //将答案转为java对象
            AnswerData answerData = objectMapper.readValue(answerDataJson, AnswerData.class);
            //获取选择题答案,
            List<Answer> answers = answerData.getAnswers();
            for (Answer answer : answers) {
                //题号
                String th = answer.getTh();
                //获得选择题 选项
                List<String> objAnswer = answer.getObjAnswer();

                //判断count集合中有没有该题号
                if (!count.containsKey(th)) {
                    //没有该题号，添加该题号,并为该题号新建一个装选项的map集合放进去
                    Map<String, Integer> options = new HashMap<>(16);
                    count.put(th, options);
                }

                //有该题号，根据该题号取出该题的答案集合。
                Map<String, Integer> options = count.get(th);
                //循环该学生该题的答案
                for (String option : objAnswer) {
                    //判断该题中有没有改选项
                    if (options.containsKey(option)) {
                        //有，该选项的次数加一
                        options.put(option, options.get(option) + 1);
                    } else {
                        //没有该选项，在该题的map集合中添加该选项，value为1
                        options.put(option, 1);
                    }
                }
            }
        }
        return count;
    }

    /**
     * 将提问数据持久化到数据库操作的封装,一个本类的实例化对象，就是一个持久化操作。
     * 每一次的持久化操作都作为一个任务，会放进延时队列，由后台线程去执行。
     * 这个任务会在提问的有效时间结束后执行
     */
    public class TiWenDateEndurance implements DelayEnduranceToMysqlService.EnduranceToMysql{

        //日志操作对象
        Logger logger=LoggerFactory.getLogger(TiWenDateEndurance.class);

        /**
         * 本次提问的结束时间
         */
        private long jssj;

        /**
         * 提问数据
         */
        private TiWenData tiWenData;

        /**
         * 构造方法
         * @param tiWenData 提问数据
         */
        public TiWenDateEndurance(TiWenData tiWenData){
            //时间单位
            TimeUnit unit=TimeUnit.SECONDS;
            this.jssj=System.currentTimeMillis()+(tiWenData.getYxsj()>0?unit.toMillis(tiWenData.getYxsj()):0);
            this.tiWenData =tiWenData;
        }

        /**
         * 持久化操作的具体功能
         */
        @Override
        @Transactional(rollbackFor = Exception.class)
        public void endurance() {
            try {
                /*1.生成本次提问的提问记录*/
                Twjl twjl=new Twjl();
                //提问id
                String twid="TW-" + tiWenData.getJxb() + "<" + tiWenData.getWeek() + ">(" + tiWenData.getWork_day()+ ")_" + tiWenData.getTwcs();
                //提问时间（yyyy-MM-dd）
                twjl.setTwsj(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                //添加提问id
                twjl.setTwid(twid);
                //获取教学班号
                twjl.setJxb(tiWenData.getJxb());
                //获取这周
                twjl.setWeek(tiWenData.getWeek());
                //获取星期几
                twjl.setWork_day(tiWenData.getWork_day());
                //获取提问次数
                twjl.setTwcs(tiWenData.getTwcs());

                /*2.将本次提问的提问记录插入到mysql数据库*/
                tiWenMapper.insertTWJL(twjl);

                /*3.获取本次提问未回答的学生名单*/
                List<ClassStuInfo> thisTimeNoAnStus = redisService.getThisTimeNoAnStu(twid);

                /*4.将本次未回答的学生名单持久化到数据库*/
                tiWenMapper.insertNoAnStuList(thisTimeNoAnStus);

                if (logger.isInfoEnabled()){
                    logger.info("将课堂[{}]提问缓存数据持久化到mysql,本次未回答人数:[{}]。",twid,thisTimeNoAnStus.size());
                }
            }catch (Exception e){
                //日志
                logger.error("将课堂提问缓存数据持久化到mysql出现未知错误！");
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
            return jssj-System.currentTimeMillis();
        }

        /**
         * 延时队列的排队规则
         * @param o Delayed对象
         * @return int值
         */
        @Override
        public int compareTo(@NotNull Delayed o) {
            TiWenDateEndurance tiWenDateEndurance= (TiWenDateEndurance) o;
            long l=this.jssj-tiWenDateEndurance.jssj;
            if (l <= 0) {
                return -1;
            }else {
                return 1;
            }
        }
    }
}
