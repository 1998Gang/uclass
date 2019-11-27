package cqupt.jyxxh.uclass.utils.yige;

public class Constants {
	public static final String APPID = "wx770c5bdd56cbbd99";
	public static final String APPSECRET = "aee4ff7cf2e784b92f9431def1c39934";

	// LDAP的根DN
	public static final String BASEDN = "dc=cqupt,dc=edu,dc=cn"; // baseDN
	// LDAP的连接地址（ldap://ip:port/）
	public static final String URL = "ldap://211.83.210.1:389/"; // LDAP链接地址
	// LDAP的连接账号（身份认证管理平台添加的应用账号）
	public static final String PRINCIPAL = "uid=ldapuser_wxgzpt,ou=Manager,dc=cqupt,dc=edu,dc=cn"; // "***"处需信息化办公室分配链接LDAP的帐号
	// LDAP的连接账号的密码（身份认证管理平台添加的应用账号的密码）
	public static final String PASSWORD = "wxgzpt_ldapuser"; // 需信息化办公室分配链接LDAP帐号的密码
	
	/**
	 * 绑定、解绑链接有效时间
	 */
	public static final int ACTION_EXPIRED_DURATION=300;
    
}
