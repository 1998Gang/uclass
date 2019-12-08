package cqupt.jyxxh.uclass.pojo;

/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 21:53 2019/11/2
 */
public class Teacher implements EduAccount {

    private String teaId;//教师的教师号
    private String teaName;//教师的name
    private String xb;//性别
    private String ykth;//一卡通号
    private String zc;//教师的职称
    private String jysm;//教师的教研室
    private String yxm;//教师的院系名
    private String lxyx;//教师的联系邮箱

    @Override
    public String toString() {
        return "Teacher{" +
                "teaId='" + teaId + '\'' +
                ", teaName='" + teaName + '\'' +
                ", xb='" + xb + '\'' +
                ", ykth='" + ykth + '\'' +
                ", zc='" + zc + '\'' +
                ", jysm='" + jysm + '\'' +
                ", yxm='" + yxm + '\'' +
                ", lxyx='" + lxyx + '\'' +
                '}';
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
    public String getAccountType() {
        return "t";
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

    public void setYkth(String ykth) {
        this.ykth = ykth;
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

    public String getLxyx() {
        return lxyx;
    }

    public void setLxyx(String lxyx) {
        this.lxyx = lxyx;
    }
}