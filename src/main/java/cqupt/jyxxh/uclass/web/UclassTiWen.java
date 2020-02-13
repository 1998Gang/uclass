package cqupt.jyxxh.uclass.web;

import cqupt.jyxxh.uclass.pojo.tiwen.AnswerData;
import cqupt.jyxxh.uclass.pojo.tiwen.TiWenData;
import cqupt.jyxxh.uclass.pojo.tiwen.TiWenResult;
import cqupt.jyxxh.uclass.service.TiWenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * 课堂提问功能接口
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 0:34 2020/1/19
 */
@Controller
public class UclassTiWen {

    Logger logger = LoggerFactory.getLogger(UclassTiWen.class);

    @Autowired
    private TiWenService tiWenService;   //提问功能service类


    /**
     * 教师发起提问接口
     *
     * @param tiWenData 发起提问数据
     * @return json格式响应信息
     */
    @RequestMapping(value = "teaaskquestions", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ResponseEntity<Map<String, String>> teaAskQuestion(@RequestBody TiWenData tiWenData) {

        Map<String, String> mm = new HashMap<>();
        try {
            //1.发起提问。发起成功，result就是本次提问的提问id(twid)。发起失败，result就是"false".
            String result = tiWenService.askQuestion(tiWenData);

            //2.判断是否成功
            if (result.equals("false")) {
                //日志
                if (logger.isInfoEnabled()) {
                    logger.info("教师发起课堂提问失败！教学班：[{}]", tiWenData.getJxb());
                }
                //2.1失败，响应417.
                mm.put("massage", "提问失败，当前有提问正在进行");
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(mm);
            } else {
                //日志
                if (logger.isInfoEnabled()) {
                    logger.info("教师发起课堂提问成功！提问id：[{}]", result);
                }
                //2.2成功，响应200
                mm.put("massage", "提问成功");
                mm.put("twid", result);
                return ResponseEntity.status(HttpStatus.OK).body(mm);
            }

        } catch (Exception e) {
            //日志
            logger.error("教师发起提问出现未知错误！错误信息：[{}]", e.getMessage());
            //响应500
            mm.put("massage", "服务器端出现未知错误！");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mm);
        }
    }


    /**
     * 学生获取课堂提问的题目
     * @param jxb 教学班
     * @param week 周
     * @param work_day 星期几
     * @return map集合。获取成功,将包含三个数据，twid、timeremaining、wtzt。提问id、该问题的剩余时间、问题主体（也就是题目）
     */
    @RequestMapping(value = "stugettopic",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<Map<String,Object>> stuGetTopicAndtime(@RequestParam("jxb") String jxb,@RequestParam("week") String week,@RequestParam("work_day") String work_day){
        //定义一个map集合
        Map<String, Object> mm = new HashMap<>();

        try {
            //1.获取题目信息以及剩余时间。
            Map<String, Object> resultMap= tiWenService.stuGetTopicAndTime(jxb,week,work_day);
            //2.判断
            if (resultMap.size()==0){
                //2.1得到的map集合长度为空，说明获取失败!
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("学生获取题目信息以及剩余时间失败！教学班：[{}],周：[{}},星期[{}]",jxb,week,work_day);
                }
                //响应403，以及提示信息
                mm.put("massage","获取失败，当前可能没有提问正在进行，或者回答时间以过。");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mm);
            }else {
                //2.2长度不为0，说明获取成功过
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("学生获取题目信息以及剩余时间成功！提问id：[{}]",resultMap.get("twid"));
                }
                //响应200
                return ResponseEntity.status(HttpStatus.OK).body(resultMap);
            }

        }catch (Exception e){
            //日志
            logger.error("学生获取课堂提问题目数据出现未知错误！");
            //响应500
            mm.put("massage","服务器端出现未未知错误");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mm);
        }
    }

    /**
     * 学生提交答案
     * @param answerData 学生提交的答案
     * @return map集合，json格式提示信息
     */
    @RequestMapping(value = "stuputanswer",method = RequestMethod.PUT,produces = "application/json;charset=utf-8")
    public ResponseEntity<Map<String,String>> stuPutAnswer(@RequestBody AnswerData answerData){
        //定义一个map集合，用于返回提示信息
        Map<String,String> mm=new HashMap<>();

        try {
            //1.提交答案
            boolean isPutTrue = tiWenService.stuPutAnswer(answerData);

            //2.判断
            if (isPutTrue){
                //2.1提交成功
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("学生：[{}]提交答案成功！",answerData.getXh()+","+answerData.getXm());
                }
                //响应200
                mm.put("massage","答案提交成功！");
                return ResponseEntity.status(HttpStatus.OK).body(mm);
            }else {
                //2.2提交失败
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("学生：[{}]提交答案失败！",answerData.getXh()+","+answerData.getXm());
                }
                //响应409
                mm.put("massage","答案提交失败！");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(mm);
            }
        }catch (Exception e){
            //日志
            logger.error("学生：[{}]提交答案出现未知错误！",answerData.getXh()+","+answerData.getXm());
            //响应500
            mm.put("massage","服务器出现未知错误！");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mm);
        }
    }

    /**
     * 教师获取提问结果
     * @param twid 提问id
     * @return TiWenResult
     */
    @RequestMapping(value = "teagettwresult",method = RequestMethod.GET,produces="application/json;charset=utf-8")
    public ResponseEntity<TiWenResult> teaGetResult(@RequestParam("twid") String twid){
        try {
            //1.获取提问结果
            TiWenResult tiWenResult = tiWenService.teaGetResult(twid);
            //日志
            if (logger.isInfoEnabled()){
                logger.info("教师获取提问结果成功。提问id:[{}]",twid);
            }
            //响应200
            return ResponseEntity.status(HttpStatus.OK).body(tiWenResult);
        }catch (Exception e){
            //日志
            logger.error("获取提问结果出现未知错误！");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
