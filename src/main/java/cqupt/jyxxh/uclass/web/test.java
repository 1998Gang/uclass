package cqupt.jyxxh.uclass.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
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
    @RequestMapping(value = "test",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<List<Map<String,String>>> testweb(){

        Map<String,String> massage=new HashMap<>();
        massage.put("name","pyg");
        massage.put("xh","2017214033");
        System.out.println("访问成功");

        Map<String,String> massage1=new HashMap<>();
        massage1.put("name","tcd");
        massage1.put("xh","13423");

        List<Map<String,String>> list=new ArrayList<>();
        list.add(massage);
        list.add(massage1);

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @RequestMapping(value = "testlist",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<List<String>> testlist(){



        List<String> list=new ArrayList<>();
        list.add("a080");
        list.add("09809");


        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
