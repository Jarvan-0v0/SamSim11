/*
 * DbCenter、Table、Field三个类分别用于数据库连接和描述、数据库表描述及表的字段描述
 */
package org.blkj.sql.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.blkj.sql.core.base.DbCenter;
import org.blkj.sql.core.connection.DbConnectionType;
import org.blkj.sql.core.table.Field;
import org.blkj.sql.core.table.Table;
import org.blkj.utils.StringTool;

import blkjweb.utils.Console;

/**负责提取、管理事务型数据库的结构信息。采用有限多列模式实现 */

public class DbCenterImp implements DbCenter 
{
	private static final long serialVersionUID = 1L;
	
	private static DbCenterImp userDb = null;//通过指定的数据库连接创建的Db实例
	private static DbCenterImp defaultDb = null;//通过db.xml配置的默认连接创建的Db实例
	private static DbCenterImp db_01 = null;//通过指定的数据库连接，并指定db_01创建的Db实例
	private static DbCenterImp db_02 = null;//通过指定的数据库连接，并指定db_02创建的Db实例
	private static DbCenterImp jpaDb = null;//通过JPA配置文件配置的数据库连接创建的Db实例，暂缓
	private static DbCenterImp mybatisDb = null;//通过iBatis配置文件配置的数据库连接创建的Db实例，暂缓
	private static DbCenterImp hibernateDb = null;//通过hibernate配置文件配置的数据库连接创建的Db实例，暂缓

	private String catalog = null;//数据库名
	private String driverName = null;//数据库驱动程序名
	private DatabaseMetaData dm = null;
    private String[] tableNames_tmp = null;//表名集合(未转换为小写名前)
    private String[] tableNames = null;//转换成小写后的表名集合
    private Map<String, Table> tableMap = new LinkedHashMap<String, Table>();//表映射表; 表经转换成小写了

    private String schema = null;//框架名
    private Connection con = null;//数据库连接
    private static int instance_times = 0;

    private String[] lastQuery = new String[20];//查询语句队列
       
	synchronized public static DbCenterImp instance(Connection con, int connectionType)
	{
		switch (connectionType)
		{
		case DbConnectionType.USING_CONFIG_OF_NONE: {//目前只使用此分支
			if (userDb == null) {
				userDb = new DbCenterImp(con);
			}
			return userDb;
		}
	
		case DbConnectionType.USING_CONFIG_OF_DEFAULT: {
			if (defaultDb == null) {
				defaultDb = new DbCenterImp(con);
			}
			return defaultDb;
		}
		case DbConnectionType.USING_CONFIG_OF_JPA: {
			if (jpaDb == null) {
				jpaDb = new DbCenterImp(con);
			}
			return jpaDb;
		}
		case DbConnectionType.USING_CONFIG_OF_HIBERNATE: {
			if (hibernateDb == null) {
				hibernateDb = new DbCenterImp(con);
			}
			return hibernateDb;
		}
		case DbConnectionType.USING_CONFIG_OF_MYBATIS: {
			if (mybatisDb == null) {
				mybatisDb = new DbCenterImp(con);
			}
			return mybatisDb;
		}
		case DbConnectionType.USING_DB_01: {
			if (db_01 == null) {
				db_01 = new DbCenterImp(con);
			}
			return db_01;
		}
		case DbConnectionType.USING_DB_02: {
			if (db_02 == null) {
				db_02 = new DbCenterImp(con);
			}
			return db_02;
		}
		default: {
			break;
		}
		}
		return null;
	}
	
	/**
     * 通过指定的数据库连接构造数据库的实例
	 * @throws SQLException 
     */
    private DbCenterImp(Connection con)  {
        init(con);
    }
    synchronized private void init(Connection _con) 
    {
        this.con = _con;
        ResultSet rs = null;
        try {
            //schema=con.getSchema();//获取Meta信息对象
            catalog = con.getCatalog();//获取当前连接的数据库名 
            dm = con.getMetaData();//获取数据库的元数据 
            driverName = dm.getDriverName();//数据库驱动名
            Set<String> tableNameSet = new java.util.LinkedHashSet<String>();//表名集合
            String[] types = {"TABLE"};

           //获取数据库中所有表的名称
            rs = dm.getTables(null, null, null, types);
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tableNameSet.add(tableName);
            }
            Object[] o = tableNameSet.toArray();
            tableNames_tmp = new String[o.length];
            tableNames = new String[o.length];
            for (int i = 0; i < o.length; i++) {
                tableNames_tmp[i] = (String) o[i].toString();//原始值
                tableNames[i] = (String) o[i].toString().toLowerCase();//转换为小写的值
            }
            if (tableNames_tmp != null) {
                for (int i = 0; i < tableNames_tmp.length; i++) {
                    initTableMap(tableNames_tmp[i]);//统一转换为大写
                }
            }

            instance_times++;

        } catch (SQLException e) {
        	Console.showMessage(DbCenterImp.class.getSimpleName(), e.getMessage(), e);
        } finally {
        	try {//xbm
				rs.close();//局部变量，为何不能关？
				//con.close();//原来:共享con，不能关闭，由调用 Db实例.getCon()的用户关闭
			} catch (SQLException e) {
				rs = null;
				Console.showMessage(DbCenterImp.class.getSimpleName(), e.getMessage(), e);
			}finally{
				con = null;
			}
        }
    }
	
    /**
     * 初始化字段Field及各表的键值信息。
     * 注意事项： 使用ResultSet rs = dm.getColumns(catalog,null,tableName, null);
     * 使用derby数据库时，大小写是敏感的， 因此 @param tableName的值，只能通过dm原型获取的值，
     * 这样造成了大小写问题，在运算过程中，不能转换大小写，只能在最后统一转换成小写，
     */
    
    synchronized private void initTableMap(String tableName) throws SQLException 
    {
        Table table = getTable(tableName.toLowerCase());
        if (table == null) {
            table = new Table();
        }
        table.setName(tableName.toLowerCase());
        Set<String> fieldSet = new java.util.LinkedHashSet<String>();
        Set<String> keySet = new java.util.LinkedHashSet<String>();
        Statement stmt0 = null;
        ResultSet rs = null;
        ResultSet rscname = null;
        ResultSet rsk = null;
        try {
        if (con != null)  {
            rs = dm.getColumns(catalog, null, tableName, null);//获取表中所有字段getColumns 
            
            //显示所有列,即表格的表头
            /*ResultSetMetaData rmd = rs.getMetaData() ;
            int columnCount = rmd.getColumnCount();
            for(int i=1;i<=columnCount;i++) //
               
            */
            
            Map<String, Field> field_map = new LinkedHashMap<String, Field>();
            
            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");//参数值可参考dm.getColumns(catalog, null, tableName, null)的帮助文档
                fieldSet.add(lowerCase(name));
                Field f = new Field();
                f.setName(lowerCase(name));

                String dataType = rs.getString("DATA_TYPE");
                f.setSqlType(new Integer(dataType).intValue());//如：java.sql.Types.INTEGER

                String type = rs.getString("TYPE_NAME");//如:BIGINT
                f.setTypeName(lowerCase(type));
                String position = rs.getString("ORDINAL_POSITION");//在表中的位置
                f.setPosition(position);


                String size = rs.getString("COLUMN_SIZE");//用户定义的字段长度
                f.setSize(size);

                String bufferLength = rs.getString("BUFFER_LENGTH");//字段缓冲区大小
                f.setBufferLength(bufferLength);


                String decimal = rs.getString("DECIMAL_DIGITS");//精度
                f.setDecimal(decimal);
                
                String defaultValue = rs.getString("COLUMN_DEF");
                f.setDefaultValue(defaultValue);
                
                String remark = rs.getString("REMARKS");
                f.setRemark(remark);
                
                String nullable = rs.getString("NULLABLE");//取值0||1,1允许空值,0不允许空值
                if ("0".equals(nullable)) {
                    f.setNullable(false);
                }
                if ("1".equals(nullable)) {
                    f.setNullable(true);
                }
                field_map.put(name.toLowerCase(), f);
            }

            table.setFieldMap(field_map);//字段名:Field对象的映射表;

            //获取字段名数组
            Object[] o = fieldSet.toArray();
            String[] fields = new String[o.length];
            for (int i = 0; i < o.length; i++) {
                fields[i] = ((String) o[i]).toLowerCase();

            }
            table.setFields(fields);
          
            //主键部分，开始
            //ResultSet 
            rsk = dm.getPrimaryKeys(catalog, null, tableName); 
            while (rsk.next()) {
                String name = rsk.getString("COLUMN_NAME");//主键名
                keySet.add(lowerCase(name.toLowerCase()));//
            }
            Object[] k = keySet.toArray();
            String[] keys = new String[k.length];
            for (int i = 0; i < k.length; i++) {
                keys[i] = (String) k[i];
               field_map.get(keys[i]).setPrimarykey(true);//通过mssql、mysql、derby
            }
            table.setKeys(keys);
            ///给Field属性typeClassName赋值
            String squeryFieldTypeClassName = "SELECT * FROM " + tableName.toLowerCase() + " WHERE " + table.getFields()[0] + " IS NULL";
            if (table.getKeys().length > 0) {
                squeryFieldTypeClassName = "SELECT * FROM " + tableName.toLowerCase() + " WHERE " + table.getKeys()[0] + " IS NULL";
            }
            //Statement stmt0 = con.createStatement();
            stmt0 = con.createStatement();
            //ResultSet
            rscname = stmt0.executeQuery(squeryFieldTypeClassName);
            ResultSetMetaData rsmd = rscname.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String fieldNmae = rsmd.getColumnName(i);
               
                field_map.get(fieldNmae.toLowerCase()).setTypeClassName(rsmd.getColumnClassName(i));//通过mssql、mysql、derby
            }
            
            //stmt0.close();
        }
        } finally {//XBM
        	if (rs != null)
        		rs.close();
        	if (rsk != null)
        		rsk.close();
        	if (rscname != null)
        		rscname.close();
        	if (stmt0 != null)
        		stmt0.close();
        }
        
        tableMap.put(tableName.toLowerCase(), table);//初始化Table
    }
    
	/*| TABLE_CATALOG | TABLE_SCHEMA | TABLE_NAME | COLUMN_NAME | ORDINAL_POSITION | C
	OLUMN_DEFAULT | IS_NULLABLE | DATA_TYPE | CHARACTER_MAXIMUM_LENGTH | CHARACTER_O
	CTET_LENGTH | NUMERIC_PRECISION | NUMERIC_SCALE | DATETIME_PRECISION | CHARACTER
	_SET_NAME | COLLATION_NAME  | COLUMN_TYPE | COLUMN_KEY | EXTRA | PRIVILEGES
	                 | COLUMN_COMMENT | GENERATION_EXPRESSION |
	mysql> SELECT COLUMN_NAME,column_comment FROM INFORMATION_SCHEMA.Columns where T
	ABLE_SCHEMA='redhat' and TABLE_NAME='dbback';
	*/
	
    @Override
    public Table getTable(String tableName) {
        Table t = tableMap.get(tableName);
        return t;
    }
    
    @Override
    public String[] getFields(String tableName) {
        Table table = tableMap.get(tableName);
        if (table == null)
        	return null;
        return table.getFields();
    }
   
    @Override
    public String getDriverName() {
        return driverName;
    }

    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public String[] getTableNames() {
        return tableNames;
    }

    @Override
    public Map<String, Table> getTableMap() {
        return tableMap;
    }


    @Override
    public String[] getKeys(String tableName) {
        Table table = tableMap.get(tableName);
        if(table == null)//XBM
        	return null;
        return table.getKeys();
    }

     /**
     * 主键类型
     */
    @Override
    public String[] getKeysType(String tableName) {
        Table table = tableMap.get(tableName);
        String[] keys = table.getKeys();
        String[] type = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            Field f = table.getFieldMap().get(keys[i]);
            type[i] = f.getTypeName();
        }
        return type;
    }

    @Override
    public Map<String, Field> getFieldMap(String tableName, String fieldName) {
        Table table = tableMap.get(tableName);
        return table.getFieldMap();
    }

    @Override
    public Field getField(String tableName, String fieldName) {
        Table table = tableMap.get(tableName);
        Field f = table.getFieldMap().get(fieldName);
        return f;
    }

    @Override
    public String getFieldTypeName(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.getTypeName();
    }

    @Override
    public int getFieldSqlType(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.getSqlType();
    }

    @Override
    public String getFieldTypeClassName(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.getTypeClassName();
    }

    @Override
    public String getFieldPosition(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.getPosition();
    }

    @Override
    public String getFieldSize(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.getSize();
    }

    public String getFieldBufferLength(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.getBufferLength();
    }

    @Override
    public String getFieldDecimal(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.getDecimal();
    }

    @Override
    public String getFieldRegex(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.getRegex();
    }

    @Override
    public String getFieldDefaultValue(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.getDefaultValue();
    }

    @Override
    public String getFieldRemark(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.getRemark();
    }

    @Override
    public boolean isFieldNullable(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.isNullable();
    }

    @Override
    public boolean isFieldPrimarykey(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.isPrimarykey();
    }

     @Override
    public boolean isExistTable(String tableName) {
        boolean b = StringTool.isInFields(tableNames, tableName);
        return b;
    }

    @Override
    public boolean isExistField(String tableName, String fieldName) {
        boolean b = StringTool.isInFields(getFields(tableName), fieldName);
        return b;
    }

    @Override
    public boolean isExistKeyField(String tableName, String fieldName) {
        boolean b = StringTool.isInFields(getKeys(tableName), fieldName);
        return b;
    }

    /**
     * 提取指定查询语句
     *
     * @param last 最近第last条
     */
    public String getLastQuerySql(int last) {
        if (last > (lastQuery.length - 1)) {
            return null;
        }
        return lastQuery[lastQuery.length - 1 - last];
    }

    /**
     * 将最近一次查询语句保存到已查询队列尾部
     *
     * @param lastQuerySql 最近一次查询语句
     */
    public void setLastQuerySql(String lastQuerySql) 
    {
        for (int i = 0; i < this.lastQuery.length - 1; i++) {
            lastQuery[i] = lastQuery[i + 1];
        }
        lastQuery[lastQuery.length - 1] = lastQuerySql;
    }

    @Override
    public Connection getCon() {
        return con;
    }

    private String lowerCase(String s) {
        if (s != null) {
            s = s.toLowerCase();
        }
        return s;
    }
    
    public static int getInstance_times() {
		return instance_times;
	}
    @Override
    public String getSchema() {
        return schema;
    }

    ///////////////////////////////////
    // private int initNum = 0;
    /**
     * @deprecated 未获初始化值，暂不公开
    private boolean isFieldForeignkey(String tableName, String fieldName) {
        Field f = getField(tableName, fieldName);
        return f.isForeignkey();
    }
    private String lastQuerySql = null;//上次使用的查询语句
    private int tsLevel = -1;//事务支持级别,可以在db.xml中预定义
    private DateTool dt = new DateTool();
    */


}
/*
public static final int USING_CONFIG_OF_JPA = 0;//使用JPA配置文件，获取连接
public static final int USING_CONFIG_OF_HIBERNATE = 1;//使用HIBERNATE配置文件，获取连接
public static final int USING_CONFIG_OF_MYBATIS = 2;//使用MYBATIS配置文件，获取连接
public static final int USING_DB_01 = 101;//使用db_01，获取连接
public static final int USING_DB_02 = 102;//使用db_02，获取连接


*/
//DATA_TYPE int => SQL type from java.sql.Types

/*
 Each column description has the following columns:
 TABLE_CAT String => table catalog (may be null)
 TABLE_SCHEM String => table schema (may be null)
 TABLE_NAME String => table name
 COLUMN_NAME String => column name
 DATA_TYPE int => SQL type from java.sql.Types
 TYPE_NAME String => Data source dependent type name, for a UDT the type name is fully qualified
 COLUMN_SIZE int => column size.
 BUFFER_LENGTH is not used.
 DECIMAL_DIGITS int => the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is not applicable.
 NUM_PREC_RADIX int => Radix (typically either 10 or 2)
 NULLABLE int => is NULL allowed.
 columnNoNulls - might not allow NULL values
 columnNullable - definitely allows NULL values
 columnNullableUnknown - nullability unknown
 REMARKS String => comment describing column (may be null)
 COLUMN_DEF String => default value for the column, which should be interpreted as a string when the value is enclosed in single quotes (may be null)
 SQL_DATA_TYPE int => unused
 SQL_DATETIME_SUB int => unused
 CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the column
 ORDINAL_POSITION int => index of column in table (starting at 1)
 IS_NULLABLE String => ISO rules are used to determine the nullability for a column.
 YES --- if the column can include NULLs
 NO --- if the column cannot include NULLs
 empty string --- if the nullability for the column is unknown
 SCOPE_CATALOG String => catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF)
 SCOPE_SCHEMA String => schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF)
 SCOPE_TABLE String => table name that this the scope of a reference attribute (null if the DATA_TYPE isn't REF)
 SOURCE_DATA_TYPE short => source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)
 IS_AUTOINCREMENT String => Indicates whether this column is auto incremented
 YES --- if the column is auto incremented
 NO --- if the column is not auto incremented
 empty string --- if it cannot be determined whether the column is auto incremented
 IS_GENERATEDCOLUMN String => Indicates whether this is a generated column
 YES --- if this a generated column
 NO --- if this not a generated column
 empty string --- if it cannot be determined whether this is a generated column
 */