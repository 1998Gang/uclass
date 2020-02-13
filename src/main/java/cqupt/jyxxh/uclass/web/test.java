package cqupt.jyxxh.uclass.web;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 20:08 2019/12/9
 */

@Controller
public class test {
    @RequestMapping(value = "test",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<String> testweb(@RequestParam("test")String test){

        return ResponseEntity.status(HttpStatus.OK).body(test);
    }

    @RequestMapping(value = "testlist",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<List<String>> testlist(){

        List<String> list=new ArrayList<>();
        list.add("a080");
        list.add("09809");

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
