package cqupt.jyxxh.uclass.pojo.tiwen;

/**
 * 提问记录
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 20:25 2020/2/13
 */
public class Twjl {
    /**
     * 提问ID
     */
    private String twid;
    /**
     * 第几周
     */
    private String week;
    /**
     * 星期几
     */
    private String work_day;
    /**
     * 提问次数，针对这堂课的第几次提问
     */
    private String twcs;
    /**
     * 教学班
     */
    private String jxb;
    /**
     * 发起这次提问的时间(yyyy-MM-dd)
     */
    private String twsj;

    @Override
    public String toString() {
        return "Twjl{" +
                "twid='" + twid + '\'' +
                ", week='" + week + '\'' +
                ", work_day='" + work_day + '\'' +
                ", twcs='" + twcs + '\'' +
                ", jxb='" + jxb + '\'' +
                ", twsj='" + twsj + '\'' +
                '}';
    }

    public String getTwid() {
        return twid;
    }

    public void setTwid(String twid) {
        this.twid = twid;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getWork_day() {
        return work_day;
    }

    public void setWork_day(String work_day) {
        this.work_day = work_day;
    }

    public String getTwcs() {
        return twcs;
    }

    public void setTwcs(String twcs) {
        this.twcs = twcs;
    }

    public String getJxb() {
        return jxb;
    }

    public void setJxb(String jxb) {
        this.jxb = jxb;
    }

    public String getTwsj() {
        return twsj;
    }

    public void setTwsj(String twsj) {
        this.twsj = twsj;
    }
}
