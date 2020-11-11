package org.blkj.sql.core.base;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
/**
 * 负责统一定义实现DML操作的方法
 * 
 */
public interface BaseDML extends Serializable 
{
	
	List<Map<String, Object>> queryCount(String table, String items, String where, String group) throws SQLException;
	
	//int ADD_BEFORE = 0;
    //int ADD_AFTER = 1;  
	
	void closeCon();//关闭连接
    boolean isClosed() throws SQLException;//是否关闭连接
    void rollback();
    void commit() throws SQLException;
    void setAutoCommit(boolean autoCommit);
    Connection getCon();// 获取连接
    DbCenter getDb();
    
    
    boolean checkTableIfExist(String tableName) throws SQLException;
  
    boolean isAutoCommit() throws SQLException;
    void releaseSavepoint() throws SQLException;
    Savepoint setSavepoint(String point) throws SQLException;
    Savepoint setSavepoint() throws SQLException;
    void setTransactionIsolation(int transactionIsolation) throws SQLException ;
     
    boolean execute(String updateSql) throws SQLException;
    boolean execute(String[] updateSql) throws SQLException;
    boolean executeSqlFile(String sqlFileName) throws SQLException;
  
    int batchMultiTable(String[] sql) throws SQLException;
    int batchMultiTable(List<String> tableList,List<String> opList,
            List<List<Map<String, Object>>> recordList,
            List<String> whereList,List<Boolean> flagList) throws SQLException;
    
    int getIndexNodesStepLength();
    int getIndexNodesLength(String tableName);
    Map<String, Object> indexByRow(String tableName, String[] fields, int row_in_table) throws SQLException;
    List<Map<String, Object>> indexByRow(String tableName, String[] fields, int row_in_table, int length, boolean asc) throws SQLException;
    List<Map<String, Object>> indexByIndexNodes(String tableName, String[] fields, int position_of_indexNodes, boolean asc, boolean rebuildIndexNodes) throws SQLException;

    int deleteAll(String table) throws SQLException;
    int delete(String deleteSql) throws SQLException;
    int delete(String[] deleteSql) throws SQLException;
    int delete(String table,String where) throws SQLException;
    
    int delete(String tableName, Map<String, Object> mapRecord) throws SQLException;
    
    //操作失败，返回值为-1；其他情况，执行成功
    int update(String updateSql,Object[] params) throws SQLException;
    int update(String tableName, Map<String, Object> mapRecord) throws SQLException;
    int update(String tableName, Map<String, Object> mapRecord, String where) throws SQLException;
    int update(String updateSql) throws SQLException;
    int update(String[] updateSql) throws SQLException;
    int update(String tableName, List<Map<String, Object>> listRecord) throws SQLException;
    int update(String tableName, List<Map<String, Object>> listRecord, String where) throws SQLException;
    int update(String tableName, Map<String, Object> mapRecord, boolean isUpdateKey, String where) throws SQLException;
    int update_raw(String tableName, Map<String, Object> mapRecord,String where) throws SQLException;
    boolean update(String sql, Object[] params, boolean paramFlag, boolean delFlag) throws SQLException;
    int updateInsert(String tableName, Map<String, Object> mapRecord) throws SQLException;
    
    String queryDbInfo() throws SQLException;
    String queryTableInfo(String tableName) throws SQLException;
    String[] queryTableFields (String tableName);
    List<Map<String, Object>> queryDBInfo(String catalogName) throws SQLException;		
    List<Map<String, Object>> queryTableInfoMap(String tableName) throws SQLException;		
    List<Map<String, Object>> queryTableInfo(String DBName,String tableName) throws SQLException;
    
    List<Map<String, Object>> queryTableOfDB(String DBName,String tableName) throws SQLException;
    /**查询结果保存在Map，String字段名；Object对应的值*/
	Map<String, Object> queryOne(String tableName, String fieldName, Object fieldValue) throws SQLException;
	Map<String, Object> queryOne(String sqlQuery) throws SQLException;//查询一条记录
	Map<String, Object> queryOne(String tableName, String where) throws SQLException;//查询一条记录 where必须
	Map<String, Object> queryOne(String table, String item, String where) throws SQLException;
	
	List<Map<String, Object>> queryAllField(String table, String where) throws SQLException;
	List<Map<String, Object>> query(String table, String item, String where) throws SQLException;
	List<Map<String, Object>> query(String table,String sort, String order, String where, Object[] params) throws SQLException;
	List<Map<String, Object>> query(String table,String sort, String order, String where, int position, int length, boolean isScrollSenstive) throws SQLException;
	List<Map<String, Object>> query(String table,String items,String sort, String order, String where,Object[] params) throws SQLException;
	
	List<Map<String, Object>> query(String table, String where) throws SQLException;
    List<Map<String, Object>> query(String[] fieldName, String tableName) throws SQLException;
    List<Map<String, Object>> query(String sql, Object[] params) throws SQLException;
	List<Map<String, Object>> query(String sql) throws SQLException;
	List<Map<String, Object>> query(String sqlquery, int timeout) throws SQLException;
	List<Map<String, Object>> query(String sqlquery, int position, int length, boolean isScrollSenstive) throws SQLException;
	ResultSet queryResultSet(String querySql) throws SQLException;
    long queryCount(String tableName) throws SQLException;
    long queryCount(String tableName, String where) throws SQLException;
    List<Map<String, Object>> queryByLast(int last) throws SQLException;
    List<Map<String, Object>> queryFetchSize(String sqlquery, int fetchSize) throws SQLException;
    Map<String, Object> queryFirstRecord(String tableName) throws SQLException;
    Map<String, Object> queryLastRecord(String tableName) throws SQLException;
    ResultSet queryResultSet(String querySql, Statement statement) throws SQLException;
    
    int insert(String tableName, List<Map<String, Object>> listRecord) throws SQLException;
    int insert(String tableName, List<Map<String, Object>> listRecord, boolean autoInsertKey) throws SQLException;
    int insert(String insertSql) throws SQLException;
    int insert(String[] insertSql) throws SQLException;
    int insert(String tableName, Map<String, Object> mapRecord) throws SQLException;
    int insert(String tableName, Map<String, Object> mapRecord, boolean autoInsertKey) throws SQLException;
    Object insertAutoKey(String tableName) throws SQLException;
    Long insertKey(String tableName) throws SQLException;
    String insertKey(String tableName, String keyValue) throws SQLException;
    int doInsertGenIndex(String tableName, Map<String, Object> mapRecord) throws SQLException;
    
    
    int save(String tableName, Map<String, Object> mapRecord) throws SQLException;
    Object saveOne(String tableName, Map<String, Object> mapRecord) throws SQLException;
 
}
