package org.blkj.sql.core;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.blkj.sql.core.base.BaseDML;
import org.blkj.sql.core.connection.DbConnection;
import org.blkj.sql.core.connection.DbConnectionType;
import org.blkj.sql.core.table.Field;
import org.blkj.sql.core.table.IndexNode;
import org.blkj.sql.core.table.Table;
import org.blkj.utils.DateTool;
import org.blkj.utils.StringTool;
import org.blkj.utils.base.RegexType;
import blkjweb.utils.Console;


/** 资源关闭顺序
 *1、关闭resultSet
 *2、关闭statement preparedStatement
 *3、关闭connection
 */

public class BaseDMLImp implements BaseDML
{

	//private boolean isSupportJTA;//是否支持JTA事务
	
	private static final long serialVersionUID = 1L;
	
	private StringTool tool = new StringTool();
	private DateTool dt = new DateTool();

	private DbCenterImp dbCenterImp;//保存连接的数据库的元数据
	private boolean autoCommit; //false禁止自动提交，true自动提交
	private boolean failCommit; //true表示提交失败
	private Savepoint savepoint;//事务设置回滚点
	private int indexStepLength = 1000; //索引节点步长

	private java.sql.Connection con; //保存为用户建立的数据库连接
	private java.sql.ResultSet rs;
	private java.sql.Statement stmt;  //提供基本的 SQL 操作. 适合静态SQL语句, 且传入的 SQL语句无法接受参数.
	private java.sql.PreparedStatement preStmt;//可以在 SQL 中传递参数, 适合多次使用的 SQL 语句.
	private java.sql.ResultSetMetaData rsmd;

	/**
	 * 默认构造函数.使用/META-INF/dbCenterImp.xml配置的数据库连接
	 */
	public BaseDMLImp() {
		this.con = DbConnection.getDefaultCon();
		dbCenterImp = DbCenterImp.instance(DbConnection.getDefaultCon(), DbConnectionType.USING_CONFIG_OF_DEFAULT);
		autoCommit = true;
		failCommit = true;
		//where = new WhereString(dbCenterImp);
		// query = new QueryString(dbCenterImp);
	}
	
	public BaseDMLImp(Connection con)
	{
		this.con = con;
		//创建数据库实例 ,通过它可以访问数据库的元数据 
		dbCenterImp = DbCenterImp.instance(con, DbConnectionType.USING_CONFIG_OF_NONE);
		autoCommit = true;
		failCommit = true;
	}

	/**
	 * 构造函数.这个构造函数可以根据connectionType值创建不同的Db实例，可以用于不同数据库之间数据的交换
	 *
	 * @param con 使用指定的数据库连接
	 * @param connectionType 连接的类型 连接的类型，目前以下四种类型<br/>
	 * DbConnectionType.USING_CONFIG_OF_DEFAULT，实际的值1，需要db.xml配置文件<br/>
	 * DbConnectionType.USING_CONFIG_OF_NONE，实际的值0，需要指定的con<br/>
	 * DbConnectionType.USING_DB_01，实际的值101，需要指定的con<br/>
	 * DbConnectionType.USING_DB_02，实际的值102，需要指定的con<br/>
	 */
	public BaseDMLImp(Connection con, int connectionType) {
		this.con = con;

		switch (connectionType) {
		case DbConnectionType.USING_CONFIG_OF_JPA: {
			//dbCenterImp = DbCenter.instance(con, DbConnectionType.USING_CONFIG_OF_JPA);
			this.con = con;
			dbCenterImp = DbCenterImp.instance(con, DbConnectionType.USING_CONFIG_OF_NONE);
			break;
		}
		case DbConnectionType.USING_CONFIG_OF_HIBERNATE: {
			//dbCenterImp = DbCenter.instance(con, DbConnectionType.USING_CONFIG_OF_HIBERNATE);
			this.con = con;
			dbCenterImp = DbCenterImp.instance(con, DbConnectionType.USING_CONFIG_OF_NONE);
			break;
		}
		case DbConnectionType.USING_CONFIG_OF_MYBATIS: {
			//dbCenterImp = DbCenter.instance(con, DbConnectionType.USING_CONFIG_OF_MYBATIS);
			this.con = con;
			dbCenterImp = DbCenterImp.instance(con, DbConnectionType.USING_CONFIG_OF_NONE);
			break;
		}
		case DbConnectionType.USING_CONFIG_OF_DEFAULT: {
			this.con = DbConnection.getDefaultCon();
			dbCenterImp = DbCenterImp.instance(con, DbConnectionType.USING_CONFIG_OF_DEFAULT);
			break;
		}
		case DbConnectionType.USING_CONFIG_OF_NONE: {
			this.con = con;
			dbCenterImp = DbCenterImp.instance(con, DbConnectionType.USING_CONFIG_OF_NONE);
			break;
		}
		case DbConnectionType.USING_DB_01: {
			this.con = con;
			dbCenterImp = DbCenterImp.instance(con, DbConnectionType.USING_DB_01);
			break;
		}
		case DbConnectionType.USING_DB_02: {
			this.con = con;
			dbCenterImp = DbCenterImp.instance(con, DbConnectionType.USING_DB_02);
			break;
		}
		default: {
			this.con = DbConnection.getDefaultCon();
			dbCenterImp = DbCenterImp.instance(con, DbConnectionType.USING_CONFIG_OF_DEFAULT);
			break;
		}
		}

		autoCommit = true;
		failCommit = true;
		// where = new WhereString(dbCenterImp);
		//query = new QueryString(dbCenterImp);
	}
	
	/*
	 * 构造函数.
	 *
	 * @param connectionType 指定配置文件的构造函数 取值：<br/>
	 * DbConnectionType.USING_CONFIG_OF_JPA<br/>
	 * DbConnectionType.USING_CONFIG_OF_HIBERNATE<br/>
	 * DbConnectionType.USING_CONFIG_OF_MYBATIS<br/>
	 * DbConnectionType.USING_DB_01<br/> DbConnectionType.USING_DB_02<br/>
	 * @deprecated

     public ProcessVO(int connectionType) {
     switch (connectionType) {
     case DbConnectionType.USING_CONFIG_OF_JPA: {
     where = new WhereString(dbCenterImp);
     query = new QueryString(dbCenterImp);
     break;
     }
     case DbConnectionType.USING_CONFIG_OF_HIBERNATE: {
     where = new WhereString(dbCenterImp);
     query = new QueryString(dbCenterImp);
     break;
     }
     case DbConnectionType.USING_CONFIG_OF_MYBATIS: {
     where = new WhereString(dbCenterImp);
     query = new QueryString(dbCenterImp);
     break;
     }
     default: {
     break;
     }
     }
     } 
	 */
	//dbbase///////////////////////////////////////////////////////////////////////////
	//false禁止自动提交，true自动提交(默认)
	@Override
	public void setAutoCommit(boolean autoCommit){
		try {
			if(con != null)
				con.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			Console.showMessage(BaseDMLImp.class.getSimpleName(), e.getMessage(), e);
		}
		this.autoCommit = autoCommit;
	}
	//提交事务
	@Override
	public void commit() throws SQLException{
		if (!con.isClosed()) {
			if (!autoCommit) {
				try {
					con.commit();
					autoCommit = true;
					con.setAutoCommit(autoCommit);
					failCommit = false;
					con.close();
				} finally {
					if (failCommit) {
						rollback();
					}
				}
			}
		}
	}

	//事务回滚
	@Override
	public void rollback(){
		if(con == null)
			return;
		try {
			if (!con.isClosed()) {
				if (!this.autoCommit) {
					if (failCommit) {
						if (savepoint == null) {
							//MyLogger.showMessage("con.rollback();");
							con.rollback();
							con.close();
						} else {
							con.rollback(savepoint);
							con.close();
						}
						autoCommit = true;
					}
				}
			}
		} catch (SQLException e) {
			Console.showMessage(BaseDMLImp.class.getSimpleName(), e.getMessage(), e);
		}
	}

	//3关闭数据库联接
	@Override
	public void closeCon()  
	{
		if(con == null)
			return;
		try {  	

		}finally {
			try {
				if (! con.isClosed()) 
					con.close();//立即释放连接对象的数据库和JDBC资源
			}catch (SQLException e) {
				Console.showMessage(BaseDMLImp.class.getSimpleName(), e.getMessage(), e);
			}finally{
				con = null;  //Make sure we don't close it twice
			}
		}
	}

	//判断数据库连接是否关闭,返回true表示已关闭，返回false表示未关闭.
	@Override
	public boolean isClosed() throws SQLException
	{
		if(con == null)
			return true;
		return con.isClosed();//已关闭，返回true
	}
	//返回数据库连接
	@Override
	public Connection getCon() 
	{
		return this.con;
	}  
	/**
	 * 返回Db实例，以便于用户查询数据库及其表、字段的结构信息
	 *
	 * @return 当前使用的Db实例
	 */
	@Override
	public DbCenterImp getDb() {
		return dbCenterImp;
	}
	///////////////////////////////////////////////////////
	/**
	 * 设置事务隔离级别，默认的事务隔离级别是Connection.TRANSACTION_READ_COMMITTED，具体请参阅Connection的文档
	 * <pre>
	 *         Connection.TRANSACTION_NONE=              0;        不能使用
	 *         Connection.TRANSACTION_READ_UNCOMMITTED = 1;        dirty reads, non-repeatable reads and phantom reads can occur
	 *         Connection.TRANSACTION_READ_COMMITTED   = 2;        dirty reads are prevented; non-repeatable reads and phantom reads can occur
	 *         Connection.TRANSACTION_REPEATABLE_READ  = 4;        dirty reads and non-repeatable reads are prevented; phantom reads can occur.
	 *         Connection.TRANSACTION_SERIALIZABLE     = 8;        dirty reads, non-repeatable reads and phantom reads are prevented
	 * </pre>
	 *
	 * @param transactionIsolation 的取值应当是Connection给定的常量之一 :
	 */
	@Override
	public void setTransactionIsolation(int transactionIsolation) throws SQLException {
		con.setTransactionIsolation(transactionIsolation);
	}

	/**
	 * 设置事务保存点。
	 *
	 * @throws SQLException
	 */
	@Override
	public Savepoint setSavepoint() throws SQLException {
		savepoint = con.setSavepoint();
		return savepoint;
	}

	/**
	 * 设置事务保存点。
	 *
	 * @param point 事务点名称
	 * @throws SQLException
	 */
	@Override
	public Savepoint setSavepoint(String point) throws SQLException {
		savepoint = con.setSavepoint(point);
		return savepoint;
	}

	/**
	 * 释放事务保存点。
	 *
	 * @throws SQLException
	 */
	@Override
	public void releaseSavepoint() throws SQLException {
		if (savepoint != null) {
			con.releaseSavepoint(savepoint);
		}
	}

	/**
	 * 用于判断数据库连接是否使用了事务
	 *
	 * @throws SQLException
	 */
	@Override
	public boolean isAutoCommit() throws SQLException {
		return con.getAutoCommit();
	}

	//private////////////////////////////////////////////////////////////////////////////////////  

	//1关闭resultSet    
	private void closeRs() throws SQLException
	{
		try {
			if(rs != null)
				rs.close();
		}catch (SQLException e) {
			throw e;
		}finally{
			rs = null;//是否需要
		}
	}
	//2关闭PreparedStatement PreStmt
	private void closePreStmt() throws SQLException 
	{
		try {
			if(preStmt != null)
				preStmt.cancel();
		}catch (SQLException e) {
			throw e;
		}finally{
			try {
				if(preStmt != null)
					preStmt.close();
			} catch (SQLException e) {
				throw e;
			}
			preStmt = null;//是否需要	
		}
	}

	//2关闭 Statement stmt
	private void closeStmt() throws SQLException 
	{
		try {
			if(stmt != null)
				stmt.cancel(); //取消Statement中的SQL语句指定的数据库操作命令
		}catch (SQLException e) {
			throw e;
		}finally{
			try {
				if(stmt != null)
					stmt.close(); //关闭Statement语句指定的数据库连接
			} catch (SQLException e) {
				throw e;
			}
			stmt = null;//是否需要??
		}
	}
	/**
	 * 终结守卫者,最后一道安全屏障,关闭联接。开发人员不应依赖此方法来关闭联接,但以下情况发生后，可能造成数据库连接不能关闭，因此有必要保留终结守卫者模式。长期的实践证明，终结守卫者能起到良好的作用。
	 * 1、网络连接终止； 2、应用程序中发生非SQLException异常后，可能导致数据库连接没有关闭。
	 */
	public final Object _finalizerGuardian = new Object() {
		@Override
		protected void finalize() throws SQLException, Throwable {
			if (con != null && !con.isClosed()) {
				try {
					super.finalize();
				} finally {
					if (!autoCommit) {
						if (failCommit) {
							try {
								if (savepoint == null) {
									con.rollback();
								} else {
									con.rollback(savepoint);
								}
								Console.showMessage("[" + dt.dateTime() + "] Connection rollback in finalize.");
							} catch (SQLException ex) {
								Console.showMessage(BaseDMLImp.class.getSimpleName(), ex.getMessage(), ex);
							} finally {
								con.close();
								autoCommit = true;
								Console.showMessage("[" + dt.dateTime() + "] Connection is not autocommit, rollback and then closed in finalize.");
							}
						} else {
							con.close();
							Console.showMessage("[" + dt.dateTime() + "] Connection is autocommit, closed in finalize,You should close it.");
						}
					} else {
						con.close();
						Console.showMessage("[" + dt.dateTime() + "] Connection closed in finalize,You should close it.");
					}
				}
			}
		}
	};

	/**
	 * 查询一个索引节点
	 *
	 * @param tableName 表名
	 * @param row_in_table 行索引位置，indexRow>=1,indexRow<=表的记录数 @ return
	 * 返回IndexNode @throws SQLException
	 */
	private IndexNode _indexNodeOne(String tableName, int row_in_table) throws SQLException {
		String key = dbCenterImp.getFields(tableName)[0];
		if (dbCenterImp.getKeys(tableName).length > 0) {
			key = dbCenterImp.getKeys(tableName)[0];
		}
		String querySql = "select " + key + " from " + tableName + " order by " + key;
		stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);//创建不敏感的可滚动结果集
		rs = stmt.executeQuery(querySql);
		rs.last();
		if (row_in_table > rs.getRow()) {
			return null;
		}
		rs.absolute(row_in_table);
		IndexNode n = new IndexNode();
		n.setFirstKeyValue(rs.getObject(key));
		n.setRow(row_in_table);
		this.closeRs();
		this.closeStmt();
		return n;
	}

	/**
	 * 查询两个索引节点
	 *
	 * @param tableName 表名
	 * @param row_in_table 行索引位置，indexRow>=1,indexRow<=表的记录数 @ param length
	 * 长度，节点的长度 @return 返回IndexNode数组 @ t hrows SQLException
	 */
	private IndexNode[] _indexNodeTwo(String tableName, int row_in_table, int length) throws SQLException {
		String key = dbCenterImp.getFields(tableName)[0];
		if (dbCenterImp.getKeys(tableName).length > 0) {
			key = dbCenterImp.getKeys(tableName)[0];
		}
		String querySql = "select " + key + " from " + tableName + " order by " + key;
		stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);//创建不敏感的可滚动结果集
		rs = stmt.executeQuery(querySql);
		rs.last();
		int allLength = rs.getRow();
		if (row_in_table > allLength) {
			return null;
		}
		rs.absolute(row_in_table);
		IndexNode[] n = new IndexNode[]{new IndexNode(), new IndexNode()};
		n[0].setFirstKeyValue(rs.getObject(key));
		n[0].setRow(row_in_table);

		int row2 = row_in_table + length - 1;
		if (row2 > allLength) {
			row2 = allLength;
		}
		rs.absolute(row2);
		n[1].setFirstKeyValue(rs.getObject(key));
		n[1].setRow(row_in_table);

		this.closeRs();
		this.closeStmt();
		return n;
	}

	/**
	 * 创建表的索引节点数组<br/>
	 * 使用默认步长1000，每增加1000条记录创建一个节点，最后一个节点保存最后一条记录信息，每个节点记录表的第一个主键值以及按该键顺序查询结果的行数<br/>
	 *
	 * @param tableName 表名
	 * @param rebuildIndexNodes 是否无条件重建
	 * @return 返回IndexNode数组
	 * @throws SQLException
	 */
	private IndexNode[] _indexNodes(String tableName, boolean rebuildIndexNodes) throws SQLException {
		Table table = dbCenterImp.getTable(tableName);
		String key = dbCenterImp.getFields(tableName)[0];
		if (dbCenterImp.getKeys(tableName).length > 0) {
			key = dbCenterImp.getKeys(tableName)[0];
		}
		int length = (int) this.queryCount(tableName);
		int nodeNums = (int) Math.ceil((double) length / (double) indexStepLength);
		IndexNode[] nodes = dbCenterImp.getTable(tableName).getIndexNodes();
		if (nodes == null) {
			nodes = new IndexNode[nodeNums];
		} else if (nodes.length == 0) {
			nodes = new IndexNode[nodeNums];
		}
		int countInNodes = 0;
		if (nodes[nodes.length - 1] != null) {//======= ====
				countInNodes = nodes[nodes.length - 1].getRow();//最后一个节点保存了记录总长度(总行数)信息
		}
		boolean b = false;
		if (table.getIndexStepLength() != indexStepLength) {
			b = true;
			table.setIndexStepLength(indexStepLength);
		} else if (rebuildIndexNodes) {
			b = true;
		} else if (countInNodes == 0) {//无条件重新建立索引
			b = true;
		} else if (length > countInNodes) {
			b = true;
		}
		if (b) {
			String querySql = "select " + key + " from " + tableName + " order by " + key;
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);//创建不敏感的可滚动结果集
			rs = stmt.executeQuery(querySql);
			nodes = new IndexNode[nodeNums];

			int row = indexStepLength;
			for (int i = 0; i < nodes.length - 1; i++) {
				rs.absolute(row);
				nodes[i] = new IndexNode();
				nodes[i].setFirstKeyValue(rs.getObject(key));
				nodes[i].setRow(rs.getRow());//"第一个关键字段的值对应的行号"
				row += indexStepLength;
			}
			rs.last();
			nodes[nodes.length - 1] = new IndexNode();
			nodes[nodes.length - 1].setFirstKeyValue(rs.getObject(key));
			nodes[nodes.length - 1].setRow(rs.getRow());//"第一个关键字段的值对应的行号"
			dbCenterImp.getTable(tableName).setIndexNodes(nodes);
		}
		this.closeRs();
		this.closeStmt();
		return nodes;
	}
	/**
	 * 查询表中记录
	 *
	 * @param sql 是查询语句
	 * @return 获取记录数.
	 * @throws SQLException
	 */
	private long _queryCountOfTable(String sql) throws SQLException {
		long count = 0l;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			Object o = rs.getObject("express");
			count = Long.parseLong(o.toString());
			this.closeRs();
		} catch (SQLException ex) {
			throw ex;
		} finally {
			this.closeStmt();
		}
		return count;
	}
	
	private int _preparedStatementUpdateInsert(String tableName, List<Map<String, Object>> listRecord) throws SQLException
	{
		int num = 0;
		if (StringTool.isNullEmpty(tableName) || StringTool.isNull(listRecord))
			return num;
		
		String[] tableFields = dbCenterImp.getFields(tableName);//表中的字段名的集合
		if(StringTool.isNull(tableFields))	
			return num;
		
		String[] tableKeys = dbCenterImp.getKeys(tableName);//表中的主键字段集
		//System.out.println("tableKeys: "+tableKeys);
		if(StringTool.isNull(tableKeys))
			return num;
		
		Map<String, Object> _m = new LinkedHashMap<String, Object>(listRecord.get(0));//获取一条记录，作为创建preparedStatementSQL语句的依据
		Object[] recordFields = (_m.keySet()).toArray(); //获取记录里的字段名的集合
		for (int i = 0; i < recordFields.length; i++) {
			if (!StringTool.isInFields(tableFields, recordFields[i].toString())) {
				_m.remove(recordFields[i].toString());//移除无效字段， 查看记录中的字段在表中是否存在，如果不存在，则移除到
			}
		}
		Object[] fields = (_m.keySet()).toArray(); //过滤后的有效字段
		String[] values = new String[fields.length]; //保存通配符'?'
		String[] kss = new String[fields.length]; //保存"键名=?"
		
		
		for (int i = 0; i < fields.length; i++) {
			values[i] = "?";
			kss[i] = fields[i].toString() + "=?";
		}
		String sql_field = tool.arryToString(fields, ",");
		String sql_values = tool.arryToString(values, ",");
		String preparedStatementInsert = "INSERT INTO " + tableName + " (" + sql_field + ") VALUES (" + sql_values + ")";

		String n_v = tool.arryToString(kss, ",");
		String preparedStatementUpdate = " ON DUPLICATE KEY UPDATE " + n_v;

		//"INSERT INTO userrole(userid,roleid) values (?,?) ON DUPLICATE KEY UPDATE userid=?,roleid=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(preparedStatementInsert + preparedStatementUpdate);
			//同一组值，需要赋值两次。
			int step = fields.length;
			int innerLoop = step;
			int k = -1;
			for(int outLoop=0; outLoop<2; outLoop++){//同一组数据需要使用两次对?进行赋值
				Map<String, Object> record = listRecord.get(0); 
				int i = 0;
				if(outLoop == 1){
					i += step;
					innerLoop += step;
					k = -1;
				}
				for (; i < innerLoop; i++) {//对对应的取值进行处理
					k++; 
					Field f = dbCenterImp.getField(tableName, fields[k].toString());
					Object v = record.get(fields[k].toString());//获取给定字段的值（用户传入）

					String className = f.getTypeClassName();
					int index = f.getSqlType();
					//if (v == null) {//XBM原代码。由于不能保证v永远是一个对象，即v多数情况下可能是取null值。所以变更代码如下：
					if ( (v == null) || "null".equals(v) ) {
						pstmt.setNull(i + 1, index);//continue;
					} 
					else { //else if (v != null) {XBM 原代码。
						String _c = ((Class<? extends Object>) v.getClass()).getName(); //增加对表单数据的支持,在表单中获取的数据均为String型,固应对其进行转换.
						if ((_c.equals("java.lang.String")) && ("".equals(((String) v).trim()))) {
							pstmt.setNull(i + 1, index);//continue;
						} else {
							if (className.equals("java.lang.String")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setString(i + 1, (String) v);
								}
								continue;
							}

							if (className.equals("java.lang.Integer")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setInt(i + 1, Integer.parseInt((String) v));
								} else {
									if (_c.equals("java.lang.Integer")) {
										pstmt.setInt(i + 1, ((Integer) v).intValue());
									} else {
										Integer n = new Integer(v.toString());
										pstmt.setInt(i + 1, n);
									}
								}
								continue;
							}

							if (className.equals("java.lang.Long")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setLong(i + 1, Long.parseLong((String) v));
								} else {
									if (_c.equals("java.lang.Long")) {
										pstmt.setLong(i + 1, ((Long) v).longValue());
									} else {
										Long l = new Long(v.toString());
										pstmt.setLong(i + 1, l);
									}
								}
								continue;
							}

							if (className.equals("java.lang.Short")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setShort(i + 1, Short.parseShort((String) v));
								} else {
									pstmt.setShort(i + 1, ((Short) v).shortValue());
								}
								continue;
							}

							if (className.equals("java.lang.Float")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setFloat(i + 1, Float.parseFloat((String) v));
								} else {
									pstmt.setFloat(i + 1, ((Float) v).floatValue());
								}
								continue;
							}

							if (className.equals("java.lang.Double")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setDouble(i + 1, Double.parseDouble((String) v));
								} else {
									pstmt.setDouble(i + 1, ((Double) v).doubleValue());
								}
								continue;
							}

							if (className.equals("java.lang.Boolean")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBoolean(i + 1, (Boolean.valueOf((String) v)).booleanValue());
								} else {
									pstmt.setBoolean(i + 1, ((Boolean) v).booleanValue());
								}
								continue;
							}

							if (className.equals("java.sql.Timestamp")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									String _s = ((String) v).trim();
									if (StringTool.matches(RegexType.chinaDate, _s)) {//如：2012-01-24
										Time t = new Time(0l);
										_s = _s + " " + t.toString();
										pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf(_s));
									} else {
										pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf((String) v));
									}
								} else if (_c.equals("java.sql.Date")) {
									java.sql.Date _v = (java.sql.Date) v;
									pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
								} else if (_c.equals("java.sql.Time")) {
									java.sql.Time _v = (java.sql.Time) v;
									pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
								} else {
									pstmt.setTimestamp(i + 1, new Timestamp(((java.util.Date) v).getTime()));//能支持更多的应用
									//pstmt.setTimestamp(i + 1,  (java.sql.Timestamp) v);//使用jsf日期转换后获得的结果可能不完整，这时会出现转换异常
								}
								continue;
							}

							if (className.equals("java.sql.Date")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
										 pstmt.setString(i + 1, (String) v);
									 }
									 else
										 pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									pstmt.setDate(i + 1, new java.sql.Date(_v.getTime()));
								} else {
									pstmt.setDate(i + 1, (java.sql.Date) v);
								}
								continue;
							}

							if (className.equals("java.util.Date")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
										 pstmt.setString(i + 1, (String) v);
									 }
									 else
										 pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									pstmt.setDate(i + 1, new java.sql.Date(_v.getTime()));
								} else {
									pstmt.setDate(i + 1, (java.sql.Date) v);
								}
								continue;
							}

							if (className.equals("java.sql.Time")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setTime(i + 1, java.sql.Time.valueOf((String) v));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									DateFormat df = new SimpleDateFormat("HH:mm:ss");
									String _dt = df.format(_v);
									pstmt.setTime(i + 1, java.sql.Time.valueOf(_dt));
								} else {
									pstmt.setTime(i + 1, (java.sql.Time) v);
								}
								continue;
							}

							if (className.equals("[B") || className.equals("byte[]")) {
								//SQL Server 的image\timestamp\binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBytes(i + 1, ((String) v).getBytes());
								} else {
									pstmt.setBytes(i + 1, (byte[]) v);
								}
								continue;
							}
							if (className.equals("java.sql.Blob")) {
								//SQL Server 的image\timestamp\binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBytes(i + 1, ((String) v).getBytes());
								} else {
									pstmt.setBlob(i + 1, (java.sql.Blob) v);
								}
								continue;
							}

							if (className.equals("java.lang.Object")) {
								//SQL Server 的image\timestamp\binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBytes(i + 1, ((String) v).getBytes());
								} else {
									pstmt.setObject(i + 1, v);
								}
								continue;
							}

							if (className.equals("java.lang.Byte")) {
								//MySQL的tinyint是java.lang.Byte
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setByte(i + 1, java.lang.Byte.parseByte((String) v));
								} else {
									pstmt.setByte(i + 1, java.lang.Byte.parseByte(v.toString()));
								}
								continue;
							}

							if (className.equals("java.math.BigDecimal")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBigDecimal(i + 1, new BigDecimal((String) v));
								} else if ((_c.equals("java.lang.Double"))) {
									pstmt.setBigDecimal(i + 1, new BigDecimal((Double) v));
								} else if ((_c.equals("java.lang.Float"))) {
									double _v = (Double) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("java.lang.Integer"))) {
									int _v = (Integer) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("java.lang.Long"))) {
									long _v = (Long) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("java.math.BigInteger"))) {
									java.math.BigInteger _v = (java.math.BigInteger) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("[C"))) {
									char[] _v = (char[]) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else {
									pstmt.setBigDecimal(i + 1, (BigDecimal) v);
								}
								continue;
							}

							//以下部分将根据具体的数据库需要而定,有待验证
							if (className.equals("java.sql.Clob")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setString(i + 1, (String) v);//给clob类型的字段赋予字符串型
								} else {
									pstmt.setClob(i + 1, (java.sql.Clob) v);
								}
								continue;
							}

							//以下部分将根据具体的数据库需要而定,有待验证
							if (className.equals("java.sql.Array")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									//
								} else {
									pstmt.setArray(i + 1, (java.sql.Array) v);
								}
								continue;
							}

							//特殊类型，非常用，置后
							if (className.equals("com.sybase.jdbc2.tds.SybTimestamp") || className.toLowerCase().indexOf("timestamp") > 0) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf((String) v));
								} else {
									pstmt.setTimestamp(i + 1, (java.sql.Timestamp) v);
								}
								continue;
							}

							//概略匹配
							if (className.toLowerCase().indexOf("date") > 0) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
								} else {
									pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) v).getTime()));
								}
								continue;
							}

							if (className.toLowerCase().indexOf("time") > 0) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setTime(i + 1, java.sql.Time.valueOf((String) v));
								} else {
									pstmt.setTime(i + 1, (java.sql.Time) v);
								}
								continue;
							}

							if (_c.equals("java.io.FileInputStream")) {
								//调用如：FileInputStream in = new FileInputStream("D:\\test.jpg");的结果
								pstmt.setBinaryStream(i + 1, (FileInputStream) v, ((FileInputStream) v).available());
								continue;
							}//java.io.FileInputStream
							//其它特殊类型，非常用，置后
						}
					}
				}//填写？
				//对insert部分赋值和对Update部分赋值
			}//for(int ii=0; ii<2; ii++)
			//System.out.println(pstmt);
			num = num + pstmt.executeUpdate();
		} catch (java.lang.ClassCastException ex) {
			throw new SQLException("java.lang.ClassCastException: " + ex.getMessage(), ex.getCause());
		} catch (NumberFormatException ex) {
			throw new SQLException("NumberFormatException: " + ex.getMessage(), ex.getCause());
		} catch (IOException ex) {
			throw new SQLException("IOException: " + ex.getMessage(), ex.getCause());
		} finally {
			pstmt.close();
		}
		return num;
	}
	
	//////////////////////////////////////////批处理SQL语句
		/**
	 * 使用PreparedStatement插入多条记录 本方法是根据JDBC API类名的判断来存贮数据<br/>
	 *
	 * 为了方便表单数据的录入,本方法提供了用字符串来表达Integer|Long|Short|Float|Double|Bigdecimal|java.sql.Date等常规类型的支持,<br/>
	 * 但请注意:用字符串表达Date时,目前java.sql.Date仅支持将"yyyy-mm-dd"格式转换成Date对象;<br/>
	 * 对于其它类型,用户必须建立相应类型的对象; 本方法提供了对上传文件的支持; 零长度字符串将保存为null。<br/>
	 
	 * 完成插入的步骤： 1、过滤记录中无效字段，得有效字段Object[] fields<br/>
	 * 2、根据isUpdateKey对有效字段继续分组：true时，再得主键字段组Object[]
	 * keys和非主键字段组Object[]fields<br/> 3、自动组合成PreparedStatement所需要的插入语句<br/>
	 * 4、迭代记录for (Map record : listRecord) {}<br/>
	 * 5、执行PreparedStatement的executeUpdate(updateSQL);<br/>
	 *
	 *
	 * @param tableName 是一个表名
	 * @param listRecord 是具有相同结构的一组记录
	 * @param autoInsertKey 值为true时，自动插入主键
	 * 是操作条件，插入时不起作用，更新时，若where==null||"".equals(where)，则自动根据记录自身主键字段的键值对组合where条件语句
	 * 是从LinkedHashMap参数中获取的值
	 * @throws SQLException
	 *                  
	   PreparedStatement实现的，在初始化的时候就得传一个sql语句进去，然后对于?参数再使用setXxx()填充进去，作者为了偷懒就没有实现多条语句的操作。
	       即只涉及一个表的多记录插入。利用Statement可以解决此问题
	 * 
	 */
	private int _preparedStatementInsert(String tableName, List<Map<String, Object>> listRecord, boolean autoInsertKey) throws SQLException 
	{
		int num = 0;
		
		if (StringTool.isNullEmpty(tableName) || StringTool.isNull(listRecord) )
			return num;
		
		String preparedStatementInsert = "";
		
		Map<String, Object> _m = new LinkedHashMap<String, Object>(listRecord.get(0));//获取一条记录，作为创建preparedStatementSQL语句的依据
		Object maxFieldValue = null;
		
		String[] tableFields = dbCenterImp.getFields(tableName);//表中的字段名的集合
		if(StringTool.isNull(tableFields))//XBM
			return num;
		
		String[] tableKeys = dbCenterImp.getKeys(tableName);//表中的主键字段集
		if(StringTool.isNull(tableKeys))//XBM
			return num;
		
		if (autoInsertKey) {
			Map<?, ?> lastRecord = this.queryLastRecord(tableName);//准备自动插入的主键
			if (tableKeys.length > 0) {
				maxFieldValue = lastRecord.get(tableKeys[0]);
				_m.put(tableKeys[0], "");//防止记录中不含tableKeys[0]主键字段
			} else {
				maxFieldValue = lastRecord.get(tableFields[0]);
				_m.put(tableFields[0], "");//防止记录中不含tableKeys[0]主键字段
			}
		}
		Object[] recordFields = (_m.keySet()).toArray(); //获取记录里的字段名的集合
		for (int i = 0; i < recordFields.length; i++) {
			if (!StringTool.isInFields(tableFields, recordFields[i].toString())) {
				_m.remove(recordFields[i].toString());//移除无效字段， 查看记录中的字段在表中是否存在，如果不存在，则移除到
			}
		}

		Object[] fields = (_m.keySet()).toArray(); //过滤后的有效字段
		String[] values = new String[fields.length]; //保存通配符'?'
		for (int i = 0; i < fields.length; i++) {
			values[i] = "?";
		}

		String sql_field = tool.arryToString(fields, ",");
		String sql_values = tool.arryToString(values, ",");
		preparedStatementInsert = "INSERT INTO " + tableName + " (" + sql_field + ") VALUES (" + sql_values + ")";//合成preparedStatement语句
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(preparedStatementInsert);//多记录共享prepareStatement插入语句
			long firstStringKey = (new java.util.Date()).getTime();
			//为了提高运算效率，规定判断条件优先顺序：常用标准条件精确匹配、标准条件精确匹配、非标准条件精确匹配、非标准条件概略匹配、非标准条件概略小写匹配
			for (Map<String, Object> record : listRecord){
				if (autoInsertKey) {//自动插入主键
					if (tableKeys.length > 0) {
						Field keyF = dbCenterImp.getField(tableName, tableKeys[0]);
						if ("java.lang.Long".equals(keyF.getTypeClassName())) {//时间+4位整数
							record.put(tableKeys[0], dbCenterImp.getTable(tableName).makeLongKeyValue());
						} else if ("java.lang.String".equals(keyF.getTypeClassName())) {
							if (Integer.parseInt(keyF.getSize()) > 13) {
								record.put(tableKeys[0], "" + (firstStringKey + 1));//自动插入字符型主键值//if (className.equals("java.lang.String") && Integer.parseInt(f.getSize()) > 13) 
							} else {
								return num;
							}
						} else {
							maxFieldValue = dbCenterImp.getTable(tableName).makeObjectKeyValue(keyF, maxFieldValue);
							record.put(tableKeys[0], maxFieldValue);//自动插入数值型主键值
						}
					} else {
						Field keyF = dbCenterImp.getField(tableName, tableFields[0]);
						if ("java.lang.Long".equals(keyF.getTypeClassName())) {//时间+4位整数
							record.put(tableFields[0], dbCenterImp.getTable(tableName).makeLongKeyValue());
						} else if ("java.lang.String".equals(keyF.getTypeClassName())) {
							if (Integer.parseInt(keyF.getSize()) > 13) {
								record.put(tableFields[0], "" + (firstStringKey + 1));//自动插入字符型主键值//if (className.equals("java.lang.String") && Integer.parseInt(f.getSize()) > 13) 
							} else {
								return num;
							}
						} else {
							maxFieldValue = dbCenterImp.getTable(tableName).makeObjectKeyValue(keyF, maxFieldValue);
							record.put(tableFields[0], maxFieldValue);//自动插入数值型主键值
						}
					}
				}//自动插入主键

				for (int i = 0; i < fields.length; i++) {//对对应的取值进行处理
					Field f = dbCenterImp.getField(tableName, fields[i].toString());
					String className = f.getTypeClassName();//数据库表所定义相关字段的类型
					int index = f.getSqlType();
					Object v = record.get(fields[i].toString());//获取给定字段的值（用户传入）
					//if (v == null) {//XBM 原代码。由于不能保证v永远是一个对象，即v多数情况下可能是取null值。所以变更代码如下：
					if ( (v == null) || "null".equals(v) ) {
						pstmt.setNull(i + 1, index);//continue;
					} 
					//else if (v != null) {XBM 原代码。
					else {//_c由页面传过来的值，都是String类型
						String _c = ((Class<? extends Object>) v.getClass()).getName();//增加对表单数据的支持,在表单中获取的数据均为String型,固应对其进行转换.
						if ((_c.equals("java.lang.String")) && ("".equals(((String) v).trim()))) {
							pstmt.setNull(i + 1, index);//continue;
						} else {
							if (className.equals("java.lang.String")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setString(i + 1, (String) v);
								}
								continue;
							}

							if (className.equals("java.lang.Integer")) {//tinyint
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setInt(i + 1, Integer.parseInt((String) v));
								} else {
									if (_c.equals("java.lang.Integer")) {
										pstmt.setInt(i + 1, ((Integer) v).intValue());
									} else {//tinyint
										Integer n = new Integer(v.toString());
										pstmt.setInt(i + 1, n);
									}
								}
								continue;
							}

							if (className.equals("java.lang.Long")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setLong(i + 1, Long.parseLong((String) v));
								} else {
									if (_c.equals("java.lang.Long")) {
										pstmt.setLong(i + 1, ((Long) v).longValue());
									} else {
										Long l = new Long(v.toString());
										pstmt.setLong(i + 1, l);
									}
								}
								continue;
							}

							if (className.equals("java.lang.Short")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setShort(i + 1, Short.parseShort((String) v));
								} else {
									pstmt.setShort(i + 1, ((Short) v).shortValue());
								}
								continue;
							}

							if (className.equals("java.lang.Float")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setFloat(i + 1, Float.parseFloat((String) v));
								} else {
									pstmt.setFloat(i + 1, ((Float) v).floatValue());
								}
								continue;
							}

							if (className.equals("java.lang.Double")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setDouble(i + 1, Double.parseDouble((String) v));
								} else {
									pstmt.setDouble(i + 1, ((Double) v).doubleValue());
								}
								continue;
							}

							if (className.equals("java.lang.Boolean")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBoolean(i + 1, (Boolean.valueOf((String) v)).booleanValue());
								} else {
									pstmt.setBoolean(i + 1, ((Boolean) v).booleanValue());
								}
								continue;
							}

							if (className.equals("java.sql.Timestamp")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									String _s = ((String) v).trim();
									if (StringTool.matches(RegexType.chinaDate, _s)) {//如：2012-01-24
										Time t = new Time(0l);
										_s = _s + " " + t.toString();
										pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf(_s));
									} else {
										pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf((String) v));
									}
								} else if (_c.equals("java.sql.Date")) {
									java.sql.Date _v = (java.sql.Date) v;
									pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
								} else if (_c.equals("java.sql.Time")) {
									java.sql.Time _v = (java.sql.Time) v;
									pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
								} else {
									pstmt.setTimestamp(i + 1, new Timestamp(((java.util.Date) v).getTime()));//能支持更多的应用
									//pstmt.setTimestamp(i + 1,  (java.sql.Timestamp) v);//使用jsf日期转换后获得的结果可能不完整，这时会出现转换异常
								}
								continue;
							}
							/**数据库定义为year的类型（只有年），系统的java.sql.Date.valueOf函数无法正确工作。因为
							  valueOf参数必须是一个有效日期的yyyy-mm-dd格式类型的日期*/
							if (className.equals("java.sql.Date")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
										 pstmt.setString(i + 1, (String) v);
									 }
									 else
										 pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									pstmt.setDate(i + 1, new java.sql.Date(_v.getTime()));
								} else {
									pstmt.setDate(i + 1, (java.sql.Date) v);
								}
								continue;
							}

							if (className.equals("java.util.Date")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
										 pstmt.setString(i + 1, (String) v);
									 }
									 else
										 pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									pstmt.setDate(i + 1, new java.sql.Date(_v.getTime()));
								} else {
									pstmt.setDate(i + 1, (java.sql.Date) v);
								}
								continue;
							}
							
							if (className.equals("java.sql.Time")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setTime(i + 1, java.sql.Time.valueOf((String) v));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									DateFormat df = new SimpleDateFormat("HH:mm:ss");
									String _dt = df.format(_v);
									pstmt.setTime(i + 1, java.sql.Time.valueOf(_dt));
								} else {
									pstmt.setTime(i + 1, (java.sql.Time) v);
								}
								continue;
							}

							if (className.equals("[B") || className.equals("byte[]")) {
								//SQL Server 的image\timestamp\binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBytes(i + 1, ((String) v).getBytes());
								} else {
									pstmt.setBytes(i + 1, (byte[]) v);
								}
								continue;
							}
							if (className.equals("java.sql.Blob")) {
								//SQL Server 的image\timestamp\binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBytes(i + 1, ((String) v).getBytes());
								} else {
									pstmt.setBlob(i + 1, (java.sql.Blob) v);
								}
								continue;
							}

							if (className.equals("java.lang.Object")) {
								//SQL Server 的image\timestamp\binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBytes(i + 1, ((String) v).getBytes());
								} else {
									pstmt.setObject(i + 1, v);
								}
								continue;
							}

							if (className.equals("java.lang.Byte")) {
								//MySQL的tinyint是java.lang.Byte
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setByte(i + 1, java.lang.Byte.parseByte((String) v));
								} else {
									pstmt.setByte(i + 1, java.lang.Byte.parseByte(v.toString()));
								}
								continue;
							}

							if (className.equals("java.math.BigDecimal")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBigDecimal(i + 1, new BigDecimal((String) v));
								} else if ((_c.equals("java.lang.Double"))) {
									pstmt.setBigDecimal(i + 1, new BigDecimal((Double) v));
								} else if ((_c.equals("java.lang.Float"))) {
									double _v = (Double) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("java.lang.Integer"))) {
									int _v = (Integer) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("java.lang.Long"))) {
									long _v = (Long) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("java.math.BigInteger"))) {
									java.math.BigInteger _v = (java.math.BigInteger) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("[C"))) {
									char[] _v = (char[]) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else {
									pstmt.setBigDecimal(i + 1, (BigDecimal) v);
								}
								continue;
							}

							//以下部分将根据具体的数据库需要而定,有待验证
							if (className.equals("java.sql.Clob")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setString(i + 1, (String) v);//给clob类型的字段赋予字符串型
								} else {
									pstmt.setClob(i + 1, (java.sql.Clob) v);
								}
								continue;
							}

							//以下部分将根据具体的数据库需要而定,有待验证
							if (className.equals("java.sql.Array")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									//
								} else {
									pstmt.setArray(i + 1, (java.sql.Array) v);
								}
								continue;
							}

							//特殊类型，非常用，置后
							if (className.equals("com.sybase.jdbc2.tds.SybTimestamp") || className.toLowerCase().indexOf("timestamp") > 0) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf((String) v));
								} else {
									pstmt.setTimestamp(i + 1, (java.sql.Timestamp) v);
								}
								continue;
							}

							//概略匹配
							if (className.toLowerCase().indexOf("date") > 0) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
								} else {
									pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) v).getTime()));
								}
								continue;
							}

							if (className.toLowerCase().indexOf("time") > 0) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setTime(i + 1, java.sql.Time.valueOf((String) v));
								} else {
									pstmt.setTime(i + 1, (java.sql.Time) v);
								}
								continue;
							}

							if (_c.equals("java.io.FileInputStream")) {
								//调用如：FileInputStream in = new FileInputStream("D:\\test.jpg");的结果
								pstmt.setBinaryStream(i + 1, (FileInputStream) v, ((FileInputStream) v).available());
								continue;
							}//java.io.FileInputStream

							//其它特殊类型，非常用，置后
						}
 					}
				}//对对应的取值进行处理
				Console.showMessage(pstmt);
				num = num + pstmt.executeUpdate();//原来的代码，方法一
				//需要验证代码的性能
				//pstmt.addBatch();//方法二
			}//所有记录的处理
			//方法二
			/*int[] rowCounts = pstmt.executeBatch();
			for (int j = 0; j < rowCounts.length; j++) {  
				if (rowCounts[j] == Statement.SUCCESS_NO_INFO) { //成功执行，但受影响的行数是未知的  
					//num = 10;
				} else if (rowCounts[j] == Statement.EXECUTE_FAILED) { //指示未能成功执行命 
					//throw new SQLException("The batched statement at index " + j + " failed to execute.");  
				} else { num += rowCounts[j]; }  
			}  
			*/
		} catch (java.lang.ClassCastException ex) {
			throw new SQLException("java.lang.ClassCastException: " + ex.getMessage(), ex.getCause());
		} catch (NumberFormatException ex) {
			throw new SQLException("NumberFormatException: " + ex.getMessage(), ex.getCause());
		} catch (IOException ex) {
			throw new SQLException("IOException: " + ex.getMessage(), ex.getCause());
		} finally {
			pstmt.close();
		}
		return num;
	}
	
	/**
	 * 为了方便表单数据的录入,本方法提供了用字符串来表达Integer|Long|Short|Float|Double|Bigdecimal|java.sql.Date等常规类型的支持
	 * 注意:用字符串表达Date时,目前java.sql.Date仅支持将"yyyy-mm-dd"格式转换成Date对象;
	 * 对于其它类型,用户必须建立相应类型的对象; 本方法提供了对上传文件的支持; 零长度字符串将保存为null。
	 * 
	 * 完成更新的步骤：
	 * 1、过滤记录中无效字段，得有效字段Object[] fields
	 * 2、
	 * 3、自动生成PreparedStatement所需的更新操作
	 * 4、迭代记录for(Map record:listRecord) {}
	 * 5、执行PreparedStatement的executeUpdate(updateSQL);
	 *
	 * @param tableName 是一个表名
	 * @param listRecord 是具有相同结构的一组记录
	 * @param whereStr 是操作条件，插入时不起作用，更新时， 若where==null||"".equals(where)，
	 *        则自动根据库表主键字段的键值对组合where条件语句;从LinkedHashMap参数中获取的值
	 * @throws SQLException
	 *
	 * PreparedStatement实现的，在初始化的时候就得传一个sql语句进去，然后对于?参数再使用setXxx()填充进去，作者为了偷懒就没有实现多条语句的操作。
	       即只涉及一个表的多记录插入。利用Statement可以解决此问题
	       
	     此方法应用于：当由多个字段构成主键时，对构成主键的某一个字段进行更新。
	 */
	private int _preparedStatementUpdate_raw(String tableNameT,
			                                 List<Map<String, Object>> listRecord,
			                                 String whereStr) throws SQLException 
	{	
		int num = 0;
		if (StringTool.isNullEmpty(tableNameT) || StringTool.isNull(listRecord)) {
			return num;
		}
		Map<String, Object> temp = listRecord.get(0);
		if (StringTool.isNull(temp)) {
			return num;
		}
		String tableName = tableNameT.toLowerCase();
		String[] tableFields = dbCenterImp.getFields(tableName);//表中的字段
		
		//获取一条记录，作为过滤、分组依据
		Map<String, Object> _m = new LinkedHashMap<String, Object>(temp);
		
		Object[] recordFields = (_m.keySet()).toArray(); //获取记录里的字段名的集合
		for (int i = 0; i < recordFields.length; i++) {
			if (!StringTool.isInFields(tableFields, recordFields[i].toString())) {
				_m.remove(recordFields[i].toString());//移除无效字段， 查看记录中的字段在表中是否存在，如果不存在，则移除到
			}
		}
		Map<String, Object> key_m = new LinkedHashMap<String, Object>();//记录里的主键 
		Object[] fields = (_m.keySet()).toArray(); //记录中不包含主键的有效字段；再次过滤掉主键字段的结果
		Object[] keys = (key_m.keySet()).toArray(); //记录中包含的主键

	    //准备SQL语句
		String[] kss = new String[fields.length]; //保存"键名=?"
		for (int i = 0; i < fields.length; i++) {
			kss[i] = fields[i].toString() + "=?";
		}
		String n_v = tool.arryToString(kss, ",");
		String preparedStatementUpdate = "UPDATE " + tableName + " SET " + n_v + " ";
		
        //准备where子句
		PreparedStatement pstmt = null;
		try {//用户提供WHERE语句；格式为：具体条件.无： WHERE 
			
			if (!StringTool.isNullEmpty(whereStr)) {//whereStr != null
				pstmt = con.prepareStatement(preparedStatementUpdate + " WHERE "+ whereStr);
			}
			
			//所有记录的处理
			for (Map<String, Object> record : listRecord) {
				/**系统自动根据库表主键字段的键值对组合where条件语句，默认为AND关系*/	
				if (StringTool.isNullEmpty(whereStr)) {//whereStr == null  xbm
					String _w = "";
					if (keys.length > 0) {
						Field f = dbCenterImp.getField(tableName, keys[0].toString());
						Object _o = record.get(keys[0].toString());
						String _s = "";
						if(null != _o ) {//xbm 增加判断条件
							if (f.getTypeClassName().equals("java.lang.String")) {
								_s = keys[0].toString() + " LIKE '" + _o.toString() + "'";
							} else {
								_s = keys[0].toString() + " = " + _o.toString();
							}
							if (keys.length > 1) {
								for (int i = 1; i < keys.length; i++) {
									f = dbCenterImp.getField(tableName, keys[i].toString());
									_o = record.get(keys[i].toString());
									if (f.getTypeClassName().equals("java.lang.String")) {
										_s = _s + " AND " + keys[i].toString() + " LIKE '" + _o.toString() + "'";
									} else {
										_s = _s + " AND " + keys[i].toString() + " = " + _o.toString();
									}
								}
							}
							_w = " WHERE " + _s;
						}
					}
					//如果whereStr==null，从这里执行PrepareStatement更新//条件不同，只能逐条变更插入语句
					pstmt = con.prepareStatement(preparedStatementUpdate + _w);
				}//组装where子句
				 
				//对SQL语句中的?进行赋值. 为了提高运算效率，规定判断条件优先顺序：常用标准条件精确匹配、
				//标准条件精确匹配、非标准条件精确匹配、非标准条件概略匹配、非标准条件概略小写匹配
				for (int i = 0; i < fields.length; i++) {
					Field f = dbCenterImp.getField(tableName,fields[i].toString());
					String className = f.getTypeClassName();
					int index = f.getSqlType();
					Object v = record.get(fields[i].toString());
					
					//if (v == null) {//XBM 原代码。由于不能保证v永远是一个对象，即v多数情况下可能是取null值。所以变更代码如下：
					if ( (v == null) || "null".equals(v) ) {
						pstmt.setNull(i + 1, index);//continue;
					} 
					//else if (v != null) {XBM 原代码。
					else {
						String _c = ((Class<? extends Object>) v.getClass()).getName(); //增加对表单数据的支持,在表单中获取的数据均为String型,固应对其进行转换.
						if ((_c.equals("java.lang.String")) && ("".equals(((String) v).trim()))) {
							pstmt.setNull(i + 1, index);//continue;
						} else {
							if (className.equals("java.lang.String")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setString(i + 1, (String) v);
								}
								continue;
							}
							if (className.equals("java.lang.Integer")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setInt(i + 1, Integer.parseInt((String) v));
								} else {
									if (_c.equals("java.lang.Integer")) {
										pstmt.setInt(i + 1, ((Integer) v).intValue());
									} else {
										Integer n = new Integer(v.toString());
										pstmt.setInt(i + 1, n);
									}
								}
								continue;
							}

							if (className.equals("java.lang.Long")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setLong(i + 1, Long.parseLong((String) v));
								} else {
									if (_c.equals("java.lang.Long")) {
										pstmt.setLong(i + 1, ((Long) v).longValue());
									} else {
										Long l = new Long(v.toString());
										pstmt.setLong(i + 1, l);
									}
								}
								continue;
							}

							if (className.equals("java.lang.Short")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setShort(i + 1, Short.parseShort((String) v));
								} else {
									pstmt.setShort(i + 1, ((Short) v).shortValue());
								}
								continue;
							}

							if (className.equals("java.lang.Float")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setFloat(i + 1, Float.parseFloat((String) v));
								} else {
									pstmt.setFloat(i + 1, ((Float) v).floatValue());
								}
								continue;
							}

							if (className.equals("java.lang.Double")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setDouble(i + 1, Double.parseDouble((String) v));
								} else {
									pstmt.setDouble(i + 1, ((Double) v).doubleValue());
								}
								continue;
							}

							if (className.equals("java.lang.Boolean")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBoolean(i + 1, (Boolean.valueOf((String) v)).booleanValue());
								} else {
									pstmt.setBoolean(i + 1, ((Boolean) v).booleanValue());
								}
								continue;
							}

							if (className.equals("java.sql.Timestamp")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									String _s = ((String) v).trim();
									if (StringTool.matches(RegexType.chinaDate, _s)) {//如：2012-01-24
										Time t = new Time(0l);
										_s = _s + " " + t.toString();
										pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf(_s));
									} else {
										pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf((String) v));
									}
								} else if (_c.equals("java.sql.Date")) {
									java.sql.Date _v = (java.sql.Date) v;
									pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
								} else if (_c.equals("java.sql.Time")) {
									java.sql.Time _v = (java.sql.Time) v;
									pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
								} else {
									pstmt.setTimestamp(i + 1, new Timestamp(((java.util.Date) v).getTime()));//能支持更多的应用
									//pstmt.setTimestamp(i + 1,  (java.sql.Timestamp) v);//使用jsf日期转换后获得的结果可能不完整，这时会出现转换异常
								}
								continue;
							}

							if (className.equals("java.sql.Date")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
										 pstmt.setString(i + 1, (String) v);
									 }
									 else
										 pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									pstmt.setDate(i + 1, new java.sql.Date(_v.getTime()));
								} else {
									pstmt.setDate(i + 1, (java.sql.Date) v);
								}
								continue;
							}

							if (className.equals("java.util.Date")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
										 pstmt.setString(i + 1, (String) v);
									 }
									 else
										 pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									pstmt.setDate(i + 1, new java.sql.Date(_v.getTime()));
								} else {
									pstmt.setDate(i + 1, (java.sql.Date) v);
								}
								continue;
							}

							if (className.equals("java.sql.Time")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setTime(i + 1, java.sql.Time.valueOf((String) v));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									DateFormat df = new SimpleDateFormat("HH:mm:ss");
									String _dt = df.format(_v);
									pstmt.setTime(i + 1, java.sql.Time.valueOf(_dt));
								} else {
									pstmt.setTime(i + 1, (java.sql.Time) v);
								}
								continue;
							}

							if (className.equals("[B") || className.equals("byte[]")) {
								//SQL Server 的image、timestamp、binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBytes(i + 1, ((String) v).getBytes());
								} else {
									pstmt.setBytes(i + 1, (byte[]) v);
								}
								continue;
							}
							if (className.equals("java.sql.Blob")) {
								//SQL Server 的image、timestamp、binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBytes(i + 1, ((String) v).getBytes());
								} else {
									pstmt.setBlob(i + 1, (java.sql.Blob) v);
								}
								continue;
							}
							if (className.equals("java.lang.Object")) {
								//SQL Server 的image、timestamp、binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBytes(i + 1, ((String) v).getBytes());
								} else {
									pstmt.setObject(i + 1, v);
								}
								continue;
							}

							if (className.equals("java.lang.Byte")) {
								//MySQL的tinyint是java.lang.Byte
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setByte(i + 1, java.lang.Byte.parseByte((String) v));
								} else {
									pstmt.setByte(i + 1, java.lang.Byte.parseByte(v.toString()));
								}
								continue;
							}

							if (className.equals("java.math.BigDecimal")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBigDecimal(i + 1, new BigDecimal((String) v));
								} else if ((_c.equals("java.lang.Double"))) {
									pstmt.setBigDecimal(i + 1, new BigDecimal((Double) v));
								} else if ((_c.equals("java.lang.Float"))) {
									double _v = (Double) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("java.lang.Integer"))) {
									int _v = (Integer) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("java.lang.Long"))) {
									long _v = (Long) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("java.math.BigInteger"))) {
									java.math.BigInteger _v = (java.math.BigInteger) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("[C"))) {
									char[] _v = (char[]) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else {
									pstmt.setBigDecimal(i + 1, (BigDecimal) v);
								}
								continue;
							}

							//以下部分将根据具体的数据库需要而定,有待验证
							if (className.equals("java.sql.Clob")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setString(i + 1, (String) v);//给clob类型的字段赋予字符串型
								} else {
									pstmt.setClob(i + 1, (java.sql.Clob) v);
								}
								continue;
							}

							//以下部分将根据具体的数据库需要而定,有待验证
							if (className.equals("java.sql.Array")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									//
								} else {
									pstmt.setArray(i + 1, (java.sql.Array) v);
								}
								continue;
							}

							//特殊类型，非常用，置后
							if (className.equals("com.sybase.jdbc2.tds.SybTimestamp") || className.toLowerCase().indexOf("timestamp") > 0) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf((String) v));
								} else {
									pstmt.setTimestamp(i + 1, (java.sql.Timestamp) v);
								}
								continue;
							}

							//概略匹配
							if (className.toLowerCase().indexOf("date") > 0) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
								} else {
									pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) v).getTime()));
								}
								continue;
							}

							if (className.toLowerCase().indexOf("time") > 0) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setTime(i + 1, java.sql.Time.valueOf((String) v));
								} else {
									pstmt.setTime(i + 1, (java.sql.Time) v);
								}
								continue;
							}

							if (_c.equals("java.io.FileInputStream")) {
								//调用如：FileInputStream in = new FileInputStream("D:\\test.jpg");的结果
								pstmt.setBinaryStream(i + 1, (FileInputStream) v, ((FileInputStream) v).available());
								continue;
							}//java.io.FileInputStream
							//其它特殊类型
						}
					}
				}
				Console.showMessage("update_raw:"+pstmt);
				num = num + pstmt.executeUpdate();//原来的代码
				//pstmt.addBatch();//方法二
			}//所有记录的处理	
			/*方法二 需要验证代码的性能
			int[] rowCounts = pstmt.executeBatch();
			for (int j = 0; j < rowCounts.length; j++) {  
				if (rowCounts[j] == Statement.SUCCESS_NO_INFO) { //成功执行，但受影响的行数是未知的  
					//num = 10;
				} else if (rowCounts[j] == Statement.EXECUTE_FAILED) { //指示未能成功执行命 
					//throw new SQLException("The batched statement at index " + j + " failed to execute.");  
				} else { num += rowCounts[j]; }  
			}  
			*/
		} catch (java.lang.ClassCastException ex) {
			throw new SQLException("java.lang.ClassCastException: " + ex.getMessage(), ex.getCause());
		} catch (NumberFormatException ex) {
			throw new SQLException("NumberFormatException: " + ex.getMessage(), ex.getCause());
		} catch (IOException ex) {
			throw new SQLException("IOException: " + ex.getMessage(), ex.getCause());
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		return num;
	}

	/**
	 * 为了方便表单数据的录入,本方法提供了用字符串来表达Integer|Long|Short|Float|Double|Bigdecimal|java.sql.Date等常规类型的支持
	 * 注意:用字符串表达Date时,目前java.sql.Date仅支持将"yyyy-mm-dd"格式转换成Date对象;
	 * 对于其它类型,用户必须建立相应类型的对象; 本方法提供了对上传文件的支持; 零长度字符串将保存为null。
	 * 
	 * 完成更新的步骤：
	 * 1、过滤记录中无效字段，得有效字段Object[] fields
	 * 2、自动生成PreparedStatement所需的更新操作
	 * 3、迭代记录for(Map record:listRecord) {}
	 * 4、执行PreparedStatement的executeUpdate(updateSQL);
	 *
	 * @param tableName 是一个表名
	 * @param listRecord 是具有相同结构的一组记录
	 * @param isUpdateKey 是否更新主键;true表示“自动插入主键”即更新主键
	 * @param whereStr 是操作条件，插入时不起作用，更新时， 若where==null||"".equals(where)，
	 *        则自动根据库表主键字段的键值对组合where条件语句;从LinkedHashMap参数中获取的值
	 * @throws SQLException
	 *
	 * PreparedStatement实现的，在初始化的时候就得传一个sql语句进去，然后对于?参数再使用setXxx()填充进去，作者为了偷懒就没有实现多条语句的操作。
	       即只涉及一个表的多记录插入。利用Statement可以解决此问题
	       
	   当由多个字段构成主键时，此方法无法对构成主键的任何一个字段进行更新。此情况可以使用：
	   _preparedStatementUpdate_raw(String tableNameT,List<Map<String, Object>> listRecord,
			                             String whereStr) throws SQLException 
	 */
	private int _preparedStatementUpdate(String tableNameT,
			 							 List<Map<String, Object>> listRecord,
			 							 boolean isUpdateKey,
			 							 String whereStr) throws SQLException 
	{	
		int num = 0;//xbm0;
		//xbm
		if (StringTool.isNullEmpty(tableNameT) || StringTool.isNull(listRecord)) {
			return num;
		}
		
		Map<String, Object> temp = listRecord.get(0);
		if (StringTool.isNull(temp)) {
			return num;
		}
		
		String tableName = tableNameT.toLowerCase();
		//xbm end
		String[] tableFields = dbCenterImp.getFields(tableName);//表中的字段
		String[] tableKeys = dbCenterImp.getKeys(tableName);//表中的主键

		//获取一条记录，作为过滤、分组依据
		Map<String, Object> _m = new LinkedHashMap<String, Object>(temp);

		Object[] recordFields = (_m.keySet()).toArray(); //获取记录里的字段名的集合
		for (int i = 0; i < recordFields.length; i++) {
			if (!StringTool.isInFields(tableFields, recordFields[i].toString())) {
				_m.remove(recordFields[i].toString());//移除无效字段， 查看记录中的字段在表中是否存在，如果不存在，则移除到
			}
		}
		Object[] k0 = (_m.keySet()).toArray(); //过滤后的有效字段

		Map<String, Object> key_m = new LinkedHashMap<String, Object>();//记录里的主键 
		if (!isUpdateKey) {
			for (int i = 0; i < k0.length; i++) {
				if (StringTool.isInFields(tableKeys, k0[i].toString())) {//记录中是否有主键
					key_m.put(k0[i].toString(), _m.remove(k0[i].toString()));//将记录中的主键移到key_m中；保证不对主键更新
				}
			}
		}

		Object[] fields = (_m.keySet()).toArray(); //记录中不包含主键的有效字段；再次过滤掉主键字段的结果
		Object[] keys = (key_m.keySet()).toArray(); //记录中包含的主键

		//要更新主键，但主键非法，则返回. XBM此代码有问题。keys中的值永远是空。
		/*if (isUpdateKey) {
			if (keys.length == 0 || keys.length != tableKeys.length) {
				return num;
			}
		}*/
		
		//准备SQL语句
		String[] kss = new String[fields.length]; //保存"键名=?"
		for (int i = 0; i < fields.length; i++) {
			kss[i] = fields[i].toString() + "=?";
		}
		String n_v = tool.arryToString(kss, ",");
		String preparedStatementUpdate = "UPDATE " + tableName + " SET " + n_v + " ";

		//准备where子句
		PreparedStatement pstmt = null;
		try {//用户提供WHERE语句；格式为：具体条件.无： WHERE 

			if (!StringTool.isNullEmpty(whereStr)) {//whereStr != null
		
				pstmt = con.prepareStatement(preparedStatementUpdate + " WHERE "+ whereStr);
			}
			
			//所有记录的处理
			for (Map<String, Object> record : listRecord) {
				/**系统自动根据库表主键字段的键值对组合where条件语句，默认为AND关系*/	
				if (StringTool.isNullEmpty(whereStr)) {//whereStr == null  xbm
					String _w = "";
					if (keys.length > 0) {
						Field f = dbCenterImp.getField(tableName, keys[0].toString());
						Object _o = record.get(keys[0].toString());
						String _s = "";
						if(null != _o ) {//xbm 增加判断条件
							if (f.getTypeClassName().equals("java.lang.String")) {
								_s = keys[0].toString() + " LIKE '" + _o.toString() + "'";
							} else {
								_s = keys[0].toString() + " = " + _o.toString();
							}
							if (keys.length > 1) {
								for (int i = 1; i < keys.length; i++) {
									f = dbCenterImp.getField(tableName, keys[i].toString());
									_o = record.get(keys[i].toString());
									if (f.getTypeClassName().equals("java.lang.String")) {
										_s = _s + " AND " + keys[i].toString() + " LIKE '" + _o.toString() + "'";
									} else {
										_s = _s + " AND " + keys[i].toString() + " = " + _o.toString();
									}
								}
							}
							_w = " WHERE " + _s;
						}
					}
					//如果whereStr==null，从这里执行PrepareStatement更新//条件不同，只能逐条变更插入语句
					pstmt = con.prepareStatement(preparedStatementUpdate + _w);
				}//组装where子句
				
				//对SQL语句中的?进行赋值. 为了提高运算效率，规定判断条件优先顺序：常用标准条件精确匹配、
				//标准条件精确匹配、非标准条件精确匹配、非标准条件概略匹配、非标准条件概略小写匹配
				for (int i = 0; i < fields.length; i++) {
					Field f = dbCenterImp.getField(tableName,fields[i].toString());
					String className = f.getTypeClassName();
					int index = f.getSqlType();
					Object v = record.get(fields[i].toString());//用户要写入对应字段的值
					
					//System.out.println(fields[i].toString()+ "_" + v+ "_" + className);

					//if (v == null) {//XBM 原代码。由于不能保证v永远是一个对象，即v多数情况下可能是取null值。所以变更代码如下：
					if ( (v == null) || "null".equals(v) ) {
						pstmt.setNull(i + 1, index);//continue;
					} 
					//else if (v != null) {XBM 原代码。
					else {
						//增加对表单数据的支持,在表单中获取的数据均为String型,故应对其进行转换.
						String _c = ((Class<? extends Object>) v.getClass()).getName(); 
						if ((_c.equals("java.lang.String")) && ("".equals(((String) v).trim()))) {
							pstmt.setNull(i + 1, index);//continue;
						} else {
							if (className.equals("java.lang.String")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									System.out.println(v);
									pstmt.setString(i + 1, (String) v);
								}
								continue;
							}
							//对应SQL的tinyint
							if (className.equals("java.lang.Integer")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setInt(i + 1, Integer.parseInt((String) v));
								} else {
									if (_c.equals("java.lang.Integer")) {
										pstmt.setInt(i + 1, ((Integer) v).intValue());
									} else {
										Integer n = new Integer(v.toString());
										pstmt.setInt(i + 1, n);
									}
								}
								continue;
							}

							if (className.equals("java.lang.Long")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setLong(i + 1, Long.parseLong((String) v));
								} else {
									if (_c.equals("java.lang.Long")) {
										pstmt.setLong(i + 1, ((Long) v).longValue());
									} else {
										Long l = new Long(v.toString());
										pstmt.setLong(i + 1, l);
									}
								}
								continue;
							}

							if (className.equals("java.lang.Short")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setShort(i + 1, Short.parseShort((String) v));
								} else {
									pstmt.setShort(i + 1, ((Short) v).shortValue());
								}
								continue;
							}

							if (className.equals("java.lang.Float")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setFloat(i + 1, Float.parseFloat((String) v));
								} else {
									pstmt.setFloat(i + 1, ((Float) v).floatValue());
								}
								continue;
							}

							if (className.equals("java.lang.Double")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setDouble(i + 1, Double.parseDouble((String) v));
								} else {
									pstmt.setDouble(i + 1, ((Double) v).doubleValue());
								}
								continue;
							}

							if (className.equals("java.lang.Boolean")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBoolean(i + 1, (Boolean.valueOf((String) v)).booleanValue());
								} else {
									pstmt.setBoolean(i + 1, ((Boolean) v).booleanValue());
								}
								continue;
							}
                            //对应SQL的decimal
							if (className.equals("java.math.BigDecimal")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBigDecimal(i + 1, new BigDecimal((String) v));
								} else if ((_c.equals("java.lang.Double"))) {
									pstmt.setBigDecimal(i + 1, new BigDecimal((Double) v));
								} else if ((_c.equals("java.lang.Float"))) {
									double _v = (Double) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("java.lang.Integer"))) {
									int _v = (Integer) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("java.lang.Long"))) {
									long _v = (Long) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("java.math.BigInteger"))) {
									java.math.BigInteger _v = (java.math.BigInteger) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else if ((_c.equals("[C"))) {
									char[] _v = (char[]) v;
									pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
								} else {
									pstmt.setBigDecimal(i + 1, (BigDecimal) v);
								}
								continue;
							}

							if (className.equals("java.sql.Date")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
										 pstmt.setString(i + 1, (String) v);
									 }
									 else
										 pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									pstmt.setDate(i + 1, new java.sql.Date(_v.getTime()));
								} else {
									pstmt.setDate(i + 1, (java.sql.Date) v);
								}
								continue;
							}

							if (className.equals("java.util.Date")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
										 pstmt.setString(i + 1, (String) v);
									 }
									 else
										 pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									pstmt.setDate(i + 1, new java.sql.Date(_v.getTime()));
								} else {
									pstmt.setDate(i + 1, (java.sql.Date) v);
								}
								continue;
							}

							if (className.equals("java.sql.Time")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setTime(i + 1, java.sql.Time.valueOf((String) v));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									DateFormat df = new SimpleDateFormat("HH:mm:ss");
									String _dt = df.format(_v);
									pstmt.setTime(i + 1, java.sql.Time.valueOf(_dt));
								} else {
									pstmt.setTime(i + 1, (java.sql.Time) v);
								}
								continue;
							}


							if (className.equals("java.sql.Timestamp")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									String _s = ((String) v).trim();
									if (StringTool.matches(RegexType.chinaDate, _s)) {//如：2012-01-24
										Time t = new Time(0l);
										_s = _s + " " + t.toString();
										pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf(_s));
									} else {
										pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf((String) v));
									}
								} else if (_c.equals("java.sql.Date")) {
									java.sql.Date _v = (java.sql.Date) v;
									pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
								} else if (_c.equals("java.util.Date")) {
									java.util.Date _v = (java.util.Date) v;
									pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
								} else if (_c.equals("java.sql.Time")) {
									java.sql.Time _v = (java.sql.Time) v;
									pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
								} else {
									pstmt.setTimestamp(i + 1, new Timestamp(((java.util.Date) v).getTime()));//能支持更多的应用
									//pstmt.setTimestamp(i + 1,  (java.sql.Timestamp) v);//使用jsf日期转换后获得的结果可能不完整，这时会出现转换异常
								}
								continue;
							}
							if (className.equals("[B") || className.equals("byte[]")) {
								//SQL Server 的image、timestamp、binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBytes(i + 1, ((String) v).getBytes());
								} else {
									pstmt.setBytes(i + 1, (byte[]) v);
								}
								continue;
							}
							
							if (className.equals("java.sql.Blob")) {
								//SQL Server 的image、timestamp、binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBytes(i + 1, ((String) v).getBytes());
								} else {
									pstmt.setBlob(i + 1, (java.sql.Blob) v);
								}
								continue;
							}
							
							if (className.equals("java.lang.Object")) {
								//SQL Server 的image、timestamp、binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setBytes(i + 1, ((String) v).getBytes());
								} else {
									pstmt.setObject(i + 1, v);
								}
								continue;
							}

							if (className.equals("java.lang.Byte")) {
								//MySQL的tinyint是java.lang.Byte
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setByte(i + 1, java.lang.Byte.parseByte((String) v));
								} else {
									pstmt.setByte(i + 1, java.lang.Byte.parseByte(v.toString()));
								}
								continue;
							}

													//以下部分将根据具体的数据库需要而定,有待验证
							if (className.equals("java.sql.Clob")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setString(i + 1, (String) v);//给clob类型的字段赋予字符串型
								} else {
									pstmt.setClob(i + 1, (java.sql.Clob) v);
								}
								continue;
							}

							//以下部分将根据具体的数据库需要而定,有待验证
							if (className.equals("java.sql.Array")) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									//
								} else {
									pstmt.setArray(i + 1, (java.sql.Array) v);
								}
								continue;
							}

							//特殊类型，非常用，置后
							if (className.equals("com.sybase.jdbc2.tds.SybTimestamp") || className.toLowerCase().indexOf("timestamp") > 0) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf((String) v));
								} else {
									pstmt.setTimestamp(i + 1, (java.sql.Timestamp) v);
								}
								continue;
							}

							//概略匹配
							if (className.toLowerCase().indexOf("date") > 0) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
								} else {
									pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) v).getTime()));
								}
								continue;
							}

							if (className.toLowerCase().indexOf("time") > 0) {
								if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
									pstmt.setTime(i + 1, java.sql.Time.valueOf((String) v));
								} else {
									pstmt.setTime(i + 1, (java.sql.Time) v);
								}
								continue;
							}

							if (_c.equals("java.io.FileInputStream")) {
								//调用如：FileInputStream in = new FileInputStream("D:\\test.jpg");的结果
								pstmt.setBinaryStream(i + 1, (FileInputStream) v, ((FileInputStream) v).available());
								continue;
							}//java.io.FileInputStream
							//其它特殊类型
						}
					}
				}
				Console.showMessage("XBM:"+pstmt);  
				num = num + pstmt.executeUpdate();//原来的代码
				//pstmt.addBatch();//方法二
			}//所有记录的处理	
			/*方法二 需要验证代码的性能
			int[] rowCounts = pstmt.executeBatch();
			for (int j = 0; j < rowCounts.length; j++) {  
			if (rowCounts[j] == Statement.SUCCESS_NO_INFO) { //成功执行，但受影响的行数是未知的  
			//num = 10;
			} else if (rowCounts[j] == Statement.EXECUTE_FAILED) { //指示未能成功执行命 
			//throw new SQLException("The batched statement at index " + j + " failed to execute.");  
			} else { num += rowCounts[j]; }  
			}  
			 */
		} catch (java.lang.ClassCastException ex) {
			throw new SQLException("java.lang.ClassCastException: " + ex.getMessage(), ex.getCause());
		} catch (NumberFormatException ex) {
			throw new SQLException("NumberFormatException: " + ex.getMessage(), ex.getCause());
		} catch (IOException ex) {
			throw new SQLException("IOException: " + ex.getMessage(), ex.getCause());
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
		return num;
	}
   /*参数同上。上述函数的改进版。原因：
	 * 对于只执行一次的SQL语句选择Statement是最好的. 相反, 如果SQL语句被多次执行选用PreparedStatement是最好的.
	 * PreparedStatement的第一次执行消耗是很高的. 它的性能体现在后面的重复执行

	   addBatch()和executeUpdate()是用来执行数据插入的，不同就是一个批量，一个单个插入。这两个不要一起使用，那样addBatch()就和没写一样。
       addBatch()是用来缓存数据的，将多条sql语句缓存起来，再通过executeBatch()方法一次性发给数据库，大大提高执行效率。
       executeUpdate()注重的及时性，每写一条sql语句就发送给数据库保存起来，没有缓存，这样频繁操作数据库效率非常低。
       
       addBatch(String sql) 将给定的 SQL 命令添加到此 Statement 对象的当前命令列表中。通过调用方法 executeBatch 可以批量执行此列表中的命令。
       int[] executeBatch() 将一批命令提交给数据库来执行，如果全部命令执行成功，则返回更新计数组成的数组。
       String buf = SQL; 
       Statement stmt =null;    
       stmt.addBatch(buf);   
       int []line = stmt.executeBatch();
    		
	 例子1	 
	  PreparedStatement ps = conn.prepareStatement("INSERT into employees values (?, ?, ?)");     
     for (n = 0; n < 100; n++) {     
	      ps.setString(name[n]);     
	      ps.setLong(id[n]);     
	      ps.setInt(salary[n]);     
	      ps.executeUpdate();
	   }
     
        例子2
        PreparedStatement ps = conn.prepareStatement("INSERT into employees values (?, ?, ?)");     
		 for (n = 0; n < 100; n++) {     
			  ps.setString(name[n]);     
			  ps.setLong(id[n]);     
			  ps.setInt(salary[n]);     
			  ps.addBatch();     
		 }      
		 ps.executeBatch();      
   
                       在例 1中, PreparedStatement被用来多次执行INSERT语句. 在这里, 执行了100次INSERT操作, 共有101次网络往返.
                       其中,1次往返是预储statement, 另外100次往返执行每个迭代. 在例2中, 当在100次INSERT操作中使用addBatch()方法时,
                      只有两次网络往返. 1次往返是预储statement, 另一次是执行batch命令. 虽然Batch命令会用到更多的数据库的CPU周期,
                       但是通过减少网络往返，性能得到提高. 记住, JDBC的性能最大的增进是减少JDBC驱动与数据库之间的网络通讯.
                       
	   PreparedStatement实现的，在初始化的时候就得传一个sql语句进去，然后对于?参数再使用setXxx()填充进去，作者为了偷懒就没有实现多条语句的操作。
	       即只涉及一个表的多记录插入。利用Statement可以解决此问题
	 
	  Statement它更适合执行不同sql的批处理，它没有提供预处理功能，性能比较低。
	  PreparedStatement它适合执行相同的批处理，它提供了预处理功能，属性比较高。
	*/
	/** @param whereStr 是操作条件，插入时不起作用，更新时， 若where==null||"".equals(where)，
	 *                  则自动根据库表主键字段的键值对组合where条件语句;从LinkedHashMap参数中获取的值
	 * 	根据isUpdateKey对有效字段继续分组：!true时，再得主键字段组Object[] keys和非主键字段组Object[] fields
	 */
	private ArrayList<String> _preparedBatchUpdate(String tableNameT,
			                  List<Map<String, Object>> listRecord,
			                  boolean isUpdateKey, String whereStr) {

		ArrayList<String> arrayList = new ArrayList<String>();

		if (StringTool.isNull(tableNameT) || StringTool.isNull(listRecord)) {
			return arrayList;
		}
		String tableName = tableNameT.toLowerCase();
		String[] tableFields = dbCenterImp.getFields(tableName);//获取表中所有字段
		String[] tableKeys = dbCenterImp.getKeys(tableName);//获取表中的主键
		
		/**获取一条记录，作为过滤、分组依据。*/
		Map<String, Object> temp = listRecord.get(0);
		//_m中包含是用户传过来的一条记录的所有字段和其对应的值
		Map<String, Object> _m = new LinkedHashMap<String, Object>(temp);
		 //获取记录里的字段名的集合
		Object[] recordFields = (_m.keySet()).toArray();
		for (int i = 0; i < recordFields.length; i++) {
			if (!StringTool.isInFields(tableFields, recordFields[i].toString())) {
				//移除无效字段即移除库表中不存在的字段，查看记录中的字段在表中是否存在，如果不存在，则移除
				_m.remove(recordFields[i].toString());
			}
		}
		Object[] k0 = (_m.keySet()).toArray(); //过滤后的有效字段,一般来讲：对应表的字段
		
	    //记录表中的主键		
		Map<String, Object> key_m = new LinkedHashMap<String, Object>();//记录库表的主键
		if (!isUpdateKey) {//不更新主键
			for (int i = 0; i < k0.length; i++) {
				if (StringTool.isInFields(tableKeys, k0[i].toString())) {//记录中是否有主键
					key_m.put(k0[i].toString(), _m.remove(k0[i].toString()));//将记录中的主键移到key_m中；保证不对主键更新
				}
			}
		}
		Object[] fields = (_m.keySet()).toArray(); //记录中不包含主键的有效字段
		Object[] keys = (key_m.keySet()).toArray(); //记录中包含的主键
		
		//如果不更新主键，则key_m为空，即keys为空，而tableKeys不空，所以执行如下代码时，if语句总执行。XBM
		/*if (isUpdateKey) {//要更新主键，但主键非法，则返回
			if (keys.length == 0 || keys.length != tableKeys.length) {
				return arrayList;
			}
		}*/
		
		String sqlPre = "UPDATE " + tableName + " SET " ;
		String whereCols = "";
		for (Map<String, Object> record : listRecord) {//处理所有记录
			//默认为AND关系
			if (StringTool.isNullEmpty(whereStr))  {//isNull xbm
				if (keys.length > 0 && StringTool.isNull(whereCols)) {
					Field f = dbCenterImp.getField(tableName, keys[0].toString());
					Object _o = record.get(keys[0].toString());
					String _s = "";
					if(null != _o ) {//xbm 增加判断条件
						if (f.getTypeClassName().equals("java.lang.String")) {
							_s = keys[0].toString() + " LIKE '" + _o.toString() + "'";
						} else {
							_s = keys[0].toString() + " = " + _o.toString();
						}
						if (keys.length > 1) {
							for (int i = 1; i < keys.length; i++) {
								f = dbCenterImp.getField(tableName, keys[i].toString());
								_o = record.get(keys[i].toString());
								if (f.getTypeClassName().equals("java.lang.String")) {
									_s = _s + " AND " + keys[i].toString() + " LIKE '" + _o.toString() + "'";
								} else {
									_s = _s + " AND " + keys[i].toString() + " = " + _o.toString();
								}
							}
						}
						whereCols = " WHERE " + _s;
					}
				}
			}
			else
				whereCols = " WHERE " + whereStr;
			
			String updateCols = "";
			//fields保存的是记录中不包含主键的有效字段；
			for (int i = 0; i < fields.length; i++){//处理每条记录 
				String fieldName = fields[i].toString();//字段名称
				Field f = dbCenterImp.getField(tableName, fieldName);
				String className = f.getTypeClassName();
				Object v = record.get(fieldName);//获取对应字段所对应的具体值

				String fieldValue = "";
				//if (v == null) {//XBM 原代码。由于不能保证v永远是一个对象，即v多数情况下可能是取null值。所以变更代码如下：
				if ( (v == null) || "null".equals(v) ) {
					fieldValue = "";
				} 
				//else if (v != null) {XBM 原代码。
				else {
					String _c = ((Class<? extends Object>) v.getClass()).getName();
					if (className.equals("java.lang.Integer")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + Integer.parseInt((String) v);
						} else {
							if (_c.equals("java.lang.Integer")) {
								fieldValue = "" + ((Integer) v).intValue();
							} else {
								Integer n = new Integer(v.toString());
								fieldValue = "" + n;
							}
						}
					}
					else if (className.equals("java.lang.Long")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + Long.parseLong((String) v);
						} else {
							if (_c.equals("java.lang.Long")) {
								fieldValue = "" + ((Long) v).longValue();
							} else {
								Long l = new Long(v.toString());
								fieldValue = "" + l;
							}
						}
					}
					else if (className.equals("java.lang.Short")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + Short.parseShort((String) v);
						} else {
							fieldValue = "" + ((Short) v).shortValue();
						}
					}
					else if (className.equals("java.lang.Float")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + Float.parseFloat((String) v);
						} else {
							fieldValue = "" + ((Float) v).floatValue();
						}
					}
					else if (className.equals("java.lang.Double")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + Double.parseDouble((String) v);
						} else {
							fieldValue = "" + ((Double) v).doubleValue();
						}
					}
					else if (className.equals("java.lang.Boolean")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + (Boolean.valueOf((String) v)).booleanValue();
						} else {
							fieldValue = "" + ((Boolean) v).booleanValue();
						}
					}
					else if (className.equals("java.sql.Date")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
								 fieldValue = (String) v;
							 }
							 else
								 fieldValue = "" + java.sql.Date.valueOf((String) v);
						} else if (_c.equals("java.util.Date")) {
							java.util.Date _v = (java.util.Date) v;
							fieldValue = "" + new java.sql.Date(_v.getTime());
						} else {
							fieldValue = "" + (java.sql.Date) v;
						}
					}
					else if (className.equals("java.util.Date")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
								 fieldValue = (String) v;
							 }
							 else
								 fieldValue = "" + java.sql.Date.valueOf((String) v);
						} else if (_c.equals("java.util.Date")) {
							java.util.Date _v = (java.util.Date) v;
							fieldValue = "" + new java.sql.Date(_v.getTime());
						} else {
							fieldValue = "" + (java.sql.Date) v;
						}
					}
					else  if (className.equals("java.lang.String")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + (String) v;
						}
					}
				}
				if( ! "".equals(updateCols))
					updateCols +=",";	
				
				updateCols += fieldName + "='"+fieldValue+"'";
                
			} //处理每条记录 
			//记录下一条完整的SQL语句
			String sql =  sqlPre + updateCols + whereCols;
			arrayList.add(sql);
		}//所有记录的处理
		return arrayList;
	}
	//@param autoInsertKey 值为true时，自动插入主键
	private ArrayList<String> _preparedBatchInsert(String tableNameT,
			                  List<Map<String, Object>> listRecord,
			                  boolean autoInsertKey)
			throws SQLException 
	 {
		ArrayList<String> arrayList = new ArrayList<String>();
		if (StringTool.isNull(tableNameT) ||
			StringTool.isNull(listRecord) || 
			listRecord.size() <=0 ) {
			return arrayList;
		}

		String tableName = tableNameT.toLowerCase();
		String[] tableFields = dbCenterImp.getFields(tableName);//获取表中所有字段
		String[] tableKeys = dbCenterImp.getKeys(tableName);//获取表中的主键
		
		//获取一条记录，作为过滤、分组依据 _m中包含是用户传过来的一条记录的所有字段和其对应的值
		Map<String, Object> temp = listRecord.get(0);
		Map<String, Object> _m = new LinkedHashMap<String, Object>(temp);
		
		Object maxFieldValue = null;
		if (autoInsertKey) {//防止记录中不含tableKeys[0]主键字段
			Map<?, ?> lastRecord = this.queryLastRecord(tableName);//准备自动插入的主键
			if (tableKeys.length > 0) {
				maxFieldValue = lastRecord.get(tableKeys[0]);
				_m.put(tableKeys[0], "");
			} else {
				maxFieldValue = lastRecord.get(tableFields[0]);
				_m.put(tableFields[0], "");
			}
		}
		
		Object[] recordFields = (_m.keySet()).toArray(); //获取记录里的字段名的集合
		for (int i = 0; i < recordFields.length; i++) {
			if (! StringTool.isInFields(tableFields, recordFields[i].toString())) {
				_m.remove(recordFields[i].toString());//移除无效字段即移除库表中不存在的字段， 查看记录中的字段在表中是否存在，如果不存在，则移除到
			}
		}
		Object[] fields = (_m.keySet()).toArray(); //过滤后的有效字段
		String sql_field = tool.arryToString(fields, ",");
		
		String sqlPre = "INSERT INTO " + tableName + " (" + sql_field + ") VALUES (" ;

		long firstStringKey = (new java.util.Date()).getTime();//自动主键值
		for (Map<String, Object> record : listRecord) {//处理所有记录
			if (autoInsertKey) {//自动插入主键
				if (tableKeys.length > 0) {
					Field keyF = dbCenterImp.getField(tableName, tableKeys[0]);
					if ("java.lang.Long".equals(keyF.getTypeClassName())) {//时间+4位整数
						record.put(tableKeys[0], dbCenterImp.getTable(tableName).makeLongKeyValue());
					} else if ("java.lang.String".equals(keyF.getTypeClassName())) {
						if (Integer.parseInt(keyF.getSize()) > 13) {
							record.put(tableKeys[0], "" + (firstStringKey + 1));//自动插入字符型主键值//if (className.equals("java.lang.String") && Integer.parseInt(f.getSize()) > 13) 
						} else {
							return arrayList;
						}
					} else {
						maxFieldValue = dbCenterImp.getTable(tableName).makeObjectKeyValue(keyF, maxFieldValue);
						record.put(tableKeys[0], maxFieldValue);//自动插入数值型主键值
					}
				} else {
					Field keyF = dbCenterImp.getField(tableName, tableFields[0]);
					if ("java.lang.Long".equals(keyF.getTypeClassName())) {//时间+4位整数
						record.put(tableFields[0], dbCenterImp.getTable(tableName).makeLongKeyValue());
					} else if ("java.lang.String".equals(keyF.getTypeClassName())) {
						if (Integer.parseInt(keyF.getSize()) > 13) {
							record.put(tableFields[0], "" + (firstStringKey + 1));//自动插入字符型主键值//if (className.equals("java.lang.String") && Integer.parseInt(f.getSize()) > 13) 
						} else {
							return arrayList;
						}
					} else {
						maxFieldValue = dbCenterImp.getTable(tableName).makeObjectKeyValue(keyF, maxFieldValue);
						record.put(tableFields[0], maxFieldValue);//自动插入数值型主键值
					}
				}
			}//自动插入主键

			String value = "";
			//fields保存的是记录中不包含主键的有效字段；
			for (int i = 0; i < fields.length; i++){//处理每条记录 
				String fieldName = fields[i].toString();//字段名称
				Field f = dbCenterImp.getField(tableName, fieldName);
				String className = f.getTypeClassName();//库表中的类型
				Object v = record.get(fieldName);//获取对应字段所对应的具体值 表中的值
				 
				String fieldValue = "";
				//if (v == null) {//xbm原代码
				if (v == null || "null".equals(v)) {
                    if (className.equals("java.sql.Date") || className.equals("java.util.Date"))
                    	fieldValue = "2000-01-01";
                    else if (className.equals("java.lang.Integer") )
                    	fieldValue = "0";//无意义
                    else		
                    	fieldValue = "";
				} 
				//else if (v != null) {//xbm原代码
				else  {
					 String _c = ((Class<? extends Object>) v.getClass()).getName();//对象的类型
					// MyLogger.showMessage(fieldName +"::"+ _c  + "::"  + v); 
				     if (className.equals("java.lang.String")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + (String) v;
						}
					}
					else if (className.equals("java.sql.Date")) {
						if ((_c.equals("java.lang.String"))) {
							if (!"".equals(((String) v).trim()) ){
								 if (((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
									 fieldValue = (String) v;
								 }
								 else
									 fieldValue = "" + java.sql.Date.valueOf((String) v);
							}
							else
								fieldValue = "2000-01-01";
						} else if (_c.equals("java.util.Date")) {
							java.util.Date _v = (java.util.Date) v;
							fieldValue = "" + new java.sql.Date(_v.getTime());
						} else {
							fieldValue = "" + v;
						}
					}
					else if (className.equals("java.util.Date")) {
						if ((_c.equals("java.lang.String"))) {
							if (!"".equals(((String) v).trim()) ){
								 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
									 fieldValue = (String) v;
								 }
								 else
									fieldValue = "" + java.sql.Date.valueOf((String) v);
							}
							else
								fieldValue = "2000-01-01";
						} else if (_c.equals("java.util.Date")) {
							java.util.Date _v = (java.util.Date) v;
							fieldValue = "" + new java.sql.Date(_v.getTime());
						} else {
							fieldValue = "" + v;
						}
					}
					else
					if (className.equals("java.lang.Integer")) {
						if ((_c.equals("java.lang.String"))) {
							if (StringTool.isNumeric(((String) v).trim()) ) 
								fieldValue = "" + Integer.parseInt((String) v);
							else
								fieldValue = "0";
						} else {
							if (_c.equals("java.lang.Integer")) {
								fieldValue = "" + ((Integer) v).intValue();
							} else {
								Integer n = new Integer(v.toString());
								fieldValue = "" + n;
							}
						}
					}
					else if (className.equals("java.lang.Long")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + Long.parseLong((String) v);
						} else {
							if (_c.equals("java.lang.Long")) {
								fieldValue = "" + ((Long) v).longValue();
							} else {
								Long l = new Long(v.toString());
								fieldValue = "" + l;
							}
						}
					}
					else if (className.equals("java.lang.Short")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + Short.parseShort((String) v);
						} else {
							fieldValue = "" + ((Short) v).shortValue();
						}
					}
					else if (className.equals("java.lang.Float")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + Float.parseFloat((String) v);
						} else {
							fieldValue = "" + ((Float) v).floatValue();
						}
					}
					else if (className.equals("java.lang.Double")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + Double.parseDouble((String) v);
						} else {
							fieldValue = "" + ((Double) v).doubleValue();
						}
					}
					else	if (className.equals("java.lang.Boolean")) {
						if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
							fieldValue = "" + (Boolean.valueOf((String) v)).booleanValue();
						} else {
							fieldValue = "" + ((Boolean) v).booleanValue();
						}
					}
				}
				
				//组装Insert子句
				if (! "".equals(value)) {
					value += ",";
				}
				value += "'" + fieldValue + "'";
			
			} //处理每条记录 
			//记录下一条完整的SQL语句
			String sql =  sqlPre + value + ") ;";
			arrayList.add(sql);
			
		}//所有记录的处理

		return arrayList;
	}

	//批量删除  目前仅支持AND 
	private ArrayList<String> _preparedBatchDelete(String tableNameT,
			                  List<Map<String, Object>> listRecord, 
			                  String whereStr) 
     {

		ArrayList<String> arrayList = new ArrayList<String>();

		if (StringTool.isNull(tableNameT) || StringTool.isNull(listRecord)) {
			return arrayList;
		}
		String tableName = tableNameT.toLowerCase();
		Map<String, Object> temp = listRecord.get(0);
		//获取一条记录，作为过滤、分组依据 _m中包含是用户传过来的一条记录的所有字段和其对应的值
		Map<String, Object> _m = new LinkedHashMap<String, Object>(temp);
		String[] tableFields = dbCenterImp.getFields(tableName);//获取表中所有字段
		Object[] recordFields = (_m.keySet()).toArray(); //获取记录里的字段名的集合
		for (int i = 0; i < recordFields.length; i++) {
			if (!StringTool.isInFields(tableFields, recordFields[i].toString())) {
				_m.remove(recordFields[i].toString());//移除无效字段即移除库表中不存在的字段， 查看记录中的字段在表中是否存在，如果不存在，则移除到
			}
		}
		Object[] fields = (_m.keySet()).toArray();//所有有效字段 

		//String sqlPre = "DELETE FROM " + tableName + " ";
		String sqlPre = "DELETE FROM " + tableName + " WHERE ";
		
		if (StringTool.isNull(whereStr))//null 返回true
		{
			//sqlPre += " WHERE ";
			String whereCols = "";
			for (Map<String, Object> record : listRecord) {//处理所有记录
				whereCols = "";
				for (int i = 0; i < fields.length; i++){//处理每条记录   fields保存的是记录中不包含主键的有效字段； 
					String fieldName = fields[i].toString();//字段名称
					Field f = dbCenterImp.getField(tableName, fieldName);
					String className = f.getTypeClassName();

					Object v = record.get(fieldName);//获取对应字段所对应的具体值
					String fieldValue = "";
					
					//if (v == null) {//XBM 原代码。由于不能保证v永远是一个对象，即v多数情况下可能是取null值。所以变更代码如下：
					if ( (v == null) || "null".equals(v) ) {     
						if (className.equals("java.sql.Date") || className.equals("java.util.Date"))
							fieldValue = "2000-01-01";
						else if (className.equals("java.lang.Integer") )
							fieldValue = "0";//无意义
						else if (className.equals("java.sql.Timestamp"))
							fieldValue = "2000-01-01 00:00:00";
						else
							fieldValue = "";
					} 
					//else if (v != null) {//XBM 原代码
					else {
						String _c = ((Class<? extends Object>) v.getClass()).getName();
						if (className.equals("java.lang.String")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								fieldValue = "" + (String) v;
							}
						}else if (className.equals("java.lang.Integer")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								fieldValue = "" + Integer.parseInt((String) v);
							} else {
								if (_c.equals("java.lang.Integer")) {
									fieldValue = "" + ((Integer) v).intValue();
								} else {
									Integer n = new Integer(v.toString());
									fieldValue = "" + n;
								}
							}
						} else if (className.equals("java.sql.Timestamp")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								String _s = ((String) v).trim();
								if (StringTool.matches(RegexType.chinaDate, _s)) {//如：2012-01-24
									Time t = new Time(0l);
									_s = _s + " " + t.toString();
									fieldValue = "" + java.sql.Timestamp.valueOf(_s);
								} else {
									fieldValue = "" + java.sql.Timestamp.valueOf((String) v);
								}
							} else if (_c.equals("java.sql.Date")) {
								java.sql.Date _v = (java.sql.Date) v;
								fieldValue = "" + new Timestamp(_v.getTime());
							} else if (_c.equals("java.util.Date")) {
								java.util.Date _v = (java.util.Date) v;
								fieldValue = "" + new Timestamp(_v.getTime());
							} else if (_c.equals("java.sql.Time")) {
								java.sql.Time _v = (java.sql.Time) v;
								fieldValue = "" + new Timestamp(_v.getTime());
							} else {
								fieldValue = "" +  new Timestamp(((java.util.Date) v).getTime());//能支持更多的应用
								//fieldValue = "" +  (java.sql.Timestamp) v;//使用jsf日期转换后获得的结果可能不完整，这时会出现转换异常
							}
						}else if (className.equals("java.lang.Long")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								fieldValue = "" + Long.parseLong((String) v);
							} else {
								if (_c.equals("java.lang.Long")) {
									fieldValue = "" + ((Long) v).longValue();
								} else {
									Long l = new Long(v.toString());
									fieldValue = "" + l;
								}
							}

						}
						else if (className.equals("java.lang.Short")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								fieldValue = "" + Short.parseShort((String) v);
							} else {
								fieldValue = "" + ((Short) v).shortValue();
							}
						}
						else if (className.equals("java.lang.Float")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								fieldValue = "" + Float.parseFloat((String) v);
							} else {
								fieldValue = "" + ((Float) v).floatValue();
							}
						}
						else if (className.equals("java.lang.Double")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								fieldValue = "" + Double.parseDouble((String) v);
							} else {
								fieldValue = "" + ((Double) v).doubleValue();
							}
						}
						else	if (className.equals("java.lang.Boolean")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								fieldValue = "" + (Boolean.valueOf((String) v)).booleanValue();
							} else {
								fieldValue = "" + ((Boolean) v).booleanValue();
							}
						}
						else if (className.equals("java.sql.Date")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
									 fieldValue = (String) v;
								 }
								 else
									 fieldValue = "" + java.sql.Date.valueOf((String) v);
							} else if (_c.equals("java.util.Date")) {
								java.util.Date _v = (java.util.Date) v;
								fieldValue = "" + new java.sql.Date(_v.getTime());
							} else {
								fieldValue = "" + (java.sql.Date) v;
							}
						}
						else if (className.equals("java.util.Date")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								 if ( ((String) v).indexOf("-") == -1){//XBM不存在 即非yyyy-mm-dd格式类型的日期
									 fieldValue = (String) v;
								 }
								 else
									 fieldValue = "" + java.sql.Date.valueOf((String) v);
							} else if (_c.equals("java.util.Date")) {
								java.util.Date _v = (java.util.Date) v;
								fieldValue = "" + new java.sql.Date(_v.getTime());
							} else {
								fieldValue = "" + (java.sql.Date) v;
							}
						}



					}
					//组装where子句
					if (! "".equals(whereCols)) {
						whereCols += " AND ";
					}
					whereCols += "(" + fieldName + "='"+fieldValue+"'" + ")";
				} //处理每条记录 

				String sql =  sqlPre + whereCols;
				arrayList.add(sql);
			}
		}
		else{
			arrayList.add(sqlPre + whereStr);
		}
		//记录下一条完整的SQL语句
		return arrayList;
	}

	//多表批处理
	/**
	 * 尽量使用：doEexcuteBatchSql(String []sql)
	 * 支持事务处理   批量执行不支持Select语句
	 * 
	 * sqlStr String[]  存放sql语句的数组
	 * 
	 *  对于delete、replace语句来讲，如果没有满足条件的记录，返回值也是0，这里res[i] <= 0)表示操作有错
	 *  对insert，update, 返回0表示操作失败。但对于多表进行delete时，可能无法满足要求（一个表可以删除，另表不一定存在）
	 *  对于update,如果库中无相应的记录时,更新会出错,所以，在组装SQL语句时需要注意各个SQL语句的顺序
	 *  deleteFlag 删除标记 true
	 */
	/*autoflag isUpdateKey autoInsertKey 或null
	 wherest
	 */
	@Override
	public int batchMultiTable(List<String> tableList, //要操作的表名列
			                   List<String> opList,//操作类别列
			                   List<List<Map<String, Object>>> recordList,//操作数据
			                   List<String> whereList,//where子句
			                   List<Boolean> flagList)//是否更新主键 
     throws SQLException
	{
		int num = 0;
		//表名和对应的操作（delete update insert）
		//IdentityHashMap<String,String> tableMap = new LinkedIdentityHashMap<String,String>();
		int size = tableList.size();
		if((size == opList.size()) && (size == recordList.size()) &&
		   (size == whereList.size()) && (size == flagList.size()) )
		{
			List<Map<String, Object>> listRecord = new ArrayList<Map<String, Object>>();
			String opName = "";
			String tableName = "";
			String whereStr = "";
			Boolean autoflag = true;

			ArrayList<String> arrayList =  new ArrayList<String>();
			for (int i = 0; i < size; i++){
				ArrayList<String> arrayListT =  new ArrayList<String>();

				opName = opList.get(i);
				tableName = tableList.get(i);
				listRecord = recordList.get(i);
				whereStr = whereList.get(i);
				autoflag = flagList.get(i);

				if(opName.equalsIgnoreCase("delete"))
					arrayListT = _preparedBatchDelete(tableName,listRecord, whereStr);
				else if(opName.equalsIgnoreCase("insert"))//OK
					arrayListT = _preparedBatchInsert(tableName,listRecord,autoflag);
				else if(opName.equalsIgnoreCase("update"))
					arrayListT = _preparedBatchUpdate(tableName,listRecord,autoflag, whereStr);
                
				if ( arrayListT != null && arrayListT.size() > 0)
                	arrayList.addAll(arrayListT);
				
			}
			size = arrayList.size();
			if(size == 0) return num;

			String [] sqls = new String [size];
			for(int i = 0; i < size; i++){
			//	System.out.println((String)arrayList.get(i));
				
				sqls[i] = (String)arrayList.get(i);
			}
			
			return batchMultiTable(sqls);
		}
		else 
			return num;
	} 

   @Override
   public int batchMultiTable(String[] sql) throws SQLException 
   {
	   int num = 0;
	   
	   if(sql.length <= 0) return num;

	   Statement stmt = null; 
	   
	   try {
		   stmt = con.createStatement();
		   if(stmt == null)
			   return num;
		   
		   for(int i = 0; i < sql.length; i++){
			   Console.showMessage(sql[i]);
			   stmt.addBatch(sql[i]);
		   }
		
		   int[] rowCounts = stmt.executeBatch();
		   for (int j = 0; j < rowCounts.length; j++) { 
			   if (rowCounts[j] == Statement.SUCCESS_NO_INFO) { //成功执行，但受影响的行数是未知的  
				   num = 5;//随意设置
			   } 
			   else if (rowCounts[j] == Statement.EXECUTE_FAILED) { //指示未能成功执行命
				   throw new SQLException("The batched statement at index " + j + " failed to execute.");  
			   } 
			   else { 
				   num += rowCounts[j]; 
			   }  
		   }
	   } catch (SQLException e) {
		   throw e;
	   } finally {
		   if (stmt != null)
				stmt.close();
	   } 
	   return num;
   }

   //////////////////////////////////////////批处理SQL语句
     
	/**
	 * 将ResultSet结果集中的记录映射到Map对象中.
	 *
	 * @param fieldClassName 是JDBC API中的类型名称,
	 * @param fieldName 是字段名，
	 * @param rs 是一个ResultSet查询结果集,
	 * @param fieldValue Map对象,用于存贮一条记录.
	 * @throws SQLException
	 */
	private void _recordMappingToMap(String fieldClassName, String fieldName, ResultSet rs, 
			Map<String, Object> fieldValue)	throws SQLException
	{
		fieldName = fieldName.toLowerCase();

		//优先规则：常用类型靠前
		if (fieldClassName.equals("java.lang.String")) {
			String s = rs.getString(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.lang.Integer")) {
			int s = rs.getInt(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);//早期jdk需要包装，jdk1.5后不需要包装
			}
		} else if (fieldClassName.equals("java.lang.Long")) {
			long s = rs.getLong(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.lang.Boolean")) {
			boolean s = rs.getBoolean(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.lang.Short")) {
			short s = rs.getShort(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.lang.Float")) {
			float s = rs.getFloat(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.lang.Double")) {
			double s = rs.getDouble(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.sql.Timestamp")) {
			java.sql.Timestamp s = rs.getTimestamp(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if ( fieldClassName.equals("java.sql.Date") ||
				    fieldClassName.equals("java.util.Date")) {
			java.util.Date s = rs.getDate(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.sql.Time")) {
			java.sql.Time s = rs.getTime(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.lang.Byte")) {
			byte s = rs.getByte(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, new Byte(s));
			}
		} else if (fieldClassName.equals("[B") || fieldClassName.equals("byte[]")) {
			//byte[]出现在SQL Server中
			byte[] s = rs.getBytes(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.math.BigDecimal")) {
			BigDecimal s = rs.getBigDecimal(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.lang.Object")|| fieldClassName.equals("oracle.sql.STRUCT")) {
			Object s = rs.getObject(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.sql.Array")|| fieldClassName.equals("oracle.sql.ARRAY")) {
			java.sql.Array s = rs.getArray(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.sql.Clob")) {
			java.sql.Clob s = rs.getClob(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else if (fieldClassName.equals("java.sql.Blob")) {
			java.sql.Blob s = rs.getBlob(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		} else {//对于其它任何未知类型的处理
			Object s = rs.getObject(fieldName);
			if (rs.wasNull()) {
				fieldValue.put(fieldName, null);
			} else {
				fieldValue.put(fieldName, s);
			}
		}
	}
	//////////////////////////////////////////////////////////
	/**
	 * 删除记录， 执行Statement的executeUpdate方法
	 *
	 * @param deleteSql 是一条标准的SQL删除语句
	 * @return 返回标准更新语句的返回值
	 * @throws SQLException
	 */
	@Override
	public int delete(String deleteSql) throws SQLException {
		return this.update(deleteSql);
	}
	
	@Override
	public int deleteAll(String table) throws SQLException {
		String deleteSql = "DELETE FROM "+ table;
		return this.update(deleteSql);
	}

	/**
	 * 批量删除记录， 执行Statement的executeUpdate方法
	 *
	 * @param deleteSql 是多条标准的SQL删除语句
	 * @return 返回标准更新语句的返回值
	 * @throws SQLException
	 */
	@Override
	public int delete(String[] deleteSql) throws SQLException {
		return this.update(deleteSql);
	}

	@Override
	public int delete(String tableName, Map<String, Object> mapRecord) throws SQLException
	{
		int num = 0;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(mapRecord);
		
		ArrayList<String> arrayList =  new ArrayList<String>();
		arrayList = _preparedBatchDelete(tableName,list, "");
		
		int size = arrayList.size();
		
		if(size == 0) return num;

		String [] sqls = new String [size];
		for(int i = 0; i < size; i++){
		
			sqls[++num] = (String)arrayList.get(i);
		}
		return delete(sqls);
		
	}
	
	@Override 
	public int delete(String table,String where) throws SQLException
	{
		if( StringTool.isNullEmpty(table))
			return 0;

		String deleteSql = "DELETE FROM "+ table;

		if(! StringTool.isNullEmpty(where))
			deleteSql = deleteSql + " WHERE " + where;

		return delete(deleteSql);
	} 
	 
	 
	//////////////////////////////////////////////////////////////////////////////////////////////	
	/**对于update，用executeUpdate执行后如返回的值是0表示操作失败；为正整数则成功*/
	/**
	 * 更新一条记录. 零长度字符串将保存为null。
	 * @param tableName 是表名
	 * @param mapRecord 是准备插入到表中的一条记录的数据,其键名与字段名相同,顺序无关;但键名不能是字段名以外的其它名称;
	 * @return 返回标准update方法的返回值
	 * @throws SQLException
	 */
	@Override
	public int update(String tableName, Map<String, Object> mapRecord) throws SQLException 
	{
		List<Map<String, Object>> a = new ArrayList<Map<String, Object>>();
		a.add(mapRecord);
		return this._preparedStatementUpdate(tableName, a, false, null);
	}

	@Override
	public int update(String updateSql,Object[] params) throws SQLException
	{  
		int count = 0;
		try {
			preStmt = con.prepareStatement(updateSql);
			if (preStmt == null)
				return count;
			if(params != null)
				for (int i=0; i<params.length; i++){
					preStmt.setObject(i + 1,params[i]);
				}
			//对于update，用executeUpdate执行后如返回的值是0则表示操作失败，为正整数则成功
			count = preStmt.executeUpdate();
		} catch (SQLException ex) {
			throw ex;
		} finally {
			closePreStmt();
		}
		return count;
	}

	/**
	 * 通过PreparedStatement自动更新多条记录
	 *
	 * @param tableName 表名
	 * @param listRecord 包含完整主键字段的记录集
	 * @return 返回标准update方法的返回值
	 * @throws SQLException
	 */
	@Override
	public int update(String tableName, List<Map<String, Object>> listRecord) throws SQLException {
		int num = 0;//xbm 0;
		if( StringTool.isNullEmpty(tableName))
			return num;
		
		return _preparedStatementUpdate(tableName, listRecord, false, null);
	}

	/**
	 * 保存一条记录.
	 * 如果该记录存在，则更新之，如果该记录不存在，则插入。如果第一个主键的值为null||""，则自动插入新的主键值，这个方法不适合对含多主键的表进行插入操作，但不影响对多主键的表进行更新
	 *
	 * @param tableName 是表名
	 * @param listRecord 是准备插入到表中的多条记录的数据,其键名与字段名相同,顺序无关;但键名不能是字段名以外的其它名称;
	 * @param where 是一个标准的where子句.
	 * @return 返回标准update方法返回的状态值
	 * @throws SQLException
	 */
	@Override
	public int update(String tableName, List<Map<String, Object>> listRecord, String where) throws SQLException {
		int num = 0;//xbm0;
		if( StringTool.isNullEmpty(tableName))
			return num;
		
		return _preparedStatementUpdate(tableName, listRecord, false, where);
		
	}
	/**
	 * 执行Statement的executeUpdate方法，如：插入、删除、更新记录
	 *
	 * @param updateSql 是一条标准的SQL更新语句，可以是插入、删除、更新.
	 * @return 返回标准更新语句的返回值
	 * @throws SQLException
	 */
	@Override
	public int update(String updateSql) throws SQLException {
		int count = 0;
		try {
			stmt = con.createStatement();
			count = stmt.executeUpdate(updateSql);
		} catch (SQLException ex) {
			throw ex;
		} finally {
			this.closeStmt();
		}
		return count;
	}
	
	/** executeUpdate用于执行 INSERT、UPDATE 或 DELETE 语句以及 SQL DDL（数据定义语言）语句，例如 CREATE TABLE 和 DROP TABLE
	 *  对于 CREATE TABLE 或 DROP TABLE 等不操作行的语句，executeUpdate的返回值总为零。
	 *  对于INSERT、UPDATE 或 DELETE 语句.返回值是一个整数，指示受影响的行数（即更新计数）
	 *  如果库中没有任何记录,进行删除时,返回的值是0,此方法却返回false.
	 *  paramFlag表示有无参数 true表示有参数
	 *  delFlag表示是否删除操作 true表示删除操作
	 */
	@Override
	public boolean update(String sql, Object[] params, boolean paramFlag, boolean delFlag) throws SQLException {
		boolean temp = false;
		if (null == sql || sql.length() == 0 )
			return temp;
		try {
			preStmt = con.prepareStatement(sql);
			if (preStmt == null)
				return temp;
			if(paramFlag && params != null)
				for (int i=0; i<params.length; i++){
					preStmt.setObject(i + 1,params[i]);
				}
			if (preStmt == null)
				return temp;

			int count =  preStmt.executeUpdate(); //返回值是更新的记录条数。返回值为0表示执行出错。

			//对于insert,update等，用executeUpdate执行后如返回的值是0则表示操作失败，为正整数则成功
			/**关于删除操作：对于delete from tableName executeUpdate的执行结果是回成功删除的记录个数（>=0）
			 * 对于truncate table tableName executeUpdate的执行结果总是0
			 */
			if( (delFlag) &&  (count >= 0))
				temp= true;
			else
				if (count > 0)
					temp= true;	
		} catch (SQLException ex) {
			throw ex;
		} finally {
			closePreStmt();
		} 
		return temp;	
	}

	
	/**
	 * 执行Statement的executeUpdate方法，如：更新记录
	 *
	 * @param updateSql 是一组标准的SQL更新语句，可以是插入、删除、更新.
	 * @return 返回标准更新语句的返回值
	 * @throws SQLException
	 */
	@Override
	public int update(String[] updateSql) throws SQLException {
		int count = 0;
		try {
			stmt = con.createStatement();
			for (int i = 0; i < updateSql.length; i++) {
				stmt.executeUpdate(updateSql[i]);
			}
			count = stmt.getUpdateCount();
		} catch (SQLException ex) {
			throw ex;
		} finally {
			this.closeStmt();
		}
		return count;
	}
	/**
	 * 更新一条记录. 零长度字符串将保存为null。
	 *
	 * @param tableName 是表名
	 * @param mapRecord 是准备插入到表中的一条记录的数据,其键名与字段名相同,顺序无关
	 * @param where 是一个标准的where子句.
	 * @return 返回标准update方法的返回值
	 * @throws SQLException
	 */
	@Override
	public int update(String tableName, Map<String, Object> mapRecord, String where) throws SQLException
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(mapRecord);
		return this._preparedStatementUpdate(tableName, list, false, where);
	}
	
	@Override
	public int update(String tableName, Map<String, Object> mapRecord, boolean isUpdateKey, String where) throws SQLException
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(mapRecord);
		return this._preparedStatementUpdate(tableName, list, isUpdateKey, where);
	}
	
	@Override
	public int update_raw(String tableName, Map<String, Object> mapRecord,String where) throws SQLException
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(mapRecord);
		return this._preparedStatementUpdate_raw(tableName, list, where);
	}

	///////////////////////////////////////////////////////////////////// 
	/**
	 * 根据上次查询语句查询
	 *
	 * @param last 查询语句栈中的倒序数
	 * @throws SQLException
	 */
	@Override
	public List<Map<String, Object>> queryByLast(int last) throws SQLException {
		List<Map<String, Object>> v = new ArrayList<Map<String, Object>>();
		try {
			stmt = con.createStatement();
			String sqlquery = dbCenterImp.getLastQuerySql(last);
			rs = stmt.executeQuery(sqlquery);
			rsmd = rs.getMetaData();
			int fieldCount = rsmd.getColumnCount();
			while (rs.next()) {
				Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= fieldCount; i++) {
					String fieldClassName = rsmd.getColumnClassName(i);
					String fieldName = rsmd.getColumnName(i);
					this._recordMappingToMap(fieldClassName, fieldName, rs, valueMap);
				}
				v.add(valueMap);
			}
			this.closeRs();
		} finally {
			this.closeStmt();
		}
		return v;
	}

	/**
	 * 查询方法
	 *
	 * @param sqlquery 是标准查询语句,可以是任意复杂的多表查询语句,但必须是受JDBC API支持的标准查询语句
	 * @param fetchSize 预取数
	 * @return 获取查询结果集转化成List对象
	 * @throws SQLException
	 */
	@Override
	public List<Map<String, Object>> queryFetchSize(String sqlquery, int fetchSize) throws SQLException {
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		Map<String, Object> valueMap = null;
		int fieldCount = 0;
		stmt = con.createStatement();
		stmt.setFetchSize(fetchSize);//
		rs = stmt.executeQuery(sqlquery);
		rsmd = rs.getMetaData();
		fieldCount = rsmd.getColumnCount();
		int index = 0;
		while (rs.next()) {
			valueMap = new LinkedHashMap<String, Object>();
			for (int i = 1; i <= fieldCount; i++) {
				String fieldClassName = rsmd.getColumnClassName(i);
				String fieldName = rsmd.getColumnName(i);
				this._recordMappingToMap(fieldClassName, fieldName, rs, valueMap);
			}
			records.add(valueMap);
			index = index + 1;
		}
		rs.setFetchSize(fetchSize);
		this.closeRs();
		this.closeStmt();

		return records;
	}

	/**
	 * 查询表中第一条记录
	 *
	 * @param tableName 表名
	 * @return 获取表的第一条记录
	 * @throws SQLException
	 */
	@Override
	public Map<String, Object> queryFirstRecord(String tableName) throws SQLException {
		if( StringTool.isNullEmpty(tableName))
			return null;
		
		Table t = dbCenterImp.getTable(tableName);
		String fieldName = t.getFields()[0];
		if (t.getKeys().length > 0) {
			fieldName = t.getKeys()[0];
		}
		Field f = dbCenterImp.getField(tableName, fieldName);
		String sql = "select min(" + fieldName + ") as id from " + tableName;
		Map<String, Object> mm = this.queryOne(sql);
		if (f.getTypeClassName().equals("java.lang.String")) {
			sql = "select * from " + tableName + " where " + fieldName + " like '" + mm.get("id") + "'";
		} else {
			sql = "select * from " + tableName + " where " + fieldName + " = " + mm.get("id");
		}
		Map<String, Object> m = this.queryOne(sql);
		return m;
	}

	/**
	 * 查询表中最后一条记录
	 * @param tableName 表名
	 * @return 获取表的最后一条记录
	 * @throws SQLException
	 */
	@Override
	public Map<String, Object> queryLastRecord(String table) throws SQLException
	{
		if( StringTool.isNullEmpty(table))
			return null;
		
		Table t = dbCenterImp.getTable(table);
		if(t == null)  return null;
		
		String fieldName = t.getFields()[0];
		if (t.getKeys().length > 0) {
			fieldName = t.getKeys()[0];
		}
		Field f = dbCenterImp.getField(table, fieldName);
		String sql = "select max(" + fieldName + ") as id from " + table;
				
		Map<String, Object> mm = this.queryOne(sql);
		
		if (f.getTypeClassName().equals("java.lang.String")) {
			sql = "select * from " + table + " where " + fieldName + " like '" + mm.get("id") + "'";
		} else {
			sql = "select * from " + table + " where " + fieldName + " = " + mm.get("id");
		}
		Map<String, Object> m = this.queryOne(sql);
		
		return m;
	}
	/**
	 * 查询方法
	 * @return 将查询结果ResultSet对象转换成List类型的结果
	 * @throws SQLException
	 */
	@Override
	public List<Map<String, Object>> query(String sqlquery) throws SQLException 
	{
		Console.showMessage(sqlquery);
		return this.query(sqlquery, 0);
	}
	
	public List<Map<String, Object>> executeQuery(String tableName,String items,String where) throws SQLException 
	{
		if( StringTool.isNullEmpty(tableName))
			return null;

		String sqlquery = "SELECT * FROM "+ tableName;

		if(!StringTool.isNullEmpty(items))
			sqlquery = "SELECT " + items + " FROM "+ tableName;

		if(!StringTool.isNullEmpty(where))
			sqlquery = sqlquery + " " + where;

		
		Console.showMessage(sqlquery);
		return this.query(sqlquery, 0);
	}
	
	@Override
	public List<Map<String, Object>> queryAllField(String table, String where) throws SQLException
	{

		if( StringTool.isNullEmpty(table))
			return null;
	
		String sqlquery = "SELECT * FROM "+ table;

		if(! StringTool.isNullEmpty(where))
			sqlquery = sqlquery + " WHERE " + where;

		Console.showMessage(sqlquery);
		
		return this.query(sqlquery, 0);
	}
	
	@Override
	public List<Map<String, Object>> query(String table, String item, String where) throws SQLException
	{

		if( StringTool.isNullEmpty(table) )
			return null;

		String sqlquery = "SELECT * FROM "+ table;
		
		if(! StringTool.isNullEmpty(item))
			sqlquery = "SELECT " + item + " FROM "+ table;
		
		if(! StringTool.isNullEmpty(where))
			sqlquery = sqlquery + " WHERE " + where;
		
		Console.showMessage(sqlquery);
		
		return this.query(sqlquery, 0);

	}
	
	/**
	 * 查询一条记录
	 * @param tableName 是表名;
	 * @param fieldName 关键字段名;
	 * @param fieldValue 关键字段值.
	 * @return 根据指定条件,获取一条记录。
	 * @throws SQLException
	 */
	@Override
	public Map<String, Object> queryOne(String tableName, String fieldName, Object fieldValue) throws SQLException
	{
		if( StringTool.isNullEmpty(tableName) || 
			StringTool.isNullEmpty(fieldName) || 
			StringTool.isNull(fieldValue) )
			return null;
		
		String sql = "SELECT * FROM " + tableName + " WHERE " + fieldName + "='" + fieldValue.toString()+ "'";
		Console.showMessage(sql);   
	
        return queryOne(sql);
	}
	
	@Override
	public Map<String, Object> queryOne(String table, String item, String where) throws SQLException
	{
		if( StringTool.isNullEmpty(table))
			return null;

		String sql = "SELECT * FROM "+ table;
		if(! StringTool.isNullEmpty(item))
			sql = "SELECT " + item + " FROM "+ table;
		if(! StringTool.isNullEmpty(where))
			sql = sql + " WHERE " + where;
		
		//System.out.println("OOo:"+sql);
		
		return queryOne(sql);
	}
	
	@Override
	public Map<String, Object> queryOne(String table, String where) throws SQLException
	{
		if( StringTool.isNullEmpty(table))
			return null;

		String sql = "SELECT * FROM " + table;
		
		if(! StringTool.isNullEmpty(where))
			sql = sql + " WHERE " + where;
		
		Console.showMessage(sql);
		
		return queryOne(sql);
	}
	
	/**
	 * 查询一条记录
	 *
	 * @param sqlQuery 查询语句
	 * @return 获取一条记录。
	 * @throws SQLException
	 */
	@Override
	public Map<String, Object> queryOne(String sqlQuery) throws SQLException 
	{
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			rsmd = rs.getMetaData();
			int fieldCount = rsmd.getColumnCount();
			if (rs.next()) {
				for (int i = 1; i <= fieldCount; i++) {
					String fieldClassName = rsmd.getColumnClassName(i);
					String fieldName = rsmd.getColumnName(i);
					_recordMappingToMap(fieldClassName, fieldName, rs, m);
				}
			}
			//XBM
			//rs.close();
		} finally {
			this.closeRs();
			this.closeStmt();
		}
		//xbm 目前在不考虑其用途
		//dbCenterImp.setLastQuerySql(sqlQuery);//将最近一次查询语句保存到已查询队列尾部?

		return m;
	}

	@Override
	public List<Map<String, Object>> query(String[] fieldName, String tableName) throws SQLException
	{
		if( StringTool.isNullEmpty(tableName))
			return null;
			
		String fieldStr = "";
		if (fieldName == null || fieldName.length == 0){
			fieldStr = "*";
		}
		else
		for(int i =0 ; i< fieldName.length; i++){
			if (fieldStr.length() != 0)
				fieldStr += "," + fieldName[i];
			else
				fieldStr += fieldName[i];
		}
		String sql = "SELECT " + fieldStr + " FROM " + tableName;

		return query(sql);
	}

	@Override
	public List<Map<String, Object>> query(String table, String where) throws SQLException
	{
		if( StringTool.isNullEmpty(table))
			return null;


		String sqlquery = "SELECT * FROM "+ table;
		if(!StringTool.isNullEmpty(where))
			sqlquery = sqlquery + " WHERE " + where;

		return query(sqlquery); 

	}
	
	@Override
	public 	List<Map<String, Object>> query(String table,String sort, String order, String where, 
			                                Object[] params) throws SQLException
	{
		if( StringTool.isNullEmpty(table))
			return null;

		String sqlquery = "SELECT * FROM "+ table;// + " " + where + " ORDER BY " + sort + " " + order +" limit ?,?" ;

		if(!StringTool.isNullEmpty(where))
			sqlquery = sqlquery + " WHERE " + where;

		if(!StringTool.isNullEmpty(sort))
			sqlquery = sqlquery + " ORDER BY " + sort + " " + order;

		sqlquery = sqlquery + " limit ?,?" ;

		Console.showMessage(sqlquery);   
	    
		return query(sqlquery, params);

	}
	@Override
	public 	List<Map<String, Object>> query(String table,String items,String sort, String order, String where, 
			Object[] params) throws SQLException{

		if( StringTool.isNullEmpty(table))
			return null;

		String sqlquery = "SELECT * FROM "+ table;

		if(!StringTool.isNullEmpty(items))
			sqlquery = "SELECT " + items + " FROM "+ table;

		if(!StringTool.isNullEmpty(where))
			sqlquery = sqlquery + " WHERE " + where;

		if(!StringTool.isNullEmpty(sort))
			sqlquery = sqlquery + " ORDER BY " + sort + " " + order;

		sqlquery = sqlquery + " limit ?,?" ;

		return query(sqlquery, params);

	}
	/**
	 * 查询方法
	 * 每个map表示一条查询结果(含所有要选的字段)
	 * @return 将查询结果ResultSet对象转换成List类型的结果
	 */
	@Override
	public List<Map<String, Object>> query(String sql, Object[] params) throws SQLException
	{
		if (StringTool.isNullEmpty(sql))
			return null;
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try{
			preStmt = con.prepareStatement(sql);
			if (preStmt == null)
				return null;
			if(params != null)
			for (int i=0; i<params.length; i++){
				preStmt.setObject(i + 1,params[i]);
			}
			
			//System.out.println(preStmt);
			if (preStmt.execute()){//用于执行返回多个结果集
				rs = preStmt.getResultSet();//获得结果集
			}
			else{
				return null;
			}		
			
			rsmd = rs.getMetaData();
			int fieldCount = rsmd.getColumnCount();
			while (rs.next()) {
				Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= fieldCount; i++) {
					String fieldClassName = rsmd.getColumnClassName(i);
					String fieldName = rsmd.getColumnName(i);
					this._recordMappingToMap(fieldClassName, fieldName, rs, valueMap);
				}
				result.add(valueMap);
			}
		}finally {
			this.closeRs();
			this.closePreStmt();
		}
		//xbm 目前在不考虑其用途 缓存SQL
		//dbCenterImp.setLastQuerySql(sqlquery);
		return result;
	}

	/**
	 * 查询表中记录
	 *
	 * @param tableName 是表名
	 * @param where String对象where是where子句部分
	 * @return 获取记录数.
	 * @throws SQLException
	 */
	@Override
	public long queryCount(String tableName, String where) throws SQLException {
		
		if (StringTool.isNullEmpty(tableName))
			return 0;
		
		String sql = "SELECT count(*) as express FROM " + tableName;// + " WHERE " + where;
		
		if(!StringTool.isNullEmpty(where))
			sql = sql + " WHERE " + where;
		
		Console.showMessage(sql); 
		return _queryCountOfTable(sql);
	}
	
	public long executeCount(String condition) throws SQLException 
	{
		if (StringTool.isNullEmpty(condition))
			return 0;
		
		String sql = "SELECT count(*) as express FROM "; 
		
		if(!StringTool.isNullEmpty(condition))
			sql = sql + condition;
		
		Console.showMessage("sqlCount:" + sql);
		return _queryCountOfTable(sql);
	}
	/**
	 * 查询表中记录
	 *
	 * @param tableName 是表名.
	 * @return 获取记录数.
	 * @throws SQLException
	 */
	@Override
	public long queryCount(String tableName) throws SQLException 
	{
		if (StringTool.isNullEmpty(tableName))
			return 0;
		
		String sql = "select count(*) as express from " + tableName;
		return _queryCountOfTable(sql);
	}

	@Override
	public List<Map<String, Object>> query(String table,String sort, String order, String where, 
			                               int position, int length, boolean isScrollSenstive) throws SQLException
	 {
		if (StringTool.isNullEmpty(table))
			return null;
		
		String sqlquery = "SELECT * FROM "+ table;//+ " " + where + " ORDER BY " + sort + " " + order ;
		if(!StringTool.isNullEmpty(where))
			sqlquery = sqlquery + " WHERE " + where;
		
		if(!StringTool.isNullEmpty(sort))
			sqlquery = sqlquery + " ORDER BY " + sort + " " + order;
		
		return query(sqlquery, position, length, isScrollSenstive);
	 }
	
	
	/**
	 * 此方法一次从空中读取所有记录，然后指定的子集。
	 * 
	 * 通过一个可滚动结果集获取指定起始位置、指定长度的子结果集,不论isScrollSenstive真否，结果集均设定为只读.
	 *
	 * @param sqlquery 是标准查询语句,可以是任意复杂的多表查询语句,但必须是受JDBC API支持的标准查询语句
	 * @param position 记录起始位置,注意表中记录是从1开始;越界则返回0条记录
	 * @param length 是指定记录长度,若不够长度,则含position后的全部记录
	 * @param isScrollSenstive 指定结果集是否敏感
	 * @return
	 * 获取查询结果集转化成List对象,每一条记录映射成一个HashMap对象,这个HashMap对象的键名是表中的字段名，或是字段的别名，键值为字段值，键值的类型是字段所对应的JDBC
	 * API的Java类。若无记录则返回零长度List对象。
	 * @throws SQLException
	 */
	@Override
	public List<Map<String, Object>> query(String sqlquery, int position, int length, boolean isScrollSenstive) throws SQLException {
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		try {
			java.sql.DatabaseMetaData dmd = con.getMetaData();
			if (dmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)
					|| dmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
				if (isScrollSenstive) {
					stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
				} else {
					stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
				}
			}
			//MyLogger.showMessage(sqlquery);
			rs = stmt.executeQuery(sqlquery);
			rsmd = rs.getMetaData();
			int fieldCount = rsmd.getColumnCount();
			rs.last();
			int x = rs.getRow();
			if (position < 1 || position > x) {
				return records; //起始位置越界,则返回0条记录;
			}
			if (position + length > x) {
				length = x - (position - 1); //若起始位置后的记录数小于length,则取起始位置后的全部记录;
			}
			Map<String, Object> valueMap = null;
			if (rs.absolute(position)) {
				for (int k = position; k < position + length; k++) {
					valueMap = new HashMap<String, Object>();
					for (int i = 1; i <= fieldCount; i++) {
						String fieldClassName = rsmd.getColumnClassName(i);
						String fieldName = rsmd.getColumnName(i);
						this._recordMappingToMap(fieldClassName, fieldName, rs, valueMap);
					}
					records.add(valueMap);
					if (!rs.next()) {
						break;
					}
				}
			}
		} finally {
			this.closeStmt();
		}
		return records;
	}

	/**
	 * 查询结果集
	 *
	 * @param querySql 是标准查询语句,可以是任意复杂的多表查询语句,但必须是受JDBC API支持的标准查询语句
	 * @return 获取查询结果ResultSet对象
	 * @throws SQLException
	 */
	@Override
	public ResultSet queryResultSet(String querySql) throws SQLException {
		stmt = con.createStatement();
		rs = stmt.executeQuery(querySql);
		return rs;
	}

	/**
	 * 查询结果集
	 *
	 * @param querySql 是标准查询语句,可以是任意复杂的多表查询语句,但必须是受JDBC API支持的标准查询语句
	 * @param statement
	 * 用户定义的Statement对象，用来实现更高级的查询，如可更新的、可滚动的、只读的、敏感的、非敏感的，可以通过Statement
	 * statement=getCon().createStatement(ResultSet.参数0，ResultSet.参数1);的方式实现
	 * @return 获取查询结果ResultSet对象
	 * @throws SQLException
	 */
	@Override
	public ResultSet queryResultSet(String querySql, Statement statement) throws SQLException {
		rs = statement.executeQuery(querySql);
		return rs;
	}
	
	/**
	 * 查询方法
	 * @param timeout 设定查询时限，单位：秒；timeout=0,则查询不受时间限制
	 * @return 将查询结果ResultSet对象转换成List;Map;String,Object类型的结果
	 * @throws SQLException
	 */
	@Override
	public List<Map<String, Object>> query(String sqlquery, int timeout) throws SQLException 
	{   //每个元素对应一条记录的信息
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		try {
			stmt = con.createStatement();
			if (timeout > 0) {
				stmt.setQueryTimeout(timeout);
			}
			rs = stmt.executeQuery(sqlquery);
			rsmd = rs.getMetaData();
						
			int fieldCount = rsmd.getColumnCount();
			while (rs.next()) {//对每条记录进行处理
				Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= fieldCount; i++) {
					String fieldClassName = rsmd.getColumnClassName(i);
					String fieldName = rsmd.getColumnName(i);
					this._recordMappingToMap(fieldClassName, fieldName, rs, valueMap);
				}
				records.add(valueMap);
			}

		} finally {
			this.closeRs();
			this.closeStmt();
		}
		//xbm 目前在不考虑其用途
		//dbCenterImp.setLastQuerySql(sqlquery);
		
		return records;
	}

	/////////////////////////////////////////////////////////////////////////////
	/**
	 * 插入记录
	 *
	 * @param insertSql 插入语句
	 * @return 返回JDBC标准插入语句的返回值
	 * @throws SQLException
	 */
	@Override
	public int insert(String insertSql) throws SQLException {
		int num = this.update(insertSql);
		return num;
	}

	/**
	 * Run an SQL insert query and return generated index
	 * @param sql SQL query with ? for parameters
	 * @param params array of objects for query parameters  flag = true;
	 * @return integer value of the index generated by the database
	 */
	//插入记录一条,并返回id -1表示插入失败
	@Override
	public int doInsertGenIndex(String tableName, Map<String, Object> mapRecord) throws SQLException {
		int num = 0;
		num = this._preparedStatementInsert(tableName,mapRecord, true);
		return num;
	}
	//插入一条记录 @param autoInsertKey 值为true时，系统自动插入主键
	private int _preparedStatementInsert(String tableName, Map<String, Object> mapRecord, boolean autoInsertKey) throws SQLException	
	{
		int num = 0;
		
		if( StringTool.isNullEmpty(tableName) || StringTool.isNull(mapRecord)  )
			return num;
		
		String preparedStatementInsert = "";

		Map<String, Object> _m = mapRecord;//作为创建preparedStatementSQL语句的依据
		Object maxFieldValue = null;
		String[] tableFields = dbCenterImp.getFields(tableName);//表中的字段名的集合
		String[] tableKeys = dbCenterImp.getKeys(tableName);//表中的主键字段集
		
		if (autoInsertKey) {
			Map<?, ?> lastRecord = this.queryLastRecord(tableName);//准备自动插入的主键
			if (tableKeys.length > 0) {
				maxFieldValue = lastRecord.get(tableKeys[0]);
				_m.put(tableKeys[0], "");//防止记录中不含tableKeys[0]主键字段
			} else {
				maxFieldValue = lastRecord.get(tableFields[0]);
				_m.put(tableFields[0], "");//防止记录中不含tableKeys[0]主键字段
			}
		}
		
		Object[] recordFields = (_m.keySet()).toArray(); //获取记录里的字段名的集合
		for (int i = 0; i < recordFields.length; i++) {
			if (!StringTool.isInFields(tableFields, recordFields[i].toString())) {
				_m.remove(recordFields[i].toString());//移除无效字段， 查看记录中的字段在表中是否存在，如果不存在，则移除到
			}
		}

		Object[] fields = (_m.keySet()).toArray(); //过滤后的有效字段
		String[] values = new String[fields.length]; //保存通配符'?'
		for (int i = 0; i < fields.length; i++) {
			values[i] = "?";
		}

		String sql_field = tool.arryToString(fields, ",");
		String sql_values = tool.arryToString(values, ",");
		preparedStatementInsert = "INSERT INTO " + tableName + " (" + sql_field + ") VALUES (" + sql_values + ")";//合成preparedStatement语句
		PreparedStatement pstmt = null;

		try {
			pstmt = con.prepareStatement(preparedStatementInsert,Statement.RETURN_GENERATED_KEYS);
			long firstStringKey = (new java.util.Date()).getTime();
			if (autoInsertKey) {//自动插入主键
				if (tableKeys.length > 0) {
					Field keyF = dbCenterImp.getField(tableName, tableKeys[0]);
					if ("java.lang.Long".equals(keyF.getTypeClassName())) {//时间+4位整数
						mapRecord.put(tableKeys[0], dbCenterImp.getTable(tableName).makeLongKeyValue());
					} else if ("java.lang.String".equals(keyF.getTypeClassName())) {
						if (Integer.parseInt(keyF.getSize()) > 13) {
							mapRecord.put(tableKeys[0], "" + (firstStringKey + 1));//自动插入字符型主键值//if (className.equals("java.lang.String") && Integer.parseInt(f.getSize()) > 13) 
						} else {
							return num;
						}
					} 
					else {
						maxFieldValue = dbCenterImp.getTable(tableName).makeObjectKeyValue(keyF, maxFieldValue);
						mapRecord.put(tableKeys[0], maxFieldValue);//自动插入数值型主键值
					}
				} else {
					Field keyF = dbCenterImp.getField(tableName, tableFields[0]);
					if ("java.lang.Long".equals(keyF.getTypeClassName())) {//时间+4位整数
						mapRecord.put(tableFields[0], dbCenterImp.getTable(tableName).makeLongKeyValue());
					} else if ("java.lang.String".equals(keyF.getTypeClassName())) {
						if (Integer.parseInt(keyF.getSize()) > 13) {
							mapRecord.put(tableFields[0], "" + (firstStringKey + 1));//自动插入字符型主键值//if (className.equals("java.lang.String") && Integer.parseInt(f.getSize()) > 13) 
						} else {
							return num;
						}
					}
					else {
						maxFieldValue = dbCenterImp.getTable(tableName).makeObjectKeyValue(keyF, maxFieldValue);
						mapRecord.put(tableFields[0], maxFieldValue);//自动插入数值型主键值
					}
				}
			}//自动插入主键

			for (int i = 0; i < fields.length; i++) {//对对应的取值进行处理
				Field f = dbCenterImp.getField(tableName, fields[i].toString());
				String className = f.getTypeClassName();
				int index = f.getSqlType();
				Object v = mapRecord.get(fields[i].toString());//获取给定字段的值（用户传入）

				if (v == null) {
					pstmt.setNull(i + 1, index);//continue;
				} 
				else if (v != null) {
					String _c = ((Class<? extends Object>) v.getClass()).getName(); 
					//增加对表单数据的支持,在表单中获取的数据均为String型,固应对其进行转换.
					if ((_c.equals("java.lang.String")) && ("".equals(((String) v).trim()))) {
						pstmt.setNull(i + 1, index);//continue;
					}
					else {
						if (className.equals("java.lang.String")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setString(i + 1, (String) v);
							}
							continue;
						}
						if (className.equals("java.lang.Integer")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setInt(i + 1, Integer.parseInt((String) v));
							} else {
								if (_c.equals("java.lang.Integer")) {
									pstmt.setInt(i + 1, ((Integer) v).intValue());
								} else {
									Integer n = new Integer(v.toString());
									pstmt.setInt(i + 1, n);
								}
							}
							continue;
						}

						if (className.equals("java.lang.Long")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setLong(i + 1, Long.parseLong((String) v));
							} else {
								if (_c.equals("java.lang.Long")) {
									pstmt.setLong(i + 1, ((Long) v).longValue());
								} else {
									Long l = new Long(v.toString());
									pstmt.setLong(i + 1, l);
								}
							}
							continue;
						}

						if (className.equals("java.lang.Short")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setShort(i + 1, Short.parseShort((String) v));
							} else {
								pstmt.setShort(i + 1, ((Short) v).shortValue());
							}
							continue;
						}

						if (className.equals("java.lang.Float")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setFloat(i + 1, Float.parseFloat((String) v));
							} else {
								pstmt.setFloat(i + 1, ((Float) v).floatValue());
							}
							continue;
						}

						if (className.equals("java.lang.Double")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setDouble(i + 1, Double.parseDouble((String) v));
							} else {
								pstmt.setDouble(i + 1, ((Double) v).doubleValue());
							}
							continue;
						}

						if (className.equals("java.lang.Boolean")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setBoolean(i + 1, (Boolean.valueOf((String) v)).booleanValue());
							} else {
								pstmt.setBoolean(i + 1, ((Boolean) v).booleanValue());
							}
							continue;
						}

						if (className.equals("java.sql.Timestamp")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								String _s = ((String) v).trim();
								if (StringTool.matches(RegexType.chinaDate, _s)) {//如：2012-01-24
									Time t = new Time(0l);
									_s = _s + " " + t.toString();
									pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf(_s));
								} else {
									pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf((String) v));
								}
							} else if (_c.equals("java.sql.Date")) {
								java.sql.Date _v = (java.sql.Date) v;
								pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
							} else if (_c.equals("java.util.Date")) {
								java.util.Date _v = (java.util.Date) v;
								pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
							} else if (_c.equals("java.sql.Time")) {
								java.sql.Time _v = (java.sql.Time) v;
								pstmt.setTimestamp(i + 1, new Timestamp(_v.getTime()));
							} else {
								pstmt.setTimestamp(i + 1, new Timestamp(((java.util.Date) v).getTime()));//能支持更多的应用
								//pstmt.setTimestamp(i + 1,  (java.sql.Timestamp) v);//使用jsf日期转换后获得的结果可能不完整，这时会出现转换异常
							}
							continue;
						}

						if (className.equals("java.sql.Date")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
							} else if (_c.equals("java.util.Date")) {
								java.util.Date _v = (java.util.Date) v;
								pstmt.setDate(i + 1, new java.sql.Date(_v.getTime()));
							} else {
								pstmt.setDate(i + 1, (java.sql.Date) v);
							}
							continue;
						}

						if (className.equals("java.util.Date")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
							} else if (_c.equals("java.util.Date")) {
								java.util.Date _v = (java.util.Date) v;
								pstmt.setDate(i + 1, new java.sql.Date(_v.getTime()));
							} else {
								pstmt.setDate(i + 1, (java.sql.Date) v);
							}
							continue;
						}

						if (className.equals("java.sql.Time")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setTime(i + 1, java.sql.Time.valueOf((String) v));
							} else if (_c.equals("java.util.Date")) {
								java.util.Date _v = (java.util.Date) v;
								DateFormat df = new SimpleDateFormat("HH:mm:ss");
								String _dt = df.format(_v);
								pstmt.setTime(i + 1, java.sql.Time.valueOf(_dt));
							} else {
								pstmt.setTime(i + 1, (java.sql.Time) v);
							}
							continue;
						}

						if (className.equals("[B") || className.equals("byte[]")) {
							//SQL Server 的image\timestamp\binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setBytes(i + 1, ((String) v).getBytes());
							} else {
								pstmt.setBytes(i + 1, (byte[]) v);
							}
							continue;
						}
						if (className.equals("java.sql.Blob")) {
							//SQL Server 的image\timestamp\binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setBytes(i + 1, ((String) v).getBytes());
							} else {
								pstmt.setBlob(i + 1, (java.sql.Blob) v);
							}
							continue;
						}

						if (className.equals("java.lang.Object")) {
							//SQL Server 的image\timestamp\binary类型是byte[],MySQL 的blob系列是java.lang.Object,Sybase的image是[B
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setBytes(i + 1, ((String) v).getBytes());
							} else {
								pstmt.setObject(i + 1, v);
							}
							continue;
						}

						if (className.equals("java.lang.Byte")) {
							//MySQL的tinyint是java.lang.Byte
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setByte(i + 1, java.lang.Byte.parseByte((String) v));
							} else {
								pstmt.setByte(i + 1, java.lang.Byte.parseByte(v.toString()));
							}
							continue;
						}

						if (className.equals("java.math.BigDecimal")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setBigDecimal(i + 1, new BigDecimal((String) v));
							} else if ((_c.equals("java.lang.Double"))) {
								pstmt.setBigDecimal(i + 1, new BigDecimal((Double) v));
							} else if ((_c.equals("java.lang.Float"))) {
								double _v = (Double) v;
								pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
							} else if ((_c.equals("java.lang.Integer"))) {
								int _v = (Integer) v;
								pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
							} else if ((_c.equals("java.lang.Long"))) {
								long _v = (Long) v;
								pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
							} else if ((_c.equals("java.math.BigInteger"))) {
								java.math.BigInteger _v = (java.math.BigInteger) v;
								pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
							} else if ((_c.equals("[C"))) {
								char[] _v = (char[]) v;
								pstmt.setBigDecimal(i + 1, new BigDecimal(_v));
							} else {
								pstmt.setBigDecimal(i + 1, (BigDecimal) v);
							}
							continue;
						}

						//以下部分将根据具体的数据库需要而定,有待验证
						if (className.equals("java.sql.Clob")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setString(i + 1, (String) v);//给clob类型的字段赋予字符串型
							} else {
								pstmt.setClob(i + 1, (java.sql.Clob) v);
							}
							continue;
						}

						//以下部分将根据具体的数据库需要而定,有待验证
						if (className.equals("java.sql.Array")) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								//
							} else {
								pstmt.setArray(i + 1, (java.sql.Array) v);
							}
							continue;
						}

						//特殊类型，非常用，置后
						if (className.equals("com.sybase.jdbc2.tds.SybTimestamp") || className.toLowerCase().indexOf("timestamp") > 0) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf((String) v));
							} else {
								pstmt.setTimestamp(i + 1, (java.sql.Timestamp) v);
							}
							continue;
						}

						//概略匹配
						if (className.toLowerCase().indexOf("date") > 0) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setDate(i + 1, java.sql.Date.valueOf((String) v));
							} else {
								pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) v).getTime()));
							}
							continue;
						}

						if (className.toLowerCase().indexOf("time") > 0) {
							if ((_c.equals("java.lang.String")) && (!"".equals(((String) v).trim()))) {
								pstmt.setTime(i + 1, java.sql.Time.valueOf((String) v));
							} else {
								pstmt.setTime(i + 1, (java.sql.Time) v);
							}
							continue;
						}

						if (_c.equals("java.io.FileInputStream")) {
							//调用如：FileInputStream in = new FileInputStream("D:\\test.jpg");的结果
							pstmt.setBinaryStream(i + 1, (FileInputStream) v, ((FileInputStream) v).available());
							continue;
						}//java.io.FileInputStream

						//其它特殊类型，非常用，置后
					}
				}
			}//对对应的取值进行处理
			//Console.showMessage("Insert:" + pstmt);
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			rs.next();
			num = rs.getInt(1);
		} catch (java.lang.ClassCastException ex) {
			throw new SQLException("java.lang.ClassCastException: " + ex.getMessage(), ex.getCause());
		} catch (NumberFormatException ex) {
			throw new SQLException("NumberFormatException: " + ex.getMessage(), ex.getCause());
		} catch (IOException ex) {
			throw new SQLException("IOException: " + ex.getMessage(), ex.getCause());
		} finally {
			pstmt.close();
		}
		return num;
	}

	/**
	 * 插入一组记录
	 *
	 * @param insertSql 插入语句数组
	 * @return 返回JDBC标准插入语句的返回值
	 * @throws SQLException
	 */
	@Override
	public int insert(String[] insertSql) throws SQLException {
		int num = this.update(insertSql);
		return num;
	}

	
	/**
	 * 插入一条记录
	 *
	 * @param tableName 是表名
	 * @param mapRecord
	 * 是准备插入到表中的一条记录的数据,其键名与字段名相同,顺序无关;但键名不能是字段名以外的其它名称，自动过滤无效字段数据
	 * @return 返回JDBC标准插入语句的返回值
	 * @throws SQLException
	 */
	@Override
	public int insert(String tableName, Map<String, Object> mapRecord) throws SQLException {
		int num = 0;
		List<Map<String, Object>> a = new ArrayList<Map<String, Object>>();
		a.add(mapRecord);
		num = this.insert(tableName, a);
		return num;
	}

	/**
	 * 插入一条记录
	 *
	 * @param tableName 是表名
	 * @param mapRecord 是准备插入到表中的一条记录的数据,其键名与字段名相同,顺序无关 ,自动过滤无效字段数据
	 * @param autoInsertKey 值为true时，自动插入主键
	 * @return 返回JDBC标准插入语句的返回值
	 * @throws SQLException
	 */
	@Override
	public int insert(String tableName, Map<String, Object> mapRecord, boolean autoInsertKey) throws SQLException {
		int num = 0;
		List<Map<String, Object>> a = new ArrayList<Map<String, Object>>();
		a.add(mapRecord);
		num = this._preparedStatementInsert(tableName, a, autoInsertKey);
		return num;
	}

	/**
	 * 插入多条记录
	 *
	 * @param tableName 是表名
	 * @param listRecord 是准备插入到表中的多条记录的数据,其键名与字段名相同,顺序无关 ,自动过滤无效字段数据
	 * @return 返回JDBC标准插入语句的返回值
	 * @throws SQLException
	 */
	@Override
	public int insert(String tableName,List<Map<String, Object>> listRecord) throws SQLException {
		int num = this._preparedStatementInsert(tableName, listRecord, false);
		return num;
	}

	/**
	 * 插入多条记录 ?
	 *
	 * @param tableName 是表名
	 * @param listRecord 是准备插入到表中的多条记录的数据,其键名与字段名相同,顺序无关 ,自动过滤无效字段数据
	 * @param autoInsertKey 值为true时，自动插入主键
	 * @return 返回JDBC标准插入语句的返回值
	 * @throws SQLException
	 */
	@Override
	public int insert(String tableName, List<Map<String, Object>> listRecord, boolean autoInsertKey) throws SQLException {
		int num = this._preparedStatementInsert(tableName, listRecord, true);
		return num;
	}

	/**
	 * 插入长整形主键值，只对第一个主键或第一个字段有效 使用单例模式和唯一性验证<br/>
	 *
	 * @param tableName 是表名,
	 * @return 返回经过唯一性验证值，
	 * 当前系统时间*10000+四位自然数，如2012-11-01某时某刻系统时间：1351749144390，修改后：13517491443897256；如果将来希望根据主键获取时间，则用主键值/10000即可，误差到1/100毫秒。
	 * @throws SQLException
	 *
	 */
	@Override
	public Long insertKey(String tableName) throws SQLException 
	{
		if( StringTool.isNullEmpty(tableName))
			return 0L;
		
		Long ID = null;
		long keyValue = 0;
		Map<String, Object> m = new HashMap<String, Object>();
		int b = 0;
		String keyField = dbCenterImp.getTable(tableName).getFields()[0];
		if (dbCenterImp.getTable(tableName).getKeys().length > 0) {
			keyField = dbCenterImp.getTable(tableName).getKeys()[0];
		}
		synchronized (this) {
			keyValue = dbCenterImp.getTable(tableName).makeLongKeyValue();
			m.put(keyField, keyValue);
			b = insert(tableName, m);
		}
		if (b > 0) {
			ID = keyValue;
		}
		return ID;
	}

	/**
	 * 插入指定键值<br/> 用字符串的形式给数字型和字符型的主键插入键值，但键值的格式必须与字段类型相符<br/> 使用单例模式和唯一性验证<br/>
	 *
	 * @return 插入指定的键值，如果成功则返回该值，如果不成功，则返回null。
	 * @param tableName 是表名,
	 * @param keyValue 键值
	 * @return 若keyValue有效，则返回keyValue，否则返回null。
	 * @throws SQLException
	 *
	 */
	@Override
	public String insertKey(String tableName, String keyValue) throws SQLException 
	{
		if( StringTool.isNullEmpty(tableName))
			return null;
		
		String keyField = dbCenterImp.getTable(tableName).getFields()[0];
		if (dbCenterImp.getTable(tableName).getKeys().length > 0) {
			keyField = dbCenterImp.getTable(tableName).getKeys()[0];
		}
		String ID = null;
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		int b = 0;
		synchronized (this) {
			Map<String, Object> mm = this.queryOne("select " + keyField + " from " + tableName + " where " + keyField + " like '" + keyValue + "'");
			if (mm.isEmpty()) {
				//String v = dbCenterImp.getTable(tableName).makeStringKeyValue(keyField, keyValue);//检查合法性、唯一性
				m.put(keyField, keyValue);
				b = insert(tableName, m);
			} else {
				throw new SQLException("the key field value already being existence. ");
			}
		}
		if (b > 0) {
			ID = keyValue;
		}
		return ID;
	}

	/**
	 * 通用键值生成器，只对第一个主键或第一个字段有效<br/>
	 * 支持的字段类型有：Long\Integer\Short\Float\Double\String(长度大于等于13)<br/>
	 * 整数型自动加1；浮点型自动加1并取整，字符型插入Date的long值，如：1348315761671，字段宽度应当大于等于13<br/>
	 * 使用单例模式和唯一性验证<br/>
	 *
	 * @param tableName 是表名,
	 * @return 返回经过唯一性验证，自动增量值
	 * @throws SQLException
	 */
	@Override
	public Object insertAutoKey(String tableName) throws SQLException 
	{
		if( StringTool.isNullEmpty(tableName))
			return null;
			
		String keyField = dbCenterImp.getTable(tableName).getFields()[0];
		if (dbCenterImp.getTable(tableName).getKeys().length > 0) {
			keyField = dbCenterImp.getTable(tableName).getKeys()[0];
		}
		Field f = dbCenterImp.getField(tableName, keyField);
		String className = f.getTypeClassName();

		Object ID = null;
		Object keyValue = null;
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		int b = 0;
		if (className.equals("java.lang.Long") || className.equals("java.lang.Integer") || className.equals("java.lang.Short") || className.equals("java.lang.Float") || className.equals("java.lang.Double") || className.equals("java.lang.String")) {
			synchronized (this) {
				if ((className.equals("java.lang.String") && Integer.parseInt(f.getSize()) > 13)) {
					keyValue = "" + (new java.util.Date()).getTime();
				} else {
					String query = "select max(" + keyField + ") as exp1 from " + tableName;//select max(id) as exp1 from test_table01_on_pvo
					Map<String, Object> map = this.queryOne(query);//{exp1=20121105}
					Object _ID = map.get("exp1");
					keyValue = dbCenterImp.getTable(tableName).makeObjectKeyValue(f, _ID);//makeObjectKeyValue
				}
				m.put(keyField, keyValue);
				b = insert(tableName, m);
				if (b > 0) {
					ID = keyValue;
				}
			}
		} else {
			throw new SQLException(" not support this type of " + className + " to make keyvalue");
		}

		return ID;
	}

	///////////////////////////////////////////////////////////////////////////////
	@Override
	public int updateInsert(String tableName, Map<String, Object> mapRecord) throws SQLException 
	{
		if( StringTool.isNullEmpty(tableName)  || StringTool.isNull(mapRecord))
			return 0;
	
		return save(tableName,mapRecord);
		
		/*对多个主键有问题？？？、
		List<Map<String, Object>> a = new ArrayList<Map<String, Object>>();
		a.add(mapRecord);
		return this._preparedStatementUpdateInsert(tableName, a);*/
	}
	/**
	 * 保存一条记录.
	 * 如果该记录存在，则更新之，如果该记录不存在，则插入。如果第一个主键的值为null||""，则自动插入新的主键值，这个方法不适合对含多主键的表进行插入操作，但不影响对多主键的表进行更新
	 *
	 * @param tableName 是表名
	 * @param mapRecord 是准备插入到表中的一条记录的数据,其键名与字段名相同,顺序无关;但键名不能是字段名以外的其它名称;
	 * @return 返回第一个主键值。【注：原返回int型update返回值，更改为第一个主键值，将更方便应用】
	 * @throws SQLException
	 */
	@Override
	public Object saveOne(String tableName, Map<String, Object> mapRecord) throws SQLException {
		if( StringTool.isNullEmpty(tableName)  || StringTool.isNull(mapRecord))
			return null;
		
		Object firstKeyValue = null;
		int n = 0;
		String _w = "";
		String[] keys = dbCenterImp.getKeys(tableName);
		String[] fields = dbCenterImp.getFields(tableName);
		if (keys.length == 0) {
			n = this.insert(tableName, mapRecord);//无主键的表可以直接插入
			if (n > 0) {
				firstKeyValue = mapRecord.get(fields[0]);
			}//没有主键，则返回第一个字段值
		} else {
			firstKeyValue = mapRecord.get(keys[0].toString());
			if (firstKeyValue != null & !"".equals(firstKeyValue)) {
				Object[] recordFields = mapRecord.keySet().toArray();
				Map<String, Object> _key_m = new LinkedHashMap<String, Object>();
				for (int i = 0; i < recordFields.length; i++) {
					if (StringTool.isInFields(keys, recordFields[i].toString())) {
						_key_m.put(recordFields[i].toString(), mapRecord.get(recordFields[i].toString()));//提取记录中的主键字段
					}
				}
				if (!_key_m.isEmpty()) {
					Object[] _k = _key_m.keySet().toArray();
					if (_k.length != keys.length) {
						return null;//如果有多个主键，而记录中缺少多个主键，则不保存
					} else {//本人注释掉/*" where " +*/， 是否正确还没有验证
						Field f = dbCenterImp.getField(tableName, _k[0].toString());
						if (f.getTypeClassName().equals("java.lang.String")) {
							_w = /*" where " +*/  _k[0] + " like '" + mapRecord.get(_k[0].toString()) + "'";
						} else {
							_w = /*" where " +*/  _k[0] + " = " + mapRecord.get(_k[0].toString());
						}
						if (_k.length > 1) {
							for (int i = 1; i < _k.length; i++) {
								f = dbCenterImp.getField(tableName, _k[i].toString());
								if (f.getTypeClassName().equals("java.lang.String")) {
									_w = _w + " and " + _k[i] + " like '" + mapRecord.get(_k[i].toString()) + "'";
								} else {
									_w = _w + " and " + _k[i] + " = " + mapRecord.get(_k[i].toString());
								}
							}
						}
						Map<String, Object> rm = this.queryOne("select " + _k[0] + " from " + tableName + _w);
						if (rm.isEmpty()) {
							n = this.insert(tableName, mapRecord);//原原本本地插入
						} else {
							n = this.update(tableName, mapRecord, _w);//原原本本地更新
						}
					}
				}
			} else {// if (firstKeyValue == null || "".equals(firstKeyValue))
				Field f = dbCenterImp.getField(tableName, keys[0]);
				String className = f.getTypeClassName();
				if (className.equals("java.lang.Long")) {
					firstKeyValue = insertKey(tableName);
					mapRecord.put(keys[0], firstKeyValue);
					n = update(tableName, mapRecord);//表有主键，但记录中不含主键记录||主键记录值是null||主键值是""，则插入，并自动插入主键

				}
				if (className.equals("java.lang.Integer") || className.equals("java.lang.Short") || className.equals("java.lang.Float") || className.equals("java.lang.Double") || className.equals("java.lang.String")) {
					firstKeyValue = insertAutoKey(tableName);
					mapRecord.put(keys[0], firstKeyValue);
					n = update(tableName, mapRecord);//表有主键，但记录中不含主键记录||主键记录值是null||主键值是""，则插入，并自动插入主键
				}
			}
		}
		return firstKeyValue;
	}

	/**
	 * 保存一条记录.
	 * 如果该记录存在，则更新之，如果该记录不存在，则插入。如果第一个主键的值为null||""，则自动插入新的主键值，这个方法不适合对含多主键的表进行插入操作，但不影响对多主键的表进行更新
	 *
	 * @param tableName 是表名
	 * @param mapRecord 是准备插入到表中的一条记录的数据,其键名与字段名相同,顺序无关;但键名不能是字段名以外的其它名称;
	 * @return 返回标准update方法返回的状态值
	 * @throws SQLException
	 */
	@Override
	public int save(String tableName, Map<String, Object> mapRecord) throws SQLException 
	{
		int num = 0;
		if( StringTool.isNullEmpty(tableName)  || StringTool.isNull(mapRecord))
			return num;
			
		String _w = "";
		String[] keys = dbCenterImp.getKeys(tableName);
		
		if (keys.length == 0) {
			return this.insert(tableName, mapRecord);//无主键的表可以直接插入
		} 
		else {
			Object kv = mapRecord.get(keys[0].toString());
			if (kv == null || "".equals(kv)) {//主键自动增一
				num = insert(tableName, mapRecord, true);//表有主键，但记录中不含主键记录||主键记录值是null||主键值是""，则插入，并自动插入主键
			}
			else {
				Object[] recordFields = mapRecord.keySet().toArray();
				Map<String, Object> _key_m = new LinkedHashMap<String, Object>();
				for (int i = 0; i < recordFields.length; i++) {
					if (StringTool.isInFields(keys, recordFields[i].toString())) {
						_key_m.put(recordFields[i].toString(), mapRecord.get(recordFields[i].toString()));//提取记录中的主键字段
					}
				}
				if (!_key_m.isEmpty()) {
					Object[] _k = _key_m.keySet().toArray();
					if (_k.length != keys.length) {
						return num;
					} else {
						/*Field f = dbCenterImp.getField(tableName, _k[0].toString());
						if (f.getTypeClassName().equals("java.lang.String")) {
							_w = " WHERE " + _k[0] + " like '" + mapRecord.get(_k[0].toString()) + "'";
						} 
						else {
							_w = " WHERE " +  _k[0] + " = " + mapRecord.get(_k[0].toString());
						}*/
						_w = _k[0] + " = '" + mapRecord.get(_k[0].toString()) + "'";//替换上边的语句 XBM
						if (_k.length > 1) {
							for (int i = 1; i < _k.length; i++) {
								/*f = dbCenterImp.getField(tableName, _k[i].toString());
								if (f.getTypeClassName().equals("java.lang.String")) {
									_w = _w + " and " + _k[i] + " like '" + mapRecord.get(_k[i].toString()) + "'";
								} else {
									_w = _w + " and " + _k[i] + " = " + mapRecord.get(_k[i].toString());
								}*/
								_w = _w + " AND " + _k[i] + " ='" + mapRecord.get(_k[i].toString()) + "'";//替换上边的语句 XBM
							}
						}
						//Map<String, Object> rm = this.queryOne("select " + _k[0] + " from " + tableName + _w);
						//替换上边的语句 XBM
						//System.out.println("8:"+ ("select " + _k[0] + " from " + tableName + " WHERE " + _w));
						Map<String, Object> rm = this.queryOne("select " + _k[0] + " from " + tableName + " WHERE " + _w);
						if (rm.isEmpty()) {
							Console.showMessage("insert");
							num = this.insert(tableName, mapRecord);//原原本本地插入
						} else {
							Console.showMessage("update");
							num = this.update(tableName, mapRecord, _w);//原原本本地更新
						}
					}
				}
			}
		}
		return num;
	}
	//index/////////////////////////////////////////////////////////////////////////////////
	/**
	 * 索引节点数组的长度
	 *
	 * @param tableName 表名
	 * @return 返回索引节点数组的长度
	 */
	public int getIndexNodesLength(String tableName) {
		return dbCenterImp.getTable(tableName).getIndexNodes().length;
	}

	/**
	 * @return 返回索引节点的步长
	 */
	public int getIndexNodesStepLength() {
		return indexStepLength;
	}


	/**
	 * 索引查询一条记录
	 *
	 * @param tableName 表名
	 * @param fields 待查询的字段
	 * @param row_in_table 从该行开始，取值[1,记录总长度]，出界位置返回0长度List&lt;Map&gt;结果
	 * @return 返回List&lt;Map&gt;类型的结果
	 * @throws SQLException
	 */
	public Map<String, Object> indexByRow(String tableName, String[] fields, int row_in_table) throws SQLException 
	{
		if( StringTool.isNullEmpty(tableName))
			return null;
		
		String key = dbCenterImp.getFields(tableName)[0];
		if (dbCenterImp.getKeys(tableName).length > 0) {
			key = dbCenterImp.getKeys(tableName)[0];
		}
		IndexNode nodes = _indexNodeOne(tableName, row_in_table);
		Field f = dbCenterImp.getField(tableName, key);
		String sql = "select " + tool.arryToString(fields, ",") + " from  " + tableName + " where " + key + "=" + nodes.getFirstKeyValue();
		if (f.getTypeClassName().equals("java.lang.String")) {
			sql = "select " + tool.arryToString(fields, ",") + " from  " + tableName + " where " + key + " like '" + nodes.getFirstKeyValue() + "'";
		}
		Map<String, Object> m = this.queryOne(sql);
		return m;
	}

	/**
	 * 索引查询多条记录
	 *
	 * @param tableName 表名
	 * @param fields 待查询的字段
	 * @param row_in_table 从该行开始，取值[1,记录总长度]，出界位置返回0长度List&lt;Map&gt;结果
	 * @param length 查询长度，row_in_table后记录数少于length，则返回其后的全部记录
	 * @param asc 排列顺序，true顺序，false逆序
	 * @return 返回List&lt;Map&gt;类型的结果
	 * @throws SQLException
	 */
	public List<Map<String, Object>> indexByRow(String tableName, String[] fields, int row_in_table, int length, boolean asc) throws SQLException 
	{
		if( StringTool.isNullEmpty(tableName))
			return null;
	
		
		String key = dbCenterImp.getFields(tableName)[0];
		if (dbCenterImp.getKeys(tableName).length > 0) {
			key = dbCenterImp.getKeys(tableName)[0];
		}
		//xbm
		//List<Map> list = new ArrayList();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		IndexNode[] nodes = _indexNodeTwo(tableName, row_in_table, length);
		Field f = dbCenterImp.getField(tableName, key);
		String sort = "asc";
		if (!asc) {
			sort = "desc";
		}
		String sql = "select " + tool.arryToString(fields, ",") + " from  " + tableName + " where " + key + ">=" + nodes[0].getFirstKeyValue() + " and  " + key + "<=" + nodes[1].getFirstKeyValue() + " order by " + key + " " + sort;
		if (f.getTypeClassName().equals("java.lang.String")) {
			sql = "select " + tool.arryToString(fields, ",") + " from  " + tableName + " where " + key + ">='" + nodes[0].getFirstKeyValue() + "' and  " + key + "<='" + nodes[1].getFirstKeyValue() + "' order by " + key + " " + sort;
		}
		list = this.query(sql);
		return list;
	}

	/**
	 * 索引查询多条记录<br/> 根据已经建立的索引节点数组查询
	 * 返回的记录是当前节点的行数减前一节点的行数，即：默认的步长内的记录数，通常是1000。<br/>
	 * 默认步长可以调用pvo.setIndexNodesStepLength(indexStepLength)方法重新设置<br/>
	 * 可用于大数据分组分页查询
	 *
	 * @param tableName 表名
	 * @param fields 待查询的字段
	 * @param position_of_indexNodes
	 * 取第position_of_indexNodes位已建立的节点，取值[0,nodes.length - 1]
	 * @param asc 排列顺序，true顺序，false逆序
	 * @param rebuildIndexNodes 在查询前是否重新创建索引数组
	 * @return 返回List&lt;Map&gt;类型的结果
	 * @throws SQLException
	 */
	public List<Map<String, Object>> indexByIndexNodes(String tableName, String[] fields, int position_of_indexNodes, boolean asc, boolean rebuildIndexNodes) throws SQLException 
	{
		if( StringTool.isNullEmpty(tableName))
			return null;
		
		String key = dbCenterImp.getFields(tableName)[0];
		if (dbCenterImp.getKeys(tableName).length > 0) {
			key = dbCenterImp.getKeys(tableName)[0];
		}
		int step_old = dbCenterImp.getTable(tableName).getIndexStepLength();
		if (this.indexStepLength != step_old) {
			rebuildIndexNodes = true;
		}//步长发生变化时，必须重建
		IndexNode[] nodes = _indexNodes(tableName, rebuildIndexNodes);
		//xbm
		//List<Map> list = new ArrayList();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (position_of_indexNodes < 0 || position_of_indexNodes >= (nodes.length - 1)) {
			return list;//超出范围
		}
		Field f = dbCenterImp.getField(tableName, key);
		String sort = "asc";
		if (!asc) {
			sort = "desc";
		}
		String sql = "select " + tool.arryToString(fields, ",") + " from  " + tableName + " where " + key + "<=" + nodes[position_of_indexNodes].getFirstKeyValue() + " order by " + key + " " + sort;
		if (f.getTypeClassName().equals("java.lang.String")) {
			sql = "select " + tool.arryToString(fields, ",") + " from  " + tableName + " where " + key + "<='" + nodes[position_of_indexNodes].getFirstKeyValue() + "' order by " + key + " " + sort;
		}
		if (position_of_indexNodes > 0 && position_of_indexNodes <= (nodes.length - 1)) {
			sql = "select " + tool.arryToString(fields, ",") + " from  " + tableName + " where " + key + ">" + nodes[position_of_indexNodes - 1].getFirstKeyValue() + " and " + key + "<=" + nodes[position_of_indexNodes].getFirstKeyValue() + " order by " + key + " " + sort;
			if (f.getTypeClassName().equals("java.lang.String")) {
				sql = "select " + tool.arryToString(fields, ",") + " from  " + tableName + " where " + key + ">'" + nodes[position_of_indexNodes - 1].getFirstKeyValue() + "'and " + key + "<='" + nodes[position_of_indexNodes].getFirstKeyValue() + "' order by " + key + " " + sort;
			}
		}
		dbCenterImp.getTable(tableName).setPosition_of_indexNodes(position_of_indexNodes);//保存本次查询的位置信息
		list = this.query(sql);
		return list;
	}

	/**
	 * 设置索引节点的步长
	 *
	 * @param indexStepLength 新的索引节点的步长
	 */
	public void setIndexNodesStepLength(int indexStepLength) {
		this.indexStepLength = indexStepLength;
	}

	//判断表存在与否,存在返回true
	@Override
	public boolean checkTableIfExist(String tableName) throws SQLException
	{
		boolean temp = false;
		try {
			DatabaseMetaData dbm = con.getMetaData();
			if (dbm != null)
				rs = dbm.getTables(null, null, tableName, null);
			if (rs.next())
				temp = true;//Table exists
		}catch (Exception e) {
			temp = false;
		}finally{
			this.closeRs();
		}
		return temp;
	}

	/**
	 * 执行Statement的execute方法 负责执行创建、更新表的结构、...
	 *
	 * @param sqlUpdate 是一条标准的SQL更新语句.
	 * @return 返回标准更新语句的返回值
	 * @throws SQLException
	 * 
	 *  executeUpdate:
                      用于执行 INSERT、UPDATE 或 DELETE 语句以及 SQL DDL（数据定义语言）语句，
                      例如 CREATE TABLE 和 DROP TABLE。INSERT、UPDATE 或 DELETE 语句的效果是修改表中零行或多行中的一列或多列。
        executeUpdate 的返回值是一个整数（int），指示受影响的行数（即更新计数）。
                     对于 CREATE TABLE 或 DROP TABLE 等不操作行的语句，executeUpdate 的返回值总为零。
                     
       execute： 
                  可用于执行任何SQL语句，返回一个boolean值，表明执行该SQL语句是否返回了ResultSet。
                  如果执行后第一个结果是ResultSet，则返回true，否则返回false。但它执行SQL语句时比较麻烦，
                  通常没有必要使用execute方法来执行SQL语句，而是使用executeQuery或executeUpdate更适合，
                   但如果在不清楚SQL语句的类型时则只能使用execute方法来执行该SQL语句。
                   
      executeQuery
                 用于产生单个结果集（ResultSet）的语句，例如 SELECT 语句。 被使用最多的执行 SQL 语句的方法。
	 */
	@Override
	public boolean execute(String sqlUpdate) throws SQLException {
		Console.showMessage(sqlUpdate);
		boolean f = false;
		try {
			stmt = con.createStatement();
			f = stmt.execute(sqlUpdate);
		} catch (SQLException ex) {
			throw ex;
		} finally {
			this.closeStmt();
		}
		return f;
	}

	/**
	 * 执行Statement的executeUpdate方法，如：更新记录
	 *
	 * @param updateSql 是一组标准的SQL更新语句，可以是插入、删除、更新.
	 * @return 返回标准更新语句的返回值
	 * @throws SQLException
	 */
	@Override
	public boolean execute(String[] updateSql) throws SQLException {
		boolean f = false;
		try {
			stmt = con.createStatement();
			for (int i = 0; i < updateSql.length; i++) {
				Console.showMessage(updateSql[i]);
				f = stmt.execute(updateSql[i]);
			}
		} catch (SQLException ex) {
			throw ex;
		} finally {
			this.closeStmt();
		}
		return f;
	}

	/**
	 * 调用、执行一个sql文件
	 *
	 * @param sqlFileName 一个sql文件.
	 * @return 是否执行成功
	 * @throws SQLException
	 */
	@Override
	public boolean executeSqlFile(String sqlFileName) throws SQLException {
		boolean f = false;
		try {
			String sql = new String(tool.getBytesFromFile(new File(sqlFileName)));
			stmt = con.createStatement();
			f = stmt.execute(sql);
		} catch (Exception ex) {
			stmt.cancel();
			throw new SQLException("IOException: " + ex.getMessage(), ex.getCause());
		}finally {
			this.closeStmt();
		}
		return f;
	}
	
	@Override
	public List<Map<String, Object>> queryTableOfDB(String DBName,String tableName) throws SQLException 
	{
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String sqlquery = "SELECT * FROM information_schema.TABLES " + 
			              "WHERE TABLE_SCHEMA ='" + DBName + "' AND TABLE_NAME ='" + tableName + "'";
		result = query(sqlquery);
		
		return result;
	}
	
	//DBSTRUCT//////////////////////////////////////////////////////////////////////////////////
		/**
		 * 查询数据库结构信息
		 */
		@Override
		public String queryDbInfo() throws SQLException {
			String s = "Catalog : \t\t\t" + dbCenterImp.getCatalog();
			s = s + "\nConnection driver name :\t" + dbCenterImp.getDriverName();
			String[] t = dbCenterImp.getTableNames();
			for (int i = 0; i < t.length; i++) {
				s = s + "\n\n\t TABLE[" + i + "]: " + t[i] + "----------------------------------";
				String[] fs = dbCenterImp.getFields(t[i]);
				for (int j = 0; j < fs.length; j++) {
					Field f = dbCenterImp.getField(t[i], fs[j]);
					s = s + "\n\n\t\t FieldName :\t" + f.getName()
							+ ";\n\t\t IsPrimarykey :\t" + f.isPrimarykey()
							+ ";\n\t\t TypeName :\t" + f.getTypeName()
							+ ";\n\t\t DataType :\t" + f.getSqlType()
							+ ";\n\t\t BufferLength :\t" + f.getBufferLength()
							+ ";\n\t\t Position :\t" + f.getPosition()
							+ ";\n\t\t ThisSize :\t" + f.getSize()
							+ ";\n\t\t Decimal :\t" + f.getDecimal()
							+ ";\n\t\t DefaultValue :\t" + f.getDefaultValue()
							+ ";\n\t\t Remark :\t" + f.getRemark()
							+ ";\n\t\t NullAble :\t" + f.isNullable()
							//+ ";\n\t\t Regex :\t" + f.getRegex()//暂未实现
							+ ";\n\t\t className :\t" + f.getTypeClassName();
				}
			}
			return s;
		}

		/**
		 * 查询指定表的结构信息
		 *
		 * @param tableName
		 * @return 返回表及其所有字段信息
		 */
		@Override
		public String queryTableInfo(String tableName) throws SQLException {
			String s = "TABLE:\t " + tableName + "\n------------------------------------------------------";
			String[] fs = dbCenterImp.getFields(tableName);
			for (int j = 0; j < fs.length; j++) {
				Field f = dbCenterImp.getField(tableName, fs[j]);
				s = s + "\n\n\t\t FieldName :\t" + f.getName()
						//+ ";\n\t\t IsPrimarykey :\t" + f.isPrimarykey()
						+ ";\n\t\t TypeName :\t" + f.getTypeName()
						+ ";\n\t\t DataType :\t" + f.getSqlType()
						+ ";\n\t\t BufferLength :\t" + f.getBufferLength()
						+ ";\n\t\t Position :\t" + f.getPosition()
						+ ";\n\t\t ThisSize :\t" + f.getSize()
						+ ";\n\t\t Decimal :\t" + f.getDecimal()
						+ ";\n\t\t DefaultValue :\t" + f.getDefaultValue()
						+ ";\n\t\t Remark :\t" + f.getRemark()
						+ ";\n\t\t NullAble :\t" + f.isNullable()
						//+ ";\n\t\t Regex :\t" + f.getRegex()//暂未实现
						+ ";\n\t\t className :\t" + f.getTypeClassName();
			}

			return s;
		}

		@Override
		public List<Map<String, Object>> queryTableInfoMap(String tableName) throws SQLException 
		{
			//table中的keys = new String[0];//主键名数组
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			String[] fs = dbCenterImp.getFields(tableName);
			if(fs !=  null)
			for (int j = 0; j < fs.length; j++) {
				Map<String, Object> map = new HashMap<String, Object>();
				Field f = dbCenterImp.getField(tableName, fs[j]);
				map.put("column", f.getName());//列名
				map.put("datatype", f.getTypeName());//数据类型
				map.put("length", f.getSize());//字段长度 
				map.put("isnullable", f.isNullable());//允许空
				map.put("key",f.isPrimarykey());//主键
				map.put("foreignkey",f.isForeignkey());//外键
				map.put("default", f.getDefaultValue());//默认值
				map.put("remark", f.getCommend());//说明,注释
			
				result.add(map);
			}
			return result;
		}
			
		
		
	
		@Override
		public String[] queryTableFields (String tableName)
		{
			return dbCenterImp.getFields(tableName);
		}
		//查看库中所有表的信息
		@Override
		public List<Map<String, Object>> queryDBInfo(String DBName) throws SQLException 
		{
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			//table_comment
			String sqlTable = "SELECT table_name,data_length, index_length,table_rows,engine,table_comment "+
					 		  "FROM information_schema.TABLES " + 
					          "WHERE TABLE_SCHEMA='" + DBName + "'";
			
			
			//SELECT table_name,data_length, index_length,table_rows,engine,table_comment FROM information_schema.TABLES WHERE TABLE_SCHEMA='redhat';
			//SELECT * FROM information_schema.TABLES WHERE TABLE_SCHEMA='redhat';
			//result = query(sqlTable);//所有表
			
			result = queryDBFieldAndKey(sqlTable);//所有表
			return result;
		}
		
		//查看库中指定表的信息
		@Override
		public List<Map<String, Object>> queryTableInfo(String DBName,String tableName) throws SQLException 
		{
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			//查看库中所有表的信息
			String sqlTable = "SELECT table_name,data_length, index_length,table_rows,engine,table_comment "+
					 		  "FROM information_schema.TABLES " + 
					          "WHERE TABLE_SCHEMA ='" + DBName + "' AND TABLE_NAME ='" + tableName + "'";

			//result = query(sqlTable);
			
			result = queryDBFieldAndKey(sqlTable);
			return result;
		}
		
		private List<Map<String, Object>> queryDBFieldAndKey(String sqlquery) throws SQLException 
		{   //每个元素对应一条记录的信息
			List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(sqlquery);
				rsmd = rs.getMetaData();
				
				int fieldCount = rsmd.getColumnCount();
				
				while (rs.next()) {//对每条记录进行处理
					Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
					String tableName = "";
					for (int i = 1; i <= fieldCount; i++) {
						String fieldClassName = rsmd.getColumnClassName(i);
						String fieldName = rsmd.getColumnName(i);
						tableName = rs.getString("TABLE_NAME");
						this._recordMappingToMap(fieldClassName, fieldName, rs, valueMap);
					}
					//主键部分，Key=='PRI'为主键
					String[] key = dbCenterImp.getKeys(tableName);
					if(key != null){
						int num = dbCenterImp.getKeys(tableName).length;
						String pk = "";
						for(int i=0; i< num; i++){
							pk = pk + dbCenterImp.getKeys(tableName)[i] + ",";
						}
						if(!pk.isEmpty())
							pk = pk.substring(0,pk.length()-1); 
						valueMap.put("pk", pk);
					}
					records.add(valueMap);
				}
			} finally {
				this.closeRs();
				this.closeStmt();
			}
			return records;
		}
	
		
		@Override
		public List<Map<String, Object>> queryCount(String table, String items, String where, String group) throws SQLException
		{
			if (StringTool.isNullEmpty(table))
				return null;

			String sqlquery = "SELECT *, count(*) as sum FROM "+ table;
			
			if(!StringTool.isNullEmpty(items))
			{
				if(items.contains("AS") || items.contains("COUNT"))//统计规则由用户定义
					sqlquery = "SELECT " + items + " FROM "+ table;
				else
					sqlquery = "SELECT " + items + ", count(*) as sum FROM "+ table;
			}
			
			if(!StringTool.isNullEmpty(where))
				sqlquery = sqlquery + " WHERE " + where;

			if(!StringTool.isNullEmpty(group))
				sqlquery = sqlquery + " Group BY " + group;

			Console.showMessage(sqlquery);

			return query(sqlquery); 
		}

		public String[] getKeys(String tableName) {
			// TODO Auto-generated method stub
			String[] keys = dbCenterImp.getKeys(tableName);
			return keys;
		}
}

/*
    SELECT  * FROM information_schema.columns WHERE TABLE_SCHEMA ='redhat' AND TABLE_NAME ='dbback'
	查询指定数据库中指定表的所有字段名column_name
	select column_name from information_schema.columns where table_schema='csdb' and table_name='users'
	 查询所有数据的大小
   select concat(round(sum(DATA_LENGTH/1024/1024),2),'MB') as data from TABLES
 */

//delete操作 成功 count >= 0
//对于insert,update等，用executeUpdate执行后如返回的值是0则表示操作失败，为正整数则成功
//关于删除操作：对于delete from tableName;executeUpdate的执行结果是返回成功删除的记录个数（>=0）
//对于truncate table tableName executeUpdate的执行结果总是0

/**
 * execute方法应该仅在语句能返回多个ResultSet对象、多个更新计数或ResultSet对象与更新计数的组合时使用。
 * 在使用方法 execute 执行该过程后，必须调用方法 getResultSet 获得第一个结果集，然后调用适当的 getXXX 方法获取其中的值。
 * 要获得第二个结果集，需要先调用getMoreResults 方法，然后再调用getResultSet方法。
 * 如果已知某个过程返回两个更新计数，则首先调用方法getUpdateCount，然后调用getMoreResults，并再次调用getUpdateCount
 * 
 * 对于不知道返回内容，则情况更为复杂。如果结果是ResultSet 对象，则方法execute返回 true；
 * 如果结果是 Java int，则返回false。如果返回int，则意味着结果是更新计数或执行的语句是DDL命令。
 * 
 * 在调用方法 execute之后要做的第一件事情是调用 getResultSet 或 getUpdateCount。
 * 调用方法 getResultSet 可以获得两个或多个ResultSet对象中第一个对象；或调用方法getUpdateCount可以获得两个或多个更新计数中第一个更新计数的内容。
 * 
 * 当SQL语句的结果不是结果集时，则方法getResultSet将返回null。这可能意味着结果是一个更新计数或没有其它结果。
 * 在这种情况下，判断null真正含义的唯一方法是调用方法getUpdateCount，它将返回一个整数。这个整数为调用语句所影响的行数；
 * 如果为-1则表示结果是结果集或没有结果。如果方法getResultSet 已返回null表示结果不是ResultSet对象），则返回值-1表示没有其它结果。也就是说，当下列条件为真时表示没有结果（或没有其它结果）：
 * ((stmt.getResultSet() == null) && (stmt.getUpdateCount() == -1))
 * 如果已经调用方法getResultSet 并处理了它返回的ResultSet对象，则有必要调用方法getMoreResults以确定是否有其它结果集或更新计数。
 * 如果getMoreResults返回true，则需要再次调用getResultSet来检索下一个结果集。如上所述，如果getResultSet返回null，则需要调
 * 用getUpdateCount来检查null 是表示结果为更新计数还是表示没有其它结果。当getMoreResults返回false时，它表示该SQL语句返回一
 * 个更新计数或没有其它结果。因此需要调用方法 getUpdateCount 来检查它是哪一种情况。在这种情况下，当下列条件为真时表示没有其它结果：
 * ((stmt.getMoreResults() == false) && (stmt.getUpdateCount() == -1))


	//将查询结果以Json字符串的形式返回实例代码
	public  String getUserInfo_Json2(String LoginName){
		List<Object> list = new ArrayList<Object>();
		String sql = "select * from Login where Login_id=?";
		list = thisDb.doQueryList("Login", "Login_", sql, new String[]{LoginName}, true);
		return JsonUtil.list2json(list);
	}

	//关闭数据库资源的顺序
	1)关闭Connection对象,系统部会自动关闭Statement,ResultSet对象
	2)Statement对象将由Java垃圾收集程序自动关闭,而作为一种好的编程风格,
	  应在不需要Statement对象时显式地关闭它们,这将立即释放DBMS资源,有助于避免潜在的内存问题.
	3)ResultSet维护指向其当前数据行的光标.每调用一次next方法,光标向下移动一行.最初它位于第一
	  行之前,因此第一次调用next将把光标置于第一行上,使它成为当前行.随着每次调用next导致光标向
	  下移动一行.按照从上至下的次序获取ResultSet行,在ResultSet对象或其父辈Statement对象关
	  闭之前,光标一直保持有效.
	  所以在打开数据库资源后,尽量手工关闭Connection对象和Statement,ResultSet对象,要养成一种良好的编程风格.

	  他们三者之间关闭没有任何关联,即先关闭谁没有任何先后顺序,可以先关闭他们中的任何一个,且关闭其中的任何一个对象都不
	   会关闭其他其他对象,但一般养成按关闭ResultSet,Statement,Connection的顺序关闭资源.

	 public ResultSet executeQuery() throws SQLException
         返回一个不为空的结果集

public int executeUpdate() throws SQLException
       返回：
   1、返回INSERT、UPDATE或者DELETE语句执行后的更新行数；
   2、返回0表示SQL语句没有执行成功。

public boolean execute() throws SQLException
     返回：true表示SQL语句执行的结果返回ResultSet对象；
   false表示SQL语句执行结果返回的是更新行数或者没有返回
 */
//数据库更新类操作方法。 用于执行INSERT、UPDATE、DELETE、REPLACE INTO、on duplicate等语句
	/** executeUpdate用于执行 INSERT、UPDATE 或 DELETE 语句以及 SQL DDL（数据定义语言）语句，例如 CREATE TABLE 和 DROP TABLE
	 *  对于 CREATE TABLE 或 DROP TABLE 等不操作行的语句，executeUpdate的返回值总为零。
	 *  对于INSERT、UPDATE 或 DELETE 语句.返回值是一个整数，指示受影响的行数（即更新计数）
	 *  如果库中没有任何记录,进行删除时,返回的值是0,此方法却返回false.
	 *  delFlag表示是否删除操作 true表示删除操作
	 */
	//数据库更新类操作方法。 用于执行INSERT、UPDATE、DELETE、REPLACE INTO、on duplicate等语句
