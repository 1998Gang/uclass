package cqupt.jyxxh.uclass.pojo.tiwen;

import java.util.List;
import java.util.Map;

/**
 * 提问结果统计的实体类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 17:44 2020/1/19
 */
public class TiWenResult {
    private String twid;//提问id
    private String questiontype;//问题类型  sub代表主观 obj代表客观
    private List<AnswerData> subResult;//主观题结果
    private List<ObjResult> objResults;//客观题结果
    private List<Map<String,String>> noAnswerStuList;//未答题学生名单

    @Override
    public String toString() {
        return "TiWenResult{" +
                "twid='" + twid + '\'' +
                ", questiontype='" + questiontype + '\'' +
                ", subResult=" + subResult +
                ", objResults=" + objResults +
                ", noAnswerStuList=" + noAnswerStuList +
                '}';
    }

    public String getTwid() {
        return twid;
    }

    public void setTwid(String twid) {
        this.twid = twid;
    }

    public String getQuestiontype() {
        return questiontype;
    }

    public void setQuestiontype(String questiontype) {
        this.questiontype = questiontype;
    }

    public List<AnswerData> getSubResult() {
        return subResult;
    }

    public void setSubResult(List<AnswerData> subResult) {
        this.subResult = subResult;
    }

    public List<ObjResult> getObjResults() {
        return objResults;
    }

    public void setObjResults(List<ObjResult> objResults) {
        this.objResults = objResults;
    }

    public List<Map<String, String>> getNoAnswerStuList() {
        return noAnswerStuList;
    }

    public void setNoAnswerStuList(List<Map<String, String>> noAnswerStuList) {
        this.noAnswerStuList = noAnswerStuList;
    }

}
