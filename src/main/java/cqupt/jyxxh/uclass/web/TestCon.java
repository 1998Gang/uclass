package cqupt.jyxxh.uclass.web;


import checkers.units.quals.A;
import cqupt.jyxxh.uclass.dao.TiWenMapper;
import cqupt.jyxxh.uclass.service.DelayEnduranceToMysqlService;
import cqupt.jyxxh.uclass.service.QianDaoService;
import cqupt.jyxxh.uclass.service.TiWenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.TimeUnit;


/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 20:08 2019/12/9
 */

@Controller
public class TestCon {



    @Autowired
    private    TiWenMapper tiWenMapper;

    @Autowired
    private TiWenService tiWenService;

    @Autowired
    private QianDaoService qianDaoService;


    /**
     * 用于测试连接的
     * @param test 参数 任意
     * @return test返回test
     */
    @RequestMapping(value = "test",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<Integer> testweb(@RequestParam("test")int test){

        return ResponseEntity.status(HttpStatus.OK).body(test);
    }
}
