package org.blkj.sql.core;

import java.sql.*;
import java.util.*;

import org.blkj.sql.core.connection.DbConnection;

import blkjweb.utils.Console;


public class TableInfoBean {

	private Connection connection;
	private Statement statement = null;
	private ResultSet resultSet = null;
	BaseDMLImp pvo = null;
	//产生一个数据库连接句柄
	private boolean initialDBCon() {
		connection = null;
        pvo = new BaseDMLImp(DbConnection.getDefaultCon());
		connection = pvo.getCon();
		
		if (connection == null)	{
			return false;
		}	
		return true;
	}
	private void closeResource(boolean rsFlag, boolean stmtFlag)
	{ /**次序不能乱*/
		//1,关闭resultSet
		if (rsFlag){
			try {
				resultSet.close();
			}catch (SQLException e) {
				Console.showMessage(TableInfoBean.class.getSimpleName(), e.getMessage(), e);
			}finally{
				resultSet = null;//是否需要
			}
		}

		//2 关闭statement或preparedStatement
		if (stmtFlag){//关闭statement资源
			try {
				statement.close();
			}catch (SQLException e) {
				Console.showMessage(TableInfoBean.class.getSimpleName(), e.getMessage(), e);
			}finally{
				statement = null;//是否需要	
			}
		}

		//3 关闭connection
		closeDBCon();
	}

	private void closeDBCon(){
		try{
			if ( pvo != null)
				 pvo.closeCon();
		}finally{
			if(connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					Console.showMessage(TableInfoBean.class.getSimpleName(), e.getMessage(), e);
				}finally{
					connection = null;  //Make sure we don't close it twice
				}
			}
			pvo = null;
		}
	}

	/** 
     * 数据库类型,枚举 
     *  
     */  
    public enum DATABASETYPE {  
        ORACLE, MYSQL, SQLSERVER, SQLSERVER2005, DB2, INFORMIX, SYBASE, OTHER, EMPTY  
    }  
  //判断表存在与否,存在返回true
  	public boolean checkTableIfExist(String tableName){
  		boolean temp = false;

		try{
			if (initialDBCon()){
				DatabaseMetaData dbm = connection.getMetaData();
	  			if (dbm != null)
	  				resultSet = dbm.getTables(null, null, tableName, null);
	  			if (resultSet.next())
	  				temp = true;//Table exists
			}
		} catch (SQLException e) {
			Console.showMessage(TableInfoBean.class.getSimpleName(), e.getMessage(), e);
		}finally{
			closeResource(true,false);
		}
		return temp;
  	}

  	
	//删除表 主要用于CREATE TABLE 或DROP TABLE等不操作行的语句
	//对于 CREATE TABLE 或DROP TABLE等不操作行的语句，executeUpdate的返回值总为零。
	public boolean executeSchema(String sql){
		boolean temp = false;

		try{
			if (initialDBCon()){
				statement = connection.createStatement();
				if (statement == null)
					return temp;
				statement.executeUpdate(sql);
				temp = true;
			}
		} catch (SQLException e) {
			Console.showMessage(TableInfoBean.class.getSimpleName(), e.getMessage(), e);
		}finally{
			closeResource(false,true);
		}
		return temp;
	}

	//获取所有的数据库的名称
	public List<String> getDatabaseCatalogs(){
		List<String> list = new ArrayList<String>();
		try{
			if (initialDBCon()){
				statement = connection.createStatement();
				resultSet = statement.executeQuery("show databases");
				while(resultSet.next()) {
					String dbName = resultSet.getString(1);//列名称
					list.add(dbName);
				}
			}
		} catch (SQLException e) {
			Console.showMessage(TableInfoBean.class.getSimpleName(), e.getMessage(), e);
		}finally{
			closeResource(true,true);
		}
		return  list; 
	}
	//获取数据库类型的名称如 Mysql
	public String getDatabaseName(){
		String temp = "";
		try{
			if (initialDBCon()){
				DatabaseMetaData meta;
				meta = connection.getMetaData();
				temp = meta.getDatabaseProductName();
			}
		}catch (Exception e) {
			Console.showMessage(TableInfoBean.class.getSimpleName(), e.getMessage(), e);
		}finally{
			closeDBCon();
		}
		return temp;
	}

	//获取tableName表列信息
	public List<String> getColumnsInfo(String tableName){
		List<String> list = new ArrayList<String>();
		try{
			if (initialDBCon()){
				DatabaseMetaData meta;
				meta = connection.getMetaData();
				resultSet = meta.getColumns(null,"%", tableName,"%");//获取某表字段的名字和类型

				while(resultSet.next()) {
					//String columnComment = resultSet.getString("REMARKS");
					//String sqlType = resultSet.getString("DATA_TYPE");
					String columnName = resultSet.getString("COLUMN_NAME");//列名称
					list.add(columnName);
				}
			}
		}catch (Exception e) {
			Console.showMessage(TableInfoBean.class.getSimpleName(), e.getMessage(), e);
		}finally{
			closeResource(true,false);
		}
		return list;
	}

	//获取某一数据库中所有表的名称
	public List<String> getTables(String catalog){
		List<String> list = new ArrayList<String>();
		try{
			if (initialDBCon())
			{
				DatabaseMetaData meta = connection.getMetaData();
				//"%"表示所有, m_TableName是要获取的数据表的名字，如果想获取所有的表的名字，用"%"作为参数
				resultSet = meta.getTables(catalog,"%","%"/*m_TableName*/,new String[]{"TABLE"});
				//String tableComment = tableSet.getString("REMARKS");
				while(resultSet.next()) {
					String tableName = resultSet.getString("TABLE_NAME");//表名称
					list.add(tableName);
				}

			}
		}catch (SQLException e) {
			Console.showMessage(TableInfoBean.class.getSimpleName(), e.getMessage(), e);
		}finally{
			closeResource(true,false);
		}
		return list;		
	}

	//获取某表的主键
	public List<String> getPrimKey(String tableName){
		DatabaseMetaData meta;
		List<String> list = new ArrayList<String>();
		try{
			if (initialDBCon()){

				meta = connection.getMetaData();
				resultSet = meta.getPrimaryKeys(null, null,tableName);
				while (resultSet.next()) {
					list.add(resultSet.getString("COLUMN_NAME"));//列名称
				}
			}
		} catch (SQLException e) {
			Console.showMessage(TableInfoBean.class.getSimpleName(), e.getMessage(), e);
		}finally{
			closeResource(true,false);
		}
		return list;
	}

}
/*
"列名： resultSet.getString("COLUMN_NAME")
 数据类型是："+resultSet.getString("DATA_TYPE")
 类型名称是："+resultSet.getString("TYPE_NAME")
列大小是："+resultSet.getString("COLUMN_SIZE")
 注释是："+resultSet.getString("REMARKS")
 
 ResultSet rs=stmt.executeQuery(sql);
ResultSetMetaData data=rs.getMetaData();
while(rs.next()){
for(int i = 1 ; i<= data.getColumnCount() ; i++){
//获得所有列的数目及实际列数
int columnCount=data.getColumnCount();
//获得指定列的列名
String columnName = data.getColumnName(i);
//获得指定列的列值
String columnValue = rs.getString(i);
//获得指定列的数据类型
int columnType=data.getColumnType(i);
//获得指定列的数据类型名
String columnTypeName=data.getColumnTypeName(i);
//所在的Catalog名字
String catalogName=data.getCatalogName(i);
//对应数据类型的类
String columnClassName=data.getColumnClassName(i);
//在数据库中类型的最大字符个数
int columnDisplaySize=data.getColumnDisplaySize(i);
//默认的列的标题
String columnLabel=data.getColumnLabel(i);
//获得列的模式
String schemaName=data.getSchemaName(i);
//某列类型的精确度(类型的长度)
int precision= data.getPrecision(i);
//小数点后的位数
int scale=data.getScale(i);
//获取某列对应的表名
String tableName=data.getTableName(i);
// 是否自动递增
boolean isAutoInctement=data.isAutoIncrement(i);
//在数据库中是否为货币型
boolean isCurrency=data.isCurrency(i);
//是否为空
int isNullable=data.isNullable(i);
//是否为只读
boolean isReadOnly=data.isReadOnly(i);
//能否出现在where中
boolean isSearchable=data.isSearchable(i); 
*/