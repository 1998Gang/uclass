package cqupt.jyxxh.uclass.web;

import cqupt.jyxxh.uclass.pojo.KebiaoInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 根据教师号或者学号获取课表数据
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 21:47 2019/11/28
 */
@Controller
public class UclassKebiao {
    /**
     * 获取教师的完整课表，
     * @param  teaId 教师号
     * @return  以json数组形式返回完整的课表信息
     */
    @RequestMapping(value = "teakebiao",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<List<KebiaoInfo>> getTeaKebiao(@RequestParam("teaId") String teaId){

        return ResponseEntity.ok(null);
    }

    /**
     * 获取学生的完整课表
     * @param xh 学号
     * @return 以json数组形式返回完整课表信息
     */
    @RequestMapping(value = "stukebiao",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<KebiaoInfo> getStuKebiao(@RequestParam("xh") String xh){

        return ResponseEntity.ok(null);
    }

}
