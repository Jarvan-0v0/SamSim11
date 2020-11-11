package org.blkj.text;

import java.io.File;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.OutputStream;  

import org.blkj.text.PdfFileExportUtil.HeadFootInfoPdfPageEvent;
import org.blkj.text.PdfFileExportUtil.PictureWaterMarkPdfPageEvent;
import org.blkj.text.PdfFileExportUtil.TextWaterMarkPdfPageEvent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

  
public class PdfFileExport {  
      
    /** 
     * 从数据库中导出数据并以PDF文件形式存储 
     * 列信息较多，行信息可能超过100万 
     * 文档仅有只读权限，设置文档作者信息 
     * 在文档页头设置公司信息版权信息 
     * 添加公司的文字和图片水印信息 
     * @param fullFilePath 
     * @param tableContent 
     * @param rowsNumber 
     * @param submitAmount 
     * @return 
     */  
    public boolean exportTableContent(String fullFilePath,String[][] tableContent,int rowsNumber,int submitAmount)
    {  
        Document pdfDocument = new Document(PageSize.A2,50,50,50,50);
      
        try {  
            //构建一个PDF文档输出流程  
            OutputStream pdfFileOutputStream = new FileOutputStream(new File(fullFilePath));  
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, pdfFileOutputStream);  
            
            //设置作者信息  
            PdfFileExportUtil.setCreatorInfo(pdfDocument);  
            
            //设置文件只读权限  
            PdfFileExportUtil.setReadOnlyPDFFile(pdfWriter);  
            
            //通过PDF页面事件模式添加文字水印功能  
            PdfFileExportUtil pdfFileExportUtil = new PdfFileExportUtil();  
            pdfWriter.setPageEvent(pdfFileExportUtil.new TextWaterMarkPdfPageEvent("杭州水果大王信息技术"));  
           
            //通过PDF页面事件模式添加图片水印功能  
            String waterMarkFullFilePath = "F:/1.png";//水印图片  
            pdfWriter.setPageEvent(pdfFileExportUtil.new PictureWaterMarkPdfPageEvent(waterMarkFullFilePath));  
            
            //通过PDF页面事件模式添加页头和页脚信息功能  
            pdfWriter.setPageEvent(pdfFileExportUtil.new HeadFootInfoPdfPageEvent());  
            
            //设置中文字体和字体样式  
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);    
            Font f8 = new Font(bfChinese, 8, Font.NORMAL);  
            
            //打开PDF文件流  
            pdfDocument.open();  
            //创建一个N列的表格控件  
            PdfPTable pdfTable = new PdfPTable(tableContent[0].length);  
            //设置表格占PDF文档100%宽度  
            pdfTable.setWidthPercentage(100);  
            //水平方向表格控件左对齐  
            pdfTable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);  
            //创建一个表格的表头单元格  
            PdfPCell pdfTableHeaderCell = new PdfPCell();  
            //设置表格的表头单元格颜色  
            pdfTableHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);  
            pdfTableHeaderCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);  
            for(String tableHeaderInfo : tableContent[0]){  
                pdfTableHeaderCell.setPhrase(new Paragraph(tableHeaderInfo, f8));  
                pdfTable.addCell(pdfTableHeaderCell);  
            }  
            //创建一个表格的正文内容单元格  
            PdfPCell pdfTableContentCell = new PdfPCell();  
            pdfTableContentCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);  
            pdfTableContentCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);  
            //表格内容行数的填充  
            for(int i = 0;i < rowsNumber;i++){  
                for(String tableContentInfo : tableContent[1]){  
                    pdfTableContentCell.setPhrase(new Paragraph(tableContentInfo, f8));  
                    pdfTable.addCell(pdfTableContentCell);  
                }  
                //表格内容每写满某个数字的行数时，其内容一方面写入物理文件，另一方面释放内存中存留的内容。  
                if((i%submitAmount)==0){  
                    pdfDocument.add(pdfTable);  
                    pdfTable.deleteBodyRows();  
                }
                else if(i==rowsNumber){  
                    //如果全部类容完毕且又没达到某个行数限制，则也要写入物理文件中。  
                    pdfDocument.add(pdfTable);  
                    pdfTable.deleteBodyRows();  
                }  
            }  
            return true;  
        }catch(FileNotFoundException de) {  
            de.printStackTrace();  
            System.err.println("pdf file: " + de.getMessage());  
            return false;  
        }catch(DocumentException de) {  
            de.printStackTrace();  
            System.err.println("document: " + de.getMessage());  
            return false;  
        }catch(IOException de) {  
            de.printStackTrace();  
            System.err.println("pdf font: " + de.getMessage());  
            return false;  
        }finally{  
            //关闭PDF文档流，OutputStream文件输出流也将在PDF文档流关闭方法内部关闭  
            if(pdfDocument!=null){  
                pdfDocument.close();  
            }  
        }          
    }  
      
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        String[][] tableContent = new String[][]{//页眉和内容  
                {"序号","姓名","年龄","职业","籍贯","学历","单位名称","联系电话","联系地址","语文","数学","英语","物理","化学","生物","政治","历史","地理","音乐","美术","体育","课外实践","学校名称","备注"},
                {"1","许果","31","软件工程师","浙江杭州","大学本科","浙江水果大王信息技术有限公司","18905710571","浙江省杭州市西湖区三墩镇三墩街188号","85","95","75","90","90","85","80","90","90","75","65","75","80","石城中学","他是一名优秀的IT工程师，日常爱好旅游，运动"}  
        };
        PdfFileExport pdfFileExport = new PdfFileExport();  
        pdfFileExport.exportTableContent("F:/PDF文档.pdf", tableContent, 100000, 2000);  
    }  
      
}  