package blkjweb.controller;

import java.util.*;
import javax.annotation.Resource;
import org.blkj.utils.ConvertTool;
import org.blkj.utils.FileTool;
import org.blkj.utils.PageTool;
import org.blkj.utils.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import blkjweb.service.*;
import blkjweb.utils.Const;

@Controller
public class CulvertDocCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService; 

	private final String handongbasicinfoTable = "thandongbasicinfo";
	private final String handongjudgeTable = "thandongjudge";
	private final String handongriskcheckTable = "thandongriskcheck";
	private final String handongrichangxunjianTable = "thandongrichangxunjian";
	private final String handongweihuTable = "thandongweihu";
	private final String unitTable = "tbridgezenrenren";

	@RequestMapping(value="/culvert_doc_delete")  
	@ResponseBody
	public Object culvert_doc_delete() 
	{
		String code = "2";
		String message = "成功删除记录!";

		PageTool pd = new PageTool();
		pd = this.getPageData();

		String handonghao = pd.getString("Keyword");
		String where = " WHERE handonghao='" + handonghao +"'";
		String where2 = " WHERE qiaohao='" + handonghao +"' AND idtype=1";

		String[] batchSQLStr = new String[4];

		batchSQLStr[0] = "DELETE FROM " + handongbasicinfoTable + where;
		batchSQLStr[1] = "DELETE FROM " + unitTable  + where2;

		String imageID = "thandongbasicinfo_" + handonghao;
		String where3 = " WHERE id LIKE '%" + imageID + "-%'";//图片
		String where4 = " WHERE id LIKE '%" + imageID + "_tuzhi-%'";//图纸
		batchSQLStr[2] = "DELETE FROM timageinfo" + where3;
		batchSQLStr[3] = "DELETE FROM timageinfo" + where4;
		
		boolean result = systemService.batchMultiTable(batchSQLStr);

		if (! result){//不成功 <= 0
			code = "-1";
			message = "删除记录失败!";
		}
		else {//删除硬盘上的图片
			String folderId = Const.getQiaohanImagesPath() + Const.separator;//要写入的目录
			FileTool.delMultiFile(folderId,imageID);
		}
		return message(code,message);
	}

	@RequestMapping(value="/culvert_check_delete")  
	@ResponseBody 
	public Object culvert_check_delete() 
	{
		String code = "2";
		String message = "成功删除!";

		PageTool pd = new PageTool();
		pd = this.getPageData();

		String qiaohao = pd.getString("qiaohao");
		String pingdingdate = pd.getString("pingdingdate");

		String where = " WHERE qiaohao='" + qiaohao +"' AND pingdingdate='" + pingdingdate + "'";
		//int result = systemService.delete(handongjudgeTable,where);

		String[] batchSQLStr = new String[4];

		batchSQLStr[0] = "DELETE FROM " + handongjudgeTable + where;
		
		String imageID = "thandongjudge_" + qiaohao + "_" + pingdingdate;
		String where2 = " WHERE id LIKE '%" + imageID + "_%'";//图片
		batchSQLStr[2] = "DELETE FROM timageinfo" + where2;

		boolean result = systemService.batchMultiTable(batchSQLStr);

		if (! result){//不成功 <= 0
			code = "-1";
			message = "删除记录失败!";
		}
		else {//删除硬盘上的图片.getQiaohanImagesPath()
			String folderId = Const.getBridgeImagesPath() + Const.separator;//要写入的目录
			FileTool.delMultiFile(folderId,imageID);
		}
		
		return message(code,message);
	}
	
	
	@RequestMapping(value="/culvert_doc_basicinfo_add")  
	@ResponseBody 
	public Object culvert_doc_basicinfo_add() 
	{
		String code = "2";
		String message = "成功保存记录!";

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		mapRecord = pd.getMap();

		int result = systemService.updateInsert(handongbasicinfoTable, mapRecord);

		if (result < 0){//不成功
			code = "-1";
			message = "保存记录失败!";
		}
		return message(code,message);
	}

	@RequestMapping(value="/culvert_doc_unitInfo_add")  
	@ResponseBody 
	public Object culvert_unitInfo_add() 
	{
		String code = "2";
		String message = "成功保存记录!";

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		mapRecord = pd.getMap();

		mapRecord.put("idtype","1");//0桥；1涵 此库桥涵共用
		int result = systemService.updateInsert(unitTable, mapRecord);//insert

		if (result < 0){//不成功
			code = "-1";
			message = "保存记录失败!";
		}
		return message(code,message);
	}

	@RequestMapping(value="/culvert_doc_search", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object culvert_doc_search() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String keywords = pd.getString("QueryWhere");
		String value = pd.getString("KeyValue");

		StringBuffer where = new StringBuffer();
		if (!StringTool.isNullEmpty(value)){
			where.append(keywords);
			where.append("'" + value + "'");
		}
		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");    

		long totalRecord = systemService.queryCount(handongbasicinfoTable,where.toString());
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; 

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(handongbasicinfoTable,where.toString());

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json;
	}
	@RequestMapping(value="/culvert_basicinfo_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_basicinfo_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String keyValue = pd.getString("KeyValue");

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = systemService.queryOne(handongbasicinfoTable,"handonghao",keyValue); 

		if(mapRecord != null && mapRecord.size() > 0)
			return mapRecord ;
		else
			return "";
	}

	//保存编辑后的信息
	@RequestMapping(value="/culvert_basicinfo_editsave")  
	@ResponseBody
	public Object culvert_basicinfo_editsave() 
	{
		String code = "2";
		String message = "成功更新!";

		PageTool pd = new PageTool();
		pd = this.getPageData();
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = pd.getMap();

		String id = pd.getString("handonghao");
		String oldID = pd.getString("oldHandonghao");

		boolean isUpdateKey = false; 
		String where = "handonghao='"+ oldID +"'";
		if(! StringTool.isNullEmpty(id) && //新编号不空 且 新旧编号不等 
				! oldID.equals(id)){
			isUpdateKey = true; //更新主键
			mapRecord.put("handonghao", id);
		}
		//系统会自动过滤到无用字段
		int result = systemService.update(handongbasicinfoTable, mapRecord,isUpdateKey, where);

		if (result < 0){//不成功
			code = "-1";
			message = "更新失败!";
		}
		return message(code,message);
	}
	@RequestMapping(value="/culvert_unitInfo_delete")  
	@ResponseBody 
	public Object culvert_unitInfo_delete() 
	{
		String code = "2";
		String message = "成功删除!";

		PageTool pd = new PageTool();
		pd = this.getPageData();

		String qiaohao = pd.getString("KeyValue");

		String where = "qiaohao='" + qiaohao +"' AND idtype=1";
		int result = systemService.delete(unitTable, where);

		if (result <= 0){//不成功
			code = "-1";
			message = "删除失败!";
		}
		return message(code,message);
	}
	//保存编辑后的信息
	@RequestMapping(value="/culvert_unitInfo_editsave")  
	@ResponseBody
	public Object culvert_unitInfo_editsave() 
	{
		String code = "2";
		String message = "成功更新!";

		PageTool pd = new PageTool();
		pd = this.getPageData();
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = pd.getMap();
		String id = pd.getString("unitQiaohao");

		//存在更新，不存在插入
		mapRecord.put("qiaohao",id);
		mapRecord.put("idtype","1");
		int result = systemService.updateInsert(unitTable, mapRecord);

		if (result < 0){//不成功
			code = "-1";
			message = "更新失败!";
		}
		return message(code,message);
	}
	@RequestMapping(value="/culvert_doc_init", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object culvert_doc_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize;   

		long totalRecord = systemService.queryCount(handongbasicinfoTable);
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(handongbasicinfoTable, sort, order, "", new Object[] {index, pageSize});

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";

		return json; 
	}

	@RequestMapping(value="/culvert_unitInfo_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_unitInfo_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String keyValue = pd.getString("KeyValue");

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		String where = "qiaohao='" + keyValue +"' AND idtype=1";
		mapRecord = systemService.queryOne(unitTable, where);

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query("danwei","id,mobile,danweileibie","");
		if(mapRecord != null && mapRecord.size() > 0){//最多一条记录
			for (Map<String, Object> map: lists) {//lists的每个Map包含有danwei表的一条记录，字段有：id,mobile,danweileibie
				String unitID = "";
				String mobile = "";
				int danweileibie = 0;
				for (String keyT : map.keySet()) {//含有三个元素
					Object valueT = map.get(keyT);
					if( keyT.equalsIgnoreCase("danweileibie") ) 
						danweileibie = (Integer)valueT; 
					else if( keyT.equalsIgnoreCase("id") ) 
						unitID = (String)valueT;
					else if( keyT.equalsIgnoreCase("mobile") ) 
						mobile = (String)valueT;
				}
				if (danweileibie == 50 && unitID.equals(mapRecord.get("shigongdanwei")))//桥梁施工单位号 	
					mapRecord.put("shigongdanweiphone", mobile);
				else if (danweileibie == 40 && unitID.equals(mapRecord.get("jianlidanwei"))) //桥梁监理单位号	
					mapRecord.put("jianlidanweiphone", mobile);
				else if (danweileibie == 30  && unitID.equals(mapRecord.get("shejidanwei"))) //桥梁设计	
					mapRecord.put("shejidanweiphone", mobile);	
				else if (danweileibie == 20 && unitID.equals(mapRecord.get("jianshedanwei"))) //桥梁建设单位	
					mapRecord.put("jianshedanweiphone", mobile);
			}
			return mapRecord ;
		}
		else
			return "";
	}


	

	@RequestMapping(value="/culvert_check_handong_select_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_check_handong_select_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize;   

		long totalRecord = systemService.queryCount(handongbasicinfoTable);
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(handongbasicinfoTable, 
				"handonghao,handongname,shuoshugongdui", 
				sort, order, "", new Object[] {index, pageSize});

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";

		return json; 
	}

	//查询
	@RequestMapping(value="/culvert_check_handong_select_search", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object culvert_check_handong_select_search() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String keywords = pd.getString("QueryWhere");
		String value = pd.getString("KeyValue");

		StringBuffer where = new StringBuffer();
		if (keywords.indexOf("handonghao") == -1){//不含qiaohao
			if (!StringTool.isNullEmpty(value)){
				where.append(keywords);
				where.append(" '%" + value + "%'");
			}
		}
		else{
			where.append(keywords);
			where.append("'" + value + "'");
		}

		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");    

		long totalRecord = systemService.queryCount(handongbasicinfoTable,where.toString());
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; 

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(handongbasicinfoTable, 
				"handonghao,handongname,shuoshugongdui",
				where.toString());
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json;
	}
	
	
	
	
	
	
	
	//private final String handongjudgeTable = "thandonghaojudge";
	@RequestMapping(value="/culvert_check_search", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_check_search() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String key = pd.getString("Query");
		String operation = pd.getString("Logic");
		String value = pd.getString("Queryvalue");

		StringBuffer where = new StringBuffer();
		where.append(key);
		boolean flag = false;
		if( key.equals("handongname") ||  key.equals("qiaohao") )  
		{
			if( operation.equals("=") ){
				where.append(" " + operation);
				where.append("'" + value + "'");
				flag = true;
			}
			else if( operation.equals("LIKE") ){
				where.append(" " + operation);
				where.append("'%" + value + "%'");
				flag = true;
			}
		}
		
		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");    
		long totalRecord = 0; 
		long  totalPage = 0;
		String rowList;
		if (flag){
			String begindate = pd.getString("Begindate");
			String enddate = pd.getString("Enddate");
			if (! StringTool.isNullEmpty(begindate))
				where.append(" AND pingdingdate>='" + begindate + "'");
			if (! StringTool.isNullEmpty(enddate))
				where.append(" AND pingdingdate<='" + enddate + "'");

			totalRecord = systemService.queryCount(handongjudgeTable,where.toString());
			List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
			lists= systemService.query(handongjudgeTable,where.toString());

			totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数
			rowList = ConvertTool.list2json((ArrayList<?>)lists);//数据转换
		}
		else
			rowList= null;

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json;
	}
	

	@RequestMapping(value="/culvert_check_init_focus", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_check_init_focus() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize;   

		String handonghao = pd.getString("handonghao");
		String where = "handonghao='" + handonghao + "'";
		
		long totalRecord = systemService.queryCount(handongjudgeTable,where);
		long totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(handongjudgeTable, sort, order, where,new Object[] {index, pageSize});

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json; 
	}
	
	
	@RequestMapping(value="/culvert_check_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_check_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize;   

		long totalRecord = systemService.queryCount(handongjudgeTable);
		long totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(handongjudgeTable, sort, order, "",new Object[] {index, pageSize});

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json; 
	}
	
	@RequestMapping(value="/culvert_check_add")  
	@ResponseBody 
	public Object culvert_check_add() 
	{
		String code = "2";
		String message = "成功保存记录!";

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		mapRecord = pd.getMap();
		
		getTotalJudge(mapRecord);
		
		int result = systemService.updateInsert(handongjudgeTable, mapRecord);

		if (result < 0){//不成功
			code = "-1";
			message = "保存记录失败!";
		}
		return message(code,message);
	}
	
	@RequestMapping(value="/culvert_check_edit_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_check_edit_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String qiaohao = pd.getString("qiaohao");
		String pingdingdate = pd.getString("pingdingdate");
		
		String where = "qiaohao='" + qiaohao + "' AND pingdingdate='" + pingdingdate  + "'";
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = systemService.queryOne(handongjudgeTable,where); 

		if(mapRecord != null && mapRecord.size() > 0)
			return mapRecord ;
		else
			return "";
	}
	@RequestMapping(value="/culvert_check_edit_save")  
	@ResponseBody
	public Object culvert_check_edit_save() 
	{
		String code = "2";
		String message = "成功保存记录!";

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		mapRecord = pd.getMap();

		String qiaohao = pd.getString("qiaohao");
		String pingdingdate = pd.getString("pingdingdate");
		
		String where = "qiaohao='" + qiaohao + "' AND pingdingdate='" + pingdingdate  + "'";
		
		getTotalJudge(mapRecord);
		
		//由多个字段构成主键的情况
		int result = systemService.update_raw(handongjudgeTable,mapRecord,where); 	

		if (result < 0){//不成功 对于update： = 0页表示成功
			code = "-1";
			message = "更新失败!";
		}
		return message(code,message);
	}
	private void getTotalJudge(Map<String, Object> mapRecord)
	{
		String level = getFinalJudge(mapRecord);//获取本次的所有评价等级
		//获取最差的评价结果
		String levelTotal = "";
		if(level.indexOf("AA") != -1)
			levelTotal = "AA";
		else if(level.indexOf("A1") != -1)
			levelTotal = "A1";
		else if(level.indexOf("B") != -1)
			levelTotal = "B";
		else if(level.indexOf("C") != -1)
			levelTotal = "C";
		else if(level.indexOf("D") != -1)
			levelTotal = "D";
		mapRecord.put("zongjudge",levelTotal);
		
		//同步涵洞基本信息库中的劣化等级
		Map<String, Object> mapRecordT = new HashMap<String, Object>();
		String handonghao = (String)mapRecord.get("handonghao");
		String whereT = "handonghao='" + handonghao + "'";
		mapRecordT.put("handonghao", handonghao);
		mapRecordT.put("liehuadengji",levelTotal);
		systemService.update(handongbasicinfoTable, mapRecordT,whereT);
				
	}
	
	
/**风险源检查*/
	
	@RequestMapping(value="/culvert_risk_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_risk_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize;   

		long totalRecord = systemService.queryCount(handongriskcheckTable);
		long totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(handongriskcheckTable, sort, order, "",new Object[] {index, pageSize});
		
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json; 
	}
	@RequestMapping(value="/culvert_risk_delete")  
	@ResponseBody 
	public Object culvert_risk_delete() 
	{
		String code = "2";
		String message = "成功删除!";

		PageTool pd = new PageTool();
		pd = this.getPageData();

		String handonghao = pd.getString("handonghao");
		String pingdingdate = pd.getString("pingdingdate");
		
		String where = "handonghao='" + handonghao + "' AND pingdingdate='" + pingdingdate +  "'";

		int result = systemService.delete(handongriskcheckTable, where);

		if (result <= 0){//不成功
			code = "-1";
			message = "删除失败!";
		}
		return message(code,message);
	}
	@RequestMapping(value="/culvert_risk_search", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_risk_search() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String key = pd.getString("Query");
		String operation = pd.getString("Logic");
		String value = pd.getString("Queryvalue");

		StringBuffer where = new StringBuffer();
		where.append(key);
		boolean flag = false;
		if( key.equals("handongname") || //桥名
				key.equals("handonghao") )  //桥号
		{
			if( operation.equals("=") ){
				where.append(" " + operation);
				where.append("'" + value + "'");
				flag = true;
			}
			else if( operation.equals("LIKE") ){
				where.append(" " + operation);
				where.append("'%" + value + "%'");
				flag = true;
			}
		}
		
		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");    
		long totalRecord = 0; 
		long  totalPage = 0;
		String rowList;
		if (flag){
			String begindate = pd.getString("Begindate");
			String enddate = pd.getString("Enddate");
			if (! StringTool.isNullEmpty(begindate))
				where.append(" AND pingdingdate>='" + begindate + "'");
			if (! StringTool.isNullEmpty(enddate))
				where.append(" AND pingdingdate<='" + enddate + "'");

			totalRecord = systemService.queryCount(handongriskcheckTable,where.toString());
			List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
			lists= systemService.query(handongriskcheckTable,where.toString());

			totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数
			rowList = ConvertTool.list2json((ArrayList<?>)lists);//数据转换
		}
		else
			rowList= null;

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json;
	}
	@RequestMapping(value="/culvert_risk_add")  
	@ResponseBody 
	public Object culvert_risk_add() 
	{
		String code = "2";
		String message = "成功保存记录!";

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		mapRecord = pd.getMap();

		int result = systemService.insert(handongriskcheckTable, mapRecord);

		if (result <= 0){//不成功
			code = "-1";
			message = "保存记录失败!";
		}
		return message(code,message);
	}
	@RequestMapping(value="/culvert_risk_detail_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_risk_detail_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		
		String handonghao = pd.getString("handonghao");
		String pingdingdate = pd.getString("pingdingdate");
		String where = "handonghao='" + handonghao + "' AND pingdingdate='" + pingdingdate +  "'";
		
		Map<String, Object> mapRecord = new HashMap<String, Object>();

		mapRecord = systemService.queryOne(handongriskcheckTable,where); 

		if(mapRecord != null && mapRecord.size() > 0)
			return mapRecord ;
		else
			return "";
	}
	@RequestMapping(value="/culvert_risk_edit_save")  
	@ResponseBody
	public Object culvert_risk_edit_save() 
	{
		String code = "2";
		String message = "成功保存记录!";

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		mapRecord = pd.getMap();

		String handonghao = pd.getString("handonghao");
		String pingdingdate = pd.getString("pingdingdate");
		String where = "handonghao='" + handonghao + "' AND pingdingdate='" + pingdingdate +  "'";
		//由多个字段构成主键的情况
		int result = systemService.update_raw(handongriskcheckTable,mapRecord,where); 	

		if (result < 0){//不成功 对于update： = 0页表示成功
			code = "-1";
			message = "更新失败!";
		}
		return message(code,message);
	}
	
	@RequestMapping(value="/culvert_patrol_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_patrol_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize;   

		long totalRecord = systemService.queryCount(handongrichangxunjianTable);
		long totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(handongrichangxunjianTable, sort, order, "",new Object[] {index, pageSize});

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json; 
	}
	
	
	@RequestMapping(value="/culvert_patrol_search", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_patrol_search() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String key = pd.getString("Query");
		String operation = pd.getString("Logic");
		String value = pd.getString("Queryvalue");

		StringBuffer where = new StringBuffer();
		where.append(key);
		boolean flag = false;
		if( key.equals("handonghao")||
				key.equals("handongname") )
		{
			if( operation.equals("=") ){
				where.append(" " + operation);
				where.append("'" + value + "'");
				flag = true;
			}
			else if( operation.equals("LIKE") ){
				where.append(" " + operation);
				where.append("'%" + value + "%'");
				flag = true;
			}
		}
		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");    
		long totalRecord = 0; 
		long  totalPage = 0;
		String rowList;
		if (flag){
			String begindate = pd.getString("Begindate");
			String enddate = pd.getString("Enddate");
			if (! StringTool.isNullEmpty(begindate))
				where.append(" AND pingdingdate>='" + begindate + "'");
			if (! StringTool.isNullEmpty(enddate))
				where.append(" AND pingdingdate<='" + enddate + "'");

			totalRecord = systemService.queryCount(handongrichangxunjianTable,where.toString());
			List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
			lists= systemService.query(handongrichangxunjianTable,where.toString());

			totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数
			rowList = ConvertTool.list2json((ArrayList<?>)lists);//数据转换
		}
		else
			rowList= null;

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json;
	}
	
	@RequestMapping(value="/culvert_patrol_delete")  
	@ResponseBody 
	public Object culvert_patrol_delete() 
	{
		String code = "2";
		String message = "成功删除!";

		PageTool pd = new PageTool();
		pd = this.getPageData();

		String handonghao = pd.getString("handonghao");
		String pingdingdate = pd.getString("pingdingdate");

		String where = "handonghao='" + handonghao + "' AND pingdingdate='" + pingdingdate + "'";

		int result = systemService.delete(handongrichangxunjianTable, where);

		if (result <= 0){//不成功
			code = "-1";
			message = "删除失败!";
		}
		return message(code,message);
	}
	
	@RequestMapping(value="/culvert_patrol_detail_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_patrol_detail_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		
		String handonghao = pd.getString("handonghao");
		String pingdingdate = pd.getString("pingdingdate");

		String where = "handonghao='" + handonghao + "' AND pingdingdate='" + pingdingdate + "'";

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = systemService.queryOne(handongrichangxunjianTable,where); 

		if(mapRecord != null && mapRecord.size() > 0){
			return mapRecord ;
		}
		else
			return "";
	}
	@RequestMapping(value="/culvert_patrol_add")  
	@ResponseBody 
	public Object culvert_patrol_add() 
	{
		String code = "2";
		String message = "成功保存记录!";

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		mapRecord = pd.getMap();

		int result = systemService.insert(handongrichangxunjianTable, mapRecord);

		if (result <= 0){//不成功
			code = "-1";
			message = "保存记录失败!";
		}
		return message(code,message);
	}
	
	@RequestMapping(value="/culvert_patrol_edit_save")  
	@ResponseBody
	public Object culvert_patrol_edit_save() 
	{
		String code = "2";
		String message = "成功保存记录!";

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		mapRecord = pd.getMap();

		String handonghao = pd.getString("handonghao");
		String pingdingdate = pd.getString("pingdingdate");
		String where = "handonghao='" + handonghao + "' AND pingdingdate='" + pingdingdate +  "'";
		//由多个字段构成主键的情况
		int result = systemService.update_raw(handongrichangxunjianTable,mapRecord,where); 	

		if (result < 0){//不成功 对于update： = 0页表示成功
			code = "-1";
			message = "更新失败!";
		}
		return message(code,message);
	}
	
	@RequestMapping(value="/culvert_weihu_add")  
	@ResponseBody 
	public Object culvert_weihu_add() 
	{
		String code = "2";
		String message = "成功保存记录!";

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		mapRecord = pd.getMap();

		int result = systemService.insert(handongweihuTable, mapRecord);

		if (result <= 0){//不成功
			code = "-1";
			message = "保存记录失败!";
		}
		return message(code,message);
	}
	
	@RequestMapping(value="/culvert_weihu_detail_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_weihu_detail_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		
		String handonghao = pd.getString("handonghao");
		

		String where = "handonghao='" + handonghao + "'";

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = systemService.queryOne(handongweihuTable,where); 

		if(mapRecord != null && mapRecord.size() > 0){
			return mapRecord ;
		}
		else
			return "";
	}
	
	@RequestMapping(value="/culvert_weihu_edit_save")  
	@ResponseBody
	public Object culvert_weihu_edit_save() 
	{
		String code = "2";
		String message = "成功保存记录!";

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		mapRecord = pd.getMap();

		String handonghao = pd.getString("handonghao");
		String where = "handonghao='" + handonghao +  "'";
		//由多个字段构成主键的情况
		int result = systemService.update_raw(handongweihuTable,mapRecord,where); 	

		if (result < 0){//不成功 对于update： = 0页表示成功
			code = "-1";
			message = "更新失败!";
		}
		return message(code,message);
	}
	@RequestMapping(value="/culvert_weihu_delete")  
	@ResponseBody 
	public Object culvert_weihu_delete() 
	{
		String code = "2";
		String message = "成功删除!";

		PageTool pd = new PageTool();
		pd = this.getPageData();

		String handonghao = pd.getString("handonghao");
		String where = "handonghao='" + handonghao +  "'"; 
		
		int result = systemService.delete(handongweihuTable,where);

		if (result <= 0){//不成功 <= 0
			code = "-1";
			message = "删除记录失败!";
		}
		return message(code,message);
	}

	@RequestMapping(value="/culvert_weihu_search", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_weihu_search() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String key = pd.getString("Query");
		String operation = pd.getString("Logic");
		String value = pd.getString("Queryvalue");

		StringBuffer where = new StringBuffer();
		where.append(key);
		boolean flag = false;
		if( key.equals("projectname")||
		   key.equals("handonghao") )
		{
			if( operation.equals("=") ){
				where.append(" " + operation);
				where.append("'" + value + "'");
				flag = true;
			}
			else if( operation.equals("LIKE") ){
				where.append(" " + operation);
				where.append("'%" + value + "%'");
				flag = true;
			}
		}
		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");    
		long totalRecord = 0; 
		long  totalPage = 0;
		String rowList;
		if (flag){
			String begindate = pd.getString("Begindate");
			String enddate = pd.getString("Enddate");
			if (! StringTool.isNullEmpty(begindate))
				where.append(" AND pingdingdate>='" + begindate + "'");
			if (! StringTool.isNullEmpty(enddate))
				where.append(" AND pingdingdate<='" + enddate + "'");

			totalRecord = systemService.queryCount(handongweihuTable,where.toString());
			List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
			lists= systemService.query(handongweihuTable,where.toString());

			totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数
			rowList = ConvertTool.list2json((ArrayList<?>)lists);//数据转换
		}
		else
			rowList= null;

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json;
	}
	
	@RequestMapping(value="/culvert_weihu_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_weihu_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize;   

		long totalRecord = systemService.queryCount(handongweihuTable);
		long totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(handongweihuTable, sort, order, "",new Object[] {index, pageSize});

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json; 
	}
	
	
}
//handongweihuTable