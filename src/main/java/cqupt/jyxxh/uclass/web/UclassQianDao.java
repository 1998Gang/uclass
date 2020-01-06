package cqupt.jyxxh.uclass.web;

import checkers.units.quals.A;
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
     * @return
     */
    @RequestMapping(value = "originateqiandao",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public ResponseEntity<String> originateQanDao(@RequestBody Map<String,String> param){

        try{
            boolean isTrue = qianDaoService.teaCreatQiandao(param);
            if (!isTrue){
                //签到失败，响应417
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("发起失败!当前有签到正在进行");
            }
            //日志
            if (logger.isInfoEnabled()){
                logger.info("发起签到成功！教学班：[{}],时间:[{}]",param.get("jxb"),param.get("week")+"周_星期"+param.get("work_day"));
            }
            //发起成功响应200
            return ResponseEntity.status(HttpStatus.OK).body("发起签到成功");
        }catch (Exception e){
            logger.error("发起签到接口，发生未知错误");
            //异常错误！响应500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器端发生未知错误！");
        }
    }

}
