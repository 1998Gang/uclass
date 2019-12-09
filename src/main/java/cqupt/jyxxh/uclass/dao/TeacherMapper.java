package cqupt.jyxxh.uclass.dao;

import cqupt.jyxxh.uclass.pojo.Teacher;


public interface TeacherMapper {

    /**
     * 查询教师信息
     *
     * @param teaId 教师号
     * @return Teacher
     */
     Teacher queryTeacherByTeaId(String teaId);

    /**
     *根据教师号（teaId）删除老师数据
     *
     * @param teaId 教师号
     */
    void deleteTeacherByTeaId(String teaId);


    /**
     * 插入一条教师信息数据
     * @param teacher 教务账号实体(教师)
     */
    void insertTeacher(Teacher teacher);


    /**
     * 通过一卡通号查询数据条数
     * @param ykth 一卡通号
     * @return  数据条数
     */
     int numByYkth(String ykth);


    /**
     * 获取教务账号（教师）通过一卡通号
     * @param ykth 一卡通号
     * @return  teacher 教务账号（教师）实体
     */
    Teacher queryTeacherByYkth(String ykth);
}
