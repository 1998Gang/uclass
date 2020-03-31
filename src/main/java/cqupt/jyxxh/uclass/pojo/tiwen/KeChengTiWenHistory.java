package cqupt.jyxxh.uclass.pojo.tiwen;

import java.util.List;

/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 16:45 2020/2/19
 */
public class KeChengTiWenHistory {
    /**
     * 教学班
     */
    private String jxb;

    /**
     * 有未答题记录的总人次
     */
    private int total;

    /**
     * 表示本门课程 过去提过几次问。
     */
    private int askTimes;

    /**
     * 没有回答问题的学生统计名单
     */
    private List<KeChengTwOneStuRecord> noAnswerStuList;

    @Override
    public String toString() {
        return "KeChengTiWenHistory{" +
                "jxb='" + jxb + '\'' +
                ", total=" + total +
                ", noAnswerStuList=" + noAnswerStuList +
                '}';
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

    public int getAskTimes() {
        return askTimes;
    }

    public void setAskTimes(int askTimes) {
        this.askTimes = askTimes;
    }

    public List<KeChengTwOneStuRecord> getNoAnswerStuList() {
        return noAnswerStuList;
    }

    public void setNoAnswerStuList(List<KeChengTwOneStuRecord> noAnswerStuList) {
        this.noAnswerStuList = noAnswerStuList;
    }
}
