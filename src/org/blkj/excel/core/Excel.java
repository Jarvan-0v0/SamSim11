package org.blkj.excel.core;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

public class Excel {


	/**
	 * <p>单元格格式为日期时，获取指定形式的日期格式</p>
	 * @param cell
	 * @return The value
	 */
	public String getExcelDateTime(Cell cell) {
		if (isBlank(cell))
			return null;
		double d =cell.getNumericCellValue();
		Date date = HSSFDateUtil.getJavaDate(d);
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
		String s_datetime = sFormat.format(date);
		return s_datetime;
	}
	/**
	 * <p>空判断</p>
	 * @param cell
	 * @return The value
	 */
	public boolean isBlank(Cell cell) {
		if (null == cell)
			return true;
		int cellType =cell.getCellType();
		if (Cell.CELL_TYPE_BLANK == cellType) {
			return true;
		}
		return false;
	}
	/**
	 * <p>取得整型格式</p>
	 * @param cell
	 * @return The value
	 */
	public String getExcelInt(Cell cell) {
		if (isBlank(cell))
			return null;
		return (int)cell.getNumericCellValue() + "";
	}
	/**
	 * <p>取得长整型格式</p>
	 * @param cell
	 * @return The value
	 */
	public String getExcelLong(Cell cell) {
		if (isBlank(cell))
			return null;
		return (long)cell.getNumericCellValue() + "";
	}
	/**
	 * <p>取得长整型格式</p>
	 * @param cell
	 * @return The value
	 */
	public String getExcelDouble(Cell cell) {
		if (isBlank(cell))
			return null;
		return cell.getNumericCellValue() + "";
	}
	/**
	 * <p>取得字符串形式
	 * @param cell
	 * @return The value
	 */
	public String getExcelString(Cell cell) {
		if (isBlank(cell))
			return null;
		return cell.getStringCellValue();
	}
	
	//正确地处理整数后自动加零的情况
	public String getRightStr(String sNum){
		DecimalFormat decimalFormat = new DecimalFormat("#.000000");
		String resultStr = decimalFormat.format(new Double(sNum));
		if (resultStr.matches("^[-+]?\\d+\\.[0]+$"))
		{
			resultStr = resultStr.substring(0, resultStr.indexOf("."));
		}
		return resultStr;
	}
}