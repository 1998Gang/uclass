package cqupt.jyxxh.uclass.pojo.tiwen;

import java.util.List;

/**
 * 学生提交的答案的实体类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 16:03 2020/1/19
 */
public class AnswerData {
    private String twid;//提问id
    private String xh;//学号
    private String yxm;//学院名
    private String xm;//姓名
    private String questiontype;//问题类型，sub代表主观题，obj代表客观题。
    private List<Answer> answers;//答案列表

    @Override
    public String toString() {
        return "AnswerData{" +
                "twid='" + twid + '\'' +
                ", xh='" + xh + '\'' +
                ", yxm='" + yxm + '\'' +
                ", xm='" + xm + '\'' +
                ", questiontype='" + questiontype + '\'' +
                ", answers=" + answers +
                '}';
    }

    public String getTwid() {
        return twid;
    }

    public void setTwid(String twid) {
        this.twid = twid;
    }

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }

    public String getYxm() {
        return yxm;
    }

    public void setYxm(String yxm) {
        this.yxm = yxm;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getQuestiontype() {
        return questiontype;
    }

    public void setQuestiontype(String questiontype) {
        this.questiontype = questiontype;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
