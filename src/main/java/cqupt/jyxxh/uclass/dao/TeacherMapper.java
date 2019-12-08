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
}
