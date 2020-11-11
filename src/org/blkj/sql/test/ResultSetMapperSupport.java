package org.blkj.sql.test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This Mapper should correctly handle the following problems...<br />
 * Problem 1: Mapping to aggregate objects from one ResultSet - result is
 * Aggregate with all targeted objects filled<br />
 * Problem 2: Multiple objects in one row - result is a list containing the
 * objects<br />
 * Problem 3: Inheritance - ResultSet that maps to a parent or any child
 * objects.
 */
public abstract class ResultSetMapperSupport<T> implements ResultSetMapper<T> {

  private static NameMatcher defaultNameMatcher = new NameConverter();

  private static ObjectValidator defaultObjectValidator = new DefaultObjectValidator();

  private boolean annotationRequired = true;

  protected ObjectValidator objectValidator;

  protected NameMatcher nameMatcher;

  /**
   * Construct a <code>ResultSet</code> processor.
   * 
   */
  public ResultSetMapperSupport() {
    super();
    this.objectValidator = defaultObjectValidator;
    this.nameMatcher = defaultNameMatcher;
  }

  /**
   * @see ResultSetMapper#mapTable(java.sql.ResultSet)
   */
  public List<T> mapTable(ResultSet resultSet) throws SQLException {
    List<T> results = new ArrayList<T>();

    if (!resultSet.next()) {
      return results;
    }

    do {
      results.add(this.mapRow(resultSet));
    } while (resultSet.next());

    return results;
  }

  protected Object processColumn(ResultSet resultSet, int index, Class<?> type)
      throws SQLException {
    if (type.equals(Date.class)) {
      return resultSet.getDate(index);
    } else if (type.equals(BigDecimal.class)) {
      return resultSet.getBigDecimal(index);
    } else if (type.equals(byte[].class)) {
      return resultSet.getBytes(index);
    } else if (type.equals(String.class)) {
      return resultSet.getString(index);

    } else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
      return new Integer(resultSet.getInt(index));

    } else if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
      return new Boolean(resultSet.getBoolean(index));

    } else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
      return new Long(resultSet.getLong(index));

    } else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
      return new Double(resultSet.getDouble(index));

    } else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
      return new Float(resultSet.getFloat(index));

    } else if (type.equals(Short.TYPE) || type.equals(Short.class)) {
      return new Short(resultSet.getShort(index));

    } else if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
      return new Byte(resultSet.getByte(index));

    } else if (type.equals(Timestamp.class)) {
      return resultSet.getTimestamp(index);

    } else {
      return resultSet.getObject(index);
    }
  }

  /**
   * @param nameMatcher
   *          the nameMatcher to set
   */
  public final void setNameMatcher(NameMatcher nameMatcher) {
    this.nameMatcher = nameMatcher;
  }

  /**
   * Set the <code>ObjectValidator</code> - this will decide whether the
   * constructed object should be retained.
   * 
   * @param objectValidator
   *          The objectValidator to set
   */
  public final void setObjectValidator(ObjectValidator objectValidator) {
    this.objectValidator = objectValidator;
  }

  /**
   * @see ResultSetMapper#isAnnotationRequired()
   */
  public boolean isAnnotationRequired() {
    return this.annotationRequired;
  }

  /**
   * @see ResultSetMapper#setAnnotationRequired(boolean)
   */
  public void setAnnotationRequired(boolean required) {
    this.annotationRequired = required;
  }

  /**
   * Return whether the passed object is valid.
   * 
   * @param object Object to validate.
   * @return true = valid object; false = invalid object.
   */
  protected boolean isValid(Object object) {
    if (object instanceof ClassValidator) {
      return ((ClassValidator) object).isValid();
    }
    return objectValidator.isValid(object);
  }
}