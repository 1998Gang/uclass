package cqupt.jyxxh.uclass.pojo;

import java.io.Serializable;

/**
 * 用于传递与课堂有关的学生数据
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 9:05 2020/1/8
 */
public class ClassStuInfo implements Serializable {

    private String xh;//学号
    private String xm;//姓名
    private String xb;//性别
    private String yxm;//学院
    private String zym;//专业
    private String nj;//年级
    private String bj;//班级
    private String xjzt;//学籍状态
    private String xkzt; //选课状态(重修|自修|在修|补修|正常)
    private String qdzt; //签到状态（"QQ"(缺勤),"CD"（迟到）,"QJ"（请假））
    private String qdid; //该值用于持久化到数据库。
    private String twid; //提问id，用于持久化到数据库

    @Override
    public String toString() {
        return "ClassStudentInfo{" +
                "xh='" + xh + '\'' +
                ", xm='" + xm + '\'' +
                ", xb='" + xb + '\'' +
                ", yxm='" + yxm + '\'' +
                ", zym='" + zym + '\'' +
                ", nj='" + nj + '\'' +
                ", bj='" + bj + '\'' +
                ", xjzt='" + xjzt + '\'' +
                ", xkzt='" + xkzt + '\'' +
                ", qdzt='" + qdzt + '\'' +
                ", qdid='" + qdid + '\'' +
                ", twid='" + twid + '\'' +
                '}';
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

    public String getXkzt() {
        return xkzt;
    }

    public void setXkzt(String xkzt) {
        this.xkzt = xkzt;
    }

    public String getQdzt() {
        return qdzt;
    }

    public void setQdzt(String qdzt) {
        this.qdzt = qdzt;
    }

    public String getQdid() {
        return qdid;
    }

    public void setQdid(String qdid) {
        this.qdid = qdid;
    }

    public String getTwid() {
        return twid;
    }

    public void setTwid(String twid) {
        this.twid = twid;
    }
}
