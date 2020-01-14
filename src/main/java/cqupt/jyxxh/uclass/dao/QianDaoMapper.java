package cqupt.jyxxh.uclass.dao;

import cqupt.jyxxh.uclass.pojo.ClassStudentInfo;
import cqupt.jyxxh.uclass.pojo.QianDaoResult;
import cqupt.jyxxh.uclass.pojo.StuSingleRecord;

import java.util.List;
import java.util.Map;

/**
 * 签到结果
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 18:04 2020/1/11
 */
public interface QianDaoMapper {

    /**
     * 将签到结果插入到数据库
     *
     * @param qianDaoResultList list集合，元素是QianDaoResult
     */
    void insertQiandaoResult(List<QianDaoResult> qianDaoResultList);

    /**
     * 将有有记录的学生数据插入到数据库中（包含缺勤|请假|迟到的）。
     *
     * @param classStudentInfoList list集合，元素的ClassStudentInfo
     */
    void insertClassStuInfo(List<ClassStudentInfo> classStudentInfoList);

    /**
     * 学生获取自己某一门课的签到记录。
     * @param xhAndJxb map集合
     * @return List集合，元素SingleRecord,一个代表一次签到结果。
     */
    List<StuSingleRecord> getStuQdRecord(Map<String,String> xhAndJxb);

    /**
     * 获取某一门课程的签到记录。有哪些学生有缺勤或者迟到或者请假记录和响应的次数。
     * 例：
     * {{xm:彭渝刚，xh：2017214033，CD：1}，
     * {xm:彭渝刚，xh：2017214033，QQ：1}，
     * {xm:鞠青松，xh：2017214032，CD：1}，
     * ......
     * }
     * @param jxb 教学案板你
     * @return Lsit<Map<String,String>>   list表示多个学生，map表示一个学生的某种状态。
     */
    List<Map<String,String>> getkcQdRecord(String jxb);

}
