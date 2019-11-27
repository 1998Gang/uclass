package cqupt.jyxxh.uclass.service;

import cqupt.jyxxh.uclass.dao.StudentMapper;
import cqupt.jyxxh.uclass.dao.TeacherMapper;
import cqupt.jyxxh.uclass.pojo.Student;
import cqupt.jyxxh.uclass.pojo.Teacher;
import cqupt.jyxxh.uclass.pojo.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * 对用户数据进行操作的类
 *
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:19 2019/11/2
 */

@Service
public class OperationUserInfo {

    final Logger logger= LoggerFactory.getLogger(OperationUserInfo.class);


    @Autowired
    private StudentMapper studentMapper;  //与学生数据有关的操作（DAO）

    @Autowired
    private TeacherMapper teacherMapper;  //与老师数据有关的操作（DAO）

    @Autowired
    private OperationBind bindInfo;       //与用户绑定有关的操作（service）

    /**
     * 通过openid查询学生数据[Student]
     *
     * @param openid
     * @return
     */
    public Student getStudentInfoByOpenid(String openid){
        Student student = studentMapper.queryStudentByopenid(openid);
        return student;
    }

    /**
     * 通过openid查询老师数据[Teacher]
     *
     * @param openid
     * @return
     */
    public Teacher getTeacherInfoByOpenid(String openid){
        Teacher teacher =teacherMapper.queryTeacherByOpenid(openid);
        return teacher;
    }

    /**
     * 根据openid删除学生信息
     *
     * 如果进行的是 【解除用户绑定】的逻辑，在调用本方法之前，一定不能先调用 deleteBind(String openid) 方法。
     *      本方法要依赖 绑定数据 来判断执行该操作的用户是老师还是学生。
     *
     * @param openid
     * @return
     */
    public boolean deleteUserInfo(String openid){
        boolean flage=false;
        //根据openid判断该用户是老师还是学生  返回值"01"代表老师，"16"代表学生
        String stuOrTea=bindInfo.bindType(openid);
        switch (stuOrTea){
            case "01":{
                //删除老师信息
                teacherMapper.deleteTeacherByOpenid(openid);
                flage=true;
                break;
            }
            case "16":{
                //删除学生信息
                studentMapper.deleteStudentByOpenid(openid);
                flage=true;
                break;
            }
            default:{
                //其他用户类型，没有信息数据可以删除。
                break;
            }
        }
        return flage;
    }

    /**
     * 向用户信息数据库新增一条信息
     *
     * @param userInfo   用户实体（Student或Teacher）
     * @param userType   一卡通号前两位
     * @return
     */
    public boolean addUserInfo(UserInfo userInfo,String userType){
        boolean flage=true;
        try{
            //2.向数据库写入数据，判断该用户是学生("16")还是老师("01")
            switch (userType){
                case "01":{
                    //2.1 向教师表写入教师信息
                    teacherMapper.insertTeacher((Teacher)userInfo);
                    break;
                }
                case "16":{
                    //2.2 向学生表写入学生信息
                    studentMapper.insertStudent((Student) userInfo);
                    break;
                }
                default:{
                    break;
                }
            }
        }catch (Exception e){
            flage=false;
            logger.error("【插入用户数据（addUserInfo）】向数据库插入用户数据失败，统一认证码：[{}]",userInfo.getYkth(),e);
            //数据库操作失败，抛出RuntimeException()，使事务可以回滚。
            throw new RuntimeException();
        }
        return flage;
    }

}
