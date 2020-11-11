package blkjweb.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;  
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;  

//https://www.cnblogs.com/dj0721/p/5846387.html
//https://www.cnblogs.com/huajiezh/p/5467821.html
//http://poi.apache.org/apidocs/
public class ExcelUtil {

	public static void main(String[] args) throws Exception {    

		ExcelUtil test = new ExcelUtil(); 

		
	
		//在excel中的第3行每列的参数
		String[] head0 = new String[] { "序号", "试验参数", "规范值", "实测值", "实测值", "实测值","是否满足规范要求"};//, "实测值"
		//在excel中的第4行每列（合并列）的参数
		String[] head1 = new String[] {"温度℃", "湿度%","号外1"};
		//对应excel中的行和列，下表从0开始{"开始行,结束行,开始列,结束列"} //"2,2,3,5"与"实测值"对应即可
		String[] headnum0 = new String[] { "2,3,0,0", "2,3,1,1", "2,3,2,2",
											"2,2,3,5",
											"2,3,6,6"};
		
		//需要显示在excel中的参数对应的值，因为是用map存的，放的都是对应的key
		String[] colName = new String[] { "date", "weather", "natureTem", "adjustTem", "adjustHum", "remark"};

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> tmpMap = new HashMap<String, Object>();
		tmpMap.put("date", "1");
		tmpMap.put("weather","天气");
		tmpMap.put("natureTem","自然2");
		tmpMap.put("natureHum", "自然1");
		tmpMap.put("adjustTem", "调整1");
		tmpMap.put("adjustHum", "调整2");
		tmpMap.put("remark", "评论");
		dataList.add(tmpMap);

		tmpMap = new HashMap<String, Object>();
		tmpMap.put("date", "12");
		tmpMap.put("weather","天气2");
		tmpMap.put("natureTem","自然22");
		tmpMap.put("natureHum", "自然12");
		tmpMap.put("adjustTem", "调整12");
		tmpMap.put("adjustHum", "调整22");
		tmpMap.put("remark", "评论2");
		dataList.add(tmpMap);

		//new ExcelUtil().reportMergeXls(head0,headnum0, head1,dataList,colName);

		/*   HSSFWorkbook wb = new HSSFWorkbook();
         OutputStream os = new FileOutputStream("F:\\test2.xls");  
         test.createFile(os, wb);
		 */
	}
	/**
	 * 多行表头
	 * dataList：导出的数据；
	 * sheetName：表头名称； 
	 * head0：表头第一行列名；
	 * headnum0：第一行合并单元格的参数
	 * head1：表头第二行列名；
	 * headnum1：第二行合并单元格的参数；
	 * detail：导出的表体字段
	 *
	 */
	public void reportMergeXls(
			HttpServletRequest request,HttpServletResponse response,
			String[] head0,String[] headnum0, String[] head1,
			List<Map<String, Object>> dataList,String[] colName,
			Map<String, Object> dataMap) 
	{

		String sheetName = "运营性能实验结果";
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(sheetName);// 创建一个表
		// 表头标题样式
		HSSFFont headfont = workbook.createFont();
		headfont.setFontName("宋体");
		headfont.setFontHeightInPoints((short) 22);// 字体大小
		HSSFCellStyle headstyle = workbook.createCellStyle();
		headstyle.setFont(headfont);
		headstyle.setAlignment(HorizontalAlignment.CENTER);// 左右居中
		headstyle.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
		headstyle.setLocked(true);    
		//表头时间样式
		HSSFFont datefont = workbook.createFont();
		datefont.setFontName("宋体");
		datefont.setFontHeightInPoints((short) 12);// 字体大小
		HSSFCellStyle datestyle = workbook.createCellStyle();
		datestyle.setFont(datefont);
		datestyle.setAlignment(HorizontalAlignment.CENTER);// 左右居中
		datestyle.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
		datestyle.setLocked(true);
		// 列名样式
		HSSFFont font = workbook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);// 字体大小
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN); //下边框
		style.setBorderLeft(BorderStyle.THIN);//左边框
		style.setBorderTop(BorderStyle.THIN);//上边框
		style.setBorderRight(BorderStyle.THIN);//右边框
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);// 左右居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
		style.setLocked(true);
		
		// 普通单元格样式（中文）
		HSSFFont font2 = workbook.createFont();
		font2.setFontName("宋体");
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
		font2.setFontHeightInPoints((short) 12);
		HSSFCellStyle style2 = workbook.createCellStyle();
		style2.setBorderBottom(BorderStyle.THIN); //下边框
		style2.setBorderLeft(BorderStyle.THIN);//左边框
		style2.setBorderTop(BorderStyle.THIN);//上边框
		style2.setBorderRight(BorderStyle.THIN);//右边框
		style2.setFont(font2);
		style2.setAlignment(HorizontalAlignment.CENTER);// 左右居中
		style2.setWrapText(true); // 换行
		style2.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
		
		HSSFFont font3 = workbook.createFont();
		font3.setFontName("黑体");
        font3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
		font3.setFontHeightInPoints((short) 12);// 字体大小
		HSSFCellStyle style3 = workbook.createCellStyle();
		style3.setBorderBottom(BorderStyle.THIN); //下边框
		style3.setBorderLeft(BorderStyle.THIN);//左边框
		style3.setBorderTop(BorderStyle.THIN);//上边框
		style3.setBorderRight(BorderStyle.THIN);//右边框
		style3.setFont(font);
		style3.setAlignment(HorizontalAlignment.CENTER);// 左右居中
		style3.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
		style3.setLocked(true);
		
	   // 设置列宽  （第几列，宽度）
		for (int i=0; i <head0.length; i++) {
			if(i== 1)
				sheet.setColumnWidth(i, 15000);
			else
				sheet.setColumnWidth(i, 5000);
		}
		
		//sheet.autoSizeColumn(0);
	
		sheet.setDefaultRowHeight((short)460);//设置行高

		int dataStartRow = 4;//数据起始行 0开始
		
		// { "序号", "试验参数", "规范值", "实测值", "实测值","是否满足规范要求"};
		int multiLevelTitleStar = 3;//具有二级标题的开始列
		int multiLevelTitleEnd = head0.length - 1;//具有二级标题的结束列
	
		int itemNums = 3 ; //每行显示的数量
		int dataTotalNum = dataList.size();//数据总数
	    	    
		HSSFRow row;
		HSSFCell cell;

		int firstRow = 0;
		int lastRow = firstRow;
		int firstCol = itemNums * 2 -1;
		int lastCol = head0.length-1;
		//第一行表头
		if(head0.length - itemNums*2 >= 1){
			sheet.addMergedRegion(new CellRangeAddress(firstRow,lastRow,firstCol,lastCol));
		}
		row = sheet.createRow(firstRow);  

		cell = row.createCell(0);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,"桥号:");	    		   
		cell = row.createCell(1);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,dataMap.get("qiaohao"));

		cell = row.createCell(2);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,"桥名:");	    		   
		cell = row.createCell(3);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,dataMap.get("qiaoming"));

		cell = row.createCell(4);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,"中心里程:");	    		   
		cell = row.createCell(5);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,dataMap.get("zhongxinlicheng"));
		

		//第二行表头
		firstRow = 1;
		lastRow = firstRow;
		firstCol = itemNums * 2 -1;//5
		lastCol = head0.length - 1;
		if(head0.length - itemNums*2 >= 1){
			sheet.addMergedRegion(new CellRangeAddress(firstRow,lastRow,firstCol,lastCol));
		}
		row = sheet.createRow(firstRow);  

		cell = row.createCell(0);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,"孔跨号");	    		   
		cell = row.createCell(1);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,dataMap.get("kongkuahao"));
		
		cell = row.createCell(2);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,"墩台1");	    		   
		cell = row.createCell(3);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,dataMap.get("duntaihao1"));
		
		cell = row.createCell(4);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,"墩台2");	    		   
		cell = row.createCell(5);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,dataMap.get("duntaihao2"));

		//第三行表头列名
		row = sheet.createRow(2);//第三行
		for (int i = 0; i < head0.length; i++) {//第三行的一级标题
			cell = row.createCell(i);
			cell.setCellStyle(style);
			
			//System.out.println(i+ "_head0_" + head0[i]); 
			cell.setCellValue(head0[i]);
		}
		
		for (int i = 0; i < headnum0.length; i++) {
			String[] temp = headnum0[i].split(",");
			Integer startrow = Integer.parseInt(temp[0]);
			Integer overrow = Integer.parseInt(temp[1]);
			Integer startcol = Integer.parseInt(temp[2]);
			Integer overcol = Integer.parseInt(temp[3]);
			sheet.addMergedRegion(new CellRangeAddress(startrow, overrow,startcol, overcol));
		}
		//设置合并单元格的参数并初始化带边框的表头（这样做可以避免因为合并单元格后有的单元格的边框显示不出来）
		row = sheet.createRow(3);//因为下标从0开始，所以这里表示的是excel中的第四行
		for (int i = 0; i < head0.length; i++) {
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i > multiLevelTitleStar && i<multiLevelTitleEnd) {//第三行的二级标题赋值head1中的值
				for (int j = 0; j < head1.length; j++) {
					cell = row.createCell(j + multiLevelTitleStar);
					//System.out.println(i+ "_head1_" + head1[j]);
					cell.setCellValue(head1[j]);
					cell.setCellStyle(style);
				}
			}
		}
		// 设置列值-内容   暂时假定只有两行数据
		for (int i = 0; i < dataTotalNum; i++) {
			row = sheet.createRow(i + dataStartRow);
			for (int j = 0; j < colName.length; j++) {
				cell = row.createCell(j);
				cell.setCellStyle(style2);
				cell.setCellType(CellType.STRING);
				
				Map<String, Object> tempmap = (HashMap<String, Object>)dataList.get(i);
				Object data = tempmap.get(colName[j]);
				CellUtil.setCellValue(cell, data);
			}
		}
	
		//暂时假定只有两行数据
		firstRow = dataTotalNum + dataStartRow;
		lastRow = firstRow;
		firstCol = 1;
		lastCol = head0.length-1;
		sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow,firstCol, lastCol));
		row = sheet.createRow(firstRow);//第六行 试验结论
		cell = row.createCell(0);  
		row.setHeight((short) 0x349);
		cell.setCellStyle(style);
		CellUtil.setCellValue(cell,"实验结论："); 
		cell = row.createCell(1);
		row.setHeight((short) 0x349);
		cell.setCellStyle(style);  
		CellUtil.setCellValue(cell,dataMap.get("djielun"));
	
		
		firstRow = 1 + firstRow;
		lastRow = firstRow;
		lastCol = head0.length-1;
		sheet.addMergedRegion(new CellRangeAddress(firstRow,lastRow,firstCol, lastCol));
		row = sheet.createRow(firstRow);//第七行 决策建议
		cell = row.createCell(0);
		row.setHeight((short) 0x349);
		cell.setCellStyle(style); 
		CellUtil.setCellValue(cell,"决策建议:");
		
		cell = row.createCell(1);
		cell.setCellStyle(style);  
		CellUtil.setCellValue(cell,dataMap.get("jianyi"));

	
		//每行显示三项数据
		firstRow = 1 + firstRow;
		lastRow = firstRow;
		firstCol = itemNums * 2 - 1;//5;
		lastCol = head0.length - 1;
		if(head0.length - itemNums*2 >= 1){
			sheet.addMergedRegion(new CellRangeAddress(firstRow,lastRow,firstCol,lastCol));
		}
		row = sheet.createRow(firstRow); 
		cell = row.createCell(0);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,"试验时间：");	    		   
		cell = row.createCell(1);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,dataMap.get("pingdingdate"));
		
		cell = row.createCell(2);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,"录入人员：");	    		   
		cell = row.createCell(3);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,dataMap.get("jiluren"));
		
		cell = row.createCell(4);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,"试验单位：");	    		   
		cell = row.createCell(5);
		cell.setCellStyle(style); 
		cell.setCellType(CellType.STRING);
		CellUtil.setCellValue(cell,dataMap.get("shiyandanwei"));
		
		OutputStream os = null;
		ByteArrayInputStream bais = null;
		try {//写入文件的方法
			/*OutputStream os = new FileOutputStream("F:\\test.xls");    
			workbook.write(os);*/
		
			sheetName = new String(sheetName.getBytes("gb2312"), "ISO8859-1");
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        workbook.write(baos);

	        response.setContentType("application/x-download;charset=utf-8");
	        response.addHeader("Content-Disposition", "attachment;filename=" + sheetName + ".xls");
	        os = response.getOutputStream();
	        bais = new ByteArrayInputStream(baos.toByteArray());
	        byte[] b = new byte[1024];
	        while ((bais.read(b)) > 0) {
	            os.write(b);
	        }
			os.flush();
		} catch (IOException e) {
			e.getMessage();
		}
		finally{
			try {
				if(bais != null)
					bais.close();
				if(os != null) 
					os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        			
		}
	}
}
