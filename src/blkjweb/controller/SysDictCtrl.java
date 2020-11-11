package blkjweb.controller;

import java.util.*;
import javax.annotation.Resource;
import org.blkj.utils.PageTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import blkjweb.service.*;

@Controller
public class SysDictCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService;


	@RequestMapping(value="/sys_dict_init", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object sys_dict_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableName = pd.getString("tableName");
		//获取界面返回的数据
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		String where = "";
		lists = systemService.query(tableName, where);
		//String rowList = ConvertTool.list2json((ArrayList<?>)lists);//数据转换
		return lists;
		//return rowList;
	}	

	//添加信息
	@RequestMapping(value="/sys_dict_add")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object sys_dict_add() 
	{
		String code = "2";
		String message = "成功保存记录!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableName = pd.getString("DictCode");
		String id = pd.getString("id");
		String name = pd.getString("name");
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord.put("id", id);
		mapRecord.put("name", name);
		int result = systemService.insert(tableName, mapRecord);
		if (result <= 0){//不成功
			code = "-1";
			message = "保存记录失败(库表不存在或其他原因)!";
		}

		return message(code,message);
	}

	//保存编辑后的数据
	@RequestMapping(value="/sys_dict_edit")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object sys_dict_edit() 
	{
		String code = "2";
		String message = "成功更新!";

		PageTool pd = new PageTool();
		pd = this.getPageData();

		String tableName = pd.getString("DictCode");
		String id = pd.getString("id");
		String name = pd.getString("name");
		String oldId = pd.getString("oldId");

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord.put("id", id);
		mapRecord.put("name", name);

		String where = "id='"+ oldId +"'";
		int result = systemService.update(tableName, mapRecord,true, where);//系统会自动过滤到无用字段

		if (result < 0){//不成功
			code = "-1";
			message = "更新失败!";
		}

		return message(code,message);
	}

	//删除选定的记录
	@RequestMapping(value="/sys_dict_del")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object sys_dict_del() 
	{
		String code = "2";
		String message = "成功删除!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String id = pd.getString("KeyValue");
		String tableName = pd.getString("tableName");

		String where = "id='" + id +"'";
		int result = systemService.delete(tableName, where);
		if (result <= 0){//不成功
			code = "-1";
			message = "删除失败!";
		}

		return message(code,message);
	}

	//删除满足条件所有记录
	@RequestMapping(value="/sys_dict_broom")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object sys_dict_broom() 
	{
		String code = "2";
		String message = "成功删除!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();//获取HttpServletRequest	
		String tableName = pd.getString("tableName");		
		int result = systemService.broom(tableName);
		if (result <= 0){//不成功
			code = "-1";
			message = "删除失败!";
		}

		return message(code,message);
	}

}

