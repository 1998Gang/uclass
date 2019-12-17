package cqupt.jyxxh.uclass.service;


import cqupt.jyxxh.uclass.pojo.KeChengInfo;
import cqupt.jyxxh.uclass.utils.GetDataFromJWZX;
import cqupt.jyxxh.uclass.utils.Parse;
import cqupt.jyxxh.uclass.utils.SendHttpRquest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * 获取用户课表
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 19:08 2019/12/2
 */

@Service
public class KebiaoService {

    @Autowired
    private GetDataFromJWZX getDataFromJWZX;    //去教务在线获取数据的工具类

    @Value("${URLStuKebiaoFromJWZX}")
    private String URL_STUKEBIAO_FROM_JWZX;       //从教务在线获取学生课表的URL

    @Value("${URLTeaKebiaoFromJWZX}")
    private String URL_TEAKEBIAO_FROM_JWZX;       //从教务在线获取教师课表URL


    /**
     *  根据学号或者教师号获取课表
     *
     * @param number 学号或者教师号
     * @param type  类别，"s"代表学生，"t"代表老师
     * @return ArrayList<ArrayList<ArrayList<KeChengInfo>>>
     */
    public ArrayList<ArrayList<ArrayList<KeChengInfo>>> getKebiao(String number, String type) {

        //课表 list嵌套
        ArrayList<ArrayList<ArrayList<KeChengInfo>>> kebiao=null;

        //1.根据学号或者教师号去获取教务在线的课表页（html）
        switch (type){
            //学生
            case "s":{
                kebiao=getDataFromJWZX.getStukebiaoByXh(number);
                break;
            }
            //老师
            case "t":{
                kebiao=getDataFromJWZX.getTeaKebiaoByTeaId(number);
                break;
            }

        }

        return kebiao;
    }

    /**
     * 获取教务时间，匹配课表
     * @return Map，教务时间
     */
    public Map<String,String> getSchoolTime(){

        return getDataFromJWZX.getSchoolTime();
    }


}
