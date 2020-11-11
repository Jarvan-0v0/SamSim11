package org.blkj.text;

import java.awt.Color;
import java.util.List;  
import java.util.Map;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
import org.springframework.web.servlet.view.document.AbstractPdfView;  
import com.lowagie.text.Document;  
import com.lowagie.text.Paragraph;  
import com.lowagie.text.pdf.BaseFont;  
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Element;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;

/** 
   http://blog.csdn.net/marila4720/article/details/8603333
 */  

public class PDFView extends AbstractPdfView
{     
	@Override
	protected void buildPdfMetadata(Map<String, Object> model, 
			Document document, HttpServletRequest request)
	{
		Rectangle rec = new Rectangle(600, 800);
		document.setPageSize(rec);
		document.setPageCount(1);
		
		
	}

	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{

		try{
			String excelName = "用户信息.pdf";  
			// 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
			response.reset();// 清空response  
			response.setContentType("APPLICATION/OCTET-STREAM");  
			//response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8")); 
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(excelName.getBytes("gbk"),"iso-8859-1"));
			//显示中文  
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);   
			com.lowagie.text.Font FontChinese = new com.lowagie.text.Font(bfChinese, 12, com.lowagie.text.Font.NORMAL );   

			PdfPTable table = new PdfPTable(5);// 建立一个pdf表格  
			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.getDefaultCell().setBackgroundColor(Color.CYAN);
		
			//Object ob = model.get("contents");
			List<?> stuList = (List<?>) model.get("list"); 
			String value = null;  
			for (int i = 0; i < stuList.size(); i++) {    
				//Student s = (Student)stuList.get(i);  
				value = "姓名: "+ i+",性别: "+i + ",日期: " + i + ",总数: " + i;  
				document.add(new Paragraph(value,FontChinese));     
			}  
			PdfPCell cell = null;  
			for (int i = 0; i < stuList.size(); i++) {    
    		//Student s = (Student)stuList.get(i);  
    			//Object feedObj = ((List<?>) ob).get(i);
    			//MyContent myContent = (MyContent)feedObj;
    			table.addCell("myContent.getId()");
    			table.addCell("myContent.getTitle()");
    			cell = new PdfPCell(new Paragraph("身份证号",FontChinese));//解决不显示中文问题
    			table.addCell(cell);
    	        //table.addCell("中国g");
    			table.addCell("myContent.getPubDate().toString()");
    			table.addCell("myContent.getLink()");
    		}
			table.completeRow();
			document.add(table);
		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	} 

}  

