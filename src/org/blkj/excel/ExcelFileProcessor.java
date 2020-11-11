package org.blkj.excel;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.blkj.utils.FileTool;

import blkjweb.utils.Const;


/**
 * Excel FileManager
 */
public class ExcelFileProcessor {
	
	public String INDETERMINATE = "INVALID_COLUMN";

    private String fileName;

    private ArrayList<String> worksheets = new ArrayList<String>();

    private int noOfRows;//不含表头,即第1行外的总行数，即excel表含有的实际记录数

    private Sheet curSheet;

    private int row = -1;

    private Row curRow;//从excel表中读取出来的当前行（相当于一条记录）

    private Workbook wkbook = null;
   
    /** Creates a new instance of ExcelFileManager */
    public ExcelFileProcessor() {  }
  
    /**  
     * @param fileName 文件路径  
     * @param flag  true：2003，false：2007  
     * @throws Exception  
     */  
    public boolean loadFile(String fileName,boolean flag) throws Exception 
    {   
    	//Log.debug(fileName +"//"+flag);
    	resetFile();
        this.fileName = fileName;
        /** 检查文件是否存在 是否可读等*/
		File f = new File( this.fileName );
			
        if (f == null || !f.exists() || !f.isFile() || !f.canRead() )
        {
            return false;
        }
		FileInputStream is = new FileInputStream(f);
        //2003 true;; 2007 false
		//wkbook = flag? new HSSFWorkbook(is) : new XSSFWorkbook(is);
		if(flag)
			wkbook = new HSSFWorkbook(is);
		else
			wkbook =  new XSSFWorkbook(is);	
	
		is.close();
		
    	read(wkbook);

    	return true;
    }   
    /** 
     * Read in the file 
     * 将一个XLS文件中每个sheet的名字保存worksheets中
     */
    private void read(Workbook wkbook) throws Exception {

        int noOfWorksheets = wkbook.getNumberOfSheets();//get the number of spreadsheets in the workbook
        if (noOfWorksheets != 0) {
            for (int count = 0; count < noOfWorksheets; ++count) {
                worksheets.add(wkbook.getSheetName(count));//get the sheet name
            }
        }
    }

    private void resetFile() {
        fileName = null;
        worksheets = new ArrayList<String>();
        noOfRows = 0;
        curSheet = null;
        row = -1;
        curRow = null;
    }
    
    /**
     * Load worksheet.
     */
    public void loadWorksheet(int index) throws Exception {
    	
    	//Log.debug("loadWorksheet_01");
    	resetSheet();
    	
    	//Log.debug("loadWorksheet_02");
    	//获得第一个工作表对象
        curSheet = wkbook.getSheetAt(index);//Get the HSSFSheet object at the given index.
       // Log.debug("loadWorksheet_03");
        
        //不含表头,即第1行外的总行数，即excel表含有的实际记录数
        noOfRows = curSheet.getLastRowNum();//Gets the number last row 行数 on the sheet.
        //Log.debug("loadWorksheet_04:" + noOfRows);
    }

    private void resetSheet() {
        noOfRows = 0;
        curSheet = null;
        row = -1;
        curRow = null;
    }
    
    /**
     * return worksheets
     */
    public Collection<String> getWorksheets() {
        return worksheets;
    }
    
    /**
     * Move cursor to the next row return true if Data exists.
     */
    public boolean next() {
        if (row + 1 > noOfRows)
            return false;
        Row record = curSheet.getRow(++row);
        curRow = record;
        return true;
    }
    
    /**
     * return Next row of the data, If called after next().
     *从excel表中获取下一行的内容
     */
    //getColumnIndex() 替换原来的getCellNum()
    public Vector<String> getNextRow() {//不同行的数据
        Vector<String> objects = new Vector<String>();
        if (curRow != null && curRow.getLastCellNum() != -1) {
        	//Log.debug(row +" "+curRow.getLastCellNum());
        	objects.setSize(curRow.getLastCellNum());//以getLastRowNum结束
        	
        	//循环Excel的行
        	Iterator<?> itr = curRow.cellIterator();
        	while (itr.hasNext()) {
        		Cell cell = (Cell)itr.next();
        		
        		int type = cell.getCellType();
        		//Log.debug("Type="+ type);
        		
        		switch(type)
        		{
        		case Cell.CELL_TYPE_STRING://处理字符串型  1
        			//Log.debug(row +" "+ cell.getCellNum() + " "+ objects.size());
        			objects.remove(cell.getColumnIndex());
        			String str = cell.getRichStringCellValue().getString();

        			//Log.debug("原始值:" + str);
        			
        			//2010-11-1修订
        			//去除纯数字字符串中的空格、回车、换行符、制表符 及字符串前的数字0
        			//str = FileUtil.trimBlankZero(str);
        			//去除字符串前后的空格
        			str = FileTool.trimBlank(str);
        		    
        			//Log.debug("处理后的值_1:" + str);
        			
        			objects.add(cell.getColumnIndex(),str);
        			break;
        		case Cell.CELL_TYPE_NUMERIC:// 处理数字型的 0
        			if (HSSFDateUtil.isCellDateFormatted(cell)) /** 在excel里,日期也是数字,在此要进行判定 */
        			{
        				/*SimpleDateFormat sdf = new SimpleDateFormat(conf.DATE_FORMAT);
                        String dt = sdf.format(cell.getDateCellValue());
                        objects.remove(cell.getColumnIndex());
                        objects.add(cell.getColumnIndex(), dt);
        				 */ 
        				// cell type numeric.
        				objects.remove(cell.getColumnIndex());
        				
        				objects.add(cell.getColumnIndex(),String.valueOf(cell.getNumericCellValue()));
        			}
        			else
        			{//getNumericCellValue()会回传double值，若不希望出现小数点，请自行转型为int
        				objects.remove(cell.getColumnIndex());
        				
        				 //double值
        				String tmpValue = String.valueOf(cell.getNumericCellValue());
        				/*
        				Double dValue = cell.getNumericCellValue();
						int iValue = (new Double(dValue)).intValue();
						String tmpValue = String.valueOf(iValue);
						Log.debug("xbm_003:" + tmpValue);
						*/
        				if (Const.readNumberAsText) 
        				{
        					//2011用DecimalFormat对这个double进行格式化，随后使用format方法获得的String。就是你想要的String值
            				DecimalFormat df = new DecimalFormat("0");
            				tmpValue = df.format(cell.getNumericCellValue());
        					/*if ((int) cell.getNumericCellValue() == cell.getNumericCellValue()) 
        					{
        						tmpValue = String.valueOf((int) cell.getNumericCellValue());
        					}*/
        				}
        				
        				objects.add(cell.getColumnIndex(), tmpValue);
        			}

        			break;
        		case Cell.CELL_TYPE_FORMULA://Formula 公式, 方程式 2
                  	//String.valueOf(cell.getNumericCellValue()); 
                  	break;
        		case Cell.CELL_TYPE_BLANK:// 空值  3
        			objects.remove(cell.getColumnIndex());
        			objects.add(cell.getColumnIndex(), null);
        			break;
        		case Cell.CELL_TYPE_BOOLEAN:// Boolean 4
        			//Boolean.valueOf(cell.getBooleanCellValue()).toString();
        			break;
                case Cell.CELL_TYPE_ERROR:  // 故障 5
                	break;
            	default:
        			objects.remove(cell.getColumnIndex());
                	objects.add(cell.getColumnIndex(), INDETERMINATE);
                	break;
        		}
            }
        }
        return objects;
    }
 

}