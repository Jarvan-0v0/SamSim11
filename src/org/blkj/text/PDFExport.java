package org.blkj.text;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import antlr.collections.List;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;


/*
         导出PDF格式的简历   
   @RequestMapping("exportPdf")    
   public ResponseEntity<byte[]> exportPdf(HttpServletRequest request, HttpServletResponse response) throws Exception{     
    Account a = getCurAccount(request);  
    Integer accountId = a.getAccId();  
    Map resultMap = accountService.findAllProfileAndResumeForExport(accountId);  
    File oFile = ExportPdfUtil.getPdf(a, resultMap);  
    HttpHeaders headers = new HttpHeaders();        
       String fileName = null;    
       fileName = new String((a.getRealName()+"简历-"+"迷你校.pdf").getBytes("utf-8"),"iso-8859-1");//解决中文乱码  
       headers.setContentDispositionFormData("attachment", fileName);       
       headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);    
  return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(oFile),headers, HttpStatus.OK);    
   }
 */
public class PDFExport 
{
	public static File getPdf(/*Account account,*/Map map) throws Exception{  
		PdfPCell cell1 =null;  
		String fileName = "test.pdf";  
		File outFile = new File(/*Constant.pdfExportPath + */File.separator + fileName);  
		if (! outFile.exists()) {  
			new File("Constant.pdfExportPath").mkdirs();  
			outFile.createNewFile();  
		}  
		// 1.新建document对象  
		// 第一个参数是页面大小。接下来的参数分别是左、右、上和下页边距。  
		Document document = new Document(PageSize.A4, 50, 50, 50, 50);  
		// 2.建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。  
		PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(outFile));  
		// 3.打开文档  
		document.open();  
		BaseFont baseFontChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);  
		Font fontChinese =  new  Font(baseFontChinese , 11 , Font.NORMAL);  
		Font font_2 = new Font(baseFontChinese,12,Font.BOLD);  
		Font font = new Font(baseFontChinese, 14, Font.BOLD);  
		// 4.向文档中添加内容  
		Paragraph paragraph = new Paragraph(/*account.getRealName()+*/"个人简历",new Font(baseFontChinese,18,Font.BOLD));  
		paragraph.setAlignment(1); //  设置段落居中   
		paragraph.setLeading(7.0f);;  //设置段落与其上方的距离  
		document.add(paragraph);  
		document.add(new Paragraph("\n"));   
		document.add(new Paragraph("基本信息",font));  
		/*  
		 * 输出一条直线  
		 */  
		Paragraph p1 = new Paragraph();    
		p1.add(new Chunk(new LineSeparator()));    
		document.add(p1);   
		document.add(new Paragraph("\n"));    
		if( map.get("r_1001") != null){  
			List list = (List) map.get("r_1001");  
			JSONArray jsonArray = JSONArray.fromObject(list);  
			for(int i = 0 ; i<jsonArray.size(); i++){  
				JSONObject jsonObject = jsonArray.getJSONObject(i);  
				//输出一个表格  
				PdfPTable table = new PdfPTable(6);  
				//第一行  
				cell1 = new PdfPCell(new Phrase("姓名",fontChinese));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("name") != null) ? (jsonObject.get("name")) : ""),fontChinese));  
				cell1.setPadding(6.5f);  
				cell1.setBorderWidth(0);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase(""));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase(""));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  

				cell1 = new PdfPCell(new Phrase(""));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase(""));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				//第二行  
				cell1 = new PdfPCell(new Phrase("性别",fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("sex") != null) ? (jsonObject.get("sex")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase("出生日期",fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("birthday") != null) ? (jsonObject.get("birthday")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase("政治面貌",fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("political") != null) ? (jsonObject.get("political")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				//第三行  
				cell1 = new PdfPCell(new Phrase("现居住地",fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  

				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("city") != null) ? (jsonObject.get("city")) : ""),fontChinese));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase("生源地",fontChinese));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("province") != null) ? (jsonObject.get("province")) : ""),fontChinese));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase("",fontChinese));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase("",fontChinese));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				//第四行  
				cell1 = new PdfPCell(new Phrase("手机号码",fontChinese));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("mobile") != null) ? (jsonObject.get("mobile")) : ""),fontChinese));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase("接收邮箱",fontChinese));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				PdfPCell cell_e = new PdfPCell(new Phrase((String) ((jsonObject.get("email") != null) ? (jsonObject.get("email")) : ""),fontChinese));  
				cell_e.setBorderWidth(0);  
				cell_e.setPadding(6.5f);  
				cell_e.setColspan(2);  
				table.addCell(cell_e);  
				cell1 = new PdfPCell(new Phrase(""));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase(""));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				table.addCell(cell1);  
				//99%    
				table.setWidthPercentage(99);    
				document.add(table);    
			}  
		}else{  
			//输出一个表格  
			PdfPTable table = new PdfPTable(6);  
			//第一行  
			cell1 = new PdfPCell(new Phrase("姓名",fontChinese));  
			cell1.setBorderWidth(0);  
			cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);  
			cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);  
			cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);  
			cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);  
			cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);  
			cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			//第二行  
			cell1 = new PdfPCell(new Phrase("性别",fontChinese));  
			cell1.setBorderWidth(0);  
			cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);  
			cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("出生日期",fontChinese));  
			cell1.setBorderWidth(0);  
			cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);  
			cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("政治面貌",fontChinese));  
			cell1.setBorderWidth(0);  
			cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);  
			cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			//第三行  
			cell1 = new PdfPCell(new Phrase("现居住地",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("生源地",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			//第四行  
			cell1 = new PdfPCell(new Phrase("手机号码",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("接收邮箱",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			table.addCell(cell1);  
			//99%    
			table.setWidthPercentage(99);    
			document.add(table);    
		}  


		//教育经历  
		document.add(new Paragraph("\n"));   
		document.add(new Paragraph("教育经历",font));  
		document.add(p1);   
		document.add(new Paragraph("\n"));   
		if(map.get("r_2001") != null){  
			List list = (List) map.get("r_2001");  
			JSONArray jsonArray = JSONArray.fromObject(list);  
			for(int i = 0 ; i<jsonArray.size(); i++){  
				JSONObject jsonObject = jsonArray.getJSONObject(i);  
				//判断学历  
				String degreee_num = (String) ((jsonObject.get("degree") != null) ? (jsonObject.get("degree")) : "");  
				PdfPTable edu_table = new PdfPTable(4);  
				cell1 = new PdfPCell(new Phrase(((jsonObject.get("startDate") != null) ? (jsonObject.get("startDate")) : "")+"-"+((jsonObject.get("endDate") != null) ? (jsonObject.get("endDate")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				edu_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("school") != null) ? (jsonObject.get("school")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				edu_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("major") != null) ? (jsonObject.get("major")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				edu_table.addCell(cell1);  
				if(jsonObject.get("rank") != null){  
					cell1 = new PdfPCell(new Phrase(degreee_num +"("+((jsonObject.get("rank") != null) ? (jsonObject.get("rank")) : "")+")",fontChinese));  
				}else{  
					cell1 = new PdfPCell(new Phrase(degreee_num,fontChinese));  
				}  

				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				edu_table.addCell(cell1);  
				edu_table.setWidthPercentage(99);    
				document.add(edu_table);  
			}  
		}  
		else{  
			PdfPTable edu_table = new PdfPTable(4);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			edu_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			edu_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			edu_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			edu_table.addCell(cell1);  
			edu_table.setWidthPercentage(99);    
			document.add(edu_table);  
		}  

		//实习经历  
		document.add(new Paragraph("\n"));   
		document.add(new Paragraph("实习经历",font));  
		document.add(p1);   
		document.add(new Paragraph("\n"));   

		if(map.get("r_5001") != null){  
			List list = (List) map.get("r_5001");  
			JSONArray jsonArray = JSONArray.fromObject(list);  
			for(int i = 0 ; i<jsonArray.size(); i++){  
				JSONObject jsonObject = jsonArray.getJSONObject(i);  
				PdfPTable prac_table = new PdfPTable(3);  
				cell1 = new PdfPCell(new Phrase(((jsonObject.get("startDate") != null) ? (jsonObject.get("startDate")) : "")+"-"+((jsonObject.get("endDate") != null) ? (jsonObject.get("endDate")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				prac_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("companyName") != null) ? (jsonObject.get("companyName")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				prac_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("jobName") != null) ? (jsonObject.get("jobName")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				prac_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase("",fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				prac_table.addCell(cell1);  

				PdfPCell cell_d1 = new PdfPCell(new Phrase((String) ((jsonObject.get("describe") != null) ? (jsonObject.get("describe")) : ""),fontChinese));  
				cell_d1.setBorderWidth(0);  
				cell_d1.setPadding(6.5f);  
				cell_d1.setColspan(2);  
				prac_table.addCell(cell_d1);  

				prac_table.setWidthPercentage(99);    
				document.add(prac_table);  
			}  
		}else{  

			PdfPTable prac_table = new PdfPTable(3);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			prac_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			prac_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			prac_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			prac_table.addCell(cell1);  
			prac_table.setWidthPercentage(99);    
			document.add(prac_table);  
		}  



		//社团经历  
		document.add(new Paragraph("\n"));   
		document.add(new Paragraph("社团经历",font));  
		document.add(p1);   
		document.add(new Paragraph("\n"));   
		if(map.get("r_1201") != null){  
			List list = (List) map.get("r_1201");  
			JSONArray jsonArray = JSONArray.fromObject(list);  
			for(int i = 0 ; i<jsonArray.size(); i++){  
				JSONObject jsonObject = jsonArray.getJSONObject(i);  
				PdfPTable party_table = new PdfPTable(4);  
				cell1 = new PdfPCell(new Phrase(((jsonObject.get("startDate") != null) ? (jsonObject.get("startDate")) : "")+"-"+((jsonObject.get("endDate") != null) ? (jsonObject.get("endDate")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				party_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("name") != null) ? (jsonObject.get("name")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				party_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("level") != null) ? (jsonObject.get("level")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				party_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("duty") != null) ? (jsonObject.get("duty")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				party_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase("",fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				party_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("descibe") != null) ? (jsonObject.get("descibe")) : ""),fontChinese));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				cell1.setColspan(2);  
				party_table.addCell(cell1);  
				party_table.setWidthPercentage(99);    
				document.add(party_table);  
			}  
		}else {  
			PdfPTable party_table = new PdfPTable(3);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			party_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			party_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			party_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase(""));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			party_table.addCell(cell1);  
			party_table.setWidthPercentage(99);    
			document.add(party_table);  
		}  


		//项目经验  
		document.add(new Paragraph("\n"));   
		document.add(new Paragraph("项目经验",font));  
		document.add(p1);   
		document.add(new Paragraph("\n"));   

		if(map.get("r_6001") != null){  
			List list = (List) map.get("r_6001");  
			JSONArray jsonArray = JSONArray.fromObject(list);  
			for(int i = 0 ; i<jsonArray.size(); i++){  
				JSONObject jsonObject = jsonArray.getJSONObject(i);  
				PdfPTable pro_table = new PdfPTable(3);  
				cell1 = new PdfPCell(new Phrase(((jsonObject.get("startDate") != null) ? (jsonObject.get("startDate")).toString().trim() : "")+"-"+((jsonObject.get("endDate") != null) ? (jsonObject.get("endDate")).toString().trim() : ""),fontChinese));  
				cell1.setBorderWidth(0);  
				cell1.setPadding(6.5f);  
				pro_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("name") != null) ? (jsonObject.get("name")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				pro_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("role") != null) ? (jsonObject.get("role")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				pro_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase("",fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				pro_table.addCell(cell1);  
				PdfPCell cell_d1 = new PdfPCell(new Phrase((String) ((jsonObject.get("describe") != null) ? (jsonObject.get("describe")) : ""),fontChinese));  
				cell_d1.setBorderWidth(0);cell_d1.setPadding(6.5f);  
				cell_d1.setColspan(2);  
				pro_table.addCell(cell_d1);  
				pro_table.setWidthPercentage(99);   
				document.add(pro_table);  
			}  
		}else {  
			PdfPTable pro_table = new PdfPTable(3);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			pro_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			pro_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			pro_table.addCell(cell1);  
			document.add(pro_table);  
		}  

		//奖励荣誉  
		document.add(new Paragraph("\n"));   
		document.add(new Paragraph("奖励荣誉",font));  
		document.add(p1);   
		document.add(new Paragraph("\n"));   
		//奖学金  
		document.add(new Paragraph("   | 奖学金",font_2));  
		document.add(new Paragraph("\n"));   
		if(map.get("r_3001") != null){  
			List list = (List) map.get("r_3001");  
			JSONArray jsonArray = JSONArray.fromObject(list);  
			for(int i = 0 ; i<jsonArray.size(); i++){  
				JSONObject jsonObject = jsonArray.getJSONObject(i);  
				PdfPTable reward_table = new PdfPTable(4);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("date") != null) ? (jsonObject.get("date")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				reward_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("name") != null) ? (jsonObject.get("name")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				reward_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("rank") != null) ? (jsonObject.get("rank")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				reward_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase("",fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				reward_table.addCell(cell1);   
				reward_table.setWidthPercentage(99);    
				document.add(reward_table);  
			}  
		}else{  
			PdfPTable reward_table = new PdfPTable(4);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			reward_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			reward_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			reward_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			reward_table.addCell(cell1);   
			reward_table.setWidthPercentage(99);    
			document.add(reward_table);  
		}  

		//活动大赛  
		document.add(new Paragraph("   | 活动大赛",font_2));  
		document.add(new Paragraph("\n"));   
		if(map.get("r_1301") != null){  
			List list = (List) map.get("r_1301");  
			JSONArray jsonArray = JSONArray.fromObject(list);  
			for(int i = 0 ; i<jsonArray.size(); i++){  
				JSONObject jsonObject = jsonArray.getJSONObject(i);  
				PdfPTable activity_table = new PdfPTable(4);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("time") != null) ? (jsonObject.get("time")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				activity_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("name") != null) ? (jsonObject.get("name")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				activity_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("eType") != null) ? (jsonObject.get("eType")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				activity_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("level") != null) ? (jsonObject.get("level")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				activity_table.addCell(cell1);   
				activity_table.setWidthPercentage(99);    
				document.add(activity_table);  

			}  
		}else{  
			PdfPTable activity_table = new PdfPTable(4);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			activity_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			activity_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			activity_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			activity_table.addCell(cell1);   
			activity_table.setWidthPercentage(99);    
			document.add(activity_table);  

		}  

		//技能证书  
		document.add(new Paragraph("\n"));   
		document.add(new Paragraph("技能/证书",font));  
		document.add(p1);   
		document.add(new Paragraph("\n"));   
		//语言能力  
		document.add(new Paragraph("   | 语言能力",font_2));  
		document.add(new Paragraph("\n"));   

		if(map.get("r_7002") != null){  
			List list = (List) map.get("r_7002");  
			JSONArray jsonArray = JSONArray.fromObject(list);  
			for(int i = 0 ; i<jsonArray.size(); i++){  
				JSONObject jsonObject = jsonArray.getJSONObject(i);  
				PdfPTable language_table = new PdfPTable(3);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("lType") != null) ? (jsonObject.get("lType")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				language_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("level") != null) ? (jsonObject.get("level")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				language_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("rank") != null) ? (jsonObject.get("rank")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				language_table.addCell(cell1);  
				language_table.setWidthPercentage(99);    
				document.add(language_table);  
			}  
		}else{  
			PdfPTable language_table = new PdfPTable(3);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			language_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			language_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			language_table.addCell(cell1);  
			language_table.setWidthPercentage(99);    
			document.add(language_table);  
		}  


		//证书  
		document.add(new Paragraph("   | 证书",font_2));  
		document.add(new Paragraph("\n"));   
		if(map.get("r_3002") != null){  
			List list = (List) map.get("r_3002");  
			JSONArray jsonArray = JSONArray.fromObject(list);  
			for(int i = 0 ; i<jsonArray.size(); i++){  
				JSONObject jsonObject = jsonArray.getJSONObject(i);  
				PdfPTable cert_table = new PdfPTable(2);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("name1") != null) ? (jsonObject.get("name1")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				cert_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("authority") != null) ? (jsonObject.get("authority")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				cert_table.addCell(cell1);  
				cert_table.setWidthPercentage(99);    
				document.add(cert_table);  
			}  
		}else{  
			PdfPTable cert_table = new PdfPTable(2);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			cert_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			cert_table.addCell(cell1);  
			cert_table.setWidthPercentage(99);    
			document.add(cert_table);  
		}  

		//技能  
		document.add(new Paragraph("   | 技能",font_2));  
		document.add(new Paragraph("\n"));   
		if(map.get("r_7001") != null){  
			List list = (List) map.get("r_7001");  
			JSONArray jsonArray = JSONArray.fromObject(list);  
			for(int i = 0 ; i<jsonArray.size(); i++){  
				JSONObject jsonObject = jsonArray.getJSONObject(i);  
				PdfPTable skill_table = new PdfPTable(3);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("name") != null) ? (jsonObject.get("name")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				cell1.setColspan(3);  
				skill_table.addCell(cell1);  
				skill_table.setWidthPercentage(99);    
				document.add(skill_table);  

			}  
		}else{  

			PdfPTable skill_table = new PdfPTable(3);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			skill_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			skill_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			skill_table.addCell(cell1);  
			skill_table.setWidthPercentage(99);    
			document.add(skill_table);  

		}  

		//附加信息  
		document.add(new Paragraph("\n"));   
		document.add(new Paragraph("附加信息",font));  
		document.add(p1);   
		document.add(new Paragraph("\n"));   
		//实验室  
		document.add(new Paragraph("   | 实验室",font_2));  
		document.add(new Paragraph("\n"));   

		if(map.get("r_1401") != null){  
			List list = (List) map.get("r_1401");  
			JSONArray jsonArray = JSONArray.fromObject(list);  
			for(int i = 0 ; i<jsonArray.size(); i++){  
				JSONObject jsonObject = jsonArray.getJSONObject(i);  
				PdfPTable lab_table = new PdfPTable(4);  
				cell1 = new PdfPCell(new Phrase(((jsonObject.get("startDate") != null) ? (jsonObject.get("startDate")) : "")+"-"+((jsonObject.get("endDate") != null) ? (jsonObject.get("endDate")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				lab_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("name") != null) ? (jsonObject.get("name")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				lab_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase((String) ((jsonObject.get("level") != null) ? (jsonObject.get("level")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				lab_table.addCell(cell1);  
				cell1 = new PdfPCell(new Phrase("导师:  "+((jsonObject.get("instructor") != null) ? (jsonObject.get("instructor")) : ""),fontChinese));  
				cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
				lab_table.addCell(cell1);   
				lab_table.setWidthPercentage(99);    
				document.add(lab_table);  
			}  
		}else{  
			PdfPTable lab_table = new PdfPTable(4);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			lab_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			lab_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			lab_table.addCell(cell1);  
			cell1 = new PdfPCell(new Phrase("",fontChinese));  
			cell1.setBorderWidth(0);cell1.setPadding(6.5f);  
			lab_table.addCell(cell1);   
			lab_table.setWidthPercentage(99);    
			document.add(lab_table);  
		}  
		// 5.关闭文档  
		document.close();  
		return outFile;  

	} 
}
