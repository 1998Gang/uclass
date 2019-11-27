package cqupt.jyxxh.uclass.service;


import cqupt.jyxxh.uclass.pojo.Student;
import cqupt.jyxxh.uclass.pojo.Teacher;
import cqupt.jyxxh.uclass.utils.Authentication;
import cqupt.jyxxh.uclass.utils.Parse;
import cqupt.jyxxh.uclass.utils.SendHttpRquest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.directory.Attributes;
import java.util.HashMap;
import java.util.List;

/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 18:45 2019/11/12
 */

@Service
public class AddUser {

    final Logger logger= LoggerFactory.getLogger(AddUser.class);    //日志


    @Autowired
    private Authentication authentication;          //统一身份认证验证工具类（LDAP）

    @Autowired
    private SendHttpRquest sendHttpRquest;         //工具类，url访问获取数据。


    @Autowired
    private OperationUserInfo operationUserInfo;    //用户数据操作类（service）

    @Autowired
    private OperationBind operationBind;            //绑定数据操作类（service）

    @Value("${getStuInfoFronJWZX}")
    private  String GET_STUINFO_FROMJWZX;           //从教务在线获取学生信息的url

    @Value("${getTeaInfoFromJWZX}")
    private String GET_TEAINFO_FROMJWZX;           //从教务在线获取教师信息的url

    /**
     *
     * @param openid  微信用户唯一的openid
     * @param yktId   学生或老师的一卡通号（统一认证码）
     * @param password   统一认证码密码
     * @return boolean值，添加学生成功或者错误
     */
    @Transactional(rollbackFor = Exception.class)  //注解开启事务,rollbackFor = Exception.class有异常，回滚事务。
    public boolean addUInfoAndUBind(String openid,String yktId,String password){
        boolean flage=false;
        try{
            //从LDAP获取基本信息  老师：{姓名，所属学院，一卡通号}   学生：{姓名，所属学院，一卡通号，学号}
            Attributes attributes=authentication.getAttributes(yktId,password);
            //解析attributes，转换为map集合    attrsMap :   {uid=1655728, edupersonorgdn=软件工程学院, cn=彭渝刚, edupersonstudentid=2017214033}
            HashMap<String,String> attrsMap= Parse.ParseAttributes(attributes);
            //获取一卡通号前两位，根据yktId判断是该用户是老师还是学生，“01”开头是老师，“16”开头是学生，“72”开头是留学生，其他是学校其他人员
            String yktBegin=yktId.substring(0,2);
            switch (yktBegin) {
                case "16": {
                    try {
                        //获得学生学号
                        String edupersonstudentid = attrsMap.get("edupersonstudentid");
                        //根据学号去教务在线查询学生个人信息(json字符串)    studentinfoF={"code":0,"info":"ok","returnData":[{"xh":"2017214033","xm":"\u5f6d\u6e1d\u521a","xmEn":"Peng Yu Gang ","xb":"\u7537","bj":"13001701","zyh":"1300","zym":"\u8f6f\u4ef6\u5de5\u7a0b","yxh":"13","yxm":"\u8f6f\u4ef6\u5de5\u7a0b\u5b66\u9662","nj":"2017","csrq":"19981030","xjzt":"\u5728\u6821","rxrq":"201709","yxmen":"School of Software","zymEn":"Software Engineering","xz":4,"mz":"\u6c49\u65cf                "}]}
                        String stuJsoninfo = sendHttpRquest.getJsonfromhttp(GET_STUINFO_FROMJWZX, "searchKey=" + edupersonstudentid);
                        //解析该json数据，转化未Student对象
                        Student student = Parse.ParseJsonToStudent(stuJsoninfo);
                        //往该Student对象添加openid、与一卡通号（统一认证码）
                        student.setOpenid(openid);
                        student.setYkth(yktId);

                        // 保存学生信息到数据库
                        boolean b = operationUserInfo.addUserInfo(student, yktBegin);

                        //添加绑定信息到数据库
                        boolean b1 = operationBind.addBind(student, yktBegin);

                        //保存信息数据，添加绑定数据同时成功，表示学生添加成功！
                        if (b && b1) {
                            flage = true;
                            if (logger.isInfoEnabled()){
                                logger.info("【添加用户（addUInfoAndUBind）】添加学生成功，学生：[{},{},{}]",student.getXm(),student.getXh(),student.getYkth());
                            }
                        }
                    } catch (Exception e) {
                        flage = false;
                        logger.error("【添加用户（addUInfoAndUBind）】添加学生出错，学生：[{},{},{}]", attrsMap.get("uid"), attrsMap.get("cn"), attrsMap.get("edupersonstudentid"), e);
                        break;
                    }
                    break;
                }
                case "01": {
                    try {
                        //1.获得教师姓名以及学院名（LDAP返回的信息）
                        String teaName = attrsMap.get("cn");
                        String yxm = attrsMap.get("edupersonorgdn");
                        //2.根据教师姓名去教务在线查教师信息
                        String teaJsonInfo = sendHttpRquest.getJsonfromhttp(GET_TEAINFO_FROMJWZX, "searchKey=" + teaName);
                        //3.解析该json数据，转化为Teacher对象,由于用教师姓名进行查询，存在查询出多条教师信息的情况。
                        List<Teacher> teachers = Parse.ParseJsonToTeacher(teaJsonInfo);
                        //4.遍历该教师集合，找出符合条件的教师。  （两个条件：姓名、学院）
                        for (Teacher teacher : teachers) {
                            //判断教师姓名，与教师所属学院，同时符合的为正确的教师
                            if (Parse.isbaohan(teaName, teacher.getTeaName()) && Parse.isbaohan(yxm, teacher.getYxm())) {
                                teacher.setOpenid(openid);//teacher添加openid
                                teacher.setYkth(yktId);   //teacher添加一卡通号

                                //保存教师信息到数据库
                                boolean b = operationUserInfo.addUserInfo(teacher, yktBegin);

                                //添加绑定信息到数据库
                                boolean b1 = operationBind.addBind(teacher, yktBegin);

                                //以上两个保存操作都正确，添加用户操作才正确。
                                if (b&&b1){
                                    flage=true;
                                    if (logger.isInfoEnabled()){
                                        logger.info("【添加用户（addUInfoAndUBind）】添加教师成功，教师：[{},{},{}]",teacher.getTeaName(),teacher.getTeaId(),teacher.getYkth());
                                    }

                                }
                            }
                        }
                    } catch (Exception e) {
                        flage = false;
                        logger.error("【添加用户（addUInfoAndUBind）】添加教师出错，教师：[{},{},{}]",attrsMap.get("uid"),attrsMap.get("cn"),attrsMap.get("edupersonorgdn"));
                    }
                    break;
                }
                default: {
                    //其他类型用户不允许被绑定，当然后续也可以跟进开发，支持其他类型用户
                    flage = false;
                    if (logger.isInfoEnabled()) {
                        logger.info("【添加绑定（addBind）】添加绑定失败！统一认证码[{}]是不支持的用户类型", yktId);
                    }
                    break;
                }
            }
        }catch (Exception e){
            flage=false;
            logger.error("【添加新用户（addUInfoAndUBind）】出现未知错误，统一认证吗:[{}]",yktId,e);

        }
        return flage;
    }

}
