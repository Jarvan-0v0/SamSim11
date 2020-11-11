package org.blkj.excel.core;

public class Column {

	private String name;//表的字段名称
	private int type;//类型
	private int size;//大小
	public static final int DATE = java.sql.Types.DATE;
	public static final int STRING = java.sql.Types.VARCHAR;
	
	
	/** Creates a new instance of Column */
	public Column() {
	}

	public Column(String name, int type) {
		this.name = name;
		this.type = type;
	}

	public Column(String name) {
		this.name = name;
		this.type = STRING;
	}

	public String getName() {
		return name;
	}

	public void setName(String columnName) {
		this.name = columnName;
	}

	public int getType() {
		return type;
	}

	public void setType(int columnType) {
		this.type = columnType;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean equals(Object ob) {
		if (!(ob instanceof Column)) {
			return false;
		} else {
			Column column = (Column) ob;
			return column.getName().equalsIgnoreCase(this.getName());
		}
	}
	
	public String toString() {
		return getName();
	}
}
