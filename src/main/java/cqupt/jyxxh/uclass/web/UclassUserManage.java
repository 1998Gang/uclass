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

    private final GetInfoFromWxService getInfoFromWxService;    //去微信获取数据的service

    private final UserService userService;                     //用户信息操作类

    private final EduAccountService EduAccountService;         //教务账户信息操作类

    private final Authentication authentication;                //统一身份验证工具类

    @Autowired
    public UclassUserManage(GetInfoFromWxService getInfoFromWxService, UserService userService, EduAccountService EduAccountService, Authentication authentication) {
        this.getInfoFromWxService = getInfoFromWxService;
        this.userService = userService;
        this.EduAccountService = EduAccountService;
        this.authentication = authentication;
    }


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
                if (logger.isInfoEnabled()){
                    logger.info("【绑定接口（UclassUserManage.bind）】绑定失败!用户openid：[{}],已经绑定教务账号",openid);
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body("该微信用户已经绑定了教务账号");
            }

            // 4.判断用户输入的统一身份是否正确
            boolean istrue = authentication.ldapCheck(ykth, password);
            if (!istrue){
                // 4.1 用户输入的统一身份不正确，响应403
                // 日志
                if (logger.isInfoEnabled()){
                    logger.info("【绑定接口（UclassUserManage.bind）】绑定失败!用户openid：[{}]的统一身份：[{}]验证失败",openid,ykth);
                }
                return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("统一身份验证失败");
            }


            // 5.给用户添加教务账户绑定
            boolean isSetBind = userService.setBind(uclassuser, ykth, password);
            if (isSetBind){
                //日志
                if (logger.isInfoEnabled()){
                    logger.info("用户：[{}]绑定成功！统一认证码：[{}]",openid,ykth);
                }
                return ResponseEntity.status(HttpStatus.OK).body("绑定成功！");
            }



           /* // 3. 验证身份（一卡通账号密码） 账号密码是否正确
            boolean isTrue=authentication.ldapCheck(yktId,password);

            // 4.添加新用户
            if (isTrue){
                // 4.1身份验证成功 添加绑定
                boolean isAdd= addUserService.addUInfoAndUBind(openid,yktId,password);
                if (isAdd){
                    if (logger.isInfoEnabled()){
                        logger.info("【绑定接口（bind）】绑定成功！统一认证码:[{}]",yktId);
                    }
                    //4.1.1 绑定成功，http状态码响应 200
                    return  ResponseEntity.status(HttpStatus.OK).body("绑定成功");
                }else {
                    if (logger.isInfoEnabled()){
                        logger.info("【绑定接口（bind）】绑定失败！统一身份验证成功，但该微信用户已经绑定或是不支持用户类型（统一认证码不以”01“”16“开头），统一认证码：[{}]",yktId);
                    }
                    // 4.1.2 绑定失败（身份验证成功，该微信用户存在绑定），http状态码响应 409
                    return  ResponseEntity.status(HttpStatus.CONFLICT).body("绑定失败（统一身份验证成功），该微信用户存在绑定或是不支持用户类型（统一认证码不以”01“”16“开头）");
                }
            }else {
                //  4.2身份验证失败，http状态码响应 403
                if (logger.isInfoEnabled()){
                    logger.info("【绑定接口（bind）】绑定失败！身份验证失败,统一认证码：[{}]",yktId);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("绑定失败（统一身份验证失败）");
            }*/


        }catch (Exception e){
            logger.error("【绑定接口（bind）】绑定操作错误！统一认证码[{}]",ykth);
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
        //获取code
        String code=deleteInfo.get("code");

        try{

        /*if (logger.isDebugEnabled()){
            logger.debug("【删除用户接口（deldete）】 接收code：[{}]",code);
        }

        // 1.判断是否接收到code参数
        if ("00".equals(code)){
            if (logger.isWarnEnabled()){
                logger.warn("【删除用户接口（deldete）】 没有接收到code参数，删除失败。");
            }
            // 1.1 没有参数 返回400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("删除失败，没有接收到code参数");
        }


        // 2.根据code换取openid，去除引号
            String openid= getInfoFromWxService.getOpenid(code).replace("\"","");
            // 2.1判断oepnid是否正确
            if ("nullOpenid"==openid){
                if (logger.isInfoEnabled()){
                    logger.info("【删除用户接口（deldete）】删除绑定用户失败！ code:[{}]换取openid失败",code);
                }
                // 2.1 openid换取失败，可能原因是code不正确，http响应状态码 415
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("删除失败，可能因为code不正确或该code使用过，code："+code);

            }


        // 3.根据有openid删除相关用户数据（用户信息，用户绑定）
            boolean b = deleteUserService.deleteUserinfoAndBind(openid);
            //3.1 判断删除操作是否成功
            if (!b){
                if (logger.isInfoEnabled()){
                    logger.info("【删除用户接口（deldete）】删除绑定用户失败！ openid：[{}]",openid);
                }
                //删除绑定失败 返回 409
                return ResponseEntity.status(HttpStatus.CONFLICT).body("删除绑定用户失败");
            }

            if (logger.isInfoEnabled()){
                logger.info("【删除用户接口（deldete）】删除用户绑定成功！ openid:[{}]",openid);
            }
            //删除绑定成功 返回200
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("删除绑定用户成功");*/
        }catch (Exception e){
            logger.error("【删除用户接口（deldete）】删除用户绑定操作出错！",e);
        }

        // 4.服务器内部错误 响应 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除失败，服务器端内部未知错误");

    }
}
