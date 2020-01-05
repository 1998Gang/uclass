package cqupt.jyxxh.uclass.web;

import cqupt.jyxxh.uclass.pojo.KbStuListData;
import cqupt.jyxxh.uclass.service.ComponentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * u课堂小程序一些小功能组件的接口
 * 1.获取教务时间（SchoolTime）
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 15:25 2020/1/3
 */

@Controller
public class UclassComponents {

    Logger logger=LoggerFactory.getLogger(UclassComponents.class);

    @Autowired
    private ComponentService componentService;     //u课堂一些小组件的service


    /**
     * 获取教务时间
     * @return 时间map集合{"学期":"1","年":"2019","学年":"2019-2020","日":"16","周":"16","月":"12","星期":"1"}
     */
    @RequestMapping(value = "schooltime",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<String> SchoolTime(){
        String sTime=null;
        try {
            // 1.获取教务时间
            sTime = componentService.getSchoolTime();
            // 2.判断sTime是否为空
            if (null==sTime){
                //日志
                logger.info("获取教务时间失败！为空！");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("获取失败！");
            }
            // 2.获取成功，响应200.
            //日志
            if (logger.isInfoEnabled()){
                logger.info("获取教务时间成功！");
            }
            return ResponseEntity.status(HttpStatus.OK).body(sTime);

        }catch (Exception e){
            logger.error("【课表接口（SchoolTime）】教务时间获取失败！");
        }

        // 未知错误响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器端出现未知错误！");
    }

    /**
     * 获取上课学生名单
     * @return 名单的json数据
     */
    @RequestMapping(value = "kbstuist",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<String> kbStuList(@RequestParam("jxb")String jxb){


        try{
            //获取上课学生名单数据。string
            String data = componentService.getKbStuListData(jxb);

            //判断数据是否为空
            if (null==data){
                //为空，响应404
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("学生名单数据获取失败！为空！教学班：[{}]",jxb);
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("资源未找到");
            }

            //数据正常，响应200
            //日志
            if (logger.isInfoEnabled()){
                logger.info("学生名单数据获取成功！教学班:[{}]",jxb);
            }
            return ResponseEntity.status(HttpStatus.OK).body(data);


        }catch (Exception e){
            logger.error("【学生名单获取（UclassComponents.kbStuList）】出现未知错误！教学班：[{}]",jxb);
        }

        //未知错误响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器端出现未知错误！");
    }
}