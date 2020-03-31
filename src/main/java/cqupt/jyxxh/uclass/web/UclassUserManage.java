package cqupt.jyxxh.uclass.web;


import cqupt.jyxxh.uclass.pojo.user.UclassUser;
import cqupt.jyxxh.uclass.pojo.user.EduAccount;
import cqupt.jyxxh.uclass.service.*;

import cqupt.jyxxh.uclass.utils.Authentication;
import cqupt.jyxxh.uclass.utils.GetDataFromWX;
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
 *  login方法（GET请求）：接收code，换openid，判断用户是否绑定了教务账户？
 *                                        如果已经绑定就返回教务账户数据，视为登陆成功。
 *                                        如果没有绑定教务账户，响应402，视为登陆失败。
 *
 *  bind方法（POST请求）：接收code、ykth、password。为用户绑定教务账户。
 *                                        如果同一个用户，多次绑定。只要教务账户账号密码正确，会绑定后一次请求的教务账户。
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 23:29 2019/11/5
 */

@Controller
@RequestMapping(value = "usermanage")
public class UclassUserManage {


    /**
     *
     */
    private final Logger logger= LoggerFactory.getLogger(UclassUserManage.class);


    /**
     * 用户信息操作类
     */
    @Autowired
    private  UserService userService;

    /**
     * 教务账户信息操作类
     */
    @Autowired
    private  EduAccountService EduAccountService;

    /**
     * 验证统一身份的工具类
     */
    @Autowired
    private  Authentication authentication;
    /**
     * 从微信获取数据的工具类
     */
    @Autowired
    private  GetDataFromWX getDataFromWX;





    /**
     * 用户登陆，调用此方法。
     * 登陆成功，返回绑定的教务账户信息。响应200。
     * 登陆失败，会自动创建一个新用户（未绑定教务账户）。响应错误代码。
     *
     * @param code   微信小程序临时身份验证码
     * @return 返回值为EduAccount实体，教务账户信息，（学生或者老师）
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<EduAccount> login(@RequestParam("code") String code){
        //日志
        if (logger.isDebugEnabled()){
            logger.debug("登陆接口 接收参数code：[{}]",code);
        }

        try {
            // 1.获取openid
            String openid = getDataFromWX.getOpenid(code);
            if (null==openid||"".equals(openid)){
                //日志
                if (logger.isDebugEnabled()){
                    logger.debug("登陆接口 获取openid失败");
                }
                if (logger.isInfoEnabled()){
                    logger.info("用户：[{}]登陆失败！openid获取失败，code无效：[{}]",openid,code);
                }
                //获取openid失败，响应415
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
            }

            // 2.通过openid获取用户实体数据
            UclassUser uclassUser = userService.getUser(openid);

            // 3.判断该用户是否绑定了教务账户
            if (userService.isBind(uclassUser)){

                // 3.1 绑定了教务账户，获取教务账户数据并返回 响应200。
                EduAccount eduAccount = EduAccountService.getUserEduAccountInfo(uclassUser);

                //日志
                if (logger.isInfoEnabled()){
                    logger.info("用户：[{}]登陆成功！教务账号:[{},{},{}]",openid,eduAccount.getName(),eduAccount.getNumber(),eduAccount.getYkth());
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
            if ("Identity is overdue".equals(e.getLocalizedMessage())){
                //发生此异常说明，用户身份过期，需重新登陆！响应410
                //日志
                logger.info("用户身份过期，需重新绑定！");
                return ResponseEntity.status(HttpStatus.GONE).body(null);
            }
            //日志
            logger.error("用户登陆 出现未知错误！",e);
        }

        //出现未知异常，响应500
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
        String openid=null;
        try {
            // 1. 获取请求参数
             code= bindInfo1.get("code");
             ykth= bindInfo1.get("ykth");
             password= bindInfo1.get("password");
             //日志
            if (logger.isDebugEnabled()){
                logger.debug("【绑定接口（UclassUserManage.bind）】 接收参数code[{}],ykth[{}]",code,ykth);
            }

            // 2. 通过code获取openid，并校验该openid是否合法。
            openid = getDataFromWX.getOpenid(code);
            if (null==openid||openid.equals("")){
                //日志
                if (logger.isDebugEnabled()){
                    logger.debug("【绑定接口（UclassUserManage.bind）】获取openid失败");
                }
                //获取openid失败，响应415
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("绑定失败，可能是无效code");
            }


            // 3.判断用户输入的统一身份是否正确
            boolean istrue = authentication.ldapCheck(ykth, password);
            if (!istrue){
                // 4.1 用户输入的统一身份不正确，响应403
                // 日志
                if (logger.isDebugEnabled()){
                    logger.debug("【绑定接口（UclassUserManage.bind）】绑定失败!用户openid：[{}]的统一身份：[{}]验证失败",openid,ykth);
                }
                if (logger.isInfoEnabled()){
                    logger.info("用户：[{}]绑定失！身份验证失败，一卡通号：[{}]！",openid,ykth);
                }
                return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("统一身份验证失败");
            }


            // 4. 根据openid获取用户信息，并判断该用户是否存在绑定。
            UclassUser uclassuser = userService.getUser(openid);
            if ("y".equals(uclassuser.getIs_bind())){
                // 4.1 用户已经存在绑定,删除旧绑定。(uclassuser表中，”y“代表已经绑定了教务账户了。”n“代表没有绑定)
                userService.deleteBind(uclassuser);
                //日志
                if (logger.isDebugEnabled()){
                    logger.debug("【绑定接口（UclassUserManage.bind）】删除旧绑定账户成功!用户openid：[{}]",openid);
                }
            }

            // 5.给用户添加新的教务账户绑定
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

            //不支持的用户类型 401，如非教师非学生用户
            if ("Unsupported academic administration account".equals(e.getLocalizedMessage())){
                logger.error("用户：[{}]绑定失败！统一认证码:[{}]是不支持的用户类型！",openid,ykth);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("不支持的教务账户类型");
            }

            logger.error("【绑定接口（bind）】出现未知错误！统一认证码[{}]",ykth,e);
        }

        //  5，服务器内部错误，http状态码响应 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("绑定失败，服务器内部错误！");
    }
}
