package cqupt.jyxxh.uclass.service;


import cqupt.jyxxh.uclass.dao.StudentMapper;
import cqupt.jyxxh.uclass.dao.TeacherMapper;
import cqupt.jyxxh.uclass.pojo.EduAccount;
import cqupt.jyxxh.uclass.pojo.Student;
import cqupt.jyxxh.uclass.pojo.Teacher;
import cqupt.jyxxh.uclass.pojo.UclassUser;
import cqupt.jyxxh.uclass.utils.Authentication;
import cqupt.jyxxh.uclass.utils.Parse;
import cqupt.jyxxh.uclass.utils.SendHttpRquest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.naming.directory.Attributes;
import java.util.HashMap;
import java.util.List;

/**
 * 教务账户的操作类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 19:51 2019/12/8
 */

@Service
public class EduAccountService {

    private final Logger logger= LoggerFactory.getLogger(EduAccountService.class);

    @Autowired
    private  Authentication authentication;       //统一身份认证实现类

    @Autowired
    private  SendHttpRquest sendHttpRquest;       //发送http请求的工具类

    @Autowired
    private  TeacherMapper teacherMapper;       //教务账号（教师）dao操作接口

    @Autowired
    private  StudentMapper studentMapper;        //教务账号（学生）dao操作接口

    @Value("${URLTeaInfoFromJWZX}")
    private  String URL_TeaInfo_From_JWZX;            //去教务在线请求教师信息的URL
    @Value("${URLStuInfoFromJWZX}")
    private  String URL_StuInfo_From_JWZX;            //去教务在线请求学生信息的URL


    /**
     * 获取教务账户信息，通过统一身份，去教务在线爬取。
     * @param ykth   统一身份认证码（一卡通号）
     * @param password   统一身份认证密码
     * @return EduAccount 教务账号实体对象
     */
    public EduAccount getEduAccountInfoFromJWZX(String ykth, String password){
        EduAccount eduAccount =null;

        // 1.验证统一身份是否正确
        boolean istrue = authentication.ldapCheck(ykth, password);
        // 不正确
        if (!istrue){
            //日志
            if (logger.isInfoEnabled()){
                logger.info("【教务账号获取(UserBindInfoService.getBindInfofromYkth)】获取教务账号失败！因为统一身份认证失败！");
            }
            return eduAccount;
        }

        // 2.身份验证成功，获取LDAP返回的简单数据
        // 老师：{姓名，所属学院，一卡通号}
        // 学生：{姓名，所属学院，一卡通号，学号}
        Attributes attributes = authentication.getAttributes(ykth, password);

        // 3.使用工具类 Parse 解析 attributes
        HashMap<String, String> attributeHashMap = Parse.ParseAttributes(attributes);

        // 4.根据统一身份认证码(一卡通号)判断该教务账户是教师账户还是学生账
        //   01开头为教师
        //   16开头为本科生、72开头为留学生
        String ykthStart=ykth.substring(0,2);

        switch (ykthStart){
            // 4.1  教师
            case "01":{
                //4.1.1 获取教师姓名、教师的所属学院（LDAP返回的数据）
                String jsxm = attributeHashMap.get("cn");
                String yxm=attributeHashMap.get("edupersonorgdn");
                //4.1.2 以教师姓名为参数去教务在线查询教师账户的详细信息
                String param="searchKey="+jsxm;
                String teaJsonInfo = sendHttpRquest.getJsonfromhttp(URL_TeaInfo_From_JWZX, param);
                //4.1.3 将请求回的json数据转码
                String teaJsonInfo_ZW = Parse.decodeUnicode(teaJsonInfo);
                //4.1.4 将josn数据解析为Teacher对象
                List<Teacher> teachers = Parse.ParseJsonToTeacher(teaJsonInfo_ZW);
                //4.1.5 遍历teachers集合，筛选符合条件的教师教师。
                /* 通过姓名查询，会查询出较多同名教师。
                    但是这些同名教师的所属学院大概率不一致，当然这也不能百分百保证能正确筛选。
                    实在没有办法，目前只能用这样的方式去确定老师。
                    所有此处采用去对比学院名的方式，来确定该教务账户到底是哪一个老师。*/
                for (Teacher teacher:teachers){
                    if (Parse.isbaohan(yxm,teacher.getYxm())){
                        //添加一卡通号
                        teacher.setYkth(ykth);
                        eduAccount=teacher;
                    }
                }
                break;
            }
            // 4.2 留学生、本科生
            case "72":
            case "16":{
                //4.2.1 获取学生学号(LDAP返回的数据)
                String xh=attributeHashMap.get("edupersonstudentid");
                //4.2.2 以学生学号为参数去教务在线查询详细的学生信息
                String param="searchKey="+xh;
                String stuJsonInfo = sendHttpRquest.getJsonfromhttp(URL_StuInfo_From_JWZX, param);
                // 4.2.3 转码
                String stuJsonInfo_ZW = Parse.decodeUnicode(stuJsonInfo);
                //4.2.4 将json数据解析为Student对象
                Student student = Parse.ParseJsonToStudent(stuJsonInfo_ZW);
                //添加一卡通号
                student.setYkth(ykth);

                eduAccount=student;
                break;
            }
            // 4.3
            default:{

                break;
            }
        }
        return eduAccount;
    }


    /**
     * 获取用户已经绑定的教务账号信息
     * @param uclassUser 用户实体
     * @return EduAccount 教务账号信息
     */
    public EduAccount getUserEduAccountInfo(UclassUser uclassUser){
        //教务账号对象
        EduAccount eduAccount =null;

        // 1.获取用户绑定的教务账号的类型
        String user_type = uclassUser.getUser_type();
        // 2.判断教务账号类型，执行不同操作
        switch (user_type){
            case "t":{
                // 2.1 教务账号为老师
                eduAccount = teacherMapper.queryTeacherByTeaId(uclassUser.getBind_number());
                break;
            }
            case "s":{
                // 2.2 教务账号为学生
                eduAccount = studentMapper.queryStudentByXh(uclassUser.getBind_number());
                break;
            }
            default:{
                break;
            }
        }

        return eduAccount;
    }


    /**
     * 将教务账户数据插入到数据库中
     * 自动判断传如参数是学生账户还剩教师账户
     *
     * @param eduAccount 教务账户实体
     */
    public void insertEduAccountToDB(EduAccount eduAccount){
        boolean flage=false;
        // 1.获取账户类型
        String accountType = eduAccount.getAccountType();
        // 2.根据类型分别进行数据库操作
        switch (accountType){
            case "t":{
                teacherMapper.insertTeacher((Teacher) eduAccount);
                break;
            }
            case "s":{
                studentMapper.insertStudent((Student) eduAccount);
                break;
            }
            default:{
                break;
            }
        }
    }

    public boolean isEduAccountInDB(String ykth){
        boolean flage=false;
        // 1.获取用户类型（一卡通前两位，16开头为本科生、72开头为留学生、01开头为教师）
        String ykthStart=ykth.substring(0,2);
        // 2.判断
        switch(ykthStart){
            //教师
            case "01":{
                //根据一卡通号去u课堂后台数据库（教务账号表（uclass_teacher_info））查询数据条数，如果返回1，就说明数据库中有该教务用户。
                int num = teacherMapper.numByYkth(ykth);
                flage= 1 == num;
                break;
            }
            //学生
            case "16":
            case "72":{
                //根据一卡通号去u课堂后台数据库（教务账号表（uclass_students_info））查询数据条数，如果返回1，就说明数据库中有该教务用户。
                int num=studentMapper.numByYkth(ykth);
                flage= 1 == num;
                break;
            }
        }

        return flage;
    }


    /**
     * 通过一卡通号（ykth）获取教务账号实体
     * @param ykth 一卡通号
     * @return eduAccount
     */
    public EduAccount getEduAccountFromDB(String ykth) {
        EduAccount eduAccount=null;

        //1.获取用户类型（一卡通前两位，16开头为本科生、72开头为留学生、01开头为教师）
        String ykthStart = ykth.substring(0, 2);
        // 2.判断
        switch(ykthStart){
            //教师
            case "01":{
                //根据ykth号查询教师教务账号数据。
                eduAccount= teacherMapper.queryTeacherByYkth(ykth);
                break;
            }
            //学生
            case "16":
            case "72":{
                //根据ykth号查询学生教务账号数据。
                eduAccount= studentMapper.queryStudentByYkth(ykth);
                break;
            }
            default:{
                break;
            }
        }

        return eduAccount;
    }
}
