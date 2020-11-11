package org.blkj.excel.core;






public class Cell {

	private Column column; //列的名称
	private Object value; //与该列名对应的值

	/** Creates a new instance of Cell */
	public Cell() {
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

}
