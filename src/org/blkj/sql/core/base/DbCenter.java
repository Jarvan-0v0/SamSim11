package org.blkj.sql.core.base;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;

import org.blkj.sql.core.table.Field;
import org.blkj.sql.core.table.Table;

/*
*  负责统一定义提取和管理数据库结构信息的方法
*  @author XBM
 */
public interface DbCenter extends Serializable
{
    Connection getCon();
    String getDriverName();
    String getSchema();
    String getCatalog();
    Field getField(String tableName, String fieldName);
    String getFieldDecimal(String tableName, String fieldName);
    String getFieldDefaultValue(String tableName, String fieldName);
    String getFieldPosition(String tableName, String fieldName);
    Map<String, Field> getFieldMap(String tableName, String fieldName);
    String getFieldRegex(String tableName, String fieldName);
    String getFieldRemark(String tableName, String fieldName);
    String getFieldSize(String tableName, String fieldName);
    String getFieldBufferLength(String tableName, String fieldName);
    int getFieldSqlType(String tableName, String fieldName);
    String getFieldTypeName(String tableName, String fieldName);
    String getFieldTypeClassName(String tableName, String fieldName);
    String[] getFields(String tableName);
    String[] getKeys(String tableName);
    String[] getKeysType(String tableName);
    Table getTable(String tableName);
    Map<String, Table> getTableMap();
    String[] getTableNames();
    boolean isExistField(String tableName, String fieldName);
    boolean isExistKeyField(String tableName, String fieldName);
    boolean isExistTable(String tableName);
    boolean isFieldNullable(String tableName, String fieldName);
    boolean isFieldPrimarykey(String tableName, String fieldName);
    String getLastQuerySql(int last);
    void setLastQuerySql(String lastQuerySql) ;
    
}
