package cqupt.jyxxh.uclass.pojo.tiwen;

/**
 * 客观题的选项
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 18:30 2020/1/19
 */
public class Letter {
    private int id;//id
    private String letter;//选择 （A|B|C|D|...）
    private int num;    //选择这个选项的人数
    private int present;   //选这个选项的占总的百分百。
    private String background; //底色  默认为"#C9C9C9",最大的一个选项为"#8CDCDF"

    @Override
    public String toString() {
        return "Letter{" +
                "id='" + id + '\'' +
                ", letter='" + letter + '\'' +
                ", num='" + num + '\'' +
                ", present=" + present +
                ", background='" + background + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
