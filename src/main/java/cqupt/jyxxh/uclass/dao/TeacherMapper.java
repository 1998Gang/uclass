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


    /**
     * 给教师账户添加密码
     * @param eduAccount 教师账户实体
     */
    void addPassword(Teacher eduAccount);


    /**
     * 根据一卡通号删除教师账户的密码
     * @param ykth 一卡通号
     */
    void deletePassword(String ykth);

    /**
     * 根据一卡通号删除教师数据
     * @param ykth 一卡通号
     */
    void deleteTeacherByYkth(String ykth);
}
