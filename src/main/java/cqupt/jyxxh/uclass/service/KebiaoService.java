package cqupt.jyxxh.uclass.service;


import cqupt.jyxxh.uclass.pojo.keChengInfo;
import cqupt.jyxxh.uclass.utils.Parse;
import cqupt.jyxxh.uclass.utils.SendHttpRquest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

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
    private  SendHttpRquest sendHttpRquest;


    @Value("${URLStuKebiaoFromJWZX}")
    private String URL_STUKEBIAO_FROM_JWZX;       //从教务在线获取学生课表的URL


    /**
     *
     * @param number 学号或者教师号
     * @param type  类别，"s"代表学生，"t"代表老师
     * @return
     */
    public ArrayList<ArrayList<ArrayList<keChengInfo>>> getKebiao(String number, String type)  {

        //1.根据学号去获取教务在线的课表页（html）
        String stuKebiaoHtml = null;
        try {
            stuKebiaoHtml = sendHttpRquest.getHtmlFromHttp(URL_STUKEBIAO_FROM_JWZX, "xh=" + number);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //2.将获取的课表页解析成为一个 keChengInfo[][][]数组
        ArrayList<ArrayList<ArrayList<keChengInfo>>> kebiaoList = Parse.parseHtmlToKebiaoInfo(stuKebiaoHtml,type);

        return kebiaoList;
    }


}
