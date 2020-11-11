package org.blkj.text;

//http://blog.csdn.net/u013614451/article/details/25597559

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFWithWaterMark 
{
    
	public static void main(String[] args) throws Exception {
		// 添加水印之前的pdf文件
		//String inputFile = "f:/1.pdf";
		//String markImagePath = "F:/1.png"; // 水印图片路径
		//String outputFile = "F:/2.pdf";
		//createPdf(inputFile,markImagePath,outputFile);
		
		createPdf();
		   
	}
	
	//////////////////////////
	public static void createPdf() throws Exception 
	{
		//设置字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);   
		Font FontChinese30 = new Font(bfChinese, 30, Font.BOLD);
		Font FontChinese24 = new Font(bfChinese, 30, Font.NORMAL);

		//①建立com.lowagie.text.Document对象的实例 PageSize.A4, 30, 30, 30, 30
		//页面大小  
		Document document = new Document(PageSize.A4.rotate());//设置图片大小 页面为A4。 横向显示
		
		//页边空白     
		//document.setMargins(10, 20, 30, 40);

		//②建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
		PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream("F:\\Helloworld.PDF"));


		//通过PDF页面事件模式添加文字水印功能  
		PdfFileExportUtil pdfFileExportUtil = new PdfFileExportUtil();  
		pdfWriter.setPageEvent(pdfFileExportUtil.new TextWaterMarkPdfPageEvent("北京交通大学"));  

		//③打开文档
		document.open();

		String titleStr = "安全漏洞扫描报告";  
		String contentStr ="在软件在线升级流程中， 华动泰越科技有限责任公司负责软件在线升级系统对外业务的管理与控制；客户服务中心具体管理软件在线升级的业务流程，以及软件版本、 "
				+ "文件的发布、 升级、维护等； 软件安装实施部负责具体升级工作的操作； 产品运维部负责管理系统的稳定运行。该软件在线升级系统的流程管理体现了华动泰越科技有限责任"
				+ "公司根据规范的制度给具体的岗位和公司人员安排分配";

		//④向文档中添加内容
		Paragraph title = new Paragraph(titleStr, FontChinese30);//设置标题
		//设置段落前后的间距
		title.setSpacingBefore(50);
		title.setSpacingAfter(5);
		title.setAlignment(Element.ALIGN_CENTER); //设置段落的对齐方式
		document.add(title);

		//加入空行
		document.add(new Chunk("\n\n"));

		//行距是用单位来衡量。每英寸有72个单位。默认间距是字体高度的1.5倍。您可以更改行距间距作为参数传递给段落构造方法
		Paragraph content = new Paragraph(contentStr,FontChinese24); 
		//设置行间距
		content.setLeading(35); 
		//整段缩进可以设置Paragraph的setIndentationRight或者setIndentationLeft
		//首行缩进可以通过设置Paragraph的FirstLineIndent属性可以将段落首行缩进。
		content.setFirstLineIndent(50);//距左边的间隔；

		document.add(content);  

		//⑤关闭文档。
		document.close();

	}

	
	static final float IAMGE_HEIGHT = 110f; // 限制水印图片的的高度
	static final float IAMGE_WIDTH = 110f; // 限制水印图片的的宽度

	/**
	 * 给pdf文件添加水印
	 * @param file 要加水印的原pdf文件
	 * @param markImagePath 水印图片路径
	 * @param outputFil 添加水印之后的pdf文件
	 * @throws Exception
	 */
	public static void createPdf(String inputFile, String markImagePath,String outputFile) throws Exception 
	{
		File file = new File(inputFile);
		// 如果是web项目，应该从web项目里面获取logo
		// String markImagePath =
		//request.getSession().getServletContext().getRealPath("/") +"resources/images/logo.jpg";
		PdfReader reader = new PdfReader(file.getPath(), "PDF".getBytes());
		// 如果是web项目，直接下载应该放到response的流里面
		//PdfStamper stamp = new PdfStamper(reader,response.getOutputStream());
		//添加水印之后的pdf文件
		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(outputFile));
		
		int pageSize = reader.getNumberOfPages();
		float pageHeight = reader.getPageSize(1).getHeight();
		float pageWidth = reader.getPageSize(1).getWidth();
		try {
			// 每两行显示两个 左边一个，右边一个
			int lineNum = (int) (pageHeight / IAMGE_HEIGHT); // 行数
			int middleY = (int) pageWidth / 2;
			for (int i = 1; i <= pageSize; i++) {
				for (int j = 0, k = 0; j < lineNum; j = j + 2, k++) {
					Random random = new Random();
					Image img = Image.getInstance(markImagePath);// 插入水印
					img.scaleAbsolute(IAMGE_WIDTH, IAMGE_HEIGHT * 184 / 455);
					img.setAlignment(Image.UNDERLYING); // 在字下面
					int trueY;
					while (true) {
						trueY = random.nextInt(middleY);
						if (trueY > IAMGE_WIDTH / 2	&& trueY < (middleY - IAMGE_WIDTH)) {
							break;
						}
					}
					img.setAbsolutePosition(trueY, j * IAMGE_HEIGHT	+ (float) random.nextInt((int) IAMGE_HEIGHT) - (k % 2) * 10); // 水印的位置
					img.setRotationDegrees(random.nextInt(360));// 旋转 角度
					PdfContentByte under = stamp.getUnderContent(i);
					PdfGState gs = new PdfGState();
					gs.setFillOpacity(0.3f); // 设置透明度为0.3
					under.setGState(gs);
					under.addImage(img);
					while (true) {
						trueY = random.nextInt(middleY) + middleY;
						if (trueY > middleY + IAMGE_WIDTH / 2 && trueY < (2 * middleY - IAMGE_WIDTH)) {
							break;
						}
					}
					img.setAbsolutePosition(trueY, j * IAMGE_HEIGHT	+ (float) random.nextInt((int) IAMGE_HEIGHT)- (k % 2) * 10); // 水印的位置
					img.setRotationDegrees(random.nextInt(180));// 旋转 角度
					under.addImage(img);
				}
			}
		} finally {
			stamp.close();// 关闭
			reader.close();
		}

	}
	
	
}
