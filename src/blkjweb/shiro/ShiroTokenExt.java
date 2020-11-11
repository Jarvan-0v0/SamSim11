package blkjweb.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

//扩展传入参数
public class ShiroTokenExt extends UsernamePasswordToken {

	private static final long serialVersionUID = 1L;
	private String tableName;

	public ShiroTokenExt(String loginName, String password, String tableName) {
		super(loginName, password);
		this.tableName = tableName;
	}
	public ShiroTokenExt(String loginName, String password) {
		super(loginName, password);
	}
	
	public String getTableName() {
		return tableName;

	}
}
