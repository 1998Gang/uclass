package cqupt.jyxxh.uclass.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 21:53 2019/11/2
 */
public class Teacher implements EduAccount, Serializable {

    private String teaId;//教师的教师号
    private String teaName;//教师的name
    private String xb;//性别
    private String ykth;//一卡通号
    @JsonIgnore
    private String password;//一卡通密码
    private String zc;//教师的职称
    private String jysm;//教师的教研室
    private String yxm;//教师的院系名
    private String email;//教师的联系邮箱
    private String phone;//电话号码

    @Override
    public String toString() {
        return "Teacher{" +
                "teaId='" + teaId + '\'' +
                ", teaName='" + teaName + '\'' +
                ", xb='" + xb + '\'' +
                ", ykth='" + ykth + '\'' +
                ", password='" + password + '\'' +
                ", zc='" + zc + '\'' +
                ", jysm='" + jysm + '\'' +
                ", yxm='" + yxm + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    //返回类型，返回t代表是老师。
    @Override
    public String getAccountType() {
        return "t";
    }

    public String getTeaId() {
        return teaId;
    }

    public void setTeaId(String teaId) {
        this.teaId = teaId;
    }

    public String getTeaName() {
        return teaName;
    }

    public void setTeaName(String teaName) {
        this.teaName = teaName;
    }

    public String getXb() {
        return xb;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }



    @Override
    public String getName() {
        return teaName;
    }

    @Override
    public String getNumber() {
        return teaId;
    }

    public String getYkth() {
        return ykth;
    }

    @Override
    public void setPassword(String password) {
        this.password=password;
    }

    @Override
    public void setYkth(String ykth) {
        this.ykth = ykth;
    }

    public String getPassword() {

        return password;
    }


    public String getZc() {
        return zc;
    }

    public void setZc(String zc) {
        this.zc = zc;
    }

    public String getJysm() {
        return jysm;
    }

    public void setJysm(String jysm) {
        this.jysm = jysm;
    }

    public String getYxm() {
        return yxm;
    }

    public void setYxm(String yxm) {
        this.yxm = yxm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}