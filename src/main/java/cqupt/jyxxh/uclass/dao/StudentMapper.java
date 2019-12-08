package cqupt.jyxxh.uclass.dao;

import cqupt.jyxxh.uclass.pojo.Student;

/**
 *学生信息操作接口
 */
public interface StudentMapper {

    /**
     * 根据学号（xh）查询学生数据
     *
     * @param xh 学号
     *
     */
     Student queryStudentByXh(String xh);

    /**
     * 插入一条学生信息
     *
     * @param student
     * @return
     */
     void insertStudent(Student student);


    /**
     * 根据学号（xh）删除学生信息
     *
     * @param xh
     */
    void deleteStudentByOpenid(String xh);
}
