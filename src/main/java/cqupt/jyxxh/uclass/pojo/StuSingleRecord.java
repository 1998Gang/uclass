package cqupt.jyxxh.uclass.pojo;

/**
 * 单次签到记录
 *主要用于返回学生某一门课程的签到记录。
 * 一门课程，会有多次签到。一个SingleRecord对象代表一次。
 * 一个List<SingleRecord>集合代表一个学生，一门课程的签到记录。
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 20:05 2020/1/12
 */
public class StuSingleRecord {
    private String jxb;   //教学班
    private String qdsj;  //日期（yyyy-MM-dd）字符串
    private String week;  //周
    private String work_day; //星期几
    private String weekStr;//签到周数以及星期数（第8周星期1）
    private String qdzt;//签到状态（CD|QJ|QQ）代表迟到|请假|缺勤。如果为空代表出勤。

    @Override
    public String toString() {
        return "StuSingleRecord{" +
                "jxb='" + jxb + '\'' +
                ", qdsj='" + qdsj + '\'' +
                ", week='" + week + '\'' +
                ", work_day='" + work_day + '\'' +
                ", weekStr='" + weekStr + '\'' +
                ", qdzt='" + qdzt + '\'' +
                '}';
    }

    public String getJxb() {
        return jxb;
    }

    public void setJxb(String jxb) {
        this.jxb = jxb;
    }

    public String getQdsj() {
        return qdsj;
    }

    public void setQdsj(String qdsj) {
        this.qdsj = qdsj;
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

    public String getWeekStr() {
        return weekStr;
    }

    public void setWeekStr(String weekStr) {
        this.weekStr = weekStr;
    }

    public String getQdzt() {
        return qdzt;
    }

    public void setQdzt(String qdzt) {
        this.qdzt = qdzt;
    }
}
