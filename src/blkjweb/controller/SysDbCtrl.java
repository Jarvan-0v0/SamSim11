package blkjweb.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.blkj.utils.ConvertTool;
import org.blkj.utils.FileTool;
import org.blkj.utils.PageTool;
import org.blkj.utils.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import blkjweb.model.FileList;
import blkjweb.service.*;
import blkjweb.utils.Const;
import blkjweb.utils.MysqlBack;

@Controller
public class SysDbCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService;

	private final String tableName = "schema";

	//获取指定库中所有表
	@RequestMapping(value="/db_center_initGrid")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object dbInit() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		//String tableName = "schema";
		String DbName = pd.getString("DbName");

		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   
		long totalRecord = 0; //数据库中满足条件的总记录数

		//方法一
		//List<Object> lists = new ArrayList<Object>();
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.queryDBInfo(tableName,DbName);//一次性读取全部记录
		if (lists != null)
			totalRecord = lists.size();

		/*方法二
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			result = systemService.queryDBInfo(tableName,DbName);
			if (result != null)
			{
				totalRecord = result.size();
				for(int i=0; i < totalRecord; i++)
				{
					Map<String, Object> map = new HashMap<String, Object>();
					map = result.get(i);//一条记录
					schema obj = new schema();
					obj.setData_length((java.math.BigInteger)map.get("data_length"));
					obj.setEngine((String)map.get("engine"));
					obj.setIndex_length((java.math.BigInteger)map.get("index_length"));
					obj.setTable_comment((String)map.get("table_comment"));
					obj.setTable_name((String)map.get("table_name"));
					obj.setTable_rows((java.math.BigInteger)map.get("table_rows"));
					lists.add(obj);
				}
			}
		 */	

		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);//数据转换
		//支持服务器端翻页的Json格式
		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json; //将JSON数据返回页面
	}	

	//获取给定表的表结构。 子表格
	@RequestMapping(value="/db_center_tableInfo")  
	@ResponseBody 
	public Object getTableInfo() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String tableName = pd.getString("tableName");
		List<Map<String, Object>> mapRecord = new ArrayList<Map<String, Object>>();
		mapRecord = systemService.queryTableInfo(tableName); 
		String rowList = ConvertTool.list2json((ArrayList<?>)mapRecord);//数据转换

		String json = "{\"page\":" +"0" +"," +
				" \"total\":"  +"0" + "," + 
				" \"records\":" + "0" + "," + 
				" \"rows\":" + rowList +"}";

		return json; //将JSON数据返回页面
	}	

	//删除选定的库表
	@RequestMapping(value="/db_center_deleteTable", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object deleteTable() 
	{
		String code = "2";
		String message = "成功删除!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableName = pd.getString("KeyValue");
		String sql = "DROP TABLE IF EXISTS "+ tableName;
		systemService.execute(sql);//返回值无法判断操作是否成功
		boolean result = systemService.checkTableIfExist(tableName);//存在返回true
		if (result){//不成功
			code = "-1";
			message = "删除失败!";
		}
		return message(code,message);
	}


	@RequestMapping(value="/db_center_search", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object tableSearch() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableName = pd.getString("tableName");
		String DbName = pd.getString("DbName");

		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   
		long totalRecord = 0; //数据库中满足条件的总记录数
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数

		List<Map<String, Object>> mapRecord = new ArrayList<Map<String, Object>>();
		mapRecord = systemService.queryTableInfo(DbName,tableName); 
		String rowList = ConvertTool.list2json((ArrayList<?>)mapRecord);//数据转换

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json; 
	}	


	@RequestMapping(value="/db_center_detail", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object initGridTablePage() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String tableName = pd.getString("tableName");

		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   
		String sort = pd.getString("sidx");//排序列
		String order = pd.getString("sord");//排序方式
		int index = (page - 1) * pageSize; // 开始记录数   

		long totalRecord = systemService.queryCount(tableName); //数据库中满足条件的总记录数

		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数

		//List<Object> lists = new ArrayList<Object>();
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		//只从库中检索所需要的记录：LIMIT 5,10 检索记录行 6-15 第一个参数指定第一个返回记录行的偏移量，第二个参数指定返回记录行的最大数目
		lists = systemService.query(tableName, sort, order, "", new Object[] {index, pageSize});

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		//支持服务器端翻页的Json格式
		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";//
		//一次性全部读取，在客户端进行翻页     String json = " \"rows\":" + rowList +"}";

		return json; //将JSON数据返回页面
	}

	//编辑选定的记录
	@RequestMapping(value="/db_center_detail_EditDataTableRow", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object editDataTableRow() 
	{
		String code = "2";
		String message = "成功更新!";

		PageTool pd = new PageTool();
		pd = this.getPageData();

		String tableName = pd.getString("tableName");
		//String pk = pd.getString("pk");
		String entityJons = pd.getString("entityJons");

		Map<String, Object> mapRecord = ConvertTool.json2Map(entityJons);
		//目前不支持对主键的修改
		/*StringBuffer where = new StringBuffer();		
			if(!StringTool.isNullEmpty(pk))
			{
				String[] keys = pk.split(",");
				for (int i = 0; i <keys.length; i++){
					String key = keys[i];
					if (i != 0)
						where.append(" AND ");

					where.append(key);
					where.append("='");
					where.append(mapRecord.get(key)+"'");
				}
			}*/
		//系统会自动过滤到无用字段
		//int result = systemService.update(tableName, mapRecord, where.toString());
		int result = systemService.update(tableName, mapRecord,"");

		if (result < 0){//不成功
			code = "-1";
			message = "更新失败!";
		}
		return message(code,message);
	}		

	//删除选定的记录
	@RequestMapping(value="/db_center_detail_delete")  
	@ResponseBody
	public Object deleteDataTableRow() 
	{
		String code = "2";
		String message = "成功删除!";
	
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String tableName = pd.getString("tableName");
		String pk = pd.getString("pk");

		String entityJons = pd.getString("entityJons");
		Map<String, Object> map = ConvertTool.json2Map(entityJons);

		StringBuffer where = new StringBuffer();		
		if(!StringTool.isNullEmpty(pk))	{
			String[] keys = pk.split(",");
			for (int i = 0; i <keys.length; i++){
				String key = keys[i];
				if (i != 0) 	where.append(" AND ");
				where.append(key);
				where.append("='");
				where.append(map.get(key)+"'");
			}
		}

		int result = systemService.delete(tableName, where.toString());

		if (result <= 0){//不成功
			code = "-1";
			message = "删除失败!";
		}
		return message(code,message);
	}	

	@RequestMapping(value="/db_center_detail_search", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object searchDataTableRow() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String tableName = pd.getString("tableName");
		//{}格式
		String ParameterJson = pd.getString("ParameterJson");
		Map<String, Object> map = ConvertTool.json2Map(ParameterJson);

		String KeyValue = (String)map.get("paramValue");
		StringBuffer where = new StringBuffer();
		/*<option value="Null">为空</option> 目前不处理
         <option value="NotNull">不为空</option>
		 */		
		if (!StringTool.isNullEmpty(KeyValue)){
			where.append(map.get("paramName"));
			where.append(map.get("Operation"));
			where.append("'" + KeyValue + "'");
		}
		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   

		//数据库中满足条件的总记录数
		long totalRecord = systemService.queryCount(tableName,where.toString());
		//计算总页数
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; 

		//List<Object> lists = new ArrayList<Object>();
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(tableName,where.toString());

		/*if (lists != null)
			totalRecord = lists.size();*/

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);//数据转换
		//支持服务器端翻页的Json格式
		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json; //将JSON数据返回页面
	}



	//以下为数据库备份与恢复的内容


	@RequestMapping(value="/db_center_dbback")  
	@ResponseBody
	public Object db_center_dbback() 
	{
		String code = "2";
		String message = "数据库成功备份!";
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		String file = sdf.format(date) + ".sql";

		MysqlBack dbBack = new MysqlBack();
		if (!dbBack.backup(file))
		{
			code = "-1";
			message = "数据库备份失败!";
		}
		return message(code,message);
	}	

	@RequestMapping(value="/db_center_backupList", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object dbBackup() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		//String DbName = pd.getString("DbName");
		String tableName = "jobplan";

		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   
		long totalRecord = 0; //数据库中满足条件的总记录数

		String where = "";
		//List<Object> lists = new ArrayList<Object>();
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(tableName,where);//queryTableOfDB(DbName,tableName);

		if (lists != null)
			totalRecord = lists.size();

		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);//数据转换

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";

		return json; 
	}	

	//需要对特定目录下的文件进行操作  名称 修改时间 类型 大小
	@RequestMapping(value="/db_center_backupfile")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object dbBackupFile() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   
		int totalRecord = 0; //数据库中满足条件的总记录数

		String path = Const.getPathDbback();//保留数据库备份文件的目录

		//获取目录下所有文件(按时间排序)
		List<File> allFile =  FileTool.getFileSortByTime(path);

		if (allFile != null)
			totalRecord = allFile.size();

		int totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数

		int index = (page - 1) * pageSize; // 开始记录数   

		List<FileList> fileList = new ArrayList<FileList>();

		int end =  index + pageSize;
		if (end >= totalRecord)
			end = totalRecord;

		for(int i=index; i< end; i++)
		{
			File tFile = allFile.get(i);
			if (tFile.isFile())
			{   
				String name = tFile.getName();
				if (name.contains(".sql"))//仅显示含有.sql的文件
				{ 
					FileList file  = new FileList();
					file.setName(name);
					file.setFullName(path+name);
					file.setSize(tFile.length());
					file.setTime(""+ (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(tFile.lastModified()));

					fileList.add(file);
				}
			}
		}
		String rowList = ConvertTool.list2json((ArrayList<?>)fileList);//数据转换
		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json; 
	}	

	//下载选取的特定文件
	@RequestMapping(value="/db_center_file_download")  
	@ResponseBody
	public void downloadOneFile()
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String fileName = pd.getString("KeyValue"); 
	
		String path = Const.getPathDbback();//保留数据库备份文件的目录
		/*if( !getAuthCode("90","1111"))
		{
			fileName =  "default.pdf";
			path = Const.getDocCenterPath() +"/temp/";
		}*/
		
		if (FileTool.checkFile(path + fileName))//检查压缩后的文件是否存在，存在
			printToClient(path,fileName);
	}

	//下载: 目录下所有文件打包下载
	@RequestMapping(value="/db_center_allFile_download")  
	@ResponseBody
	public void downloadAllFile()
	{
		String path = Const.getPathDbback();//保留数据库备份文件的目录
		String fileName = "DBFiles.zip";
		File zipFile = new File(path + fileName);//建立保存文件的路径
		FileTool.compress(path,zipFile);//进行压缩  暂不处理压缩失败的问题
		if (FileTool.checkFile(path + fileName))//检查压缩后的文件是否存在，存在
			printToClient(path,fileName);
	}
	
	//上传文件
	@RequestMapping(value="/db_center_upload", method=RequestMethod.POST, produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object db_center_upload(HttpServletRequest request)
	{
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		
		//读取上传的文件
		MultipartFile mf = multipartRequest.getFile("uploadFile");//读取上传的文件
		String folderId = Const.getPathDbback();
				
		boolean flag = false;
		String responseStr = "文档成功上传！";
		if (mf != null && ! mf.isEmpty()){// 得到上传的文件的文件名
			//String content = file.getContentType(); //application/msexcel 
			//String fileName = file.getName(); //文件选择框中 id所给出的文件名 
			String filename = mf.getOriginalFilename(); //上传文件的原始文件名
			flag = FileTool.copy(mf,folderId,filename);
			if(!flag)
				responseStr = "文档上传失败！";
		}
		else
			responseStr = "文档上传失败！(可能原因：没有选择附件！)";

		return responseStr;//返回给前台的提示信息
	}


	//恢复数据库操作
	@RequestMapping(value="/db_center_file_restore")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object delete() 
	{
		String code = "2";
		String message = "成功恢复数据!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String fileName = pd.getString("KeyValue");
		boolean result = false;
		if (! StringTool.isNullEmpty(fileName))
			result = new MysqlBack().restore(fileName);

		if (!result ){//不成功
			code = "-1";
			message = "数据恢复失败!";
		}

		return message(code,message);
	}

	//删除目录下所有文件
	@RequestMapping(value="/db_center_file_broom")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object clearAll() 
	{
		String code = "2";
		String message = "成功清空!";
		
		String path = Const.getPathDbback();//保留数据库备份文件的目录
		boolean result = false;
		//检查某目录是否为空.空返回true
		result = FileTool.dirIfEmpty(path);
		if(!result)
		{
			FileTool.delDirFile(path, false);//删除指定目录（含字目录）下全部文件和目录
			result = FileTool.dirIfEmpty(path);
		}
		if (!result){//不成功
			code = "-1";
			message = "清空失败!，或目录下无文件！";
		}

		return message(code,message);
	}








	private String assemblySQL(String tableName, List<Map<String, Object>> fieldList)
	{ 
		String sqlCreatePre = "CREATE TABLE " + tableName + "(";

		String sqlCreateSuf = ")ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;";

		String sql = "";		 
		String sqltemp = "";
		int size = 0 ; 
		if(fieldList != null){
			size = fieldList.size();
			boolean flag = false;
			boolean pkFlag = false;
			String primaryKey = "";
			for(int  i = 0; i < size; i++){//所有字段
				Map<String, Object> map = new HashMap<String, Object>();
				map = fieldList.get(i);
				Iterator<String> iterator = map.keySet().iterator();
				String column = "";
				String dataBaseType = "";
				String defaultValue = "";
				String description = "";
				String allowNull = "";
				flag = false;
				pkFlag = false;
				while (iterator.hasNext()) {//某一字段
					String key = iterator.next();
					String value = (String) map.get(key);
					if(key.equalsIgnoreCase("Column")){
						if(StringTool.isNullEmpty(value)){
							flag = true;
							break;
						}
						else{
							column = value  + " ";
						}
					}
					else 
						if(key.equalsIgnoreCase("DataBaseType")){
							if(StringTool.isNullEmpty(value)){
								flag = true;
								break;
							}
							else
								dataBaseType = " " + value + " ";
						}
						else 
							if(key.equalsIgnoreCase("DefaultValue") && !StringTool.isNullEmpty(value) )
								defaultValue = " DEFAULT '" + value + "'";
							else 
								if(key.equalsIgnoreCase("Description") &&  !StringTool.isNullEmpty(value) ) 
									description  = " comment' " + value + "'";
								else 
									if(key.equalsIgnoreCase("AllowNull") && value.equalsIgnoreCase("0") )//1表示选中  允许空
										allowNull = " NOT NULL ";
									else 
										if(key.contains("PrimaryKey") && value.equalsIgnoreCase("1") ) //1表示选中, 是主键
											pkFlag = true;
				}//while

				if(pkFlag) {
					if(StringTool.isNullEmpty(primaryKey))
						primaryKey = column;
					else
						primaryKey = "," + column;
				}
				if (flag) 
					continue;
				else
					sqltemp = sqltemp + column + dataBaseType + allowNull + defaultValue + description + ", ";

			}//for
			if (!StringTool.isNullEmpty(primaryKey)) {
				sqltemp = sqltemp + " PRIMARY KEY (" + primaryKey +") ";	

				sql = sqlCreatePre + sqltemp + sqlCreateSuf;
			} 
		}
		return sql;
	}
	/**对于建库等涉及库表结构的操作，本系统不适合提供此类功能，原因是：本系统启东时，会先读取库表结构，然后所有操作都基于读出来的信息进行
	 * 新建的库表结构，如果不启动服务器的话，不会起作用
	 */
	//建库
	@RequestMapping(value="/sys_db_form_SubmitForm")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object createTableSQL() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String tableName = pd.getString("KeyValue");
		String tableFieldJson = pd.getString("TableFieldJson");//JSON: [{},{},{}]格式

		List<Map<String, Object>> list = ConvertTool.json4Map(tableFieldJson);
		String sqlStr = assemblySQL(tableName, list);
		String sqlDrop = "DROP TABLE IF EXISTS " + tableName + "; ";


		systemService.execute(new String[]{sqlDrop,sqlStr});//返回值无法判断操作是否成功

		boolean result = systemService.checkTableIfExist(tableName);//存在返回true

		String code = "2";
		String message = "成功删除!";
		if (!result){//不成功
			code = "-1";
			message = "删除失败!";
		}
		return message(code,message);

	}

	//获取建库的SQL语句
	@RequestMapping(value="/sys_db_form_lookSql")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object lookSql() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableFieldJson = pd.getString("TableFieldJson");
		String tableName = pd.getString("KeyValue");
		List<Map<String, Object>> list = ConvertTool.json4Map(tableFieldJson);
		String message = assemblySQL(tableName, list);

		return message("2",message);

	}
}

