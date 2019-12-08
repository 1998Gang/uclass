package cqupt.jyxxh.uclass.pojo;

/**
 * 学生信息类
 *
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:45 2019/11/1
 */
public class Student implements EduAccount {

    private String xh;//学号 主键
    private String xm;//姓名
    private String xb;//性别
    private String ykth;//一卡通号码  统一认证吗
    private String yxm;//学院
    private String zym;//专业
    private String nj;//年级
    private String bj;//班级
    private String xjzt;//学籍状态
    private String mz;//民族
    private String csrq;//出生日期
    private String dhhm;//电话号码

    @Override
    public String toString() {
        return "Student{" +
                "xh='" + xh + '\'' +
                ", xm='" + xm + '\'' +
                ", xb='" + xb + '\'' +
                ", ykth='" + ykth + '\'' +
                ", yxm='" + yxm + '\'' +
                ", zym='" + zym + '\'' +
                ", nj='" + nj + '\'' +
                ", bj='" + bj + '\'' +
                ", xjzt='" + xjzt + '\'' +
                ", mz='" + mz + '\'' +
                ", csrq='" + csrq + '\'' +
                ", dhhm='" + dhhm + '\'' +
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

    @Override
    public String getAccountType() {
        return "s";
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

    public void setYkth(String ykth) {
        this.ykth = ykth;
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

    public String getDhhm() {
        return dhhm;
    }

    public void setDhhm(String dhhm) {
        this.dhhm = dhhm;
    }
}
