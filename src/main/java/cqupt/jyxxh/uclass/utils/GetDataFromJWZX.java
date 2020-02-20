package cqupt.jyxxh.uclass.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.pojo.*;
import cqupt.jyxxh.uclass.pojo.user.Student;
import cqupt.jyxxh.uclass.pojo.user.Teacher;
import cqupt.jyxxh.uclass.service.EduAccountService;
import cqupt.jyxxh.uclass.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 从教务在线获取数据的工具类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:39 2019/12/13
 */

@Component
public class GetDataFromJWZX {

    Logger logger= LoggerFactory.getLogger(GetDataFromJWZX.class);//日志

    @Autowired
    private Authentication authentication;           //统一身份认证相关操作工具类

    @Autowired
    private EduAccountService eduAccountService;     //教务账户操作类

    @Autowired
    private RedisService redisService;               //redis操作类

    @Autowired
    private SimulationLogin simulationLogin;         //模拟登陆教务在线的操作类



    @Value("${JWZX.URLTeaInfo}")
    private  String JWZX_URL_TEAINFO;            //去教务在线请求教师信息的URL

    @Value("${JWZX.URLStuInfo}")
    private  String JWZX_URL_StuInfo;            //去教务在线请求学生信息的URL

    @Value("${JWZX.URLStuKebiao}")
    private String JWZX_URL_STUKEBIAO;            //从教务在线获取学生课表的URL

    @Value("${JWZX.URLTeaKebiao}")
    private String JWZX_URL_TEAKEBIAO;            //从教务在线获取教师课表URL

    @Value("${JWZX.URLHome}")
    private String JWZX_URL_HOME;                            //教务在线首页地址

    @Value("${JWZX.URLStuSkjh}")
    private String JWZX_URL_STUSKJH;                //去教务在线获取学生课程成绩组成的URL

    @Value("${JWZX.URLKbStuList}")
    private String JWZX_URL_KESTULIST;              //去教务在线获取教学班学生名单的URL，参数 jxb=教学班号

    @Value("${JWZX.URLUser}")
    private String JWZX_URL_USER;                            //教务在线个人服务页

    /**
     * 根据学号去教务在线获取学生数据
     * @param xh 学号
     * @return EduAccount
     */
    public Student getStudentInfoByXh(String xh){


        // 1.访问教务在线url获取返回的数据
        String studentJson = SendHttpRquest.getJson(JWZX_URL_StuInfo, "searchKey=" + xh);

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
        String teacherInfo = SendHttpRquest.getJson(JWZX_URL_TEAINFO, "searchKey=" + teaId);
        // 2.解析json数据获取教师实体（返回的是List集合，但此处因为是用教师号进行的查询，所有只有一个教师数据）
        List<Teacher> teachers= Parse.ParseJsonToTeacher(teacherInfo);
        // 3.取出该教师数据,以EduAccount形式返回
        for (Teacher tea:teachers){
            teacher=tea;
        }
        return teacher;
    }



    /**
     * 通过统一身份去教务在线获取教师号
     * @param ykth 一卡通号
     * @param password 密码
     * @return Teacher
     */
    public Teacher getTeacherInfoByTYSH(String ykth,String password) throws Exception {

        Teacher teacherInfo;

        // 1.获取表示用户登陆教务在线后的cookie（phpsessid）用于后续获取数据。
        String phpsessid = simulationLogin.getPhpsessid(ykth, password);

        // 2.使用上一步获取的phpsessid去访问教务在线个人服务页，获取教师号。
        String htmlTea = SendHttpRquest.getHtmlWithCookie(JWZX_URL_USER, phpsessid);
        // 2.1 解析返回的html页面,获取教师号。

        String teaId = Parse.ParseHtmlToteaIdOrXh(htmlTea);

        // 3.通过教师号，再次请求教务在线，获取详细教师数据。
        teacherInfo = getTeacherInfoByTeaId(teaId);

        return teacherInfo;
    }

    /**
     * 通过统一身份去教务在线获取学生数据
     * @param ykth  一卡通号
     * @param password 密码
     * @return Student
     */
    public Student getStudentInfoByTYSH(String ykth,String password) throws Exception {
        Student studentInfo;

        //1.获取表示用户登陆教务在线后的cookie（PHPSESSID）用于后续获取数据。
        String phpsessid = simulationLogin.getPhpsessid(ykth, password);

        //2.使用获取的phpsessid去访问教务在线个人服务页（学生端），获取学号。
        String htmlStu = SendHttpRquest.getHtmlWithCookie(JWZX_URL_USER, phpsessid);
        // 2.1 解析返回的学习个人服务页，获取学号
        String xh = Parse.ParseHtmlToteaIdOrXh(htmlStu);

        // 3.通过学号,再次请求教务在线，获取学生详细数据。
        studentInfo = getStudentInfoByXh(xh);

        return studentInfo;
    }


    /**
     * 通过学号获取课表
     * @param xh 学号
     * @return ArrayList<ArrayList<ArrayList<KeChengInfo>>> 课表数组
     */
    public ArrayList<ArrayList<ArrayList<KeChengInfo>>> getStukebiaoByXh(String xh)  {

        // jackson操作对象
        ObjectMapper objectMapper = new ObjectMapper();


        // 2.发起http请求，获取教务在线的课表页的Html代码
        String stuKebiaoHtml=null;
        try {
             stuKebiaoHtml= SendHttpRquest.getHtmlWithParam(JWZX_URL_STUKEBIAO, "xh=" + xh);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //解析并返回，带参数”s“表示这个是学生的课表,第三个参数是成绩组成。
        return Parse.parseHtmlToKebiaoInfo(stuKebiaoHtml, "s");
    }
    /**
     * 通过教师号获取教师课表
     * @param teaId 教师号
     * @return String json字符串，课表数组
     */
    public ArrayList<ArrayList<ArrayList<KeChengInfo>>> getTeaKebiaoByTeaId(String teaId){

        //这里定义一个空map集合，因为parseHtmlToKebiaoInfo需要一个map作为参数。在获取教师课表这个方法内，cjzcs无其他意义。
        Map<String,String> cjzcs=new HashMap<>();

        //教务在线课表页的html代码，字符串表示。
        String teaKebiaoHtml=null;

        //发起http请求，获取教务在线的课表ktml代码
        try {
            teaKebiaoHtml= SendHttpRquest.getHtmlWithParam(JWZX_URL_TEAKEBIAO, "teaId=" + teaId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //教师获取课表

        //解析并返回,带参数”t“表示这是老师的课表。
        return Parse.parseHtmlToKebiaoInfo(teaKebiaoHtml,"t");
    }
    /**
     * 获取学生所有课程的成绩组成
     * @param xh 学号
     * @return Map集合，key是教学班，value是成绩组成
     * @throws IOException http请求异常
     */
    public Map<String,String> getCjzcByXh(String xh) throws Exception {

        //json操作类
        ObjectMapper objectMapper=new ObjectMapper();
        //先获取缓存
        try {
            String cjzcJson = redisService.getCjzc(xh);
            if (null!=cjzcJson){
                //不为空说明缓存有数据，直接返回！
                return objectMapper.readValue(cjzcJson,Map.class);
            }
        }catch (Exception e){
            logger.error("获取成绩组成缓存出现未知错误！");
        }

        Map<String,String> cjzc;

        // 1.根据学号获取一卡通号和密码。
        Map<String, String> ykthandPassword = eduAccountService.getStuYkthandPassword(xh);

        // 1.获取模拟登陆后代表用户登陆状态的cookie （PHPSESSID）
        String phpsessid = simulationLogin.getPhpsessid(ykthandPassword.get("ykth"), ykthandPassword.get("password"));

        // 2.发起http请求（get）获取教务在 线上授课计划页的html代码
        String cjzcHtml = SendHttpRquest.getHtmlWithCookie(JWZX_URL_STUSKJH, phpsessid);

        // 3.解析html
        cjzc = Parse.parseHtmlToCJZC(cjzcHtml);

        // 4.放进缓存
        try {
            //将成绩组成 Map<>集合转未json格式数据
            String cjzcJson = objectMapper.writeValueAsString(cjzc);
            redisService.setCjzc(xh,cjzcJson);
        }catch (Exception e){
            logger.error("将成绩组成添加进缓存失败！");
        }
        return cjzc;
    }

    /**
     * 通过教学班获取上课学生名单
     * @param jxb 教学班
     * @return list<ClassStudentInfo>
     */
    public List<ClassStuInfo>getKbStuList(String jxb)  {
        List<ClassStuInfo> ClassStuList = null;

        try {
            //发起http请求，请求教务在线学生名单页。
            String param="jxb="+jxb;
            //学生名单页的html
            String stuListHtml = SendHttpRquest.getHtmlWithParam(JWZX_URL_KESTULIST, param);
            //解析html
            ClassStuList = Parse.parseHtmlToStuList(stuListHtml);
        }catch (Exception e){
            //日志
            logger.error("【获取上课学生名单(GetDataFromJWZX)】失败！可能因为教学班号不对！错误信息：[{}]",e.getMessage());
        }
        return ClassStuList;
    }

    /**
     * 获取重庆邮电大学，校历时间。
     * @return 时间的map集合
     */
    public SchoolTime getSchoolTime(){
        try {
            // 1.请求教务在线的主页
            String JWZXhtml = SendHttpRquest.getHtml(JWZX_URL_HOME);
            // 2.解析获取时间
            // 3.返回
            return Parse.parseHtmlToSchoolTime(JWZXhtml);

        }catch (Exception e){
            //日志
            logger.error("获取教务时间出错！错误信息：[{}]",e.getMessage());
        }
        return null;
    }
}
