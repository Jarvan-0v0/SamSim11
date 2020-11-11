package blkjweb.service.base;

import java.util.Map;
import java.util.Set;

public interface UserService {
	
	Map<String, Object>/*user*/ findUserById(String tableName, String userID);//依据用户ID查找用户信息
	Set<String> findRoles(String userID);//根据用户ID查找其角色  
	Set<String> findRoleName(Set<String> roleID);//根据角色ID查找角色名字
	Set<String> findPermissions(Set<String> roleIDs); //根据角色ID查找其权限ID  
	Set<String> findPersName(Set<String> persID); //根据权限ID查找其权限名字
	
//@Override
	/*public User createUser(User user); //创建账户  
	public void changePassword(Long userId, String newPassword);//修改密码  
	public void correlationRoles(Long userId, Long... roleIds); //添加用户-角色关系  
	public void uncorrelationRoles(Long userId, Long... roleIds);// 移除用户-角色关系  
	  */
	
}