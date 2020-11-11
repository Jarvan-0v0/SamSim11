package blkjweb.controller;

import java.util.*;
import javax.annotation.Resource;
import org.blkj.utils.ConvertTool;
import org.blkj.utils.PageTool;
import org.blkj.utils.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import blkjweb.service.*;

@Controller
public class BaseUnitCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService;

	private final String tableName = "danwei";
	
	//初始化系统日志 jqgrid
	@RequestMapping(value="/base_unit_init", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_unit_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();//获取HttpServletRequest

		//获取prmNames传递的参数
		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   
		String sort = pd.getString("sidx");//排序列
		String order = pd.getString("sord");//排序方式
		int index = (page - 1) * pageSize; // 开始记录数   

		long totalRecord = systemService.queryCount(tableName); //数据库中满足条件的总记录数
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		//只从库中检索所需要的记录：LIMIT 5,10 检索记录行 6-15 第一个参数指定第一个返回记录行的偏移量，第二个参数指定返回记录行的最大数目
		//lists= systemService.query("user",sort, order, "",new Object[] {index, pageSize});
		//一次性全部读取，然后返回子集.index记录起始位置,注意表中记录是从1开始;越界则返回0条记录
		lists = systemService.query(tableName, sort, order, "", index+1, pageSize);
		//List<Object> query(String table,String sort, String order, String where, int position, int length)

		//数据转换
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);
		//支持服务器端翻页的Json格式
		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		//一次性全部读取，在客户端进行翻页     String json = " \"rows\":" + rowList +"}";

		return json; //将JSON数据返回页面
	}
	
	//添加
	@RequestMapping(value="/base_unit_add")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_unit_add() 
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
		
	//方法获取要编辑的记录
	@RequestMapping(value="/base_unit_edit_init")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_unit_edit_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String KeyValue = pd.getString("KeyValue");

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = systemService.queryOne(tableName,"id",KeyValue); 

		if(mapRecord != null && mapRecord.size() > 0)
			return mapRecord ;
		else
			return "";
	}

	//保存编辑后的信息
	@RequestMapping(value="/base_unit_editsave")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_unit_edit() 
	{
		String code = "2";
		String message = "成功更新!";
				
		PageTool pd = new PageTool();
		pd = this.getPageData();
	
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = pd.getMap();
		
		String oldId = (String)mapRecord.get("oldId");
		String newId = (String)mapRecord.get("id");
		boolean isUpdateKey = false; 
		String where = "id='"+ oldId +"'";
		if(! StringTool.isNullEmpty(newId) && //新编号不空 且 新旧编号不等 
		   ! newId.equals(oldId)){
			isUpdateKey = true; //更新主键
			mapRecord.put("id",newId);
		}
		//系统会自动过滤到无用字段
		int result = systemService.update(tableName, mapRecord, isUpdateKey, where);

		if (result < 0){//不成功
			code = "-1";
			message = "更新失败!";
		}
		return message(code,message);
	}

	//删除选定的记录
	@RequestMapping(value="/base_unit_del")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_unit_del() 
	{
		String code = "2";
		String message = "成功删除!";
				
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String id = pd.getString("KeyValue");

		String where = "id='" + id +"'";
		int result = systemService.delete(tableName, where);

		
		if (result <= 0){//不成功
			code = "-1";
			message = "删除失败!";
		}
		return message(code,message);
	}
	
	
	//删除满足条件所有记录
	@RequestMapping(value="/base_unit_broom")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_unit_broom() 
	{
		String code = "2";
		String message = "成功删除!";
		
		int result = systemService.broom(tableName);
		
		if (result <= 0){//不成功
			code = "-1";
			message = "删除失败!";
		}
		return message(code,message);
	}
	
	@RequestMapping(value="/base_unit_search", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_unit_search() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String KeyValue = pd.getString("KeyValue");
		String KeyWord = pd.getString("KeyWord");
		
		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   
		long totalRecord = 0; //数据库中满足条件的总记录数

		String where = "";
		if (KeyWord != null && KeyWord.equals("id")){//精确查询
			where = KeyWord + " = '" + KeyValue + "'";//= 与 like 的区别？
		}
		else//模糊查询
			where = KeyWord + " LIKE '%" + KeyValue + "%'";
			
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(tableName,where);
		
		if (lists != null)
			totalRecord = lists.size();

		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);//数据转换
		
		//支持服务器端翻页的Json格式
		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json; //将JSON数据返回页面
	}

}

