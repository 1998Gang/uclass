package cqupt.jyxxh.uclass.pojo.tiwen;

import java.util.List;

/**
 * 学生某一门课程的课堂提问历史表现
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:39 2020/2/13
 */
public class StuTiWenHistory {
    private String xh;//学号
    private String jxb;//教学班
    private String total;//本门课程提问总次数
    private String hdTimes;//回答次数
    private String wdTimes;//未回答次数
    private List<StuTWRecord> stuTWRecordList;//学生回答提问情况记录

    @Override
    public String toString() {
        return "StuTiWenHistory{" +
                "xh='" + xh + '\'' +
                ", jxb='" + jxb + '\'' +
                ", total='" + total + '\'' +
                ", hdTimes='" + hdTimes + '\'' +
                ", wdTimes='" + wdTimes + '\'' +
                ", stuTWRecordList=" + stuTWRecordList +
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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getHdTimes() {
        return hdTimes;
    }

    public void setHdTimes(String hdTimes) {
        this.hdTimes = hdTimes;
    }

    public String getWdTimes() {
        return wdTimes;
    }

    public void setWdTimes(String wdTimes) {
        this.wdTimes = wdTimes;
    }

    public List<StuTWRecord> getStuTWRecordList() {
        return stuTWRecordList;
    }

    public void setStuTWRecordList(List<StuTWRecord> stuTWRecordList) {
        this.stuTWRecordList = stuTWRecordList;
    }
}
