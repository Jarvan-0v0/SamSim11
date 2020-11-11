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
public class BasePermissionCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService; 

	private final String tableName = "rolepermission";
	private final String permissionTable = "permission";
	private final String roleTable = "role";

	//初始化
	@RequestMapping(value="/base_permission_init", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_permission_init() 
	{
		//(roleid,permissionid) 
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query(tableName,"");

		if(lists != null){
			/** 利用缓存技术读取角色表 (id name)*/	
			List<Map<String, Object>> roleLists = new ArrayList<Map<String, Object>>();
			roleLists = (List<Map<String, Object>>) EhcacheUtils.get("ROLE");
			if (roleLists == null){//获取name值
				roleLists = systemService.query(roleTable,"");
				EhcacheUtils.put("ROLE",roleLists);
			}
			/** 利用缓存技术读取权限表 (id name)*/	
			List<Map<String, Object>> permissionLists = new ArrayList<Map<String, Object>>();
			permissionLists = (List<Map<String, Object>>) EhcacheUtils.get("PERMISSION");
			if (permissionLists == null){//获取name值
				permissionLists = systemService.query(permissionTable,"");
				EhcacheUtils.put("PERMISSION",permissionLists);
			}
			//更新有关数据域
			for(int i=0; i<lists.size(); i++){//修改单位名字
				Map<String, Object> map = new HashMap<String, Object>();
				map = lists.get(i);
				map.put("rolename", getNameByID4List(roleLists,"id",(String)map.get("roleid"),"name"));
				map.put("permissionname", getNameByID4List(permissionLists,"id",(String)map.get("permissionid"),"name"));
			}
		}
		
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);//数据转换
		return rowList;
	}


	//添加
	@RequestMapping(value="/base_permission_add")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_permission_add() 
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
	@RequestMapping(value="/base_permission_del")  
	@ResponseBody
	public Object base_permission_del() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String roleid = pd.getString("RoleID");
		String permissionid = pd.getString("PermissionID");

		String where = "roleid='" + roleid + "' AND permissionid='" + permissionid + "'";
		int result = systemService.delete(tableName, where);

		String code = "2";
		String message = "成功删除!";
		if (result <= 0){//不成功
			code = "-1";
			message = "删除失败!";
		}
		return message(code,message);
	}


	//保存编辑后的信息
	@RequestMapping(value="/base_permission_editsave")  
	@ResponseBody
	public Object base_permission_edit() 
	{
		String code = "2";
		String message = "成功更新!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String roleid = pd.getString("roleid");
		String permissionid = pd.getString("permissionid");

		Map<String, Object> mapRecord = new HashMap<String, Object>();

		String where = "roleid='"+ roleid +"'";
		mapRecord.put("permissionid",permissionid);
		//当由多个字段构成主键时，含有isUpdateKey的update方法无法对构成主键的任何一个字段进行更新。
		int result = systemService.update_raw(tableName,mapRecord,where);

		if (result < 0){//不成功
			code = "-1";
			message = "更新失败!";
		}
		return message(code,message);
	}

}
