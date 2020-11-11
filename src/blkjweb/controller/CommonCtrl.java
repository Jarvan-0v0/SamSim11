package blkjweb.controller;


import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import blkjweb.service.*;
import blkjweb.utils.Const;
//图片命名规则：table + "_" + $("#qiaohao").val()-序号
@Controller
public class CommonCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService; 
	

	@RequestMapping(value="/ComSQL_update")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object ComSQL_update() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = pd.getMap();
		String tableName =  pd.getString("TableName");
		String where = getSQLWhere(pd.getString("Ids"));//主键格式：键:值
		String updateKey =  pd.getString("UpdateKey");//是否更新主键: true更新; 默认不更新
		boolean isUpdateKey = false; 
		if(!StringTool.isNullEmpty(updateKey) && "true".equalsIgnoreCase(updateKey)) 
			isUpdateKey = true; //更新主键

		int result = systemService.update(tableName, mapRecord,isUpdateKey,where);

		return message(result,"更新");
	}
	
	@RequestMapping(value="/ComSQL_updateInsert")  
	@ResponseBody 
	public Object ComSQL_updateInsert() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String tableName = pd.getString("TableName");
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = pd.getMap();

		int result = -1;
		if( !StringTool.isNullEmpty(tableName) && !StringTool.isNull(mapRecord) )
			result = systemService.updateInsert(tableName, mapRecord);

		return message(result,"更新");
	}
	
	@RequestMapping(value="/ComSQL_insert")  
	@ResponseBody 
	public Object ComSQL_insert() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();
		
		String tableName =  pd.getString("TableName");

		int result = systemService.insert(tableName, mapRecord);

		return message(result,"添加信息");
	}


	
	
    //显示timageinfo表中已有记录
	@RequestMapping(value="image_info_init8",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_init8()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();

		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}
		
		/**此版本将图的有关信息都保存在timageinfo表中（评价表中仍需要保留图片个数）；
		       所以：原有代码从前端传来的参数tableName，Ids等都不需要了，
		*/
		//String where = getSQLWhere((String)mapRecord.get("Ids"));
		String tableName = "timageinfo";
		//path: table + "_" + handonghao
		String imageID = (String)mapRecord.get("path");//imageID为图片文件命名规则的前缀部分：后缀部分为：-1,-2,-3。。。
		String where = " id LIKE '%" + imageID + "-%' ORDER BY id";//, uploaddate
		
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query(tableName,where);
		
		/**String sql="SELECT id,descript,max(pingdingdate) FROM timageinfo WHERE "+ where +" Group by gkuahao 
		 	lists= systemService.executeQuery(sql);
		 */	
		
		if(!StringTool.isNull(lists)){
			for(int i=0; i<lists.size(); i++){
				Map<String, Object> map = new HashMap<String, Object>();
				map = lists.get(i);
				String fieldValue = (String)map.get("id");
				map.put("fullname", fieldValue + ".jpg");
			}
		}

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		return rowList; 
	} 
	//增加新纪录
	@RequestMapping(value="uploadAction8",method=RequestMethod.POST,produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object uploadAction8(HttpServletRequest request)
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
		
		if(StringTool.isNullEmpty(where))
			return "ERROR:前端代码有误，请检查上传代码中的Ids参数！";
		
		//为了处理上的一致性，对桥渡添加了虚构的kongkuahao字段。在sql查询时应去掉
		if("tqiaodu".equalsIgnoreCase(tableName))
			where = where.substring(0,where.indexOf("AND"));	
		
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
			String sqlT =  "UPDATE " + tableName +" SET imgnum ='" + fileNum +"' WHERE " + where;
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
	
	@RequestMapping(value="image_info_delete8",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_delete8()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}

		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}

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
		String img = pd.getString("img");//tqiaomianjudge-7-1-2017-08-28-401011-2.jpg 图片名字中“.jpg”前面的内容为img
		
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
				
				sqlT = "UPDATE " + tableName +" SET imgnum='" + (totalNum-1) +"' WHERE " + where;
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
	
	@RequestMapping(value="image_info_change8",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_change8()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		
		//图片所在目录
		String folderId = Const.getQiaohanImagesPath() + Const.separator;//要写入的目录
		String selecedImg = pd.getString("selecedImg");//新首页图
		//String firstImg = pd.getString("firstImg");//原来的首页图
	
		String code = "2";
		String message = "首页图片变更成功!";
		
		String firstImg = "";
		int orderNum = 0;
		int index = selecedImg.lastIndexOf("-");
		if(index != -1)
		{//获取图片序号，即tqiaomianjudge-7-1-2017-08-28-401011-2.jpg中"-2"中的数字"2"
			String imgOrder = selecedImg.substring(index+1,selecedImg.length());
			orderNum = StringTool.stringToInt(imgOrder);
		}
		else {
			code = "-1";
			message = "无法进行首页图片变更!";
			return message(code,message); 
		}

		if(orderNum == 1){
			code = "-1";
			message = "无需进行首页图片变更!";
			return message(code,message); 
		}
		
		boolean result = false;
		if(! StringTool.isNullEmpty(selecedImg))
		{
			firstImg = selecedImg.substring(0,index+1) + "1";
			//先对两条记录的主键进行交互
			ArrayList<String> sqlArray = new ArrayList<String>();
			String sqlT = "UPDATE timageinfo SET id='-1111' WHERE id='" + firstImg + "'";
			sqlArray.add(sqlT);
			
			/*sqlT = "UPDATE timageinfo SET id='" + firstImg +"' WHERE id='" + selecedImg + "'";
			sqlArray.add(sqlT);
			sqlT = "UPDATE timageinfo SET id='" + selecedImg +"' WHERE id='-1111'";*/
			
			//===============================修改于2018年10月14日10:40:19
//			sqlT = "UPDATE timageinfo as t1,timageinfo as t2"
//				  +" SET t1.id='" + firstImg +"',t2.id='" + selecedImg +"'"
//					+ " WHERE t1.id='" + selecedImg +"' AND t2.id='-1111'";
			sqlT="UPDATE timageinfo SET id='"+firstImg+"' WHERE id='" + selecedImg +"'";
			sqlArray.add(sqlT);
			sqlT="UPDATE timageinfo SET id='"+selecedImg+"' WHERE id='-1111'";
			sqlArray.add(sqlT);
			
			result = systemService.batchMultiTable(sqlArray); 
					
			/*
			两张图的内容进行交换（除主键）
			String sql ="UPDATE timageinfo AS S1 JOIN timageinfo AS S2"
						+ " ON (S1.id ='"+ firstImg +"' AND S2.id ='" + selecedImg+ "')"
						+ " SET S1.descript=S2.descript,S2.descript=S1.descript,"
						+ "S1.uploaddate=S2.uploaddate,S2.uploaddate=S1.uploaddate,"
						+ "S1.uploader=S2.uploader,S2.uploader=S1.uploader";
			result = systemService.execute(sql);
			*/
			
					
			if(!result) {
				code = "-1";
				message = "首页图片变更失败!";
				return message(code,message); 
			}
			String temp = "a01a";
			//renameFile(String path,String oldName,String newName)
			result = FileTool.renameFile(folderId,firstImg+".jpg",temp);//修改首图的文件名为一临时名称
			if(result){//各种情况的处理不完整 
				result = FileTool.renameFile(folderId,selecedImg+".jpg",firstImg+".jpg");
				if(result)//成功变更名称
					result = FileTool.renameFile(folderId,temp,selecedImg+".jpg");
				else
					FileTool.renameFile(folderId,temp,firstImg+".jpg");
			}
		}
		if (!result){
			code = "-1";
			message = "首页图片变更失败!";
		}
		return message(code,message); 
	} 
		
	//增加新纪录
	@RequestMapping(value="uploadAction82",method=RequestMethod.POST,produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object uploadAction82(HttpServletRequest request)
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
		
		if(StringTool.isNullEmpty(where))
			return "ERROR:前端代码有误，请检查上传代码中的Ids参数！";
		//需先判断记录是否存在。 目前的处理逻辑是：记录存在后才能上传图片。
		if (systemService.queryOne(tableName,where).size() <= 0)
			return "ERROR:先保存基本信息，然后上传图纸！";
		
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
			String sqlT = "UPDATE " + tableName +" SET tuzhinum ='" + fileNum +"' WHERE " + where; 
			batchSQLStr.add(sqlT);
			/*if(num == 0)//
				sqlT = "INSERT INTO " + tableName +"(handonghao,tuzhinum,tuzhiext) values('" + handonghao + "','" + fileNum +"','" + ext +"') ON DUPLICATE KEY UPDATE " + where;
			else*/
			
			sqlT = "INSERT INTO timageinfo (id,uploaddate) values('" + (imgPre + index)+"','"+ getNowTime() +"')";
			batchSQLStr.add(sqlT);
			
			result = systemService.batchInsertUpdate(batchSQLStr);//execute 
		}
		
		if(!result)
			return "但保存记录失败，请重传图纸！";
		else
			return "且正确写入服务器。";
	} 
	
	@RequestMapping(value="image_info_delete82",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_delete82()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}

		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}

		String tableName = (String)mapRecord.get("tableName");
		//String path = (String)mapRecord.get("path");//图在硬盘上的文件名部分

		String Ids = (String)mapRecord.get("Ids");//主键格式：键：值,键：值...  AND关系
		String where = getSQLWhere(Ids);
		
		String total = pd.getString("total");//总记录数
		int totalNum = StringTool.stringToInt(total);
		
		//删除硬盘上的图片
		String folderId = Const.getQiaohanImagesPath() + Const.separator;//要写入的目录
		String img = pd.getString("img");//tqiaomianjudge-7-1-2017-08-28-401011-2.jpg 图片名字中“.jpg”前面的内容为img
		
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
				
				sqlT = "UPDATE " + tableName +" SET tuzhinum='" + (totalNum-1) +"' WHERE " + where;
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
	//获取涵洞的图纸信息
	@RequestMapping(value="/bridgeService_check_imgsBrowser22", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object bridgeService_check_imgsBrowser22() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		//imagePath为图片文件命名规则的前缀部分：后缀部分为：-1,-2,-3。。。
		String imagePath = pd.getString("Path");
		String where = "id LIKE '%" + imagePath + "-%' ORDER BY id";
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query("timageinfo","descript,uploaddate",where);
		int code = 0;
		String message ="";
		if (lists != null)
			code = lists.size();
		
		for(int i=0; i<code; i++){
			Map<String, Object> mapT = new HashMap<String, Object>();
			mapT = lists.get(i);
			String descript = (String)mapT.get("descript");
			Object uploaddate = mapT.get("uploaddate");
            if(StringTool.isNullEmpty(descript))
            	descript = "无标注信息";
            if(StringTool.isNull(uploaddate))
            	uploaddate = "无上传日期";
            
			if(message.length() == 0)
				message = descript +"("+uploaddate+")";
			else
				message = message + ";" + descript +"("+uploaddate+")";
		}
		
		Map<String,String> rMap = new HashMap<String,String>();
		rMap.put("Code", code+"");
		rMap.put("Message", message);
		return rMap;
		
		
		/*
		 String tableName = pd.getString("TableName");
		
		String where = pd.getString("Where");
		String item = "tuzhinum"; 
		Map<String, Object> map = new HashMap<String, Object>();
		map = systemService.queryOne4Items(tableName,item,where); 

		
		if(!StringTool.isNull(map)){
			Object value = map.get("tuzhinum");
			code = StringTool.intToString((Integer)value);
		}
		 if(!StringTool.isNull(map))
			for (Entry<String, Object> entry: map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if(StringTool.isNullEmpty(key) ||StringTool.isNull(value) )
					continue;
				if(key.equals("tuzhinum"))
					code = StringTool.intToString((Integer)value);
				else if(key.equals("tuzhiext"))
					message = (String)value;
			}*/
	}

	// 处理文件上传
	@RequestMapping(value="uploadAction3",method=RequestMethod.POST,produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object uploadAction3(HttpServletRequest request)
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
		
		String judgeCode = (String)mapRecord.get("judge");
		String imgnum = "imgnum"+ judgeCode;
		
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
		
		String folderId = "";//要写入图片的的目录
		folderId = Const.getBridgeImagesPath() + Const.separator;//原来的代码
		
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
			String sqlT = "UPDATE " + tableName +" SET "+ imgnum + "='" + fileNum +"' WHERE " + where; 
			batchSQLStr.add(sqlT);
			
			sqlT = "INSERT INTO timageinfo (id,uploaddate) values('" + (imgPre + index)+"','"+ getNowTime() +"')";
			batchSQLStr.add(sqlT);
			
			result = systemService.batchInsertUpdate(batchSQLStr);//execute 
		}
		
		if(!result)
			return "但保存记录失败，请重传图片！";
		else
			return "且正确写入服务器。";
	} 


	@RequestMapping(value="image_info_delete",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_delete()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}

		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}

		String tableName = (String)mapRecord.get("tableName");
		String Ids = (String)mapRecord.get("Ids");//主键格式：键：值,键：值...  AND关系
		String where = getSQLWhere(Ids);
		
		
		String judgeCode = (String)mapRecord.get("judge");
		String imgnum = "imgnum"+ judgeCode;
		
		String total = pd.getString("total");//总记录数
		int totalNum = StringTool.stringToInt(total);
		
		//删除硬盘上的图片
		String folderId = "";//要写入图片的的目录
		folderId = Const.getBridgeImagesPath() + Const.separator;//原来的代码
		
		
		String img = pd.getString("img");//tqiaomianjudge-7-1-2017-08-28-401011-2.jpg 图片名字中“.jpg”前面的内容为img
		
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
				
				sqlT = "UPDATE " + tableName +" SET "+ imgnum + "='" + (totalNum-1) +"' WHERE " + where; 
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
		
	@RequestMapping(value="image_info_change",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_change()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		
		//图片所在目录
		String folderId = "";//要写入图片的的目录
		folderId = Const.getBridgeImagesPath() + Const.separator;//原来的代码

		String selecedImg = pd.getString("selecedImg");//新首页图
	
		String code = "2";
		String message = "首页图片变更成功!";
		
		String firstImg = "";
		int orderNum = 0;
		int index = selecedImg.lastIndexOf("-");
		if(index != -1)
		{//获取图片序号，即tqiaomianjudge-7-1-2017-08-28-401011-2.jpg中"-2"中的数字"2"
			String imgOrder = selecedImg.substring(index+1,selecedImg.length());//修改为length
			orderNum = StringTool.stringToInt(imgOrder);
		}
		else {
			code = "-1";
			message = "无法进行首页图片变更!";
			return message(code,message); 
		}

		if(orderNum == 1){
			code = "-1";
			message = "无需进行首页图片变更!";
			return message(code,message); 
		}
		
		boolean result = false;
		if(! StringTool.isNullEmpty(selecedImg))
		{
			firstImg = selecedImg.substring(0,index+1) + "1";
			//先对两条记录的主键进行交互
			ArrayList<String> sqlArray = new ArrayList<String>();
			String sqlT = "UPDATE timageinfo SET id='-1111' WHERE id='" + firstImg + "'";
			sqlArray.add(sqlT);
			
			sqlT = "UPDATE timageinfo as t1,timageinfo as t2"
				  +" SET t1.id='" + firstImg +"',t2.id='" + selecedImg +"'"
					+ " WHERE t1.id='" + selecedImg +"' AND t2.id='-1111'";
			sqlArray.add(sqlT);
			
			result = systemService.batchMultiTable(sqlArray); 
		
			if(!result) {
				code = "-1";
				message = "首页图片变更失败!";
				return message(code,message); 
			}
			String temp = "a01a";
			//renameFile(String path,String oldName,String newName)
			result = FileTool.renameFile(folderId,firstImg+".jpg",temp);//修改首图的文件名为一临时名称
			if(result){//各种情况的处理不完整 
				result = FileTool.renameFile(folderId,selecedImg+".jpg",firstImg+".jpg");
				if(result)//成功变更名称
					result = FileTool.renameFile(folderId,temp,selecedImg+".jpg");
				else
					FileTool.renameFile(folderId,temp,firstImg+".jpg");
			}
		}
		if (!result){
			code = "-1";
			message = "首页图片变更失败!";
		}
		return message(code,message); 
	} 
	


	




	
	
	
	
	
	
	
	
	
	
	
	
	

	//获取涵洞的图片信息
	@RequestMapping(value="/bridgeService_check_imgsBrowser2", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object bridgeService_check_imgsBrowser2() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String tableName = pd.getString("TableName");
		String where = pd.getString("Where");
		String item = "imgnum,imgext"; 

		Map<String, Object> map = new HashMap<String, Object>();
		map = systemService.queryOne4Items(tableName,item,where); 

		String code ="";
		String message ="";
		if(!StringTool.isNull(map))
			for (Entry<String, Object> entry: map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if(StringTool.isNullEmpty(key) ||StringTool.isNull(value) )
					continue;
				if(key.equals("imgnum"))
					code = StringTool.intToString((Integer)value);
				else if(key.equals("imgext"))
					message = (String)value;
			}

		return message(code,message);
	}

	@RequestMapping(value="uploadAction4",method=RequestMethod.POST,produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object uploadFile4(HttpServletRequest request)
	{    
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

		PageTool pd = new PageTool();
		pd = this.getPageData(multiRequest);		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}
		
		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}
		
		/*使用updateInsert的原因是： 图片和基本信息可能不同时输入*/  
		String tableName = (String)mapRecord.get("tableName");
		String path = (String)mapRecord.get("path");//文件名
		String Ids = (String)mapRecord.get("Ids");//主键格式：键：值,键：值...  AND关系
		
		//过去已上传的文件数量
		String numStr = pd.getString("num");
		int num = StringTool.stringToInt(numStr);
		if(num < 0 ) num = 0;
		
		//本次上传的文件数量和次序信息
		String fileIndex = pd.getString("fileIndex");//上传第几个文件。从1开始编号
		int index = StringTool.stringToInt(fileIndex);
		if(index != -1)
			index = index + num;
		else
			index = num;
		
		//组装图片的存放位置
		String folderId = Const.getQiaohanImagesPath() + Const.separator;//要写入的目录
		String imgPre = path + "-";//(表名-桥号-桥号-孔跨号:由客户端组装)-序号
		
		String ext = "";//扩展名
		String fullFileName = "";
		boolean flag = false;
		Iterator<String> iter = multiRequest.getFileNames();
		while (iter.hasNext()) {
			MultipartFile file = multiRequest.getFile(iter.next());// 取得上传文件
			if ( StringTool.isNull(file))
				continue;
			fullFileName = file.getOriginalFilename(); //获取原文件名 含扩展名

			if(! StringTool.isNullEmpty(fullFileName) && fullFileName.lastIndexOf(".")>-1){  
				ext = fullFileName.substring(fullFileName.lastIndexOf("."));

				if(!StringTool.isNullEmpty(ext))
					ext = ext.toLowerCase();
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
		
		int result = 0;
		//不同表关键字可能不同，由 Ids的内容决定
		if(!StringTool.isNull(Ids) && flag){
			String[] idArry = StringTool.stringToArray(Ids,",");
			mapRecord = new HashMap<String,Object>();

			if(idArry != null && idArry.length > 0 ){
				for(int i = 0; i< idArry.length; i++){
					String[] keyvalue = StringTool.stringToArray(idArry[i],":"); 
					if(keyvalue != null && (keyvalue.length%2 == 0))//只有两个元素
						mapRecord.put(keyvalue[0].trim(),keyvalue[1]);
				}
			}

			mapRecord.put("imgnum",index);
			mapRecord.put("imgext",ext);
			
			result = systemService.updateInsert(tableName,mapRecord);
		}
		if(result <= 0)
			return "但保存记录失败，请重传图片！";
		else
			return "且正确写入服务器。";
	} 
	
	@RequestMapping(value="uploadAction42",method=RequestMethod.POST,produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object uploadFile42(HttpServletRequest request)
	{    
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

		PageTool pd = new PageTool();
		pd = this.getPageData(multiRequest);		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}
		
		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}
		
		/*使用updateInsert的原因是： 图片和基本信息可能不同时输入*/  
		String tableName = (String)mapRecord.get("tableName");
		String path = (String)mapRecord.get("path");//文件名
		String Ids = (String)mapRecord.get("Ids");//主键格式：键：值,键：值...  AND关系
		
		//过去已上传的文件数量
		String numStr = pd.getString("num");
		int num = StringTool.stringToInt(numStr);
		if(num < 0 ) num = 0;
		
		//本次上传的文件数量和次序信息
		String fileIndex = pd.getString("fileIndex");//上传第几个文件。从1开始编号
		int index = StringTool.stringToInt(fileIndex);
		if(index != -1)
			index = index + num;
		else
			index = num;
		
		//组装图片的存放位置
		String folderId = Const.getQiaohanImagesPath() + Const.separator;//要写入的目录
		String imgPre = path + "-";//(表名-桥号-桥号-孔跨号:由客户端组装)-序号
		
		String ext = "";//“.”+扩展名 
		String fullFileName = "";
		boolean flag = false;
		Iterator<String> iter = multiRequest.getFileNames();
		while (iter.hasNext()) {
			MultipartFile file = multiRequest.getFile(iter.next());// 取得上传文件
			if ( StringTool.isNull(file))
				continue;
			fullFileName = file.getOriginalFilename(); //获取原文件名 含扩展名

			if(! StringTool.isNullEmpty(fullFileName) && fullFileName.lastIndexOf(".")>-1){  
				ext = fullFileName.substring(fullFileName.lastIndexOf("."));

				if(!StringTool.isNullEmpty(ext))
					ext = ext.toLowerCase();
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
		
		int result = 0;
		//不同表关键字可能不同，由 Ids的内容决定
		if(!StringTool.isNull(Ids) && flag){
			String[] idArry = StringTool.stringToArray(Ids,",");
			mapRecord = new HashMap<String,Object>();

			if(idArry != null && idArry.length > 0 ){
				for(int i = 0; i< idArry.length; i++){
					String[] keyvalue = StringTool.stringToArray(idArry[i],":"); 
					if(keyvalue != null && (keyvalue.length%2 == 0))//只有两个元素
						mapRecord.put(keyvalue[0].trim(),keyvalue[1]);
				}
			}

			mapRecord.put("tuzhinum",index);
			mapRecord.put("tuzhiext",ext);
			
			result = systemService.updateInsert(tableName,mapRecord);
		}
		if(result <= 0)
			return "但保存记录失败，请重传图片！";
		else
			return "且正确写入服务器。";
	} 
	
	@RequestMapping(value="image_info_delete4",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_delete4()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}

		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}

		String tableName = (String)mapRecord.get("tableName");
		//String path = (String)mapRecord.get("path");//图在硬盘上的文件名部分

		String Ids = (String)mapRecord.get("Ids");//主键格式：键：值,键：值...  AND关系
		String where = getSQLWhere(Ids);
		
		String total = pd.getString("total");//总记录数
		int totalNum =  StringTool.stringToInt(total);
		
		//删除硬盘上的图片
		String folderId = Const.getQiaohanImagesPath() + Const.separator;//要写入的目录
		String img = pd.getString("img");//tqiaomianjudge-7-1-2017-08-28-401011-2.jpg 图片名字
		String imgOrder = "";
		int orderNum = 0;
		int result = 0;
		if(!StringTool.isNullEmpty(img)){
			int index = img.lastIndexOf("-");
			if(index != -1){
				imgOrder = img.substring(index+1,index+2);
				orderNum = StringTool.stringToInt(imgOrder);
				if(orderNum != -1){
					FileTool.delFile(folderId,img);
					//修改删除文件后硬盘上的文件序号，确保所有文件的序号是连续的。
					if(orderNum != totalNum){//删除的不是最后一个图片,将最后一个图片的序号修改为修好orderNum
						//int lastIndex = img.lastIndexOf(".jpg");//目前只支持此格式文件的上传
						String oldName = img.substring(0,index+1)//到最后一个-;
								        + totalNum + ".jpg";
						FileTool.renameFile(folderId,oldName,img);	
					}
						
					if(totalNum > 0){//最多一条记录
						Map<String, Object> oMap = new HashMap<String, Object>();
						oMap.put("imgnum", totalNum-1);
						result = systemService.update(tableName,oMap,where);
					}
				}
			}
		}
		
		String code = "2";
		String message = "成功删除!";
		if (result <= 0){//不成功 对于update： = 0页表示成功
			code = "-1";
			message = "删除失败!";
		}
		return message(code,message); 
	} 

	@RequestMapping(value="image_info_delete42",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_delete42()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}

		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}

		String tableName = (String)mapRecord.get("tableName");
		//String path = (String)mapRecord.get("path");//图在硬盘上的文件名部分

		String Ids = (String)mapRecord.get("Ids");//主键格式：键：值,键：值...  AND关系
		String where = getSQLWhere(Ids);
		
		String total = pd.getString("total");//总记录数
		int totalNum =  StringTool.stringToInt(total);
		
		//删除硬盘上的图片
		String folderId = Const.getQiaohanImagesPath() + Const.separator;//要写入的目录
		String img = pd.getString("img");//tqiaomianjudge-7-1-2017-08-28-401011-2.jpg 图片名字
		String imgOrder = "";
		int orderNum = 0;
		int result = 0;
		if(!StringTool.isNullEmpty(img)){
			int index = img.lastIndexOf("-");
			if(index != -1){
				imgOrder = img.substring(index+1,index+2);
				orderNum = StringTool.stringToInt(imgOrder);
				if(orderNum != -1){
					FileTool.delFile(folderId,img);
					//修改删除文件后硬盘上的文件序号，确保所有文件的序号是连续的。
					if(orderNum != totalNum){//删除的不是最后一个图片,将最后一个图片的序号修改为修好orderNum
						//int lastIndex = img.lastIndexOf(".jpg");//目前只支持此格式文件的上传
						String oldName = img.substring(0,index+1)//到最后一个-;
								        + totalNum + ".jpg";
						FileTool.renameFile(folderId,oldName,img);	
					}
						
					if(totalNum > 0){//最多一条记录
						Map<String, Object> oMap = new HashMap<String, Object>();
						oMap.put("tuzhinum", totalNum-1);
						result = systemService.update(tableName,oMap,where);
					}
				}
			}
		}
		
		String code = "2";
		String message = "成功删除!";
		if (result <= 0){//不成功 对于update： = 0页表示成功
			code = "-1";
			message = "删除失败!";
		}
		return message(code,message); 
	} 
	
	@RequestMapping(value="image_info_change4",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_change4()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		
		//图片所在目录
		String folderId = Const.getQiaohanImagesPath() + Const.separator;//要写入的目录
		String selecedImg = pd.getString("selecedImg");//新首页图
		String firstImg = pd.getString("firstImg");//原来的首页图
	
	
		boolean result = false;
		if(! StringTool.isNullEmpty(firstImg) && 
		   ! StringTool.isNullEmpty(selecedImg) &&
		   ! firstImg.equalsIgnoreCase(selecedImg)){
			String temp = "a01a";
			result = FileTool.renameFile(folderId,firstImg,temp);//修改首图的文件名为一临时名称
			if(result){//各种情况的处理不完整
				result = FileTool.renameFile(folderId,selecedImg,firstImg);
				if(result)
					result = FileTool.renameFile(folderId,temp,selecedImg);
				else
					FileTool.renameFile(folderId,temp,firstImg);
			}
		}
		
		String code = "2";
		String message = "首页图片变更成功!";
		if (!result){
			code = "-1";
			message = "首页图片变更失败!";
		}
		return message(code,message); 
	} 
	
	@RequestMapping(value="image_info_init42",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_init42()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}

		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}

		String tableName = (String)mapRecord.get("tableName");
		String path = (String)mapRecord.get("path");//图在硬盘上的文件名部分
		String Ids = (String)mapRecord.get("Ids");//主键格式：键：值,键：值...  AND关系
		String where = getSQLWhere(Ids);
		String items = "tuzhinum,tuzhiext";
				
		//最多一条记录
		Map<String, Object> oMap = new HashMap<String, Object>();
		oMap = systemService.queryOne4Items(tableName,items,where);
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		if(!StringTool.isNull(oMap))
		{
			String img = "";
			String ext = (String)oMap.get("tuzhiext");
			Object numStr = oMap.get("tuzhinum");
			int num = 0;
			if (StringTool.isNull(numStr))
				num = 0;
			else{
			   num = (Integer)numStr;
			   if(num < 0 ) num = 0;
			}
			for(int i = 1; i <= num ; i++ ){
				Map<String, Object> mapRecordT = new HashMap<String, Object>();
				mapRecordT.put("tuzhinum", i);
				mapRecordT.put("tuzhiext", ext);
				img = path + "-" + i + ext;
				mapRecordT.put("img", img);
				mapRecordT.put("imgpath", img);
				
				lists.add(mapRecordT);
			}
		}

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		return rowList; 
	} 
	
	
	
	@RequestMapping(value="image_info_init4",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_init4()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}

		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}

		String tableName = (String)mapRecord.get("tableName");
		String path = (String)mapRecord.get("path");//图在硬盘上的文件名部分
		String Ids = (String)mapRecord.get("Ids");//主键格式：键：值,键：值...  AND关系
		String where = getSQLWhere(Ids);
		
		String items = "imgnum,imgext";
		//最多一条记录
		Map<String, Object> oMap = new HashMap<String, Object>();
		oMap = systemService.queryOne4Items(tableName,items,where);
		
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		if(!StringTool.isNull(oMap))
		{
			String img = "";
			String ext = (String)oMap.get("imgext");
			Object numStr = oMap.get("imgnum");
			int num = 0;
			if (StringTool.isNull(numStr))
				num = 0;
			else{
			   num = (Integer)numStr;
			   if(num < 0 ) num = 0;
			}
			for(int i = 1; i <= num ; i++ ){
				Map<String, Object> mapRecordT = new HashMap<String, Object>();
				mapRecordT.put("imgnum", i);
				mapRecordT.put("imgext", ext);
				
				img = path + "-" + i + ext;
				mapRecordT.put("img", img);
				mapRecordT.put("imgpath", img);
				
				lists.add(mapRecordT);
			}
		}

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		return rowList; 
	} 
	
	
	@RequestMapping(value="image_info_init",produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object image_info_init()
	{ 
		PageTool pd = new PageTool();
		pd = this.getPageData();		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}

		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}

		String tableName = (String)mapRecord.get("tableName");
		String qiaohao = (String)mapRecord.get("qiaohao");
		String kongkuahao = (String)mapRecord.get("kongkuahao");
		String pingdingdate = (String)mapRecord.get("pingdingdate");
		String judgeCode = (String)mapRecord.get("judge");

		String imgext = ""; 
		String imgnum = "";
		String items = "";
		if(!StringTool.isNullEmpty(judgeCode)){
			imgext = "imgext"+ judgeCode;
			imgnum = "imgnum"+ judgeCode;
			items = imgext + "," + imgnum;
		}
		String where = "qiaohao='" + qiaohao 
				+ "' AND pingdingdate='" + pingdingdate 
				+ "' AND kongkuahao='" + kongkuahao 
				+ "'" ; 
	
		if( "thandongjudge".equalsIgnoreCase(tableName)	)//对于桥渡和涵洞来讲，没有孔跨号
			where = "qiaohao='" + qiaohao + "' AND pingdingdate='" + pingdingdate	+ "'" ;
	
		//最多一条记录
		Map<String, Object> oMap = new HashMap<String, Object>();
		oMap = systemService.queryOne4Items(tableName,items,where);
      
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		if(!StringTool.isNull(oMap))
		{
			//组装图片的存放位置
			String imgPre = "";//表名-桥号-桥号-孔跨号-评定时间-劣化评定项代码-序号
			imgPre = tableName + "-" + qiaohao;
			if(! StringTool.isNullEmpty(kongkuahao))//对于桥渡和涵洞来讲，没有孔跨号
				imgPre = imgPre + "-" + kongkuahao;
			
			imgPre = imgPre + "-" +  pingdingdate  + "-" + judgeCode + "-";
			//imgPre = "../../qiaohanImgs/" + imgPre;

			String img = "";
			String ext = (String)oMap.get(imgext);
			Object numStr = oMap.get(imgnum);
			int num = 0;
			if (StringTool.isNull(numStr))
				num = 0;
			else{
			   num = (Integer)numStr;
			   if(num < 0 ) num = 0;
			}
			for(int i = 1; i <= num ; i++ ){
				Map<String, Object> mapRecordT = new HashMap<String, Object>();
				mapRecordT.put("imgnum", i);
				mapRecordT.put("imgext", ext);
				img = imgPre + i + ext;
				mapRecordT.put("img", img);
				mapRecordT.put("imgpath", img);
				
				lists.add(mapRecordT);
			}
		}

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		return rowList; 
	} 

	@RequestMapping(value="uploadAction2",method=RequestMethod.POST,produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object /*void*/ uploadFile2(HttpServletRequest request)
	{ //所谓一次传多个文件，实质上仍然是一次上传一个文件。
		//转换成多部分request    
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

		PageTool pd = new PageTool();
		pd = this.getPageData(multiRequest);		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}
		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}

		String fileIndex = pd.getString("fileIndex");//上传第几个文件。从1开始编号
		String fileNum = pd.getString("fileNum");//要上传的文件总数

		/*使用updateInsert的原因是： 图片和基本信息可能不同时输入*/  
		String tableName = (String)mapRecord.get("tableName");
		String path = (String)mapRecord.get("path");//文件名
		String Ids = (String)mapRecord.get("Ids");//主键格式：键：值,键：值...  AND关系

		String fileFullPath = path + "-";

		String folderId = Const.getQiaohanImagesPath() + Const.separator;//要写入的目录
		String fileNameExt;
		String imgext = "";

		boolean flag = false;

		Iterator<String> iter = multiRequest.getFileNames();
		while (iter.hasNext()) {
			MultipartFile file = multiRequest.getFile(iter.next());// 取得上传文件
			if ( StringTool.isNull(file))
				continue;

			fileNameExt = file.getOriginalFilename(); //获取原文件名 含扩展名

			if(! StringTool.isNullEmpty(fileNameExt) && fileNameExt.lastIndexOf(".")>-1){  
				imgext = fileNameExt.substring(fileNameExt.lastIndexOf("."));

				if(!StringTool.isNullEmpty(imgext))
					imgext = imgext.toLowerCase();
			}
			else
				continue;

			
			if(imgext.equalsIgnoreCase(".bmp"))
				new ImageFormatConversion().Conversion("bmp","jpg",(fileFullPath + fileIndex));
			else if(imgext.equalsIgnoreCase(".png"))
				new ImageFormatConversion().Conversion("png","jpg",(fileFullPath + fileIndex));
			if(imgext.equalsIgnoreCase(".gif"))
				new ImageFormatConversion().Conversion("gif","jpg",(fileFullPath + fileIndex));
			imgext = ".jpg";
			
			fileNameExt = fileFullPath + fileIndex + imgext;  
			flag = FileTool.copy(file,(folderId + fileNameExt),true);//返回true写成功（写入硬盘）
		}
		int result = 0;

		//代表取整运算5/2=2； %只能用于整数   取余运算 a%b的余数是多少，如1%2，余数为1
		String[] idArry = StringTool.stringToArray(Ids,",");

		mapRecord = new HashMap<String,Object>();
		if (flag){
			if(idArry != null && idArry.length > 0 ){
				for(int i = 0; i< idArry.length; i++){
					String[] keyvalue = StringTool.stringToArray(idArry[i],":"); 
					if(keyvalue != null && (keyvalue.length%2 == 0))//只有两个元素
						mapRecord.put(keyvalue[0].trim(),keyvalue[1]);
				}
			}

			mapRecord.put("imgnum",fileNum);
			mapRecord.put("imgext",imgext);
			result = systemService.updateInsert(tableName,mapRecord);
		}

		if(result <= 0)
			return "但保存记录失败，请重传图片！";
		else
			return "且正确写入服务器。";
	} 
	
	//上边的是桥涵的基本图像操作。下边的是劣化评价的基本操作

	// 处理文件上传
	@RequestMapping(value="uploadAction",method=RequestMethod.POST,produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object /*void*/ uploadFile(HttpServletRequest request)
	{ //所谓一次传多个文件，实质上仍然是一次上传一个文件。
		//转换成多部分request    
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

		PageTool pd = new PageTool();
		pd = this.getPageData(multiRequest);		
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();//{JsonObjParam={名：值，。。。}}
		//读取JSON字符串
		String jsonObj = pd.getString("JsonObjParam");//{名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);//{名=值,。。。}

		String fileIndex = pd.getString("fileIndex");//上传第几个文件。从1开始编号
		String fileNum = pd.getString("fileNum");//要上传的文件总数
		String opType = pd.getString("opType");//此参数可以决定是否对已上传的图片进行删除

		/*使用updateInsert的原因是： 图片和基本信息可能不同时输入*/  
		String tableName = (String)mapRecord.get("tableName");
		String qiaohao = (String)mapRecord.get("qiaohao");
		String pingdingdate = (String)mapRecord.get("pingdingdate");
		String judge = (String)mapRecord.get("judge");
		String kongkuahao = (String)mapRecord.get("kongkuahao");

		String fileFullPath = tableName + "-" + qiaohao;

		/*if(  !qiaodujudgeTable.equals(tableName)//对于桥渡来讲，没有孔跨号
		  && !handongjudgeTable.equals(tableName) )//对于涵洞来讲，没有孔跨号
			fileFullPath = fileFullPath + "-" + kongkuahao;*/
		if(! StringTool.isNullEmpty(kongkuahao))
			fileFullPath = fileFullPath + "-" + kongkuahao;

		fileFullPath = fileFullPath + "-" +  pingdingdate  + "-" + judge + "-";

		String imgnumFieldName = "imgnum" + judge;
		String imgextFieldName = "imgext" + judge;

		String folderId = Const.getBridgeImagesPath() + Const.separator;//要写入的目录
		String fileNameExt;
		String imgext = "";

		//先删除过去上传的图片
		/*if ("edit".equals(opType)){
			String item = imgnumFieldName + "," + imgextFieldName;
			String where = "qiaohao='" + qiaohao + "' AND pingdingdate='" + pingdingdate  + "'";

			//if( ! qiaodujudgeTable.equals(tableName)//对于桥渡来讲，没有孔跨号
			 //&& !handongjudgeTable.equals(tableName) )//对于涵洞来讲，没有孔跨号
				//where = where + " AND kongkuahao='" + kongkuahao + "'";
			if(! StringTool.isNullEmpty(kongkuahao))
				where = where + " AND kongkuahao='" + kongkuahao + "'";

			Map<String, Object> map = new HashMap<String, Object>();
			map = systemService.queryOne4Items(tableName,item,where); 
			int code = 0;
			if(!StringTool.isNull(map))
				for (Entry<String, Object> entry: map.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if(StringTool.isNullEmpty(key) ||StringTool.isNull(value) )
						continue;
					if(key.equals("imgnum"+ judge))
						code = (Integer)value;
					else if(key.equals("imgext"+ judge))
						imgext = (String)value;
				}
			for(int i = 1; i<= code; i++ ){
				fileNameExt = fileFullPath + i + imgext;  
				FileTool.delFile(folderId,fileNameExt);
			}
		}*/

		boolean flag = false;
		// 取得request中的所有文件名
		/*方法一
			Map<String, MultipartFile> fileMap = multiRequest.getFileMap(); 
			 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()){
				 MultipartFile file = entity.getValue();  */
		//方法二
		Iterator<String> iter = multiRequest.getFileNames();
		while (iter.hasNext()) {
			MultipartFile file = multiRequest.getFile(iter.next());// 取得上传文件
			if ( StringTool.isNull(file))
				continue;

			fileNameExt = file.getOriginalFilename(); //获取原文件名 含扩展名

			if(! StringTool.isNullEmpty(fileNameExt)  && fileNameExt.lastIndexOf(".")>-1){  
				imgext = fileNameExt.substring(fileNameExt.lastIndexOf("."));

				if(!StringTool.isNullEmpty(imgext))
					imgext = imgext.toLowerCase();
			}
			else
				continue;

			if(imgext.equalsIgnoreCase(".bmp"))
				new ImageFormatConversion().Conversion("bmp","jpg",(fileFullPath + fileIndex));
			else if(imgext.equalsIgnoreCase(".png"))
				new ImageFormatConversion().Conversion("png","jpg",(fileFullPath + fileIndex));
			if(imgext.equalsIgnoreCase(".gif"))
				new ImageFormatConversion().Conversion("gif","jpg",(fileFullPath + fileIndex));
			imgext = ".jpg";
			
			fileNameExt = fileFullPath + fileIndex + imgext;  

			//System.out.println(folderId + fileNameExt);
			flag = FileTool.copy(file,(folderId + fileNameExt),true);//返回true写成功（写入硬盘）
		}
		int result = 0;
		if (flag){
			mapRecord.put(imgnumFieldName,fileNum);
			mapRecord.put(imgextFieldName,imgext);
			//System.out.println(mapRecord.toString());
			result = systemService.updateInsert(tableName,mapRecord);
		}

		if(result <= 0)
			return "但保存记录失败，请重传图片！";
		else
			return "且正确写入服务器。";
	} 

	//获取劣化评估的照片信息：
	@RequestMapping(value="/bridgeService_check_imgsBrowser", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object bridgeService_check_imgsBrowser() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String qiaohao = pd.getString("Qiaohao");
		String pingdingdate = pd.getString("Pingdingdate");
		String judge = pd.getString("Judge");
		String tableName = pd.getString("tableName");

		String kongkuahao = pd.getString("Kongkuahao");

		String item = "imgnum"+ judge +",imgext"+judge; 
		String where = "qiaohao='" + qiaohao + "' AND pingdingdate='" + pingdingdate  + "'";
		//对于桥渡来讲，没有孔跨号
		/*if( ! qiaodujudgeTable.equals(tableName)//对于桥渡来讲，没有孔跨号
		 && !handongjudgeTable.equals(tableName) )//对于涵洞来讲，没有孔跨号
		    where = where + " AND kongkuahao='" + kongkuahao + "'";*/
		if(! StringTool.isNullEmpty(kongkuahao))
			where = where + " AND kongkuahao='" + kongkuahao + "'";

		Map<String, Object> map = new HashMap<String, Object>();
		map = systemService.queryOne4Items(tableName,item,where); 
		String code ="";
		String message ="";
		if(!StringTool.isNull(map))
			for (Entry<String, Object> entry: map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if(StringTool.isNullEmpty(key) ||StringTool.isNull(value) )
					continue;
				if(key.equals("imgnum"+ judge))
					code = StringTool.intToString((Integer)value);
				else if(key.equals("imgext"+ judge))
					message = (String)value;
			}

		return message(code,message);
	}


	//操作权限检查
	@RequestMapping(value="/authority_check")  
	@ResponseBody
	public Object authority_check() 
	{  	
		PageTool pd = new PageTool();
		pd = this.getPageData();
		Subject currentUser = SecurityUtils.getSubject();  
		Session session = currentUser.getSession();

		/** 操作对象，比如要添加劣化检查的桥梁劣化等级评价记录表(总)中一条记录， 以及操作人所属工队编码  */
		//前端：要操作的模块（本系统即桥梁和涵洞）所属工队编码
		String gongduiCodeFront = pd.getString("shuoshugongdui");
		//后端：登录用户所属的工队编号. userinfo表中默认工队编号为：10
		String gongduiCodeBack = (String)session.getAttribute(Const.SESSION_UNITID);
		/* 用户名 暂不处理。
		String userName	= pd.getString("userName");
		session.getAttribute(Const.SESSION_USERNAME);
		 //查询此人所述单位
		 String unitcode = "0";
		 Map<String, Object> unitMap = new HashMap<String, Object>();
		 String where = "id='" + username + "'";
		 unitMap = systemService.queryOne4Items("userinfo","unitid", where);//最多一条记录
		 if(! StringTool.isNull(unitMap))
		 unitcode = (String)unitMap.get("unitid");
		 */

		/**权限表 
		 * 前两位表示对应模块moduleiId：10：工队 ; 90总部；
		 * 后四位表示增 删 改 查authOp：1表示可以进行对应的操作，0表示否
		 * 比如：session.getAttribute(Const.SESSION_USERAUTH)获取的值为 901111
		 * 则前两位moduleiId： 90； 后四位authOp为：1111
		 * 
		 * 目前支持的类别：
		 * ('901111', '增删改查'); ('900001', '查(全部)'); 
		 * ('101111', '增删改查'); ('100001', '查(工队)');
		 */
		/* 目前不用
		String moduleiId = pd.getString("moduleiId");//
		String authOp = pd.getString("authOp");
		 */

		//页面传来的操作类型 。 
		String opType = pd.getString("opType");//add1、edit2、del3、browser4、upload5、download6

		/**角色表 
		 * 角色编码共四位: 前两位表示单位：10：工队 ; 90总部；后两位表示角色：10管理员：90领导
		 * 
		 * 目前支持的类别：
		 * ('9010', '超级管理员'); ('9090', '领导');
		 * ('1010', '工队管理员'); ('1090', '工队领导'); 
		 */
		//由后台根据用户登录时系统自动获得的角色信息获取。
		String fullRoleCode = (String)session.getAttribute(Const.SESSION_USERROLECODE);//9010
		/*暂时不用
		String unitCode = "";//90
		String roleCode = "";//10
		if( ! StringTool.isNullEmpty(fullRoleCode) && fullRoleCode.length() == 4){
			unitCode = fullRoleCode.substring(0,2);
			roleCode = fullRoleCode.substring(2);
		}*/

		//操作编码： add1、edit2、del3、browser4、upload5、download6； 成功正整数；失败发对应的负整数
		String code = "-7";//小于0，无权或不成功； >0:操作成功
		String message = "您无权进行此操作!";
		if ( StringTool.isNullEmpty(fullRoleCode) ){
			code = "-10";
			message =  "权限分配有错!";
		}
		else if (fullRoleCode.equals("9090") ){//总部：超级领导。 可以查看所有信息
			if(opType.equals("browser"))//browser
				code = "4";
			//else  //add、edit、del
			//code = "-1";
		}
		else if (fullRoleCode.equals("9010")){//总部：超级管理员。可以对所有记录进行所有操作
			code = fullRoleCode;	  
		}
		else if (fullRoleCode.equals("1090")){//工队:领导.只能对所属工队管理的桥梁进行查看
			if( StringTool.isNullEmpty(gongduiCodeBack) || StringTool.isNullEmpty(gongduiCodeFront) ){
				code = "-9";
				message =  "您无权对此模块进行操作!";
			}
			else if( ! gongduiCodeFront.equalsIgnoreCase(gongduiCodeBack) ){
				code = "-8";
				message = "您无权对此记录进行此操作!";
			}
			else if(opType.equals("browser"))//browser
				code = "4";
			//else  //add、edit、del
			//code = "-1";
		}
		else if (fullRoleCode.equals("1010")){//1010 工队 管理员
			if( StringTool.isNullEmpty(gongduiCodeBack) || StringTool.isNullEmpty(gongduiCodeFront) ){
				code = "-9";
				message =  "您无权对此模块进行操作!";
			}
			else if( ! gongduiCodeFront.equalsIgnoreCase(gongduiCodeBack) ){
				code = "-8";
				message = "您无权对此记录进行此操作!";
			}
			else 
				code = "1010";
		}
		return message(code,message);
	}
	//|| StringTool.isNullEmpty(moduleiId) 
	//|| StringTool.isNullEmpty(authOp) 
	//|| StringTool.isNullEmpty(unitCode) 
	//|| StringTool.isNullEmpty(roleCode)


	//判断指定字段的值是否存在
	@RequestMapping(value="/fieldExist")  
	@ResponseBody
	public ModelAndView fieldExist() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String tablename = pd.getString("tablename");				
		String fieldname = pd.getString("fieldname");
		String fieldvalue = pd.getString("fieldvalue");

		Map<String,String> map = new HashMap<String,String>();
		map.put("Status", "false");

		if( ! StringUtils.isEmpty(tablename) && 
				! StringUtils.isEmpty(fieldname) &&
				! StringUtils.isEmpty(fieldvalue) )
		{
			if (systemService.queryOne(tablename, fieldname, fieldvalue).size() > 0)
				map.put("Status", "true");
		}

		return new ModelAndView(new MappingJackson2JsonView(),map);
	}













	//获取火车站站名
	@RequestMapping(value="/drop_select_station", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object drop_select_station() 
	{
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query("tstation","");
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		return rowList;
	}

	//获取单位下拉框的内容
	@RequestMapping(value="/drop_select_unitid", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object drop_select_unitid() 
	{  
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String unittype = pd.getString("unittype");

		String where = "danweileibie='" + unittype +"'";
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query("danwei","id,name", where);
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		return rowList;
	}

	//获取角色下拉框中的值
	@RequestMapping(value="/drop_select_role", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object drop_select_role() 
	{
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query("role", "");
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);
		return rowList;
	}
	//获取权限下拉框中的值
	@RequestMapping(value="/drop_select_permission", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object drop_select_permission() 
	{
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query("permission","");
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		return rowList;
	}


	//主梁图号
	@RequestMapping(value="/drop_select_figurenumber", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object drop_select_figurenumber() 
	{
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query("tfigurenumber","");
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);
		return rowList;
	}

	//获取下拉框中的值
	@RequestMapping(value="/drop_select_qiaodunleixingcailiao", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object drop_select_qiaotaileixingcailiao() 
	{
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query("tqiaoduncailiao","");
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);
		return rowList;
	}

	//获取下拉框中的值
	@RequestMapping(value="/drop_select_qiaodunjichuleixingcailiao", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object drop_select_qiaodunjichuleixingcailiao() 
	{
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists = systemService.query("tqiaodunjichucailiao","");
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);
		return rowList;
	}












	//获取下拉框中的值
	@RequestMapping(value="/drop_select_qiaotaituhao", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object drop_select_qiaotaituhao() 
	{
		List<Object> lists = new ArrayList<Object>();
		//lists = systemService.query("tqiaotaituhao","");
		//lists = systemService.query(tableName,where, model,true);
		//lists = systemService.query("tqiaotaituhao","");
		/*List<role> list = new ArrayList<role>();
		list = ConvertTool.castList(lists,role.class);*/
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);

		return rowList;
	}


	//获取下拉框中的值
	@RequestMapping(value="/drop_select_qiaotaijichuleixingcailiao", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object drop_select_tqiaotaijichucailiao() 
	{
		List<Object> lists = new ArrayList<Object>();
		//lists = systemService.query("tqiaotaijichucailiao","");
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);
		return rowList;
	}
	//获取下拉框中的值
	@RequestMapping(value="/drop_select_qiaotaijichuleixinggouzao", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object drop_select_qiaotaijichuleixinggouzao() 
	{
		List<Object> lists = new ArrayList<Object>();
		//lists = systemService.query("tqiaotaijichugouzao","");
		String rowList = ConvertTool.list2json((ArrayList<?>)lists);
		return rowList;
	}
}