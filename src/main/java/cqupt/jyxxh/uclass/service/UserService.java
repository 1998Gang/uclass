package cqupt.jyxxh.uclass.service;

import cqupt.jyxxh.uclass.dao.UclassUserMapper;
import cqupt.jyxxh.uclass.pojo.user.EduAccount;
import cqupt.jyxxh.uclass.pojo.user.UclassUser;
import cqupt.jyxxh.uclass.utils.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    /**
     * 日志
     */
    private Logger logger= LoggerFactory.getLogger(UserService.class);

    /**
     * 用户dao操作类
     */
    @Autowired
    private  UclassUserMapper uclassUserMapper;

    /**
     * 教务账号操作类
     */
    @Autowired
    private  EduAccountService eduAccountService;

    /**
     * 加解密工具
     */
    @Autowired
    private  EncryptionUtil encryptionUtil;

    /**
     * 用户存在绑定了教务账号的标识
     */
    private final  String YES_BIND="y";

    /**
     * 用户没绑定教务账户的标识
     */
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
                //用户openid
                uclassUser.setOpenid(openid);
                //用户是否绑定 n 未绑定（新建用户）
                uclassUser.setIs_bind("n");
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
     * 接收用户实体（uclassUser）判断用户是否存在绑定
     * @param uclassUser 用户实体
     * @return boolean
     */
    public boolean isBind(UclassUser uclassUser){
        //标识符
        boolean flage;

        //1.获取用户实体的绑定标识 y表示绑定了教务账户 n表示没有绑定教务账户
        String is_bind = uclassUser.getIs_bind();
        //2.判断绑定标识符
        flage= "y".equals(is_bind);

        return flage;
    }


    /**
     * 返回老用户的教务账号类型
     * @param openid openid
     * @return t为老师，s为学生
     */
    public String getUserType(String openid){
        //1.获取用户绑定教务账号类型
        //2.返回类型
        return uclassUserMapper.queryBindTypeByOpenid(openid);
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
        // 2.判断，如果i为“1”则该用户为老用户(数据库存在记录)，如果为“0”该用户为第一次使用（数据库不存在记录）
        switch (i){
            case 1 :{
                // 2.1 返回老用户数据
                uclassUser=uclassUserMapper.queryUserByOpenid(openid);
                break;
            }
            case 0:{
                // 2.2 新建一个用户，并向数据库插入新用户数据
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
     * @param uclassUser 用户实体（未绑定教务账户的）
     * @param ykth   统一身份认证码、一卡通号
     * @param password 统一身份密码
     * @return boolean，绑定是否成功
     */
    public boolean setBind(UclassUser uclassUser,String ykth,String password) throws Exception {
        //本方法的返回标识
        boolean flage;
        //教务账户实体
        EduAccount eduAccount;

        try {
            // 1.通过一卡通号判断u课堂后台数据库（uclass_students_info、uclass_teacher_info）中有没有该教务用户
            boolean isIn = eduAccountService.isEduAccountInDB(ykth);

            // 2.判断，如果数据库中有就在数据库中取，如果没有就取教务在线爬取并存入本地数据库。
            if (isIn){
                //2.1.1数据库中有，通过一卡通号从数据库获取数据。
                 eduAccount = eduAccountService.getEduAccountFromDB(ykth);
                // 2.1.2 将密码加密!!!!!
                String password_encrypt = encryptionUtil.encrypt(password);
                //2.1.3为该教务账户添加统一身份认证密码，并更新数据库。因为从数据库中获取的教务账户此时是无统一认证码密码的（解除绑定时候，保留基本数据，但会删除密码）。
                eduAccount.setPassword(password_encrypt);
                //2.1.4 将教务账号的密码更新到数据库
                eduAccountService.addPassword(eduAccount);

            }else {
                //2.2.1 数据库中没有，通过统一身份认证码（一卡通号）获取教务账号实体
                eduAccount = eduAccountService.getEduAccountFromJWZX(ykth,password);
                // 2.1.2 将密码加密
                String password_encrypt = encryptionUtil.encrypt(password);
                // 2.2.3为教务账户添加一卡通号，和密码。
                eduAccount.setPassword(password_encrypt);
                eduAccount.setYkth(ykth);
                //2.2.4将教务账户数据插入到数据库中.
                eduAccountService.insertEduAccountToDB(eduAccount);
            }

            // 3.将教务账号绑定到微信用户上
            //更改绑定标识，该用户绑定了教务账户
            uclassUser.setIs_bind(YES_BIND);
            //设置用户绑定的教务账号的类型
            uclassUser.setUser_type(eduAccount.getAccountType());
            //设置用户绑定的教务账户的姓名
            uclassUser.setBind_name(eduAccount.getName());
            //设置用户绑定的教务账号的学号或者教师号
            uclassUser.setBind_number(eduAccount.getNumber());
            //设置用户绑定的教务账号的（统一身份认证码）一卡通号
            uclassUser.setBind_ykth(eduAccount.getYkth());
            //设置最后一次操作时间
            uclassUser.setLast_use_time(new Date());

            // 4.将更改之后的用户数据持久化到数据库
            uclassUserMapper.updateUser(uclassUser);

            // 5.成功
            flage=true;
        }catch (Exception e){
            if (logger.isErrorEnabled()){
                logger.error("【添加绑定（UserService.setBind）】绑定失败！用户：[{}],统一身份认证码：[{}]",uclassUser.getOpenid(),ykth);
            }
            //捕获异常，抛出！(这里捕获的异常是捕获的  不支持账户的异常。)  重要！！！！
            throw e;
        }
        return flage;
    }


    /**
     * 删除用户绑定的教务账号
     * @param uclassUser 教务账号实体
     * @return boolean
     */
    public boolean deleteBind(UclassUser uclassUser) {
        boolean flage;

        try {
            // 1.删除用户绑定的教务账户的密码.
            String bind_ykth = uclassUser.getBind_ykth();
            String user_type = uclassUser.getUser_type();
            eduAccountService.deletePassword(bind_ykth,user_type);

            // 2.删除用户的绑定数据（更改用户实体（uclassUser）数据）
            uclassUser.setIs_bind(NO_BIND); //将是否绑定教务账户的标识改为n
            uclassUser.setUser_type("");//绑定教务用户类型为空
            uclassUser.setBind_name("");//绑定的教务用户的姓名为空
            uclassUser.setBind_ykth("");//绑定的教务账户的一卡通号为空
            uclassUser.setBind_number("");//绑定的教务账户的学号或者教师号为空
            uclassUser.setLast_use_time(new Date());//该账户最后一次操作时间

            // 3.将更改后的数据持久化到数据库
            uclassUserMapper.updateUser(uclassUser);

            // 3.更改标识符(删除绑定成功)
            flage=true;
        }catch (Exception e){
            flage=false;
            logger.error("【删除绑定操作（deleteBind）】未知错误！");
        }
        return flage;
    }
}
