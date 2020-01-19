package cqupt.jyxxh.uclass.web;

import cqupt.jyxxh.uclass.pojo.qiandao.KcQianDaoHistory;
import cqupt.jyxxh.uclass.pojo.qiandao.QianDaoResult;
import cqupt.jyxxh.uclass.pojo.qiandao.StuQianDaoHistory;
import cqupt.jyxxh.uclass.service.QianDaoService;
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
 * 签到点名功能
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 1:10 2020/1/6
 */
@Controller
public class UclassQianDao {

    Logger logger = LoggerFactory.getLogger(UclassQianDao.class);


    @Autowired
    private QianDaoService qianDaoService;           //签到功能的具体逻辑类

    /**
     * 教师发起签到
     *
     * @return ResponseEntity
     */
    @RequestMapping(value = "originateqiandao", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ResponseEntity<Map<String,String>> originateQanDao(@RequestBody Map<String, String> param) {

        //装载返回数据
        Map<String, String> massage = new HashMap<>();

        try {
            String qddate = qianDaoService.teaCreatQiandao(param);
            if ("false".equals(qddate)) {
                //签到失败，响应417
                massage.put("massage", "发起失败!当前有签到正在进行!");
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(massage);
            }
            if ("jxb error".equals(qddate)) {
                //发起签到失败，响应400
                massage.put("massage", "参数错误，可能是无效的教学班号");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(massage);
            }
            //日志
            if (logger.isInfoEnabled()) {
                logger.info("发起签到成功！教学班：[{}],时间:[{}]", param.get("jxb"), param.get("week") + "周_星期" + param.get("work_day"));
            }
            //发起成功响应200
            massage.put("massage", "发起签到成功!");
            massage.put("qdid", qddate);
            return ResponseEntity.status(HttpStatus.OK).body(massage);
        } catch (Exception e) {
            logger.error("发起签到接口，发生未知错误");
            //异常错误！响应500
            massage.put("massage", "服务器端发生未知错误！");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(massage);
        }
    }

    /**
     * 教师获取某堂课某一次点名结果
     *
     * @param qdid 签到id 一次签到的唯一码
     * @return QianDaoResult
     */
    @RequestMapping(value = "teaqiandaoresult", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ResponseEntity<QianDaoResult> teaGetQDResult(@RequestParam("qdid") String qdid) {
        try {
            // 1.获取点名结果
            QianDaoResult qianDaoResult = qianDaoService.getQianDaoResultOneTimes(qdid);
            // 2.获取成功，返回！响应200
            //日志
            if (logger.isInfoEnabled()) {
                logger.info("教师获取点名结果成功！签到id：[{}]", qdid);
            }
            return ResponseEntity.status(HttpStatus.OK).body(qianDaoResult);
        } catch (Exception e) {
            //日志
            logger.error("教师获取点名结果出现未知错误！错误信息：[{}]", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 教师给学生补签
     *
     * @param param map集合 {"jxb":"教学班号","week":"上课周","work_day":"星期几","qdcs":"签到次数（针对这节课发起的第几次签到）","xh":"补签学生学号"}
     * @return 响应信息
     */
    @RequestMapping(value = "tearetroactive", method = RequestMethod.PUT, produces = "application/json;charset=utf-8")
    public ResponseEntity<Map<String,String>> teaRetroactive(@RequestBody Map<String, String> param) {

        //响应信息
        Map<String, String> massage = new HashMap<>();

        try {
            // 补签操作
            boolean b = qianDaoService.teaRetroactive(param);
            if (b) {
                //补签成功！响应200
                //日志
                if (logger.isInfoEnabled()) {
                    logger.info("补签成功！补签类型：[{}]  学号：[{}]，签到id：[{}]", param.get("bqlx"), param.get("xh"), param.get("qdid"));
                }
                massage.put("massage", "补签成功！");
                return ResponseEntity.status(HttpStatus.OK).body(massage);
            } else {
                // 补签失败！响应409
                //日志
                if (logger.isInfoEnabled()) {
                    logger.info("补签失败！学号：[{}]，签到id：[{}]", param.get("xh"), param.get("qdid"));
                }
                massage.put("massage", "补签失败！");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(massage);
            }
        } catch (Exception e) {
            //日志
            logger.error("补签接口发生未知错误！错误信息：[{}]", e.getMessage());
            //响应500
            massage.put("massage", "服务器内部错误!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(massage);
        }
    }

    /**
     * 教师根据教学班获取该班的历史签到数据
     * @param jxb 教学班号
     * @return KcQianDaoResult
     */
    @RequestMapping(value = "teaqiandaohistroy",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<KcQianDaoHistory> teaGetKcQdHistory(@RequestParam("jxb")String jxb){
        try{
            //调用service层方法
            KcQianDaoHistory kcQDhistory = qianDaoService.getKcQDhistory(jxb);
            //日志
            if (logger.isInfoEnabled()){
                logger.info("教师获取教学班：[{}]的历史点名数据成功！",jxb);
            }
            //获取成功返回数据，响应200
            return ResponseEntity.status(HttpStatus.OK).body(kcQDhistory);
        }catch (Exception e){
            //日志
            logger.error("教师获取教学班：[{}]的历史数据出现未知错误！错误信息：[{}]",jxb,e.getMessage());
        }
        //出错，响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }


    /**
     * 学生获取签到剩余时间和签到id
     *
     * @param jxb      教学班
     * @param week     周
     * @param work_day 星期几
     * @return Map {"timeremaining":"102(签到剩余时间)","qdid":"签到id","yxsj":"这次签到的有效时间"}
     */
    @RequestMapping(value = "stugetqiandaotimeremaining", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ResponseEntity<Map<String,String>> stuGetqiandaoTimeRemain(@RequestParam("jxb") String jxb, @RequestParam("week") String week, @RequestParam("work_day") String work_day) {

        //响应信息
        Map<String, String> massage = new HashMap<>();

        try {

            // 1.调用service方法
            Map<String, String> qiandaoreaminTime = qianDaoService.getQiandaoreaminTime(jxb, week, work_day);
            // 2.如果结果是一个空map说明获取失败
            if (qiandaoreaminTime.size() == 0) {
                //日志
                if (logger.isInfoEnabled()) {
                    logger.info("获取签到码剩余时间失败！可能当前并无签到进行！教学班：[{}] 时间：周[{}]xq[{}]", jxb, week, work_day);
                }
                //响应403
                massage.put("massage", "当前无签到，或签到时间已过");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(massage);
            }
            //3.获取成功响应200
            //日志
            if (logger.isInfoEnabled()) {
                logger.info("获取签到码剩余时间成功！教学班：[{}] 时间：周[{}]星期[{}]", jxb, week, work_day);
            }
            return ResponseEntity.status(HttpStatus.OK).body(qiandaoreaminTime);

        } catch (Exception e) {
            massage.put("massage", "服务器端未知错误！");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(massage);
        }
    }

    /**
     * 学生签到
     *
     * @param param 签到参数 {"qdid":"签到id","qdm":"签到码","xh":"学号"}
     * @return map集合 {"massage":""}
     */
    @RequestMapping(value = "stuqiandao", method = RequestMethod.PUT, produces = "application/json;charset=utf-8")
    public ResponseEntity<Map<String,String>> stuQianDao(@RequestBody Map<String, String> param) {

        //响应信息
        Map<String, String> massage = new HashMap<>();

        try {
            //1.签到
            boolean b = qianDaoService.stuQiandao(param);
            //2.签到不成功
            if (!b) {
                //日志
                if (logger.isInfoEnabled()) {
                    logger.info("学生：[{}] 签到失败!", param.get("xh"));
                }
                //响应409
                massage.put("massage", "签到失败");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(massage);
            }
            //3.签到成功
            //日志
            if (logger.isInfoEnabled()) {
                logger.info("学生：[{}]签到成功！", param.get("xh"));
            }
            //响应200
            massage.put("massage", "签到成功！");
            return ResponseEntity.status(HttpStatus.OK).body(massage);
        } catch (Exception e) {
            //日志
            logger.error("学生签到接口出现未知错误！错误信息：[{}]", e.getMessage());
        }
        massage.put("massage", "服务前端未知错误！");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(massage);
    }

    /**
     * 学生获取自己某一门课的历史签到数据
     *
     * @param xh 学号
     * @param jxb 教学班号
     * @return StuQianDaoResult
     */
    @RequestMapping(value = "stuqiandaohistory", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ResponseEntity<StuQianDaoHistory> stuGetQDhistory(@RequestParam("xh")String xh, @RequestParam("jxb")String jxb) {
        try {
            StuQianDaoHistory stuQDhistory = qianDaoService.getStuQDhistory(xh, jxb);
            //日志
            if (logger.isInfoEnabled()) {
                logger.info("学生[{}]获取教学班[{}]历史签到记录成功！", xh, jxb);
            }
            //获取成功，响应200，并返回数据
            return ResponseEntity.status(HttpStatus.OK).body(stuQDhistory);
        } catch (Exception e) {
            //日志
            logger.error("学生[{}]获取教学班[{}]历史签到记录出现未知错误！错误信息：[{}]", xh, jxb, e.getCause());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

}
