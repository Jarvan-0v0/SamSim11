package org.blkj.sql.test;





/**
 * Utility to convert between case sensitive and case insensitive name
 * representations. <br />
 * Case sensitive standard = camelCase <br />
 * Case insensitive standard = under_score
 */
public class NameConverter extends NameMatcher {

  /**
   * Prefix.
   */
  private String prefix = "";

  /**
   * Suffix.
   */
  private String suffix = "";

  /**
   * Convert a string that may contain underscores to camel case.
   *
   * @param underScore
   *          Underscore name.
   * @return Camel case representation of the underscore string.
   */
  public static String convertToCamelCase(String underScore) {
    String result = "";
    boolean nextUpper = false;
    String allLower = underScore.toLowerCase();
    for (int i = 0; i < allLower.length(); i++) {
      Character currentChar = allLower.charAt(i);
      if (currentChar == '_') {
        nextUpper = true;
      } else {
        if (nextUpper) {
          currentChar = Character.toUpperCase(currentChar);
          nextUpper = false;
        }
        result = result.concat(currentChar.toString());
      }
    }
    return result;
  }

  /**
   * Convert a camel case string to underscore representation.
   * 
   * @param camelCase
   *          Camel case name.
   * @return Underscore representation of the camel case string.
   */
  public static String convertToUnderScore(String camelCase) {
    String result = "";
    for (int i = 0; i < camelCase.length(); i++) {
      Character currentChar = camelCase.charAt(i);
      // This is starting at 1 so the result does not end up with an
      // underscore at the begin of the value
      if (i > 0 && Character.isUpperCase(currentChar)) {
        result = result.concat("_");
      }
      result = result.concat(currentChar.toString().toLowerCase());
    }
    return result;
  }

  /**
   * @see NameMatcher#isMatching(java.lang.String,
   *      java.lang.String)
   */
  public boolean isMatching(String fieldName, String columnName) {
    if (columnName.equalsIgnoreCase(prefix.concat(
        NameConverter.convertToUnderScore(fieldName)).concat(suffix))) {
      return true;
    }
    return false;
  }

  /**
   * @see NameMatcher#setFieldPrefix(java.lang.String)
   */
  public void setFieldPrefix(String prefix) {
    if (prefix == null) {
      this.prefix = "";
    } else {
      this.prefix = prefix;
    }
  }

  /**
   * @see NameMatcher#setFieldSuffix(java.lang.String)
   */
  public void setFieldSuffix(String suffix) {
    if (suffix == null) {
      this.suffix = "";
    } else {
      this.suffix = suffix;
    }
  }

  /**
   * Capitalises the first character of a string.
   *
   * @param name
   *          The string to capitilised
   * @return The name with the first character capitalised.
   */
  public static String capitalise(String name) {
    if (name == null || name.length() == 0) {
      return name;
    }
    char[] chars = name.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  /**
   * Decapitalises the first character of a string.
   *
   * @param name
   *          The string to decapitilised.
   * @return The name with the first character converted to lower case.
   */
  public static String deCapitalise(String name) {
    if (name == null || name.length() == 0) {
      return name;
    }
    char[] chars = name.toCharArray();
    chars[0] = Character.toLowerCase(chars[0]);
    return new String(chars);
  }
}
