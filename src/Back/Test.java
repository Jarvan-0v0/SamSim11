package Back;

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

import blkjweb.utils.CellUtil;  

//https://www.cnblogs.com/dj0721/p/5846387.html
//http://poi.apache.org/apidocs/
public class Test {

	 public static void main(String[] args) throws Exception {    
        
         Test test = new Test(); 
         
         String sheetName = "温湿度日记录";
         String date = "2018-01-01";
         //在excel中的第3行每列的参数
         String[] head0 = new String[] { "日期", "天气", "自然", "自然", "调整", "调整","备注", "记录人" };
       //在excel中的第4行每列（合并列）的参数
         String[] head1 = new String[] { "温度℃", "湿度%", "温度℃", "湿度%" };
         //对应excel中的行和列，下表从0开始{"开始行,结束行,开始列,结束列"}
         String[] headnum0 = new String[] { "2,3,0,0", "2,3,1,1", "2,2,2,3","2,2,4,5", "2,3,6,6", "2,3,7,7" };
         String[] headnum1 = new String[] { "3,3,2,2", "3,3,3,3", "3,3,4,4","3,3,5,5" };
        
         //需要显示在excel中的参数对应的值，因为是用map存的，放的都是对应的key
         String[] colName = new String[] { "date", "weather", "natureTem","natureHum", "adjustTem", "adjustHum", "remark", "creator" };
         
         List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
          Map<String, Object> tmpMap = new HashMap<String, Object>();
           tmpMap.put("date", "1");
           tmpMap.put("weather","天气");
           tmpMap.put("natureTem","自然2");
           tmpMap.put("natureHum", "自然1");
           tmpMap.put("adjustTem", "调整1");
           tmpMap.put("adjustHum", "调整2");
           tmpMap.put("remark", "评论");
           tmpMap.put("creator", "记录人");
           dataList.add(tmpMap);
           
           tmpMap = new HashMap<String, Object>();
           tmpMap.put("date", "12");
           tmpMap.put("weather","天气2");
           tmpMap.put("natureTem","自然22");
           tmpMap.put("natureHum", "自然12");
           tmpMap.put("adjustTem", "调整12");
           tmpMap.put("adjustHum", "调整22");
           tmpMap.put("remark", "评论2");
           tmpMap.put("creator", "记录人2");
           dataList.add(tmpMap);
         
         //test.reportMergeXls(sheetName,date,head0,headnum0, head1,headnum1,dataList,colName);
         
         HSSFWorkbook wb = new HSSFWorkbook();
         OutputStream os = new FileOutputStream("F:\\test2.xls");  
         test.createFile(os, wb);
    
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
	 void reportMergeXls(String sheetName,String date,
			 			String[] head0,String[] headnum0, String[] head1, String[] headnum1,
			 			List<Map<String, Object>> dataList,String[] detail) 
	 {
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
	        // 设置列宽  （第几列，宽度）
	        sheet.setColumnWidth( 0, 1600);
	        sheet.setColumnWidth( 1, 3600);  
	        sheet.setColumnWidth( 2, 2800);  
	        sheet.setColumnWidth( 3, 2800);  
	        sheet.setColumnWidth( 4, 2800);  
	        sheet.setColumnWidth( 5, 2800);
	        sheet.setColumnWidth( 6, 4500);
	        sheet.setColumnWidth( 7, 3600);
	        sheet.setDefaultRowHeight((short)360);//设置行高
	        
	        // 第一行表头  标题
	        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, head0.length-1));
	        HSSFRow row = sheet.createRow(0);
	        row.setHeight((short) 0x349);
	        HSSFCell cell = row.createCell(0);
	        cell.setCellStyle(headstyle);
	        CellUtil.setCellValue(cell, sheetName);
	        // 第二行表头  时间
	        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, head0.length-1));
	        HSSFRow row1 = sheet.createRow(1);
	        row.setHeight((short) 0x349);
	        HSSFCell cell1 = row1.createCell(0);
	        cell1.setCellStyle(datestyle);
	        CellUtil.setCellValue(cell1, date);        
	        
	        //第三行表头列名
	        row = sheet.createRow(2);
	        for (int i = 0; i < 8; i++) {
	            cell = row.createCell(i);
	            cell.setCellValue(head0[i]);
	            cell.setCellStyle(style);
	        }
	        //动态合并单元格
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
	            cell.setCellStyle(style);//设置excel中第四行的1、2、7、8列的边框
	            if(i > 1 && i< 6) {
	                for (int j = 0; j < head1.length; j++) {
	                    cell = row.createCell(j + 2);
	                    cell.setCellValue(head1[j]);//给excel中第四行的3、4、5、6列赋值（"温度℃", "湿度%", "温度℃", "湿度%"）
	                    cell.setCellStyle(style);//设置excel中第四行的3、4、5、6列的边框
	                }
	            }
	        }
	        //动态合并单元格
	      /*  for (int i = 0; i < headnum1.length; i++) {
	            String[] temp = headnum1[i].split(",");
	            Integer startrow = Integer.parseInt(temp[0]);
	            Integer overrow = Integer.parseInt(temp[1]);
	            Integer startcol = Integer.parseInt(temp[2]);
	            Integer overcol = Integer.parseInt(temp[3]);
	            System.out.println("输出："+i);
	            sheet.addMergedRegion(new CellRangeAddress(startrow, overrow, startcol, overcol));
	        }*/
	        
	        // 设置列值-内容
	        for (int i = 0; i < dataList.size(); i++) {
	            row = sheet.createRow(i + 4);//标题、时间、表头字段共占了4行，所以在填充数据的时候要加4，也就是数据要从第5行开始填充
	            for (int j = 0; j < detail.length; j++) {
	                Map<String, Object> tempmap = (HashMap<String, Object>)dataList.get(i);
	                Object data = tempmap.get(detail[j]);                
	                cell = row.createCell(j);
	                cell.setCellStyle(style2);
	                cell.setCellType(CellType.STRING);
	                CellUtil.setCellValue(cell, data);
	            }
	        }
	        
	        try {
	        	//写入文件的方法
	        	OutputStream os = new FileOutputStream("F:\\test.xls");    
				workbook.write(os);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        
	        /*
	                      网页 供下载
	        HttpServletRequest request,HttpServletResponse response
	        String fileName = new String(sheetName.getBytes("gb2312"), "ISO8859-1");
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        workbook.write(baos);
	       
	        response.setContentType("application/x-download;charset=utf-8");
	        response.addHeader("Content-Disposition", "attachment;filename="
	                + fileName + ".xls");
	        OutputStream os = response.getOutputStream();
	        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	        byte[] b = new byte[1024];
	        while ((bais.read(b)) > 0) {
	            os.write(b);
	        }
	        bais.close();
	        os.flush();
	        os.close();
	       */
	  
	 }
	 
     private void createFile(OutputStream os,Workbook wb) throws IOException{    
         int i = 0;    
         int j = 0;    
         double trans_amt = 0.00;    
         double ref_amt = 0.00;    
         String[] refundLogs = new String[2];    
         String str1 = "20110812|34234234242432|345.00|323.00";    
         String str2 = "20110504|45656464535345|231.34|231.34";    
         refundLogs[0] = str1;    
         refundLogs[1] = str2;    
         Sheet sheet = wb.createSheet("T建行退款文件");    
         Row row = sheet.createRow(0);    
         for(i=1;i<=3;i++){    
             sheet.createRow(i);    
         }    
             
         for(i=0;i<4;i++)    
             row.createCell(i);    
             
         sheet.addMergedRegion(new CellRangeAddress(0, 3, 0, 3));    
         //CellRangeAddress（int firstRow, int lastRow, int firstCol, int lastCol）  
         //参数：起始行号，终止行号， 起始列号，终止列号  
         HSSFFont font = (HSSFFont) wb.createFont();    
         font.setFontName("黑体");    
         font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);    
       
         CellStyle cs1 = wb.createCellStyle();    
         cs1.setAlignment(HorizontalAlignment.CENTER);    
         cs1.setDataFormat(wb.createDataFormat().getFormat("yyyyMMdd"));    
         cs1.setFont(font);    
             
         CellStyle cs2 = wb.createCellStyle();    
         cs2.setAlignment(HorizontalAlignment.CENTER);    
         cs2.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));    
         cs2.setFont(font);    
             
         CellStyle cs3 = wb.createCellStyle();    
         cs3.setAlignment(HorizontalAlignment.CENTER);    
         cs3.setFont(font);    
             
         row = sheet.getRow(0);    
         Cell cell = row.getCell(0);    
         cell.setCellType(CellType.STRING);    
         cell.setCellValue("建行运行中心：\n\t"+"现有"+refundLogs.length+"表退款交易，请配合汇付天下公司进行审核");    
             
         sheet.createRow(4);    
         row = sheet.createRow(5);    
             
         cell = row.createCell(0);    
         cell.setCellType(CellType.STRING);    
         cell.setCellValue("商户编号：");    
         cell = row.createCell(1);    
         cell.setCellType(CellType.STRING);    
         cell.setCellValue("45433242");    
             
         row = sheet.createRow(6);    
         cell = row.createCell(0);    
         cell.setCellType(CellType.STRING);    
         cell.setCellValue("交易明细：");    
             
         row = sheet.createRow(7);    
         row.createCell(0).setCellValue("退款日期");    
         row.createCell(1).setCellValue("消费卡号");    
         row.createCell(2).setCellValue("消费金额");    
         row.createCell(3).setCellValue("退款金额");    
         for(i=0;i<4;i++)    
             row.getCell(i).setCellStyle(cs3);    
             
             
         for(i=8;i<=7+refundLogs.length;i++)    
         {    
             sheet.createRow(i);    
             for(j=0;j<4;j++)    
                 sheet.getRow(i).createCell(j);    
         }    
         for(i=0;i<refundLogs.length;i++){    
             row = sheet.getRow(8+i);    
             String[] refundLog = refundLogs[i].split("\\|");    
             cell = row.getCell(0);    
             cell.setCellStyle(cs1);    
             cell.setCellValue(refundLog[0]);    
                 
             cell = row.getCell(1);    
             cell.setCellType(CellType.STRING);    
             cell.setCellStyle(cs3);    
             cell.setCellValue(refundLog[1]);    
                 
             cell = row.getCell(2);    
             cell.setCellStyle(cs2);    
             cell.setCellValue(refundLog[2]);    
             trans_amt += Double.parseDouble(refundLog[2]);    
                 
             cell = row.getCell(3);    
             cell.setCellType(CellType.NUMERIC);    
             cell.setCellStyle(cs2);    
             cell.setCellValue(refundLog[3]);    
             ref_amt += Double.parseDouble(refundLog[3]);    
         }    
             
         row = sheet.createRow(9+i);    
         for(i=0;i<4;i++)    
             row.createCell(i);    
         row.getCell(0).setCellValue("总计：");    
         row.getCell(2).setCellValue(trans_amt);    
         row.getCell(3).setCellValue(ref_amt);    
             
         sheet.autoSizeColumn(0);    
         sheet.autoSizeColumn(1);    
         sheet.autoSizeColumn(2);    
         sheet.autoSizeColumn(3);    
             
         wb.write(os);    
     }    

}
