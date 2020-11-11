package blkjweb.service;

import java.util.*;
import org.blkj.sql.DMLProcessor;
import org.blkj.sql.core.BaseDMLImp;
import org.blkj.sql.core.base.DbCenter;
import org.blkj.sql.core.criterion.WhereString;
import org.blkj.sql.core.table.Table;
import org.blkj.utils.ConvertTool;
import org.springframework.stereotype.Service;
import blkjweb.service.base.*;

//@Service
@Service("SystemServiceImp")
public class SystemServiceImp implements SystemService {
	@Override
	public List<Map<String, Object>> queryCount(String tableName, String items, String where, String group) {
//		DMLProcessor dbUtil = new DMLProcessor(); 
//		List<Map<String, Object>> result = dbUtil.queryCount(tableName,items,where,group);
//		dbUtil.commit(); 
//		return result;
		return null;
	}

	public boolean checkTableIfExist(String tableName) {
		DMLProcessor dbUtil = new DMLProcessor();
		boolean result = dbUtil.checkTableIfExist(tableName);
		dbUtil.commit();

		return result;
	}

	public long executeCount(String condition) {
		DMLProcessor dbUtil = new DMLProcessor();
		long result = dbUtil.executeCount(condition);
		dbUtil.commit();
		return result;
	}

	public List<Map<String, Object>> executeQuery(String sql) {
		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> result = dbUtil.query(sql);
		dbUtil.commit();
		return result;
	}

	public List<Map<String, Object>> executeQuery(String tableName, String items, String rawwhere) {
		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> result = dbUtil.executeQuery(tableName, items, rawwhere);
		dbUtil.commit();
		return result;
	}

	// 执行Statement的execute方法 负责执行创建、更新表的结构
	@Override
	public boolean execute(String sql) {
		DMLProcessor dbUtil = new DMLProcessor();
		boolean result = dbUtil.execute(sql);
		dbUtil.commit();

		return result;
	}

	@Override
	public boolean execute(String[] sql) {
		DMLProcessor dbUtil = new DMLProcessor();
		boolean result = dbUtil.execute(sql);
		dbUtil.commit();

		return result;
	}

	@Override
	public int doInsertGenIndex(String tableName, Map<String, Object> mapRecord) {
		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.doInsertGenIndex(tableName, mapRecord);
		dbUtil.commit();
		return result;
	}

	// 插入一条新纪录
	@Override
	public int insert(String tableName, Map<String, Object> mapRecord) {
		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.insert(tableName, mapRecord);
		dbUtil.commit();
		return result;
	}

	@Override
	public List<Map<String, Object>> queryTableInfo(String tableName) {
		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> result = dbUtil.queryTableInfoMap(tableName);
		dbUtil.commit();
		return result;
	}

	@Override
	public List<Map<String, Object>> queryTableInfo(String DBName, String tableName) {// 获取库表结构，不能进行反射
		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> result = dbUtil.queryTableInfo(DBName, tableName);
		dbUtil.commit();
		return result;
	}

	@Override
	public List<Map<String, Object>> queryDBInfo(String table, String catalogName) {
		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> result = dbUtil.queryDBInfo(catalogName);
		dbUtil.commit();
		return result;
	}

	@Override
	public long queryCount(String tableName, String where) {
		DMLProcessor dbUtil = new DMLProcessor();
		long result = dbUtil.queryCount(tableName, where);
		dbUtil.commit();
		return result;
	}

	@Override
	public long queryCount(String tableName) {
		DMLProcessor dbUtil = new DMLProcessor();
		long result = dbUtil.queryCount(tableName);
		dbUtil.commit();
		return result;
	}

	@Override
	public Map<String, Object> queryOne(String tableName, String fieldName, Object fieldValue) {
		DMLProcessor dbUtil = new DMLProcessor();
		Map<String, Object> result = dbUtil.queryOne(tableName, fieldName, fieldValue);
		dbUtil.commit();
		return result;
	}

	@Override
	public Map<String, Object> queryOne(String tableName, String where) {
		DMLProcessor dbUtil = new DMLProcessor();
		Map<String, Object> result = dbUtil.queryOne(tableName, where);
		dbUtil.commit();
		return result;
	}

	@Override
	public Map<String, Object> queryOne4Items(String table, String item, String where) {
		DMLProcessor dbUtil = new DMLProcessor();
		Map<String, Object> result = dbUtil.queryOne(table, item, where);
		dbUtil.commit();
		return result;
	}

	// item含有用“,”分割的要检索字段名的字符串
	@Override
	public List<Map<String, Object>> query(String table, String item, String where) {
		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> result = dbUtil.query(table, item, where);
		dbUtil.commit();
		return result;
	}

	@Override
	public List<Map<String, Object>> query(String table, String where) {
		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> result = dbUtil.queryAllField(table, where);
		dbUtil.commit();
		return result;
	}

	// 只从库中检索所需要的记录：LIMIT 5,10 检索记录行 6-15 第一个参数指定第一个返回记录行的偏移量，第二个参数指定返回记录行的最大数目
	@Override
	public List<Map<String, Object>> query(String table, String items, String sort, String order, String where,
			Object[] params) {
		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> result = dbUtil.query(table, items, sort, order, where, params);
		dbUtil.commit();
		return result;
	}

	@Override
	public List<Map<String, Object>> query(String table, String sort, String order, String where, Object[] params) {
		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> result = dbUtil.query(table, sort, order, where, params);
		dbUtil.commit();
		return result;
	}
	// 一次性全部读取，然后返回子集.position记录起始位置,注意表中记录是从1开始;越界则返回0条记录

	@Override
	public List<Map<String, Object>> query(String table, String sort, String order, String where, int position,
			int length) {
		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> result = dbUtil.query(table, sort, order, where, position, length, true);
		dbUtil.commit();
		return result;
	}

	@Override
	public List<Map<String, Object>> queryAllField(String table, String where) {
		DMLProcessor dbUtil = new DMLProcessor();
		List<Map<String, Object>> result = dbUtil.queryAllField(table, where);
		dbUtil.commit();
		return result;
	}

	@Override
	public int updateInsert(String tableName, Map<String, Object> mapRecord) {
		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.updateInsert(tableName, mapRecord);
		dbUtil.commit();
		return result;
	}

	
	public int updateInsertShenheTable(String tableName, Map<String, Object> mapRecord) {
		
		 String insertJson=  ConvertTool.object2json(mapRecord);
		 String xiugairen=(String)mapRecord.get("xiugairen");
			//String nowTime = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss").format(date);
	String sql ="INSERT INTO `shenhe` VALUES ('"+xiugairen+"', curtime, 'sds', '2018-12-18 11:01:53', '未审核', 'insert', '{}', '"+insertJson+"', 'URl', "+tableName+")";

	 boolean   result=execute(sql);
		/*DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.updateInsert(tableName, mapRecord);
		dbUtil.commit();*/
		//int result;
	 if(result==true)
		return 1;
	 else return 0;
	}
	
	@Override
	public boolean update(String sql, Object[] params, boolean paramFlag, boolean delFlag) {
		DMLProcessor dbUtil = new DMLProcessor();
		boolean result = dbUtil.update(sql, params, paramFlag, delFlag);
		dbUtil.commit();
		return result;
	}

	// 当由多个字段构成主键时，含有isUpdateKey的update方法无法对构成主键的任何一个字段进行更新。
	@Override
	public int update_raw(String tableName, Map<String, Object> mapRecord, String where) {
		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.update_raw(tableName, mapRecord, where);
		dbUtil.commit();
		return result;
	}

	// 失败返回-1；成功返回>=0 由1个字段构成主键
	@Override
	public int update(String tableName, Map<String, Object> mapRecord, boolean isUpdateKey, String where) {
		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.update(tableName, mapRecord, isUpdateKey, where);
		dbUtil.commit();
		return result;
	}

	@Override
	public int update(String tableName, Map<String, Object> mapRecord, String where) {
		DMLProcessor dbUtil = new DMLProcessor();

		int result = dbUtil.update(tableName, mapRecord, where);
		dbUtil.commit();
		return result;
	}

	@Override
	public int delete(String table, String where) {
		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.delete(table, where);
		dbUtil.commit();
		return result;
	}

	@Override
	public int delete(String tableName, Map<String, Object> mapRecord) {
		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.delete(tableName, mapRecord);
		dbUtil.commit();

		return result;
	}

	// 批量写数据到同一数据库
	@Override
	public boolean batchInsertUpdate(ArrayList<String> batchSQLStr) {
		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.insert(batchSQLStr); // batchSQLStr是一组标准的SQL更新语句，可以是插入、删除、更新.
		dbUtil.commit();

		if (result <= 0)
			return false;
		else
			return true;

	}

	// 批量操作，涉及多个数据库
	@Override
	public boolean batchMultitable(List<String> tableList, List<String> opList,
			List<List<Map<String, Object>>> recordList, List<String> whereList, List<Boolean> flagList) {
		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.batchMultitable(tableList, opList, recordList, whereList, flagList);
		dbUtil.commit();
		// MyLogger.showMessage("result:" + result);
		if (result <= 0)
			return false;
		else
			return true;
	}

	// 批量操作，涉及多个数据库
	@Override
	public boolean batchMultiTable(ArrayList<String> batchSQLStr) {
		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.batchMultitable(batchSQLStr); // batchSQLStr是一组标准的SQL更新语句，可以是插入、删除、更新.
		dbUtil.commit();

		if (result <= 0)
			return false;
		else
			return true;
	}

	// 批量操作，涉及多个数据库
	@Override
	public boolean batchMultiTable(String[] batchSQLStr) {
		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.batchMultitable(batchSQLStr); // batchSQLStr是一组标准的SQL更新语句，可以是插入、删除、更新.
		dbUtil.commit();

		if (result <= 0)
			return false;
		else
			return true;

	}

	///////////////////////////////////////////////
	@Override
	public int broom(String table) {
		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.deleteAll(table);
		dbUtil.commit();
		return result;
	}

	///////////////
	@Override
	public int updatePW(String tableName, String userID, String oldpw, String newpw1) {
		// 带更新的数据域
		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord.put("password", newpw1);

		// 如下语句的功能等同：String where = "WHERE id='" + userID +"' AND password='" + oldpw
		// +"'";
		String where = "";
		WhereString whereStr = new WhereString();
		List<String> statment = new ArrayList<String>();
		List<String> subOp = new ArrayList<String>();
		statment.add("id='" + userID + "'");

		if (!tableName.equalsIgnoreCase("admin")) {// 管理员专用库表名
			subOp.add("AND");
			statment.add("password='" + oldpw + "'");
			whereStr.setSubOp(subOp);
		}

		whereStr.setStatment(statment);
		where = whereStr.getWhereStr();

		DMLProcessor dbUtil = new DMLProcessor();
		int result = dbUtil.update(tableName, mapRecord, where);
		dbUtil.commit();
		return result;
		/*
		 * String sql="UPDATE user SET password=? WHERE id=? AND password=?"; int result
		 * = dbUtil.update(sql,new String[]{newpw1,userID,oldpw});
		 */
	}
	
	public Map<String, Object> queryViewOne(Map<String, Object> Tmap, String tableName){
		DMLProcessor dbUtil = new DMLProcessor();
		String[] keys = dbUtil.getKeys(tableName);
		if (keys.length < 1)
			return null;
		Map<String, Object> mapRecordT = new HashMap<String, Object>();
		String where = keys[0] + " ='" + Tmap.get(keys[0].toString()) + "'";
		for (int i = 1; i < keys.length; i++) {
			where = where + " AND " + keys[i] + " ='" + Tmap.get(keys[i].toString()) + "'";
		}
		
		mapRecordT = queryOne(tableName+"view", where);
		return mapRecordT;
	}
	@Override
	public Map<String, Object> queryOne(Map<String, Object> Tmap, String tableName) {

		DMLProcessor dbUtil = new DMLProcessor();
		String[] keys = dbUtil.getKeys(tableName);
		if (keys.length < 1)
			return null;
		Map<String, Object> mapRecordT = new HashMap<String, Object>();
		String where = keys[0] + " ='" + Tmap.get(keys[0].toString()) + "'";
		for (int i = 1; i < keys.length; i++) {
			where = where + " AND " + keys[i] + " ='" + Tmap.get(keys[i].toString()) + "'";
		}
		
		mapRecordT = queryOne(tableName, where);
		return mapRecordT;
	}

	@Override
	public String[] getkeys(String tableName) {
		DMLProcessor dbUtil = new DMLProcessor();
		return dbUtil.getKeys(tableName);
	}

}

/*
 * 检测运行时间 long startTime = System.currentTimeMillis();//获取当前时间 //要运行的java程序 long
 * endTime = System.currentTimeMillis();
 * MyLogger.showMessage("程序运行时间："+(endTime-startTime)+"ms");
 */
//依据不同表进行反射
/*
 * private RecordToObject r2o = new RecordToObject(); public List<Object>
 * query(String table,String sort, String order, String where, Object[] params)
 * {//Object为类的对象 DMLProcessor dbUtil = new DMLProcessor(); List<Map<String,
 * Object>> result = dbUtil.query(table,sort,order, where,params);
 * dbUtil.commit(); return r2o.queryResult_obj2db(table,result); } public
 * List<Object> query(String tableName, String[] fieldName) { DMLProcessor
 * dbUtil = new DMLProcessor(); List<Map<String, Object>> result =
 * dbUtil.query(fieldName,tableName); dbUtil.commit(); return
 * r2o.queryResult_db2obj(tableName,result); }
 */