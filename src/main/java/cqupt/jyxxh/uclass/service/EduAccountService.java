package cqupt.jyxxh.uclass.service;


import cqupt.jyxxh.uclass.dao.StudentMapper;
import cqupt.jyxxh.uclass.dao.TeacherMapper;
import cqupt.jyxxh.uclass.pojo.EduAccount;
import cqupt.jyxxh.uclass.pojo.Student;
import cqupt.jyxxh.uclass.pojo.Teacher;
import cqupt.jyxxh.uclass.pojo.UclassUser;
import cqupt.jyxxh.uclass.utils.Authentication;
import cqupt.jyxxh.uclass.utils.GetDataFromJWZX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


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
    private  TeacherMapper teacherMapper;       //教务账号（教师）dao操作接口

    @Autowired
    private  StudentMapper studentMapper;        //教务账号（学生）dao操作接口

    @Autowired
    private GetDataFromJWZX getDataFromJWZX;     //去教务在线获取数据的工具类

    @Value("${URLTeaInfoFromJWZX}")
    private  String URL_TEAINFO_FROM_JWZX;            //去教务在线请求教师信息的URL
    @Value("${URLStuInfoFromJWZX}")
    private  String URL_StuInfo_From_JWZX;            //去教务在线请求学生信息的URL



    /**
     * 获取教务账户信息，通过统一身份，去教务在线爬取。
     * @param ykth   统一身份认证码（一卡通号）
     * @param password   统一身份认证密码
     * @return EduAccount 教务账号实体对象
     */
    public EduAccount getEduAccountInfoByYkth(String ykth, String password) throws Exception {
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

        // 2.根据统一身份认证码(一卡通号)判断该教务账户是教师账户还是学生账
        //   01开头为教师
        //   16开头为本科生、72开头为留学生
        String ykthStart=ykth.substring(0,2);
        switch (ykthStart){

            // 4.1  教师
            case "01":{
                eduAccount = getDataFromJWZX.getTeacherInfoByTYSH(ykth, password);

                if (eduAccount==null){
                    throw new Exception("Unsupported academic administration account");
                }
                break;
            }

            // 4.2 留学生、本科生
            case "16":
            case "72": {
                eduAccount = getDataFromJWZX.getStudentInfoByTYSH(ykth, password);
                System.out.println("====getEduAccountInfoByYkth====="+(Student)eduAccount);
                break;
            }
            // 4.3
            default:{
                //不支持的教务账户，抛出异常！
                throw new Exception("Unsupported academic administration account");
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
        EduAccount eduAccount =null;

        // 1.获取用户绑定的教务账号的类型
        String user_type = uclassUser.getUser_type();

        // 2.判断教务账号类型，执行不同操作
        switch (user_type){
            // 2.1 教务账号为老师
            case "t":{
                // 根据ykth判断u课堂数据库中是否存在该教务账户数据（教师）
                boolean isture = isEduAccountInDB(uclassUser.getBind_ykth());
                if (isture){
                    //数据库中有,通过绑定的教师号去拿。
                    eduAccount = teacherMapper.queryTeacherByTeaId(uclassUser.getBind_number());
                }else {
                    //数据库中没有，通过绑定的教师号去拿。
                    eduAccount = getDataFromJWZX.getTeacherInfoByTeaId(uclassUser.getBind_number());
                }

                break;
            }
            // 2.2 教务账号为学生
            case "s":{
                //根据ykth判断u课堂数据库中是否存在该教务账户数据（学生）
                boolean istrue = isEduAccountInDB(uclassUser.getBind_ykth());
                if (istrue){
                    //数据库中有
                    eduAccount = studentMapper.queryStudentByXh(uclassUser.getBind_number());
                }else {
                    //数据库中没有，根据学号去教务在线拿
                    eduAccount = getDataFromJWZX.getStudentInfoByXh(uclassUser.getBind_number());
                }
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

    /**
     * 根据一卡通号判断u课堂后台数据库中是否有该教务账户的数据
     * @param ykth 统一身份认证吗（一卡通号）
     * @return boolean
     */
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
