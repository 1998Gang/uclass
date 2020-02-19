package cqupt.jyxxh.uclass.web;


import cqupt.jyxxh.uclass.dao.TiWenMapper;
import cqupt.jyxxh.uclass.pojo.qiandao.StuQianDaoHistory;
import cqupt.jyxxh.uclass.pojo.tiwen.StuTWRecord;
import cqupt.jyxxh.uclass.pojo.tiwen.StuTiWenHistory;
import cqupt.jyxxh.uclass.service.QianDaoService;
import cqupt.jyxxh.uclass.service.TiWenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 20:08 2019/12/9
 */

@Controller
public class test {

    @Autowired
    TiWenMapper tiWenMapper;

    @Autowired
    TiWenService tiWenService;

    @Autowired
    QianDaoService qianDaoService;

    @RequestMapping(value = "test",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<String> testweb(@RequestParam("test")String test){
        tiWenService.persistentTiWenData();
        qianDaoService.persistentQianDaoData();
        return ResponseEntity.status(HttpStatus.OK).body(test);
    }

    @RequestMapping(value = "testlist",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<StuTiWenHistory> testlist(@RequestParam("xh") String xh, @RequestParam("jxb") String jxb){

        StuTiWenHistory stuTWHistory = tiWenService.getStuTWHistory(xh, jxb);

        return ResponseEntity.status(HttpStatus.OK).body(stuTWHistory);
    }
}
