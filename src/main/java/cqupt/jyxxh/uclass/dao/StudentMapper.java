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
     * @param student 教务账号（学生）
     * 
     */
     void insertStudent(Student student);


    /**
     * 根据学号（xh）删除学生信息
     *
     * @param xh 学号
     */
    void deleteStudentByOpenid(String xh);


    /**
     * 根据一卡通号查询数据库中数据条数
     * @param ykth 一卡通号
     * @return 数据条数（不是1 就是0 ）
     */
    int numByYkth(String ykth);

    /**
     * 根据学号（xh）查询学生数据
     * @param ykth 一卡通号
     * @return  student 教务账户（学生）实体
     */
    Student queryStudentByYkth(String ykth);
}
