package blkjweb.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
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
import org.blkj.utils.ConvertTool;
import org.blkj.utils.PageTool;
import org.blkj.utils.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import blkjweb.service.SystemServiceImp;
import blkjweb.utils.Console;
import blkjweb.utils.Const;

public class PrintCtrl2 extends AbstractBase {

	@Resource
	private SystemServiceImp systemService;

	@RequestMapping(value = "/printPage_duntai")
	@ResponseBody // 作用是将返回的对象作为响应，发送给页面
	public void printPage_duntai() {
		
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		
		HttpServletResponse response = this.getResponse();
		response.setContentType("text/html;charset=UTF-8");
		String contentType = "application/octet-stream";
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		
		try {
			// request.setCharacterEncoding("UTF-8");
			long fileLength = new File("").length();
			response.setContentType(contentType);
			response.setHeader("Content-disposition", "attachment; filename=" + ConvertTool.getISO(""));
			response.setHeader("Content-Length", String.valueOf(fileLength));

			bis = new BufferedInputStream(new FileInputStream(""));
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
		} catch (Exception e) {
			Console.showMessage(AbstractBase.class.getSimpleName(), e.getMessage(), e);
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				Console.showMessage(AbstractBase.class.getSimpleName(), e.getMessage(), e);
			}
		}
		

		String jsonObj = pd.getString("JsonObjParam");// {名：值，。。。}
		mapRecord = ConvertTool.json2Map(jsonObj);
		String tableName = mapRecord.get("tableName").toString();
		mapRecord = systemService.queryOne(mapRecord, tableName);
		mapRecord = ConvertTool.removeNull(ConvertTool.json2Map(ConvertTool.map2json(mapRecord)));
		int tfid = 1;
		String templatePath = Const.getTemplatePath() + Const.separator;
		String fileName = tfid + ".docx";
		XWPFDocument hdt = null;
		FileOutputStream fileOut = null;
		String tempname = tfid + "-temp.docx";
		try {
			hdt = readwriteWord(fileName, mapRecord);

			fileOut = new FileOutputStream(templatePath + tempname);
			hdt.write(fileOut);
			hdt.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		printToClient(templatePath, tempname);
	}

	XWPFDocument readwriteWord(String sourceFilePath, Map<String, Object> map) throws IOException {
		FileInputStream in = null;
		String Path = Const.getTemplatePath() + Const.separator;
		try {
			in = new FileInputStream(new File(Path + sourceFilePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		XWPFDocument hdt = null;
		try {
			hdt = new XWPFDocument(in);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
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

	@RequestMapping(value = "/printPage_list")
	@ResponseBody // 作用是将返回的对象作为响应，发送给页面
	public void printPage_list() {
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String jsonObj = pd.getString("JsonObjParam");
		mapRecord = ConvertTool.json2Map(jsonObj);
		String tableName = mapRecord.get("tableName").toString();
//		mapRecord = systemService.queryOne(mapRecord,tableName); 
//		mapRecord =ConvertTool.removeNull(ConvertTool.json2Map(ConvertTool.map2json(mapRecord)));
		List<Map<String, Object>> listMapRecord = new ArrayList<Map<String, Object>>();

		listMapRecord = systemService.query(tableName, "");
		String tfid = "qiao";
		String templatePath = Const.getTemplatePath() + Const.separator;
		String fileName = tfid + ".xlsx";
		XSSFWorkbook wb = null;
		FileOutputStream fileOut = null;
		String tempname = tfid + "-temp.xlsx";
		try {
			wb = replaceModel(fileName, listMapRecord);
			fileOut = new FileOutputStream(templatePath + tempname);
			wb.write(fileOut);
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		printToClient(templatePath, tempname);
	}

	XSSFWorkbook replaceModel(String sourceFilePath, List<Map<String, Object>> lists) {
		XSSFWorkbook wb = null;

		String Path = Const.getTemplatePath() + Const.separator;
		try {
			Map<Integer, String> map = new HashMap<>();// 存储key值对应的列
			// POIFSFileSystem fs =new POIFSFileSystem(new FileInputStream(sourceFilePath));
			wb = new XSSFWorkbook(new FileInputStream(Path + sourceFilePath)); 
			XSSFCellStyle cellStyle = wb.createCellStyle();
			XSSFFont font = wb.createFont();  
			font.setFontName("黑体");  
			font.setFontHeightInPoints((short) 16);//设置字体大小   
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
					cell.setCellStyle(cellStyle);
					cell.setCellValue(ConvertTool.Object2String(value));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return wb;
	}
}