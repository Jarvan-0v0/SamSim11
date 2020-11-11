package blkjweb.shiro;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import blkjweb.service.*;

//认证和授权的管理操作
//@Service("monitorRealm")
public class MonitorRealm extends AuthorizingRealm 
{
	/*均已验证 
	  @Autowired
	  private UserService userService;
	  @Resource(type=UserServiceImp.class)
	  private UserServiceImp userService;
	  */
	@Resource
	private UserServiceImp userService;
	
	public MonitorRealm() {
		super();
	}
	//登陆验证: 验证当前登录的Subject 认证回调函数, 登录时调用. 1、doGetAuthenticationInfo
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
	{//获取基于用户名和密码的令牌
		//token中储存着提交的登录信息,即输入的用户名和密码
		//UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		ShiroTokenExt token = (ShiroTokenExt) authcToken;
		String userID = token.getUsername();
		String tableName = token.getTableName();
		
		//检查库中是否存在此人
		Map<String, Object>/*user*/ userObj = userService.findUserById(tableName,userID);
		AuthenticationInfo authenticationInfo = null;
		if (null != userObj) { //user.getPassword().toCharArray()
			String password = String.valueOf(token.getPassword());
			if (password.equals((String)userObj.get("password")/*userObj.getPassword()*/)) {
				//若存在，将此用户存放到登录认证info中
				authenticationInfo = new SimpleAuthenticationInfo(userID,password,getName());
			}
		}
		return authenticationInfo;
	}

	//权限验证: 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.  2、doGetAuthorizationInfo
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
	{
		//获取当前登录的用户名. 获取登录时输入的用户名 等价于(String)principals.fromRealm(this.getName()).iterator().next()
		String userID = (String) super.getAvailablePrincipal(principals); 
		
	    //权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

		//用户的角色集合
		Set<String> roleIDs = new HashSet<String>();
		//获取特定人的角色编码。
		roleIDs = userService.findRoles(userID);
		if(roleIDs != null){
			info.setRoles(roleIDs);//添加用户角色
			Set<String> permissionIDs = new HashSet<String>();
			permissionIDs = userService.findPermissions(roleIDs);
			if(permissionIDs != null)
				info.addStringPermissions(permissionIDs);//添加用户权限
		}
		return info;
	}

	/** 
	 * 更新用户授权信息缓存. 
	 */  
	public void clearCachedAuthorizationInfo(String principal) 
	{
		SimplePrincipalCollection principals =
				new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	/** 
	 * 清除所有用户授权信息缓存. 
	 */  
	public void clearAllCachedAuthorizationInfo() 
	{  
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();  
		if (cache != null) {  
			for (Object key : cache.keys()) {  
				cache.remove(key);  
			}  
		}  
	}  
}

/** 基于接口实现：
 * @Resource默认是按照名称来装配注入的，只有当找不到与名称匹配的bean才会按照类型来装配注入；
 * @Autowired默认是按照类型装配注入的，如果想按照名称来转配注入，则需要结合@Qualifier一起使用；
 * @Resource注解是又J2EE提供，而@Autowired是由Spring提供，故减少系统对spring的依赖建议使用@Resource的方式；
 * @Resource和@Autowired都可以书写标注在字段或者该字段的setter方法之上
 * @Autowired 
 * private UserDao userDao;//用于字段上
 * @Autowired
 * public void setUserDao(UserDao userDao) {//用于属性的setter方法上
 *  this.userDao= userDao;
 *  }
 * @Autowired默认情况下它要求依赖对象必须存在，如果允许null值，可以设置它required属性为false。
 * 如果我们想使用按名称装配，可以结合@Qualifier注解一起使用。如下：
 * @Autowired  @Qualifier("userDao")
 * private PersonDao  personDao;
 * 
 * @Resource(name=“userDao”)
 * private UserDao userDao;//用于字段上
 * @Resource(name=“userDao”)
 * public void setUserDao(UserDao userDao) {//用于属性的setter方法上
 *     this.userDao= userDao;
 * }
 * 注：最好是将@Resource放在setter方法上
 * 
 *  
 *  1.如果某个接口只有一个实现类的时候
 *  autowired和resource可以替换使用
 *  2.如果某个接口有多个实现类的时候
 *  autowired会报错
 *  resource(name="")或者resource(type=)或者resource(name=""，type=)可以指定具体的实现类。
 *  而且resource是java内置的，它可以脱离第三方框架的束缚
 * */

/*
hasRole(String roleName) 当用户拥有指定角色时，返回true
hasRoles(List<String> roleNames) 按照列表顺序返回相应的一个boolean值数组
hasAllRoles(Collection<String> roleNames) 如果用户拥有所有指定角色时，返回true

isPermitted(Permission p) Subject拥有制定权限时，返回true
isPermitted(List<Permission> perms) 返回对应权限的boolean数组
isPermittedAll(Collection<Permission> perms) Subject拥有所有制定权限时，返回true
*/

//http://jinnianshilongnian.iteye.com/blog/2018398