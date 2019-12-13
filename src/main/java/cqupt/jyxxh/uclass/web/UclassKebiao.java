package cqupt.jyxxh.uclass.web;

import cqupt.jyxxh.uclass.pojo.KeChengInfo;
import cqupt.jyxxh.uclass.service.KebiaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

/**
 * 根据教师号或者学号获取课表数据
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 21:47 2019/11/28
 */
@Controller
public class UclassKebiao {

    @Autowired
    private KebiaoService kebiaoService;     //获取课表课表



    /**
     * 获取教师的完整课表，
     * @param  teaId 教师号
     * @return  以json数组形式返回完整的课表信息
     */
    @RequestMapping(value = "teakebiao",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<ArrayList<ArrayList<ArrayList<KeChengInfo>>>> getTeaKebiao(@RequestParam("teaId") String teaId){

        ArrayList<ArrayList<ArrayList<KeChengInfo>>> teaKebiao = kebiaoService.getKebiao(teaId, "t");
        return ResponseEntity.ok(teaKebiao);
    }

    /**
     * 获取学生的完整课表
     * @param xh 学号
     * @return 以json数组形式返回完整课表信息
     */
    @RequestMapping(value = "stukebiao",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<ArrayList<ArrayList<ArrayList<KeChengInfo>>>> getStuKebiao(@RequestParam("xh") String xh){

        ArrayList<ArrayList<ArrayList<KeChengInfo>>> stuKebiao = kebiaoService.getKebiao(xh,"s");

        return ResponseEntity.ok(stuKebiao);
    }

}
