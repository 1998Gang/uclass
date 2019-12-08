package cqupt.jyxxh.uclass.dao;

import cqupt.jyxxh.uclass.pojo.UclassUser;

/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 5:27 2019/12/8
 */
public interface UclassUserMapper {

    /**
     * 根据openid判断数据库中是否存在该用户
     * @param openid 微信用户唯一的身份凭证
     * @return   数据条数，在此处，不是0就是1
     */
     int numByOpenid(String openid);


    /**
     * 获取该用户绑定的教务账号类型，t为老师，s为学生
     * @param openid 微信用户唯一的身份凭证
     * @return    t为老师，s为学生
     */
    String queryBindTypeByOpenid(String openid);


    /**
     * 根据opneid查询该用户是否绑定教务账号
     * @param openid 微信用户唯一的身份凭证
     * @return  “y”绑定了，“n”未绑定
     */
    String queryIsBindByOpenid(String openid);


    /**
     * 新建一个用户数据
     * @param uclassUser 用户实体
     */
    void insertUclassUser(UclassUser uclassUser);


    /**
     * 获取用户实体
     * @param openid 微信用户唯一的身份凭证
     * @return UclassUser
     */
    UclassUser queryUserByOpenid(String openid);


    /**
     * 更新用户数据
     * @param uclassUser 用户实体
     */
    void updateUser(UclassUser uclassUser);
}
