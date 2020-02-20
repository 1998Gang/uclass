package cqupt.jyxxh.uclass.pojo;

import cqupt.jyxxh.uclass.pojo.user.Teacher;

/**
 * 学生端某一门课程的更多数据
 * 包含本门课程教师的数据，本门课程的成绩组成，该生的历史签到记录，该生的历史回答记录
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 17:15 2020/2/20
 */
public class StuKcMoreInfo {
    private Teacher teacher;//本门课程教师的信息
    private String cjzc;//本门课程的成绩组成
    private int qdTotal;//总签到次数
    private int qqTime; //缺勤次数
    private int cdTime;//迟到次数
    private int qjTime;//请假次数
    private int cqTime;//出勤次数
    private int twTotal;//本门课程提问总次数
    private int hdTimes;//回答次数
    private int wdTimes;//未回答次数

    @Override
    public String toString() {
        return "StuKcMoreInfo{" +
                "teacher=" + teacher +
                ", cjzc='" + cjzc + '\'' +
                ", qdTotal=" + qdTotal +
                ", qqTime=" + qqTime +
                ", cdTime=" + cdTime +
                ", qjTime=" + qjTime +
                ", cqTime=" + cqTime +
                ", twTotal=" + twTotal +
                ", hdTimes=" + hdTimes +
                ", wdTimes=" + wdTimes +
                '}';
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getCjzc() {
        return cjzc;
    }

    public void setCjzc(String cjzc) {
        this.cjzc = cjzc;
    }

    public int getQdTotal() {
        return qdTotal;
    }

    public void setQdTotal(int qdTotal) {
        this.qdTotal = qdTotal;
    }

    public int getQqTime() {
        return qqTime;
    }

    public void setQqTime(int qqTime) {
        this.qqTime = qqTime;
    }

    public int getCdTime() {
        return cdTime;
    }

    public void setCdTime(int cdTime) {
        this.cdTime = cdTime;
    }

    public int getQjTime() {
        return qjTime;
    }

    public void setQjTime(int qjTime) {
        this.qjTime = qjTime;
    }

    public int getCqTime() {
        return cqTime;
    }

    public void setCqTime(int cqTime) {
        this.cqTime = cqTime;
    }

    public int getTwTotal() {
        return twTotal;
    }

    public void setTwTotal(int twTotal) {
        this.twTotal = twTotal;
    }

    public int getHdTimes() {
        return hdTimes;
    }

    public void setHdTimes(int hdTimes) {
        this.hdTimes = hdTimes;
    }

    public int getWdTimes() {
        return wdTimes;
    }

    public void setWdTimes(int wdTimes) {
        this.wdTimes = wdTimes;
    }
}
