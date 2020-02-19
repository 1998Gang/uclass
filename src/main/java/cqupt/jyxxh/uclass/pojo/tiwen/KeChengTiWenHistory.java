package cqupt.jyxxh.uclass.pojo.tiwen;

import java.util.List;

/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 16:45 2020/2/19
 */
public class KeChengTiWenHistory {
    private String jxb;//教学班
    private int total;//有未答题记录的总人次
    private List<KeChengTWOneStuRecord> noAnswerStuList;

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

    public List<KeChengTWOneStuRecord> getNoAnswerStuList() {
        return noAnswerStuList;
    }

    public void setNoAnswerStuList(List<KeChengTWOneStuRecord> noAnswerStuList) {
        this.noAnswerStuList = noAnswerStuList;
    }
}
