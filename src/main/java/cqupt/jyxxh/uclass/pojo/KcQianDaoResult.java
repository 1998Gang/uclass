package cqupt.jyxxh.uclass.pojo;

import java.util.List;

/**
 * 表示一门课程的历史签到情况
 *
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 21:51 2020/1/13
 */
public class KcQianDaoResult {
    private String jxb;  //教学班
    private int total;  //有记录的总人次
    private int Absenteeism;//有缺勤记录是人次
    private int LateArrivals;//有迟到记录的人次
    private int NumberOfLeave;//有请假记录的人次
    private List<KcOneStuRecord> stuList;//名单

    @Override
    public String toString() {
        return "KcQianDaoResult{" +
                "jxb='" + jxb + '\'' +
                ", total=" + total +
                ", Absenteeism=" + Absenteeism +
                ", LateArrivals=" + LateArrivals +
                ", NumberOfLeave=" + NumberOfLeave +
                ", stuList=" + stuList +
                '}';
    }

    public String getJxb() {
        return jxb;
    }

    public void setJxb(String jxb) {
        this.jxb = jxb;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAbsenteeism() {
        return Absenteeism;
    }

    public void setAbsenteeism(int absenteeism) {
        Absenteeism = absenteeism;
    }

    public int getLateArrivals() {
        return LateArrivals;
    }

    public void setLateArrivals(int lateArrivals) {
        LateArrivals = lateArrivals;
    }

    public int getNumberOfLeave() {
        return NumberOfLeave;
    }

    public void setNumberOfLeave(int numberOfLeave) {
        NumberOfLeave = numberOfLeave;
    }

    public List<KcOneStuRecord> getStuList() {
        return stuList;
    }

    public void setStuList(List<KcOneStuRecord> stuList) {
        this.stuList = stuList;
    }
}
