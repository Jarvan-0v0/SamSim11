package org.blkj.sql.test;





/**
 * Define the function of a bean field name to column name matching strategy.
 */
public abstract class NameMatcher implements Cloneable {

  /**
   * This field prefix and suffix will literally be appended to the fieldName
   * for comparison to the column name.
   * 
   * @param fieldName
   * @param columnName
   * @return Returns whether the two names are a match.
   */
  public abstract boolean isMatching(String fieldName, String columnName);

  /**
   * Set the prefix to use when matching a field name to column name. The prefix
   * will be attached to the column name before matching.
   * 
   * @param fieldPrefix
   *          Prefix.
   */
  public abstract void setFieldPrefix(String fieldPrefix);

  /**
   * Set the prefix to use when matching a field name to column name. The prefix
   * will be attached to the column name before matching.
   * 
   * @param fieldSuffix
   *          Suffix.
   */
  public abstract void setFieldSuffix(String fieldSuffix);

  /**
   * Clone this name converter.
   * 
   * @return The clone of the converter.
   */
  public NameConverter clone() {
    try {
      return (NameConverter) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
}
