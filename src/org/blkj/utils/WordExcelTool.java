package org.blkj.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class WordExcelTool {
	
	//Excel替换
	public static XSSFWorkbook replaceModel(String sourceFilePath, List<Map<String, Object>> lists) {
		XSSFWorkbook wb = null;

		try {
			Map<Integer, String> map = new HashMap<>();// 存储key值对应的列
			// POIFSFileSystem fs =new POIFSFileSystem(new FileInputStream(sourceFilePath));
			wb = new XSSFWorkbook(new FileInputStream(sourceFilePath));

			// 样式设置
			XSSFCellStyle cellStyle = wb.createCellStyle();
			XSSFFont font = wb.createFont();
			font.setFontName("楷体");
			font.setFontHeightInPoints((short) 16);// 设置字体大小
			cellStyle.setWrapText(true);
			cellStyle.setFont(font);

			XSSFSheet sheet = wb.getSheetAt(0);
			int column = 0;// 存储列数
			XSSFRow row = sheet.getRow(1);
			if (row != null) {
				column = row.getLastCellNum();
				for (int i = 0; i < column; i++) {
					XSSFCell cell = row.getCell(i);
					String cellValue = cell.getStringCellValue();
					map.put(i, cellValue);
				}
			}
			int listSize = lists.size();
			for (int i = 0; i < listSize; i++) {
				Map<String, Object> item = lists.get(i);
				row = sheet.createRow(i + 1);
				for (int j = 0; j < column; j++) {
					XSSFCell cell = row.createCell(j);
					Object value = item.get(map.get(j));
					cell.setCellStyle(cellStyle);// 设置样式
					cell.setCellValue(ConvertTool.Object2String(value));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wb;
	}
	
	
	
	//Word替换
	public static XWPFDocument readwriteWord(String sourceFilePath, Map<String, Object> map) throws IOException {
		 String regular = "";
	        if(map.containsKey("regular")){
	        	regular = (String) map.get("regular");
	        	map.remove("regular");
	        }

		FileInputStream in = null;
		XWPFDocument hdt = null;
		try {
			in = new FileInputStream(new File(sourceFilePath));
			hdt = new XWPFDocument(in);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		hdt=replaceCommonIntoSpaceInWord(hdt,map);
		
		replaceRegularIntoSpaceInWord(hdt,regular);
		//in.close();
		return hdt;
	}
	static XWPFDocument  replaceCommonIntoSpaceInWord(XWPFDocument hdt,Map<String, Object> map){
		
		List<XWPFTable> tables = hdt.getTables();
		for (XWPFTable table : tables) {
			int table_rows = table.getNumberOfRows();
			for (int i = 0; i < table_rows; i++) {
				XWPFTableRow row = table.getRow(i);
				List<XWPFTableCell> cells = row.getTableCells();
				for (XWPFTableCell cell : cells) {
					List<XWPFParagraph> paras = cell.getParagraphs();
					if (paras.size() != 1)
						continue;
					XWPFParagraph para = paras.get(0);
					String value = para.getText();
					Set<String> keySet = map.keySet();
					Iterator<String> it = keySet.iterator();
					while (it.hasNext()) {
						String text = it.next();
						if (text == null)
							break;
						if (value.equalsIgnoreCase(text)) {
							cell.removeParagraph(0);

							cell.setText(map.get(text).toString());
							break;
						}
					}
				}
			}
		}
		return hdt;
	}

	static XWPFDocument  replaceRegularIntoSpaceInWord(XWPFDocument hdt,String regular){
		if(null!=regular&&regular.length()>0){
			List<XWPFTable> tables = hdt.getTables();
	        for(XWPFTable table:tables){
	        	int table_rows = table.getNumberOfRows();
	        	for(int i=0;i<table_rows;i++){
	        		XWPFTableRow row = table.getRow(i);
	        		List<XWPFTableCell> cells = row.getTableCells();
	        		for(XWPFTableCell cell:cells){
	    	        	List<XWPFParagraph> paras = cell.getParagraphs();
	    	        	if(paras.size()!=1)
	    	        		continue;
	    	        	XWPFParagraph para = paras.get(0);
	            		String value = para.getText();
	            		//System.out.println(regular+"  "+value);
	            		
	            		if(Pattern.matches(regular, value)){
	            			cell.removeParagraph(0);

							cell.setText(" ");
	            		//	para.removeRun(0);
                         //   XWPFRun run2 = para.createRun();
                        //    run2.setText("");
                         //   run2.setFontFamily(fontFamily);
                        //    run2.setFontSize(fontSize);		            		
	            	
                        }
	        		}
	        	}
	        		
	        }
		
   }
		return hdt;
}
}