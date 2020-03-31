package cqupt.jyxxh.uclass.pojo.tiwen;

import java.util.List;
import java.util.Set;

/**
 * 问题实体
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 0:52 2020/1/19
 */
public class Question {
    /**
     * 题号
     */
    private String th;

    /**
     * 主观题题目
     */
    private String subTopic;
    /**
     * 主观题具体描述
     */
    private String subDescription;

    /**
     * 客观题选项
     */
    private Set<String> objOptins;

    /**
     * 主观题图片
     */
    private List<String> imageUrl;

    @Override
    public String toString() {
        return "Question{" +
                "th='" + th + '\'' +
                ", subTopic='" + subTopic + '\'' +
                ", subDescription='" + subDescription + '\'' +
                ", objOptins=" + objOptins +
                ", imageUrl=" + imageUrl +
                '}';
    }

    public String getTh() {
        return th;
    }

    public void setTh(String th) {
        this.th = th;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    public String getSubDescription() {
        return subDescription;
    }

    public void setSubDescription(String subDescription) {
        this.subDescription = subDescription;
    }

    public Set<String> getObjOptins() {
        return objOptins;
    }

    public void setObjOptins(Set<String> objOptins) {
        this.objOptins = objOptins;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }
}
