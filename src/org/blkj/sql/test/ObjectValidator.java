package org.blkj.sql.test;


/**
 * Define the function of an object validator.<br />
 * This will be used when an object is constructed with the available data to
 * check that the pbject is correct - for example 2 target classes may be
 * specified, both those classes will be constructed from the available data and
 * an implementation of this interface will determine which of the result
 * objects will be included in the return result.
 */
public interface ObjectValidator {

  /**
   * Returns whether the constructed object is valid.
   * 
   * @param object
   *          The Object to validate.
   * @return true = valid object; false = invalid object.
   */
  public boolean isValid(Object object);
}
