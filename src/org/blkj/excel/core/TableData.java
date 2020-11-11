package org.blkj.excel.core;

import java.util.*;


/**  保留数据的结构：
 *  （1）每个Cell保存的是excel表一行中某一个字段的名称和他的值
 *  （2）每个DataRow保存的是excel表一行中所有的Cell的值
 *  （3）Table保存的是表的名称和所有字段名
 *  （4）TableData保存的是Table和一个excel表的所有DataRow
 */

/**
 * Table Data. 
 */
public class TableData {

	private Collection<DataRow> datarows = new ArrayList<DataRow>();

	private Table table;

	/** Creates a new instance of TableData */
	public TableData() {
	}

	public Collection<DataRow> getDatarows() {
		return datarows;
	}

	public void setDatarows(Collection<DataRow> datarows) {
		this.datarows = datarows;
	}

	public void addDatarow(DataRow row) {
		datarows.add(row);
	}

	public void clear() {
		datarows = new ArrayList<DataRow>();
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}
    //字段名及对应的值
    public Collection<Map<String, Object>> getDataMap() {
        Collection<Map<String, Object>> col = new ArrayList<Map<String, Object>>();
		for (DataRow dr : datarows) {
			Map<String, Object> map = new TreeMap<String, Object>();
			Collection<Cell> cells = dr.getCells();
			for (Cell cl : cells) 
			{
				map.put(cl.getColumn().getName(), cl.getValue());
			}
			col.add(map);
		}
		return col;
	}

	public int size() {
		return datarows.size();
	}

}
