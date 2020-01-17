package cqupt.jyxxh.uclass.pojo;

/**
 * 教务时间
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 14:04 2020/1/17
 */
public class SchoolTime {
    private String school_year;  //学年  例：2019-2020
    private String semester;     //学期  例：1
    private String work_day;     //星期几
    private String week;         //周
    private String day;          //日
    private String month;       //月
    private String year;        //年

    @Override
    public String toString() {
        return "SchoolTime{" +
                "school_year='" + school_year + '\'' +
                ", semester='" + semester + '\'' +
                ", work_day='" + work_day + '\'' +
                ", wekk='" + week + '\'' +
                ", day='" + day + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                '}';
    }

    public String getSchool_year() {
        return school_year;
    }

    public void setSchool_year(String school_year) {
        this.school_year = school_year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getWork_day() {
        return work_day;
    }

    public void setWork_day(String work_day) {
        this.work_day = work_day;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
