package cqupt.jyxxh.uclass.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.pojo.KeChengInfo;
import cqupt.jyxxh.uclass.pojo.Student;
import cqupt.jyxxh.uclass.pojo.Teacher;
import cqupt.jyxxh.uclass.service.EduAccountService;
import cqupt.jyxxh.uclass.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.naming.directory.Attributes;
import java.io.IOException;
import java.lang.invoke.StringConcatFactory;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @Value("${URLTeaInfoFromJWZX}")
    private  String URL_TEAINFO_FROM_JWZX;            //去教务在线请求教师信息的URL

    @Value("${URLStuInfoFromJWZX}")
    private  String URL_StuInfo_From_JWZX;            //去教务在线请求学生信息的URL

    @Value("${URLStuKebiaoFromJWZX}")
    private String URL_STUKEBIAO_FROM_JWZX;            //从教务在线获取学生课表的URL

    @Value("${URLTeaKebiaoFromJWZX}")
    private String URL_TEAKEBIAO_FROM_JWZX;            //从教务在线获取教师课表URL

    @Value("${JWZXURL}")
    private String JWZX_URL;                            //教务在线首页地址

    @Value("${URLStuSkjhFromJWZX}")
    private String URL_STUSKJH_FROM_JWZX;                //去教务在线获取学生课程成绩组成的URL

    @Value("${URLAuthserverLoginToJWZX}")
    private String URL_AUTHSERVER_LOGIN_TO_JWZX;         //统一身份认证平台URL（登陆教务在线）

    @Value("${URLKbStuListFromJWZX}")
    private String URL_KESTULIST_FROM_JWZX;              //去教务在线获取教学班学生名单的URL，参数 jxb=教学班号

    /**
     * 根据学号去教务在线获取学生数据
     * @param xh 学号
     * @return EduAccount
     */
    public  Student getStudentInfoByXh(String xh){


        // 1.访问教务在线url获取返回的数据
        String studentJson = SendHttpRquest.getJson(URL_StuInfo_From_JWZX, "searchKey=" + xh);

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
        String teacherInfo = SendHttpRquest.getJson(URL_TEAINFO_FROM_JWZX, "searchKey=" + teaId);
        // 2.解析json数据获取教师实体（返回的是List集合，但此处因为是用教师号进行的查询，所有只有一个教师数据）
        List<Teacher> teachers= Parse.ParseJsonToTeacher(teacherInfo);
        // 3.取出该教师数据,以EduAccount形式返回
        for (Teacher tea:teachers){
            teacher=tea;
        }

        return teacher;
    }
    /**
     * 根据教师姓名，学院名去教务在线获取教师数据
     * @param jsxm 教师姓名
     * @param yxm  教师的学院名
     * @return Teacher，教师实体
     */
    public Teacher getTeacherInfoByTeaName(String jsxm,String yxm){
        Teacher teacher = null;

        // 1. 以教师姓名为参数去教务在线查询教师的json数据
        String teaJsonInfo = SendHttpRquest.getJson(URL_TEAINFO_FROM_JWZX,"searchKey="+jsxm);

        // 2.将josn数据解析为Teacher对象（用姓名查教师数据，可能存在多个同名教师）
        List<Teacher> teachers = Parse.ParseJsonToTeacher(teaJsonInfo);

        // 3.筛选正确的老师
        /*通过姓名查询，会查询出较多同名教师。
                    但是这些同名教师的所属学院大概率不一致，当然这也不能百分百保证能正确筛选。
                    实在没有办法，目前只能用这样的方式去确定老师。
                    所有此处采用去对比学院名的方式，来确定该教务账户到底是哪一个老师。*/
        for (Teacher tea:teachers){
            if (Parse.isbaohan(yxm, tea.getYxm())){
                teacher=tea;
            }
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

        Teacher teacher;

        // 1.通过统一身份验证系统（LDAP），获取简单是账户数据，当作去教务在线获取详细数据的参数。
        Attributes attributes = authentication.getAttributes(ykth, password);

        // 2.使用工具类 Parse 解析 attributes
        HashMap<String, String> att = Parse.ParseAttributes(attributes);

        // 3.获取教师姓名、教师的所属学院（LDAP返回的数据）
        String jsxm = att.get("cn");
        String yxm=att.get("edupersonorgdn");

        // 4.更改教师姓名的编码格式(中文更改编码格式，否则发起请求是乱码，查不到数据)。这一步很重要！！！！！Charset.forName("utf-8")
        jsxm = URLEncoder.encode(jsxm, StandardCharsets.UTF_8);

        // 5. 以教师姓名为参数去教务在线查询教师的json数据
        teacher = getTeacherInfoByTeaName(jsxm, yxm);

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

        // 1.通过统一身份验证系统（LDAP），获取简单是账户数据，当作去教务在线获取详细数据的参数。
        Attributes attributes = authentication.getAttributes(ykth, password);

        // 2.使用工具类 Parse 解析 attributes
        HashMap<String, String> attributeHashMap = Parse.ParseAttributes(attributes);

        // 3.获取学生学号（LDAP）
        String xh=attributeHashMap.get("edupersonstudentid");

        // 4.通过学号获取学生数据,并添加一卡通号。
        student = getStudentInfoByXh(xh);

        return student;
    }


    /**
     * 通过学号获取课表
     * @param xh 学号
     * @return ArrayList<ArrayList<ArrayList<KeChengInfo>>> 课表数组
     */
    public ArrayList<ArrayList<ArrayList<KeChengInfo>>> getStukebiaoByXh(String xh) throws IOException {

        // jackson操作对象
        ObjectMapper objectMapper = new ObjectMapper();

        // 1.在获取课表之前先获取成绩组成
        Map<String,String> cjzcs=new HashMap<>();
        // 1.2先去redis中拿数据。
        try{
            String data = redisService.getCjzc(xh);
            if ("false".equals(data)){
                //从redis获取数据失败。1.缓存中没有该key的数据。或者 2.操作redis时出现未知错误。
                //缓存没有，就去教务在线获取
                cjzcs = getCjzcByXh(xh);
                //教务在线获取成功后，将数据存入redis中。
                //将教务在线获取map集合转为json字符串。
                String cjzcJson = objectMapper.writeValueAsString(cjzcs);
                redisService.setCjzc(xh,cjzcJson);
            }else {
                //从redis获取数据成功,将json字符串转为map集合
                cjzcs = objectMapper.readValue(data, Map.class);
            }
        }catch (Exception e){
            logger.error("【获取课表（GetDataFromJWZX.getStukebiaoByXh）】获取成绩组成出错！");
        }

        // 2.发起http请求，获取教务在线的课表页的Html代码
        String stuKebiaoHtml=null;
        try {
             stuKebiaoHtml= SendHttpRquest.getHtmlWithParam(URL_STUKEBIAO_FROM_JWZX, "xh=" + xh);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //解析并返回，带参数”s“表示这个是学生的课表,第三个参数是成绩组成。
        return Parse.parseHtmlToKebiaoInfo(stuKebiaoHtml, "s",cjzcs);
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
            teaKebiaoHtml= SendHttpRquest.getHtmlWithParam(URL_TEAKEBIAO_FROM_JWZX, "teaId=" + teaId);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //解析并返回,带参数”t“表示这是老师的课表。
        return Parse.parseHtmlToKebiaoInfo(teaKebiaoHtml,"t",cjzcs);
    }
    /**
     * 获取学生所有课程的成绩组成
     * @param xh 学号
     * @return Map集合，key是教学班，value是成绩组成
     * @throws IOException http请求异常
     */
    public Map<String,String> getCjzcByXh(String xh) throws IOException {


        Map<String,String> cjzc;

        // 1.根据学号获取一卡通号和密码。
        Map<String, String> ykthandPassword = eduAccountService.getStuYkthandPassword(xh);


        // 1.获取模拟登陆后代表用户登陆状态的cookie （PHPSESSID）
        String phpsessid = SimulationLogin.getPhpsessid(URL_AUTHSERVER_LOGIN_TO_JWZX, ykthandPassword.get("ykth"), ykthandPassword.get("password"));

        // 2.发起http请求（get）获取教务在 线上授课计划页的html代码
        String cjzcHtml = SendHttpRquest.getHtmlWithCookie(URL_STUSKJH_FROM_JWZX, phpsessid);

        // 3.解析html
        cjzc = Parse.parseHtmlToCJZC(cjzcHtml);

        return cjzc;
    }


    /**
     * 通过教学班获取上课学生名单
     * @param jxb 教学班
     * @return list<Student></Student>
     */
    public List<Student>getKbStuList(String jxb) throws IOException {
        List<Student> stuList;

        //发起http请求，请求教务在线学生名单页。
        String param="jxb="+jxb;
        //学生名单页的html
        String stuListHtml = SendHttpRquest.getHtmlWithParam(URL_KESTULIST_FROM_JWZX, param);
        //解析html
        stuList = Parse.parseHtmlToStuList(stuListHtml);
        return stuList;
    }

    /**
     * 获取重庆邮电大学，校历时间。
     * @return 时间的map集合
     */
    public Map<String,String> getSchoolTime(){
        Map<String,String> sTime=null;
        try {
            // 1.请求教务在线的主页
            String JWZXhtml = SendHttpRquest.getHtml(JWZX_URL);
            // 2.解析获取时间
            sTime = Parse.parseHtmlToSchoolTime(JWZXhtml);
            // 3.返回
            return sTime;

        }catch (Exception ignored){

        }

        return sTime;
    }





}
