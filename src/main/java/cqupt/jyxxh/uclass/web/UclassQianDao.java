package cqupt.jyxxh.uclass.web;

import checkers.units.quals.A;
import cqupt.jyxxh.uclass.pojo.QianDaoResult;
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
import java.util.List;
import java.util.Map;

/**
 * 签到点名功能
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 1:10 2020/1/6
 */
@Controller
public class UclassQianDao {

    Logger logger= LoggerFactory.getLogger(UclassQianDao.class);


    @Autowired
    private QianDaoService qianDaoService;           //签到功能的具体逻辑类

    /**
     * 教师发起签到
     * @return ResponseEntity
     */
    @RequestMapping(value = "originateqiandao",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public ResponseEntity<Map> originateQanDao(@RequestBody Map<String,String> param){

        //装载返回数据
        Map<String,String> massage=new HashMap<>();

        try{
            boolean isTrue = qianDaoService.teaCreatQiandao(param);
            if (!isTrue){
                //签到失败，响应417
                massage.put("massage","发起失败!当前有签到正在进行!");
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(massage);
            }
            //日志
            if (logger.isInfoEnabled()){
                logger.info("发起签到成功！教学班：[{}],时间:[{}]",param.get("jxb"),param.get("week")+"周_星期"+param.get("work_day"));
            }
            //发起成功响应200
            massage.put("massage","发起签到成功!");
            return ResponseEntity.status(HttpStatus.OK).body(massage);
        }catch (Exception e){
            logger.error("发起签到接口，发生未知错误");
            //异常错误！响应500
            massage.put("massage","服务器端发生未知错误！");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(massage);
        }
    }

    /**
     * 教师获取点名结果
     * @param jxb 教学班
     * @param week 上课周数
     * @param work_day 星期几
     * @param qdcs 签到次数
     * @return List<map<String,String>> 未签到学生名单  [{"xm":"姓名"，"xh"："学号","bj":"班级"},{....}]
     */
    @RequestMapping(value = "teaqiandaoresult",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<QianDaoResult> teaGetQDResult(@RequestParam("jxb") String jxb, @RequestParam("week")String week, @RequestParam("work_day")String work_day, @RequestParam("qdcs")String qdcs){

        QianDaoResult qianDaoResult = qianDaoService.teaGetQianDaoResult(jxb, week, work_day, qdcs);
        return ResponseEntity.status(HttpStatus.OK).body(qianDaoResult);

    }

}
