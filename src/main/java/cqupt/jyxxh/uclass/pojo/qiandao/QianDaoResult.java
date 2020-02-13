package cqupt.jyxxh.uclass.pojo.qiandao;


import cqupt.jyxxh.uclass.pojo.ClassStuInfo;

import java.util.Set;

/**
 * 一次点名的签到结果
 * 用于实时返回课堂签到结果
 * 单次点名的签到结果,对于老师。
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 16:40 2020/1/7
 */
public class QianDaoResult {

    private String qdid;              //签到id  jxb<week>(work_day)_qdcs  例：SJ13191A2130640003<19>(2)_1
    private String week;              //周
    private String work_day;          //星期几
    private String qdcs;               //针对这堂课第几次签到
    private String jxb;                //教学班号
    private Set<ClassStuInfo> notOnTheStuList;//未签到学生名单（包含没签到，迟到，请假）
    private int allStuNum;               //总人数
    private int notOnStuNum;             //未签人数
    private int inStuNum;                //签了的人数
    private int beLateNum;                //迟到人数
    private int askForLeaveNum;           //请假人数
    private String qdsj;                    //签到时间，发生这次签到的时间（yyyy-MM-dd）。


    @Override
    public String toString() {
        return "QianDaoResult{" +
                "qdid='" + qdid + '\'' +
                ", week='" + week + '\'' +
                ", work_day='" + work_day + '\'' +
                ", qdcs='" + qdcs + '\'' +
                ", jxb='" + jxb + '\'' +
                ", notOnTheStuList=" + notOnTheStuList +
                ", allStuNum=" + allStuNum +
                ", notOnStuNum=" + notOnStuNum +
                ", inStuNum=" + inStuNum +
                ", beLateNum=" + beLateNum +
                ", askForLeaveNum=" + askForLeaveNum +
                ", qdsj=" + qdsj +
                '}';
    }

    public String getQdid() {
        return qdid;
    }

    public void setQdid(String qdid) {
        this.qdid = qdid;
    }

    public Set<ClassStuInfo> getNotOnTheStuList() {
        return notOnTheStuList;
    }

    public void setNotOnTheStuList(Set<ClassStuInfo> notOnTheStuList) {
        this.notOnTheStuList = notOnTheStuList;
    }

    public int getAllStuNum() {
        return allStuNum;
    }

    public void setAllStuNum(int allStuNum) {
        this.allStuNum = allStuNum;
    }

    public int getNotOnStuNum() {
        return notOnStuNum;
    }

    public void setNotOnStuNum(int notOnStuNum) {
        this.notOnStuNum = notOnStuNum;
    }

    public int getInStuNum() {
        return inStuNum;
    }

    public void setInStuNum(int inStuNum) {
        this.inStuNum = inStuNum;
    }

    public int getBeLateNum() {
        return beLateNum;
    }

    public void setBeLateNum(int beLateNum) {
        this.beLateNum = beLateNum;
    }

    public int getAskForLeaveNum() {
        return askForLeaveNum;
    }

    public void setAskForLeaveNum(int askForLeaveNum) {
        this.askForLeaveNum = askForLeaveNum;
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
}
