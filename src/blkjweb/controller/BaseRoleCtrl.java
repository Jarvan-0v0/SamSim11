package blkjweb.controller;

import java.util.*;
import javax.annotation.Resource;
import org.blkj.enhance.EhcacheUtils;
import org.blkj.utils.ConvertTool;
import org.blkj.utils.PageTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import blkjweb.service.*;

@Controller
public class BaseRoleCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService; 

	private final String tableName = "userrole";
	private final String userinfoTable = "userinfo";
	private final String roleTable = "role";

	//初始化系统日志
	@RequestMapping(value="/base_role_init", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object base_role_init() 
	{
		//(userid,roleid) 
		String where = "userid !='admin'"; 
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query(tableName,where); //userrole表中的用户编码和角色编码

		if(lists != null){/** 利用缓存技术读取角色表 (id name)*/	
			List<Map<String, Object>> unitLists = new ArrayList<Map<String, Object>>();
			unitLists = (List<Map<String, Object>>) EhcacheUtils.get("ROLE");
			if (unitLists == null){//获取name值
				unitLists = systemService.query(roleTable,"");
				EhcacheUtils.put("ROLE",unitLists);
			}
			//获取所有用户的(id,name)
			List<Map<String, Object>> nameLists =  new ArrayList<Map<String, Object>>();
			nameLists = systemService.query(userinfoTable,"id,name", "");

			//更新有关数据域
			for(int i=0; i<lists.size(); i++){//修改单位名字
				Map<String, Object> map = new HashMap<String, Object>();
				map = lists.get(i);
				map.put("rolename", getNameByID4List(unitLists,"id",(String)map.get("roleid"),"name"));
				map.put("username", getNameByID4List(nameLists,"id",(String)map.get("userid"),"name"));
			}
		}
		
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);//数据转换
		return rowList;
	}

	//添加
	@RequestMapping(value="/base_role_add")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_role_add() 
	{
		String code = "2";
		String message = "成功保存记录!";
		
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		mapRecord = pd.getMap();
		int result = systemService.insert(tableName, mapRecord);

		if (result <= 0){//不成功
			code = "-1";
			message = "保存记录失败!";
		}
		return message(code,message);
	}

	//删除选定的记录
	@RequestMapping(value="/base_role_del")  
	@ResponseBody
	public Object base_role_del() 
	{
		String code = "2";
		String message = "成功删除!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String roleid = pd.getString("RoleID");
		String userid = pd.getString("UserID");

		String where = "roleid='" + roleid + "' AND userid='" + userid + "'";
		int result = systemService.delete(tableName, where);

		if (result <= 0){//不成功
			code = "-1";
			message = "删除失败!";
		}
		return message(code,message);
	}

	//保存编辑后的信息
	@RequestMapping(value="/base_role_editsave")  
	@ResponseBody
	public Object base_role_edit() 
	{
		String code = "2";
		String message = "成功更新!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String roleid = pd.getString("roleid");
		String userid = pd.getString("userid");

		Map<String, Object> mapRecord = new HashMap<String, Object>();

		String where = "userid='"+ userid +"'";
		mapRecord.put("roleid",roleid);
		//当由多个字段构成主键时，含有isUpdateKey的update方法无法对构成主键的任何一个字段进行更新。
		int result = systemService.update_raw(tableName,mapRecord,where);

		if (result < 0){//不成功
			code = "-1";
			message = "更新失败!";
		}
		return message(code,message);
	}

}
