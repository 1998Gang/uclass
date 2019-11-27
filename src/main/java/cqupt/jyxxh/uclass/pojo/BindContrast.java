package cqupt.jyxxh.uclass.pojo;

import java.util.Date;

/**
 * 绑定信息类
 *
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:46 2019/11/1
 */
public class BindContrast {

    private String openid; //绑定表openid
    private String bind_name; //绑定用户的姓名
    private String  user_type;//绑定类型   1为老师  0为学生
    private String bind_number;//绑定用户的学号或者教师号
    private String bind_ykth;//绑定用户的一卡通号
    private Date first_bind_time; //第一次绑定时间

    @Override
    public String toString() {
        return "BindContrast{" +
                "openid='" + openid + '\'' +
                ", bind_name='" + bind_name + '\'' +
                ", user_type='" + user_type + '\'' +
                ", bind_number='" + bind_number + '\'' +
                ", bind_ykth='" + bind_ykth + '\'' +
                ", first_bind_time=" + first_bind_time +
                '}';
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getBind_name() {
        return bind_name;
    }

    public void setBind_name(String bind_name) {
        this.bind_name = bind_name;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getBind_number() {
        return bind_number;
    }

    public void setBind_number(String bind_number) {
        this.bind_number = bind_number;
    }

    public String getBind_ykth() {
        return bind_ykth;
    }

    public void setBind_ykth(String bind_ykth) {
        this.bind_ykth = bind_ykth;
    }

    public Date getFirst_bind_time() {
        return first_bind_time;
    }

    public void setFirst_bind_time(Date first_bind_time) {
        this.first_bind_time = first_bind_time;
    }
}
