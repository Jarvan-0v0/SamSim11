package org.blkj.sql.test;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDBHandler <T> 
{
	protected AbstractDBHandler() {	}
	
	protected int _recordMappingToObj(ResultSet rs, Object objRef)
	{
		return new ReflectionResultSetMapper<T>()._recordMappingToObj(rs, objRef);
	}
	
	protected Class<T> type;

	
	protected AbstractDBHandler(Class<T> type) {
		this.type = type;
	}
	protected void setType(Class<T> type) {
		this.type = type;
	}

	/*protected abstract String createInsertQuery();
	protected abstract String createSelectQuery();
	protected abstract int doGenericFactory(String sql, Object[] params);
	*/
	
	protected String getColumns(boolean usePlaceHolders) 
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		/* Iterate the column-names */
		for (Field f : type.getDeclaredFields()) {
			if (first)
				first = false;
			else
				sb.append(", ");
			
			if (usePlaceHolders)
				sb.append("?");
			else
				sb.append(f.getName());
		}
		return sb.toString();
	}
	
	/**
	 * Return all column names as a list of strings
	 * @param database query result set
	 * @return list of column name strings
	 * @throws SQLException if the query fails
	 */
	protected final List<String> getColumnNames(ResultSet rs) throws SQLException
	{
		List<String> columnNames = new ArrayList<String>();
		ResultSetMetaData meta = rs.getMetaData();
		int numColumns = meta.getColumnCount();
		for (int i = 1; i <= numColumns; ++i)
		{
			columnNames.add(meta.getColumnName(i));
		}
		return columnNames;
	}
}


