package cqupt.jyxxh.uclass.dao;

import cqupt.jyxxh.uclass.pojo.ClassStuInfo;
import cqupt.jyxxh.uclass.pojo.qiandao.QianDaoResult;
import cqupt.jyxxh.uclass.pojo.qiandao.StuSingleRecord;
import org.apache.ibatis.annotations.Param;

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
     * @param qianDaoResult list集合，元素是QianDaoResult
     */
    void insertQiandaoResult(QianDaoResult qianDaoResult);

    /**
     * 将有有记录的学生数据插入到数据库中（包含缺勤|请假|迟到的）。
     *
     * @param classStuInfoList list集合，元素的ClassStudentInfo
     */
    void insertNoQDStuInfo(List<ClassStuInfo> classStuInfoList);

    /**
     * 学生获取自己某一门课的签到记录。
     * @param xhAndJxb map集合
     * @return List集合，元素SingleRecord,一个代表一次签到结果。
     */
    List<StuSingleRecord> getStuQdRecord(Map<String,String> xhAndJxb);

    /**
     * 获取某一门课程的签到记录。有哪些学生有缺勤或者迟到或者请假记录和响应的次数。
     * 例：
     * {
     * {xm:彭渝刚，xh：2017214033，qdzts：QJ,QQ}，
     * {xm:张海浪，xh：2017214121，qdzts：QQ,QQ,QQ}，
     * ......
     * }
     * @param jxb 教学案板你
     * @return Lsit<Map<String,String>>   list表示多个学生，map表示一个学生的某种状态。
     */
    List<Map<String,String>> getkcQdRecord(String jxb);

    /**
     * 教师个学生补签，类型为请假，未到人+1，请假人数-1
     * @param qdid 签到id
     */
    void updataResultQj(String qdid);

    /**
     * 教师给学生补签，类型为请假，修改改学生本次签到状态为QJ。
     * @param qdid 签到id
     * @param xh 学号
     */
    void updataNoQdStuQj(@Param("qdid") String qdid, @Param("xh") String xh);

    /**
     * 教师给学生补签，类型为迟到，未到人数-1，迟到人数+1
     * @param qdid 签到id
     */
    void updataResultCd(String qdid);


    /**
     * 教师给学生补签，类型为迟到，修改改学生本次签到状态为CD。
     * @param qdid 签到Id
     * @param xh 学号
     */
    void updataNoQdStuCd(@Param("qdid") String qdid, @Param("xh") String xh);

    /**
     * 教师给学生补签，类型为网络问题，未到人数-1 已到人数+1
     * @param qdid 签到ID
     */
    void updataResultWt(String qdid);

    /**
     * 教师给学生补签，类型为网络问题，将改学生的未到记录删除
     * @param qdid 签到id
     * @param xh 学号
     */
    void deleteNoQdStuWt(@Param("qdid") String qdid, @Param("xh") String xh);
}
