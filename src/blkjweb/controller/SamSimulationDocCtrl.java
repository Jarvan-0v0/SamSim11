package blkjweb.controller;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.blkj.utils.ConvertTool;
import org.blkj.utils.DateTool;
import org.blkj.utils.FileTool;
import org.blkj.utils.PageTool;
import org.blkj.utils.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysql.fabric.xmlrpc.base.Data;

import blkjweb.service.*;
import blkjweb.utils.Const;


/**
 * 主要要考虑用户的密码。 这里的所有方法都不涉及管理员，即用户名为001的用户
 * 当传过来的信息既有queryWhere 和 keyValue时，以此二者组成条件语句where。
 * 否则直接以传过来的where为准
 * @author Administrator
 * 2021年1月13日	by ljw
 * 
 */

@Controller
public class SamSimulationDocCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService; 

	//private final String tableName = "sql_chaxunyuju";		//存放sql语句的数据库;
	
	//初始化表格
	@RequestMapping(value="/initSamSystem", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object initSamSystem() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String tableName = pd.getString("tableName");
		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		
		//增加用户筛选
		String where = pd.getString("where");
		
		int index = (page - 1) * pageSize;   
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		//lists = systemService.executeQuery(sql);
		
		long totalRecord = systemService.queryCount(tableName,where);
		lists= systemService.query(tableName,sort,order, where,new Object[] {index, pageSize});
		long totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";

		return json; 
	}
	
	//查询单条，返回一条记录
	@RequestMapping(value="/searchOneSamSystem", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object searchOneSamSystem() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableName =  pd.getString("tableName");
		String queryWhere = pd.getString("queryWhere");
		String keyValue = pd.getString("keyValue");
		
		//增加用户筛选,当queryWhere和keyVaule都不为空时，使用二者组成where语句，
		//否则使用传递过来的where语句
		String where = "";
		if(!StringTool.isNullEmpty(queryWhere) && !StringTool.isNullEmpty(keyValue)){
			where = queryWhere + "= '" + keyValue + "'";
		}
		else {
			where = pd.getString("where");
		}

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = systemService.queryOne(tableName,where); 
		mapRecord = ConvertTool.changeNullSpace((ConvertTool.json2Map(ConvertTool.map2json(mapRecord))));

		if(mapRecord != null && mapRecord.size() > 0)
			return mapRecord ;
		else
			return "";
	}
		
	//查询多条，返回表格
	@RequestMapping(value="/searchSamSystem", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object searchSamSystem() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableName =  pd.getString("tableName");
		String queryWhere = pd.getString("queryWhere");
		String keyValue = pd.getString("keyValue");
		
		//增加用户筛选,当queryWhere和keyVaule都不为空时，使用二者组成where语句，
		//否则使用传递过来的where语句
		String where = "";
		if(!StringTool.isNullEmpty(queryWhere) && !StringTool.isNullEmpty(keyValue)){
			where = queryWhere + "= '" + keyValue + "'";
		}
		else {
			where = pd.getString("where");
		}
		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize;  
		
		long totalRecord = 0l;  	
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		totalRecord = systemService.queryCount(tableName,where);
		lists= systemService.query(tableName,sort,order, where,new Object[] {index, pageSize});

		long totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; 
		
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json;
	}

	//查询sql语句数据库，并返回信息
	@RequestMapping(value="/searchSQLSamSystem", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object searchSQLSamSystem() 
	{
		/**
		 * 此函数，须传入两部分，一个queryWhere和keyValue为查询SQL语句的条件
		 * 再传入where条件为，后续根据SQL语句进行查询的条件
		 */
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableName =  pd.getString("tableName");
		String queryWhere = pd.getString("queryWhere");
		String keyValue = pd.getString("keyValue");
		String where = queryWhere + "= '" + keyValue + "'";
		
		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize; 

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = systemService.queryOne(tableName,where);	
		String sql_tableName = (String)mapRecord.get("sql_value");	//获取相应SQL语句
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		
		//增加用户筛选，根据传递过来的where进行查询
		where = pd.getString("where");
		
		long totalRecord = 0l;  
		totalRecord = systemService.queryCount(sql_tableName,where);
		lists= systemService.query(sql_tableName,sort,order, where,new Object[] {index, pageSize});
		long totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";

		return json; 
	}
	
	//删除选定的桥的所有记录
	@RequestMapping(value="/delSamSystem", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object delSamSystem() 
	{
		String code = "2";
		String message = "成功删除记录!";
	
		PageTool pd = new PageTool();
		pd = this.getPageData();
		
		String tableName =  pd.getString("tableName");
		String deletWhere = pd.getString("deletWhere");
		String keyValue = pd.getString("keyValue");

		//增加用户筛选,当queryWhere和keyVaule都不为空时，使用二者组成where语句，
		//否则使用传递过来的where语句
		String where = "";
		if(!StringTool.isNullEmpty(deletWhere) && !StringTool.isNullEmpty(keyValue)){
			where = deletWhere + "= '" + keyValue + "'";
		}
		else {
			where = pd.getString("where");
		}
		String[] batchSQLStr = new String[9];	//用于同时删除多个表格

		batchSQLStr[0] = "DELETE FROM " + tableName + where;		//删除记录语句
		
		boolean result = systemService.batchMultiTable(batchSQLStr);

		if (! result){//不成功 <= 0
			code = "-1";
			message = "删除记录失败!";
		}
		
		return message(code,message);
	}
	
	//保存编辑后的信息
	@RequestMapping(value="/editSaveSamSystem", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object editSaveSamSystem() 
	{
		String code = "2";
		String message = "成功更新!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = pd.getMap();

		String tableName =  pd.getString("tableName");
		String keyName = pd.getString("keyName");
		String id = pd.getString("id");
		String oldID = pd.getString("oldID");
		
		String changeWhere = pd.getString("changeWhere");
		String keyValue = pd.getString("keyValue");

		//增加用户筛选,当queryWhere和keyVaule都不为空时，使用二者组成where语句，
		//否则使用传递过来的where语句
		String where = "";
		boolean isUpdateKey = false; 
		if(! StringTool.isNullEmpty(keyName)){	//如果主键名不为空，传过来了主键信息
			if(!StringTool.isNullEmpty(changeWhere) && !StringTool.isNullEmpty(keyValue)){
				where = keyName + "= '"+ oldID +"' AND " + changeWhere + "=" + keyValue +"'";
			}
			else {
				where = keyName + "= '"+ oldID +"' AND " + pd.getString("where");
			}
			if(! StringTool.isNullEmpty(id) && //新编号不空 且 新旧编号不等 
					! oldID.equals(id)){
				isUpdateKey = true; //更新主键
				mapRecord.put(keyName, id);
			}
		}
		else{
			if(!StringTool.isNullEmpty(changeWhere) && !StringTool.isNullEmpty(keyValue)){
				where = changeWhere + "=" + keyValue +"'";
			}
			else {
				where = pd.getString("where");
			}
		}
		
		//系统会自动过滤到无用字段
		Set set = mapRecord.entrySet();/// 返回此映射所包含的映射关系的 Set 视图。  
        Iterator iterator = set.iterator();  
  
        while (iterator.hasNext()) {  
            Map.Entry mapentry = (Map.Entry) iterator.next(); /////Map.Entry－－Map的内部类，描述Map中的按键/数值对。    
            System.out.println(mapentry.getKey() + "   " + mapentry.getValue()+"in " + tableName + " edit save");  
  
        }  
			
		int result = systemService.update(tableName, mapRecord,isUpdateKey, where);

		if (result <= 0){//不成功
			code = "-1";
			message = "更新失败!";
		}
		return message(code,message);
	}

	//增加一条记录
	@RequestMapping(value="/addSamSystem", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object addSamSystem() 
	{
		String code = "2";
		String message = "成功保存记录!";
		
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		mapRecord = pd.getMap();
		
		String tableName = pd.getString("tableName");
		int result = systemService.updateInsert(tableName, mapRecord);
				
		if (result < 0){//不成功
			code = "-1";
			message = "保存记录失败!";
		}
		return message(code,message);
	}	
	
	@RequestMapping(value="/changeTrackSamStstem", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object changeTrackSamStstem() 
	{
		String code = "2";
		String message = "成功更新!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = pd.getMap();

		String cc = pd.getString("cc");
		String qbid = pd.getString("qbid");

		//增加用户筛选
		String where = pd.getString("where");
		boolean isUpdateKey = false; 
		String where1 = "cc='"+ cc + "' AND" + where;
		String where2 = "qbid='"+ qbid + "' AND" + where;
		
		//系统会自动过滤到无用字段
		Set set = mapRecord.entrySet();/// 返回此映射所包含的映射关系的 Set 视图。  
        Iterator iterator = set.iterator();  
  
        while (iterator.hasNext()) {  
            Map.Entry mapentry = (Map.Entry) iterator.next(); /////Map.Entry－－Map的内部类，描述Map中的按键/数值对。    
            System.out.println(mapentry.getKey() + "   " + mapentry.getValue()+" in cch_dbml edit save");  
  
        }  
			
		String tableName =  "cch_dbml";	
		int result = systemService.update(tableName, mapRecord,isUpdateKey, where1);
		if (result <= 0){//不成功
			code = "-1";
			message = "更新目录失败!";
			return message(code,message);
		}

		String tableName2 =  "cch_dbzw";	
		result = systemService.update(tableName2, mapRecord,isUpdateKey, where2);

		if (result <= 0){//不成功
			code = "-1";
			message = "更新正文失败!";
		}
		return message(code,message);
	}
	
	//保存班计划数据
	@RequestMapping(value="/setBJHSamSystem", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object setBJHSamSystem() 
	{
		String code = "2";
		String message = "成功保存班计划数据!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableName = pd.getString("tableName");
		String stnum = pd.getString("stnum");
		String testname = pd.getString("testname");
		String where = pd.getString("where");
		//String len = pd.getString("len");
		
		//请空原来数据，可能原本没有，无需判断是否失败
		systemService.delete(tableName, where);
		
		String dataString = pd.getString("data");
		int left = 0, right = 0;
		for (int i = 1; i < dataString.length()-1; i++) {
			if(dataString.charAt(i) == '{'){
				left = i; 
			}
			else if(dataString.charAt(i) == '}') {
				right = i;
				String jsonLiString = dataString.substring(left, right+1);

				Map<String, Object> mapRecord = ConvertTool.json2Map(jsonLiString);
				mapRecord.put("stnum", stnum);
				mapRecord.put("testname", testname);
				int result = systemService.updateInsert(tableName, mapRecord);
				
				if (result < 0){//不成功
					code = "-1";
					message = "保存记录失败!";
				}
			}
			
		}
		
		return message(code,message);
	}	
	
	/**
	 * 教师端导入固定模板
	 * @return
	 */
	@RequestMapping(value="/setModelSamSystem", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object setModelSamSystem() 
	{
		String code = "2";
		String message = "成功导入固定模板!";
		
		final String testname_backup = "固定模板";
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableName = pd.getString("tableName");
		String tableName_backup = tableName + "_backup";
		String stnum = pd.getString("stnum");
		String testname = pd.getString("testname");
		String where = "stnum = '" + stnum + "' AND testname = '" + testname + "'";
		
		//请空备份表数据，可能原本没有，无需判断是否失败
		systemService.delete(tableName, where);
		systemService.broom(tableName_backup);
		
		ArrayList<String> batchSQLStr = new ArrayList<String>();
		//将固定模板数据拷贝到备份数据库
		String sql = "INSERT INTO " + tableName_backup + " SELECT * FROM " + tableName 
				+ " WHERE stnum = '" + stnum + "' AND testname = '" + testname_backup + "'";
		batchSQLStr.add(sql);
		
		//更新备份数据库的实训名称
		sql = "UPDATE " + tableName_backup + " SET testname = '" + testname + "'";
		batchSQLStr.add(sql);
		
		
		//将备份数据库修改后的内容复制到原数据库
		sql = "INSERT INTO " + tableName + " SELECT * FROM " + tableName_backup;
		batchSQLStr.add(sql);
		
		boolean bresult = systemService.batchInsertUpdate(batchSQLStr);
		if (!bresult){//不成功
			code = "-1";
			message = "导入模板失败!";
		}
		return message(code,message);
	}	
	
	
		
		/*************
		 * 此部分无用，删除
		 * 
		 * @return
		 */
/*		//教师端设置毛玻璃
		@RequestMapping(value="/setMBLSamSystem", produces="application/json; charset=utf-8")  
		@ResponseBody 
		public Object setMBLSamSystem() 
		{
			String code = "2";
			String message = "成功设置模板!";
			
			boolean bresult = true;
			int iresut = 1;
			String cch_dbml = "cch_dbml";
			String cch_dbzw = "cch_dbzw";
			String cch_dbml_backup = "cch_dbml_backup";
			String cch_dbzw_backup = "cch_dbzw_backup";
			int stuNum = 3;				//学生组数
			String sql = "";
			
			PageTool pd = new PageTool();
			pd = this.getPageData();
			String cch_dbml_new = pd.getString("cch_dbml");
			String cch_dbzw_new = pd.getString("cch_dbzw");
			
*//**********************************对目录进行设置**************************************//*
			//请空原来的目录
			iresut = systemService.broom(cch_dbml);
			if (iresut < 0){//不成功
				code = "-1";
				message = "删除目录失败!";
				return message(code,message);
			}
			for(int i=1; i<=stuNum; i++){
				//修改模板数据库组号
				sql = "";
				sql = "UPDATE ";		
				sql += cch_dbml_new;
				sql += " SET teamnum = '";
				sql += i;
				sql += "'";
				
				bresult = systemService.execute(sql);
				
				sql = "";
				sql += "INSERT INTO ";
				sql += cch_dbml;
				sql += " SELECT * FROM ";
				sql += cch_dbml_new;
				bresult = systemService.execute(sql);
				
			}
			
			//请空备份目录
			iresut = systemService.broom(cch_dbml_backup);
			//复制选定模板到备份表
			sql = "";
			sql += "INSERT INTO ";
			sql += cch_dbml_backup;
			sql += " SELECT * FROM ";
			sql += cch_dbml_new;
			bresult = systemService.execute(sql);
			
*//**********************************对正文进行设置**************************************//*
			//请空原来的正文
			iresut = systemService.broom(cch_dbzw);
			if (iresut < 0){//不成功
				code = "-1";
				message = "删除正文失败!";
				return message(code,message);
			}
			
			for(int i=1; i<=stuNum; i++){
				//修改模板数据库组号
				sql = "";
				sql = "UPDATE ";		
				sql += cch_dbzw_new;
				sql += " SET teamnum = '";
				sql += i;
				sql += "'";
				
				bresult = systemService.execute(sql);
				
				sql = "";
				sql += "INSERT INTO ";
				sql += cch_dbzw;
				sql += " SELECT * FROM ";
				sql += cch_dbzw_new;
				bresult = systemService.execute(sql);
				
			}
			
			//请空备份正文
			iresut = systemService.broom(cch_dbzw_backup);
			//复制选定模板到备份表
			sql = "";
			sql += "INSERT INTO ";
			sql += cch_dbzw_backup;
			sql += " SELECT * FROM ";
			sql += cch_dbzw_new;
			bresult = systemService.execute(sql);
			
			return message(code,message);
		}	
	*/	
		//学生恢复数据
		@RequestMapping(value="/recDataSamSystem", produces="application/json; charset=utf-8")  
		@ResponseBody 
		public Object recDataSamSystem() 
		{
			String code = "2";
			String message = "成功恢复数据!";
			boolean bresult = true;
			
			ArrayList<String> tableName = new ArrayList<String>();	//所有表名
			tableName.add("cch_dbml");
			tableName.add("cch_dbzw");
			tableName.add("bjh_bgzzrw");
			tableName.add("bjh_lccfjh");
			tableName.add("bjh_lcddjh");
			tableName.add("bjh_pkjh");
			tableName.add("bjh_tsztsj");
			tableName.add("bjh_xcjh");
			
			PageTool pd = new PageTool();
			pd = this.getPageData();
			//获取学生id和实训名称
			String stnum =  pd.getString("stnum");
			String testname =  pd.getString("testname");
			String where = "stnum = '" + stnum + "' AND testname = '" + testname + "'";
			
			/******************************************
			 * 删除该id位于上述表中现存数据
			 * 将上述这些表中对应实训的数据拷贝到备份表，并修改学生id
			 * 将备份表的数据复制回来
			 * ***************************************/	
			for (String table : tableName) {
				String tableName_backup = table + "_backup";
				systemService.broom(tableName_backup);
				systemService.delete(table, where);
				
				ArrayList<String> batchSQLStr = new ArrayList<String>();
				String sql = "INSERT INTO " + tableName_backup + " SELECT * FROM " + table 
						+ " WHERE stnum = '1001' AND testname = '" + testname + "'";
				batchSQLStr.add(sql);
				
				sql = "UPDATE " + tableName_backup + " SET stnum = '" + stnum + "'";
				batchSQLStr.add(sql);
				
				sql = "INSERT INTO " + table + " SELECT * FROM " + tableName_backup;
				batchSQLStr.add(sql);
				
				bresult = systemService.batchInsertUpdate(batchSQLStr);
			}
			
			if (! bresult){//不成功 <= 0
				code = "-1";
				message = "恢复数据失败!";
			}
			return message(code,message);
		}
	

}