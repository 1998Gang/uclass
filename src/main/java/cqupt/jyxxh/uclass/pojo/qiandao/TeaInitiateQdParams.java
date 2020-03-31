
package cqupt.jyxxh.uclass.pojo.qiandao;

/**
 *
 * 教师发起签到的参数实体
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 18:06 2020/3/9
 */
public class TeaInitiateQdParams {
    /**
     * 教学班
     */
    private String jxb;
    /**
     * 周
     */
    private String week;
    /**
     * 星期几
     */
    private String work_day;
    /**
     * 签到次数
     */
    private String qdcs;
    /**
     * 签到码
     */
    private String qdm;
    /**
     * 有效时间
     */
    private long yxsj;

    @Override
    public String toString() {
        return "TeaInitiateQDParams{" +
                "jxb='" + jxb + '\'' +
                ", week='" + week + '\'' +
                ", work_day='" + work_day + '\'' +
                ", qdcs='" + qdcs + '\'' +
                ", qdm='" + qdm + '\'' +
                ", yxsj='" + yxsj + '\'' +
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

    public String getQdcs() {
        return qdcs;
    }

    public void setQdcs(String qdcs) {
        this.qdcs = qdcs;
    }

    public String getQdm() {
        return qdm;
    }

    public void setQdm(String qdm) {
        this.qdm = qdm;
    }

    public long getYxsj() {
        return yxsj;
    }

    public void setYxsj(long yxsj) {
        this.yxsj = yxsj;
    }
}





