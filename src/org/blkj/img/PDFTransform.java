package org.blkj.img;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageOutputStream;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.PDimension;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

public class PDFTransform {


  public static final double PRINTER_RESOLUTION = 300.0; 
  public static final String COMPRESSION_TYPE = "CCITT T.6";
 

  /**
   * create by sbt
   * 方法说明：
   * PDF转换成jpg格式，单页转换
   * oPdfPath:源pdf地址
   * oPdfName：源pdf名称
   * ohterPath：生产目标pdf地址
   * 返回生产文件的名称
   */
  private ArrayList<String> icepdf_PDFtojpg(String oPdfPath,String oPdfName,String ohterPath){
	   ArrayList<String> mBackList = new ArrayList<String>();
    try {
    	 String efilesPath_Name  = oPdfPath + oPdfName ;
    	  Document document = null;
    	  float rotation = 0f;  
    	       document = new Document();
    	       document.setFile(efilesPath_Name);
	     int  maxPages = document.getNumberOfPages();
         String NewJpePagesName = "" ;
         if(oPdfName.indexOf(".")>-1){
        	NewJpePagesName = oPdfName.substring(0, oPdfName.indexOf("."));
         }else{
         	NewJpePagesName = oPdfName ;
         }
    	    System.out.println("====共处理"+maxPages);
    	    for(int i =0 ;i<maxPages;i++){
          	  BufferedImage img = (BufferedImage) document.getPageImage
        			  (i, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, rotation,2.5f);   // 第几页 ； 高度 ；页面的 ； 倾斜度 ； 放大缩小
          	         
        	     Iterator iter = ImageIO.getImageWritersBySuffix("jpg");
        	      int j = i +1 ;
            	  ImageWriter writer = (ImageWriter) iter.next();
            	  String tempPGIE  = NewJpePagesName+"_"+j+"_.jpg";
            	  File outFile = new File(ohterPath+tempPGIE);
            	  if (!outFile.getParentFile().exists()) {
            		  outFile.getParentFile().mkdirs();
        	      }
            	  FileOutputStream out = new FileOutputStream(outFile);
            	  ImageOutputStream outImage = ImageIO.createImageOutputStream(out);
            	  writer.setOutput(outImage);
            	  writer.write(new IIOImage(img, null, null)); 	
            	  img.flush();
            	 mBackList.add(tempPGIE);
    	    }
    	    document.dispose();
      } catch (Exception ex) {
        ex.printStackTrace();
     }
     return mBackList ;
  }
	
 
  /**
   * 
   * 方法说明：转换成单个tif
   * PDF转换成tif格式，单页转换
   * filePath 源文件路径
   * tfilePath 目标文件夹
   * 返回boolen  true or false 
 * @throws InterruptedException 
   */ 

  private boolean icepdf_PDFtotif_single(String filePath ,String filename ,String tfilePath) throws InterruptedException{
	    Document document = new Document(); 
	    System.out.println("===开始转换文件："+filename+"  开始时间："+this.getCurrentDate("yyyy/MM/dd/-HH:mm:ss"));
	   try { 
	        document.setFile(filePath); 
	      } catch (PDFException ex) { 
	        System.out.println("Error parsing PDF document " + ex); 
	      } catch (PDFSecurityException ex) { 
	        System.out.println("Error encryption not supported " + ex); 
	      } catch (FileNotFoundException ex) { 
	        System.out.println("Error file not found " + ex); 
	      } catch (IOException ex) { 
	       System.out.println("Error handling PDF document " + ex); 
	    } 
	   try { 
		   if(filename.indexOf(".")>-1){
			   filename = filename.substring(0, filename.indexOf("."));
	         }else{
	        	 filename = filename ;
	         }
	     for (int i = 0; i < document.getNumberOfPages(); i++) { 
	    	int j = i+1;
		     String NewJpePagesName = filename +"_"+j+"_.tif";
		     File file = new File(tfilePath+NewJpePagesName); 
		     if (!file.getParentFile().exists()) {
		    	 file.getParentFile().mkdirs();
		      }
		     System.out.println("Output file location " + file); 
		     ImageOutputStream ios = ImageIO.createImageOutputStream(file); 
		     ImageWriter writer = ImageIO.getImageWritersByFormatName("tiff").next(); 
		     writer.setOutput(ios); 
	    	
	     final double targetDPI = PRINTER_RESOLUTION; 
	     float scale = 1.0f; 
	     float rotation = 0f; 
	     PDimension size = document.getPageDimension(i, rotation, scale); 
	     double dpi = Math.sqrt((size.getWidth()*size.getWidth()) + (size.getHeight()*size.getHeight()) ) /Math.sqrt((8.5*8.5)+(11*11)); 
	     if (dpi < (targetDPI-0.1)) 
	     { 
	       scale = (float) (targetDPI / dpi); 
	       size = document.getPageDimension(i, rotation, scale); 
	     } 
		 int pageWidth = (int) size.getWidth(); 
		 int pageHeight = (int) size.getHeight(); 
		 int[] cmap = new int[] { 0xFF000000, 0xFFFFFFFF }; 
		 IndexColorModel cm = new IndexColorModel(1, cmap.length, cmap, 0, false, Transparency.OPAQUE,DataBuffer.TYPE_BYTE); 
		 BufferedImage image = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_BYTE_BINARY, cm); 
		 Graphics g = image.createGraphics(); 
		 document.paintPage(i, g, GraphicsRenderingHints.PRINT, Page.BOUNDARY_CROPBOX,rotation, scale); 
		 g.dispose(); 
		 IIOImage img = new IIOImage(image, null, null); 
		 ImageWriteParam param = writer.getDefaultWriteParam(); 
		 param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); 
		 param.setCompressionType(COMPRESSION_TYPE); 
		 writer.write(null, img, param); 
		   image.flush(); 
			 ios.flush(); 
			 ios.close(); 
			 writer.dispose(); 
			 System.out.println("PDF converted to Tiff successfully with compression type " + COMPRESSION_TYPE + NewJpePagesName); 
		 }
	    System.out.println("===结束转换文件："+filename+"  结束时间："+this.getCurrentDate("yyyy/MM/dd/-HH:mm:ss"));
	   } 
		 catch(IOException e) { 
		  System.out.println("Error saving file " + e); 
		  e.printStackTrace(); 
		  return false;
		 } 
		 document.dispose(); 
		return true;
 }
  
  /**
   * 
   * 方法说明：转换集合个tif
   * PDF转换成tif格式，单页转换
   * filePath 源文件路径
   * tfilePath 目标文件夹
   * 返回boolen  true or false 
 * @throws InterruptedException 
   */ 

  private boolean icepdf_PDFtotif_join(String filePath , String filename,String tfilePath) throws InterruptedException{
	    Document document = new Document(); 
	    System.out.println("===开始转换文件："+filename+"  开始时间："+this.getCurrentDate("yyyy/MM/dd/-HH:mm:ss"));
	   try { 
	        document.setFile(filePath); 
	      } catch (PDFException ex) { 
	        System.out.println("Error parsing PDF document " + ex); 
	      } catch (PDFSecurityException ex) { 
	        System.out.println("Error encryption not supported " + ex); 
	      } catch (FileNotFoundException ex) { 
	        System.out.println("Error file not found " + ex); 
	      } catch (IOException ex) { 
	       System.out.println("Error handling PDF document " + ex); 
	    } 
	   try { 
		   if(filename.indexOf(".")>-1){
			   filename = filename.substring(0, filename.indexOf("."));
	         }else{
	        	 filename = filename ;
	         }
		   String NewJpePagesName = filename+"_"+this.getCurrentDate("yyyyMMssHHmmss") +".tif";
	     File file = new File(tfilePath+NewJpePagesName); 
	     if (!file.getParentFile().exists()) {
	    	 file.getParentFile().mkdirs();
	      }
	     System.out.println("Output file location " + file); 
	     ImageOutputStream ios = ImageIO.createImageOutputStream(file); 
	     ImageWriter writer = ImageIO.getImageWritersByFormatName("tiff").next(); 
	     writer.setOutput(ios); 
	   
	    for (int i = 0; i < document.getNumberOfPages(); i++) { 
	     final double targetDPI = PRINTER_RESOLUTION; 
	     float scale = 1.0f; 
	     float rotation = 0f; 
	     PDimension size = document.getPageDimension(i, rotation, scale); 
	     double dpi = Math.sqrt((size.getWidth()*size.getWidth()) + (size.getHeight()*size.getHeight()) ) /Math.sqrt((8.5*8.5)+(11*11)); 
	     if (dpi < (targetDPI-0.1)) 
	     { 
	       scale = (float) (targetDPI / dpi); 
	       size = document.getPageDimension(i, rotation, scale); 
	     } 
		 int pageWidth = (int) size.getWidth(); 
		 int pageHeight = (int) size.getHeight(); 
		 int[] cmap = new int[] { 0xFF000000, 0xFFFFFFFF }; 
		 IndexColorModel cm = new IndexColorModel(1, cmap.length, cmap, 0, false, Transparency.OPAQUE,DataBuffer.TYPE_BYTE); 
		 BufferedImage image = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_BYTE_BINARY, cm); 
		 Graphics g = image.createGraphics(); 
		 document.paintPage(i, g, GraphicsRenderingHints.PRINT, Page.BOUNDARY_CROPBOX,rotation, scale); 
		 g.dispose(); 
		 IIOImage img = new IIOImage(image, null, null); 
		 ImageWriteParam param = writer.getDefaultWriteParam(); 
		 param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); 
		 param.setCompressionType(COMPRESSION_TYPE); 
		if (i == 0) 
		 { 
		    writer.write(null, img, param); 
		 } else { 
		   writer.writeInsert(-1, img, param); 
		 } 
		 image.flush(); 
		 } 
	    
		 ios.flush(); 
		 ios.close(); 
		 writer.dispose(); 
		 System.out.println("PDF converted to Tiff successfully with compression type " + COMPRESSION_TYPE ); 
		 System.out.println("===结束转换文件："+filename+"  结束时间："+this.getCurrentDate("yyyy/MM/dd/-HH:mm:ss"));
	   }
		 catch(IOException e) { 
		  System.out.println("Error saving file " + e); 
		  e.printStackTrace(); 
		  return false;
		 } 
		 document.dispose(); 
		return true;
 }  
  
  public String getCurrentDate(String pattern)
  {
      SimpleDateFormat df = new SimpleDateFormat(pattern);
      Date today = new Date();
      String tString = df.format(today);
      return tString;
  }

	  public static void main(String arr[]) throws Exception{
		  PDFTransform t = new PDFTransform();
		  String toPdfPath ="G:/test/0814r/";
		  String topdfName="r4.pdf";
		  String tohterPath ="G:/test/0814t/";
	      boolean  t2 = t.icepdf_PDFtotif_single(toPdfPath+topdfName ,topdfName, tohterPath);
	  }

	  	  
}
