package cqupt.jyxxh.uclass.dao;

import cqupt.jyxxh.uclass.pojo.Student;

/**
 *学生信息操作接口
 */
public interface StudentMapper {

    /**
     * 根据openid查询学生数据
     *
     * @param openid
     *
     */
     Student queryStudentByopenid(String openid);

    /**
     * 插入一条学生信息
     *
     * @param student
     * @return
     */
     void insertStudent(Student student);


    /**
     * 根据openid删除学生信息
     *
     * @param openid
     */
    void deleteStudentByOpenid(String openid);
}
