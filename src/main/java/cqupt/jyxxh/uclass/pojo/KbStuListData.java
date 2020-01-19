package cqupt.jyxxh.uclass.pojo;

import java.util.List;
import java.util.Map;

/**
 * 课堂学生名单数据
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 18:06 2020/1/4
 */
public class KbStuListData {

    private List<ClassStuInfo> students;     //学生名单
    private int headcount;                       //总人数
    private Map<String,Integer>numberOfXkzt;      //不同选课状态（重修，自修，在修，正常）对应的人数
    private Map<String,Map<String,Integer>> numberOfZyAndBj; //不同专业下不同班级对应的人数。
    private Map<String,Map<String,Integer>> numberOfNormalBj;//正常选课的专业和班级人数

    @Override
    public String toString() {
        return "KbStuListData{" +
                "students=" + students +
                ", headcount=" + headcount +
                ", numberOfXkzt=" + numberOfXkzt +
                ", numberOfZyAndBj=" + numberOfZyAndBj +
                ", numberOfNormalBj=" + numberOfNormalBj +
                '}';
    }

    public List<ClassStuInfo> getStudents() {
        return students;
    }

    public void setStudents(List<ClassStuInfo> students) {
        this.students = students;
    }

    public int getHeadcount() {
        return headcount;
    }

    public void setHeadcount(int headcount) {
        this.headcount = headcount;
    }

    public Map<String, Integer> getNumberOfXkzt() {
        return numberOfXkzt;
    }

    public void setNumberOfXkzt(Map<String, Integer> numberOfXkzt) {
        this.numberOfXkzt = numberOfXkzt;
    }

    public Map<String, Map<String, Integer>> getNumberOfZyAndBj() {
        return numberOfZyAndBj;
    }

    public void setNumberOfZyAndBj(Map<String, Map<String, Integer>> numberOfZyAndBj) {
        this.numberOfZyAndBj = numberOfZyAndBj;
    }

    public Map<String, Map<String, Integer>> getNumberOfNormalBj() {
        return numberOfNormalBj;
    }

    public void setNumberOfNormalBj(Map<String, Map<String, Integer>> numberOfNormalBj) {
        this.numberOfNormalBj = numberOfNormalBj;
    }
}
