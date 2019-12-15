package cqupt.jyxxh.uclass.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 用户信息接口 学生类、老师类都实现该接口,方便返回信息。
 *
 */
public interface EduAccount {

    /**
     * 获取子类的类型 t为老师 s为学生
     * @return 账户类型
     */
    String getAccountType();

    /**
     * 获取实现类的账户姓名
     * @return 账户姓名
     */
    @JsonIgnore//转换json的时候，不转换
    String getName();

    /**
     * 获取实现类的学号或者教师号
     * @return 学号或者教师号
     */
    @JsonIgnore//转换json的时候不转换
    String getNumber();

    /**
     * 获取实现类的一卡通号
     * @return 一卡通
     */
    String getYkth();


}
