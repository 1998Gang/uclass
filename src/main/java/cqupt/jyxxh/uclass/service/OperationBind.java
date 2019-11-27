package cqupt.jyxxh.uclass.service;

import cqupt.jyxxh.uclass.dao.BindContrastMapper;
import cqupt.jyxxh.uclass.dao.StudentMapper;
import cqupt.jyxxh.uclass.dao.TeacherMapper;
import cqupt.jyxxh.uclass.pojo.BindContrast;
import cqupt.jyxxh.uclass.pojo.Student;
import cqupt.jyxxh.uclass.pojo.Teacher;
import cqupt.jyxxh.uclass.pojo.UserInfo;
import cqupt.jyxxh.uclass.utils.Authentication;
import cqupt.jyxxh.uclass.utils.SendHttpRquest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


/**
 * 与绑定操作有关的类
 *
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 23:01 2019/11/1
 */

@Service
@Transactional
public class OperationBind {

    final Logger logger= LoggerFactory.getLogger(OperationBind.class);  //日志

    @Autowired
    private  BindContrastMapper  bindContrastMapper;  //操作绑定表的接口（DAO） 由spring管理创建实例对象注入


    @Value("${getStuInfoFronJWZX}")
    private String getStuInfoFromJWZX;        //重教务在线获取学生信息的url



    /**
     * 根据该openid 判断是否绑定
     *
     * @param openid
     * @return
     */
    public boolean isBind(String openid){
        //获取该openid在绑定表里的条数
        int numByOpenid = bindContrastMapper.numByOpenid(openid);
        //如果条数不为0，说明用户已经绑定了，返回true。
        //否则返回false，没有改openid没有绑定需要绑。
        if (0!=numByOpenid){
            return true;
        }else {
            return false;
        }
    }


    /**
     * 根据openid获取绑定类型
     *
     * @param openid
     * @return
     */
    public String bindType(String openid){
        //根据openid 获取绑定类型 1为老师 0为学生
        String bindType=bindContrastMapper.queryBindTypeByOpenid(openid);
        return bindType;
    }

    /**
     * 根据openid删除绑定信息
     *
     *进行【解除用户绑定】流程时，应先调用 deleteUserInfo(String openid) 方法，在调用本方法。先删除用户个人数据，在删除绑定数据。
     *
     * @param openid
     * @return
     */
    public boolean deleteBind(String openid){
        boolean flage=false;
        //根据openid删除绑定数据，表示该微信用户接触了与学校账户的绑定
        if (isBind(openid)){
            bindContrastMapper.deleteBindConByOpenid(openid);
            flage=true;
        }else {
            /**====================*/
            System.out.println("删除失败，因为没有找到该绑定："+openid);
        }
        return flage;
    }

    /**
     * 插入一条新的用户绑定数据
     * @param userInfo   用户信息实体（Student或Teaacher）
     * @param yktBegin    一卡通前两位，用来区别是老师还是学生。
     * @return
     */
    public boolean addBind(UserInfo userInfo,String yktBegin){
        //返回参数（添加绑定是否成功）
        boolean flage=true;
        try{

            //创建绑定对应实体
            BindContrast bindContrast=new BindContrast();
            //判断是老师("01")还是学生("16")
            switch (yktBegin){
                case "01":{
                    Teacher teacher= (Teacher) userInfo;
                    bindContrast.setBind_name(teacher.getTeaName());//设置姓名
                    bindContrast.setOpenid(teacher.getOpenid());    //设置openid
                    bindContrast.setBind_number(teacher.getTeaId());//教师号
                    bindContrast.setBind_ykth(teacher.getYkth());   //一卡通号
                    bindContrast.setUser_type("01");                //用户类型，教师“01”
                    bindContrast.setFirst_bind_time(new Date());    //绑定时间
                    //向数据库插入数据
                    bindContrastMapper.insertBindContrast(bindContrast);
                    break;
                }
                case "16":{
                    Student student= (Student) userInfo;
                    bindContrast.setBind_name(student.getXm());  //学生姓名
                    bindContrast.setOpenid(student.getOpenid()); //openid
                    bindContrast.setBind_number(student.getXh());//学生学号
                    bindContrast.setBind_ykth(student.getYkth());//一卡通号
                    bindContrast.setUser_type("16");             //用户类型，学生“16”
                    bindContrast.setFirst_bind_time(new Date()); //绑定时间
                    //向数据库插入绑定信息
                    bindContrastMapper.insertBindContrast(bindContrast);
                    break;
                }
                default:{
                    break;
                }
            }
        }catch (Exception e){
            flage=false;
            logger.error("【新整绑定（addBind）】向数据库插入绑定失败,用户[{}]",userInfo.getYkth(),e);
            //数据库操作失败，抛出RuntimeException()，使事务可以回滚。
            throw new RuntimeException();
        }


        return flage;
    }


}
