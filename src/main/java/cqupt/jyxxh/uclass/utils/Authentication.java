package cqupt.jyxxh.uclass.utils;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

/**
 * 统一身份验证，访问学校的统一身份认证系统，验证用户账号密码，获取用户一些基本信息
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 15:09 2019/11/3
 */
@Component
public class Authentication {

    private final Logger logger= LoggerFactory.getLogger(Authentication.class);   //日志

    @Value("${URL}")
    private String URL;       //LDAP连接地址  "ldap://211.83.210.1:389/"

    @Value("${BASEDN}")
    private  String BASEDN;   //LDAP的根DN  "dc=cqupt,dc=edu,dc=cn"

    @Value("${PRINCIPAL}")
    private String PRINCIPAL; //LDAP的连接账号

    @Value("${PASSWORD}")
    private String PASSWORD;   //LDAP连接账号的密码  wxgzpt_ldapuser


    /**
     * 验证统一身份账号密码是否正确
     *
     * @param ykth  一卡通号，统一认证码
     * @param password  统一认证密码
     * @return boolean
     */
    public boolean ldapCheck(String ykth, String password){

        /**
         * 用于开发过程教师校验,后期删除！！！
         */

        if (ykth.equals("0101303")&&password.equals("123456")){
            return true;
        }
        if (ykth.equals("0102550")){
            return true;
        }


        Control[] controls=null;

        // 1.设置账号密码检查标记
        boolean flage;

        // 2.判断密码是否为空或为空串 如果是直接返回false
        if (password==null|| password.equals("")){
            //日志
            if (logger.isDebugEnabled()){
                logger.debug("【身份验证（LDAP）】 验证失败！ 密码为空串或为空");
            }
            return false;
        }
        // 3.获取LDAP连接,如果获取连接失败，直接返回false
        LdapContext ldapContext=getLdapContext();
        if (null==ldapContext){
            return false;
        }

        // 4.获得userDN
        String userDN=getUserDN(ykth);
        if ("".equals(userDN)){
            return false;
        }

        // 5.验证账号密码
        try {
            ldapContext.addToEnvironment(Context.SECURITY_PRINCIPAL,userDN);
            ldapContext.addToEnvironment(Context.SECURITY_CREDENTIALS,password);
            ldapContext.reconnect(controls);
            flage=true;
        } catch (NamingException e) {
            //账号密码错误，[LDAP: error code 49 - Invalid Credentials]
            logger.error("【身份验证（Authentication.ldapCheck）】身份验证错误！账号或密码不正确，统一认证码:[{}]", ykth);
            flage=false;

        }
        return flage;
    }


    /**
     * 获得UserDN
     *
     * @param ykth 一卡通号，统一认证码
     * @return userDN
     */
    private String getUserDN(String ykth) {
        StringBuilder userDN= new StringBuilder();
        try {
            SearchControls searchControls=new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration enumeration=getLdapContext().search("","uid="+ykth,searchControls);
            while (enumeration!=null&&enumeration.hasMoreElements()){
                Object obj=enumeration.nextElement();
                if (obj instanceof SearchResult){
                    SearchResult si= (SearchResult) obj;
                    userDN.append(si.getName());
                    userDN.append(",").append(BASEDN);
                }else {
                    System.out.println(obj);
                }
            }
        } catch (NamingException e) {
            //日志
            logger.info("【身份验证（LDAP）】身份验证失败,获取UserDN失败,可能原因为ykth：[{}]出错",ykth);
        }
        return userDN.toString();
    }


    /**
     * 建立LDAP连接  LdapContext
     */
    private LdapContext getLdapContext(){

        Control [] controls=null;

        LdapContext ldapContext=null;

        //创建hashtable集合 放连LDAP连接参数
        Hashtable<String,String> env=new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL,URL+BASEDN);
        env.put(Context.SECURITY_AUTHENTICATION,"simple");
        env.put(Context.SECURITY_PRINCIPAL,PRINCIPAL);
        env.put(Context.SECURITY_CREDENTIALS,PASSWORD);
        //创建连接
        try{
            ldapContext=new InitialLdapContext(env,controls);
        } catch (NamingException e) {
            //日志
            logger.error("【身份验证（LDAP）】获取LDAP连接失败！");
        }
        return ldapContext;
    }


    /**
     * 获取统一认证通过后 获取姓名 学院等信息
     *
     * @return Attributes
     */
    public Attributes getAttributes(String ykth, String password){
        Attributes attributes=null;
        //如果身份验证成功，可以获取信息
        if (ldapCheck(ykth,password)){
            try {
                SearchControls searchControls=new SearchControls();
                searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                NamingEnumeration enumeration=getLdapContext().search("","uid="+ ykth,searchControls);
                while (enumeration!=null&&enumeration.hasMoreElements()){
                    Object obj=enumeration.nextElement();
                    if (obj instanceof SearchResult){
                        SearchResult si= (SearchResult) obj;
                        attributes =si.getAttributes();
                    }else {
                        System.out.println(obj);
                    }
                }
            } catch (NamingException e) {
                logger.error("【LDAP信息获取（Authentication.getAttributes）】获取信息失败！出现未知错误",e);
            }
        }else {
            if (logger.isInfoEnabled()){
                logger.info("【LDAP信息获取（Authentication.getAttributes）】获信息失败（LDAP）！原因统一身份验证失败");
            }

        }
        return attributes;
    }
}
