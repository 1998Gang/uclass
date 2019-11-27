package cqupt.jyxxh.uclass.dao;

import cqupt.jyxxh.uclass.pojo.Teacher;


public interface TeacherMapper {

    /**
     * 通过openid查询单个教师信息
     *
     * @param openid
     * @return
     */
     Teacher queryTeacherByOpenid(String openid);

    /**
     *根据openid删除老师数据
     *
     * @param openid
     */
    void deleteTeacherByOpenid(String openid);


    /**
     * 插入一条教师信息数据
     * @param userInfo
     */
    void insertTeacher(Teacher userInfo);
}
