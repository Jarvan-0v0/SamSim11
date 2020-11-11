package blkjweb.controller;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.blkj.img.ImageFormatConversion;
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

import blkjweb.service.*;
import blkjweb.utils.Const;

@Controller
public class CulvertRiskCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService; 

	//支持翻页
	@RequestMapping(value="/ComSQL_jqGrid_query", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object ComSQL_jqGrid_query() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize;   

		String tableName = pd.getString("TableName");
		String group = pd.getString("Group");

		String where = "(SELECT 1, max(pingdingdate) from "+ tableName + " GROUP BY "+group+") C";
		long totalRecord = systemService.executeCount(where);
		long totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;
	   
		String sql = "SELECT *, max(pingdingdate) as pingdingdate "
					+ " FROM " + tableName +" GROUP BY " + group
					+ " ORDER BY " + sort + " " + order
					+ " limit " +index +","+pageSize; 	    

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
	    lists = systemService.executeQuery(sql);

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);
		
		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";

		return json; 
	}
	
	@RequestMapping(value="/ComSQL_jqGrid_subQuery", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object ComSQL_jqGrid_subQuery() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize;   

		String qiaohao = pd.getString("qiaohao");
		String pingdingdate = pd.getString("pingdingdate");
		String tableName = pd.getString("TableName");
		
		String where = "";
		if(!StringTool.isNullEmpty(qiaohao))
			where = "qiaohao='" + qiaohao +"'";
		
		if(!StringTool.isNullEmpty(pingdingdate))
			where = where + " and pingdingdate !='" + pingdingdate +"'";
		
		long totalRecord = systemService.queryCount(tableName,where);
		long totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(tableName, sort, order, where,new Object[] {index, pageSize});
		
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json; 
	}
	
	@RequestMapping(value="uploadActionF",method=RequestMethod.POST,produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object uploadActionF(HttpServletRequest request)
	{    
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

		PageTool pd = new PageTool();
		pd = this.getPageData(multiRequest);		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}
		
		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}
		
		String tableName = (String)mapRecord.get("tableName");
		String path = (String)mapRecord.get("path");//文件名
		String Ids = (String)mapRecord.get("Ids");//主键格式：键：值,键：值...  AND关系
		String where = getSQLWhere(Ids);
		
		String imgNumName = (String)mapRecord.get("imgNumName");//保存图片数量的字段名
		
		if(StringTool.isNullEmpty(where))
			return "ERROR:前端代码有误，请检查上传代码中的Ids参数！";
		
		//需先判断记录是否存在。 目前的处理逻辑是：记录存在后才能上传图片。
		if (systemService.queryOne(tableName, where).size() <= 0)
			return "ERROR:先保存基本信息，然后上传图片！";
		
		//已上传且已经写到timageinfo中的图片数量
		String numStr = pd.getString("num");
		int num = StringTool.stringToInt(numStr);
		if(num < 0 ) num = 0;
	
		//本次上传的文件的次序信息
		String fileIndex = pd.getString("fileIndex");//上传第几个文件。从1开始编号
		int index = StringTool.stringToInt(fileIndex);
		if(index != -1)
			index = index + num;
		else
			index = num;
		
		//本次上传的文件数量 + 已上传文件数量， 即目前为止总的文件数量
		String fileNumStr = pd.getString("fileNum");//要上传的文件总数
		int fileNum = StringTool.stringToInt(fileNumStr);
		if(fileNum != -1)
			fileNum = fileNum + num;
		else
			fileNum = num;
		
		//组装图片的存放位置
		String folderId = Const.getQiaohanImagesPath() + Const.separator;//要写入的目录
		String imgPre = path + "-";//(表名-桥号-桥号-孔跨号:由客户端组装)-序号
		
		String ext = "";//扩展名
		String fullFileName = "";
		boolean flag = false;
		Iterator<String> iter = multiRequest.getFileNames();
		while (iter.hasNext()) {//一次上传一副图
			MultipartFile file = multiRequest.getFile(iter.next());// 取得上传文件
			if ( StringTool.isNull(file))
				continue;
			fullFileName = file.getOriginalFilename(); //获取原文件名 含扩展名

			if(! StringTool.isNullEmpty(fullFileName) && fullFileName.lastIndexOf(".")>-1)
			{//将扩展名转换为小写  
				ext = fullFileName.substring(fullFileName.lastIndexOf("."));
				if(!StringTool.isNullEmpty(ext)) ext = ext.toLowerCase();
			}
			else
				continue;
			
			if(ext.equalsIgnoreCase(".bmp"))
				new ImageFormatConversion().Conversion("bmp","jpg",(imgPre + index));
			else if(ext.equalsIgnoreCase(".png"))
				new ImageFormatConversion().Conversion("png","jpg",(imgPre + index));
			if(ext.equalsIgnoreCase(".gif"))
				new ImageFormatConversion().Conversion("gif","jpg",(imgPre + index));
			ext = ".jpg";	
				
			fullFileName = imgPre + index + ext;  
			flag = FileTool.copy(file,(folderId + fullFileName),true);//返回true写成功（写入硬盘）
		}
		boolean result = false;
		if(flag) {
			ArrayList<String> batchSQLStr = new ArrayList<String>();
			//上传时间  和  图编号（图名字及扩展名）命名方式：桥号-孔跨号-1,2,3
			//String sqlT = "UPDATE timageinfo SET id='" + (imgPre + index) +"',uploaddate='" + getNowTime() +"'";
			String sqlT =  "UPDATE " + tableName +" SET "+ imgNumName +"='" + fileNum +"' WHERE " + where;
			batchSQLStr.add(sqlT);
			/*if(num == 0)//需删除
				sqlT = "INSERT INTO " + tableName +"(handonghao,imgnum,imgext) values('" + handonghao + "','" + fileNum +"','" + ext +"') ON DUPLICATE KEY UPDATE " + where;
			else*/
			
			sqlT = "INSERT INTO timageinfo(id,uploaddate) values('" + (imgPre + index)+"','"+ getNowTime() +"')";			
			batchSQLStr.add(sqlT);
			
			result = systemService.batchInsertUpdate(batchSQLStr);//execute 
		}
		
		if(!result)
			return "但保存记录失败，请重传图片！";
		else
			return "且正确写入服务器。";
	} 
	

	@RequestMapping(value="image_info_deleteF",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_deleteF()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}

		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}

		String imgNumName = (String)mapRecord.get("imgNumName");//保存图片数量的字段名
		
		String tableName = (String)mapRecord.get("tableName");
		//String path = (String)mapRecord.get("path");//图在硬盘上的文件名部分

		String Ids = (String)mapRecord.get("Ids");//主键格式：键：值,键：值...  AND关系
		String where = getSQLWhere(Ids);
		//为了处理上的一致性，对桥渡添加了虚构的kongkuahao字段。在sql查询时应去掉
		if("tqiaodu".equalsIgnoreCase(tableName))
			where = where.substring(0,where.indexOf("AND"));	
				
		String total = pd.getString("total");//总记录数
		int totalNum = StringTool.stringToInt(total);
		
		//删除硬盘上的图片
		String folderId = Const.getQiaohanImagesPath() + Const.separator;//要写入的目录
		String img = pd.getString("img");
		
		String imgOrder = "";
		int orderNum = 0;
		boolean result;
		String code = "2";
		String message = "成功删除!";
		
		if(!StringTool.isNullEmpty(img))
		{
			int index = img.lastIndexOf("-");
			if(index != -1)
			{
				//获取要删除的图片序号，即tqiaomianjudge-7-1-2017-08-28-401011-2.jpg中"-2"中的数字"2"
				imgOrder = img.substring(index+1,index+2);
				orderNum = StringTool.stringToInt(imgOrder);
				
				String maxOrder = "";
				if(orderNum != totalNum){//删除的不是最后一个图片,需要将最后一个图片的序号修改为修好orderNum
					maxOrder = img.substring(0,index+1) + totalNum;
				}
				//先对库中的记录进行操作
				ArrayList<String> sqlArray = new ArrayList<String>();
				//上传时间  和  图编号（图名字及扩展名）命名方式：桥号-孔跨号-1,2,3
				String sqlT = "DELETE FROM timageinfo WHERE id= '" + img +"'";
				sqlArray.add(sqlT);

				if(maxOrder.length() > 0 ){
					sqlT = "UPDATE timageinfo SET id='" + img +"' WHERE id='" + maxOrder +"'";
					sqlArray.add(sqlT);
				}
				
				sqlT = "UPDATE " + tableName +" SET "+ imgNumName+"='" + (totalNum-1) +"' WHERE " + where;
				sqlArray.add(sqlT);
				
				result = systemService.batchMultiTable(sqlArray); 
				
				if(!result) {
					code = "-1";
					message = "删除失败!";
					return message(code,message); 
				}
				
				FileTool.delFile(folderId,img + ".jpg");
				
				//修改删除文件后硬盘上的文件序号，确保所有文件的序号是连续的。
				String oldName = maxOrder + ".jpg";
				FileTool.renameFile(folderId,oldName,img+ ".jpg");	
			}
		}
		return message(code,message); 
	} 
	
	
	@RequestMapping(value="/bridge_risk_delete")  
	@ResponseBody 
	public Object bridge_risk_delete() 
	{
		String code = "2";
		String message = "成功删除!";

		PageTool pd = new PageTool();
		pd = this.getPageData();

		String qiaohao = pd.getString("qiaohao");
		String pingdingdate = pd.getString("pingdingdate");
		
		String where = " WHERE qiaohao='" + qiaohao +"' AND pingdingdate='" + pingdingdate +"'";
		
		String[] batchSQLStr = new String[2];
		batchSQLStr[0] = "DELETE FROM triskcheck " + where;
		
		String imageID = "triskcheck_" + qiaohao + "_"+  pingdingdate;
		String where2 = " WHERE id LIKE '%" + imageID + "_%'";//图片
		batchSQLStr[1] = "DELETE FROM timageinfo" + where2;
		
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

	
	@RequestMapping(value="/bridge_risk_detail_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object bridge_risk_detail_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		
		String qiaohao = pd.getString("qiaohao");
		String pingdingdate = pd.getString("pingdingdate");
		String where = "qiaohao='" + qiaohao + "' AND pingdingdate='" + pingdingdate +  "'";
		
		Map<String, Object> mapRecord = new HashMap<String, Object>();

		mapRecord = systemService.queryOne("triskcheck",where); 

		if(mapRecord != null && mapRecord.size() > 0)
			return mapRecord ;
		else
			return "";
	}
	
	@RequestMapping(value="/bridge_select_init", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object bridge_select_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize;   

		long totalRecord = systemService.queryCount("tbridgebasicinfo");
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query("tbridgebasicinfo", 
				"qiaohao,qiaoming,zhongxinlicheng,shuoshugongdui", 
				sort, order, "", new Object[] {index, pageSize});

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";

		return json; 
	}

	@RequestMapping(value="/bridge_select_search", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object bridge_select_search() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String keywords = pd.getString("QueryWhere");
		String value = pd.getString("KeyValue");

		StringBuffer where = new StringBuffer();
		if (keywords.indexOf("qiaohao") == -1){//不含qiaohao
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

		long totalRecord = systemService.queryCount("tbridgebasicinfo",where.toString());
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; 

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query("tbridgebasicinfo", 
				"qiaohao,qiaoming,zhongxinlicheng,shuoshugongdui", 
				where.toString());
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json;
	}

	@RequestMapping(value="/culvert_select_searchF", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object culvert_risk_searchF() 
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

		long totalRecord = systemService.queryCount("thandongbasicinfo",where.toString());
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; 

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query("thandongbasicinfo", 
				"handonghao,handongname,zhongxinlicheng,shuoshugongdui", 
				where.toString());
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json;
	}
	
	@RequestMapping(value="/culvert_select_initF", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_select_initF() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		int pageSize = pd.getInt("rows");
		int page = pd.getInt("page");   
		String sort = pd.getString("sidx");
		String order = pd.getString("sord");
		int index = (page - 1) * pageSize;   

		long totalRecord = systemService.queryCount("thandongbasicinfo");
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1;

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query("thandongbasicinfo", 
				"handonghao,handongname,zhongxinlicheng,shuoshugongdui", 
				sort, order, "", new Object[] {index, pageSize});

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";

		return json; 
	}
	
	@RequestMapping(value="/culvert_risk_deleteF")  
	@ResponseBody 
	public Object culvert_risk_deleteF() 
	{
		String code = "2";
		String message = "成功删除!";

		PageTool pd = new PageTool();
		pd = this.getPageData();

		String handonghao = pd.getString("handonghao");
		String pingdingdate = pd.getString("pingdingdate");
		
		String where = " WHERE handonghao='" + handonghao +"' AND pingdingdate='" + pingdingdate +"'";
		
		String[] batchSQLStr = new String[2];
		batchSQLStr[0] = "DELETE FROM thandongriskcheck " + where;
		
		String imageID = "thandongriskcheck_" + handonghao + "_"+  pingdingdate;
		String where2 = " WHERE id LIKE '%" + imageID + "_%'";//图片
		batchSQLStr[1] = "DELETE FROM timageinfo" + where2;
		
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

	@RequestMapping(value="/culvert_risk_detail_initF", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object culvert_risk_detail_initF() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		
		String handonghao = pd.getString("handonghao");
		String pingdingdate = pd.getString("pingdingdate");
		String where = "handonghao='" + handonghao + "' AND pingdingdate='" + pingdingdate +  "'";
		
		Map<String, Object> mapRecord = new HashMap<String, Object>();

		mapRecord = systemService.queryOne("thandongriskcheck",where); 

		if(mapRecord != null && mapRecord.size() > 0)
			return mapRecord ;
		else
			return "";
	}
	
}
