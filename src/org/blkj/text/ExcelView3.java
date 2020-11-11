package org.blkj.text;

import java.util.List;  
import java.util.Map;  

import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  

import org.apache.poi.hssf.usermodel.HSSFCellStyle;  
import org.apache.poi.hssf.usermodel.HSSFDataFormat;  
import org.apache.poi.hssf.usermodel.HSSFRow;  
import org.apache.poi.hssf.usermodel.HSSFSheet;  
import org.apache.poi.hssf.usermodel.HSSFWorkbook;  
import org.springframework.web.servlet.view.document.AbstractExcelView;  

import blkjweb.utils.Console;


/** 
 * 生成excel视图，可用excel工具打开或者保存 
 * 由ViewController的return new ModelAndView(viewExcel, model)生成 
 */  

@SuppressWarnings("deprecation")
public class ExcelView3 extends AbstractExcelView {

	//http://blog.sina.com.cn/s/blog_4c6e822d0102vm6w.html
	public void buildExcelDocument(Map<String,Object> model, 
			HSSFWorkbook workbook,
			HttpServletRequest request,
			HttpServletResponse response)     
	{
		try{
			String excelName = "用户信息.xls";  
			/** 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开*/  
			response.reset();// 清空response  
			response.setContentType("APPLICATION/OCTET-STREAM");// 设置response的Header   
			//response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));//乱码
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(excelName.getBytes("gbk"),"iso-8859-1"));
		      
			List<?> stuList = (List<?>) model.get("list"); 
						
			//产生Excel表头  
			HSSFSheet sheet = workbook.createSheet("输出信息");  
			HSSFRow header = sheet.createRow(0); // 第0行  
			// 产生标题列  
			header.createCell((short) 0).setCellValue("name");  
			header.createCell((short) 1).setCellValue("sex");  
			header.createCell((short) 2).setCellValue("date");  
			header.createCell((short) 3).setCellValue("count");  
			HSSFCellStyle cellStyle = workbook.createCellStyle();  
			cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("mm/dd/yyyy"));  

			// 填充数据  
			int rowNum = 1;  
			for (int i=0;i<stuList.size(); i++) {  
				// Student element = (Student) iter.next();  
				HSSFRow row = sheet.createRow(rowNum++);  
				//MyLogger.showMessage(rowNum);
				row.createCell((short) 0).setCellValue(rowNum+"");  
				row.createCell((short) 1).setCellValue("好");  
				row.createCell((short) 2).setCellValue(rowNum+"");  
				row.getCell((short) 2).setCellStyle(cellStyle);  
				row.createCell((short) 3).setCellValue(rowNum);  
			}  

			// 列总和计算  
			HSSFRow row = sheet.createRow(rowNum);  
			row.createCell((short) 0).setCellValue("TOTAL:");  
			String formual = "SUM(D2:D" + rowNum + ")"; // D2到D[rowNum]单元格起(count数据)  
			row.createCell((short) 3).setCellFormula(formual);  

		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	}
	
	/*
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HSSFSheet excelSheet = workbook.createSheet("Animal List");
		setExcelHeader(excelSheet);
		
		List animalList = (List) model.get("animalList");
		setExcelRows(excelSheet,animalList);
		
	}
	public void setExcelHeader(HSSFSheet excelSheet) {
		HSSFRow excelHeader = excelSheet.createRow(0);
		excelHeader.createCell(0).setCellValue("Id");
		excelHeader.createCell(1).setCellValue("Name");
		excelHeader.createCell(2).setCellValue("Type");
		excelHeader.createCell(3).setCellValue("Aggressive");
		excelHeader.createCell(4).setCellValue("Weight");
	}
	
	public void setExcelRows(HSSFSheet excelSheet, List animalList){
		int record = 1;
		for (Animal animal : animalList) {
			HSSFRow excelRow = excelSheet.createRow(record++);
			excelRow.createCell(0).setCellValue(animal.getId());
			excelRow.createCell(1).setCellValue(animal.getAnimalName());
			excelRow.createCell(2).setCellValue(animal.getAnimalType());
			excelRow.createCell(3).setCellValue(animal.getAggressive());
			excelRow.createCell(4).setCellValue(animal.getWeight());
		}
	}*/
}  

