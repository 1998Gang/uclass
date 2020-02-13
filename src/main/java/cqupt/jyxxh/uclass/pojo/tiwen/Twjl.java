package cqupt.jyxxh.uclass.pojo.tiwen;

/**
 * 提问记录
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 20:25 2020/2/13
 */
public class Twjl {
    private String twid;//提问id
    private String week;//周
    private String work_day;//星期几
    private String twcs;//提问次数，针对这一堂课的第几次提问
    private String jxb;//教学班
    private String twsj;//发起这次提问的时间(yyyy-MM-dd)

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
