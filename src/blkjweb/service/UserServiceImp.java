package blkjweb.service;

import java.util.*;
import org.blkj.sql.DMLProcessor;
import org.blkj.utils.StringTool;
import org.springframework.stereotype.Service;
import blkjweb.service.base.UserService;

/**当需要定义某个类为一个bean，则在这个类的类名前一行使用@Service("XXX"),就相当于讲这个类定义为一个bean，bean名称为XXX;*/
//set中不能插入重复元素
//@Service
@Service("UserServiceImp")
public class UserServiceImp implements UserService
{	
	//从保存用户账户的表中，依据用户ID查找用户信息
	@Override
	public Map<String, Object> /*user*/ findUserById(String tableName, String userID){
		//user userObj = new user();
		DMLProcessor dbUtil = new DMLProcessor();
		String where = "id='" + userID + "'";
		Map<String, Object> result = dbUtil.queryOne(tableName,where);
		dbUtil.commit();
        return result;
		//ConvertTool.map2Bean_beanutils(result, userObj);//Map转换为javaBean
		//return userObj;
	}
	
	/**一个用户应该具有多个角色. 目前的系统约定：一个用只有一个角色 */
	@Override
	public Set<String> findRoles(String userID)	//依据用户ID查找用户角色 
	{	
		if (StringTool.isNull(userID)){//目前，权限管理仅存储管理员的信息
			return null;
		}
		String sql="SELECT roleid FROM userrole WHERE userid=?";
		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = dbUtil.query(sql,new String[]{userID});
		dbUtil.commit();
		if (StringTool.isNull(lists)){
			return null;
		}

		Set<String> set = new HashSet<String>();
		Map<String, Object> map = new HashMap<String, Object>(); //LinkedHashMap
		for(int i= 0; i<lists.size(); i++) {
			map = lists.get(i);
			Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
			while( entries.hasNext() ){  
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>)entries.next(); 
				Object result = entry.getValue();//value值，不是key值
				if (result == null)
					continue;
				set.add(result.toString()); //roleid 
			}  
		}
		return set;
	}
	
	/**一个角色对应多个权限。目前的系统约定：一个角色有一种权限*/
	@Override
	public Set<String> findPermissions(Set<String> roleIDs)//依据角色ID查找权限ID
	{
		if (StringTool.isNull(roleIDs))
			return null;
		
		Set<String> set = new HashSet<String>();
		String sql="SELECT permissionid FROM rolepermission WHERE";
		Iterator<String> it = roleIDs.iterator();
		StringBuilder sb = new StringBuilder();
		while (it.hasNext()) {  
			String str = it.next();  
			if (sb.length() == 0)
				sb.append(" roleid='" + str + "'");
			else
				sb.append(" OR roleid='" + str + "'");
		}  
		sql = sql + sb.toString();

		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = dbUtil.query(sql);
		dbUtil.commit();

		if (StringTool.isNull(lists)){
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>(); //LinkedHashMap
		for(int i= 0; i<lists.size(); i++) {
			map = lists.get(i);
			Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
			while(entries.hasNext() ){  
				Map.Entry<String, Object> entry = (Map.Entry<String, Object> )entries.next(); 
				Object result = entry.getValue();
				if (result == null)
					continue;
				set.add(result.toString());//roleid 
			}  
		}
		return set;
	}
	
	@Override
	public Set<String> findRoleName(Set<String> roleID)	//依据角色ID查找角色名字 
	{	
		if (StringTool.isNull(roleID)){
			return null;
		}
		
		Set<String> set = new HashSet<String>();
		Iterator<String> it = roleID.iterator();
		while (it.hasNext()) {
			String sql = "SELECT name FROM role WHERE id='" + it.next() +"'";
			DMLProcessor dbUtil = new DMLProcessor();
			Map<String, Object> map = new HashMap<String, Object>();
			map = dbUtil.queryOne(sql);
			dbUtil.commit();

			if (! StringTool.isNull(map)){
				Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
				while( entries.hasNext() ){  
					Map.Entry<String, Object> entry = (Map.Entry<String, Object>)entries.next(); 
					Object result = entry.getValue();
					if (result == null)
						continue;
					set.add(result.toString());
				}  
			}
		}
		return set;
	}

	
	
	
	
	
	
	
	
	
	
	

	@Override
	public Set<String> findPersName(Set<String> persID)	//依据权限ID查找权限名字 
	{	
		if (StringTool.isNull(persID)){
			return null;
		}
		Set<String> set = new HashSet<String>();
		Iterator<String> it = persID.iterator();
		while (it.hasNext()) {
			String sql = "SELECT name FROM permission WHERE id='" + it.next() +"'";
			DMLProcessor dbUtil = new DMLProcessor();
			Map<String, Object> map = new HashMap<String, Object>();
			map = dbUtil.queryOne(sql);
			dbUtil.commit();

			if (! StringTool.isNull(map)){
				Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
				while( entries.hasNext() ){  
					Map.Entry<String, Object> entry = (Map.Entry<String, Object>)entries.next(); 
					Object result = entry.getValue();
					if (result == null)
						continue;
					set.add(result.toString());
				}  
			}
		}
		return set;
	}


	
	

	

}


