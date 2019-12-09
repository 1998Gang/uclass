package cqupt.jyxxh.uclass.service;


import cqupt.jyxxh.uclass.pojo.KebiaoInfo;
import cqupt.jyxxh.uclass.utils.Parse;
import cqupt.jyxxh.uclass.utils.SendHttpRquest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 获取用户课表
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 19:08 2019/12/2
 */

@Service
public class KebiaoService {

    private final SendHttpRquest sendHttpRquest;


    @Value("${URLStuKebiaoFromJWZX}")
    private String URL_STUKEBIAO_FROM_JWZX;       //从教务在线获取学生课表的URL

    @Autowired
    public KebiaoService(SendHttpRquest sendHttpRquest) {
        this.sendHttpRquest = sendHttpRquest;
    }

    /**
     *
     * @param xh 学生学号
     * @return 返回课表信息list集合
     * @throws IOException getHtmlFromHttp的异常
     */
    public List<KebiaoInfo>getStuKebiao(String xh) throws IOException {
        List<KebiaoInfo> kebiaoInfoList;
        //1.根据学号去获取教务在线的课表页（html）
        String stuKebiaoHtml = sendHttpRquest.getHtmlFromHttp(URL_STUKEBIAO_FROM_JWZX, "xh=" + xh);
        //2.解析返回的课表页（html）
        kebiaoInfoList = Parse.parseHtmlToStuKebiaoInfo(stuKebiaoHtml);

        return kebiaoInfoList;
    }
}
