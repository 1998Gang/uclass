package cqupt.jyxxh.uclass.pojo;

/**
 * 用户信息接口 学生类、老师类都实现该接口,方便返回信息。
 *
 */
public interface UserInfo {

    String getOpenid();

    void setOpenid(String openid);

    String getYkth();
}
