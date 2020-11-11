package org.blkj.excel.core;

import java.util.*;


/**
 * One row of Data.  一行内容 相当于一条记录
 */
public class DataRow {

	private Collection<Cell> cells = new ArrayList<Cell>();

	/** Creates a new instance of DataRow */
	public DataRow() {
	}

	public void addColumnValue(Cell cell) {
		this.getCells().add(cell);
	}

	public void setColumnValues(Collection<Cell> cells) {
		this.setCells(cells);
	}

	public void reset() {
		this.setCells(null);
	}

	public Collection<Cell> getCells() {
		return cells;
	}

	public void setCells(Collection<Cell> cells) {
		this.cells = cells;
	}
	
}