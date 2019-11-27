package cqupt.jyxxh.uclass.utils.yige;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

/**
 * 
 * @author tingting
 * 
 */

public class UserAuth {

	/**
	 * 
	 * @param userNum
	 *            统一认证码
	 * @param psw
	 *            密码
	 * @return 认证结果 请参考<b>AuthenResult</b>类
	 */
	private static Logger logger = LoggerFactory.getLogger(AuthenResult.class);
	public static AuthenResult authen(String userNum, String psw) {

		logger.error("this is an 测试数据  这里是/getAuthStudent/sendStudent方法。测试点2.2.1 ");
		logger.error("this is an 测试数据 ========================== 学生帐号： "+userNum+"学生密码："+psw);
		AuthenResult authenResult = new AuthenResult();
		authenResult.result = AuthenResult.AUTHEN_RESULT_OK;

		Control[] connCtls = null;
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, Constants.URL + Constants.BASEDN);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, Constants.PRINCIPAL);
		env.put(Context.SECURITY_CREDENTIALS, Constants.PASSWORD);
		LdapContext ctx = null;
		String userDN = "";
		try {
			logger.error("this is an 测试数据  这里是/getAuthStudent/sendStudent方法。测试点2.2.2 ");
			// 链接ldap
			ctx = new InitialLdapContext(env, connCtls);
			// 获取用户DN
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration en = ctx
					.search("", "uid=" + userNum, constraints);
			while (en != null && en.hasMoreElements()) {
				logger.error("this is an 测试数据  这里是/getAuthStudent/sendStudent方法。测试点2.2.3 ");
				Object obj = en.nextElement();
				if (obj instanceof SearchResult) {
					SearchResult si = (SearchResult) obj;
					userDN += si.getName();
					userDN += ",";
					userDN += Constants.BASEDN;
					authenResult.setAttrs(si.getAttributes());
				}
			}
			// 用户是否存在
			if (!"".equals(userDN)) {
				logger.error("this is an 测试数据  这里是/getAuthStudent/sendStudent方法。测试点2.2.5 ");
				// 验证用户密码是否正确
				ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
				ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, psw);
				try {
					ctx.reconnect(connCtls);
				}catch (Exception e){
					System.out.println("密码错误");
				}

			} else {
				authenResult.result = AuthenResult.AUTHEN_RESULT_USERNAME_PSW_ERR;
			}
		} catch (AuthenticationException e) {
			authenResult.result = AuthenResult.AUTHEN_RESULT_USERNAME_PSW_ERR;
			e.printStackTrace();
		} catch (NamingException e) {
			authenResult.result = AuthenResult.AUTHEN_RESULT_SERVER_ERR;
			e.printStackTrace();
		} catch (Exception e) {
			authenResult.result = AuthenResult.AUTHEN_RESULT_SERVER_ERR;
			e.printStackTrace();
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				ctx = null;
			}
		}
		logger.error("this is an 测试数据  这里是/getAuthStudent/sendStudent方法。测试点2.2.6 ");
		return authenResult;
	}
}
