package org.blkj.text;

import java.io.FileOutputStream;
import java.io.IOException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PdfTemplate 
{
	public static void main(String[] args) throws Exception{
		fillTemplatePDF();
		//addBarcode();
	}

	 /** 模板文件*/
	 public static final String templateFile = "F:/t1.pdf";
	 /** 结果文件 */
	 public static final String resultFile = "F:/tt1.PDF";
	 
	public static void fillTemplatePDF(/*String templateFile, String outFile*/)
			throws IOException, DocumentException 
	{
		PdfReader reader = new PdfReader(templateFile); // 模版文件目录
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(resultFile)); // 生成的输出流
		AcroFields form =  stamper.getAcroFields();
		
		fill(form);
		
		/*  
		Barcode128 barCode = new Barcode128();
		barCode.setCode("SHA201204A7073");
		*/
		//Barcode39生成图片
		PdfContentByte over = stamper.getOverContent(1);//设置在第几页打印印章
		Barcode39 barCode = new Barcode39();
		barCode.setCode("1234567890");//设置条形码的字符串参数
		barCode.setStartStopText(false);//去掉数字前后的符号（若不去掉则会在生成的条形码前后带一个*）
		Image img = barCode.createImageWithBarcode(over, null, null);//将条形码生成一个图片
		//插入图片
		img.setAlignment(1);
		img.scaleAbsolute(120,60);//控制图片大小，第一个参数是宽，第二个是高
		img.setAbsolutePosition(400,700);//控制图片位置，左下角是坐标原点
		over.addImage(img);

	    //水印
		Image gif = Image.getInstance("F:/1.png");
		gif.setDpi(100, 100);
		gif.setBorderWidth(200);
		gif.scaleAbsolute(80, 100); 
		gif.setAbsolutePosition(100, 100);
		over.addImage(gif);
		stamper.setFormFlattening(true); // 这句不能少
		
		
		stamper.close();
		reader.close();
	}
	
	/**
	  * 将模板中的表单字段赋值
	  * @param form
	  * @throws IOException
	  * @throws DocumentException
	  */
	 public static void fill(AcroFields form) throws IOException, DocumentException 
	 {
		//设置文本域表单的字体
		 BaseFont bf = BaseFont.createFont("STSong-Light","UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
		 form.setFieldProperty("name","textfont",bf,null);//"name"是PDF模板上可编辑字段的ID
		//编辑文本域表单的内容
		 form.setField("name", "中国人民银行 ");
	 }
	 
	
	
	
	/*public static void addBarcode() throws FileNotFoundException, DocumentException
	{
		 
		//=========添加条形码begin===================
		Document document = new Document(PageSize.A4,0,0,0,0);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(RESULT));
		document.open();
		
		PdfContentByte cd = writer.getDirectContent();
		
		Barcode128 code128 = new Barcode128();
		code128.setCode("SHA201204A7073");
		Image image128 = code128.createImageWithBarcode(cd, null, null);
	
		//新建列
		PdfPCell barcodeCell = new PdfPCell(image128);
		barcodeCell.setColspan(2); //垮2列
		barcodeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		barcodeCell.setBorder(PdfPCell.NO_BORDER);
	
		//下部分的表格
		PdfPTable underTable = new PdfPTable(2);
		underTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		underTable.addCell(barcodeCell);
		
		document.add(underTable);
		//=========添加条形码end===================
		document.close();
	}*/
}
