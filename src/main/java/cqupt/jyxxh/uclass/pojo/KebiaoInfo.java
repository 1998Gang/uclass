package cqupt.jyxxh.uclass.pojo;

/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:19 2019/11/19
 */
public class KebiaoInfo {
    private String jxb;//教学班 （SJ13191A2130640003）
    private String kch;//课程号 （A2130640）
    private String kcm;//课程名 （计算机网络）
    private String skdd; //地点  （软件工程实验室(综合实验楼A503/A504)  /  4404）
    private String jsm; //教师名    （曹岭）
    private String kclb; //课程类别（必修/选修）
    private String credit;//学分    （3.0学分）

    private String skzy;//上课的专业  （软件工程 针对教师端）
    private String skbjh; //上课的班级号  （130017|01，02班 针对教师端）

    private String week; //上课周（  “00000000000000001000” 20位代表20周，本例表示第17周有课，其他周没有  ）
    private String weekday;//上课天（“1”、“2”、“3”、“4”、“5”、“6”、“7” ）
    private String cStart;//课程开始节数（如该课为 34节 的课，cStart参数为3）
    private String cTimes;//课程节数    （如该课为 34节 ，cTimes为2。如果该课为4节连上1234节，cuTime为4）

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

    public String getSkzy() {
        return skzy;
    }

    public void setSkzy(String skzy) {
        this.skzy = skzy;
    }

    public String getSkbjh() {
        return skbjh;
    }

    public void setSkbjh(String skbjh) {
        this.skbjh = skbjh;
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

    @Override
    public String toString() {
        return "KebiaoInfo{" +
                "jxb='" + jxb + '\'' +
                ", kch='" + kch + '\'' +
                ", kcm='" + kcm + '\'' +
                ", skdd='" + skdd + '\'' +
                ", jsm='" + jsm + '\'' +
                ", kclb='" + kclb + '\'' +
                ", credit='" + credit + '\'' +
                ", skzy='" + skzy + '\'' +
                ", skbjh='" + skbjh + '\'' +
                ", week='" + week + '\'' +
                ", weekday='" + weekday + '\'' +
                ", cStart='" + cStart + '\'' +
                ", cTimes='" + cTimes + '\'' +
                '}';
    }


}
