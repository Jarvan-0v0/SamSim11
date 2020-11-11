package org.blkj.excel.test;

/**代码没有进行任何调试 只是作为备份*/

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.blkj.excel.core.Column;
import org.blkj.excel.core.Table;
import org.blkj.excel.core.TableData;


public class ExcelToOracleServer {

	 private static String INVALID_COL = "_QL_INVALID_COLUMN";
	 private static final String COLUMN_SIZE = "COLUMN_SIZE";

	 private int colSize = Integer.parseInt(COLUMN_SIZE);
	 public int getColSize() {
	     return colSize;
	 }

	 public void setColSize(int colSize) {
	      this.colSize = colSize;
	 }
	/**
     * Create a new table.
     * 
     * @see com.as.quickload.db.DBManager#createTable(java.lang.String,
     *      java.util.Collection)
     */
    public Table createTable(String tableName, Collection<Column> columns) throws SQLException {

        String sql = buildCreateTableSQL(tableName, columns);

        executeCreate(sql);

        return new Table(tableName, columns);
    }

    private String buildCreateTableSQL(String tableName,
            Collection<Column> columns) {
        String sql = " CREATE TABLE " + tableName + " (";
        String cols = "";
        for (Column it : columns) {
            if (it.getName().equals(INVALID_COL)) {
                continue;
            }
            String tempName = "";
            String tempType = "";
            int tempSize = getColSize();

            if (!cols.equals("")) {
                cols += ",";
            }

            tempName = it.getName();

            if (it.getType() == Column.STRING) {
                tempType = "VARCHAR2";
                tempSize = it.getSize() != 0 ? it.getSize() : getColSize();
                cols += tempName + " " + tempType + " (" + tempSize + ")";
            } else if (it.getType() == Column.DATE) {
                tempType = "DATE";
                cols += tempName + " " + tempType;
            }
        }
        sql += cols + ")";
        return sql;
    }

    public void insertTableData(TableData tableData) throws SQLException {
        String tableName = tableData.getTable().getName();
        Collection<Column> colsa = tableData.getTable().getColumns();
        Collection<Map<String, Object>> data = tableData.getDataMap();

        if (tableData.size() < 1) {
            return;
        }

        String sql = buildInsertRowSQL(tableName, colsa);

        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(sql);
            for (Map<String, Object> it : data) {
                int index = 1;
                for (Column it2 : colsa) {
                    if (!it2.getName().equals(INVALID_COL)) {
                        stmt.setObject(index++, it.get(it2.getName()), it2
                                .getType());
                    }
                }
                stmt.addBatch();
            }
            stmt.executeBatch();
            con.commit();
        } finally {
            if (stmt != null)
                stmt.close();
            if (con != null)
                con.close();
        }
    }

    private String buildInsertRowSQL(String tableName, Collection<Column> colsa) {
        String insertIntoTable = "INSERT INTO " + tableName;
        String cols = "";
        String qm = "";

        for (Column it : colsa) {
            String tempName = "";
            if (it.getName().equals(INVALID_COL)) {
                continue;
            }
            if (!"".equals(cols)) {
                cols += ",";
                qm += ",";
            }

            tempName = it.getName();
            cols += tempName;
            qm += "?";
        }
        String insertIntoColumns = " (" + cols + ") VALUES (" + qm + ")";
        String sql = insertIntoTable + insertIntoColumns;
        return sql;
    }

    public Table createTable(Table table) throws SQLException {
        return createTable(table.getName(), table.getColumns());
    }
    
    
    ///////////////////数据库通信部分//////////////////////
    private String conURL;
    private String user;
    private String pwd;
    /**
     * Returns a Database Connection, as specified by Configuration. Calls
     * <code>DriverManager.getConnection(URL, user, password);</code>
     * 
     * @return Connection
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        Connection con = null;
        con = DriverManager.getConnection(conURL, user, pwd);
        return con;
    }
    /**
     * Generic method to execute a SQL. Calls
     * <code>statement.executeUpdate</code>
     * 
     * @param sql
     * @throws SQLException
     */
    public void executeCreate(String sql) throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
            con.commit();
        } finally {
            if (stmt != null)
                stmt.close();
            if (con != null)
                con.close();
        }
    }
}