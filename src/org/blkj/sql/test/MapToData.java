package org.blkj.sql.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation may be used on a bean field to specify to what data the
 * field should be mapped.
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MapToData {
  /**
   * Whatever other names the field that this annotation is attached to may be named.
   * @return Array of other names.
   */    
  String[] columnAliases() default { };
  /**
   * Prefix attached to fields within this field.
   * @return The prefix text.
   */
  String columnPrefix() default "";
  /**
   * Suffix attached to fields within this field.
   * @return The suffix text.
   */
  String columnSuffix() default "";
  /**
   * Indicates whether this field is an aggregate target.
   * @return true = aggregate target; false = not a target.
   */
  boolean isAggregateTarget() default false;
  /**
   * Specific name for the getter routine.
   * Only required if the field and getter are not to the JavaBean standard.
   * @return Name of the getter.
   */
  String getter() default "";
  /**
   * Specific name for the setter routine.
   * Only required if the field and setter are not to the JavaBean standard.
   * @return Name of the setter.
   */
  String setter() default "";
}
