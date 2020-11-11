package org.blkj.text;

import java.io.OutputStream;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;  
import org.apache.poi.xssf.streaming.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.web.servlet.view.AbstractView;

import blkjweb.utils.Console;

public class ExcelView extends AbstractView 
{
	private Row row;
	private Cell cell;
	private CellStyle head_Style;

	String excelName = "报表.xlsx";  //设置下载时客户端Excel的名称.xls   
	/*List<String> tableTitle;//英文表头
	public ExcelView ( List<String> item) {
		tableTitle = item;
	}*/
	
	Map<String, String> tableTitle;//中文表头
	public ExcelView (Map<String, String> item) {
		tableTitle = item;
	}
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,HttpServletRequest request, HttpServletResponse response)
				  throws Exception {
		SXSSFWorkbook workbook = new SXSSFWorkbook(); //创建新的Excel工作薄
		buildExcelDocument(model, workbook, request, response);
	}


	protected void buildExcelDocument(Map<String, Object> model, SXSSFWorkbook workbook,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		OutputStream ouputStream = null;
		try{
			/** 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开*/  
			response.reset();// 清空response  
			response.setContentType("APPLICATION/OCTET-STREAM");// 设置response的Header
			//response.setContentType("application/vnd.ms-excel");
			//response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));//乱码
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(excelName.getBytes("gbk"),"iso-8859-1"));
			ouputStream = response.getOutputStream();    

			//定义表头单元格格式
			this.head_Style = workbook.createCellStyle();
			head_Style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
			head_Style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			head_Style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			head_Style.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
			Font head_font = workbook.createFont();//先定义一个字体对象
			head_font.setFontName("黑体");// 设置头部字体为宋体宋体
			head_font.setFontHeightInPoints((short) 12);//字体大小
			head_font.setBold(true);//加粗
			this.head_Style.setFont(head_font);// 单元格样式使用字体
			
			//创建表头
			Sheet sheet =  workbook.createSheet("详细信息");
			for(int i = 0; i < tableTitle.size(); i++){//单元格宽度  
				sheet.setColumnWidth((short)i, 5000);//设置单元格宽度  
	        }  
			
			Row header = sheet.createRow(0); //第0行  
			
			//for(int i = 0; i < tableTitle.size(); i++){
			Iterator<Map.Entry<String, String>> entries = tableTitle.entrySet().iterator();//中文表头 
			int num = 0;
		    while (entries.hasNext()) {  
		        Map.Entry<String, String> entry = entries.next();  
		        cell = header.createCell(num++);
				cell.setCellValue(entry.getValue());//写入内容
				
				/*
		        cell = header.createCell(i);                     
				cell.setCellValue(tableTitle.get(i));//写入内容
				*/
				
		        cell.setCellType(XSSFCell.CELL_TYPE_STRING);//文本格式
				cell.setCellStyle(head_Style);
			}
		    
		    //从ModelAndView传过来的数据dataList内容是：List<Object>，确切讲是：List<Map<String, Object>>
		    //对象转换方法一 
		    List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			//Type safety: Unchecked cast from Object to List<Map<String,Object>>
		    //dataList = (List<Map<String, Object>>)model.get("dataList");
		    //对象转换方法二  先安全地转换为：List<Map<String, Object>>
		    List<?> dataListT = (List<?>) model.get("dataList");
		    for (int i = 0; i < dataListT.size(); i++) {
				Map<?,?> map = (Map<?,?>)dataListT.get(i);
		    	Map<String, Object> tempMap = new HashMap<String, Object>();
				for (Map.Entry<?,?> entry : map.entrySet()) {//每次循环执行一次
			    	tempMap.put(entry.getKey().toString(),entry.getValue());
			    	//MyLogger.showMessage(entry.getValue());//具体取值
			    }
				dataList.add(tempMap);
		    }
		    
			if (dataList != null){//写入单元格内容
				for (int i = 0; i < dataList.size(); i++) {
					//cell.setCellStyle(cellStyleContents);
					Map<String, Object> obj = dataList.get(i);//获取第i行数据
					row = sheet.createRow(i + 1); //建立新的行
					
					//方法一 tableTitle 是 Map<String, String> //中文表头
					Iterator<Map.Entry<String, String>> entries2 = tableTitle.entrySet().iterator(); 
					int num2 = 0;
				    while (entries2.hasNext()) {  
				        Map.Entry<String, String> entry2 = entries2.next();  
				    	cell = row.createCell((short)(num2++)); //建立新的列
						String key = entry2.getKey();
						Object o = obj.get(key);
						String temp = "";
						if (o == null)
							temp = "";
						else if (o instanceof String) {
							temp = (String)o;
							if (temp == null || temp.equals("null"))
								temp = "";
						}
						else  {//int Date
							cell.setCellValue("" + obj.get(key));
							continue;
						}
						
						cell.setCellValue(temp);
				    }
					
				    //方法二 tableTitle 是 List<String> 
					/*for (int col = 0; col < tableTitle.size(); col++) {//写入每列的值
						cell = row.createCell((short)col); //建立新的列
						String key = tableTitle.get(col);
						//MyLogger.showMessage(key + "::"+ obj.get(key));
						Object o = obj.get(key);
						String temp = "";
						if (o == null)
							temp = "";
						else if (o instanceof String) {
							temp = (String)o;
							if (temp == null || temp.equals("null"))
								temp = "";
						}
						else  {//int Date
							cell.setCellValue("" + obj.get(key));
							continue;
						}
						cell.setCellValue(temp);
					}
					*/
				} 
			}
			workbook.write(ouputStream);
		}catch(Exception e){
			Console.showMessage(ExcelView.class.getSimpleName(),e.getMessage(), e);
		} finally{
			ouputStream.flush();       
			ouputStream.close(); 
		}  
	}
	

}