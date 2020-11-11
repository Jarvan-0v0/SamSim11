package org.blkj.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.*;
import javax.media.jai.JAI;  
import javax.media.jai.RenderedOp;  

import com.sun.media.jai.codec.BMPEncodeParam;  
import com.sun.media.jai.codec.ImageCodec;  
import com.sun.media.jai.codec.ImageEncoder;  
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;

public class ImageFormatConversion 
{
	public static final String JPG = "jpg";
	public static final String GIF = "gif";
	public static final String PNG = "png";
	public static final String BMP = "bmp";
	public static final String TIF = "tif";
	//"jpg","JPG","bmp","png","tif","gif"

	/**
	 * 将jpg格式转化为tif格式
	 * 
	 * @param srcFile
	 *            需要装换的源文件
	 * @param descFile
	 *            装换后的转存文件
	 * @throws Exception
	 */
	public void jpg2tif(String srcFile, String descFile) throws Exception 
	{
		RenderedOp src = JAI.create("fileload", srcFile);
		OutputStream os = new FileOutputStream(descFile);
		TIFFEncodeParam param = new TIFFEncodeParam();
		ImageEncoder encoder = ImageCodec.createImageEncoder("TIFF", os, param);
		encoder.encode(src);
		os.close();
	}
	/* tif转换到jpg格式 */
	public void tif2jpg(String srcFile, String descFile) throws Exception {
		RenderedOp src2 = JAI.create("fileload", srcFile);
		OutputStream os2 = new FileOutputStream(descFile);
		JPEGEncodeParam param2 = new JPEGEncodeParam();
		//指定格式类型，jpg 属于 JPEG 类型
		ImageEncoder enc2 = ImageCodec.createImageEncoder("JPEG", os2, param2);
		enc2.encode(src2);
		os2.close();
	}


	//inputFormat表示原格式，outputFormat表示转化后的格式
	//不支持jpg 到tif的转换
	public void Conversion(String inputFormat,String outputFormat,String src)
	{
		try {
			File input = new File(src + "." + inputFormat);
			
			// 读文件src并对其进行解码，得到内存图象bim
			BufferedImage bim = ImageIO.read(input);

			File output = new File(src  + "." + outputFormat);
			// 把内存图象bim按照给定格式写入dest文件中去
			ImageIO.write(bim, outputFormat, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		String src = "F:\\tt";
		//new ImageFormatConversion().Conversion(BMP,JPG,src);//JPG转成PNG
		//new ImageFormatConversion().Conversion(JPG,BMP,src);//JPG转成GIF
		//new ImageFormatConversion().Conversion(GIF,JPG,src);//JPG转成GIF
		try {
			//new ImageFormatConversion().jpg2tif(src+".jpg",src +".tif");
			new ImageFormatConversion().jpg2tif(src+".tif",src +".jpg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//其余格式转化只要调用Conversion函数即可
	}

}
