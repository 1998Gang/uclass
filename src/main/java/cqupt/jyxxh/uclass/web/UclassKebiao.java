package cqupt.jyxxh.uclass.web;

import cqupt.jyxxh.uclass.pojo.KeChengInfo;
import cqupt.jyxxh.uclass.pojo.UclassUser;
import cqupt.jyxxh.uclass.service.KebiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Map;

/**
 * 根据教师号或者学号获取课表数据
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 21:47 2019/11/28
 */
@Controller
public class UclassKebiao {

    private Logger logger= LoggerFactory.getLogger(UclassKebiao.class);

    @Autowired
    private KebiaoService kebiaoService;     //获取课表课表



    /**
     * 获取教师的完整课表，
     * @param  teaId 教师号
     * @return  以json数组形式返回完整的课表信息
     */
    @RequestMapping(value = "teakebiao",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<String> getTeaKebiao(@RequestParam("teaId") String teaId){

        try {
            //1.获取课表
            String teaKebiao = kebiaoService.getKebiao(teaId, "t");

            //日志
            if (logger.isInfoEnabled()){
                logger.info("获取教师课表成功！教师号：[{}]",teaId);
            }

            // 2.获取成功，响应200.
            return ResponseEntity.status(HttpStatus.OK).body(teaKebiao);
        }catch (Exception ignored){

        }
        // 服务器端未知错误
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 获取学生的完整课表
     * @param xh 学号
     * @return 以json数组形式返回完整课表信息
     */
    @RequestMapping(value = "stukebiao",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<String> getStuKebiao(@RequestParam("xh") String xh){

        try {
            // 1.获取课表
            String stuKebiao = kebiaoService.getKebiao(xh,"s");


            //日志
            if (logger.isInfoEnabled()){
                logger.info("获取学生课表成功！学号：[{}]",xh);
            }

            // 2.获取成功，响应200.
            return ResponseEntity.status(HttpStatus.OK).body(stuKebiao);
        }catch (Exception ignored){

        }


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 获取教务时间
     * @return 时间map集合{"学期":"1","年":"2019","学年":"2019-2020","日":"16","周":"16","月":"12","星期":"1"}
     */
    @RequestMapping(value = "schooltime",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<Map<String,String>> SchoolTime(){
        Map<String,String> sTime=null;
        try {
            // 1.获取教务时间
             sTime = kebiaoService.getSchoolTime();
             // 2.获取成功，响应200.
             return ResponseEntity.status(HttpStatus.OK).body(sTime);

        }catch (Exception e){
            logger.error("【课表接口（SchoolTime）】教务时间获取失败！");
        }

        // 未知错误响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

}
