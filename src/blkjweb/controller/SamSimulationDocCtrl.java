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

import blkjweb.service.*;
import blkjweb.utils.Const;

//主要要考虑用户的密码。 这里的所有方法都不涉及管理员，即用户名为gly的用户
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
		int index = (page - 1) * pageSize;   
	
		String where = "";
		//Map<String, Object> mapRecordT = new HashMap<String, Object>();
		//mapRecordT = systemService.queryOne(tableName,where); 
		//String sql = (String)mapRecordT.get("sql_val");

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

			String where = queryWhere + "= '" + keyValue + "'";

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
		
		String where = queryWhere + "= '" + keyValue + "'";
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
				
				//lists = systemService.executeQuery(sql);
				
				where = "";
				long totalRecord = 0l;  
				totalRecord = systemService.queryCount(tableName,where);
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

		String where = " WHERE " + deletWhere + "= '" + keyValue + "'";
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

			String where = "";
			boolean isUpdateKey = false; 
			if(! StringTool.isNullEmpty(keyName)){	//如果主键名不为空，传过来了主键信息
				where = keyName + "= '"+ oldID +"' AND " + changeWhere + "=" + keyValue;
				if(! StringTool.isNullEmpty(id) && //新编号不空 且 新旧编号不等 
						! oldID.equals(id)){
					isUpdateKey = true; //更新主键
					mapRecord.put(keyName, id);
				}
			}
			else{
				where = changeWhere + "= '" + keyValue + "'";
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

			boolean isUpdateKey = false; 
			String where1 = "cc='"+ cc +"'";
			String where2 = "qbid='"+ qbid +"'";
			
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
		
		//教师端设置毛玻璃
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
			
/**********************************对目录进行设置**************************************/
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
			
/**********************************对正文进行设置**************************************/
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
		
		//学生恢复数据
		@RequestMapping(value="/recDataSamSystem", produces="application/json; charset=utf-8")  
		@ResponseBody 
		public Object recDataSamSystem() 
		{
			String code = "2";
			String message = "成功恢复数据!";
			boolean bresult = true;
			
			String cch_dbml = "cch_dbml";
			String cch_dbzw = "cch_dbzw";
			String cch_dbml_backup = "cch_dbml_backup";
			String cch_dbzw_backup = "cch_dbzw_backup";
			
			PageTool pd = new PageTool();
			pd = this.getPageData();
			//获取学生的组号
			String teamnum =  pd.getString("teamnum");
			
			/******************************************
			 * 删除该组，位于目录和正文的现存数据
			 * 将备份中的数据的组号改为该组
			 * 将备份数据拷贝到目录和正文
			 * ***************************************/			
			String where = " WHERE teamnum = '" + teamnum + "'";
			String[] batchSQLStr = new String[9];					//用于同时删除多个表格
			batchSQLStr[0] = "DELETE FROM " + cch_dbml + where;		//删除记录语句
			batchSQLStr[1] = "DELETE FROM " + cch_dbzw + where;		//删除记录语句
			batchSQLStr[2] = "UPDATE " + cch_dbml_backup + " SET teamnum = '" + teamnum + "'";		//更新记录语句
			batchSQLStr[3] = "UPDATE " + cch_dbzw_backup + " SET teamnum = '" + teamnum + "'";		//更新记录语句
			batchSQLStr[4] = "INSERT INTO " + cch_dbml + " SELECT * FROM " + cch_dbml_backup;		//拷贝备份记录语句
			batchSQLStr[5] = "INSERT INTO " + cch_dbzw + " SELECT * FROM " + cch_dbzw_backup;		//拷贝备份记录语句
			bresult = systemService.batchMultiTable(batchSQLStr);	//执行指令

			
			if (! bresult){//不成功 <= 0
				code = "-1";
				message = "恢复数据失败!";
			}
			return message(code,message);
		}
		

}