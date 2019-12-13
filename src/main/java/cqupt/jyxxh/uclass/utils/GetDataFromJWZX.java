package cqupt.jyxxh.uclass.utils;

import cqupt.jyxxh.uclass.pojo.EduAccount;
import cqupt.jyxxh.uclass.pojo.KeChengInfo;
import cqupt.jyxxh.uclass.pojo.Student;
import cqupt.jyxxh.uclass.pojo.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.directory.Attributes;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 从教务在线获取数据的工具类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:39 2019/12/13
 */

@Component
public class GetDataFromJWZX {

    @Autowired
    private Authentication authentication;           //统一身份认证相关操作工具类

    @Value("${URLTeaInfoFromJWZX}")
    private  String URL_TEAINFO_FROM_JWZX;            //去教务在线请求教师信息的URL

    @Value("${URLStuInfoFromJWZX}")
    private  String URL_StuInfo_From_JWZX;            //去教务在线请求学生信息的URL

    @Value("${URLStuKebiaoFromJWZX}")
    private String URL_STUKEBIAO_FROM_JWZX;       //从教务在线获取学生课表的URL

    @Value("${URLTeaKebiaoFromJWZX}")
    private String URL_TEAKEBIAO_FROM_JWZX;       //从教务在线获取教师课表URL



    /**
     * 根据学号去教务在线获取学生数据
     * @param xh 学号
     * @return EduAccount
     */
    public  Student getStudentInfoByXh(String xh){


        // 1.访问教务在线url获取返回的数据
        String studentJson = SendHttpRquest.getJsonfromhttp(URL_StuInfo_From_JWZX, "searchKey=" + xh);

        return Parse.ParseJsonToStudent(studentJson);

    }

    /**
     * 根据教师号去教务在线获取教师数据
     * @param teaId 教师号
     * @return EduAccount
     */
    public Teacher getTeacherInfoByTeaId(String teaId){
        Teacher teacher=null;
        // 1.访问教务在线url获取教师的json数据
        String teacherInfo = SendHttpRquest.getJsonfromhttp(URL_TEAINFO_FROM_JWZX, "searchKey=" + teaId);
        // 2.解析json数据获取教师实体（返回的是List集合，但此处因为是用教师号进行的查询，所有只有一个教师数据）
        List<Teacher> teachers= Parse.ParseJsonToTeacher(teacherInfo);
        // 3.取出该教师数据,以EduAccount形式返回
        for (Teacher tea:teachers){
            teacher=tea;
        }

        return teacher;
    }

    /**
     * 通过统一身份去教务在线获取教师信息
     * @param ykth 一卡通号
     * @param password 密码
     * @return Teacher
     */
    public Teacher getTeacherInfoByTYSH(String ykth,String password){

        Teacher teacher=null;

        // 1.验证统一身份，并相关数据(LDAP)
        Attributes attributes = authentication.getAttributes(ykth, password);

        // 2.使用工具类 Parse 解析 attributes
        HashMap<String, String> att = Parse.ParseAttributes(attributes);

        // 3.获取教师姓名、教师的所属学院（LDAP返回的数据）
        String jsxm = att.get("cn");
        String yxm=att.get("edupersonorgdn");

        // 4.更改教师姓名的编码格式(中文更改编码格式，否则发起请求是乱码，查不到数据)
        jsxm = URLEncoder.encode(jsxm, Charset.forName("utf-8"));

        // 5. 以教师姓名为参数去教务在线查询教师的json数据
        String teaJsonInfo = SendHttpRquest.getJsonfromhttp(URL_TEAINFO_FROM_JWZX,"searchKey="+jsxm);

        // 6.将josn数据解析为Teacher对象（用姓名查教师数据，可能存在多个同名教师）
        List<Teacher> teachers = Parse.ParseJsonToTeacher(teaJsonInfo);

        // 7.筛选正确的老师
        /*通过姓名查询，会查询出较多同名教师。
                    但是这些同名教师的所属学院大概率不一致，当然这也不能百分百保证能正确筛选。
                    实在没有办法，目前只能用这样的方式去确定老师。
                    所有此处采用去对比学院名的方式，来确定该教务账户到底是哪一个老师。*/
        for (Teacher tea:teachers){
            if (Parse.isbaohan(yxm, tea.getYxm())){
                //确定之后，添加一卡通号。
                tea.setYkth(ykth);
                teacher=tea;
            }
        }

        return teacher;
    }

    /**
     * 通过统一身份去教务在线获取学生数据
     * @param ykth  一卡通号
     * @param password 密码
     * @return Student
     */
    public Student getStudentInfoByTYSH(String ykth,String password){
        Student student;

        // 1.验证统一身份，并相关数据(LDAP)
        Attributes attributes = authentication.getAttributes(ykth, password);

        // 2.使用工具类 Parse 解析 attributes
        HashMap<String, String> attributeHashMap = Parse.ParseAttributes(attributes);

        // 3.获取学生学号（LDAP）
        String xh=attributeHashMap.get("edupersonstudentid");

        // 4.通过学号获取学生数据,并添加一卡通号。
        student = getStudentInfoByXh(xh);
        student.setYkth(ykth);

        return student;
    }


    /**
     * 通过学号获取课表
     * @param xh 学号
     * @return ArrayList<ArrayList<ArrayList<KeChengInfo>>> 课表数组
     */
    public ArrayList<ArrayList<ArrayList<KeChengInfo>>> getStukebiaoByXh(String xh){
        String stuKebiaoHtml=null;

        //发起http请求，获取教务在线的课表ktml代码
        try {
             stuKebiaoHtml= SendHttpRquest.getHtmlFromHttp(URL_STUKEBIAO_FROM_JWZX, "xh=" + xh);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //解析并返回
        return Parse.parseHtmlToKebiaoInfo(stuKebiaoHtml, "s");
    }


    /**
     * 通过教师号获取教师课表
     * @param teaId 教师号
     * @return ArrayList<ArrayList<ArrayList<KeChengInfo>>> 课表数组
     */
    public ArrayList<ArrayList<ArrayList<KeChengInfo>>> getTeaKebiaoByTeaId(String teaId){
        String teaKebiaoHtml=null;

        //发起http请求，获取教务在线的课表ktml代码
        try {
            teaKebiaoHtml= SendHttpRquest.getHtmlFromHttp(URL_TEAKEBIAO_FROM_JWZX, "teaId=" + teaId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //解析并返回
        return Parse.parseHtmlToKebiaoInfo(teaKebiaoHtml,"t");
    }
}
