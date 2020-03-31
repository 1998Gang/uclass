package cqupt.jyxxh.uclass.pojo.tiwen;

/**
 * 有未答题记录的学生类
 * 用于显示课程历史提问结果数据
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 16:58 2020/2/19
 */
public class KeChengTwOneStuRecord {
    /**
     * 姓名
     */
    private String xm;
    /**
     * 学号
     */
    private String xh;
    /**
     * 学院
     */
    private String yxm;
    /**
     * 年级
     */
    private String nj;
    /**
     * 未答次数
     */
    private int wdTime;

    @Override
    public String toString() {
        return "KeChengTWOneStuRecord{" +
                "xm='" + xm + '\'' +
                ", xh='" + xh + '\'' +
                ", yxm='" + yxm + '\'' +
                ", nj='" + nj + '\'' +
                ", wdTimes=" + wdTime +
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

    public String getYxm() {
        return yxm;
    }

    public void setYxm(String yxm) {
        this.yxm = yxm;
    }

    public String getNj() {
        return nj;
    }

    public void setNj(String nj) {
        this.nj = nj;
    }

    public int getWdTime() {
        return wdTime;
    }

    public void setWdTime(int wdTime) {
        this.wdTime = wdTime;
    }
}
