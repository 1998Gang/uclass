package cqupt.jyxxh.uclass.pojo.tiwen;

/**
 * 单次提问，学生回答情况。
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:48 2020/2/13
 */
public class StuTWRecord {
    private String twsj;//提问时间（yyyy-MM-dd）
    private String weekStr;//提问时间(星期几周几第几次)
    private String week;//周
    private String work_day;//星期几
    private String twcs;//提问次数（该堂课第几次提问）
    private String isAnswer;//是否回答（true回答了，false未回答）,默认为"null"

    @Override
    public String toString() {
        return "StuTWRecord{" +
                "twsj='" + twsj + '\'' +
                ", weekStr='" + weekStr + '\'' +
                ", week='" + week + '\'' +
                ", work_day='" + work_day + '\'' +
                ", twcs='" + twcs + '\'' +
                ", isAnswer='" + isAnswer + '\'' +
                '}';
    }


    public String getTwsj() {
        return twsj;
    }

    public void setTwsj(String twsj) {
        this.twsj = twsj;
    }

    public String getWeekStr() {
        return weekStr;
    }

    public void setWeekStr(String weekStr) {
        this.weekStr = weekStr;
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

    public String getIsAnswer() {
        return isAnswer;
    }

    public void setIsAnswer(String isAnswer) {
        this.isAnswer = isAnswer;
    }
}
