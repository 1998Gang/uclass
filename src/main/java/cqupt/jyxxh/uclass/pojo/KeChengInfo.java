package cqupt.jyxxh.uclass.pojo;

import java.util.List;

/**
 * 课程信息
 * 不同时间对应不同的
 * 如同一课程一周有两节课，则有两个时间数据不同的课程信息
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:19 2019/11/19
 */
public class KeChengInfo {

    private String jxb;//教学班 （SJ13191A2130640003）
    private String kch;//课程号 （A2130640）
    private String kcm;//课程名 （计算机网络）
    private String skzs;//教务在线显示的上课周数，字符串）（2-6周双周,10-12周双周）
    private String skdd; //地点  （软件工程实验室(综合实验楼A503/A504)  /  4404）
    private String jsm; //教师名    （曹岭）
    private String kclb; //课程类别（必修/选修）
    private String credit;//学分    （3.0学分）
    private String sklx;//上课类型   （在修/重修/自修等）

    private List<String> bjlbandbjh;//班级类别以及班级号 {软件工程,英语+软件,130017|10班,130617|01-03班}

    private String week; //上课周（  “00000000000000001000” 20位代表20周，本例表示第17周（某一节）有课，其他周没有  ）
    private List<String> weekNum; //上课的周数 （1、3、5、7、9）代表第1、3、5、7、9周有课。

    private String weekday;//上课天（“1”、“2”、“3”、“4”、“5”、“6”、“7” ）
    private String cStart;//课程开始节数（如该课为 34节 的课，cStart参数为3）
    private String cTimes;//课程节数    （如该课为 34节 ，cTimes为2。如果该课为4节连上1234节，cuTime为4）

    private String cjzc; //该堂课的成绩组成

    @Override
    public String toString() {
        return "KeChengInfo{" +
                "jxb='" + jxb + '\'' +
                ", kch='" + kch + '\'' +
                ", kcm='" + kcm + '\'' +
                ", skzs='" + skzs + '\'' +
                ", skdd='" + skdd + '\'' +
                ", jsm='" + jsm + '\'' +
                ", kclb='" + kclb + '\'' +
                ", credit='" + credit + '\'' +
                ", sklx='" + sklx + '\'' +
                ", bjlbandbjh=" + bjlbandbjh +
                ", week='" + week + '\'' +
                ", weekNum=" + weekNum +
                ", weekday='" + weekday + '\'' +
                ", cStart='" + cStart + '\'' +
                ", cTimes='" + cTimes + '\'' +
                ", cjzc='" + cjzc + '\'' +
                '}';
    }

    public String getSkzs() {
        return skzs;
    }

    public void setSkzs(String skzs) {
        this.skzs = skzs;
    }

    public List<String> getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(List<String> weekNum) {
        this.weekNum = weekNum;
    }


    public String getCjzc() {
        return cjzc;
    }

    public void setCjzc(String cjzc) {
        this.cjzc = cjzc;
    }

    public String getJxb() {
        return jxb;
    }

    public void setJxb(String jxb) {
        this.jxb = jxb;
    }

    public String getKch() {
        return kch;
    }

    public void setKch(String kch) {
        this.kch = kch;
    }

    public String getKcm() {
        return kcm;
    }

    public void setKcm(String kcm) {
        this.kcm = kcm;
    }

    public String getSkdd() {
        return skdd;
    }

    public void setSkdd(String skdd) {
        this.skdd = skdd;
    }

    public String getJsm() {
        return jsm;
    }

    public void setJsm(String jsm) {
        this.jsm = jsm;
    }

    public String getKclb() {
        return kclb;
    }

    public void setKclb(String kclb) {
        this.kclb = kclb;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getSklx() {
        return sklx;
    }

    public void setSklx(String sklx) {
        this.sklx = sklx;
    }

    public List<String> getBjlbandbjh() {
        return bjlbandbjh;
    }

    public void setBjlbandbjh(List<String> bjlbandbjh) {
        this.bjlbandbjh = bjlbandbjh;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getcStart() {
        return cStart;
    }

    public void setcStart(String cStart) {
        this.cStart = cStart;
    }

    public String getcTimes() {
        return cTimes;
    }

    public void setcTimes(String cTimes) {
        this.cTimes = cTimes;
    }


}
