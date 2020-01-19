package cqupt.jyxxh.uclass.pojo.qiandao;

/**
 * 某个学生某一门课程的签到情况。（只针对有记录的学生，全勤的学生不包含）
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 21:42 2020/1/13
 */
public class KcOneStuRecord {
    private String xm; //学生姓名
    private String xh; //学生学号
    private int cdTime; //迟到次数
    private int qqTime; //缺勤次数
    private int qjTime; //请假次数

    @Override
    public String toString() {
        return "KcOneStuRecord{" +
                "xm='" + xm + '\'' +
                ", xh='" + xh + '\'' +
                ", cdTime=" + cdTime +
                ", qqTime=" + qqTime +
                ", qjTime=" + qjTime +
                '}';
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }

    public int getCdTime() {
        return cdTime;
    }

    public void setCdTime(int cdTime) {
        this.cdTime = cdTime;
    }

    public int getQqTime() {
        return qqTime;
    }

    public void setQqTime(int qqTime) {
        this.qqTime = qqTime;
    }

    public int getQjTime() {
        return qjTime;
    }

    public void setQjTime(int qjTime) {
        this.qjTime = qjTime;
    }
}
