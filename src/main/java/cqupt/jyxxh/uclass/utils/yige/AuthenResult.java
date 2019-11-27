package cqupt.jyxxh.uclass.utils.yige;

import javax.naming.directory.Attributes;

/**
 * 认证结果的封装
 * 
 * @author tingting
 * 
 */
public class AuthenResult {
	/**
	 * 认证成功
	 */
	public static final int AUTHEN_RESULT_OK = 1;
	/**
	 * 认证过程中服务器出现异常
	 */
	public static final int AUTHEN_RESULT_SERVER_ERR = -1;
	/**
	 * 用户名或者密码错误
	 */
	public static final int AUTHEN_RESULT_USERNAME_PSW_ERR = 0;
	/**
	 * 认证返回的用户信息封装
	 */
	private Attributes attrs;
	/**
	 * 认证结果代码
	 */
	public int result;
	public Attributes getAttrs() {
		return attrs;
	}
	public void setAttrs(Attributes attrs) {
		this.attrs = attrs;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}

}