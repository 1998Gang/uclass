package cqupt.jyxxh.uclass.pojo;

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
    private int qqtime; //未签到次数
    private int cdtime;//迟到次数
    private int qjtime;//请假次数
    private int cqtime;//出勤次数
    private List<StuSingleRecord> Records;//签到情况记录

    @Override
    public String toString() {
        return "StuQiandaoResult{" +
                "xh='" + xh + '\'' +
                ", jxb='" + jxb + '\'' +
                ", total=" + total +
                ", qqtime=" + qqtime +
                ", cdtime=" + cdtime +
                ", qjtime=" + qjtime +
                ", cqtime=" + cqtime +
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

    public int getQqtime() {
        return qqtime;
    }

    public void setQqtime(int qqtime) {
        this.qqtime = qqtime;
    }

    public int getCdtime() {
        return cdtime;
    }

    public void setCdtime(int cdtime) {
        this.cdtime = cdtime;
    }

    public int getQjtime() {
        return qjtime;
    }

    public void setQjtime(int qjtime) {
        this.qjtime = qjtime;
    }

    public int getCqtime() {
        return cqtime;
    }

    public void setCqtime(int cqtime) {
        this.cqtime = cqtime;
    }

    public List<StuSingleRecord> getRecords() {
        return Records;
    }

    public void setRecords(List<StuSingleRecord> records) {
        Records = records;
    }
}
