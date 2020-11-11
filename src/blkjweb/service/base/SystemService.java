package blkjweb.service.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface SystemService 
{
	List<Map<String, Object>> queryCount(String tableName,String items, String where,String group);
	
	boolean execute(String sql);
	boolean execute(String[] sql);

	List<Map<String, Object>> queryAllField(String table, String where);
	List<Map<String, Object>> query(String table, String where);
	List<Map<String, Object>> query(String table, String item, String where);
	List<Map<String, Object>> query(String table,String sort, String order, String where, int position, int length);
	List<Map<String, Object>> query(String table,String sort, String order, String where, Object[] params);
	List<Map<String, Object>> query(String table,String items,String sort, String order, String where, Object[] params);
	List<Map<String, Object>> queryDBInfo(String table,String catalogName);
	List<Map<String, Object>> queryTableInfo(String DBName,String tableName);
	List<Map<String, Object>> queryTableInfo(String tableName);

	Map<String, Object> queryOne(String tableName, String fieldName, Object fieldValue);
	Map<String, Object> queryOne4Items(String table, String item, String where);
	Map<String, Object> queryOne(String tableName, String where); 
	Map<String, Object> queryOne(Map<String, Object> Tmap,String tableName); 
	
	long queryCount(String tableName);
	long queryCount(String tableName, String where);
	
	boolean batchMultiTable(String[] batchSQLStr);
	boolean batchMultiTable(ArrayList<String> batchSQLStr); 
	
	boolean batchMultitable(List<String> tableList,List<String> opList,
                                   List<List<Map<String, Object>>> recordList,
                                   List<String> whereList,List<Boolean> flagList); 
	boolean batchInsertUpdate(ArrayList<String> batchSQLStr);
	
	//失败返回-1；成功返回>=0
	int update_raw(String tableName, Map<String, Object> mapRecord, String where);//不对主键进行任何处理
	int update(String tableName, Map<String, Object> mapRecord, String where);
	int update(String tableName, Map<String, Object> mapRecord, boolean isUpdateKey, String where);
	boolean update(String sql, Object[] params,boolean paramFlag, boolean delFlag);
	int updateInsert(String tableName, Map<String, Object> mapRecord);
	int updatePW(String tableName, String userID,String oldpw,String newpw1);
	
	int insert(String tableName, Map<String, Object> mapRecord);//插入一条新纪录
	int doInsertGenIndex(String tableName, Map<String, Object> mapRecord);
	
	
	int delete(String table,String where);
	
	int delete(String tableName, Map<String, Object> mapRecord);
	int broom(String table);
	String[] getkeys(String tableName);

}