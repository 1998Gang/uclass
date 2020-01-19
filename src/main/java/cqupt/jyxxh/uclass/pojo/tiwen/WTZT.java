package cqupt.jyxxh.uclass.pojo.tiwen;

import java.util.List;

/**
 * 课堂提问问题主体
 *
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 0:37 2020/1/19
 */
public class WTZT {
    private String questiontype;//问题类型（sub代表主观题，obj代表客观题）
    private String yxsj;        //问题有效时间
    private List<Question> questions;   //问题列表

    @Override
    public String toString() {
        return "WTZT{" +
                "questiontype='" + questiontype + '\'' +
                ", yxsj='" + yxsj + '\'' +
                ", questions=" + questions +
                '}';
    }

    public String getQuestiontype() {
        return questiontype;
    }

    public void setQuestiontype(String questiontype) {
        this.questiontype = questiontype;
    }

    public String getYxsj() {
        return yxsj;
    }

    public void setYxsj(String yxsj) {
        this.yxsj = yxsj;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
