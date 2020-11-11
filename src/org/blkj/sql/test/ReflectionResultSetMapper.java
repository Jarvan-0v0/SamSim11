package org.blkj.sql.test;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import org.apache.commons.beanutils.BeanUtils;

import blkjweb.utils.Console;

/**
 * Populate a set of target classes (probably only one target class in most cases)
 * with values in a ResultSet.<br />
 * Initially based on the BeanProcessor from the Apache DbUtils project.
 * 
 */
public class ReflectionResultSetMapper <T> extends ResultSetMapperSupport<T>
{
	 //ResultSet结果集中的记录映射到Java对象中.
	/**
	 * Stuff the values from the current item in the SQL ResultSet into a class object
	 * @param pfx prefix to strip from the SQL field names before picking the class member
	 * @param javaref java object to save values into
	 * @param sqlres sql result set containing data to copy
	 * @return -1 : query failed
	 * 0 : no fields to copy
	 * >0: number of copied fields 字段大小写灵敏
	 * 2011 关于字段名称大小写的问题:
	 * 对于Mysql,读出时,系统会自动会将大写的字段名转换为小写.
	 * 对于Oracle,写入时,会自动将小写的字段名转换为大写,读出时,仍保持大写
	 * 所以原来的if (sqlcol.startsWith(upfx))语句就会有问题
	 * @throws SQLException 
	 * 获取一条记录
	 * 
	 *  更名为_recordMappingToMap
	 */
   public int  _recordMappingToObj(ResultSet rs, Object objRef)
   {
		int cols;
		try {
			//获取表头 ResultSetMetaData rsm = rs.getMetaData();
			cols = rs.getMetaData().getColumnCount(); //取得ResultSet的列名的个数
			//Log.debug("Cols: " + cols);
		} catch (SQLException e) {
			Console.showMessage(ReflectionResultSetMapper.class.getSimpleName(),e.getMessage(), e);
			return -1;  // maybe we should let the exception pass through rather than catch it
		}
		int success = 0;
		//2015以下，待验证
		Map <String,String> map = new HashMap<String,String>();
		Field fieldlist[] = objRef.getClass().getDeclaredFields();
		//Field fieldlist[] = ReflectionHelp.getAllDeclaredFields(objRef.getClass());
		for (int i = 0; i < fieldlist.length; i++) {  
			String temp = fieldlist[i].getName();  
			map.put(temp,temp);
		}
		//2015以上
		//循环获取指定行的每一列的信息
		for (int i=1; i<=cols; i++)
		{
			String sqlcol = null;
			java.lang.reflect.Field javaField = null;
			try {
				//获取库表头的各字段的名称.类型getColumnType
				sqlcol = (rs.getMetaData().getColumnName(i)).toLowerCase();//2011oracle都转为小写字符
				//问题：类中只具备表的部分字段情况，程序运行异常 
				//获取 Java 对象中所定义的与数据库表中的字段名称javacol对象的属性
				//2015以下，待验证 map == null 不可能出现
				if ( !map.containsKey(sqlcol))
					continue;
				
				String tablecol;
				tablecol = sqlcol;
				//objRef实例对象 javacol为字段名
				//获取类的字段(域) 
				javaField = objRef.getClass().getDeclaredField(tablecol);//取得Class 
				javaField.setAccessible(true); //设置允许访问  

				// handle each sql type to java type converstion
				if (javaField.getType() == java.util.Date.class) {
					//rs.getDate获取字段值
					javaField.set(objRef, rs.getDate(sqlcol));
					success++;
				}
				else if (javaField.getType() == int.class) {//JAVA 对象中定义:int类型
					javaField.setInt(objRef, rs.getInt(sqlcol));
					success++;
				}
				else{
					// leave this split for easier debugging for unhandled types
					/**列为varchar()类型*/
					String t = rs.getString(sqlcol);
					javaField.set(objRef, t);
					success++;
				}
			}catch (Exception e) {
				Console.showMessage(ReflectionResultSetMapper.class.getSimpleName(),e.getMessage(), e);
				try {
					//wrong type  
					javaField.set(objRef,null);
					//ReflectionHelp.setField(javaField,objRef,null);
				} catch (Exception er) {
					Console.showMessage(ReflectionResultSetMapper.class.getSimpleName(),er.getMessage(), er);
				}
			}
		}//end for
		return success;
	}
	//////////////////////////////////////////2016
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//////////////////////////////////////////2016
  /**
   * Special array value used by <code>mapColumnsToFields</code> that
   * indicates there is no object field that matches a column from a
   * <code>ResultSet</code>.
   */
  protected static final int PROPERTY_NOT_FOUND = -1;

  /**
   * List of classes that will be created.
   */
  private List<Class<?>> targetClasses;

  /**
   * List of classes that the target class may contain that will also be mapped.
   */
  private List<Class<?>> aggregateClasses = new ArrayList<Class<?>>();

  private HashMap<Class<?>, ClassConfiguration> classConfigMap = new HashMap<Class<?>, ClassConfiguration>();

  /**
   * Construct a result set mapper.
   * 
   * @param targetClass The Target Class to construct.
   * 
   */
  public ReflectionResultSetMapper(Class<?> targetClass) {
    super();
    targetClasses = new ArrayList<Class<?>>();
    targetClasses.add(targetClass);
  }
  public ReflectionResultSetMapper() {}
  /**
   * Construct a result set mapper.
   * 
   * @param targetClasses List of Target Classes to construct.
   */
  public ReflectionResultSetMapper(List<Class <?>> targetClasses) {
    super();
    this.targetClasses = targetClasses;
  }

  /**
   * Set the aggregate classes that will be populated.  Provides a manual way
   * to set aggregrate target classes if the annotations were not used.
   * 
   * @param aggregateTargetClasses List of aggregate targets to include.
   */
  public void setAggregateTargets(List<Class<?>> aggregateTargetClasses) {
    this.aggregateClasses = aggregateTargetClasses;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.resultsetmapper.BeanMapper#toBeans(java.sql.ResultSet)
   * 一条记录
   */
  public T mapRow(ResultSet resultSet) throws SQLException {
    T object = null;
    Iterator<Class<?>> targetClassIterator = targetClasses.iterator();
    while (targetClassIterator.hasNext() && object == null) {
      Class<?> targetClass = targetClassIterator.next();
      object = this.toObject(resultSet, targetClass);
    }
    return object;
  }

  
  // 很多条记录
  public List<T> mapTable(ResultSet rs, Class<T> outputClass) throws Exception{
	  List<T> outputList = null;
	  // make sure resultset is not null
	  if (rs != null)	{
		  // check if outputClass has 'Entity' annotation
		/*  if (outputClass.isAnnotationPresent(Entity.class)) {
			  // get the resultset metadata
			  ResultSetMetaData rsmd = rs.getMetaData();
			  // get all the attributes of outputClass
			  Field[] fields = outputClass.getDeclaredFields();
			  while (rs.next()) {
				  T bean = (T) outputClass.newInstance();
				  for (int _iterator = 0; _iterator < rsmd.getColumnCount(); _iterator++) {
					  // getting the SQL column name
					  String columnName = rsmd.getColumnName(_iterator + 1);
					  // reading the value of the SQL column
					  Object columnValue = rs.getObject(_iterator + 1);
					  // iterating over outputClass attributes to check if any attribute has 'Column' annotation with matching 'name' value
					  for (Field field : fields) {
						  if (field.isAnnotationPresent(Column.class)) {
							  Column column = field.getAnnotation(Column.class);
							  if (column.name().equalsIgnoreCase(columnName) && columnValue != null) {
								  BeanUtils.setProperty(bean, field.getName(), columnValue);
								  break;
							  }
						  }
					  }
				  }
				  if (outputList == null) {
					  outputList = new ArrayList<T>();
				  }
				  outputList.add(bean);
			  }

		  } else {
			  // throw some error
		  }
	  } 
	  else//RS =null
	  {
		  return null;*/
	  }
	  return outputList;
  }

  /**
   * Get configuration for the specified class.
   * 
   * @param type Class to lookup.
   * @param resultSetMetaData Metadata for the Result set.
   * @return The class configuration.
   * @throws SQLException
   */
  protected ClassConfiguration getConfig(Class <?> type,
      ResultSetMetaData resultSetMetaData) throws SQLException {
    ClassConfiguration classConfiguration;
    if (classConfigMap.containsKey(type)) {
      classConfiguration = classConfigMap.get(type);
    } else {
      Field[] fields = ReflectionHelp.getAllDeclaredFields(type);
      AccessibleObject.setAccessible(fields, true);

      int[] columnToField = this.mapColumnsToFields(resultSetMetaData, fields);
      classConfiguration = new ClassConfiguration(fields, columnToField);
      classConfigMap.put(type, classConfiguration);
    }
    return classConfiguration;
  }

  private T toObject(ResultSet rs, Class<?> type) throws SQLException {

    T object = null;

    // Obtain the configuration for the the class
    ClassConfiguration config = getConfig(type, rs.getMetaData());

    object = this.createObject(rs, type, config.getFields(), 
    		                   config.getColumnToField());

    if (this.isValid(object)) {
      mapAggregateTargets(rs, object);
      return object;
    }
    return null;
  }

  /**
   * Recursive routine to map any beans contained in the given Aggregate.
   * 
   * @param resultSet
   * @param object
   * @throws SQLException
   */
  private void mapAggregateTargets(ResultSet resultSet, Object object)
      throws SQLException {
    // Obtain any fields for the new bean object.
    Iterator<Field> fieldIterator = ReflectionHelp.getFields(object).iterator();
    while (fieldIterator.hasNext()) {
      Field field = fieldIterator.next();
      // Check the aggregte field for a mapping annotation.
      MapToData mapToData = field.getAnnotation(MapToData.class);

      Class<?> aggregateClass = field.getType();
      // If the bean contains the targeted aggregate class, see if it
      // can be constructed from the result set.
      if ((mapToData != null && mapToData.isAggregateTarget())
          || aggregateClasses.contains(aggregateClass)) {

        ResultSetMapper<?> resultSetMapper = new ReflectionResultSetMapper<Object>(aggregateClass);
        resultSetMapper.setNameMatcher(this.nameMatcher);
        resultSetMapper.setObjectValidator(this.objectValidator);
        resultSetMapper.setAnnotationRequired(this.isAnnotationRequired());

        // Check the aggregte field for a column prefix/suffix annotation.
        if (mapToData != null
            && (mapToData.columnPrefix().length() > 0 || mapToData
                .columnSuffix().length() > 0)) {
          NameMatcher newNameMatcher = this.nameMatcher.clone();
          newNameMatcher.setFieldPrefix(mapToData.columnPrefix());
          newNameMatcher.setFieldSuffix(mapToData.columnSuffix());
          resultSetMapper.setNameMatcher(newNameMatcher);
        }

        Object aggregateObject = resultSetMapper.mapRow(resultSet);
        if (aggregateObject != null) {
          // Recursion into the bean to setup all 'children'.
          ((ReflectionResultSetMapper<?>) resultSetMapper).mapAggregateTargets(
              resultSet, aggregateObject);
          setValue(field, object, aggregateObject);
        }
      }
    }
  }

  /**
   * Creates a new object and initialises its fields from the ResultSet.
   * 
   * @param resultSet
   * @param type
   * @param fields
   * @param columnToField
   * @return
   * @throws SQLException
   */
  private T createObject(ResultSet resultSet, Class<?> type, Field[] fields,
                         int[] columnToField) throws SQLException {
    @SuppressWarnings("unchecked")
    T object = (T) ReflectionHelp.newInstance(type);
  
    for (int i = 1; i < columnToField.length; i++) {
      if (columnToField[i] == PROPERTY_NOT_FOUND) {
        continue;
      }
      Field field = fields[columnToField[i]];
      Class<?> fieldType = field.getType();
      Object value = this.processColumn(resultSet, i, fieldType);
      setValue(field, object, value);
    }
    return object;
  }

  
  /**
   * Set the value for a field. This will be done only for annotated fields if
   * AnnotationRequired is set. There are a few ways that the value may be set,
   * the order is:
   * <ol>
   * <li>The <code>MapToData</code> has specified a <code>setter</code> -
   * use it.</li>
   * <li>The Field has an associated setter according to the JavaBean standard -
   * use -it</li>
   * <li>Inject the value straight into the field</li>
   * </ol>
   * 
   * @param field
   * @param object
   * @param value
   * @throws SQLException
   */
  private void setValue(Field field, Object object, Object value)
      throws SQLException {
    MapToData mapToData = field.getAnnotation(MapToData.class);
    if (value != null && 
        ((this.isAnnotationRequired() && mapToData != null)
        || !this.isAnnotationRequired())) {
      boolean valueSet = false;
      try {
        Method setter;
        if (mapToData != null && mapToData.setter().length() > 0) {
          // Use the setter prescribed by the annotation.
          setter = object.getClass().getMethod(mapToData.setter(),
              value.getClass());
        } else {
          try {
            setter = object.getClass().getMethod(
                "set" + field.getName().substring(0, 1).toUpperCase()
                    + field.getName().substring(1), value.getClass());
          } catch (NoSuchMethodException e) {
            // This can be expected if the Field does not conform to
            // the JavaBean standard.
            setter = null;
          }
        }
        if (setter != null) {
          setter.invoke(object, new Object[] { value });
          valueSet = true;
          
        }
      } catch (SecurityException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
      if (!valueSet) {
        // Set the field directly
        ReflectionHelp.setField(field, object, value);
      }
    }
  }

  /**
   * 
   * @param rsmd
   * @param fields
   * @return
   * @throws SQLException
   */
  private int[] mapColumnsToFields(ResultSetMetaData rsmd, Field[] fields)
      throws SQLException {
    int cols = rsmd.getColumnCount();
    int columnToField[] = new int[cols + 1];
    Arrays.fill(columnToField, PROPERTY_NOT_FOUND);

    for (int col = 1; col <= cols; col++) {
      String columnName = rsmd.getColumnName(col);
      for (int i = 0; i < fields.length; i++) {
        
        MapToData mapAnnotation = fields[i].getAnnotation(MapToData.class);
        // Check the column aliases first.
        List<String> aliases;
        if (mapAnnotation != null) {
          aliases = Arrays.asList(mapAnnotation.columnAliases());
        } else {
          aliases = new ArrayList<String>();
        }
        if (aliases.contains(columnName.toLowerCase())) {
          columnToField[col] = i;
          // Check the with the name matching algorithm.
        } else if (nameMatcher.isMatching(fields[i].getName(), columnName)) {
          columnToField[col] = i;
        }
      }
    }

    return columnToField;
  }

  /**
   * Storage for class configuration information - to allow caching.
   * 
   */
  protected class ClassConfiguration {
    private Field[] fields;

    int[] columnToField;

    ClassConfiguration(Field[] fields, int[] columnToField) {
      this.fields = fields;
      this.columnToField = columnToField;
    }

    /**
     * @return the columnToField
     */
    public int[] getColumnToField() {
      return columnToField;
    }

    /**
     * @return the fields
     */
    public Field[] getFields() {
      return fields;
    }
  }
  
 
}