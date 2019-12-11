package cqupt.jyxxh.uclass.web;


import cqupt.jyxxh.uclass.pojo.UclassUser;
import cqupt.jyxxh.uclass.pojo.EduAccount;
import cqupt.jyxxh.uclass.service.*;

import cqupt.jyxxh.uclass.utils.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 *  小程序登陆入口
 *  使用RestFul风格
 *
 *  login方法（GET请求）：请求用户数据，如果数据库有该用户数据，返回用户数据，视为登陆成功。
 *  bind方法（POST请求）：新建用户数据，在数据库新建用户数据，创建成功，视为绑定成功。
 *  delete方法（DELETE请求）：删除用户数据，将数据库中存在的用户数据删除，视为解绑。  PS：调用该接口时，请求方法为POST，添加请求参数，_method=DELETE,将post请求转换为delete请求。
 *
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 23:29 2019/11/5
 */

@Controller
@RequestMapping(value = "usermanage")
public class UclassUserManage {


    private final Logger logger= LoggerFactory.getLogger(UclassUserManage.class);    //日志（slf4j搭配logback）

    @Autowired
    private  GetInfoFromWxService getInfoFromWxService;    //去微信获取数据的service

    @Autowired
    private  UserService userService;                     //用户信息操作类

    @Autowired
    private  EduAccountService EduAccountService;         //教务账户信息操作类

    @Autowired
    private  Authentication authentication;                //统一身份验证工具类




    /**
     * 用户登陆，调用此方法。
     * 登陆成功，返回绑定的教务账户信息。
     * 登陆失败，会自动创建一个新用户，未绑定教务账户。
     *
     * @param code   微信小程序临时身份验证码
     * @return 返回值为EduAccount实体，教务账户信息，（学生或者老师）
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<EduAccount> login(@RequestParam("code") String code){
        //日志
        if (logger.isDebugEnabled()){
            logger.debug("【登陆接口（UclassUserManage.login）】接收参数code：[{}]",code);
        }

        try {
            // 1.获取openid
            String openid = getInfoFromWxService.getOpenid(code);
            if (null==openid||"".equals(openid)){
                //日志
                if (logger.isDebugEnabled()){
                    logger.debug("【登陆接口（UclassUserManage.login）】获取openid失败");
                }
                if (logger.isInfoEnabled()){
                    logger.info("用户：[{}]登陆失败！openid获取失败，code无效：[{}]",openid,code);
                }
                //获取openid失败，响应415
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
            }

            // 2.通过openid获取用户实体
            UclassUser uclassUser = userService.getUser(openid);

            // 3.判断该用户是否绑定了教务账户
            if (userService.isBind(uclassUser)){

                // 3.1 绑定了教务账户，获取教务账户数据并返回 响应200。

                EduAccount eduAccount = EduAccountService.getUserEduAccountInfo(uclassUser);

                //日志
                if (logger.isInfoEnabled()){
                    logger.info("用户：[{}]登陆成功！教务账号:[{},{}.{}]",openid,eduAccount.getName(),eduAccount.getNumber(),eduAccount.getYkth());
                }

                return ResponseEntity.status(HttpStatus.OK).body(eduAccount);

            }else {

                // 3.2 用户没有绑定教务账户 响应401
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("用户：[{}]登陆失败！未绑定教务账户",openid);
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

            }
        }catch ( Exception e){
            e.printStackTrace();
        }

        //出现位置异常，响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }


    /**
     *为用户添加教务账号绑定调用本方法
     *
     * 接收code（微信临时身份验证码）
     *    yktId（学校统一认证码）
     *    password（统一认证身份密码）
     *
     * @param bindInfo1 {code,userid,password}
     * @return 请求结果提示
     */
    @RequestMapping(method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public ResponseEntity<String> bind(@RequestBody Map<String,String> bindInfo1){
        String code;
        String ykth= null;
        String password;
        try {
            // 1. 获取请求参数
             code= bindInfo1.get("code");
             ykth= bindInfo1.get("yktId");
             password= bindInfo1.get("password");
             //日志
            if (logger.isDebugEnabled()){
                logger.debug("【绑定接口（UclassUserManage.bind）】 接收参数code[{}],ykth[{}]",code,ykth);
            }

            // 2. 通过code获取openid，并校验该openid是否合法。
            String openid = getInfoFromWxService.getOpenid(code);
            if (null==openid||openid.equals("")){
                //日志
                if (logger.isDebugEnabled()){
                    logger.debug("【绑定接口（UclassUserManage.bind）】获取openid失败");
                }
                //获取openid失败，响应415
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("绑定失败，可能是无效code");
            }

            // 3. 根据openid获取用户信息，并判断该用户是否存在绑定
            UclassUser uclassuser = userService.getUser(openid);
            if ("y".equals(uclassuser.getIs_bind())){
                // 4.1 用户已经存在绑定,响应409.
                //日志
                if (logger.isDebugEnabled()){
                    logger.debug("【绑定接口（UclassUserManage.bind）】绑定失败!用户openid：[{}],已经绑定教务账号",openid);
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body("该微信用户已经绑定了教务账号");
            }

            // 4.判断用户输入的统一身份是否正确
            boolean istrue = authentication.ldapCheck(ykth, password);
            if (!istrue){
                // 4.1 用户输入的统一身份不正确，响应403
                // 日志
                if (logger.isDebugEnabled()){
                    logger.debug("【绑定接口（UclassUserManage.bind）】绑定失败!用户openid：[{}]的统一身份：[{}]验证失败",openid,ykth);
                }
                return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("统一身份验证失败");
            }

            // 5.给用户添加教务账户绑定
            boolean isSetBind = userService.setBind(uclassuser, ykth, password);
            if (isSetBind) {
                //日志
                if (logger.isInfoEnabled()) {
                    logger.info("用户：[{}]绑定成功！统一认证码：[{}]", openid, ykth);
                }
                return ResponseEntity.status(HttpStatus.OK).body("绑定成功！");
            }else {
                //日志
                if (logger.isErrorEnabled()){
                    logger.info("用户：[{}]绑定失败！统一认证码：[{}]",openid,ykth);
                }
            }

        }catch (Exception e){
            logger.error("【绑定接口（bind）】出现未知错误！统一认证码[{}]",ykth,e);
        }

        //  5，服务器内部错误，http状态码响应 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("绑定失败，服务器内部错误！");
    }


    /**
     * 删除用户绑定数据
     *
     * 前端切换绑定操作，调用本方法 delete（DELETE请求），先删除用户绑定数据，在调用 bind（POST请求）添加新绑定
     *
     * @param deleteInfo 微信小程序临时身份验证码
     * @return 请求结果提示信息
     */
    @RequestMapping(method = RequestMethod.DELETE,produces = "application/json;charset=utf-8")
    public ResponseEntity<String> delete(@RequestBody Map<String,String> deleteInfo){

        String code=null;

        try{
            // 1.获取参数code
            code =deleteInfo.get("code");
            //日志
            if (logger.isDebugEnabled()){
                logger.debug("【删除绑定接口】接收参数code：[{}]",code);
            }


            // 2.根据code换取openid，去除引号
            String openid= getInfoFromWxService.getOpenid(code);
            // 判断oepnid是否符合要求
            if (null==openid||"".equals(openid)){
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("【删除绑定接口】openid获取失败！ 可能是无效code:[{}]",code);
                }
                // openid换取失败，可能原因是code不正确，http响应状态码 415
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("删除绑定失败！可能因为code不合法，code："+code);

            }

            // 3.根据openid获取用户实体
            UclassUser uclassUser = userService.getUser(openid);
            //获取姓名，学号或者教师号，一卡通号 用于日志书写
            String name=uclassUser.getBind_name();
            String bind_number = uclassUser.getBind_number();
            String bind_ykth = uclassUser.getBind_ykth();

            // 4.删除用户绑定的教务账户
            boolean istrue = userService.deleteBind(uclassUser);
            if (istrue){
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("用户；[{}]删除绑定成功！删除的教务账户：[{},{},{}]",openid,name,bind_number,bind_ykth);
                }
                //删除绑定成功，响应200
                return ResponseEntity.status(HttpStatus.OK).body("删除绑定成功！");
            }else {
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("用户；[{}]删除绑定失败！教务账户：[{},{},{}]",openid,uclassUser.getBind_name(),uclassUser.getBind_number(),uclassUser.getBind_ykth());
                }
                //删除绑定失败，响应409
                return  ResponseEntity.status(HttpStatus.CONFLICT).body("删除绑定失败！");
            }


        }catch (Exception e){
            logger.error("【删除用户接口（deldete）】删除用户绑定操作出错！",e);
        }

        // 4.服务器内部错误 响应 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除失败！服务器端内部未知错误！");

    }
}
