package cqupt.jyxxh.uclass.service;

import cqupt.jyxxh.uclass.dao.UclassUserMapper;
import cqupt.jyxxh.uclass.pojo.EduAccount;
import cqupt.jyxxh.uclass.pojo.UclassUser;
import cqupt.jyxxh.uclass.utils.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 用户操作类
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 5:53 2019/12/8
 */
@Service
public class UserService {


    @Autowired
    private UclassUserMapper uclassUserMapper;           //用户dao操作类

    @Autowired
    private Authentication authentication;              //统一身份认证操作类

    @Autowired
    private EduAccountService eduAccountService;          //教务账号操作类

    private final  String YES_BIND="y";
    private final  String NO_BIND="n";



    /**
     * 根据opneid判断用户是否绑定了教务账号
     * 如果该用户存在，直接判断是否绑定了教务账号。
     * 如果该用户不存在，先创建一个新用户，然后直接返回false（未绑定教务账户）。
     *
     * @param openid 微信临时身份验证码
     * @return true用户绑定了教务账号 false用户未绑定教务账号
     */
    public boolean isBind(String openid){
        //标识符
        boolean flage=false;


        // 1.根据openid查看在数据库中存在的条数
        int userNum = uclassUserMapper.numByOpenid(openid);
        // 2.判断，如果i为“1”则该用户为老用户，如果为“0”该用户为第一次使用
        switch (userNum){
            case 1 :{
                String isBindValue = uclassUserMapper.queryIsBindByOpenid(openid);
                flage="y".equals(isBindValue);
                break;
            }
            case 0:{
                UclassUser uclassUser=new UclassUser();
                uclassUser.setOpenid(openid);//用户openid
                uclassUser.setIs_bind("n");  //用户是否绑定 n 未绑定（新建用户）
                uclassUser.setFirst_use_time(new Date());
                uclassUser.setLast_use_time(new Date());
                //新建用户，向数据库插入新用户数据
                uclassUserMapper.insertUclassUser(uclassUser);
                //新建用户，此时可知未绑定，返回false
                flage= false;
                break;
            }
            default:{
                break;
            }
        }
        return flage;
    }


    /**
     * 返回老用户的教务账号类型
     * @param openid openid
     * @return t为老师，s为学生
     */
    public String getUserType(String openid){
        //1.获取用户绑定教务账号类型
        String bindType = uclassUserMapper.queryBindTypeByOpenid(openid);
        //2.返回类型
        return bindType;
    }


    /**
     * 获取用户实体，根据openid获取
     * 如果该用户存在，直接返回。
     * 如果该用户不存在，则新建一个用户并返回。
     * @param openid openid
     * @return UclassUser
     */
    public UclassUser getUser(String openid){
        //用户实体对象
        UclassUser uclassUser=null;

        // 1.根据openid查看在数据库中存在的条数
        int i = uclassUserMapper.numByOpenid(openid);
        // 2.判断，如果i为“1”则该用户为老用户，如果为“0”该用户为第一次使用
        switch (i){
            case 1 :{
                // 2.1 返回老用户数据
                uclassUser=uclassUserMapper.queryUserByOpenid(openid);
                break;
            }
            case 0:{
                // 2.2 新建用户，向数据库插入新用户数据
                uclassUser=new UclassUser();
                uclassUser.setOpenid(openid);              //用户openid
                uclassUser.setIs_bind(NO_BIND);                //用户是否绑定 n 未绑定（新建用户）
                uclassUser.setFirst_use_time(new Date());  //用户第一次使用时间
                uclassUser.setLast_use_time(new Date());
                uclassUserMapper.insertUclassUser(uclassUser);
                // 2.3 返回新用户数据
                break;
            }
            default:{
                break;
            }
        }
        return uclassUser;
    }


    /**
     * 为用户绑定教务账号
     * @param openid openid
     * @param ykth   统一身份认证码、一卡通号
     * @param password 统一身份认证密码
     * @return boolean，绑定是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean setBind(String openid,String ykth,String password){
        boolean flage=false;

        try {
            // 1.通过统一身份认证码（一卡通号）获取教务账号实体
            EduAccount eduAccount = eduAccountService.getEduAccountInfoByYKTH(ykth, password);
            // 2.获取微信用户实体
            UclassUser uclassUser = uclassUserMapper.queryUserByOpenid(openid);
            // 3.将教务账号绑定到微信用户上
            uclassUser.setIs_bind(YES_BIND);//更改绑定标识，该用户绑定了教务账户
            uclassUser.setUser_type(eduAccount.getAccountType());//设置用户绑定的教务账号的类型
            uclassUser.setBind_name(eduAccount.getName());//设置用户绑定的教务账户的姓名
            uclassUser.setBind_number(eduAccount.getNumber());//设置用户绑定的教务账号的学号或者教师号
            uclassUser.setBind_ykth(eduAccount.getYkth());//设置用户绑定的教务账号的（统一身份认证码）一卡通号
            uclassUser.setLast_use_time(new Date());  //设置最后一次操作时间

            // 4将更改之后的数据持久化到数据库
            //更新用户数据，添加绑定
            uclassUserMapper.updateUser(uclassUser);
            //将教务账户数据插入到数据库中，
            eduAccountService.insertEduAcoToDB(eduAccount);

            flage=true;
        }catch (Exception e){
            flage=false;
        }

        return flage;
    }
}
