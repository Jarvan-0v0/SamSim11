package blkjweb.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.blkj.utils.ConvertTool;
import org.blkj.utils.PageTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import blkjweb.service.SystemServiceImp;
import blkjweb.utils.Console;
import blkjweb.utils.Const;
import org.blkj.utils.WordExcelTool;
import jodd.vtor.constraint.Length;
import java.util.Calendar;
import org.blkj.utils.ConvertTool;
/**
 * 
 * @author lab
 * @author 实现了利用模板写入数据生成文件后直接下载，无序临时保存文件
 */
/*
 * 使用时务必传入fileName,否则报错 模板路径 /page/templatefiles,已写入blkjweb.utils.Const
 */
@Controller
public class PrintCtrl extends AbstractBase {

	@Resource
	private SystemServiceImp systemService;

	
	
	@RequestMapping(value = "/editRecord")
	@ResponseBody 
	public Object editRecord()
	{
		 return message("2",""); 
	}
	@RequestMapping(value = "/deleteRecord")
	@ResponseBody 
	public Object deleteRecord()
	{
		String mess;
		 String code = "2";
				//if (result)
				{
				mess = "插入成功!";
				}
					{
					code="-1";
					mess="插入失败";
					}
			 return message(code,mess); 
	}
	@RequestMapping(value = "/insertRecord")
	@ResponseBody 
		public Object insertRecord()
	{
		
		PageTool pd = new PageTool();
		pd = this.getPageData();
		Map<String,Object> mapRecord = new HashMap<String,Object>();
		mapRecord = pd.getMap();
		
		String tableName =  pd.getString("TableName");
		String xiugairen=pd.getString("xiugairen");
		//Datetime date = new Date();//获得系统时间.
        String insertJson=  ConvertTool.object2json(mapRecord);
		//String nowTime = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss").format(date);
String sql ="INSERT INTO `shenhe` VALUES ('"+xiugairen+"', curtime, 'sds', '2018-12-18 11:01:53', '未审核', 'insert', '{}', '"+insertJson+"', 'URl', 'tgirder')";

 boolean   result=systemService.execute(sql);
 String mess;
 String code = "2";
		if (result)
		{
		mess = "插入成功!";
		} else 
			{
			code="-1";
			mess="插入失败";
			}
	 return message(code,mess);
		//int result = systemService.insert(tableName, mapRecord);
	}
	
	
	@RequestMapping(value = "/printPage_some")
	@ResponseBody // 作用是将返回的对象作为响应，发送给页面
	public void printPage_some() {
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String jsonObj = pd.getString("JsonObjParam");// {名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);

		// 获取表名 文件名(模板文件名)
		String tableName = mapRecord.get("tableName").toString();
		String fileName = mapRecord.get("fileName").toString() + ".docx";
		int length=tableName.length();
		String last4string=tableName.substring(length-4);
		//String last4string=tableName.substring(tableName.length()-4);
		Map<String, Object> mapRecord1 = new HashMap<String, Object>();
		if(last4string.equalsIgnoreCase("view"))
			mapRecord1 = systemService.queryViewOne(mapRecord, tableName.substring(0, tableName.length()-4));
		else
		     mapRecord1 = systemService.queryOne(mapRecord, tableName);
		//mapRecord = systemService.queryViewOne(mapRecord, tableName);
		mapRecord.putAll(mapRecord1);
		mapRecord = ConvertTool.changeNullSpace((ConvertTool.json2Map(ConvertTool.map2json(mapRecord))));

		// 模板文件路径
		String templatePath = Const.getTemplatePath() + Const.separator;

		// httpservlet response设置头
		HttpServletResponse response = this.getResponse();
		response.setContentType("text/html;charset=UTF-8");
		response.setContentType("application/octet-stream");
		response.setHeader("Content-disposition", "attachment; filename=" + ConvertTool.getISO(fileName));// 设置下载文件名
		// response output流
		OutputStream ops = null;
		XWPFDocument hdt = null;
		try {
			
			ops = response.getOutputStream();
			hdt = WordExcelTool.readwriteWord(templatePath + fileName, mapRecord);
			hdt.write(ops);
		} catch (Exception e) {
			Console.showMessage(AbstractBase.class.getSimpleName(), e.getMessage(), e);
		} finally {
			try {
				if (hdt != null)
					hdt.close();
				if (ops != null)
					ops.close();
			} catch (IOException e) {
				Console.showMessage(AbstractBase.class.getSimpleName(), e.getMessage(), e);
			}
		}
	}


	@RequestMapping(value = "/printPage_list")
	@ResponseBody // 作用是将返回的对象作为响应，发送给页面
	public void printPage_list() {
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String jsonObj = pd.getString("JsonObjParam");
		mapRecord = ConvertTool.json2Map(jsonObj);

		// 获取文件名和表名
		String tableName = mapRecord.get("tableName").toString();
		String fileName = mapRecord.get("fileName").toString() + ".xlsx";

		String where = "";// 查询限制，可传递部分主键用于生成where
		boolean first = true;
		if (mapRecord.containsKey("qiaohao")) {
			String qiaohao = mapRecord.get("qiaohao").toString();
			if (qiaohao != null && qiaohao != "") {
				where = "qiaohao" + " ='" + qiaohao + "'";
				first = false;
			}
		}

		String[] keys = systemService.getkeys(tableName);// 获取主键
		if (keys.length > 0) {
			for (int i = 0; i < keys.length; i++) {
				Object value = mapRecord.get(keys[i]);
				if (value != null && value != "" && keys[i] != "qiaohao") {
					if (!first) {
						where = where + " AND " + keys[i] + " ='" + value.toString() + "'";
					} else {
						where = keys[i] + " ='" + value.toString() + "'";
						first = false;
					}
				}
			}
		}

		List<Map<String, Object>> listMapRecord = new ArrayList<Map<String, Object>>();
		listMapRecord = systemService.query(tableName, where);// 查询所有记录

		// 模板文件路径
		String templatePath = Const.getTemplatePath() + Const.separator;

		// httpservlet response设置头
		HttpServletResponse response = this.getResponse();
		response.setContentType("text/html;charset=UTF-8");
		response.setContentType("application/octet-stream");
		response.setHeader("Content-disposition", "attachment; filename=" + ConvertTool.getISO(fileName));// 设置下载文件名
		// response output流
		OutputStream ops = null;
		XSSFWorkbook wb = null;

		try {
			ops = response.getOutputStream();
			wb = WordExcelTool.replaceModel(templatePath + fileName, listMapRecord);
			wb.write(ops);
		} catch (Exception e) {
			Console.showMessage(AbstractBase.class.getSimpleName(), e.getMessage(), e);
		} finally {
			try {
				if (wb != null)
					wb.close();
				if (ops != null)
					ops.close();
			} catch (IOException e) {
				Console.showMessage(AbstractBase.class.getSimpleName(), e.getMessage(), e);
			}
		}
	}
}