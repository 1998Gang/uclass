package cqupt.jyxxh.uclass.pojo;

import java.util.List;

/**
 *
 * 课程学生名单，只是学生名单
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 18:30 2020/3/26
 */
public class ClassStuList {
    private List<ClassStuInfo> classStuInfoList;

    public List<ClassStuInfo> getClassStuInfoList() {
        return classStuInfoList;
    }

    public void setClassStuInfoList(List<ClassStuInfo> classStuInfoList) {
        this.classStuInfoList = classStuInfoList;
    }
}
