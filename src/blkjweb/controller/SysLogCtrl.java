package blkjweb.controller;

import java.util.*;
import javax.annotation.Resource;
import org.blkj.utils.ConvertTool;
import org.blkj.utils.DateTool;
import org.blkj.utils.PageTool;
import org.blkj.utils.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import blkjweb.service.*;

@Controller
public class SysLogCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService;

	private final String tableName = "log4j";

	/**
	 * produces = "text/html;charset=UTF-8"
	 * 前台是HTML5,不作如下处理:produces="application/json; charset=utf-8"，页面上的中文乱码。
	 */

	@RequestMapping(value="/sys_log_initPage", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object sys_log_initPage() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   
		String sort = pd.getString("sidx");//排序列
		String order = pd.getString("sord");//排序方式
		int index = (page - 1) * pageSize; // 开始记录数   

		long totalRecord = systemService.queryCount(tableName); //数据库中满足条件的总记录数
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数


		//只从库中检索所需要的记录：LIMIT 5,10 检索记录行 6-15 第一个参数指定第一个返回记录行的偏移量，第二个参数指定返回记录行的最大数目
		/*List<Object> lists = new ArrayList<Object>();
		lists= systemService.query(tableName, sort, order, "", new Object[] {index, pageSize});
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);
		 */
		List<Map<String, Object>> rowList = new ArrayList<Map<String, Object>>();
		rowList = systemService.query(tableName, sort, order, "", new Object[] {index, pageSize});

		//支持服务器端翻页的Json格式
		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";//
		//一次性全部读取，在客户端进行翻页     String json = " \"rows\":" + rowList +"}";
		//Console.showMessage("json1:"+ json);
		return json; //将JSON数据返回页面
	}

	@RequestMapping(value="/sys_log_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object sys_log_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String rootId = pd.getString("RootId");
		String subMenuId = pd.getString("SubMenuId");

		//String tableName = "log4j";
		int type = 0;//日志类别：0:访问日志;1:系统运行时日志
		String name = ""; //访问日志: 操作用户 "管理员"、"领导"、"非法用户(用户角色不详)"; 
		String status = "";//运行时日志: 异常、调试 
		String where = "";
		if ( !StringTool.isNullEmpty(rootId))
		{   //日志类别type：0:访问日志;1:运行时日志
			if (rootId.equalsIgnoreCase("pNode01")){ //运行时日志
				type = 1;
				status = subMenuId;
				where = "type='"+ type +"'";
				if (status.length() != 0)
					where = where + " AND status "+ status;	
			}
			else if (rootId.equalsIgnoreCase("pNode02")){ //访问日志
				type = 0;
				//name LIKE '%管理员%'
				name = subMenuId;
				where = "type='"+ type +"'";
				if (name.length() != 0)
					where = where + " AND name "+ name;
			}
		}

		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   
		String sort = pd.getString("sidx");//排序列
		String order = pd.getString("sord");//排序方式
		int index = (page - 1) * pageSize; // 开始记录数   
		long totalRecord = 0; //数据库中满足条件的总记录数
		if (!StringTool.isNullEmpty(where))//含有查询条件
			totalRecord = systemService.queryCount(tableName,where);
		else
			totalRecord = systemService.queryCount(tableName);
		long totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数

		//只从库中检索所需要的记录：LIMIT 5,10 检索记录行 6-15 第一个参数指定第一个返回记录行的偏移量，第二个参数指定返回记录行的最大数目
		/*List<Object> lists = new ArrayList<Object>();
		lists= systemService.query(tableName,sort, order, where, new Object[] {index, pageSize});
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);*/

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query(tableName,sort, order, where, new Object[] {index, pageSize});

		//五如下语句，前端页面不显示内容，上面的方法就可以。 原因不明
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";

		return json; 
	}	

	//删除库中指定记录
	@RequestMapping(value="/sys_log_broom")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object sys_log_broom() 
	{
		String code = "2";
		String message = "成功删除!";
		

		PageTool pd = new PageTool();
		pd = this.getPageData();//获取HttpServletRequest	
		String type = pd.getString("KeepTime");

		String where  = "";
		int result = 0;
		if ( type.equals("1") || type.equals("0") ){//0:访问日志;1:运行时日志
			where = "type='" + type +"'";
			result = systemService.delete(tableName, where);
		} else if ( type.equals("2")) //不保留，全部删除
			result = systemService.broom(tableName);
		else if ( type.equals("3")) {//保留近一个月日志
			String time  = DateTool.getBeforeMonthsDate(1);
			where = "time <='" + time +"'";
			result = systemService.delete(tableName, where);
		}
		if (result <= 0){//不成功
			code = "-1";
			message = "删除失败!";
		}

		return message(code,message);
	}

	@RequestMapping(value="/sys_log_search" , produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object sys_log_search() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String KeyValue = pd.getString("KeyValue");
		String where  = "";

		if ( KeyValue.equals("1") || KeyValue.equals("0") )
			where = "type='" + KeyValue +"'";
		else if ( KeyValue.equals("2"))
			where = "name LIKE '%角色不详%' ";

		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   
		long totalRecord = 0; //数据库中满足条件的总记录数

		if (!StringTool.isNullEmpty(where))//含有查询条件
			totalRecord = systemService.queryCount(tableName,where);
		else
			totalRecord = systemService.queryCount(tableName);

		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数

		//List<Object> lists = new ArrayList<Object>();
		//lists= systemService.query(tableName,where);
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query(tableName,where);

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);//数据转换

		//支持服务器端翻页的Json格式
		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		//Console.showMessage("json2:"+ json);
		return json; //将JSON数据返回页面
	}	
}

