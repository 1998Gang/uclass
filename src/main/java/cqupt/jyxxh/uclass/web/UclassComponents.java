package cqupt.jyxxh.uclass.web;

import cqupt.jyxxh.uclass.pojo.KbStuListData;
import cqupt.jyxxh.uclass.pojo.SchoolTime;
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
    public ResponseEntity<SchoolTime> SchoolTime(){
        SchoolTime schoolTime;
        try {
            // 1.获取教务时间
            schoolTime = componentService.getSchoolTime();
            // 2.判断schoolTime是否为空
            if (null==schoolTime){
                //日志
                logger.info("获取教务时间失败！为空！");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            // 2.获取成功，响应200.
            //日志
            if (logger.isInfoEnabled()){
                logger.info("获取教务时间成功！");
            }
            return ResponseEntity.status(HttpStatus.OK).body(schoolTime);

        }catch (Exception e){
            //日志
            logger.error("【课表接口（SchoolTime）】教务时间获取失败！");
        }

        // 未知错误响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 获取上课学生名单
     * @return 名单的json数据
     */
    @RequestMapping(value = "kbstulist",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<KbStuListData> kbStuList(@RequestParam("jxb")String jxb){


        try{
            //获取上课学生名单数据。string
            KbStuListData data = componentService.getKbStuListData(jxb);

            //判断数据是否为空
            if (null==data){
                //为空，响应415
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("学生名单数据获取失败！为空！教学班：[{}]",jxb);
                }
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
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
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
