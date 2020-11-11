package blkjweb.controller;

import java.util.*;

import javax.annotation.Resource;

import org.blkj.encryption.PasswordHash;
import org.blkj.enhance.EhcacheUtils;
import org.blkj.utils.ConvertTool;
import org.blkj.utils.PageTool;
import org.blkj.utils.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import blkjweb.model.*;
import blkjweb.service.*;

//主要要考虑用户的密码。 这里的所有方法都不涉及管理员，即用户名为gly的用户
@Controller
public class BaseUserCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService; 

	private final String tableName = "userinfo";
	private final String loginTable = "user";
	private final String roleTable = "userrole";

	//初始化系统日志
	@RequestMapping(value="/base_user_init", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_user_init() 
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
		long totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		//List<Object> lists = new ArrayList<Object>();
		//只从库中检索所需要的记录：LIMIT 5,10 检索记录行 6-15 第一个参数指定第一个返回记录行的偏移量，第二个参数指定返回记录行的最大数目
		//lists= systemService.query("user",sort, order, "",new Object[] {index, pageSize});
		//一次性全部读取，然后返回子集.index记录起始位置,注意表中记录是从1开始;越界则返回0条记录
		lists = systemService.query(tableName, sort, order, "", index+1, pageSize);
		//List<Object> query(String table,String sort, String order, String where, int position, int length)

		/** 利用缓存技术将单位代码变更为单位名字 */	
		if(! StringTool.isNull(lists)){
			List<Map<String, Object>> unitLists = new ArrayList<Map<String, Object>>();
			unitLists = (List<Map<String, Object>>) EhcacheUtils.get("DANWEI");
			if (unitLists == null){//获取name值
				//原型：List<Map<String, Object>> query(String table, String item, String where)
				unitLists = systemService.query("danwei","id,name", "");
				EhcacheUtils.put("DANWEI",unitLists);
			}

			for(int i=0; i<lists.size(); i++){//修改单位名字
				//((Map)lists.get(i)).put("unitid", getNameByID4List(unitLists,"id",(String)lists.get(i).get("unitid"),"name"));//修改值
				Map<String, Object> map = new HashMap<String, Object>();
				map = lists.get(i);
				map.put("unitid", getNameByID4List(unitLists,"id",(String)map.get("unitid"),"name"));
			}
		}
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);
		//支持服务器端翻页的Json格式
		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		//一次性全部读取，在客户端进行翻页     String json = " \"rows\":" + rowList +"}";
		return json; //将JSON数据返回页面
	}

	//添加新用户。 目前没有对用户角色进行设置
	@RequestMapping(value="/base_user_add")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_user_add() 
	{
		String code = "2";
		String message = "成功保存记录!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();

		if("admin".equalsIgnoreCase(pd.getString("id"))){
			code = "-1";
			message =  "此用户已存在!";
			return message(code,message);
		}

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = pd.getMap();

		/**数据组装*/
		List<String> tableList = new ArrayList<String>();
		List<String> opList = new ArrayList<String>();
		List<List<Map<String, Object>>> recordList = new ArrayList<List<Map<String, Object>>>();
		List<String> whereList = new ArrayList<String>();
		List<Boolean> flagList = new ArrayList<Boolean>();
		List<Map<String, Object>> listRecord = new ArrayList<Map<String, Object>>();

		//组装写用户信息表的信息
		listRecord.add(mapRecord);//操作内容

		tableList.add(tableName);//要操作的表名
		opList.add("insert");//操作类别：这里是"insert"
		whereList.add(null);//where子句
		flagList.add(false);//是否更新主键标记。 true更新
		recordList.add(listRecord);

		//组装写用户账户表的信息
		String id = (String)mapRecord.get("id");
		String password = PasswordHash.encrypt("123456");//解密用decrypt
		/**写入用户账户表信息.默认密码是：123456*/	
		mapRecord = new HashMap<String, Object>();
		listRecord = new ArrayList<Map<String, Object>>();
		mapRecord.put("password", password);
		mapRecord.put("id", id);
		listRecord.add(mapRecord);

		tableList.add(loginTable);
		opList.add("insert");//"update"
		whereList.add(null);
		flagList.add(false);
		recordList.add(listRecord);

		boolean result = systemService.batchMultitable(tableList, opList,recordList,whereList,flagList);

		if (! result){//不成功 <= 0
			code = "-1";
			message = "保存记录失败!";
		}
		return message(code,message);
	}

	//方法获取要编辑的记录
	@RequestMapping(value="/base_user_edit_init")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_user_edit_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String KeyValue = pd.getString("KeyValue");

		//获取用户基本信息
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = systemService.queryOne(tableName,"id",KeyValue); 
		if(mapRecord == null || mapRecord.size() <= 0)
			return "";

		//获取用户密码信息
		Map<String, Object> pwMap = new HashMap<String, Object>();
		pwMap = systemService.queryOne(loginTable,"id",KeyValue); 

		String pw = "";
		if(pwMap != null && pwMap.size() > 0){
			pw = PasswordHash.decrypt((String)pwMap.get("password"));//解密
			mapRecord.put("password",pw);
		}

		return mapRecord;
	}


	//保存编辑后的信息
	@RequestMapping(value="/base_user_editsave")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_user_edit() 
	{
		String code = "2";
		String message = "成功更新!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();
		if("admin".equalsIgnoreCase(pd.getString("id"))){
			code = "-1";
			message =  "此用户已存在!";
			return message(code,message);
		}


		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = pd.getMap();

		String oldid = (String)mapRecord.get("oldid");
		String id = (String)mapRecord.get("id");
		String password = (String)mapRecord.get("password");//原密码
		String newPassword = (String)mapRecord.get("newPassword");//新密码

		boolean isUpdateKey = false;
		boolean pwFlag = false;
		String where = "id='"+ oldid +"'";

		if(! id.equals(oldid)){//新旧工号不同
			isUpdateKey = true; //更新主键
			mapRecord.put("id",id);
		}
		if(! StringTool.isNullEmpty(newPassword) && 
				! password.equals(newPassword)){//新旧密码不同
			pwFlag = true;
		}
		//都是普通用户
		password = PasswordHash.encrypt(newPassword);

		if(! isUpdateKey && ! pwFlag){//主键没变,密码没变。无需更新用户登录账号
			int result = systemService.update(tableName, mapRecord, isUpdateKey, where);
			if (result < 0){//不成功
				code = "-1";
				message = "更新失败!";
			}
			return message(code,message);
		}

		/**数据组装*/
		List<String> tableList = new ArrayList<String>();
		List<String> opList = new ArrayList<String>();
		List<List<Map<String, Object>>> recordList = new ArrayList<List<Map<String, Object>>>();
		List<String> whereList = new ArrayList<String>();
		List<Boolean> flagList = new ArrayList<Boolean>();
		List<Map<String, Object>> listRecord = new ArrayList<Map<String, Object>>();

		//组装写用户信息表的信息
		listRecord.add(mapRecord);//操作内容
		tableList.add(tableName);//要操作的表名
		opList.add("update");//操作类别："insert"
		whereList.add(where);//where子句 null
		flagList.add(isUpdateKey);//是否更新主键标记。 true更新
		recordList.add(listRecord);


		//组装写用户账户表的信息
		mapRecord = new HashMap<String, Object>();
		listRecord = new ArrayList<Map<String, Object>>();
		if(pwFlag )//新旧密码不同
			mapRecord.put("password", password);
		mapRecord.put("id", id);
		listRecord.add(mapRecord);

		tableList.add(loginTable);
		opList.add("update");//"update"
		whereList.add(where);
		flagList.add(isUpdateKey);
		recordList.add(listRecord);

		boolean result = systemService.batchMultitable(tableList, opList,recordList,whereList,flagList);

		if (! result){//不成功 <= 0
			code = "-1";
			message = "保存记录失败!";
		}
		return message(code,message);
	}

	//删除选定的记录 。删除用户角色、用户信息、用户账号
	@RequestMapping(value="/base_user_del")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_user_del() 
	{
		String code = "2";
		String message = "成功删除记录!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String id = pd.getString("KeyValue");
		String where = " WHERE id='" + id +"'";

		String[] batchSQLStr = new String[3];

		batchSQLStr[0] = "DELETE FROM " + tableName + where;
		batchSQLStr[1] = "DELETE FROM " + loginTable + where;
		batchSQLStr[2] = "DELETE FROM " + roleTable + " WHERE userid='" + id +"'";

		boolean result = systemService.batchMultiTable(batchSQLStr);

		if (! result){//不成功 <= 0
			code = "-1";
			message = "删除记录失败!";
		}
		return message(code,message);
	}


	//获取用户角色
	@RequestMapping(value="/base_user_role_init")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_user_role_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String KeyValue = pd.getString("KeyValue");

		String where = "";
		if (!StringTool.isNullEmpty(KeyValue))//含有查询条件
			where = " userid='" + KeyValue + "'";
		//获取用户-角色表
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = systemService.queryOne(roleTable,where); 
		if(mapRecord == null || mapRecord.size() <= 0)
			return "";

		if(mapRecord != null){
			/** 利用缓存技术读取角色表 */	
			List<Map<String, Object>> unitLists = new ArrayList<Map<String, Object>>();
			unitLists = (List<Map<String, Object>>) EhcacheUtils.get("ROLE");
			if (unitLists == null){//获取name值
				unitLists = systemService.query("role","");
				EhcacheUtils.put("ROLE",unitLists);
			}
			//从role表获取name
			mapRecord.put("rolename", getNameByID4List(unitLists,"id",(String)mapRecord.get("roleid"),"name"));
		}
		
		return mapRecord;
	}
	//保存编辑后的信息 . 应该支持存在更新，不存在插入
	@RequestMapping(value="/base_user_role_edit")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_user_role_edit() 
	{
		String code = "2";
		String message = "操作成功!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		//mapRecord = pd.getMap(); //获取不到UserId，GetWebControls可以获取到网页中roleid
		//String roleId = (String)mapRecord.get("roleid");
		String userId = pd.getString("Userid");
		String roleId = pd.getString("Roleid");
		/*String where = "userid='"+ userId +"'";
		    mapRecord.put("roleid",roleId);
		   //当由多个字段构成主键时，含有isUpdateKey的update方法无法对构成主键的任何一个字段进行更新。
			int result = systemService.update_raw(roleTable, mapRecord, where);(result < 0){//不成功
		 */
		//方法一： 存在更新，不存在插入
		mapRecord.put("roleid",roleId);
		mapRecord.put("userid",userId);
		int result = systemService.updateInsert(roleTable, mapRecord);

		/* 方法二
			 String sql = "INSERT INTO userrole(userid,roleid) values (?,?) " +
		   			      "ON DUPLICATE KEY UPDATE userid=?,roleid=?";
	        //false 不成功
			boolean result = systemService.update(sql, new Object[]{userId,roleId,userId,roleId},true,false);
		 */
		if (result < 0){//不成功 !result
			code = "-1";
			message = "操作失败!";
		}
		return message(code,message);
	}

	//获取用户密码
	@RequestMapping(value="/base_user_pw_init")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_user_pw_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String KeyValue = pd.getString("KeyValue");

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = systemService.queryOne(loginTable,"id",KeyValue); 

		if(mapRecord != null && mapRecord.size() > 0){
			String pw = PasswordHash.decrypt((String)mapRecord.get("password"));//解密
			mapRecord.put("password",pw);
			return mapRecord ;
		}
		else
			return "";
	}

	//更新密码
	@RequestMapping(value="/base_user_setPW")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_user_setPW() 
	{	
		String info = "密码成功修改！";
		Map<String,Object> map = new HashMap<String,Object>();
		
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String id = pd.getString("KeyValue"); //用户编码
		String password = pd.getString("Password");
		password = PasswordHash.encrypt(password);

		map.put("password", password);
		String where = "id='" + id +"'";
		if (systemService.update(loginTable,map,where) == -1){ //对指定用户的密码进行更新
			info = "密码修改失败！";
		}

		map.put("Message", info);
		return map;
	}

	@RequestMapping(value="/base_user_search", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object base_user_search() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String KeyValue = pd.getString("KeyValue");

		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   
		long totalRecord = 0; //数据库中满足条件的总记录数

		String where = "";
		if (!StringTool.isNullEmpty(KeyValue))//含有查询条件
		{
			where = "id='" + KeyValue + "'";
			//totalRecord = systemService.queryCount(tableName,where);
		}
		/*else
				totalRecord = systemService.queryCount(tableName);
		 */

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




	//自定义查询
	@RequestMapping(value="/user_advanceQuery")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object advanceQuery() 
	{
		//页面上需要：ControlType ControlSource FieldName FullName;
		List<query> lists = new ArrayList<query>();
		query obj = new query();
		obj.setControlSource("true");
		obj.setControlType(2);
		obj.setControlDefault("controlDefault");
		obj.setFieldName("fieldName");
		obj.setFullName("fullName");
		lists.add(obj);

		obj = new query();
		obj.setControlSource("false");
		obj.setControlType(3);
		obj.setControlDefault("controlDefault2");
		obj.setFieldName("fieldName2");
		obj.setFullName("fullName2");
		lists.add(obj);

		//String json =  ConvertTool.object2json(obj) ;
		String json =  ConvertTool.list2json(lists);//传到前台js中，可以正常得到对象

		return json; //将JSON数据返回页面
	}
	//返回查询结果
	@RequestMapping(value="/user_query")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object query() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String KeyValue = pd.getString("KeyValue");
		String KeyWords = pd.getString("KeyWords");

		String where = KeyWords + " LIKE '" + KeyValue + "'"; //= 与 like 的区别？

		List<Object> lists = new ArrayList<Object>();
		//lists = systemService.query(tableName,where);//密码解密需要单独处理

		/*if(! StringTool.listIsNullEmpty(lists))
		for (int i=0; i< lists.size(); i++){
		    decrypw((userinfo)lists.get(i));
		}*/
		return lists; 
	}

}
