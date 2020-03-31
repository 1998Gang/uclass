package cqupt.jyxxh.uclass.dao;

import cqupt.jyxxh.uclass.pojo.Feeback;


/**
 *
 * 问题反馈Dao
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 10:53 2020/2/22
 */
public interface ComponentsMapper {
    /**
     * 将用户反馈的信息添加进缓存
     * @param feeback 反馈信息实体类
     */
    void insterFeebackInfo(Feeback feeback);
}
