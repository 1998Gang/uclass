package cqupt.jyxxh.uclass.dao;

import cqupt.jyxxh.uclass.pojo.ClassStuInfo;
import cqupt.jyxxh.uclass.pojo.tiwen.KeChengTwOneStuRecord;
import cqupt.jyxxh.uclass.pojo.tiwen.StuTWRecord;
import cqupt.jyxxh.uclass.pojo.tiwen.Twjl;

import java.util.List;
import java.util.Map;

/**
 * 提问功能的dao操作接口
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 21:15 2020/2/13
 */

public interface TiWenMapper {

    /**
     * 将提问记录持久化到mysql数据库
     * @param twjl 提问记录
     */
    void insertTWJL(Twjl twjl);

    /**
     * 将所有没有回答问题的学生持久化到mysql数据库
     * @param allNoAnStu 没有回答问题的学生集合
     */
    void insertNoAnStuList(List<ClassStuInfo> allNoAnStu);

    /**
     * 通过学号和教学班，获取某个学生某门课程的回答记录
     * @param xhAndJxb 学号和教学班 例：{xh=2017214033,jxb=A80809808090}
     *
     * @return  回答记录 集合
     */
    List<StuTWRecord> getStuTWRecord(Map<String,String> xhAndJxb);

    /**
     * 通过教学班获取该门课程有未答题记录的学生数据
     *
     * @param jxb 教学班号
     * @return KeChengTWOneStuRecord
     */
    List<KeChengTwOneStuRecord> getKCTWHistory(String jxb);

    /**
     * 通过教学班获取该班的历史提问次数
     * @param jxb 教学班
     * @return 历史提问次数
     */
    int getKcAskTimes(String jxb);
}
