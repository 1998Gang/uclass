package cqupt.jyxxh.uclass.pojo.qiandao;

import java.util.List;

/**
 * 表示一门课程的历史签到情况
 *
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 21:51 2020/1/13
 */
public class KeChengQianDaoHistory {
    /**
     * 教学班
     */
    private String jxb;
    /**
     * 有记录的总人次
     */
    private int total;
    /**
     * 有缺勤记录是人次
     */
    private int absenteeism;
    /**
     * 有迟到记录的人次
     */
    private int lateArrivals;
    /**
     * 有请假记录的人次
     */
    private int numberOfLeave;
    /**
     * 名单
     */
    private List<KeChengQdOneStuRecord> stuList;

    @Override
    public String toString() {
        return "KcQianDaoResult{" +
                "jxb='" + jxb + '\'' +
                ", total=" + total +
                ", Absenteeism=" + absenteeism +
                ", LateArrivals=" + lateArrivals +
                ", NumberOfLeave=" + numberOfLeave +
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
        return absenteeism;
    }

    public void setAbsenteeism(int absenteeism) {
        this.absenteeism = absenteeism;
    }

    public int getLateArrivals() {
        return lateArrivals;
    }

    public void setLateArrivals(int lateArrivals) {
        this.lateArrivals = lateArrivals;
    }

    public int getNumberOfLeave() {
        return numberOfLeave;
    }

    public void setNumberOfLeave(int numberOfLeave) {
        this.numberOfLeave = numberOfLeave;
    }

    public List<KeChengQdOneStuRecord> getStuList() {
        return stuList;
    }

    public void setStuList(List<KeChengQdOneStuRecord> stuList) {
        this.stuList = stuList;
    }
}
