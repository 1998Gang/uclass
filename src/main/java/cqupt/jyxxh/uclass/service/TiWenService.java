package cqupt.jyxxh.uclass.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.dao.TiWenMapper;
import cqupt.jyxxh.uclass.pojo.ClassStuInfo;
import cqupt.jyxxh.uclass.pojo.KbStuListData;
import cqupt.jyxxh.uclass.pojo.tiwen.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private RedisService redisService;     //reids操作类

    @Autowired
    private ComponentService componentService;   //service 层，用于获取一些数据

    @Autowired
    private TiWenMapper tiWenMapper;          //提问功能的dao操作


    /**
     * 教师发起课堂提问提问方法
     *
     * @param tiWenData 提问数据
     * @return String 发起成功，就返回提问id(twid)。发起失败，就返回"false"字符串。
     */
    public String askQuestion(TiWenData tiWenData) {
        try {
            //1.判断当前是否有提问正在进行
            // 1.1根据教学班、周、星期几获取相应的的缓存信息的key。
            Set<String> keys = redisService.keysTwjl(tiWenData.getJxb(), tiWenData.getWeek(), tiWenData.getWork_day());
            // 1.2如果该set集合不为0，说明该课目前正有提问在进行，发起提问失败
            if (keys.size() != 0) {
                return "false";
            }

            //2.加载学生名单进缓存
            KbStuListData kbStuListData = componentService.getKbStuListData(tiWenData.getJxb());
            String str1 = redisService.loadTWStuList(kbStuListData.getStudents(), tiWenData.getWork_day(), tiWenData.getWeek(), tiWenData.getJxb(), tiWenData.getTwcs());

            //3.加载题目，以及提问记录（问题主体 WTZT）到缓存
            String str2 = redisService.loadTWToCache(tiWenData.getWtzt(), tiWenData.getWeek(), tiWenData.getWork_day(), tiWenData.getTwcs(), tiWenData.getJxb(), tiWenData.getYxsj());

            //4.只要步骤2与步骤3其中一个失败，本次发起签到就不算成功！
            if (!(str1.equals("false") || str2.equals("false"))) {
                return str2;//如果成功，str1与str2都是提问id(twid)
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
        Map<String, Object> resultMap = new HashMap<>();

        try {
            //1.根据jxb week work_day 三个参数获取对应的问题主体缓存的key。keys的长度只会是 0 或 1。
            Set<String> keys = redisService.keysWtzt(week, jxb, work_day);
            //1.1判断keys的长度。
            if (keys.size() == 0) {
                //长度为0，说明当前没有提问正在进行，可能是时间过了。返回一个空的map集合
                return resultMap;
            }

            //2.根据这个key获相关数据
            for (String key : keys) {

                //2.1.1获取剩余时间
                long timeRemain = redisService.getTimeRemain(key, 6);
                //2.1.2如果剩余时间小于6秒，直接返回空map集合，表示获取失败。因为时间太短，加上延迟，基本无用。
                if (timeRemain <= 6) {
                    return resultMap;
                }
                //2.1.3考虑延时，在剩余时间减去3秒
                timeRemain -= 3;

                //2.2获取提问id。key: (twjl)TW-A13191A2130460001<12>(1)_1  twid:TW-A13191A2130460001<12>(1)_1
                String twid = key.substring(key.indexOf("(twjl)") + 6);

                //2.3获取问题主体
                WTZT wtzt = redisService.getWtzt(twid);

                //2.4将数据添加到集合中
                resultMap.put("twid", twid);//提问id
                resultMap.put("timeremaining", timeRemain);//本次提问的剩余时间
                resultMap.put("wtzt", wtzt);//问题主体
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
     * 教师获取提问结果
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

            //2.获取未答题学生名单
            List<Map<String, String>> noAnswerStuList = new ArrayList<>();
            //2.1根据twid获取未回答学生的key
            Set<String> nokeys = redisService.keysNoAnswerStuKey(twid);
            //2.2循环获取
            for (String key : nokeys) {
                //获取数据
                String classStuInfoJson = redisService.getNoAnswerStu(key);
                //将json数据转为java对象
                ClassStuInfo classStuInfo = objectMapper.readValue(classStuInfoJson, ClassStuInfo.class);
                //实例化一个map集合
                Map<String, String> noAnswerStu = new HashMap<>();
                //给map集合添加数据
                noAnswerStu.put("xh", classStuInfo.getXh());//学号
                noAnswerStu.put("xm", classStuInfo.getXm());//姓名
                noAnswerStu.put("yxm", classStuInfo.getYxm());//学院名
                //将map集合添加到List集合noAnswerStuList。
                noAnswerStuList.add(noAnswerStu);
            }

            //3.往tiWenResult中添加数据
            tiWenResult.setTwid(twid);//提问id
            tiWenResult.setNoAnswerStuList(noAnswerStuList);// 未回答问题的学生名单


            //4.添加答案统计结果
            //3.1判断问题类型
            switch (wtzt.getQuestiontype()) {
                case "sub": {
                    //主观题
                    List<AnswerData> subResult = getSubResult(twid);
                    tiWenResult.setSubResult(subResult);//添加主观题结果
                    tiWenResult.setQuestiontype("sub");//添加问题类型，主观题
                    break;
                }
                case "obj": {
                    //课观题
                    List<ObjResult> objResults = getObjResults(twid, wtzt);
                    tiWenResult.setObjResults(objResults);//添加客观题结果
                    tiWenResult.setQuestiontype("obj");//添加问题类型，客观题
                    break;
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
     * 根据学号与教学班获取某学生某门课程的提问记录
     * @param xh 学号
     * @param jxb 教学班
     * @return StuTiWenHistory
     */
    public StuTiWenHistory getStuTWHistory(String xh,String jxb){
        try {
            //定义返回参数
            StuTiWenHistory stuTiWenHistory=new StuTiWenHistory();//学生提问历史记录主体
            int total=0;//该门课总提问次数
            int hdTimes=0;//回答次数
            int wdTimes=0;//未答次数

            //封装参数请求参数。
            Map<String,String> xhAndJxb=new HashMap<>();
            xhAndJxb.put("xh",xh);
            xhAndJxb.put("jxb",jxb);

            //根据学号和教学班，查询数据库。获取该生该课的回答记录。
            List<StuTWRecord> stuTWRecordList = tiWenMapper.getStuTWRecord(xhAndJxb);

            //遍历回答记录
            for (StuTWRecord stuTWRecord:stuTWRecordList){

            }


        }catch (Exception e){

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
            int total=0;//总选择次数
            int max=0;//标识，用来标识这些选项中，选择次数最多的。
            String maxOption="A";//标识，用来标识被选择最多的选项。默认位A。
            //本题的选项以及对应的次数
            Map<String, Integer> stringIntegerMap = countOptions.get(th);
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
            int id = 1;//选项的id
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
            objResult.setTh(question.getTh());//设置题号
            objResult.setLetters(letters);//将所有选项加入到该题中

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

        //1.根据twid获取提交了答案的学生的key
        Set<String> keys = redisService.keysAnswerStu(twid);

        //2.循环取出这些学的是答案。
        for (String key : keys) {
            //取出该学生答案
            String answerDataJson = redisService.getAnswerData(key);
            //将答案转为java对象
            AnswerData answerData = objectMapper.readValue(answerDataJson, AnswerData.class);
            //获取选择题答案,
            List<Answer> answers = answerData.getAnswers();
            for (Answer answer : answers) {
                String th = answer.getTh();//题号
                List<String> objAnswer = answer.getObjAnswer();//选项

                //判断count集合中有没有该题号
                if (!count.containsKey(th)) {
                    //没有该题号，添加题号,并新建一个装选项的map集合放进去
                    Map<String, Integer> options = new HashMap<>();
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
     * 持久化缓存中的提问数据到mysql数据库
     * 1.提问记录
     * 2.未回答学生名单
     */
    public void persistentTiWenData() {
        try {
            //1.获取缓存中有的所有提问记录的key  例：(twjl)TW-A13191A2130460001<12>(1)_2
            Set<String> allTwjl = redisService.getAllTwjl();
            //1.1根据提问记录key获取必要数据。
            //当前时间
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            //提问记录集合
            List<Twjl> twjlList=new ArrayList<>();
            for (String oneTwjl:allTwjl){
                Twjl twjl=new Twjl();
                //获取提问id
                twjl.setTwid(oneTwjl.substring(oneTwjl.indexOf("(twjl)")+6));
                //获取教学班号
                twjl.setJxb(oneTwjl.substring(oneTwjl.indexOf("-")+1,oneTwjl.indexOf("<")));
                //获取这周
                twjl.setWeek(oneTwjl.substring(oneTwjl.indexOf("<")+1,oneTwjl.indexOf(">")));
                //获取星期几
                twjl.setWork_day(oneTwjl.substring(oneTwjl.indexOf(">(")+2,oneTwjl.indexOf(")_")));
                //获取提问次数
                twjl.setTwcs(oneTwjl.substring(oneTwjl.indexOf("_")+1));
                //获取提问时间
                twjl.setTwsj(date);
                //将该提问记录添加进集合
                twjlList.add(twjl);
            }
            //1.2将提问记录持久化到mysql数据库
            if (!twjlList.isEmpty()){
                tiWenMapper.insertTiWenJL(twjlList);
            }

            //2.获取缓存中所有没有回答课堂提问学生的数据
            List<ClassStuInfo> allNoAnStu = redisService.getAllNoAnStu();
            //2.1将这些学生数据持久化到mysql数据库
            if (!allNoAnStu.isEmpty()){
                tiWenMapper.insertNoAnStuList(allNoAnStu);
            }

            //3.持久化完毕，清空redis第7个数据库
            redisService.flushDB(6);

            //日志
            if (logger.isInfoEnabled()){
                logger.info("将课堂提问缓存数据持久化到mysql。有提问记录[{}]条，未回答提问学生数据[{}]条",twjlList.size(),allNoAnStu.size());
            }
        }catch (Exception e){
            //日志
            logger.error("将课堂提问缓存数据持久化到mysql出现未知错误！");
        }
    }
}
