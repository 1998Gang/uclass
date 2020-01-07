package cqupt.jyxxh.uclass.pojo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 单次点名的签到结果
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 16:40 2020/1/7
 */
public class QianDaoResult {

    private Set<Map<String,String>> notOnTheStuList;//未签学生名单
    private int allStu;               //总人数
    private int notOnStu;             //未签人数
    private int inStu;                //签了的人数

    @Override
    public String toString() {
        return "QianDaoResult{" +
                "notOnTheStuList=" + notOnTheStuList +
                ", allStu=" + allStu +
                ", notOnStu=" + notOnStu +
                ", inStu=" + inStu +
                '}';
    }

    public Set<Map<String,String>> getNotOnTheStuList() {
        return notOnTheStuList;
    }

    public void setNotOnTheStuList(Set<Map<String,String>> notOnTheStuList) {
        this.notOnTheStuList = notOnTheStuList;
    }

    public int getAllStu() {
        return allStu;
    }

    public void setAllStu(int allStu) {
        this.allStu = allStu;
    }

    public int getNotOnStu() {
        return notOnStu;
    }

    public void setNotOnStu(int notOnStu) {
        this.notOnStu = notOnStu;
    }

    public int getInStu() {
        return inStu;
    }

    public void setInStu(int inStu) {
        this.inStu = inStu;
    }
}
