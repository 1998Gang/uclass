package cqupt.jyxxh.uclass.pojo.tiwen;

import java.util.List;

/**
 * 学生回答某一个问题答案的实体类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 16:43 2020/1/19
 */
public class Answer {
    /**
     * 题号
     */
    private String th;
    /**
     * 主观题答案
     */
    private String subAnswer;
    /**
     * 客观题答案
     */
    private List<String> objAnswer;
    /**
     * 图片
     */
    private List<String> imageUrl;

    @Override
    public String toString() {
        return "Answer{" +
                "th='" + th + '\'' +
                ", subAnswer='" + subAnswer + '\'' +
                ", objAnswer=" + objAnswer +
                ", imageUrl=" + imageUrl +
                '}';
    }

    public String getTh() {
        return th;
    }

    public void setTh(String th) {
        this.th = th;
    }

    public String getSubAnswer() {
        return subAnswer;
    }

    public void setSubAnswer(String subAnswer) {
        this.subAnswer = subAnswer;
    }

    public List<String> getObjAnswer() {
        return objAnswer;
    }

    public void setObjAnswer(List<String> objAnswer) {
        this.objAnswer = objAnswer;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }
}
