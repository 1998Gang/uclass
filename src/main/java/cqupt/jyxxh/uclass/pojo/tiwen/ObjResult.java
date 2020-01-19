package cqupt.jyxxh.uclass.pojo.tiwen;

import java.util.List;
import java.util.Map;

/**
 * 客观题结果统计实体类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 18:27 2020/1/19
 */
public class ObjResult {
    private String th;//题号
    private List<Letter> letters;//选项

    @Override
    public String toString() {
        return "objResulst{" +
                "th='" + th + '\'' +
                ", letters=" + letters +
                '}';
    }

    public String getTh() {
        return th;
    }

    public void setTh(String th) {
        this.th = th;
    }

    public List<Letter> getLetters() {
        return letters;
    }

    public void setLetters(List<Letter> letters) {
        this.letters = letters;
    }
}
