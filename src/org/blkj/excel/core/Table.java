package org.blkj.excel.core;

import java.util.Collection;


/**
 * Table with Name, Columns. 
 */
public class Table {

	private String name;//库表名称名

	private Collection<Column> columns;//表的字段名称

	/** Creates a new instance of Table */
	public Table() {
	}

	public Table(String tableName, Collection<Column> columns) {
		this.name = tableName;
		this.columns = columns;
	}


	public String getName() {
		return name;
	}

	public void setName(String tableName) {
		this.name = tableName;
	}

	public Collection<Column> getColumns() {
		return columns;
	}

	public void setColumns(Collection<Column> columns) {
		this.columns = columns;
	}
}
