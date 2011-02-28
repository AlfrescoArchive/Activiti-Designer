/**
 * 
 */
package org.activiti.designer.integration.servicetask.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.validator.FieldValidator;

/**
 * @author Tiese Barrell
 * @version 2
 * @since 0.5.1
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

  /**
   * The {@link PropertyType} of this {@link Property}.
   */
  abstract PropertyType type();

  /**
   * The name that is shown to the user for this {@link Property}.
   */
  String displayName() default "";

  /**
   * The class that implements a {@link FieldValidator} for the value of the
   * {@link Property}'s field.
   */
  Class< ? extends FieldValidator> fieldValidator() default FieldValidator.class;

  /**
   * Whether this {@link Property} denotes a required field.
   */
  boolean required() default false;

  /**
   * The order for the {@link Property} to appear in a list of {@link Property}
   * objects. A lower order indicates a higher position in the sorted list, so a
   * {@link Property} with order 'x' is ordered before a {@link Property} with
   * order 'x + y' where y > 0.
   */
  int order() default 1;

}
