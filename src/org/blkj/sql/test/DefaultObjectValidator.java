package org.blkj.sql.test;




/**
 * Basic implementation of a ObjectValidator that always returns isValid = true.
 */
public class DefaultObjectValidator implements ObjectValidator {

  /**
   * This validator will always return true - is valid!
   * @see ObjectValidator#isValid(java.lang.Object)
   */
  public final boolean isValid(Object object) {
    return true;
  }

}
