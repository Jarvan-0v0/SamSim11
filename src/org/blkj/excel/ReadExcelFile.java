package org.blkj.excel;

import java.util.*;

import org.blkj.excel.core.Cell;
import org.blkj.excel.core.Column;
import org.blkj.excel.core.DataRow;
import org.blkj.excel.core.Table;
import org.blkj.excel.core.TableData;

/**
 * Excel FileManager
 */
public class ReadExcelFile 
{
	private ExcelFileProcessor fm;
	
	public ReadExcelFile() {}
	
	/*原来的处理：从excel中标读一行内容就往数据库中写一条记录，现修改为：一次从excel表中读取全部数据，然后一次写入*/
	//fileName为xls文件的名称,包括路径， TableName为MYsql的库表名
	public TableData /*ArrayList<String>*/tableProcessor(String fileName,String tableName, boolean excelTypeFlag)
	{
		//ArrayList<String> returnArray = new ArrayList<String>();
		fm = new ExcelFileProcessor();
		try{
			if (!fm.loadFile(fileName,excelTypeFlag)) //读取excel文件
				return null;
			fm.loadWorksheet(0);//Load worksheet(0)
		} catch (Exception e) {
				return null;
		}
		//保存表名，字段名
		Table table = new Table();
		table.setName(tableName);//设置要写入数据的数据库表的名称
		boolean isFirst = true;
		ArrayList<Column> header = null;
		int headerSize = 0;
		//保存able（表名，字段名）及表中所有数据
		TableData data = new TableData();
		data.setTable(table);
		
		while (fm.next()) {//逐行读取excel文件的内容含表头
			Vector<String> vec = fm.getNextRow();//从excel表中获取下一行的内容
			if (vec.size() > 0) 
			{
				if (isFirst) {//excel的表头
					// header row
					Collection<Column> columns = getColumns(vec);
					table.setColumns(columns);//保存表的字段名称
					header = new ArrayList<Column>(columns);
					headerSize = header.size();
					isFirst = false;
				}
				else{//组装实际数据
					int i = 0;
					DataRow row = new DataRow();
					for (String el : vec) 
					{//将excel表中一整行的内容保存在一个DataRow对象中
						if (headerSize > i) {
							Cell cell = new Cell();
							cell.setColumn(header.get(i));
							//Log.debug("colsa:"+header.get(i));
							cell.setValue(el);
							//Log.debug("valus:"+el);
							row.addColumnValue(cell);
							++i;
						}
					}//end of for
					data.addDatarow(row);
				}
			}
			/*if (data.size() != 0 )
			{//逐行读取excel文件，然后逐行写入数据库时用
				dm.insertTableData(data);//插入表数据
				data.clear();
			}*/
		}//end of while
		//dm.insertTableData(data);//逐行写入用
		//return returnArray;
		return data;
	}

	private String getColumnName(String colName) 
	{
		return (colName).toLowerCase();
	}
	private Collection<Column> getColumns(Vector<String> vec)
	{
		Collection<Column> c = new ArrayList<Column>();
		for (String s : vec) {
			String colName = null;
			Column col = new Column();
			if (s == null || s.equals(fm.INDETERMINATE)) {
				colName = fm.INDETERMINATE;
			} else {
				colName = getColumnName(s);
				if (c.contains(new Column(colName))) {
					int i = 0;
					while (c.contains(new Column(colName + (++i)))) {
					}
					colName += i;
				}
			}
			col.setName(colName);
			col.setType(Column.STRING);
			c.add(col);
		}
		return c;
	}

}