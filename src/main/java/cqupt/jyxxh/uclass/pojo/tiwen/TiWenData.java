package cqupt.jyxxh.uclass.pojo.tiwen;

/**
 * 用于接收教师发起提问数据的实体类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 8:33 2020/1/18
 */
public class TiWenData {
    private String jxb;//教学班
    private String week;//周
    private String work_day;//星期几
    private String yxsj;//本次提问的有效时间
    private String twcs;//提问次数，针对这节课
    private WTZT wtzt;//问题主体

    @Override
    public String toString() {
        return "TiWen{" +
                "jxb='" + jxb + '\'' +
                ", week='" + week + '\'' +
                ", work_day='" + work_day + '\'' +
                ", yxsj='" + yxsj + '\'' +
                ", twcs='" + twcs + '\'' +
                ", wtzt=" + wtzt +
                '}';
    }

    public String getJxb() {
        return jxb;
    }

    public void setJxb(String jxb) {
        this.jxb = jxb;
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

    public String getYxsj() {
        return yxsj;
    }

    public void setYxsj(String yxsj) {
        this.yxsj = yxsj;
    }

    public String getTwcs() {
        return twcs;
    }

    public void setTwcs(String twcs) {
        this.twcs = twcs;
    }

    public WTZT getWtzt() {
        return wtzt;
    }

    public void setWtzt(WTZT wtzt) {
        this.wtzt = wtzt;
    }
}
