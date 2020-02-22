package cqupt.jyxxh.uclass.pojo;

import java.util.Date;

/**
 * 问题反馈实体类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 10:58 2020/2/22
 */
public class Feeback {
    private Date time;//反馈时间
    private String ykth;//一卡通号
    private String name;//反馈者姓名
    private String userType;//反馈用户类型（学生|教师）
    private String phone;//电话号码
    private String email;//邮箱
    private String title;//问题
    private String content;//问题描述

    @Override
    public String toString() {
        return "Feeback{" +
                "time=" + time +
                ", ykth='" + ykth + '\'' +
                ", name='" + name + '\'' +
                ", userType='" + userType + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getYkth() {
        return ykth;
    }

    public void setYkth(String ykth) {
        this.ykth = ykth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
