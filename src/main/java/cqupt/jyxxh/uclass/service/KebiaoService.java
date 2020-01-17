package cqupt.jyxxh.uclass.service;




import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.pojo.KeChengInfo;
import cqupt.jyxxh.uclass.utils.GetDataFromJWZX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.io.IOException;
import java.util.ArrayList;

/**
 * 获取用户课表
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 19:08 2019/12/2
 */

@Service
public class KebiaoService {

    Logger logger= LoggerFactory.getLogger(KebiaoService.class);

    @Autowired
    private GetDataFromJWZX getDataFromJWZX;    //去教务在线获取数据的工具类

    @Autowired
    private RedisService redisService;          //reids操作类



    /**
     *  根据学号或者教师号获取课表
     *
     * @param number 学号或者教师号
     * @param type  类别，"s"代表学生，"t"代表老师
     * @return ArrayList<ArrayList<ArrayList<KeChengInfo>>>
     */
    public String getKebiao(String number, String type) throws IOException {

        //课表 json格式字符串
        String kebiao=null;

        //实例化json操作对象
        ObjectMapper objectMapper=new ObjectMapper();

        //1.先去redis中获取
        try{
            String data = redisService.getKebiao(number);
            if (!"false".equals(data)){
                //从缓存获取数据成功！
                kebiao=data;
                //直接返回。
                return kebiao;
            }
        }catch (Exception e){
            logger.debug("【获取课表缓存（KebiaoService.getkebiao）】出现未知错误！");
        }

        //2.根据学号或者教师号去获取教务在线的课表页（html）
        switch (type){
            //学生
            case "s":{
                ArrayList<ArrayList<ArrayList<KeChengInfo>>> stukebiaoByXh = getDataFromJWZX.getStukebiaoByXh(number);
                //将嵌套集合转位json字符串
                 kebiao = objectMapper.writeValueAsString(stukebiaoByXh);

                 //放进缓存。
                try {
                    boolean b = redisService.setKeBiao(number, kebiao);
                }catch (Exception e){
                    logger.debug("【添加学生课表缓存（KebiaoService.getkebiao）】出现未知错误！");
                }
                break;
            }
            //老师
            case "t":{
                ArrayList<ArrayList<ArrayList<KeChengInfo>>> teaKebiaoByTeaId = getDataFromJWZX.getTeaKebiaoByTeaId(number);
                //将嵌套集合转位json字符串
                kebiao = objectMapper.writeValueAsString(teaKebiaoByTeaId);

                //将结果放进缓存。
               try {
                    boolean b = redisService.setKeBiao(number, kebiao);
                }catch (Exception e){
                    logger.debug("【添加教师课表缓存（KebiaoService.getkebiao）】出现未知错误！");
                }
                break;
            }
        }
        return kebiao;
    }
}
