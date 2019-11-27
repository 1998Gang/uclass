package cqupt.jyxxh.uclass.dao;

import cqupt.jyxxh.uclass.pojo.BindContrast;

public interface BindContrastMapper {

    /**
     * 查询绑定信息
     *
     * @param openid  openid
     * @return   bindconyrast
     */
    BindContrast queryBindByOpenid(String openid);


    /**
     * 根据openid 查询绑定条数
     *
     * @param openid
     * @return
     */
    Integer numByOpenid(String openid);


    /**
     * 根据openid查询绑定用户类型  1为老师 0为学生
     * @param openid
     * @return
     */
    String queryBindTypeByOpenid(String openid);

    /**
     *插入一条绑定数据
     *
     * @param bindContrast
     * @return
     */
    void insertBindContrast(BindContrast bindContrast);

    void deleteBindConByOpenid(String openid);
}
