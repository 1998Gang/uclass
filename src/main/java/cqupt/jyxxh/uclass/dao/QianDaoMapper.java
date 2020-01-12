package cqupt.jyxxh.uclass.dao;

import cqupt.jyxxh.uclass.pojo.ClassStudentInfo;
import cqupt.jyxxh.uclass.pojo.QianDaoResult;
import cqupt.jyxxh.uclass.pojo.SingleRecord;

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
     * 获取学生某一门课的签到记录
     * @param xhAndJxb map集合
     * @return List集合，元素SingleRecord,一个代表一次签到结果。
     */
    List<SingleRecord> getStuQdRecord(Map<String,String> xhAndJxb);

}
