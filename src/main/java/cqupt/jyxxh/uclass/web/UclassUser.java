package cqupt.jyxxh.uclass.web;


import cqupt.jyxxh.uclass.pojo.Student;
import cqupt.jyxxh.uclass.pojo.Teacher;
import cqupt.jyxxh.uclass.pojo.UserInfo;
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
@RequestMapping(value = "user")
public class UclassUser {


    final Logger logger= LoggerFactory.getLogger(UclassUser.class);    //日志（slf4j搭配logback）

    @Autowired
    private GetInfoFromWx getInfoFromWx;    //去微信获取数据的service

    @Autowired
    private OperationUserInfo operationUserInfo;  //操作用户数据的service

    @Autowired
    private OperationBind operationBind;           //与绑定有关的操作 service

    @Autowired
    private Authentication authentication;        //身份验证操作的封装 对接学校的统一身份认证系统（使用的是LDAP）  utils

    @Autowired
    private DeleteUser deleteUser;                  //删除用户的操作（service）

    @Autowired
    private AddUser addUser;                        //添加新用户（service）

    /**
     * 用户登陆，调用此方法
     * 接收code，换取openid，去绑定表里找有没有记录该openid的绑定，如果有，说明已经绑定过，返回该用户的学生/老师信息，登陆成功。
     *                       绑定表里没有记录该openid的绑定，说明是新微用户，还没有绑定学生/老师账号，响应404状态码，登陆失败，绑定之后在调用该方法。
     *
     * @param code   微信小程序临时身份验证码
     * @return 返回值为suerinfo实体，用户信息，（学生或者老师）
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<UserInfo> login(@RequestParam("code") String code){
        try {

            if (logger.isDebugEnabled()){
                logger.debug("【登陆接口（login）】接收参数code：[{}]",code);
            }

            // 1.用code去微信后台换取OpenID,同时去除双引号
            String opneId =getInfoFromWx.getOpenid(code).replace("\"","");
               // 1.1 判断返回openid是否为空值
            if ("nullOpenid"==opneId){

                if (logger.isInfoEnabled()){
                    logger.info("【登陆接口（login）】code:[{}]换取openid失败，原因可能是无效code",code);
                }
                //1.1.1 openid查询失败，http状态码响应 415
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
            }else {

                if (logger.isDebugEnabled()){
                    logger.debug("【登陆接口（login）】code:[{}]换回的openid:[{}]",code,opneId);
                }
            }


            // 2.根据openid查询是否绑定
            Boolean isBind= operationBind.isBind(opneId);

            if (logger.isDebugEnabled()){
                logger.debug("【登陆接口（login）】openid:[{}]是否存在绑定数据？{}",opneId,isBind);
            }

            // 3.判断是否绑定。
            if (isBind){
                // 3.1已经绑定，根据绑定信息判断用户类型 “01”为老师 “16”为学生
                String typeNumber= operationBind.bindType(opneId);
                switch (typeNumber){
                    case "01":{
                        // 3.1.1 老师用户，根据openid查询教师信息，并返回。
                        Teacher teacher=operationUserInfo.getTeacherInfoByOpenid(opneId);

                        if (logger.isInfoEnabled()){
                            logger.info("【登陆接口（login）】登陆成功，老师[{},{},{}]",teacher.getTeaName(),teacher.getTeaId(),teacher.getOpenid());
                        }
                        // 3.1.1.1查询成功，http状态码响应 200 同时返回teacher实体
                        return ResponseEntity.status(HttpStatus.OK).body((UserInfo) teacher);
                    }
                    case "16":{
                        // 3.1.2 学生用户，根据openid查询学生信息，并返回。
                        Student student = operationUserInfo.getStudentInfoByOpenid(opneId);

                        if (logger.isInfoEnabled()){
                            logger.info("【登陆接口（login）】登陆成功，学生[{},{},{}]",student.getXm(),student.getXh(),student.getOpenid());
                        }
                        // 3.1.2.1 查询成功，http状态码响应 200 同时返回student实体
                        return ResponseEntity.status(HttpStatus.OK).body((UserInfo) student);
                    }
                }
            }else {
                //3.2 没有绑定，http状态码响应  401。
                if (logger.isInfoEnabled()){
                    logger.info("【登陆接口（login）】登陆失败，用户(openid):[{}]未绑定",opneId);
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

        }catch (Exception e){

            logger.error("【登陆接口（login）】登陆出错,服务器内部错误，用户code:[{}]",code,e);
        }

        //  出现未知内部错误，http状态码响应 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }


    /**
     *添加绑定，调用此方法。
     *
     * 接收code（微信临时身份验证码）
     *    yktId（学校统一认证码）
     *    password（统一认证身份密码）
     *
     * @param bindInfo1 {code,userid,password}
     * @return 返回请求结果提示
     */
    @RequestMapping(method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public ResponseEntity<String> bind(@RequestBody Map<String,String> bindInfo1){
        String code= null;
        String yktId= null;
        String password= null;
        try {
            // 1. 获取请求参数
             code= bindInfo1.get("code");
             yktId= bindInfo1.get("yktId");
             password= bindInfo1.get("password");

            if (logger.isDebugEnabled()){
                logger.debug("【绑定接口（bind）】 接收参数code[{}],yktId[{}],password[{}]",code,yktId,password);
            }

            // 2. 通过code获取openid，并校验该openid是否存在绑定。
            String openid = getInfoFromWx.getOpenid(code).replace("\"","");
            if ("nullOpenid"==openid){
                if (logger.isDebugEnabled()){
                    logger.debug("【添加新用户（AddUser）】获取openid失败，code：[{}]可能是无效的",code);
                }
                //获取openid失败，响应415
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("绑定失败，可能是无效code");
            }
            if (operationBind.isBind(openid)){
                if (logger.isDebugEnabled()){
                    logger.debug("【添加新用户（AddUser）】openid：[{}]已经存在绑定",openid);
                }
                //该微信用户已经存在绑定 响应 409
                return ResponseEntity.status(HttpStatus.CONFLICT).body("该微信用户已经存在绑定");
            }

            // 3. 验证身份（一卡通账号密码） 账号密码是否正确
            boolean isTrue=authentication.ldapCheck(yktId,password);

            // 4.添加新用户
            if (isTrue){
                // 4.1身份验证成功 添加绑定
                boolean isAdd=addUser.addUInfoAndUBind(openid,yktId,password);
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
            }


        }catch (Exception e){
            logger.error("【绑定接口（bind）】绑定操作错误！统一认证码[{}]",yktId,e);
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

        if (logger.isDebugEnabled()){
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
            String openid=getInfoFromWx.getOpenid(code).replace("\"","");
            // 2.1判断oepnid是否正确
            if ("nullOpenid"==openid){
                if (logger.isInfoEnabled()){
                    logger.info("【删除用户接口（deldete）】删除绑定用户失败！ code:[{}]换取openid失败",code);
                }
                // 2.1 openid换取失败，可能原因是code不正确，http响应状态码 415
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("删除失败，可能因为code不正确或该code使用过，code："+code);

            }


        // 3.根据有openid删除相关用户数据（用户信息，用户绑定）
            boolean b = deleteUser.deleteUserinfoAndBind(openid);
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
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("删除绑定用户成功");
        }catch (Exception e){
            logger.error("【删除用户接口（deldete）】删除用户绑定操作出错！",e);
        }

        // 4.服务器内部错误 响应 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除失败，服务器端内部未知错误");

    }
}
