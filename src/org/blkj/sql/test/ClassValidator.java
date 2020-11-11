package org.blkj.sql.test;

/**
 * Define the function of an class validator. This will be used when an object
 * is constructed with the available data to check that the object is valid - if
 * it is not valid it will not be returned.
 */
public interface ClassValidator {

  /**
   * Returns whether the implementing class is valid.
   * 
   * @return Indicator, true = class is valid; false = class is not valid.
   */
  public boolean isValid();
}
