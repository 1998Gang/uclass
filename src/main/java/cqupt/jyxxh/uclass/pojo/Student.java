package cqupt.jyxxh.uclass.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * 学生信息类
 *
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:45 2019/11/1
 */
public class Student implements EduAccount, Serializable {

    private String xh;//学号 主键
    private String xm;//姓名
    private String xb;//性别
    private String ykth;//一卡通号码  统一认证码
    @JsonIgnore
    private String password;//一卡通密码
    private String yxm;//学院
    private String zym;//专业
    private String nj;//年级
    private String bj;//班级
    private String xjzt;//学籍状态
    private String mz;//民族
    private String csrq;//出生日期
    private String stu_phone;//电话号
    private String stu_email;//邮箱

    private String accountType="s";//用户类型，"s"代表学生

    private String xkzt; //选课状态（用于获取教学班名单时）

    @Override
    public String toString() {
        return "Student{" +
                "xh='" + xh + '\'' +
                ", xm='" + xm + '\'' +
                ", xb='" + xb + '\'' +
                ", ykth='" + ykth + '\'' +
                ", password='" + password + '\'' +
                ", yxm='" + yxm + '\'' +
                ", zym='" + zym + '\'' +
                ", nj='" + nj + '\'' +
                ", bj='" + bj + '\'' +
                ", xjzt='" + xjzt + '\'' +
                ", mz='" + mz + '\'' +
                ", csrq='" + csrq + '\'' +
                ", stu_phone='" + stu_phone + '\'' +
                ", stu_email='" + stu_email + '\'' +
                ", accpuntType='" + accountType + '\'' +
                ", xkzt='" + xkzt + '\'' +
                '}';
    }


    //返回用户类型，返回数据为s，代表是学生。很重要！！！
    @Override
    public String getAccountType() {
        return accountType;
    }


    public void setAccountType(String accountType) {
        this.accountType = "s";
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getXb() {
        return xb;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }



    @Override
    public String getName() {
        return xm;
    }

    @Override
    public String getNumber() {
        return xh;
    }

    public String getYkth() {
        return ykth;
    }

    @Override
    public void setYkth(String ykth) {
        this.ykth = ykth;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {

        this.password = password;
    }


    public String getYxm() {
        return yxm;
    }

    public void setYxm(String yxm) {
        this.yxm = yxm;
    }

    public String getZym() {
        return zym;
    }

    public void setZym(String zym) {
        this.zym = zym;
    }

    public String getNj() {
        return nj;
    }

    public void setNj(String nj) {
        this.nj = nj;
    }

    public String getBj() {
        return bj;
    }

    public void setBj(String bj) {
        this.bj = bj;
    }

    public String getXjzt() {
        return xjzt;
    }

    public void setXjzt(String xjzt) {
        this.xjzt = xjzt;
    }

    public String getMz() {
        return mz;
    }

    public void setMz(String mz) {
        this.mz = mz;
    }

    public String getCsrq() {
        return csrq;
    }

    public void setCsrq(String csrq) {
        this.csrq = csrq;
    }

    public String getStu_phone() {
        return stu_phone;
    }

    public void setStu_phone(String stu_phone) {
        this.stu_phone = stu_phone;
    }

    public String getStu_email() {
        return stu_email;
    }

    public void setStu_email(String stu_email) {
        this.stu_email = stu_email;
    }

    public String getXkzt() {
        return xkzt;
    }

    public void setXkzt(String xkzt) {
        this.xkzt = xkzt;
    }
}
