package cqupt.jyxxh.uclass.pojo.user;

import java.util.Date;

/**
 * u课堂用户类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 22:46 2019/11/1
 */
public class UclassUser {

    private String openid; //唯一的用户识别码
    private String is_bind;//标识符   该用户是否绑定了教务账号
    private String user_type;//绑定类型   "t"为老师  "s"为学生
    private String bind_name; //绑定用户的姓名
    private String bind_number;//用户绑定的教务账号的学号或者教师号
    private String bind_ykth;//绑定用户的一卡通号
    private Date first_use_time; //该用户第一次使用u课堂时间
    private Date last_use_time; //该用户最后一次使用u课堂时间

    @Override
    public String toString() {
        return "UclassUser{" +
                "openid='" + openid + '\'' +
                ", is_bind='" + is_bind + '\'' +
                ", user_type='" + user_type + '\'' +
                ", bind_name='" + bind_name + '\'' +
                ", bind_number='" + bind_number + '\'' +
                ", bind_ykth='" + bind_ykth + '\'' +
                ", first_use_time=" + first_use_time +
                ", last_use_time=" + last_use_time +
                '}';
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getIs_bind() {
        return is_bind;
    }

    public void setIs_bind(String is_bind) {
        this.is_bind = is_bind;
    }


    /**
     *
     * @return t为老师，s为学生
     */
    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getBind_name() {
        return bind_name;
    }

    public void setBind_name(String bind_name) {
        this.bind_name = bind_name;
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

    public Date getFirst_use_time() {
        return first_use_time;
    }

    public void setFirst_use_time(Date first_use_time) {
        this.first_use_time = first_use_time;
    }

    public Date getLast_use_time() {
        return last_use_time;
    }

    public void setLast_use_time(Date last_use_time) {
        this.last_use_time = last_use_time;
    }
}
