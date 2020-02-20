package cqupt.jyxxh.uclass.pojo.qiandao;

import java.util.List;

/**
 * 该类表示一个学生一门课程的历史签到情况
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 21:21 2020/1/12
 */
public class StuQianDaoHistory {
    private String xh;//学号
    private String jxb;//教学班号
    private int total;//总签到次数
    private int qqTime; //缺勤次数
    private int cdTime;//迟到次数
    private int qjTime;//请假次数
    private int cqTime;//出勤次数
    private List<StuSingleRecord> Records;//签到情况记录

    @Override
    public String toString() {
        return "StuQiandaoResult{" +
                "xh='" + xh + '\'' +
                ", jxb='" + jxb + '\'' +
                ", total=" + total +
                ", qqTime=" + qqTime +
                ", cdTime=" + cdTime +
                ", qjTime=" + qjTime +
                ", cqtime=" + cqTime +
                ", Record=" + Records +
                '}';
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
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

    public List<StuSingleRecord> getRecords() {
        return Records;
    }

    public void setRecords(List<StuSingleRecord> records) {
        Records = records;
    }
}
