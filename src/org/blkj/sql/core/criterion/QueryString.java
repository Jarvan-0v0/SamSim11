package org.blkj.sql.core.criterion;

import org.blkj.utils.StringTool;

public class QueryString 
{

	private String selectField = "*";
	private String[] selectFields = null;
	private String[] fromTables = null;
	private String fromTable = "";
	private WhereString where;
	private String groupField = "";
	private String[] groupFields = null;
	private String[] orderFields = null;
	private String orderField = "";
	private Boolean asc = null;
	private String select = " select ";
	private WhereString[] wheres;
	private StringTool tool = new StringTool();

	public String lastSelect = "SELECT *";
	public String lastFrom = "";
	public String lastwhere = "";
	public String lastGroup = "";
	public String lastOrder = "";

	public QueryString() { }

	public QueryString(String fromTable) {
		this.setFromTable(fromTable);
	}

	public QueryString(String[] selectFields, String fromTable) {
		this.setSelectFields(selectFields);
		this.setFromTable(fromTable);
	}

	public QueryString(Object[] selectFields, String fromTable) {
		this.setSelectFields(this.ObjToStrArray(selectFields));
		this.setFromTable(fromTable);
	}

	public String toString() {
		String s = "";
		s = this.getLastSelect() + this.getLastFrom() + this.getLastwhere() + this.getLastGroup() + this.getLastOrder();
		return s;
	}

	public void setSelectField(String selectField) {
		this.selectField = selectField;
		this.selectFields = null;
	}

	public void setSelectFields(String[] selectFields) {
		this.selectFields = selectFields;
		this.selectField = "*";
	}

	public void setFromTables(String[] fromTables) {
		this.fromTables = fromTables;
		this.fromTable = "";
	}

	public void setFromTable(String fromTable) {
		this.fromTable = fromTable;
		this.fromTables = null;
	}

	public void setGroupField(String groupField) {
		this.groupField = groupField;
		this.groupFields = null;
	}

	public void setGroupFields(String[] groupFields) {
		this.groupFields = groupFields;
		this.groupField = "";
	}

	public void setOrderFields(String[] orderFields) {
		this.orderFields = orderFields;
		this.orderField = "";
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
		this.orderFields = null;
	}

	public void setAsc(Boolean asc) {
		this.asc = asc;
	}

	public void setSelect(String select) {
		this.select = select;

	}

	public void setWhere(WhereString where) {
		this.where = where;
		this.wheres = null;
	}

	public void setWheres(WhereString[] wheres) {
		this.wheres = wheres;
		this.where = null;
	}

	public String getSelectField() {
		return selectField;
	}

	public String[] getSelectFields() {
		return selectFields;
	}

	public String[] getFromTables() {
		return fromTables;
	}

	public String getFromTable() {
		return fromTable;
	}

	public String getLastwhere() {
		WhereString[] sw = getWheres();
		String s = "";
		if (sw != null) {
			for (int i = 0; i < sw.length; i++) {
				s = s + " " + sw[i].toString();
			}
		} else if (this.getWhere() != null) {
			s = getWhere().toString();
		} else {
			s = "";
		}
		return lastwhere = s;
	}

	public String getGroupField() {
		return groupField;
	}

	public String[] getGroupFields() {
		return groupFields;
	}

	public String[] getOrderFields() {
		return orderFields;
	}

	public String getOrderField() {
		return orderField;
	}

	public Boolean getAsc() {
		return asc;
	}

	public String getSelect() {
		return select;
	}

	public WhereString getWhere() {
		return where;
	}

	public String getLastSelect() {
		String s = "";
		if (this.getSelectFields() != null) {
			s = tool.arryToString(this.getSelectFields(), ",");
		} else {
			s = this.getSelectField();
		}
		return lastSelect = getSelect() + " " + s + " ";
	}

	public String getLastFrom() {
		String s = "";
		if (this.getFromTables() != null) {
			s = tool.arryToString(this.getFromTables(), ",");
		} else {
			s = this.getFromTable();
		}
		return lastFrom = " from " + s + " ";
	}

	public String getLastGroup() {
		String s = "";
		if (this.getGroupFields() != null) {
			s = " group by " + tool.arryToString(this.getGroupFields(), ",");
		} else if (!"".equals(this.groupField)) {
			s = " group by " + this.groupField + " ";
		} else {
			s = "";
		}
		return lastGroup = s;
	}

	public String getLastOrder() {
		String s = "";
		if (this.orderFields != null) {
			s = " order by " + tool.arryToString(this.orderFields, ",") + " ";
		} else if (this.orderField != "") {
			s = " order by " + this.orderField + " ";
		} else {
			s = "";
		}

		if (s != "" && this.asc != null) {
			if (asc.booleanValue()) {
				s = s + "asc";
			} else {
				s = s + "desc";
			}
		}

		return lastOrder = s;
	}

	public WhereString[] getWheres() {
		return wheres;
	}

	private String[] ObjToStrArray(Object[] objs) {
		int len = objs.length;
		String[] strs = new String[len];
		for (int i = 0; i < len; i++) {
			strs[i] = objs[i].toString();
		}
		return strs;
	}
}
